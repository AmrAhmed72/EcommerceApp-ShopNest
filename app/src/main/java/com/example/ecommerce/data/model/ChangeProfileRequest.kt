package com.example.ecommerce.data.model

data class ChangeProfileRequest(
    val email: String,
    val image: String,
    val name: String,
    val password: String,
    val phone: String
)