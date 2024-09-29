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

package com.github.panpf.sketch.state

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.drawable.DrawableFetcher
import com.github.panpf.sketch.drawable.EquitableDrawable
import com.github.panpf.sketch.drawable.RealEquitableDrawable
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.request.ImageRequest

/**
 * [EquitableDrawable] as [StateImage]
 *
 * @see com.github.panpf.sketch.core.android.test.state.DrawableStateImageTest.testAsStateImage
 */
fun EquitableDrawable.asStateImage(): DrawableStateImage = DrawableStateImage(this)

/**
 * [Drawable] as [StateImage]
 *
 * @see com.github.panpf.sketch.core.android.test.state.DrawableStateImageTest.testAsStateImage
 */
fun Drawable.asStateImage(equalityKey: Any): DrawableStateImage =
    DrawableStateImage(RealEquitableDrawable(this.asEquitable(equalityKey)))

/**
 * [ColorDrawable] as [StateImage]
 *
 * @see com.github.panpf.sketch.core.android.test.state.DrawableStateImageTest.testAsStateImage
 */
fun ColorDrawable.asStateImage(): DrawableStateImage =
    DrawableStateImage(RealEquitableDrawable(this.asEquitable()))

/**
 * Create a [DrawableStateImage] with [EquitableDrawable]
 *
 * @see com.github.panpf.sketch.core.android.test.state.DrawableStateImageTest.testDrawableStateImage
 */
fun DrawableStateImage(drawable: EquitableDrawable): DrawableStateImage =
    DrawableStateImage(RealEquitableDrawable(drawable))

/**
 * Create a [DrawableStateImage] with resource drawable
 *
 * @see com.github.panpf.sketch.core.android.test.state.DrawableStateImageTest.testDrawableStateImage
 */
fun DrawableStateImage(@DrawableRes resId: Int): DrawableStateImage =
    DrawableStateImage(ResDrawable(resId))

/**
 * Use [Drawable] as the state [Drawable]
 *
 * @see com.github.panpf.sketch.core.android.test.state.DrawableStateImageTest.testDrawableStateImage
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
        }?.asImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as DrawableStateImage
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