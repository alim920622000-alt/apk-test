package com.android.teztaomapp.network

class InMemoryTokenStorage : TokenStorage {
    private var access: String? = null
    private var refresh: String? = null
    private var expiresAt: Long = 0L

    override fun accessToken(): String? = access
    override fun refreshToken(): String? = refresh

    override fun saveTokens(access: String, refresh: String, expiresAtMillis: Long) {
        this.access = access
        this.refresh = refresh
        this.expiresAt = expiresAtMillis
    }

    override fun saveAccessToken(access: String, expiresAtMillis: Long) {
        this.access = access
        this.expiresAt = expiresAtMillis
    }

    override fun clear() {
        access = null
        refresh = null
        expiresAt = 0L
    }
}
