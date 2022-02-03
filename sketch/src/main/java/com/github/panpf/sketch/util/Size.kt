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
package com.github.panpf.sketch.util

import android.graphics.PointF
import android.graphics.Rect

open class Size(var width: Int, var height: Int) {

    constructor() : this(0, 0)

    operator fun set(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    val isEmpty: Boolean
        get() = width == 0 || height == 0

    override fun toString(): String = width.toString() + "x" + height

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Size

        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        return result
    }


    companion object {
        private fun invalidSize(s: String): NumberFormatException {
            throw NumberFormatException("Invalid Size: \"$s\"")
        }

        @Throws(NumberFormatException::class)
        fun parseSize(string: String): Size {
            var sepIx = string.indexOf('*')
            if (sepIx < 0) {
                sepIx = string.indexOf('x')
            }
            if (sepIx < 0) {
                throw invalidSize(string)
            }
            return try {
                Size(string.substring(0, sepIx).toInt(), string.substring(sepIx + 1).toInt())
            } catch (e: NumberFormatException) {
                throw invalidSize(string)
            }
        }

        /**
         * 将一个旋转了一定度数的矩形转回来（只能是 90 度的倍数）
         */
        fun reverseRotateRect(rect: Rect, rotateDegrees: Int, drawableSize: Size) {
            if (rotateDegrees % 90 != 0) {
                return
            }
            when (rotateDegrees) {
                90 -> {
                    val cache = rect.bottom
                    rect.bottom = rect.left
                    rect.left = rect.top
                    rect.top = rect.right
                    rect.right = cache
                    rect.top = drawableSize.height - rect.top
                    rect.bottom = drawableSize.height - rect.bottom
                }
                180 -> {
                    var cache = rect.right
                    rect.right = rect.left
                    rect.left = cache
                    cache = rect.bottom
                    rect.bottom = rect.top
                    rect.top = cache
                    rect.top = drawableSize.height - rect.top
                    rect.bottom = drawableSize.height - rect.bottom
                    rect.left = drawableSize.width - rect.left
                    rect.right = drawableSize.width - rect.right
                }
                270 -> {
                    val cache = rect.bottom
                    rect.bottom = rect.right
                    rect.right = rect.top
                    rect.top = rect.left
                    rect.left = cache
                    rect.left = drawableSize.width - rect.left
                    rect.right = drawableSize.width - rect.right
                }
            }
        }

        /**
         * 旋转一个点（只能是 90 的倍数）
         */
        fun rotatePoint(point: PointF, rotateDegrees: Int, drawableSize: Size) {
            if (rotateDegrees % 90 != 0) {
                return
            }
            when (rotateDegrees) {
                90 -> {
                    val newX = drawableSize.height - point.y
                    val newY = point.x
                    point.x = newX
                    point.y = newY
                }
                180 -> {
                    val newX = drawableSize.width - point.x
                    val newY = drawableSize.height - point.y
                    point.x = newX
                    point.y = newY
                }
                270 -> {
                    val newX = point.y
                    val newY = drawableSize.width - point.x
                    point.x = newX
                    point.y = newY
                }
            }
        }
    }
}