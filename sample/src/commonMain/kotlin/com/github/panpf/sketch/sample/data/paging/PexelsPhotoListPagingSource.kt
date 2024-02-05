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
package com.github.panpf.sketch.sample.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.panpf.sketch.sample.data.api.Apis
import com.github.panpf.sketch.sample.data.api.Response
import com.github.panpf.sketch.sample.data.api.pexels.PexelsPhoto
import com.github.panpf.sketch.sample.ui.model.Photo

class PexelsPhotoListPagingSource : PagingSource<Int, Photo>() {

    private val keySet = HashSet<String>()  // Compose LazyVerticalGrid does not allow a key repeat

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageNumber = (params.key ?: 0).coerceAtLeast(1)
        val response = try {
            Apis.pexelsApi.curated(pageNumber, params.loadSize)
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }

        return if (response is Response.Success) {
            val pexelsPhotos = response.body.photos
            val photos = pexelsPhotos.map { it.toPhoto() }
            val filteredPhotos = photos.filter { keySet.add(it.originalUrl) }
            val nextKey = if (pexelsPhotos.isNotEmpty()) pageNumber + 1 else null
            LoadResult.Page(filteredPhotos, null, nextKey)
        } else {
            response as Response.Error
            LoadResult.Error(Exception("Http error: ${response.throwable?.message}"))
        }
    }

    private fun PexelsPhoto.toPhoto(): Photo {
        return Photo(
            originalUrl = src.original,
            mediumUrl = src.large,
            thumbnailUrl = src.medium,
            width = width,
            height = height,
            exifOrientation = 0,
        )
    }
}