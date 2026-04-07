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

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.data.BuiltinLocalPhotoListRepo
import com.github.panpf.sketch.sample.data.GalleryLocalPhotoListRepo
import com.github.panpf.sketch.sample.ui.model.Photo

class LocalPhotoListPagingSource(val sketch: Sketch) : PagingSource<Int, Photo>() {

    private val builtinPhotoListRepo = BuiltinLocalPhotoListRepo(sketch)
    private val localPhotoListRepo = GalleryLocalPhotoListRepo(sketch)

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageStart = params.key ?: 0
        val pageSize = params.loadSize

        val builtinPhotos = builtinPhotoListRepo.loadLocalPhotoList(pageStart, pageSize)
        val galleryPhotos = if (builtinPhotos.size < pageSize) {
            val builtinPhotoListSize = builtinPhotoListRepo.size
            val galleryPageStart = if (pageStart < builtinPhotoListSize)
                0 else pageStart - builtinPhotoListSize
            val galleryPageSize = pageSize - builtinPhotos.size
            localPhotoListRepo.loadLocalPhotoList(
                pageStart = galleryPageStart,
                pageSize = galleryPageSize
            )
        } else {
            emptyList()
        }
        val dataList = builtinPhotos + galleryPhotos
        val nextKey = if (dataList.size >= pageSize) pageStart + pageSize else null
        return LoadResult.Page(data = dataList, prevKey = null, nextKey = nextKey)
    }
}