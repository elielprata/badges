package com.emartins.icmbadges.models

import com.google.gson.annotations.SerializedName

data class EnrollmentDataWrapper(
    val total: Int,
    val items: List<EnrollmentData>
)

data class EnrollmentData(

    @SerializedName("isem_id", alternate = ["id"])
    val isem_id: String,

    // pode vir como "nome" (seminarista) ou "nom_membro" (voluntário)
    @SerializedName(value = "nome", alternate = ["nom_membro"])
    val nome: String? = null,

    val dsc_classe: String? = null,

    val nom_igreja: String? = null
)