package com.alpes.utils

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Wrapper(
    val links: List<Link>
)

@JsonClass(generateAdapter = true)
data class Link(
    val app_id: String,
    val app_name: String,
    val link: String?,
    val appsFlyer: String,
    val ip: String?
)


