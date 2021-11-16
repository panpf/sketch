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
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.text.TextUtils
import com.github.panpf.sketch.request.ImageFrom
import com.github.panpf.sketch.util.SketchUtils
import java.io.*

/**
 * 用于读取来自 [android.content.ContentProvider] 的图片，使用 [ContentResolver.openInputStream] 方法读取数据，
 * 支持 content://、file://、android.resource:// 格式的 uri
 */
class ContentDataSource(
    val context: Context,
    val contentUri: Uri
) : DataSource {

    override val imageFrom: ImageFrom = ImageFrom.LOCAL

    @get:Throws(IOException::class)
    @get:Synchronized
    override var length: Long = -1
        get() {
            if (field >= 0) {
                return field
            }
            var fileDescriptor: AssetFileDescriptor? = null
            try {
                fileDescriptor = context.contentResolver.openAssetFileDescriptor(contentUri, "r")
                field = fileDescriptor?.length ?: 0
            } finally {
                SketchUtils.close(fileDescriptor)
            }
            return field
        }
        private set

    @Throws(IOException::class)
    override fun newInputStream(): InputStream {
        return context.contentResolver.openInputStream(contentUri)
            ?: throw IOException("ContentResolver.openInputStream() return null. $contentUri")
    }

    @Throws(IOException::class)
    override fun getFile(outDir: File?, outName: String?): File? {
        if (outDir == null) {
            return null
        }
        if (!outDir.exists() && !outDir.parentFile.mkdirs()) {
            return null
        }
        val outFile: File = if (!TextUtils.isEmpty(outName)) {
            File(outDir, outName)
        } else {
            File(outDir, SketchUtils.generatorTempFileName(this, contentUri.toString()))
        }
        val inputStream = newInputStream()
        val outputStream: OutputStream = try {
            FileOutputStream(outFile)
        } catch (e: IOException) {
            SketchUtils.close(inputStream)
            throw e
        }
        val data = ByteArray(1024)
        var length: Int
        try {
            while (inputStream.read(data).also { length = it } != -1) {
                outputStream.write(data, 0, length)
            }
        } finally {
            SketchUtils.close(outputStream)
            SketchUtils.close(inputStream)
        }
        return outFile
    }
}