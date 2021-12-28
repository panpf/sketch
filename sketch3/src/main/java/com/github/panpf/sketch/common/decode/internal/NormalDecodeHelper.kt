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
package com.github.panpf.sketch.common.decode.internal

import android.graphics.BitmapFactory
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.LoadableRequest
import com.github.panpf.sketch.common.datasource.DataSource
import com.github.panpf.sketch.common.decode.DecodeResult
import com.github.panpf.sketch.load.ImageInfo

class NormalDecodeHelper {

    companion object {
        private const val NAME = "NormalDecodeHelper"
    }

    private val resizeCalculator = ResizeCalculator()
    private val sizeCalculator = ImageSizeCalculator()

    fun decode(
        sketch: Sketch,
        request: LoadableRequest,
        dataSource: DataSource,
        imageInfo: ImageInfo,
        decodeOptions: BitmapFactory.Options,
    ): DecodeResult {
        // todo maxSize, bitmapConfig, inPreferQualityOverSpeed, resize,
        TODO()
    }
}