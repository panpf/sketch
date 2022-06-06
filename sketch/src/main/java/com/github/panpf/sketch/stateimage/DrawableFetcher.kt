/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
import android.graphics.drawable.VectorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

interface DrawableFetcher {

    fun getDrawable(context: Context): Drawable
}

class ResDrawable(@DrawableRes val drawableRes: Int) : DrawableFetcher {

    override fun getDrawable(context: Context): Drawable {
        return AppCompatResources.getDrawable(context, drawableRes)!!.let {
            // VectorDrawable and VectorDrawableCompat share VectorDrawableState,
            // VectorDrawableState holds alpha and other properties, which are shared, causing exceptions later
            if ((VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && it is VectorDrawable) || it is VectorDrawableCompat) {
                it.mutate()
            } else {
                it
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResDrawable

        if (drawableRes != other.drawableRes) return false

        return true
    }

    override fun hashCode(): Int {
        return drawableRes
    }

    override fun toString(): String {
        return "ResDrawable($drawableRes)"
    }
}

class RealDrawable(val drawable: Drawable) : DrawableFetcher {

    override fun getDrawable(context: Context): Drawable {
        return drawable.mutate()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RealDrawable

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