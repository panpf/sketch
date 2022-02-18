package com.github.panpf.sketch.sample.ds

import android.content.Context
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.readExifOrientationWithMimeType
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrNull
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.bean.Photo
import com.github.panpf.sketch.sample.util.ExifOrientationTestFileHelper
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4k.coroutines.withToIO

class LocalPhotoListPagingSource(private val context: Context) :
    PagingSource<Int, Photo>() {

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val startPosition = params.key ?: 0
        val pageSize = params.loadSize

        val assetPhotos = if (startPosition == 0) readAssetPhotos() else emptyList()
        val exifPhotos = if (startPosition == 0) readExifPhotos() else emptyList()
        val dataList = readLocalPhotos(startPosition, pageSize)

        val photos = urisToPhotos(assetPhotos.plus(exifPhotos).plus(dataList))
        val nextKey = if (dataList.isNotEmpty()) startPosition + pageSize else null;
        return LoadResult.Page(photos, null, nextKey)
    }

    private suspend fun readAssetPhotos(): List<String> = withToIO {
        AssetImages.FORMATS
            .plus(AssetImages.HUGES)
            .plus(AssetImages.LONGS).toList()
    }

    private suspend fun readExifPhotos(): List<String> = withToIO {
        ExifOrientationTestFileHelper(context, "exif_origin_girl_ver.jpeg").files()
            .map { it.file.path }
            .plus(
                ExifOrientationTestFileHelper(context, "exif_origin_clock_hor.jpeg")
                    .files()
                    .map { it.file.path })
            .toList()
    }

    private suspend fun readLocalPhotos(startPosition: Int, pageSize: Int): List<String> =
        withToIO {
            val cursor = context.contentResolver.query(
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
                        val uri =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                        add(uri)
                    }
                }
            }
        }

    private suspend fun urisToPhotos(uris: List<String>): List<Photo> = withToIO {
        uris.map { uri ->
            val sketch = context.sketch
            val fetcher = sketch.componentRegistry.newFetcher(sketch, LoadRequest(context, uri))
            val dataSource = fetcher.fetch().dataSource
            val imageInfo = dataSource.readImageInfoWithBitmapFactoryOrNull()
            if (imageInfo != null) {
                val exifOrientation =
                    if (context.appSettingsService.ignoreExifOrientation.value != true) {
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