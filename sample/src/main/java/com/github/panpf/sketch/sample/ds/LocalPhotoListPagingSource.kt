package com.github.panpf.sketch.sample.ds

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.panpf.sketch.sample.AssetImage
import com.github.panpf.sketch.sample.bean.Photo
import com.github.panpf.tools4k.coroutines.withToIO

class LocalPhotoListPagingSource(private val context: Context) :
    PagingSource<Int, Photo>() {

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val startPosition = params.key ?: 0
        val pageSize = params.loadSize

        val assetPhotos = if (startPosition == 0) {
            withToIO {
                AssetImage.IMAGES_FORMAT.plus(AssetImage.IMAGES_HUGE).map {
                    val options = context.assets.open(it.replace("asset://", "")).use {
                        BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                            BitmapFactory.decodeStream(it, null, this)
                        }
                    }
                    Photo(
                        originalUrl = it,
                        thumbnailUrl = null,
                        middenUrl = null,
                        width = options.outWidth,
                        height = options.outHeight,
                    )
                }
            }
        } else {
            emptyList()
        }

        val dataList = withToIO {
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
            ArrayList<Photo>(cursor?.count ?: 0).apply {
                cursor?.use {
                    while (cursor.moveToNext()) {
                        val uri =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                        val options =
                            context.contentResolver.openInputStream(Uri.parse("file://$uri")).use {
                                BitmapFactory.Options().apply {
                                    inJustDecodeBounds = true
                                    BitmapFactory.decodeStream(it, null, this)
                                }
                            }
                        add(
                            Photo(
                                originalUrl = uri,
                                thumbnailUrl = null,
                                middenUrl = null,
                                width = options.outWidth,
                                height = options.outHeight,
                            )
                        )
                    }
                }
            }
        }

        val nextKey = if (dataList.isNotEmpty()) {
            startPosition + pageSize
        } else {
            null
        }
        return LoadResult.Page(assetPhotos.plus(dataList), null, nextKey)
    }
}