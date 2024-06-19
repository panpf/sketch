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
package com.github.panpf.sketch.state

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.drawable.DrawableEqualizer
import com.github.panpf.sketch.drawable.DrawableFetcher
import com.github.panpf.sketch.drawable.RealEqualityDrawable
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.sketch.request.ImageRequest

fun DrawableEqualizer.asStateImage(): DrawableStateImage = DrawableStateImage(this)

fun DrawableStateImage(drawable: DrawableEqualizer): DrawableStateImage =
    DrawableStateImage(RealEqualityDrawable(drawable))

fun DrawableStateImage(@DrawableRes resId: Int): DrawableStateImage =
    DrawableStateImage(ResDrawable(resId))

/**
 * Use [Drawable] as the state [Drawable]
 */
class DrawableStateImage(
    val drawableFetcher: DrawableFetcher
) : StateImage {

    override val key: String = "DrawableStateImage(${drawableFetcher.key})"

    override fun getImage(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Image? {
        return try {
            drawableFetcher.getDrawable(request.context)
        } catch (e: Throwable) {
            sketch.logger.w("DrawableStateImage. getDrawable error. ${e.message}")
            e.printStackTrace()
            null
        }?.asSketchImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DrawableStateImage) return false
        if (drawableFetcher != other.drawableFetcher) return false
        return true
    }

    override fun hashCode(): Int {
        return drawableFetcher.hashCode()
    }

    override fun toString(): String {
        return "DrawableStateImage($drawableFetcher)"
    }
}