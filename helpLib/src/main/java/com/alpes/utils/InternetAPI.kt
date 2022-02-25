package com.alpes.utils

import retrofit2.http.GET
import retrofit2.http.Url



interface InternetAPI {
    @GET
    suspend fun getRoot2(@Url url: String) : Wrapper
}
