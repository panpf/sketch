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
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.model.Photo

expect suspend fun readPhotosFromPhotoAlbum(
    context: PlatformContext,
    startPosition: Int,
    pageSize: Int
): List<String>

expect suspend fun readImageInfoOrNull(
    context: PlatformContext,
    sketch: Sketch,
    uri: String,
    ignoreExifOrientation: Boolean
): ImageInfo?

class LocalPhotoListPagingSource(
    val context: PlatformContext,
    val sketch: Sketch
) : PagingSource<Int, Photo>() {

    private val keySet = HashSet<String>()  // Compose LazyVerticalGrid does not allow a key repeat
    private val builtInPhotos: List<String> by lazy {
        AssetImages.statics
            .plus(AssetImages.anims)
            .plus(AssetImages.longQMSHT)
            .plus(AssetImages.clockExifs)
            .map { it.uri }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val startPosition = params.key ?: 0
        val pageSize = params.loadSize

        val photos = if (startPosition < builtInPhotos.size) {
            val fromBuiltInPhotos = builtInPhotos.subList(
                fromIndex = startPosition,
                toIndex = (startPosition + pageSize).coerceAtMost(builtInPhotos.size)
            )
            val fromPhotoAlbumPhotos = if (fromBuiltInPhotos.size < pageSize) {
                val photoAlbumStartPosition = 0
                val photoAlbumPageSize = pageSize - fromBuiltInPhotos.size
                readPhotosFromPhotoAlbum(context, photoAlbumStartPosition, photoAlbumPageSize)
            } else {
                emptyList()
            }
            fromBuiltInPhotos.toMutableList().apply {
                addAll(fromPhotoAlbumPhotos)
            }
        } else {
            val photoAlbumStartPosition = startPosition - builtInPhotos.size
            @Suppress("UnnecessaryVariable") val photoAlbumPageSize = pageSize
            readPhotosFromPhotoAlbum(context, photoAlbumStartPosition, photoAlbumPageSize)
        }.map { uri -> uriToPhoto(uri) }
        val nextKey = if (photos.isNotEmpty()) startPosition + pageSize else null
        val filteredPhotos = photos.filter { keySet.add(it.originalUrl) }
        return LoadResult.Page(filteredPhotos, null, nextKey)
    }

    private suspend fun uriToPhoto(uri: String): Photo {
        val imageInfo = readImageInfoOrNull(
            context = context,
            sketch = sketch,
            uri = uri,
            ignoreExifOrientation = !context.appSettings.exifOrientation.value
        )?.let {
            val exifOrientationHelper = ExifOrientationHelper(it.exifOrientation)
            val newSize = exifOrientationHelper?.applyToSize(it.size) ?: it.size
            it.copy(size = newSize)
        }
        return Photo(
            originalUrl = uri,
            mediumUrl = null,
            thumbnailUrl = null,
            width = imageInfo?.width,
            height = imageInfo?.height,
            exifOrientation = imageInfo?.exifOrientation ?: ExifOrientation.UNDEFINED,
        )
    }
}