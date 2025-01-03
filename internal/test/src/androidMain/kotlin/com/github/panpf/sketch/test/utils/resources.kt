package com.github.panpf.sketch.test.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources

fun Context.getDrawableCompat(@DrawableRes resId: Int): Drawable {
    val drawable = AppCompatResources.getDrawable(this, resId)
    return checkNotNull(drawable) { "Invalid resource ID: $resId" }
}

fun createCustomDensityResources(context: Context, newDensity: Int): Resources {
    val systemResources: Resources = context.resources

    val newDisplayMetrics = DisplayMetrics()
    newDisplayMetrics.setTo(systemResources.displayMetrics)
    newDisplayMetrics.density = newDensity / 160f
    newDisplayMetrics.densityDpi = newDensity

    val newConfig = Configuration()
    newConfig.setTo(systemResources.configuration)
    newConfig.densityDpi = newDisplayMetrics.densityDpi

    return Resources(systemResources.assets, newDisplayMetrics, newConfig)
}