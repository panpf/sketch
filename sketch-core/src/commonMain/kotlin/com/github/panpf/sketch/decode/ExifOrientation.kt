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
package com.github.panpf.sketch.decode

import com.github.panpf.sketch.annotation.IntDef
import com.github.panpf.sketch.decode.ExifOrientation.Companion.FLIP_HORIZONTAL
import com.github.panpf.sketch.decode.ExifOrientation.Companion.FLIP_VERTICAL
import com.github.panpf.sketch.decode.ExifOrientation.Companion.NORMAL
import com.github.panpf.sketch.decode.ExifOrientation.Companion.ROTATE_180
import com.github.panpf.sketch.decode.ExifOrientation.Companion.ROTATE_270
import com.github.panpf.sketch.decode.ExifOrientation.Companion.ROTATE_90
import com.github.panpf.sketch.decode.ExifOrientation.Companion.TRANSPOSE
import com.github.panpf.sketch.decode.ExifOrientation.Companion.TRANSVERSE
import com.github.panpf.sketch.decode.ExifOrientation.Companion.UNDEFINED
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.LOCAL_VARIABLE
import kotlin.annotation.AnnotationTarget.PROPERTY
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.annotation.AnnotationTarget.PROPERTY_SETTER
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@IntDef(
    value = [
        UNDEFINED,
        NORMAL,
        FLIP_HORIZONTAL,
        FLIP_VERTICAL,
        ROTATE_90,
        ROTATE_180,
        ROTATE_270,
        TRANSPOSE,
        TRANSVERSE
    ]
)
@Retention(SOURCE)
@Target(FIELD, PROPERTY, LOCAL_VARIABLE, VALUE_PARAMETER, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class ExifOrientation {
    companion object {
        const val UNDEFINED = 0
        const val NORMAL = 1
        const val FLIP_HORIZONTAL = 2 // left right reversed mirror

        const val ROTATE_180 = 3
        const val FLIP_VERTICAL = 4 // upside down mirror

        // flipped about top-left <--> bottom-right axis
        const val TRANSPOSE = 5
        const val ROTATE_90 = 6 // rotate 90 cw to right it

        // flipped about top-right <--> bottom-left axis
        const val TRANSVERSE = 7
        const val ROTATE_270 = 8 // rotate 270 to right it

        fun name(exifOrientation: Int): String =
            when (exifOrientation) {
                ROTATE_90 -> "ROTATE_90"
                TRANSPOSE -> "TRANSPOSE"
                ROTATE_180 -> "ROTATE_180"
                FLIP_VERTICAL -> "FLIP_VERTICAL"
                ROTATE_270 -> "ROTATE_270"
                TRANSVERSE -> "TRANSVERSE"
                FLIP_HORIZONTAL -> "FLIP_HORIZONTAL"
                UNDEFINED -> "UNDEFINED"
                NORMAL -> "NORMAL"
                else -> exifOrientation.toString()
            }
    }
}