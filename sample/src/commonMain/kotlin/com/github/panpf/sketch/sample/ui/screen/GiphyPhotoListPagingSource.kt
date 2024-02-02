///*
// * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.github.panpf.sketch.sample.ui.screen
//
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.github.panpf.sketch.sample.data.Apis
//import com.github.panpf.sketch.sample.data.Response
//import com.github.panpf.sketch.sample.data.giphy.GiphyGif
//import com.github.panpf.sketch.sample.ui.model.Photo
//
//class GiphyPhotoListPagingSource : PagingSource<Int, Photo>() {
//
////    private val keySet = HashSet<String>()  // Compose LazyVerticalGrid does not allow a key repeat
//
//    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
//        val pageStart = params.key ?: 0
//        val pageSize = params.loadSize
//        val response = try {
//            Apis.giphyApi.trending(pageStart, pageSize)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return LoadResult.Error(e)
//        }
//
//        return if (response is Response.Success) {
//            val giphyPhotos = response.body.dataList ?: emptyList()
//            val photos = giphyPhotos.map { it.toPhoto() }
////            val filteredPhotos = photos.filter { keySet.add(it.diffKey) }
//            val filteredPhotos = photos
//            val nextKey = if (giphyPhotos.isNotEmpty()) pageStart + pageSize else null
//            LoadResult.Page(filteredPhotos, null, nextKey)
//        } else {
//            response as Response.Error
//            LoadResult.Error(Exception("Http error: ${response.throwable?.message}"))
//        }
//    }
//
//    private fun GiphyGif.toPhoto(): Photo {
//        return Photo(
//            originalUrl = images.original.downloadUrl,
//            mediumUrl = images.original.downloadUrl,
//            thumbnailUrl = images.fixedWidth.downloadUrl,
//            width = images.original.width.toInt(),
//            height = images.original.height.toInt(),
//            exifOrientation = 0,
//        )
//    }
//}