/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.getCacheFileFromStreamDataSource
import okio.Path
import okio.Source
import okio.source
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Provides access to image data in asset resources
 */
class AssetDataSource constructor(
    override val sketch: Sketch,
    override val request: ImageRequest,
    val assetFileName: String
) : BasedFileDataSource {

    override val dataFrom: DataFrom
        get() = LOCAL

    @WorkerThread
    @Throws(IOException::class)
    override fun openInputStream(): InputStream = request.context.assets.open(assetFileName)
//    override fun openInputStream(): Source = request.context.assets.open(assetFileName).source()

    @WorkerThread
    @Throws(IOException::class)
    override fun getFile(): File = getCacheFileFromStreamDataSource(sketch, request, this)
//    override fun getFile(): Path = getCacheFileFromStreamDataSource(sketch, request, this)

    override fun toString(): String =
        "AssetDataSource('$assetFileName')"
}