package com.emartins.icmbadges.models

data class SignInResponse(
    val id: Int,
    val nome: String,
    val accessToken: String
)