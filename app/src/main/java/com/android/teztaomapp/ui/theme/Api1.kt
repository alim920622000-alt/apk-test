package com.android.teztaomapp.ui.theme

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class Merchant(
    val id: Int,
    val name: String,
    val business_type: String? = null
)

interface ApiService {
    @GET("/catalog/merchants")
    suspend fun getMerchants(@Query("type") type: String? = null): List<Merchant>
}

object Api {
    private const val BASE_URL = "http://141.227.138.64:8000"

    private val client: OkHttpClient by lazy {
        val log = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        OkHttpClient.Builder().addInterceptor(log).build()
    }

    val service: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}