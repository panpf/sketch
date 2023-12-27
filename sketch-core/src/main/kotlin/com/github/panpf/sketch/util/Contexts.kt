@file:JvmName("-Contexts")

/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.util.Xml
import androidx.annotation.DrawableRes
import androidx.annotation.XmlRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

internal fun Context.getDrawableCompat(@DrawableRes resId: Int): Drawable {
    val drawable = AppCompatResources.getDrawable(this, resId)
    return checkNotNull(drawable) { "Invalid resource ID: $resId" }
}

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

internal fun Context?.findLifecycle(): Lifecycle? {
    var context: Context? = this
    while (true) {
        when (context) {
            is LifecycleOwner -> return context.lifecycle
            is ContextWrapper -> context = context.baseContext
            else -> return null
        }
    }
}
