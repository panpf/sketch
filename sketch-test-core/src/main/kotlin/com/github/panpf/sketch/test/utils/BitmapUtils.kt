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
package com.github.panpf.sketch.test.utils

import android.graphics.Bitmap


val Bitmap.cornerA: Int
    get() = getPixel(0, 0)
val Bitmap.cornerB: Int
    get() = getPixel(width - 1, 0)
val Bitmap.cornerC: Int
    get() = getPixel(width - 1, height - 1)
val Bitmap.cornerD: Int
    get() = getPixel(0, height - 1)

fun Bitmap.corners(block: Bitmap.() -> List<Int>): List<Int> {
    return block(this)
}

fun Bitmap.corners(): List<Int> = listOf(cornerA, cornerB, cornerC, cornerD)

@Suppress("USELESS_ELVIS")
val Bitmap.configOrNull: Bitmap.Config?
    get() = config ?: null

fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$configOrNull)"