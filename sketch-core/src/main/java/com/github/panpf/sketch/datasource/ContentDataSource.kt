/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.datasource

import android.content.ContentResolver
import android.net.Uri
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.internal.ImageRequest
import java.io.File
import java.io.FileDescriptor
import java.io.IOException
import java.io.InputStream

/**
 * 用于读取来自 [android.content.ContentProvider] 的图片，使用 [ContentResolver.openInputStream] 方法读取数据，
 * 支持 content://、file://、android.resource:// 格式的 uri
 */
class ContentDataSource constructor(
    override val sketch: Sketch,
    override val request: ImageRequest,
    val contentUri: Uri
) : DataSource {

    override val from: DataFrom = DataFrom.LOCAL

    private var _length = -1L

    @Throws(IOException::class)
    override fun length(): Long =
        _length.takeIf { it != -1L }
            ?: (context.contentResolver.openFileDescriptor(contentUri, "r")
                ?.use {
                    it.statSize
                } ?: throw IOException("Invalid content uri: $contentUri")).apply {
                this@ContentDataSource._length = this
            }

    override fun newFileDescriptor(): FileDescriptor =
        context.contentResolver.openFileDescriptor(contentUri, "r")?.fileDescriptor
            ?: throw IOException("Invalid content uri: $contentUri")

    @Throws(IOException::class)
    override fun newInputStream(): InputStream =
        context.contentResolver.openInputStream(contentUri)
            ?: throw IOException("Invalid content uri: $contentUri")

    @Throws(IOException::class)
    override suspend fun file(): File =
        if (contentUri.scheme.equals("file", ignoreCase = true)) {
            File(contentUri.toString().substring("file://".length))
        } else {
            super.file()
        }

    override fun toString(): String = "ContentDataSource(from=$from, contentUri=$contentUri)"
}