package com.github.panpf.sketch.sample.ui.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.res.ResourcesCompat


internal fun Resources.getDrawableCompat(@DrawableRes id: Int, theme: Theme? = null): Drawable {
    return checkNotNull(ResourcesCompat.getDrawable(this, id, theme)) {
        "Can't find drawable by id=$id"
    }
}

internal fun Context.getDrawableCompat(@DrawableRes resId: Int): Drawable {
    val drawable = AppCompatResources.getDrawable(this, resId)
    return checkNotNull(drawable) { "Invalid resource ID: $resId" }
}

internal fun Context.isDarkTheme(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}

fun Context.getWindowBackgroundColor(): Int {
    val array = theme.obtainStyledAttributes(
        intArrayOf(android.R.attr.windowBackground)
    )
    val windowBackground = array.getColor(0, 0xFF00FF)
    array.recycle()
    return windowBackground
}

fun Context.isNightMode(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}

fun toScaleType(contentScale: ContentScale, alignment: Alignment): ScaleType {
    return when (contentScale) {
        ContentScale.FillBounds -> ScaleType.FIT_XY
        ContentScale.FillWidth -> ScaleType.FIT_XY
        ContentScale.FillHeight -> ScaleType.FIT_XY
        ContentScale.Fit -> {
            when {
                alignment.isCenter || alignment.isVerticalCenter || alignment.isHorizontalCenter -> ScaleType.FIT_CENTER
                alignment.isStart || alignment.isTop -> ScaleType.FIT_START
                alignment.isEnd || alignment.isBottom -> ScaleType.FIT_END
                else -> ScaleType.FIT_CENTER
            }
        }

        ContentScale.Crop -> ScaleType.CENTER_CROP
        ContentScale.Inside -> ScaleType.CENTER_INSIDE
        ContentScale.None -> ScaleType.MATRIX
        else -> ScaleType.FIT_CENTER
    }
}