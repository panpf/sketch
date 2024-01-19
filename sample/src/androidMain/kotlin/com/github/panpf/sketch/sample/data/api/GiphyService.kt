/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.sample.data.api

import com.github.panpf.sketch.sample.data.giphy.GiphySearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyService {
    @GET("/v1/gifs/search?type=gifs&sort=&api_key=Gc7131jiJuvI7IdN0HZ1D7nh0ow5BU6g&pingback_id=17c5f87f46b18d99")
    suspend fun search(
        @Query("q") queryWord: String?,
        @Query("offset") pageStart: Int,
        @Query("limit") pageSize: Int
    ): Response<GiphySearchResponse>

    @GET("/v1/gifs/trending?&api_key=Gc7131jiJuvI7IdN0HZ1D7nh0ow5BU6g&pingback_id=17c5f87f46b18d99")
    suspend fun trending(
        @Query("offset") pageStart: Int,
        @Query("limit") pageSize: Int
    ): Response<GiphySearchResponse>
}