package com.github.panpf.sketch.sample.ui.test.insanity

import android.content.Context
import androidx.exifinterface.media.ExifInterface
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.readExifOrientationWithMimeType
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrNull
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4k.coroutines.withToIO

class InsanityTestPagingSource(private val context: Context) :
    PagingSource<Int, Photo>() {

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val startPosition = params.key ?: 0
        val assetPhotos = if (startPosition == 0) readAssetPhotos() else emptyList()
        val photos = urisToPhotos(assetPhotos)
        return LoadResult.Page(photos, null, null)
    }

    private suspend fun readAssetPhotos(): List<String> = withToIO {
        buildList {
            repeat(100) {
                addAll(AssetImages.NUMBERS)
            }
        }
    }

    private suspend fun urisToPhotos(uris: List<String>): List<Photo> = withToIO {
        uris.map { uri ->
            val sketch = context.sketch
            val fetcher = sketch.components.newFetcher(LoadRequest(context, uri))
            val dataSource = fetcher.fetch().dataSource
            val imageInfo = dataSource.readImageInfoWithBitmapFactoryOrNull()
            if (imageInfo != null) {
                val exifOrientation =
                    if (!context.prefsService.ignoreExifOrientation.value) {
                        dataSource.readExifOrientationWithMimeType(imageInfo.mimeType)
                    } else {
                        ExifInterface.ORIENTATION_UNDEFINED
                    }
                val exifOrientationHelper = ExifOrientationHelper(exifOrientation)
                val size =
                    exifOrientationHelper.applyToSize(Size(imageInfo.width, imageInfo.height))
                Photo(
                    originalUrl = uri,
                    thumbnailUrl = null,
                    middenUrl = null,
                    width = size.width,
                    height = size.height,
                    exifOrientation = exifOrientation,
                )
            } else {
                Photo(
                    originalUrl = uri,
                    thumbnailUrl = null,
                    middenUrl = null,
                    width = null,
                    height = null,
                    exifOrientation = 0,
                )
            }
        }
    }
}