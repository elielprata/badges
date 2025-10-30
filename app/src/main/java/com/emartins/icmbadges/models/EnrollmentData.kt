package com.emartins.icmbadges.models

data class EnrollmentDataWrapper(
    val total: Int,
    val items: List<EnrollmentData>
)

data class EnrollmentData(
    val isem_id: String,
    val nome: String,
    val dsc_classe: String,
    val nom_igreja: String
)
