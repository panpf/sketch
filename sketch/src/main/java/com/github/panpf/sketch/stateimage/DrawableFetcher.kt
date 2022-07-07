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
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources

interface DrawableFetcher {

    fun getDrawable(context: Context): Drawable
}

class ResDrawableFetcher(@DrawableRes val drawableRes: Int) : DrawableFetcher {

    override fun getDrawable(context: Context): Drawable =
        AppCompatResources.getDrawable(context, drawableRes)!!

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ResDrawableFetcher) return false

        if (drawableRes != other.drawableRes) return false

        return true
    }

    override fun hashCode(): Int {
        return drawableRes
    }

    override fun toString(): String {
        return "ResDrawableFetcher($drawableRes)"
    }
}

class RealDrawableFetcher(val drawable: Drawable) : DrawableFetcher {

    override fun getDrawable(context: Context): Drawable {
        return drawable
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RealDrawableFetcher) return false

        if (drawable != other.drawable) return false

        return true
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {
        return "RealDrawableFetcher($drawable)"
    }
}