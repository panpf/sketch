/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.sample.ui.gallery

import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingState
import app.cash.paging.createPagingSourceLoadResultPage
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.data.builtinImages
import com.github.panpf.sketch.sample.data.localImages
import com.github.panpf.sketch.sample.data.readImageInfoOrNull
import com.github.panpf.sketch.sample.ui.model.Photo

class LocalPhotoListPagingSource(val sketch: Sketch) : PagingSource<Int, Photo>() {

    private val keySet = HashSet<String>()  // Compose LazyVerticalGrid does not allow a key repeat
    private var photos: List<String>? = null

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0

    override suspend fun load(params: PagingSourceLoadParams<Int>): PagingSourceLoadResult<Int, Photo> {
        val startPosition = params.key ?: 0
        val pageSize = params.loadSize

        var photos = this@LocalPhotoListPagingSource.photos
        if (photos == null) {
            val builtinPhotos = builtinImages().map { it.uri }
            val localPhotos = localImages(sketch.context)
            photos = builtinPhotos + localPhotos
            this.photos = photos
        }

        val toIndex = (startPosition + pageSize).coerceAtMost(photos.size)
        val pagePhotos = photos.subList(
            fromIndex = startPosition,
            toIndex = toIndex
        ).map { uri ->
            val imageInfo = readImageInfoOrNull(sketch = sketch, uri = uri)
            Photo(
                originalUrl = uri,
                mediumUrl = null,
                thumbnailUrl = null,
                width = imageInfo?.width,
                height = imageInfo?.height,
            )
        }
        val nextKey = if (pagePhotos.isNotEmpty()) startPosition + pageSize else null
        val filteredPhotos = pagePhotos.filter { keySet.add(it.originalUrl) }
        return createPagingSourceLoadResultPage(
            data = filteredPhotos,
            prevKey = null,
            nextKey = nextKey
        )
    }
}