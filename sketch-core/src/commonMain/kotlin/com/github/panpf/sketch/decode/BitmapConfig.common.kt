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

package com.github.panpf.sketch.decode

import com.github.panpf.sketch.decode.BitmapConfig.FixedQuality
import com.github.panpf.sketch.decode.BitmapConfig.HighQuality
import com.github.panpf.sketch.decode.BitmapConfig.LowQuality
import com.github.panpf.sketch.util.Key

/**
 * Whether configured for low-quality bitmaps
 */
val BitmapConfig.isLowQuality: Boolean
    get() = this === LowQuality

/**
 * Whether configured for high-quality bitmaps
 */
val BitmapConfig.isHighQuality: Boolean
    get() = this === HighQuality

/**
 * Whether configured for fixed bitmaps
 */
val BitmapConfig.isFixed: Boolean
    get() = this is FixedQuality

/**
 * Whether configured for dynamic bitmaps
 */
val BitmapConfig.isDynamic: Boolean
    get() = this !is FixedQuality

/**
 * Configure bitmap quality
 *
 * @see com.github.panpf.sketch.core.common.test.decode.BitmapConfigTest
 */
sealed interface BitmapConfig : Key {

    val value: String

    companion object {
        fun valueOf(value: String): BitmapConfig = when (value) {
            LowQuality.value -> LowQuality
            HighQuality.value -> HighQuality
            else -> FixedQuality(value)
        }
    }

    /**
     * Low quality bitmap config. RGB_565 is preferred, followed by ARGB_8888
     */
    data object LowQuality : BitmapConfig {

        override val key: String = "LowQuality"

        override val value: String = "LowQuality"

        override fun toString(): String = "LowQuality"
    }

    /**
     * High quality bitmap config. RGBA_F16 is preferred, followed by ARGB_8888
     */
    data object HighQuality : BitmapConfig {

        override val key: String = "HighQuality"

        override val value: String = "HighQuality"

        override fun toString(): String = "HighQuality"
    }

    /**
     * Fixed bitmap config, whatever mimeTye is will return the specified config
     */
    class FixedQuality(override val value: String) : BitmapConfig {

        override val key: String = "FixedQuality($value)"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as FixedQuality
            if (value != other.value) return false
            return true
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun toString(): String {
            return "FixedQuality($value)"
        }
    }
}