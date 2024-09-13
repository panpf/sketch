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
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Rect
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
import android.os.Build.VERSION.SDK_INT
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
import com.github.panpf.sketch.drawable.SketchDrawable
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import kotlin.math.min

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
    return if (SDK_INT >= 24) {
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
 * @see com.github.panpf.sketch.core.android.test.util.DrawablesTest.testToNewBitmap
 */
internal fun Drawable.toNewBitmap(
    preferredConfig: Config? = null,
    targetSize: Size? = null
): Bitmap {
    val (oldLeft, oldTop, oldRight, oldBottom) = bounds
    val targetWidth = targetSize?.width ?: intrinsicWidth
    val targetHeight = targetSize?.height ?: intrinsicHeight
    setBounds(0, 0, targetWidth, targetHeight)

    val bitmap: Bitmap = Bitmap.createBitmap(
        /* width = */ targetWidth,
        /* height = */ targetHeight,
        /* config = */ preferredConfig.safeToSoftware(),
    )
    val canvas = Canvas(bitmap)
    draw(canvas)

    setBounds(oldLeft, oldTop, oldRight, oldBottom) // restore bounds
    return bitmap
}

/**
 * Get the width of Drawable, if it is BitmapDrawable, return the width of Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.DrawablesTest.testWidthWithBitmapFirst
 */
internal val Drawable.widthWithBitmapFirst: Int
    get() = (this as? BitmapDrawable)?.bitmap?.width ?: intrinsicWidth

/**
 * Get the height of Drawable. If it is BitmapDrawable, return the height of Bitmap.
 *
 * @see com.github.panpf.sketch.core.android.test.util.DrawablesTest.testHeightWithBitmapFirst
 */
internal val Drawable.heightWithBitmapFirst: Int
    get() = (this as? BitmapDrawable)?.bitmap?.height ?: intrinsicHeight

/**
 * Get the string applicable to the log
 *
 * @see com.github.panpf.sketch.core.android.test.util.DrawablesTest.testToLogString
 */
fun Drawable.toLogString(): String = when {
    this is SketchDrawable -> toString()
    this is BitmapDrawable -> "BitmapDrawable(${bitmap.toLogString()})"
    this is RoundedBitmapDrawable -> "RoundedBitmapDrawable(drawable=${bitmap?.toLogString()})"
    SDK_INT >= VERSION_CODES.P && this is AnimatedImageDrawable -> "AnimatedImageDrawable(${toSizeString()})"
    this is AnimatedVectorDrawableCompat -> "AnimatedVectorDrawableCompat(${toSizeString()})"
    SDK_INT >= VERSION_CODES.LOLLIPOP && this is AnimatedVectorDrawable -> "AnimatedVectorDrawable(${toSizeString()})"
    this is TransitionDrawable -> "TransitionDrawable(${toSizeString()})"
    this is ColorDrawable -> "ColorDrawable(${color})"
    SDK_INT >= VERSION_CODES.Q && this is ColorStateListDrawable -> "ColorStateListDrawable(${toSizeString()})"
    this is VectorDrawableCompat -> "VectorDrawableCompat(${toSizeString()})"
    SDK_INT >= VERSION_CODES.LOLLIPOP && this is VectorDrawable -> "VectorDrawable(${toSizeString()})"
    this is GradientDrawable -> "GradientDrawable(${toSizeString()})"
    SDK_INT >= VERSION_CODES.M && this is DrawableWrapper -> "DrawableWrapper(drawable=${drawable?.toLogString()})"
    this is DrawableWrapperCompat -> "DrawableWrapperCompat(drawable=${drawable?.toLogString()})"
    else -> toString()
}

/**
 * Get the size of the Drawable in the format "width x height"
 *
 * @see com.github.panpf.sketch.core.android.test.util.DrawablesTest.testToSizeString
 */
internal fun Drawable.toSizeString(): String = "${intrinsicWidth}x${intrinsicHeight}"

/**
 * Calculate the bounds of the Drawable to fit the container.
 *
 * @see com.github.panpf.sketch.core.android.test.util.DrawablesTest.testCalculateFitBounds
 */
internal fun calculateFitBounds(contentSize: Size, containerBounds: Rect): Rect {
    val left: Int
    val top: Int
    val right: Int
    val bottom: Int
    if (contentSize.width <= containerBounds.width() && contentSize.height <= containerBounds.height()) {
        left = containerBounds.left + (containerBounds.width() - contentSize.width) / 2
        top = containerBounds.top + (containerBounds.height() - contentSize.height) / 2
        right = left + contentSize.width
        bottom = top + contentSize.height
    } else {
        val scale = min(
            containerBounds.width().toFloat() / contentSize.width,
            containerBounds.height().toFloat() / contentSize.height
        )
        val scaledWidth = (contentSize.width * scale).toInt()
        val scaledHeight = (contentSize.height * scale).toInt()
        left = containerBounds.left + (containerBounds.width() - scaledWidth) / 2
        top = containerBounds.top + (containerBounds.height() - scaledHeight) / 2
        right = left + scaledWidth
        bottom = top + scaledHeight
    }
    return Rect(left, top, right, bottom)
}