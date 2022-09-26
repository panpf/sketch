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
package com.github.panpf.sketch.stateimage

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.util.getDrawableCompat

/**
 * For getting the Drawable
 */
interface DrawableFetcher {

    fun getDrawable(context: Context): Drawable
}

/**
 * Get Drawable from resource
 */
class ResDrawable(@DrawableRes val resId: Int) : DrawableFetcher {

    override fun getDrawable(context: Context): Drawable = context.getDrawableCompat(resId)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ResDrawable) return false
        if (resId != other.resId) return false
        return true
    }

    override fun hashCode(): Int {
        return resId
    }

    override fun toString(): String {
        return "ResDrawable($resId)"
    }
}

class RealDrawable(val drawable: Drawable) : DrawableFetcher {

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