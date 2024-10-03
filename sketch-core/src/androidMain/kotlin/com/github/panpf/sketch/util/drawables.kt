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

package com.github.panpf.sketch.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorSpace
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ColorStateListDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Xml
import androidx.annotation.DrawableRes
import androidx.annotation.XmlRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.github.panpf.sketch.AndroidBitmap
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.drawable.SketchDrawable
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

/**
 * Get a [Drawable] from the specified resource ID.
 *
 * @see com.github.panpf.sketch.core.android.test.util.DrawablesTest.testGetDrawableCompat
 */
internal fun Context.getDrawableCompat(@DrawableRes resId: Int): Drawable {
    val drawable = AppCompatResources.getDrawable(this, resId)
    return checkNotNull(drawable) { "Invalid resource ID: $resId" }
}

/**
 * Get a [Drawable] from the specified resource ID.
 *
 * @see com.github.panpf.sketch.core.android.test.util.DrawablesTest.testGetDrawableCompat
 */
internal fun Resources.getDrawableCompat(
    @DrawableRes resId: Int, theme: Resources.Theme?
): Drawable {
    val drawable = ResourcesCompat.getDrawable(this, resId, theme)
    return checkNotNull(drawable) { "Invalid resource ID: $resId" }
}

/**
 * Supports inflating XML [Drawable]s from other package's resources.
 *
 * Prefer using [Context.getDrawableCompat] for resources that are part of the current package.
 *
 * @see com.github.panpf.sketch.core.android.test.util.DrawablesTest.testGetXmlDrawableCompat
 */
@SuppressLint("ResourceType")
internal fun Context.getXmlDrawableCompat(resources: Resources, @XmlRes resId: Int): Drawable {
    // Modified from androidx.appcompat.widget.ResourceManagerInternal.
    return if (VERSION.SDK_INT >= 24) {
        resources.getDrawableCompat(resId, theme)
    } else {
        // Find the XML's start tag.
        val parser = resources.getXml(resId)
        var type = parser.next()
        while (type != XmlPullParser.START_TAG && type != XmlPullParser.END_DOCUMENT) {
            type = parser.next()
        }
        if (type != XmlPullParser.START_TAG) {
            throw XmlPullParserException("No start tag found.")
        }
        when (parser.name) {
            "vector" -> VectorDrawableCompat
                .createFromXmlInner(resources, parser, Xml.asAttributeSet(parser), theme)

            "animated-vector" -> AnimatedVectorDrawableCompat
                .createFromXmlInner(this, resources, parser, Xml.asAttributeSet(parser), theme)

            else -> resources.getDrawableCompat(resId, theme)
        }
    }
}

/**
 * Drawable into new Bitmap. Each time a new bitmap is drawn
 *
 * @see com.github.panpf.sketch.core.android.test.util.DrawablesTest.testToBitmap
 */
internal fun Drawable.toBitmap(
    colorType: ColorType? = null,
    colorSpace: ColorSpace? = null,
    targetSize: Size? = null,
): Bitmap {
    val (oldLeft, oldTop, oldRight, oldBottom) = bounds
    val targetWidth = targetSize?.width ?: intrinsicWidth
    val targetHeight = targetSize?.height ?: intrinsicHeight
    setBounds(0, 0, targetWidth, targetHeight)

    val bitmap: Bitmap = if (VERSION.SDK_INT >= VERSION_CODES.O && colorSpace != null) {
        AndroidBitmap(
            width = targetWidth,
            height = targetHeight,
            config = colorType.safeToSoftware(),
            hasAlpha = true,
            colorSpace = colorSpace
        )
    } else {
        AndroidBitmap(
            width = targetWidth,
            height = targetHeight,
            config = colorType.safeToSoftware()
        )
    }
    val canvas = Canvas(bitmap)
    draw(canvas)

    setBounds(oldLeft, oldTop, oldRight, oldBottom) // restore bounds
    return bitmap
}

/**
 * Get the string applicable to the log
 *
 * @see com.github.panpf.sketch.core.android.test.util.DrawablesTest.testToLogString
 */
fun Drawable.toLogString(): String = when {
    this is SketchDrawable -> toString()
    this is BitmapDrawable -> "BitmapDrawable(${bitmap.toLogString()})"
    this is RoundedBitmapDrawable -> "RoundedBitmapDrawable(drawable=${bitmap?.toLogString()})"
    VERSION.SDK_INT >= VERSION_CODES.P && this is AnimatedImageDrawable -> "AnimatedImageDrawable(${toSizeString()})"
    this is AnimatedVectorDrawableCompat -> "AnimatedVectorDrawableCompat(${toSizeString()})"
    VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && this is AnimatedVectorDrawable -> "AnimatedVectorDrawable(${toSizeString()})"
    this is TransitionDrawable -> "TransitionDrawable(${toSizeString()})"
    this is ColorDrawable -> "ColorDrawable(${color})"
    VERSION.SDK_INT >= VERSION_CODES.Q && this is ColorStateListDrawable -> "ColorStateListDrawable(${toSizeString()})"
    this is VectorDrawableCompat -> "VectorDrawableCompat(${toSizeString()})"
    VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && this is VectorDrawable -> "VectorDrawable(${toSizeString()})"
    this is GradientDrawable -> "GradientDrawable(${toSizeString()})"
    VERSION.SDK_INT >= VERSION_CODES.M && this is DrawableWrapper -> "DrawableWrapper(drawable=${drawable?.toLogString()})"
    this is DrawableWrapperCompat -> "DrawableWrapperCompat(drawable=${drawable?.toLogString()})"
    else -> toString()
}

/**
 * Get the size of the Drawable in the format "width x height"
 *
 * @see com.github.panpf.sketch.core.android.test.util.DrawablesTest.testToSizeString
 */
internal fun Drawable.toSizeString(): String = "${intrinsicWidth}x${intrinsicHeight}"