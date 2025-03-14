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
import com.github.panpf.sketch.drawable.RealDrawableFetcher
import com.github.panpf.sketch.drawable.ResDrawableFetcher
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
    DrawableStateImage(RealDrawableFetcher(this.asEquitable(equalityKey)))

/**
 * [ColorDrawable] as [StateImage]
 *
 * @see com.github.panpf.sketch.core.android.test.state.DrawableStateImageTest.testAsStateImage
 */
fun ColorDrawable.asStateImage(): DrawableStateImage =
    DrawableStateImage(RealDrawableFetcher(this.asEquitable()))

/**
 * Create a [DrawableStateImage] with [EquitableDrawable]
 *
 * @see com.github.panpf.sketch.core.android.test.state.DrawableStateImageTest.testDrawableStateImage
 */
fun DrawableStateImage(drawable: EquitableDrawable): DrawableStateImage =
    DrawableStateImage(RealDrawableFetcher(drawable))

/**
 * Create a [DrawableStateImage] with resource drawable
 *
 * @see com.github.panpf.sketch.core.android.test.state.DrawableStateImageTest.testDrawableStateImage
 */
fun DrawableStateImage(@DrawableRes resId: Int): DrawableStateImage =
    DrawableStateImage(ResDrawableFetcher(resId))

/**
 * Use [Drawable] as the state [Drawable]
 *
 * @see com.github.panpf.sketch.core.android.test.state.DrawableStateImageTest.testDrawableStateImage
 */
data class DrawableStateImage(
    val drawableFetcher: DrawableFetcher
) : StateImage {

    override val key: String = "Drawable(${drawableFetcher.key})"

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

    override fun toString(): String = "DrawableStateImage(drawable=$drawableFetcher)"
}