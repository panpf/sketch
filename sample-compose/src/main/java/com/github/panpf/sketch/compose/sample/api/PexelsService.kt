package com.github.panpf.sketch.compose.sample.api

import com.github.panpf.sketch.compose.sample.bean.PexelsCurated
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PexelsService {
    @GET("v1/curated?per_page=20")
    suspend fun curated(@Query("page") pageIndex: Int): Response<PexelsCurated>
}