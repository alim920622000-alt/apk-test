package com.android.teztaomapp.network

// ЗАМЕНИ тип ExistingTokenStorage на твой реальный класс (который уже есть в проекте)
class TokenStorageAdapter(
    private val existing: ExistingTokenStorage
) : TokenStorage {

    override fun accessToken(): String? = existing.getAccessToken() // <- подставь реальное имя
    override fun refreshToken(): String? = existing.getRefreshToken() // <- подставь реальное имя

    override fun saveTokens(access: String, refresh: String, expiresAtMillis: Long) {
        existing.saveTokens(access, refresh, expiresAtMillis) // <- подставь реальное имя
    }

    override fun saveAccessToken(access: String, expiresAtMillis: Long) {
        existing.saveAccess(access, expiresAtMillis) // <- подставь реальное имя
    }

    override fun clear() {
        existing.clearTokens() // <- подставь реальное имя
    }
}
