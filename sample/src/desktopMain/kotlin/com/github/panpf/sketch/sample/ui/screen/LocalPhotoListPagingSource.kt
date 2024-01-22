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
package com.github.panpf.sketch.sample.ui.screen

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.panpf.sketch.resources.AssetImages

class LocalPhotoListPagingSource :
    PagingSource<Int, Photo>() {

    private val keySet = HashSet<String>()  // Compose LazyVerticalGrid does not allow a key repeat

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0

    private val images =
        (AssetImages.statics + AssetImages.anims + AssetImages.clockExifs + arrayOf(AssetImages.longQMSHT)).asList()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageIndex = (params.key ?: 0)
        val loadSize = params.loadSize
        val fromIndex = pageIndex * loadSize
        val sliceImages = if (fromIndex < images.size) {
            val toIndex = ((pageIndex + 1) * loadSize).coerceAtMost(images.size)
            images.subList(fromIndex, toIndex)
        } else {
            emptyList()
        }
        val slicePhotos = sliceImages.map { Photo(it.uri, it.uri) }
        val filteredPhotos = slicePhotos.filter { keySet.add(it.diffKey) }
        val nextKey = if (sliceImages.isNotEmpty()) pageIndex + 1 else null
        return LoadResult.Page(filteredPhotos, null, nextKey)
    }
}