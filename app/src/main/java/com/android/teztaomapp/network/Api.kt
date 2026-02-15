package com.android.teztaomapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
object Api {

    private const val BASE_URL = "http://141.227.138.64:8000/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    // TODO: вставь сюда реальный токен
    private const val STATIC_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjEzNjEwNzkyOTksInJvbGUiOiJhZG1pbl9zaG9wIiwiaWF0IjoxNzcxMDUzNTY1LCJleHAiOjE3NzExMzk5NjV9.-82CAmQ-lWyjNtyCFO3GQ_9WvU7glnt3APHiVi6dUTk"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor { STATIC_TOKEN })
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
