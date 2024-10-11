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

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat

/**
 * For getting the color
 */
interface ColorFetcher : Key {

    fun getColor(context: Context): Int

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun toString(): String
}

/**
 * Get color from int
 *
 * @see com.github.panpf.sketch.core.android.test.util.ColorFetcherTest.testIntColorFetcher
 */
class IntColorFetcher(@ColorInt val color: Int) : ColorFetcher {

    override val key: String = "IntColorFetcher(color=$color)"

    override fun getColor(context: Context): Int = color

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as IntColorFetcher
        if (color != other.color) return false
        return true
    }

    override fun hashCode(): Int = color

    override fun toString(): String = "IntColorFetcher(color=$color)"
}

/**
 * Get color from resource
 *
 * @see com.github.panpf.sketch.core.android.test.util.ColorFetcherTest.testResColorFetcher
 */
class ResColorFetcher(@ColorRes val resId: Int) : ColorFetcher {

    override val key: String = "ResColorFetcher(resId=$resId)"

    override fun getColor(context: Context): Int =
        ResourcesCompat.getColor(context.resources, resId, null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ResColorFetcher
        if (resId != other.resId) return false
        return true
    }

    override fun hashCode(): Int = resId

    override fun toString(): String = "ResColorFetcher(resId=$resId)"
}