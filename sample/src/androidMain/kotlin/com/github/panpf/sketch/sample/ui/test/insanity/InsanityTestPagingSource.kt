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
package com.github.panpf.sketch.sample.ui.test.insanity

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrNull
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4k.coroutines.withToIO

class InsanityTestPagingSource(private val context: Context) :
    PagingSource<Int, Photo>() {

    private val keySet = HashSet<String>()  // Compose LazyVerticalGrid does not allow a key repeat

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val startPosition = params.key ?: 0
        val assetPhotos = if (startPosition == 0) readAssetPhotos() else emptyList()
        val photos = urisToPhotos(assetPhotos)
        return LoadResult.Page(photos.filter { keySet.add(it.originalUrl) }, null, null)
    }

    private suspend fun readAssetPhotos(): List<String> = withToIO {
        buildList {
            repeat(100) {
                addAll(AssetImages.numbers.map { it.uri })
            }
        }
    }

    private suspend fun urisToPhotos(uris: List<String>): List<Photo> = withToIO {
        uris.mapIndexed { index, uri ->
            val sketch = context.sketch
            val fetcher = sketch.components.newFetcherOrThrow(ImageRequest(context, uri))
            val dataSource = fetcher.fetch().getOrThrow().dataSource
            val imageInfo =
                dataSource.readImageInfoWithBitmapFactoryOrNull(context.appSettingsService.ignoreExifOrientation.value)
            if (imageInfo != null) {
                val exifOrientationHelper = ExifOrientationHelper(imageInfo.exifOrientation)
                val imageSize = Size(imageInfo.width, imageInfo.height)
                val size = exifOrientationHelper?.applyToSize(imageSize) ?: imageSize
                Photo(
                    originalUrl = uri,
                    mediumUrl = null,
                    thumbnailUrl = null,
                    width = size.width,
                    height = size.height,
                    exifOrientation = imageInfo.exifOrientation,
                    index = index,
                )
            } else {
                Photo(
                    originalUrl = uri,
                    mediumUrl = null,
                    thumbnailUrl = null,
                    width = null,
                    height = null,
                    exifOrientation = 0,
                    index = index,
                )
            }
        }
    }
}