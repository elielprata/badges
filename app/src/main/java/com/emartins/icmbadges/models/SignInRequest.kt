package com.emartins.icmbadges.models

data class SignInRequest(
    val email: String,
    val senha: String,
    val atualizarTermo: Boolean = false
)