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

import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.getCacheFileFromStreamDataSource
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Provides access to image data in android resources
 */
class ResourceDataSource constructor(
    override val sketch: Sketch,
    override val request: ImageRequest,
    val packageName: String,
    val resources: Resources,
    @RawRes @DrawableRes val drawableId: Int
) : BasedFileDataSource {

    override val dataFrom: DataFrom
        get() = DataFrom.LOCAL

    @WorkerThread
    @Throws(IOException::class)
    override fun newInputStream(): InputStream =
        resources.openRawResource(drawableId)

    @WorkerThread
    @Throws(IOException::class)
    override fun getFile(): File = getCacheFileFromStreamDataSource(sketch, request, this)

    override fun toString(): String = "ResourceDataSource($drawableId)"
}