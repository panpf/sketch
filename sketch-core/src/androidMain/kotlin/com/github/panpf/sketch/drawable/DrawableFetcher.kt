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
}

/**
 * Get Drawable from resource
 */
class ResDrawable constructor(
    val packageName: String? = null,
    val resources: Resources? = null,
    @DrawableRes val resId: Int
) : DrawableFetcher {

    override val key: String = if (packageName != null && resources != null) {
        "ResDrawable(packageName=$packageName,resources=$resources,resId=$resId)"
    } else {
        "ResDrawable($resId)"
    }

    constructor(@DrawableRes resId: Int) : this(null, null, resId)

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
        if (other !is ResDrawable) return false
        if (packageName != other.packageName) return false
        if (resources != other.resources) return false
        if (resId != other.resId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = packageName?.hashCode() ?: 0
        result = 31 * result + (resources?.hashCode() ?: 0)
        result = 31 * result + resId
        return result
    }

    override fun toString(): String {
        return if (packageName != null && resources != null) {
            "ResDrawable(packageName=$packageName, resources=$resources, resId=$resId)"
        } else {
            "ResDrawable($resId)"
        }
    }
}

class RealDrawable(val drawable: Drawable) : DrawableFetcher {

    override val key: String = "RealDrawable($drawable)"

    override fun getDrawable(context: Context): Drawable {
        return drawable
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RealDrawable) return false
        if (drawable != other.drawable) return false
        return true
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {
        return "RealDrawable($drawable)"
    }
}

class RealEqualityDrawable(val wrapper: DrawableEqualizer) : DrawableFetcher {

    override val key: String = "RealEqualityDrawable(${wrapper.key})"

    override fun getDrawable(context: Context): Drawable {
        return wrapper.wrapped
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RealEqualityDrawable) return false
        if (wrapper != other.wrapper) return false
        return true
    }

    override fun hashCode(): Int {
        return wrapper.hashCode()
    }

    override fun toString(): String {
        return "RealEqualityDrawable($wrapper)"
    }
}

class RealColorDrawable(@ColorInt val color: Int) : DrawableFetcher {

    override val key: String = "RealColorDrawable($color)"

    override fun getDrawable(context: Context): Drawable {
        return ColorDrawable(color)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RealColorDrawable) return false
        if (color != other.color) return false
        return true
    }

    override fun hashCode(): Int {
        return color.hashCode()
    }

    override fun toString(): String {
        return "RealColorDrawable($color)"
    }
}

class ColorFetcherDrawable(val color: ColorFetcher) : DrawableFetcher {

    override val key: String = "ColorFetcherDrawable(${color.key})"

    override fun getDrawable(context: Context): Drawable {
        return ColorDrawable(color.getColor(context))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ColorFetcherDrawable) return false
        if (color != other.color) return false
        return true
    }

    override fun hashCode(): Int {
        return color.hashCode()
    }

    override fun toString(): String {
        return "ColorFetcherDrawable($color)"
    }
}

class ResColorDrawable(@ColorRes val resId: Int) : DrawableFetcher {

    override val key: String = "ResColorDrawable($resId)"

    override fun getDrawable(context: Context): Drawable {
        return context.resources.getDrawableCompat(resId, null)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ResColorDrawable) return false
        if (resId != other.resId) return false
        return true
    }

    override fun hashCode(): Int {
        return resId.hashCode()
    }

    override fun toString(): String {
        return "ResColorDrawable($resId)"
    }
}