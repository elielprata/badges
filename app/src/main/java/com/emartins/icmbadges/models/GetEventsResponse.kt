package com.emartins.icmbadges.models

data class GetEventsWrapper(
    val total: Int,
    val items: List<GetEventsResponse>
)

data class GetEventsResponse(
    val id: String,
    val classes_desc: String
)
