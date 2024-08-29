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

package com.github.panpf.sketch.source

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.newKotlinResourceUri
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.util.defaultFileSystem
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.Source
import platform.Foundation.NSBundle

class KotlinResourceDataSource constructor(
    val resourcePath: String,
    val fileSystem: FileSystem = defaultFileSystem(),
) : DataSource {

    override val key: String by lazy { newKotlinResourceUri(resourcePath) }

    override val dataFrom: DataFrom = LOCAL

    override fun openSourceOrNull(): Source {
        val appResourcePath = NSBundle.mainBundle.resourcePath!!.toPath()
        val filePath = appResourcePath.resolve("compose-resources").resolve(resourcePath)
        return fileSystem.source(filePath)
    }

    override fun getFileOrNull(sketch: Sketch): Path {
        val resourcePath = NSBundle.mainBundle.resourcePath!!.toPath()
        return resourcePath.resolve("compose-resources").resolve(this.resourcePath)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as KotlinResourceDataSource
        return resourcePath == other.resourcePath
    }

    override fun hashCode(): Int {
        return resourcePath.hashCode()
    }

    override fun toString(): String = "KotlinResourceDataSource('$resourcePath')"
}