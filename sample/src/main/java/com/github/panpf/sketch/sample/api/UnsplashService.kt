package com.github.panpf.sketch.sample.api

import com.github.panpf.sketch.sample.bean.UnsplashImage
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashService {
    @GET("photos/?client_id=72acaf7c7bd1230a9828557bdf68d6f598a85c90df5ecd43bb0cc07336ea4f93&&per_page=20")
    suspend fun listPhotos(@Query("page") pageIndex: Int): Response<List<UnsplashImage>>
}