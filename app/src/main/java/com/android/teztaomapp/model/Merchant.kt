package com.android.teztaomapp.model

data class Merchant(
    val id: Int,
    val name: String,
    val business_type: String,
    val phone: String?,
    val address: String?,
    val logo_url: String?,
    val about: String?,
    val is_active: Int
)
