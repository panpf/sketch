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

package com.github.panpf.zoomimage.view.sketch.internal

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.transition.TransitionDrawable
import com.github.panpf.sketch.util.findLeafDrawable
import com.github.panpf.zoomimage.subsampling.SubsamplingImageGenerateResult
import com.github.panpf.zoomimage.view.sketch.SketchViewSubsamplingImageGenerator

/**
 * Filter animated images, animated images do not support subsampling
 *
 * @see com.github.panpf.zoomimage.view.sketch4.core.test.internal.AnimatableSketchViewSubsamplingImageGeneratorTest
 */
class AnimatableSketchViewSubsamplingImageGenerator :
    SketchViewSubsamplingImageGenerator {

    override suspend fun generateImage(
        sketch: Sketch,
        result: ImageResult.Success,
        drawable: Drawable
    ): SubsamplingImageGenerateResult? {
        val leafDrawable = drawable.findLeafDrawable()
        if (leafDrawable !is TransitionDrawable && leafDrawable is Animatable) {
            return SubsamplingImageGenerateResult.Error("Animated images do not support subsampling")
        }
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && this::class == other::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String {
        return "AnimatableSketchViewSubsamplingImageGenerator"
    }
}