package com.github.panpf.sketch.util

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import com.github.panpf.sketch.drawable.internal.toLogString
import java.lang.Deprecated

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

@Deprecated
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

@Deprecated
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
        if (other !is DrawableEqualizer) return false
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