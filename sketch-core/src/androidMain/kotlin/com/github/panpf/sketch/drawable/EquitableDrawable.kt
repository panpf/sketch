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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.drawable

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import androidx.core.content.res.ResourcesCompat
import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.key
import com.github.panpf.sketch.util.toLogString

/**
 * Get a comparable Drawable, resId will be used as the comparison key
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.EquitableDrawableTest.testContextGetEquitableDrawable
 */
fun Context.getEquitableDrawable(@DrawableRes resId: Int): EquitableDrawable {
    val drawable = getDrawable(resId)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquitable(resId)
}


/**
 * Get a comparable Drawable, resId will be used as the comparison key
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.EquitableDrawableTest.testContextGetEquitableDrawableCompat
 */
fun Context.getEquitableDrawableCompat(@DrawableRes resId: Int): EquitableDrawable {
    val drawable = AppCompatResources.getDrawable(this, resId)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquitable(resId)
}

/**
 * Get a comparable Drawable, resId will be used as the comparison key
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.EquitableDrawableTest.testResourcesGetEquitableDrawableCompat
 */
fun Resources.getEquitableDrawableCompat(
    @DrawableRes resId: Int,
    theme: Theme?
): EquitableDrawable {
    val drawable = ResourcesCompat.getDrawable(this, resId, theme)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquitable(resId)
}

/**
 * Get a comparable Drawable, resId will be used as the comparison key
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.EquitableDrawableTest.testResourcesGetEquitableDrawableCompatForDensity
 */
fun Resources.getEquitableDrawableCompatForDensity(
    @DrawableRes resId: Int,
    density: Int,
    theme: Theme?
): EquitableDrawable {
    val drawable = ResourcesCompat.getDrawableForDensity(this, resId, density, theme)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquitable(resId)
}

/**
 * Get a comparable Drawable, resId will be used as the comparison key
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.EquitableDrawableTest.testResourcesGetEquitableDrawable
 */
@Deprecated(
    message = "Use getEquitableDrawable(Int, Int, Resources.Theme) instead.",
    replaceWith = ReplaceWith("getEquitableDrawable(resId, density, theme)")
)
fun Resources.getEquitableDrawable(@DrawableRes resId: Int): EquitableDrawable {
    val drawable = getDrawable(resId)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquitable(resId)
}

/**
 * Get a comparable Drawable, resId will be used as the comparison key
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.EquitableDrawableTest.testResourcesGetEquitableDrawableTheme
 */
fun Resources.getEquitableDrawable(
    @DrawableRes resId: Int,
    theme: Resources.Theme?
): EquitableDrawable {
    val drawable = getDrawable(resId, theme)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquitable(resId)
}

/**
 * Get a comparable Drawable, resId will be used as the comparison key
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.EquitableDrawableTest.testResourcesGetEquitableDrawableForDensity
 */
@Deprecated(
    message = "Use getEquitableDrawableForDensity(Int, Int, Resources.Theme) instead.",
    replaceWith = ReplaceWith("getEquitableDrawableForDensity(resId, density, theme)")
)
fun Resources.getEquitableDrawableForDensity(
    @DrawableRes resId: Int,
    density: Int,
): EquitableDrawable {
    val drawable = getDrawableForDensity(resId, density)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquitable(resId)
}

/**
 * Get a comparable Drawable, resId will be used as the comparison key
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.EquitableDrawableTest.testResourcesGetEquitableDrawableForDensityTheme
 */
fun Resources.getEquitableDrawableForDensity(
    @DrawableRes resId: Int,
    density: Int,
    theme: Resources.Theme?
): EquitableDrawable {
    val drawable = getDrawableForDensity(resId, density, theme)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquitable(resId)
}


/**
 * Wrap the Drawable with a EquitableDrawable, equalityKey will be used as the comparison key
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.EquitableDrawableTest.testDrawableAsEquitable
 */
fun Drawable.asEquitable(equalityKey: Any): EquitableDrawable {
    return if (this is Animatable) {
        EquitableAnimatableDrawable(drawable = this, equalityKey = equalityKey)
    } else {
        EquitableDrawable(drawable = this, equalityKey = equalityKey)
    }
}

/**
 * Wrap the Drawable with a EquitableDrawable, equalityKey will be used as the comparison key
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.EquitableDrawableTest.testColorDrawableAsEquitable
 */
fun ColorDrawable.asEquitable(): EquitableDrawable =
    ColorDrawable(color).asEquitable(color)

/**
 * Wrap the ColorDrawable with a EquitableDrawable, color will be used as the comparison key
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.EquitableDrawableTest.testColorEquitableDrawable
 */
fun ColorEquitableDrawable(@ColorInt color: Int): EquitableDrawable {
    return ColorDrawable(color).asEquitable(color)
}

/**
 * Using Resources.getDrawable() for the same drawable resource and calling it twice in a row returns Drawable equals as false.
 *
 * This will affect the equals of ImageRequest, eventually causing the AsyncImage component to be reorganized to load the image repeatedly.
 *
 * Solve this problem with wrapper
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.EquitableDrawableTest
 */
open class EquitableDrawable constructor(
    drawable: Drawable,
    val equalityKey: Any,
) : DrawableWrapperCompat(drawable), Key {

    override fun getDrawable(): Drawable {
        return super.getDrawable()!!
    }

    override val key: String = drawable.key(equalityKey)

    override fun setDrawable(drawable: Drawable?) {
        checkNotNull(drawable)
        super.setDrawable(drawable)
    }

    override fun mutate(): EquitableDrawable {
        val mutateDrawable = drawable.mutate()
        return if (mutateDrawable !== drawable) {
            EquitableDrawable(
                drawable = drawable,
                equalityKey = equalityKey,
            )
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EquitableDrawable
        if (equalityKey != other.equalityKey) return false
        return true
    }

    override fun hashCode(): Int {
        return equalityKey.hashCode()
    }

    override fun toString(): String {
        return "EquitableDrawable(drawable=${drawable.toLogString()}, equalityKey=$equalityKey)"
    }
}