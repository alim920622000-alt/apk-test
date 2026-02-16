# API документация

## Авторизация

### POST /auth/login

Вход по Telegram ID (совместимо с `/auth/telegram`):

```json
{
  "telegram_user_id": 123456789
}
```

Ответ:

```json
{
  "access_token": "<jwt>",
  "refresh_token": "<refresh_token>",
  "expires_in": 86400,
  "token_type": "bearer"
}
```

- `access_token` — JWT для доступа к защищённым эндпоинтам.
- `refresh_token` — токен обновления с хранением в БД.
- `expires_in` — время жизни access token в секундах.

### POST /auth/refresh

Обновление access token по refresh token:

```json
{
  "refresh_token": "<refresh_token>"
}
```

Успешный ответ:

```json
{
  "access_token": "<jwt>",
  "expires_in": 86400,
  "token_type": "bearer"
}
```

Ошибки (`401 Unauthorized`):
- refresh токен не найден;
- refresh токен отозван (`revoked=true`);
- refresh токен истёк.

### POST /auth/logout

Отзыв refresh token:

```json
{
  "refresh_token": "<refresh_token>"
}
```

Ответ:

```json
{
  "detail": "Выход выполнен"
}
```
