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

package com.github.panpf.sketch.drawable

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import com.github.panpf.sketch.util.Equalizer
import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.key

fun Context.getEqualityDrawable(@DrawableRes resId: Int): DrawableEqualizer {
    val drawable = getDrawable(resId)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquality(resId)
}


fun Context.getEqualityDrawableCompat(@DrawableRes resId: Int): DrawableEqualizer {
    val drawable = AppCompatResources.getDrawable(this, resId)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquality(resId)
}

fun Resources.getEqualityDrawableCompat(
    @DrawableRes resId: Int,
    theme: Theme?
): DrawableEqualizer {
    val drawable = ResourcesCompat.getDrawable(this, resId, theme)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquality(resId)
}

fun Resources.getEqualityDrawableCompatForDensity(
    @DrawableRes resId: Int,
    density: Int,
    theme: Theme?
): DrawableEqualizer {
    val drawable = ResourcesCompat.getDrawableForDensity(this, resId, density, theme)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquality(resId)
}

@Deprecated(
    message = "Use getEqualityDrawable(Int, Int, Resources.Theme) instead.",
    replaceWith = ReplaceWith("getEqualityDrawable(resId, density, theme)")
)
fun Resources.getEqualityDrawable(@DrawableRes resId: Int): DrawableEqualizer {
    val drawable = getDrawable(resId)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquality(resId)
}

fun Resources.getEqualityDrawable(
    @DrawableRes resId: Int,
    theme: Resources.Theme?
): DrawableEqualizer {
    val drawable = getDrawable(resId, theme)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquality(resId)
}

@Deprecated(
    message = "Use getEqualityDrawableForDensity(Int, Int, Resources.Theme) instead.",
    replaceWith = ReplaceWith("getEqualityDrawableForDensity(resId, density, theme)")
)
fun Resources.getEqualityDrawableForDensity(
    @DrawableRes resId: Int,
    density: Int,
): DrawableEqualizer {
    val drawable = getDrawableForDensity(resId, density)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquality(resId)
}

fun Resources.getEqualityDrawableForDensity(
    @DrawableRes resId: Int,
    density: Int,
    theme: Resources.Theme?
): DrawableEqualizer {
    val drawable = getDrawableForDensity(resId, density, theme)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEquality(resId)
}


fun Drawable.asEquality(equalKey: Any): DrawableEqualizer =
    DrawableEqualizer(wrapped = this, equalityKey = equalKey)

fun ColorDrawableEqualizer(@ColorInt color: Int): DrawableEqualizer {
    return ColorDrawable(color).asEquality(color)
}

/**
 * Using Resources.getDrawable() for the same drawable resource and calling it twice in a row returns Drawable equals as false.
 *
 * This will affect the equals of ImageRequest, eventually causing the AsyncImage component to be reorganized to load the image repeatedly.
 *
 * Solve this problem with wrapper
 */
class DrawableEqualizer(
    override val wrapped: Drawable,
    override val equalityKey: Any,
    private val equalityKeyString: String = key(equalityKey)
) : Equalizer<Drawable>, Key {

    override val key: String = equalityKeyString

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as DrawableEqualizer
        if (equalityKey != other.equalityKey) return false
        return true
    }

    override fun hashCode(): Int {
        return equalityKey.hashCode()
    }

    override fun toString(): String {
        return "DrawableEqualizer(wrapped=${wrapped.toLogString()}, equalityKey=$equalityKeyString)"
    }
}