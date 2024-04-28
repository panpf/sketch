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
package pl.droidsonroids.gif

import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.source.ResourceDataSource

class GifInfoHandleHelper constructor(private val dataSource: DataSource) {

    private val gifInfoHandle: GifInfoHandle by lazy {
        val context = dataSource.request.context
        when (dataSource) {
            is ByteArrayDataSource -> {
                GifInfoHandle(dataSource.data)
            }

            is ResourceDataSource -> {
                GifInfoHandle(context.resources.openRawResourceFd(dataSource.resId))
            }

            is ContentDataSource -> {
                GifInfoHandle.openUri(context.contentResolver, dataSource.contentUri)
            }

            is AssetDataSource -> {
                GifInfoHandle(context.assets.openFd(dataSource.assetFileName))
            }

            is FileDataSource -> {
                GifInfoHandle(dataSource.getFile().toFile().path)
            }

            else -> {
                // This line of code will cause the memory to continue to be full under 6.0, so comment it out.
//                dataSource.openSourceOrNull()?.let { GifInfoHandle(it.buffer().inputStream().buffered()) }
                dataSource.getFileOrNull()?.let { GifInfoHandle(it.toFile().path) }
                    ?: throw Exception("Unsupported DataSource: ${dataSource::class.qualifiedName}")
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