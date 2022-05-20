package com.alpes.utils

import androidx.annotation.Keep
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


@Keep
@JsonClass(generateAdapter = true)
data class App(
    val bundle: String,
    val appName: String,
    val source: String? = null,
    val appsFlyer: String? = null,
    val fbAppId: String? = null,
    val fbClientSecret: String?=null
)


