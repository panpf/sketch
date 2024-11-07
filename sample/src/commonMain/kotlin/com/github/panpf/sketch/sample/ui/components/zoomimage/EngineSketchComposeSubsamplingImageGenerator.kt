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

package com.github.panpf.zoomimage.compose.sketch.internal

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.zoomimage.compose.sketch.SketchComposeSubsamplingImageGenerator
import com.github.panpf.zoomimage.sketch.SketchImageSource
import com.github.panpf.zoomimage.subsampling.ImageInfo
import com.github.panpf.zoomimage.subsampling.SubsamplingImage
import com.github.panpf.zoomimage.subsampling.SubsamplingImageGenerateResult

/**
 * [SketchComposeSubsamplingImageGenerator] implementation that uses the Sketch library to generate images
 *
 * @see com.github.panpf.zoomimage.compose.sketch4.core.test.internal.EngineSketchComposeSubsamplingImageGeneratorTest
 */
class EngineSketchComposeSubsamplingImageGenerator : SketchComposeSubsamplingImageGenerator {

    override suspend fun generateImage(
        sketch: Sketch,
        result: ImageResult.Success,
        painter: Painter
    ): SubsamplingImageGenerateResult {
        val imageSource = SketchImageSource.Factory(sketch, result.request.uri.toString())
        val imageInfo = ImageInfo(
            width = result.imageInfo.width,
            height = result.imageInfo.height,
            mimeType = result.imageInfo.mimeType
        )
        return SubsamplingImageGenerateResult.Success(SubsamplingImage(imageSource, imageInfo))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && this::class == other::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String {
        return "EngineSketchComposeSubsamplingImageGenerator"
    }
}