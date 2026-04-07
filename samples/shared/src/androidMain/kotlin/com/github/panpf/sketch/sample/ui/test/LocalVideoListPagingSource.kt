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

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.sample.ui.model.VideoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class LocalVideoListPagingSource(private val context: Context) :
    PagingSource<Int, VideoInfo>() {

    override fun getRefreshKey(state: PagingState<Int, VideoInfo>): Int = 0

    private val assetVideos = ComposeResImageFiles.videos.map {
        val simpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        VideoInfo(
            title = "sample",
            uri = it.uri,
            mimeType = "video/mp4",
            size = 157092,
            duration = 2000,
            date = simpleDateFormat.parse("2022-01-25 17:08:22")!!.time
        )
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoInfo> {
        val pageStart = params.key ?: 0
        val pageSize = params.loadSize

        val builtinVideos = loadFromBuiltin(pageStart, pageSize)
        val galleryVideos = if (builtinVideos.size < pageSize) {
            val galleryPageStart = if (pageStart < assetVideos.size)
                0 else pageStart - assetVideos.size
            val galleryPageSize = pageSize - builtinVideos.size
            loadFromGallery(
                pageStart = galleryPageStart,
                pageSize = galleryPageSize
            )
        } else {
            emptyList()
        }
        val dataList = builtinVideos + galleryVideos
        val nextKey = if (dataList.size >= pageSize) pageStart + pageSize else null
        return LoadResult.Page(data = dataList, prevKey = null, nextKey = nextKey)
    }

    private fun loadFromBuiltin(pageStart: Int, pageSize: Int): List<VideoInfo> {
        return if (pageStart < assetVideos.size) {
            assetVideos.subList(
                fromIndex = pageStart,
                toIndex = minOf(pageStart + pageSize, assetVideos.size)
            )
        } else {
            emptyList()
        }
    }

    private suspend fun loadFromGallery(pageStart: Int, pageSize: Int): List<VideoInfo> =
        withContext(Dispatchers.IO) {
            val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.MIME_TYPE
            )
            val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val queryArgs = Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_OFFSET, pageStart)
                    putInt(ContentResolver.QUERY_ARG_LIMIT, pageSize)
                    putStringArray(
                        ContentResolver.QUERY_ARG_SORT_COLUMNS,
                        arrayOf(MediaStore.Files.FileColumns.DATE_ADDED)
                    )
                    putInt(
                        ContentResolver.QUERY_ARG_SORT_DIRECTION,
                        ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                    )
                }

                context.contentResolver.query(
                    /* uri = */ contentUri,
                    /* projection = */ projection,
                    /* queryArgs = */ queryArgs,
                    /* cancellationSignal = */ null
                )
            } else {
                context.contentResolver.query(
                    /* uri = */ contentUri,
                    /* projection = */ projection,
                    /* selection = */ null,
                    /* selectionArgs = */ null,
                    /* sortOrder = */
                    MediaStore.Video.Media.DATE_ADDED + " DESC" + " limit " + pageStart + "," + pageSize
                )
            } ?: return@withContext emptyList<VideoInfo>()
            if (cursor.count == 0) {
                return@withContext emptyList()
            }

            cursor.use {
                mutableListOf<VideoInfo>().apply {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val videoUri = ContentUris.withAppendedId(contentUri, id)
                        add(
                            VideoInfo(
                                title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)),
                                uri = videoUri.toString(),
                                mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)),
                                size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)),
                                duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                                    .toLong(),
                                date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))
                            )
                        )
                    }
                }
            }
        }
}