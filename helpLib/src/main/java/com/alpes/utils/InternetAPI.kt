package com.alpes.utils

import retrofit2.Response
import retrofit2.http.*


interface InternetAPI {
    @GET("apps")
    suspend fun getApp(
        @Query("search") bundle: String
    ): App
}
