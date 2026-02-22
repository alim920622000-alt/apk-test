from __future__ import annotations

import time

from fastapi import APIRouter, Depends, HTTPException, status

from app.api.deps import CurrentUser, get_current_user, get_db
from app.api.schemas import (
    LoginResponse,
    LogoutRequest,
    LogoutResponse,
    RefreshTokenRequest,
    RefreshTokenResponse,
    TelegramAuthRequest,
)
from app.api.security import (
    create_access_token,
    generate_refresh_token,
    get_access_expires_in,
    get_refresh_expires_at,
)
from app.db.database import Database
from app.handlers_admin_restaurant.utils import get_admin_restaurant_ids
from app.handlers_admin_shop.utils import get_admin_shop_ids

router = APIRouter(prefix="/auth", tags=["auth"])


async def _ensure_refresh_tokens_table(db: Database) -> None:
    async with db.conn() as conn:
        await conn.execute(
            """
            CREATE TABLE IF NOT EXISTS refresh_tokens (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                token TEXT NOT NULL UNIQUE,
                expires_at INTEGER NOT NULL,
                revoked INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL DEFAULT (strftime('%s','now'))
            )
            """
        )
        await conn.execute("CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id)")
        await conn.commit()


async def _resolve_role(db: Database, user_id: int) -> str:
    shop_ids = await get_admin_shop_ids(db, user_id)
    if shop_ids:
        return "admin_shop"
    restaurant_ids = await get_admin_restaurant_ids(db, user_id)
    if restaurant_ids:
        return "admin_restaurant"
    return "client"


async def _issue_tokens(db: Database, user_id: int) -> LoginResponse:
    await _ensure_refresh_tokens_table(db)
    role = await _resolve_role(db, user_id)
    db_role = "admin" if role in {"admin_shop", "admin_restaurant"} else "client"

    async with db.conn() as conn:
        await conn.execute(
            """
            INSERT INTO users(user_id, role)
            VALUES (?, ?)
            ON CONFLICT(user_id) DO UPDATE SET role=excluded.role
            """,
            (user_id, db_role),
        )

        refresh_token = generate_refresh_token()
        refresh_expires_at = get_refresh_expires_at()
        await conn.execute(
            """
            INSERT INTO refresh_tokens(user_id, token, expires_at, revoked)
            VALUES (?, ?, ?, 0)
            """,
            (user_id, refresh_token, refresh_expires_at),
        )
        await conn.commit()

    access_token = create_access_token({"sub": user_id, "role": role})
    return LoginResponse(
        access_token=access_token,
        refresh_token=refresh_token,
        expires_in=get_access_expires_in(),
    )


@router.post("/login", response_model=LoginResponse)
@router.post("/telegram", response_model=LoginResponse)
async def auth_by_telegram(payload: TelegramAuthRequest, db: Database = Depends(get_db)) -> LoginResponse:
    return await _issue_tokens(db, payload.telegram_user_id)


@router.post("/refresh", response_model=RefreshTokenResponse)
async def refresh_token(payload: RefreshTokenRequest, db: Database = Depends(get_db)) -> RefreshTokenResponse:
    await _ensure_refresh_tokens_table(db)

    async with db.conn() as conn:
        cursor = await conn.execute(
            """
            SELECT user_id, expires_at, revoked
            FROM refresh_tokens
            WHERE token = ?
            LIMIT 1
            """,
            (payload.refresh_token,),
        )
        row = await cursor.fetchone()

        if not row:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Refresh токен не найден")

        user_id = int(row[0])
        expires_at = int(row[1])
        revoked = bool(row[2])

        if revoked:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Refresh токен отозван")

        if expires_at < int(time.time()):
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Срок действия refresh токена истек")

    role = await _resolve_role(db, user_id)
    access_token = create_access_token({"sub": user_id, "role": role})
    return RefreshTokenResponse(access_token=access_token, expires_in=get_access_expires_in())


@router.post("/logout", response_model=LogoutResponse)
async def logout(
    payload: LogoutRequest,
    current_user: CurrentUser = Depends(get_current_user),
    db: Database = Depends(get_db),
) -> LogoutResponse:
    await _ensure_refresh_tokens_table(db)

    async with db.conn() as conn:
        await conn.execute(
            """
            UPDATE refresh_tokens
            SET revoked = 1
            WHERE token = ? AND user_id = ?
            """,
            (payload.refresh_token, current_user.user_id),
        )
        await conn.commit()

    return LogoutResponse(detail="Выход выполнен")
