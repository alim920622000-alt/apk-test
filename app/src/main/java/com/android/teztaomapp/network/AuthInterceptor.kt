package com.android.teztaomapp.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(

    private val tokenStorage: TokenStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val token = tokenStorage.accessToken()

        Log.d("AUTH", "url=${req.url} hasToken=${!token.isNullOrBlank()}")

        val newReq = if (!token.isNullOrBlank()) {
            req.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else req

        return chain.proceed(newReq)
    }
}