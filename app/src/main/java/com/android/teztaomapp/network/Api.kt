package com.android.teztaomapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {
    private const val BASE_URL = "http://141.227.138.64:8000/"

    // Временно: хранилище в памяти. Потом заменим на DataStore.
    private val tokenStorage: TokenStorage = InMemoryTokenStorage()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Отдельный retrofit для refresh (без authenticator!)
    private val authApi: AuthApi by lazy {
        val refreshClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(refreshClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor { tokenStorage.accessToken() })
            .authenticator(TokenAuthenticator(tokenStorage, authApi))
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // на время — чтобы из Login сохранить токены
    fun tokenStorage(): TokenStorage = tokenStorage
}
