package com.android.teztaomapp.network

interface TokenStorage {
    fun accessToken(): String?
    fun refreshToken(): String?
    fun saveTokens(access: String, refresh: String, expiresAtMillis: Long)
    fun saveAccessToken(access: String, expiresAtMillis: Long)
    fun clear()
}
