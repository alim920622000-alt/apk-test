package com.android.teztaomapp.network

import com.android.teztaomapp.model.MerchantsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("catalog/merchants")
    suspend fun getMerchants(
        @Query("type") type: String,
        @Query("cursor") cursor: String? = null
    ): MerchantsResponse

}
