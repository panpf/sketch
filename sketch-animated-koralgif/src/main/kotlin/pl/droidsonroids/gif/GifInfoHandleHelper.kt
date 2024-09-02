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

package pl.droidsonroids.gif

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.source.ResourceDataSource
import com.github.panpf.sketch.source.getFileOrNull

/**
 * GifInfoHandle Helper
 *
 * @see com.github.panpf.sketch.animated.koralgif.test.GifInfoHandlerHelperTest
 */
class GifInfoHandleHelper constructor(val sketch: Sketch, val dataSource: DataSource) {

    private val gifInfoHandle: GifInfoHandle by lazy {
        when (dataSource) {
            is ByteArrayDataSource -> {
                GifInfoHandle(dataSource.data)
            }

            is ResourceDataSource -> {
                GifInfoHandle(sketch.context.resources.openRawResourceFd(dataSource.resId))
            }

            is ContentDataSource -> {
                GifInfoHandle.openUri(sketch.context.contentResolver, dataSource.contentUri)
            }

            is AssetDataSource -> {
                GifInfoHandle(sketch.context.assets.openFd(dataSource.fileName))
            }

            is FileDataSource -> {
                GifInfoHandle(dataSource.path.toString())
            }

            else -> {
                // This line of code will cause the memory to continue to be full under 6.0, so comment it out.
//                dataSource.openSourceOrNull()?.let { GifInfoHandle(it.buffer().inputStream().buffered()) }
                dataSource.getFileOrNull(sketch)?.let { GifInfoHandle(it.toFile().path) }
                    ?: throw Exception("Unsupported DataSource: ${dataSource::class}")
            }
        }
    }

    val width: Int
        get() = gifInfoHandle.width

    val height: Int
        get() = gifInfoHandle.width

    val duration: Int
        get() = gifInfoHandle.duration

    val numberOfFrames: Int
        get() = gifInfoHandle.numberOfFrames

    fun setOptions(options: GifOptions) {
        gifInfoHandle.setOptions(options.inSampleSize, options.inIsOpaque)
    }

    fun createGifDrawable(): GifDrawable = GifDrawable(gifInfoHandle, null, null, true)
}