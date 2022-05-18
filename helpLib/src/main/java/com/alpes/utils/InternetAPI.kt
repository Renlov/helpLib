package com.alpes.utils

import retrofit2.http.*


interface InternetAPI {
    @GET
    suspend fun getRoot2(@Url url: String): Wrapper

    @GET("apps")
    suspend fun getApp(
        @Query("search") bundle: String
    ): App

    @POST("add_app")
    suspend fun insertApp(
        @Body app: App
    ): App
}
