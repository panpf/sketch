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

import com.github.panpf.sketch.request.ImageFrom
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

/**
 * 用于读取来自本地的图片
 */
class FileDataSource(val file: File) : DataSource {

    override val imageFrom: ImageFrom
        get() = ImageFrom.LOCAL

    @get:Throws(IOException::class)
    @get:Synchronized
    override var length: Long = -1
        get() {
            if (field >= 0) {
                return field
            }
            field = file.length()
            return field
        }
        private set

    @Throws(IOException::class)
    override fun newInputStream(): InputStream {
        return FileInputStream(file)
    }

    override fun getFile(outDir: File?, outName: String?): File {
        return file
    }
}