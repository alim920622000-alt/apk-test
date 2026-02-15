package com.android.teztaomapp.model

import com.google.gson.annotations.SerializedName

data class MerchantsResponse(
    @SerializedName("items") val items: List<Merchant>,
    @SerializedName("next_cursor") val nextCursor: String?
)
