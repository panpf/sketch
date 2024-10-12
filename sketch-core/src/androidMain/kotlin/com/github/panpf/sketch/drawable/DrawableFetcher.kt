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

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.util.ColorFetcher
import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.getDrawableCompat
import com.github.panpf.sketch.util.getXmlDrawableCompat

/**
 * For getting the Drawable
 */
interface DrawableFetcher : Key {

    fun getDrawable(context: Context): Drawable

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun toString(): String
}

/**
 * Get Drawable from resource
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.ResDrawableFetcherTest
 */
class ResDrawableFetcher constructor(
    @DrawableRes val resId: Int,
    val resources: Resources? = null,
    val packageName: String? = null,
) : DrawableFetcher {

    override val key: String = if (packageName != null && resources != null) {
        "ResDrawable($resId,$resources,$packageName)"
    } else {
        "ResDrawable($resId)"
    }

    @SuppressLint("ResourceType")
    override fun getDrawable(context: Context): Drawable {
        return if (packageName != null && resources != null && packageName != context.packageName) {
            // getXmlDrawableCompat can load vector resources that are in the other package.
            context.getXmlDrawableCompat(resources, resId)
        } else {
            // getDrawableCompat can only load vector resources that are in the current package.
            context.getDrawableCompat(resId)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ResDrawableFetcher
        if (resId != other.resId) return false
        if (resources != other.resources) return false
        if (packageName != other.packageName) return false
        return true
    }

    override fun hashCode(): Int {
        var result = resId.hashCode()
        result = 31 * result + (resources?.hashCode() ?: 0)
        result = 31 * result + (packageName?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return if (packageName != null && resources != null) {
            "ResDrawableFetcher(resId=$resId, resources=$resources, packageName=$packageName)"
        } else {
            "ResDrawableFetcher(resId=$resId)"
        }
    }
}

/**
 * Get Drawable from real EquitableDrawable
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.RealDrawableFetcherTest
 */
class RealDrawableFetcher constructor(val drawable: EquitableDrawable) : DrawableFetcher {

    override val key: String = "RealDrawable(${drawable.key})"

    override fun getDrawable(context: Context): Drawable {
        return drawable
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as RealDrawableFetcher
        if (drawable != other.drawable) return false
        return true
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String = "RealDrawableFetcher(drawable=$drawable)"
}

/**
 * Get Drawable from real Color
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.RealColorDrawableFetcherTest
 */
class RealColorDrawableFetcher constructor(@ColorInt val color: Int) : DrawableFetcher {

    override val key: String = "RealColorDrawable($color)"

    override fun getDrawable(context: Context): Drawable {
        return ColorDrawable(color)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as RealColorDrawableFetcher
        if (color != other.color) return false
        return true
    }

    override fun hashCode(): Int {
        return color.hashCode()
    }

    override fun toString(): String = "RealColorDrawableFetcher(color=$color)"
}

/**
 * Get Drawable from ColorFetcher
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.ColorFetcherDrawableFetcherTest
 */
class ColorFetcherDrawableFetcher constructor(val color: ColorFetcher) : DrawableFetcher {

    override val key: String = "ColorFetcherDrawable(${color.key})"

    override fun getDrawable(context: Context): Drawable {
        return ColorDrawable(color.getColor(context))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ColorFetcherDrawableFetcher
        if (color != other.color) return false
        return true
    }

    override fun hashCode(): Int {
        return color.hashCode()
    }

    override fun toString(): String = "ColorFetcherDrawableFetcher(color=$color)"
}

/**
 * Get Drawable from resource color
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.ResColorDrawableFetcherTest
 */
class ResColorDrawableFetcher constructor(@ColorRes val resId: Int) : DrawableFetcher {

    override val key: String = "ResColorDrawable($resId)"

    override fun getDrawable(context: Context): Drawable {
        return context.resources.getDrawableCompat(resId, null)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ResColorDrawableFetcher
        if (resId != other.resId) return false
        return true
    }

    override fun hashCode(): Int {
        return resId.hashCode()
    }

    override fun toString(): String = "ResColorDrawableFetcher(resId=$resId)"
}