package me.panpf.sketch.sample.ds

import android.content.Context
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.panpf.sketch.sample.bean.ImageInfo
import me.panpf.sketch.sample.bean.VideoInfo

class LocalPhotoListPagingSource(private val context: Context) :
    PagingSource<Int, ImageInfo>() {

    override fun getRefreshKey(state: PagingState<Int, ImageInfo>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageInfo> {
        val startPosition = params.key ?: 0
        val pageSize = params.loadSize

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
        val dataList = ArrayList<ImageInfo>(cursor?.count ?: 0).apply {
            cursor?.use {
                while (cursor.moveToNext()) {
                    add(
                        ImageInfo(
                            title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)),
                            path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)),
                            mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)),
                            size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)),
                            date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN))
                        )
                    )
                }
            }
        }
        val nextKey = if (dataList.isNotEmpty()) {
            startPosition + pageSize
        } else {
            null
        }
        return LoadResult.Page(dataList, null, nextKey)
    }
}