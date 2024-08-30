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
import com.github.panpf.sketch.util.Key
import okio.IOException
import okio.Path
import okio.Source

/**
 * Provides access to the image data.
 *
 * @see com.github.panpf.sketch.core.common.test.source.DataSourceTest
 */
interface DataSource : Key {

    /**
     * Where the data comes from
     */
    val dataFrom: DataFrom

    /**
     * Open the Source of the DataSource, throws an exception if an error occurs
     */
    @Throws(IOException::class)
    fun openSource(): Source

    /**
     * Get the file of the DataSource, throws an exception if an error occurs
     */
    @Throws(IOException::class)
    fun getFile(sketch: Sketch): Path
}

/**
 * Open the Source of the DataSource, returns null if an exception occurs
 *
 * @see com.github.panpf.sketch.core.common.test.source.DataSourceTest.testOpenSourceOrNull
 */
@Throws(IOException::class)
fun DataSource.openSourceOrNull(): Source? = runCatching { openSource() }.getOrNull()

/**
 * Get the file of the DataSource, returns null if an exception occurs
 *
 * @see com.github.panpf.sketch.core.common.test.source.DataSourceTest.testGetFileOrNull
 */
@Throws(IOException::class)
fun DataSource.getFileOrNull(sketch: Sketch): Path? = runCatching { getFile(sketch) }.getOrNull()

/**
 * Get the cache file of the DataSource
 *
 * @see com.github.panpf.sketch.core.common.test.source.DataSourceTest.testCacheFile
 */
@Throws(IOException::class)
expect fun DataSource.cacheFile(sketch: Sketch): Path