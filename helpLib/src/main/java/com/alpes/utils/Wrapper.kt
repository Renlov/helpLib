package com.alpes.utils

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass


@Keep
@JsonClass(generateAdapter = true)
data class App(
    val bundle: String,
    val appName: String,
    val source: String? = null
)


