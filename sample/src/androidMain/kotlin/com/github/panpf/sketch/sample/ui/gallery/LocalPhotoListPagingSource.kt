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
package com.github.panpf.sketch.sample.ui.gallery

import android.content.Context
import android.graphics.RectF
import android.provider.MediaStore
import androidx.core.content.PermissionChecker
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.caverock.androidsvg.SVG
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrThrow
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sketch
import com.github.panpf.tools4k.coroutines.withToIO
import okio.buffer

class LocalPhotoListPagingSource(private val context: Context) :
    PagingSource<Int, Photo>() {

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

        val photoUris = if (startPosition < builtInPhotos.size) {
            val fromBuiltInPhotos = builtInPhotos.subList(
                fromIndex = startPosition,
                toIndex = (startPosition + pageSize).coerceAtMost(builtInPhotos.size)
            )
            val fromPhotoAlbumPhotos = if (fromBuiltInPhotos.size < pageSize) {
                val photoAlbumStartPosition = 0
                val photoAlbumPageSize = pageSize - fromBuiltInPhotos.size
                readPhotosFromPhotoAlbum(photoAlbumStartPosition, photoAlbumPageSize)
            } else {
                emptyList()
            }
            fromBuiltInPhotos.toMutableList().apply {
                addAll(fromPhotoAlbumPhotos)
            }
        } else {
            val photoAlbumStartPosition = startPosition - builtInPhotos.size
            @Suppress("UnnecessaryVariable") val photoAlbumPageSize = pageSize
            readPhotosFromPhotoAlbum(photoAlbumStartPosition, photoAlbumPageSize)
        }
        val nextKey = if (photoUris.isNotEmpty()) startPosition + pageSize else null
        val photos = photoUris
            .map { uri -> uriToPhoto(uri) }
            .filter { keySet.add(it.diffKey) }
        return LoadResult.Page(photos, null, nextKey)
    }

    private suspend fun readPhotosFromPhotoAlbum(startPosition: Int, pageSize: Int): List<String> {
        val checkSelfPermission = PermissionChecker
            .checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if (checkSelfPermission != PermissionChecker.PERMISSION_GRANTED) {
            return emptyList()
        }
        return withToIO {
            val cursor = context.contentResolver.query(
                /* uri = */ MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                /* projection = */
                arrayOf(
                    MediaStore.Images.Media.TITLE,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.DATE_TAKEN,
                ),
                /* selection = */
                null,
                /* selectionArgs = */
                null,
                /* sortOrder = */
                MediaStore.Images.Media.DATE_TAKEN + " DESC" + " limit " + startPosition + "," + pageSize
            )
            ArrayList<String>(cursor?.count ?: 0).apply {
                cursor?.use {
                    while (cursor.moveToNext()) {
                        val uri =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                        add(uri)
                    }
                }
            }
        }
    }

    private suspend fun uriToPhoto(uri: String): Photo {
        val imageInfo = withToIO {
            runCatching {
                val sketch = context.sketch
                val fetcher = sketch.components.newFetcherOrThrow(ImageRequest(context, uri))
                val dataSource = fetcher.fetch().getOrThrow().dataSource
                if (uri.endsWith(".svg")) {
                    dataSource.readSVGImageInfo()
                } else {
                    dataSource.readImageInfoWithBitmapFactoryOrThrow(context.appSettingsService.ignoreExifOrientation.value)
                }
            }.apply {
                if (isFailure) {
                    exceptionOrNull()?.printStackTrace()
                }
            }.getOrNull()
        }?.let {
            val newSize = ExifOrientationHelper(it.exifOrientation)?.applyToSize(it.size) ?: it.size
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

    private fun DataSource.readSVGImageInfo(useViewBoundsAsIntrinsicSize: Boolean = true): ImageInfo {
        val svg = openSource().buffer().inputStream().use { SVG.getFromInputStream(it) }
        val width: Int
        val height: Int
        val viewBox: RectF? = svg.documentViewBox
        if (useViewBoundsAsIntrinsicSize && viewBox != null) {
            width = viewBox.width().toInt()
            height = viewBox.height().toInt()
        } else {
            width = svg.documentWidth.toInt()
            height = svg.documentHeight.toInt()
        }
        return ImageInfo(
            width,
            height,
            SvgDecoder.MIME_TYPE,
            ExifOrientation.UNDEFINED
        )
    }
}