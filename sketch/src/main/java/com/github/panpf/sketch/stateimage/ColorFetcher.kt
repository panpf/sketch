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
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat

interface ColorFetcher {
    fun getColor(context: Context): Int
}

class IntColor(@ColorInt val color: Int) : ColorFetcher {

    override fun getColor(context: Context): Int = color

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IntColor) return false
        if (color != other.color) return false
        return true
    }

    override fun hashCode(): Int = color

    override fun toString(): String = "IntColor($color)"
}

class ResColor(@ColorRes val resId: Int) : ColorFetcher {

    override fun getColor(context: Context): Int =
        ResourcesCompat.getColor(context.resources, resId, null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ResColor) return false
        if (resId != other.resId) return false
        return true
    }

    override fun hashCode(): Int = resId

    override fun toString(): String = "ResColor($resId)"
}