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
import androidx.exifinterface.media.ExifInterface
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.caverock.androidsvg.SVG
import com.github.panpf.sketch.datasource.BasedStreamDataSource
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrNull
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.util.ExifOrientationTestFileHelper
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4k.coroutines.withToIO

class LocalPhotoListPagingSource(private val context: Context) :
    PagingSource<Int, Photo>() {

    private val keySet = HashSet<String>()  // Compose LazyVerticalGrid does not allow a key repeat

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val startPosition = params.key ?: 0
        val pageSize = params.loadSize

        val assetPhotos = if (startPosition == 0) readAssetPhotos() else emptyList()
        val exifPhotos = if (startPosition == 0) readExifPhotos() else emptyList()
        val dataList = readLocalPhotos(startPosition, pageSize)

        val photos = urisToPhotos(assetPhotos.plus(exifPhotos).plus(dataList))
        val nextKey = if (dataList.isNotEmpty()) startPosition + pageSize else null
        return LoadResult.Page(photos.filter { keySet.add(it.diffKey) }, null, nextKey)
    }

    private suspend fun readAssetPhotos(): List<String> = withToIO {
        AssetImages.statics
            .plus(AssetImages.anims)
            .plus(AssetImages.longQMSHT)
            .map { it.uri }
            .toList()
    }

    private suspend fun readExifPhotos(): List<String> = withToIO {
        ExifOrientationTestFileHelper(context, AssetImages.clockHor.fileName)
            .files()
            .map { it.file.path }
            .toList()
    }

    private suspend fun readLocalPhotos(startPosition: Int, pageSize: Int): List<String> =
        withToIO {
            @Suppress("DEPRECATION") val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Images.Media.TITLE,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.MIME_TYPE
                ),
                null,
                null,
                MediaStore.Images.Media.DATE_TAKEN + " DESC" + " limit " + startPosition + "," + pageSize
            )
            ArrayList<String>(cursor?.count ?: 0).apply {
                cursor?.use {
                    while (cursor.moveToNext()) {
                        @Suppress("DEPRECATION") val uri =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                        add(uri)
                    }
                }
            }
        }

    private suspend fun urisToPhotos(uris: List<String>): List<Photo> = withToIO {
        uris.map { uri ->
            var imageInfo: ImageInfo? = null
            val sketch = context.sketch
            try {
                val fetcher = sketch.components.newFetcherOrThrow(ImageRequest(context, uri))
                val dataSource = fetcher.fetch().getOrThrow().dataSource as BasedStreamDataSource
                imageInfo = if (uri.endsWith(".svg")) {
                    dataSource.readImageInfoWithSVG()
                } else {
                    dataSource.readImageInfoWithBitmapFactoryOrNull(context.appSettingsService.ignoreExifOrientation.value)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
                )
            } else {
                Photo(
                    originalUrl = uri,
                    mediumUrl = null,
                    thumbnailUrl = null,
                    width = null,
                    height = null,
                    exifOrientation = 0,
                )
            }
        }
    }

    private fun BasedStreamDataSource.readImageInfoWithSVG(useViewBoundsAsIntrinsicSize: Boolean = true): ImageInfo {
        val svg = openInputStream().buffered().use { SVG.getFromInputStream(it) }
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
            ExifInterface.ORIENTATION_UNDEFINED
        )
    }
}