package com.android.teztaomapp.network

import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val telegram_user_id: Long)
data class RefreshRequest(val refresh_token: String)

data class TokenResponse(
    val access_token: String,
    val refresh_token: String? = null,
    val expires_in: Long,
    val token_type: String? = null
)
interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse
    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): TokenResponse
}
