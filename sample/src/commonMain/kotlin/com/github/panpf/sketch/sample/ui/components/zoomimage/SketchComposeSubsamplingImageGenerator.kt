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

package com.github.panpf.zoomimage.compose.sketch

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.zoomimage.subsampling.SubsamplingImageGenerateResult

interface SketchComposeSubsamplingImageGenerator {

    suspend fun generateImage(
        sketch: Sketch,
        result: ImageResult.Success,
        painter: Painter
    ): SubsamplingImageGenerateResult?

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun toString(): String
}