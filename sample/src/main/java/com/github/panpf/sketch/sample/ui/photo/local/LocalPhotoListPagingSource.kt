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
package com.github.panpf.sketch.sample.ui.photo.local

import android.content.Context
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrNull
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.prefsService
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
        AssetImages.STATICS
            .plus(AssetImages.ANIMATEDS)
            .plus(AssetImages.NUMBERS)
            .plus(AssetImages.HUGES)
            .plus(AssetImages.LONGS).toList()
    }

    private suspend fun readExifPhotos(): List<String> = withToIO {
        // The exif_origin_girl_ver image is relatively large, and some devices may crash due to insufficient memory
        ExifOrientationTestFileHelper(context, "exif_origin_girl_ver.jpeg", 2).files()
            .map { it.file.path }
            .plus(
                ExifOrientationTestFileHelper(context, "exif_origin_clock_hor.jpeg")
                    .files()
                    .map { it.file.path })
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
            val sketch = context.sketch
            val fetcher = sketch.components.newFetcher(LoadRequest(context, uri))
            val dataSource = fetcher.fetch().dataSource
            val imageInfo =
                dataSource.readImageInfoWithBitmapFactoryOrNull(context.prefsService.ignoreExifOrientation.value)
            if (imageInfo != null) {
                val exifOrientationHelper = ExifOrientationHelper(imageInfo.exifOrientation)
                val size =
                    exifOrientationHelper.applyToSize(Size(imageInfo.width, imageInfo.height))
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
}