package com.android.teztaomapp.network

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenStorage: TokenStorage,
    private val authApi: AuthApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // защита от бесконечного цикла
        if (responseCount(response) >= 2) return null

        val refresh = tokenStorage.refreshToken() ?: return null

        val newAccess = runBlocking {
            try {
                val r = authApi.refresh(RefreshRequest(refresh))
                // expires_in -> millis
                val expiresAt = System.currentTimeMillis() + r.expires_in * 1000
                tokenStorage.saveAccessToken(r.access_token, expiresAt)
                r.access_token
            } catch (_: Exception) {
                tokenStorage.clear()
                null
            }
        } ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccess")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var r: Response? = response
        var count = 1
        while (r?.priorResponse != null) {
            count++
            r = r.priorResponse
        }
        return count
    }
}
