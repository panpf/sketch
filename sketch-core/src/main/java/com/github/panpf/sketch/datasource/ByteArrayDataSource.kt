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

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.internal.ImageRequest
import java.io.ByteArrayInputStream
import java.io.FileDescriptor
import java.io.IOException
import java.io.InputStream

class ByteArrayDataSource constructor(
    override val sketch: Sketch,
    override val request: ImageRequest,
    override val from: DataFrom,
    val data: ByteArray,
) : DataSource {

    @Throws(IOException::class)
    override fun length(): Long = data.size.toLong()

    @Throws(IOException::class)
    override fun newInputStream(): InputStream = ByteArrayInputStream(data)

    override fun newFileDescriptor(): FileDescriptor? = null

    override fun toString(): String =
        "ByteArrayDataSource(from=$from, length=${data.size.toLong()})"
}