package com.github.panpf.sketch.sample.ui.video

import android.content.Context
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.model.VideoInfo
import com.github.panpf.tools4k.coroutines.withToIO
import java.text.SimpleDateFormat

class LocalVideoListPagingSource(private val context: Context) :
    PagingSource<Int, VideoInfo>() {

    override fun getRefreshKey(state: PagingState<Int, VideoInfo>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoInfo> {
        val startPosition = params.key ?: 0
        val pageSize = params.loadSize

        val assetVideos = if (startPosition == 0) {
            withToIO {
                AssetImages.VIDEOS.map {
                    VideoInfo(
                        title = "sample",
                        path = it,
                        mimeType = "video/mp4",
                        size = 157092,
                        duration = 2000,
                        date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-01-25 17:08:22").time
                    )
                }
            }
        } else {
            emptyList()
        }

        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.MIME_TYPE
            ),
            null,
            null,
            MediaStore.Video.Media.DATE_TAKEN + " DESC" + " limit " + startPosition + "," + pageSize
        )
        val dataList = ArrayList<VideoInfo>(cursor?.count ?: 0).apply {
            cursor?.use {
                while (cursor.moveToNext()) {
                    add(
                        VideoInfo(
                            title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)),
                            path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)),
                            mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)),
                            size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)),
                            duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                                .toLong(),
                            date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN))
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
        return LoadResult.Page(assetVideos.plus(dataList), null, nextKey)
    }
}