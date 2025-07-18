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

import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Alias of [Size]
 *
 * @see com.github.panpf.sketch.core.common.test.util.SizeTest.testSketchSize
 */
typealias SketchSize = Size

/**
 * A simple class that represents width and height.
 *
 * @see com.github.panpf.sketch.core.common.test.util.SizeTest
 */
open class Size(val width: Int, val height: Int) {

    constructor() : this(0, 0)

    val isEmpty: Boolean
        get() = width <= 0 || height <= 0

    operator fun component1(): Int = width

    operator fun component2(): Int = height

    override fun toString(): String = "${width}x$height"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
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

        val Empty = Size(0, 0)
        val Origin = Size(0, 0)

        @Throws(NumberFormatException::class)
        fun parseSize(string: String): Size {
            var sepIx = string.indexOf('*')
            if (sepIx < 0) {
                sepIx = string.indexOf('x')
            }
            if (sepIx < 0) {
                throw NumberFormatException("Invalid Size: \"$string\"")
            }
            return try {
                Size(string.substring(0, sepIx).toInt(), string.substring(sepIx + 1).toInt())
            } catch (e: NumberFormatException) {
                throw NumberFormatException("Invalid Size: \"$string\"")
            }
        }
    }
}

/**
 * Check if the size is not empty.
 *
 * @see com.github.panpf.sketch.core.common.test.util.SizeTest.testEmptyNotEmpty
 */
val Size.isNotEmpty: Boolean
    get() = !isEmpty

/**
 * Multiplication operator.
 *
 * Returns a [Size] whose dimensions are the dimensions of the left-hand-side
 * operand (a [Size]) multiplied by the scalar right-hand-side operand (a
 * [Float]).
 *
 * @see com.github.panpf.sketch.core.common.test.util.SizeTest.testTimes
 */
operator fun Size.times(operand: Float) = Size(
    width = (width * operand).roundToInt(),
    height = (height * operand).roundToInt()
)

/**
 * Division operator.
 *
 * Returns a [Size] whose dimensions are the dimensions of the left-hand-side
 * operand (a [Size]) divided by the scalar right-hand-side operand (a
 * [Float]).
 *
 * @see com.github.panpf.sketch.core.common.test.util.SizeTest.testDiv
 */
operator fun Size.div(operand: Float) = Size(
    width = (width / operand).roundToInt(),
    height = (height / operand).roundToInt()
)

/**
 * Check if two Size have the same aspect ratio
 *
 * @see com.github.panpf.sketch.core.common.test.util.SizeTest.testIsSameAspectRatio
 */
fun Size.isSameAspectRatio(other: Size, delta: Float = 0f): Boolean {
    val selfScale = this.width / this.height.toFloat()
    val otherScale = other.width / other.height.toFloat()
    if (selfScale.compareTo(otherScale) == 0) {
        return true
    }
    if (delta != 0f && abs(selfScale - otherScale) <= delta) {
        return true
    }
    return false
}

/**
 * The size after rotating [rotation] degrees
 *
 * @see com.github.panpf.sketch.core.common.test.util.SizeTest.testRotate
 */
fun Size.rotate(rotation: Int): Size {
    return if (rotation % 180 == 0) this else Size(width = height, height = width)
}

/**
 * Ensures that this value is not less than the specified minimumValue.
 *
 * @see com.github.panpf.sketch.core.common.test.util.SizeTest.testCoerceAtLeast
 */
fun Size.coerceAtLeast(minimumValue: Size): Size = Size(
    width = width.coerceAtLeast(minimumValue.width),
    height = height.coerceAtLeast(minimumValue.height),
)

/**
 * Ensures that this value is not greater than the specified maximumValue.
 *
 * @see com.github.panpf.sketch.core.common.test.util.SizeTest.testCoerceAtMost
 */
fun Size.coerceAtMost(maximumValue: Size): Size = Size(
    width = width.coerceAtMost(maximumValue.width),
    height = height.coerceAtMost(maximumValue.height),
)

/**
 * Return the results of two IntSizeCompat addition operations
 *
 * @see com.github.panpf.sketch.core.common.test.util.SizeTest.testPlus
 */
operator fun Size.plus(other: Size): Size =
    Size(this.width + other.width, this.height + other.height)

/**
 * Return the results of two Size subtraction operations
 *
 * @see com.github.panpf.sketch.core.common.test.util.SizeTest.testMinus
 */
operator fun Size.minus(other: Size): Size =
    Size(this.width - other.width, this.height - other.height)