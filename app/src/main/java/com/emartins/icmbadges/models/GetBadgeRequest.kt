package com.emartins.icmbadges.models

data class GetBadgeRequest(
    val ids: List<String>,
    val mode: String = "seminaristas",
    val termica: Boolean = true,
    val cod_igreja: Int? = null,
    val pagina: Int = 1
)
