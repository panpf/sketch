/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.sample.ui.test

import android.content.Context
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.sample.model.VideoInfo
import com.github.panpf.tools4k.coroutines.withToIO
import java.text.SimpleDateFormat
import java.util.Locale

class LocalVideoListPagingSource(private val context: Context) :
    PagingSource<Int, VideoInfo>() {

    private val keySet = HashSet<String>()  // Compose LazyVerticalGrid does not allow a key repeat

    override fun getRefreshKey(state: PagingState<Int, VideoInfo>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoInfo> {
        val startPosition = params.key ?: 0
        val pageSize = params.loadSize

        val assetVideos = if (startPosition == 0) {
            withToIO {
                ComposeResImageFiles.videos.map {
                    val simpleDateFormat =
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    VideoInfo(
                        title = "sample",
                        path = it.uri,
                        mimeType = "video/mp4",
                        size = 157092,
                        duration = 2000,
                        date = simpleDateFormat.parse("2022-01-25 17:08:22")!!.time
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
        return LoadResult.Page(
            assetVideos.plus(dataList).filter { keySet.add(it.diffKey) },
            null,
            nextKey
        )
    }
}