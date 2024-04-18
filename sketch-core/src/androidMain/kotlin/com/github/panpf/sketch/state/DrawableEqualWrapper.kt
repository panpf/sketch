package com.github.panpf.sketch.state

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import com.github.panpf.sketch.drawable.internal.toLogString
import java.lang.Deprecated


fun Context.getEqualWrapperDrawable(@DrawableRes resId: Int): DrawableEqualWrapper {
    val drawable = getDrawable(resId)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEqualWrapper(resId)
}


fun Context.getEqualWrapperDrawableCompat(@DrawableRes resId: Int): DrawableEqualWrapper {
    val drawable = AppCompatResources.getDrawable(this, resId)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEqualWrapper(resId)
}

fun AppCompatResources.getEqualWrapperDrawable(
    context: Context,
    @DrawableRes resId: Int
): DrawableEqualWrapper {
    val drawable = AppCompatResources.getDrawable(context, resId)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEqualWrapper(resId)
}

fun ResourcesCompat.getEqualWrapperDrawable(
    resources: Resources,
    @DrawableRes resId: Int,
    theme: Theme?
): DrawableEqualWrapper {
    val drawable = ResourcesCompat.getDrawable(resources, resId, theme)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEqualWrapper(resId)
}

fun ResourcesCompat.getEqualWrapperDrawableForDensity(
    resources: Resources,
    @DrawableRes resId: Int,
    density: Int,
    theme: Theme?
): DrawableEqualWrapper {
    val drawable = ResourcesCompat.getDrawableForDensity(resources, resId, density, theme)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEqualWrapper(resId)
}

@Deprecated
fun Resources.getEqualWrapperDrawable(@DrawableRes resId: Int): DrawableEqualWrapper {
    val drawable = getDrawable(resId)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEqualWrapper(resId)
}

fun Resources.getEqualWrapperDrawable(
    @DrawableRes resId: Int,
    theme: Resources.Theme?
): DrawableEqualWrapper {
    val drawable = getDrawable(resId, theme)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEqualWrapper(resId)
}

@Deprecated
fun Resources.getEqualWrapperDrawableForDensity(
    @DrawableRes resId: Int,
    density: Int,
): DrawableEqualWrapper {
    val drawable = getDrawableForDensity(resId, density)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEqualWrapper(resId)
}

fun Resources.getEqualWrapperDrawableForDensity(
    @DrawableRes resId: Int,
    density: Int,
    theme: Resources.Theme?
): DrawableEqualWrapper {
    val drawable = getDrawableForDensity(resId, density, theme)
    checkNotNull(drawable) { "Invalid resource ID: $resId" }
    return drawable.asEqualWrapper(resId)
}


fun Drawable.asEqualWrapper(equalKey: Any): DrawableEqualWrapper =
    DrawableEqualWrapper(drawable = this, equalKey = equalKey)

/**
 * Using Resources.getDrawable() for the same drawable resource and calling it twice in a row returns Drawable equals as false.
 *
 * This will affect the equals of ImageRequest, eventually causing the AsyncImage component to be reorganized to load the image repeatedly.
 *
 * Solve this problem with wrapper
 */
class DrawableEqualWrapper(val drawable: Drawable, val equalKey: Any) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DrawableEqualWrapper) return false
        if (equalKey != other.equalKey) return false
        return true
    }

    override fun hashCode(): Int {
        return equalKey.hashCode()
    }

    override fun toString(): String {
        return "DrawableEqualWrapper(drawable=${drawable.toLogString()}, equalKey=$equalKey)"
    }
}