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
import android.content.Context
import android.net.Uri
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.util.MD5Utils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * 用于读取来自 [android.content.ContentProvider] 的图片，使用 [ContentResolver.openInputStream] 方法读取数据，
 * 支持 content://、file://、android.resource:// 格式的 uri
 */
class ContentDataSource(
    val context: Context,
    val contentUri: Uri
) : DataSource {

    override val from: DataFrom = DataFrom.LOCAL

    @get:Throws(IOException::class)
    @get:Synchronized
    override val length: Long by lazy {
        context.contentResolver.openAssetFileDescriptor(contentUri, "r")?.use {
            it.length
        } ?: 0L
    }

    @Throws(IOException::class)
    override fun newInputStream(): InputStream {
        return context.contentResolver.openInputStream(contentUri)
            ?: throw IOException("Invalid content uri: $contentUri")
    }

    @Throws(IOException::class)
    override fun getFile(outDir: File?, outName: String?): File? {
        if (outDir == null || (!outDir.exists() && !outDir.parentFile.mkdirs())) {
            return null
        }

        val outFile = File(outDir, outName ?: MD5Utils.md5(contentUri.toString()))
        newInputStream().use { inputStream ->
            FileOutputStream(outFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return outFile
    }

    override fun toString(): String {
        return "ContentDataSource(from=$from, contentUri=$contentUri)"
    }
}