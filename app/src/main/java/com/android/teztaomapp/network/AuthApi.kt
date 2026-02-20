package com.android.teztaomapp.network

import retrofit2.http.Body
import retrofit2.http.POST

data class RefreshRequest(val refresh_token: String)
data class TokenResponse(
    val access_token: String,
    val refresh_token: String?,
    val expires_in: Long,
    val token_type: String?
)

interface AuthApi {
    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): TokenResponse
}
