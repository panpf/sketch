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
package com.github.panpf.sketch.sample.ui.photo.pexels

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.panpf.sketch.sample.apiService
import com.github.panpf.sketch.sample.model.Photo

class PexelsPhotoListPagingSource(private val context: Context) :
    PagingSource<Int, Photo>() {

    private val keySet = HashSet<String>()  // Compose LazyVerticalGrid does not allow a key repeat

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageIndex = (params.key ?: 0).coerceAtLeast(1)
        val response = try {
            context.apiService.pexels.curated(pageIndex, params.loadSize)
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }

        return if (response.isSuccessful) {
            val dataList = (response.body()?.photos?.map {
                Photo(
                    originalUrl = it.src.original,
                    mediumUrl = it.src.large,
                    thumbnailUrl = it.src.medium,
                    width = it.width,
                    height = it.height,
                    exifOrientation = 0,
                )
            } ?: emptyList())
            val nextKey = if (dataList.isNotEmpty()) {
                pageIndex + 1
            } else {
                null
            }
            LoadResult.Page(dataList.filter { keySet.add(it.diffKey) }, null, nextKey)
        } else {
            LoadResult.Error(Exception("Http coded error: code=${response.code()}. message=${response.message()}"))
        }
    }
}