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
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.DrawableFetcher
import com.github.panpf.sketch.util.RealDrawable
import com.github.panpf.sketch.util.ResDrawable

fun DrawableStateImage(drawableFetcher: DrawableFetcher): DrawableStateImage =
    DrawableStateImageImpl(drawableFetcher)

fun DrawableStateImage(drawable: Drawable): DrawableStateImage =
    DrawableStateImageImpl(RealDrawable(drawable))

fun DrawableStateImage(@DrawableRes drawableRes: Int): DrawableStateImage =
    DrawableStateImageImpl(ResDrawable(drawableRes))

/**
 * Use [Drawable] as the state [Drawable]
 */
interface DrawableStateImage : StateImage {
    val drawableFetcher: DrawableFetcher
}

private class DrawableStateImageImpl(
    override val drawableFetcher: DrawableFetcher
) : DrawableStateImage {

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