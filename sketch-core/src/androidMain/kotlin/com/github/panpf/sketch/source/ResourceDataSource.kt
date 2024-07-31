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
package com.github.panpf.sketch.source

import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataFrom.LOCAL
import okio.Path
import okio.Source
import okio.source
import java.io.IOException

/**
 * Provides access to image data in android resources
 */
class ResourceDataSource constructor(
    override val sketch: Sketch,
    override val request: ImageRequest,
    val packageName: String,
    val resources: Resources,
    @RawRes @DrawableRes val resId: Int
) : DataSource {

    override val dataFrom: DataFrom
        get() = LOCAL

    @WorkerThread
    @Throws(IOException::class)
    override fun openSourceOrNull(): Source =
        resources.openRawResource(resId).source()

    @WorkerThread
    @Throws(IOException::class)
    override fun getFileOrNull(): Path? = getDataSourceCacheFile(sketch, request, this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ResourceDataSource
        if (sketch != other.sketch) return false
        if (request != other.request) return false
        if (packageName != other.packageName) return false
        if (resources != other.resources) return false
        if (resId != other.resId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sketch.hashCode()
        result = 31 * result + request.hashCode()
        result = 31 * result + packageName.hashCode()
        result = 31 * result + resources.hashCode()
        result = 31 * result + resId
        return result
    }

    override fun toString(): String = "ResourceDataSource(packageName='$packageName', resId=$resId)"
}