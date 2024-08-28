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

import kotlin.math.max
import kotlin.math.min

class Rect {
    var left = 0
    var top = 0
    var right = 0
    var bottom = 0

    /**
     * Create a new empty Rect. All coordinates are initialized to 0.
     */
    constructor()

    /**
     * Create a new rectangle with the specified coordinates. Note: no range
     * checking is performed, so the caller must ensure that left <= right and
     * top <= bottom.
     *
     * @param left   The X coordinate of the left side of the rectangle
     * @param top    The Y coordinate of the top of the rectangle
     * @param right  The X coordinate of the right side of the rectangle
     * @param bottom The Y coordinate of the bottom of the rectangle
     */
    constructor(left: Int, top: Int, right: Int, bottom: Int) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
    }

    /**
     * Create a new rectangle, initialized with the values in the specified
     * rectangle (which is left unmodified).
     *
     * @param r The rectangle whose coordinates are copied into the new
     * rectangle.
     */
    constructor(r: Rect?) {
        if (r == null) {
            bottom = 0
            right = bottom
            top = right
            left = top
        } else {
            left = r.left
            top = r.top
            right = r.right
            bottom = r.bottom
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is Rect) return false
        return left == other.left && top == other.top && right == other.right && bottom == other.bottom
    }

    override fun hashCode(): Int {
        var result = left
        result = 31 * result + top
        result = 31 * result + right
        result = 31 * result + bottom
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder(32)
        sb.append("Rect(")
        sb.append(left)
        sb.append(", ")
        sb.append(top)
        sb.append(" - ")
        sb.append(right)
        sb.append(", ")
        sb.append(bottom)
        sb.append(")")
        return sb.toString()
    }

    /**
     * Return a string representation of the rectangle in a compact form.
     */
    fun toShortString(sb: StringBuilder = StringBuilder(32)): String {
        sb.setLength(0)
        sb.append('[')
        sb.append(left)
        sb.append(',')
        sb.append(top)
        sb.append("][")
        sb.append(right)
        sb.append(',')
        sb.append(bottom)
        sb.append(']')
        return sb.toString()
    }

    /**
     * Return a string representation of the rectangle in a well-defined format.
     *
     *
     * You can later recover the Rect from this string through
     * [.unflattenFromString].
     *
     * @return Returns a new String of the form "left top right bottom"
     */
    fun flattenToString(): String {
        val sb = StringBuilder(32)
        // WARNING: Do not change the format of this string, it must be
        // preserved because Rects are saved in this flattened format.
        sb.append(left)
        sb.append(' ')
        sb.append(top)
        sb.append(' ')
        sb.append(right)
        sb.append(' ')
        sb.append(bottom)
        return sb.toString()
    }

    val isEmpty: Boolean
        /**
         * Returns true if the rectangle is empty (left >= right or top >= bottom)
         */
        get() = left >= right || top >= bottom

    val isValid: Boolean
        /**
         * @return `true` if the rectangle is valid (left <= right and top <= bottom).
         * @hide
         */
        get() = left <= right && top <= bottom

    /**
     * @return the rectangle's width. This does not check for a valid rectangle
     * (i.e. left <= right) so the result may be negative.
     */
    fun width(): Int {
        return right - left
    }

    /**
     * @return the rectangle's height. This does not check for a valid rectangle
     * (i.e. top <= bottom) so the result may be negative.
     */
    fun height(): Int {
        return bottom - top
    }

    /**
     * @return the horizontal center of the rectangle. If the computed value
     * is fractional, this method returns the largest integer that is
     * less than the computed value.
     */
    fun centerX(): Int {
        return left + right shr 1
    }

    /**
     * @return the vertical center of the rectangle. If the computed value
     * is fractional, this method returns the largest integer that is
     * less than the computed value.
     */
    fun centerY(): Int {
        return top + bottom shr 1
    }

    /**
     * @return the exact horizontal center of the rectangle as a float.
     */
    fun exactCenterX(): Float {
        return (left + right) * 0.5f
    }

    /**
     * @return the exact vertical center of the rectangle as a float.
     */
    fun exactCenterY(): Float {
        return (top + bottom) * 0.5f
    }

    /**
     * Set the rectangle to (0,0,0,0)
     */
    fun setEmpty() {
        bottom = 0
        top = bottom
        right = top
        left = right
    }

    /**
     * Set the rectangle's coordinates to the specified values. Note: no range
     * checking is performed, so it is up to the caller to ensure that
     * left <= right and top <= bottom.
     *
     * @param left   The X coordinate of the left side of the rectangle
     * @param top    The Y coordinate of the top of the rectangle
     * @param right  The X coordinate of the right side of the rectangle
     * @param bottom The Y coordinate of the bottom of the rectangle
     */
    operator fun set(left: Int, top: Int, right: Int, bottom: Int) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
    }

    /**
     * Copy the coordinates from src into this rectangle.
     *
     * @param src The rectangle whose coordinates are copied into this
     * rectangle.
     */
    fun set(src: Rect) {
        left = src.left
        top = src.top
        right = src.right
        bottom = src.bottom
    }

    /**
     * Offset the rectangle by adding dx to its left and right coordinates, and
     * adding dy to its top and bottom coordinates.
     *
     * @param dx The amount to add to the rectangle's left and right coordinates
     * @param dy The amount to add to the rectangle's top and bottom coordinates
     */
    fun offset(dx: Int, dy: Int) {
        left += dx
        top += dy
        right += dx
        bottom += dy
    }

    /**
     * Offset the rectangle to a specific (left, top) position,
     * keeping its width and height the same.
     *
     * @param newLeft   The new "left" coordinate for the rectangle
     * @param newTop    The new "top" coordinate for the rectangle
     */
    fun offsetTo(newLeft: Int, newTop: Int) {
        right += newLeft - left
        bottom += newTop - top
        left = newLeft
        top = newTop
    }

    /**
     * Inset the rectangle by (dx,dy). If dx is positive, then the sides are
     * moved inwards, making the rectangle narrower. If dx is negative, then the
     * sides are moved outwards, making the rectangle wider. The same holds true
     * for dy and the top and bottom.
     *
     * @param dx The amount to add(subtract) from the rectangle's left(right)
     * @param dy The amount to add(subtract) from the rectangle's top(bottom)
     */
    fun inset(dx: Int, dy: Int) {
        left += dx
        top += dy
        right -= dx
        bottom -= dy
    }

    /**
     * Insets the rectangle on all sides specified by the dimensions of the `insets`
     * rectangle.
     * @hide
     * @param insets The rectangle specifying the insets on all side.
     */
    fun inset(insets: Rect) {
        left += insets.left
        top += insets.top
        right -= insets.right
        bottom -= insets.bottom
    }

    /**
     * Insets the rectangle on all sides specified by the insets.
     *
     * @param left The amount to add from the rectangle's left
     * @param top The amount to add from the rectangle's top
     * @param right The amount to subtract from the rectangle's right
     * @param bottom The amount to subtract from the rectangle's bottom
     */
    fun inset(left: Int, top: Int, right: Int, bottom: Int) {
        this.left += left
        this.top += top
        this.right -= right
        this.bottom -= bottom
    }

    /**
     * Returns true if (x,y) is inside the rectangle. The left and top are
     * considered to be inside, while the right and bottom are not. This means
     * that for a x,y to be contained: left <= x < right and top <= y < bottom.
     * An empty rectangle never contains any point.
     *
     * @param x The X coordinate of the point being tested for containment
     * @param y The Y coordinate of the point being tested for containment
     * @return true iff (x,y) are contained by the rectangle, where containment
     * means left <= x < right and top <= y < bottom
     */
    fun contains(x: Int, y: Int): Boolean {
        // check for empty first
        return left < right
                && top < bottom
                && x >= left
                && x < right
                && y >= top
                && y < bottom
    }

    /**
     * Returns true iff the 4 specified sides of a rectangle are inside or equal
     * to this rectangle. i.e. is this rectangle a superset of the specified
     * rectangle. An empty rectangle never contains another rectangle.
     *
     * @param left The left side of the rectangle being tested for containment
     * @param top The top of the rectangle being tested for containment
     * @param right The right side of the rectangle being tested for containment
     * @param bottom The bottom of the rectangle being tested for containment
     * @return true iff the the 4 specified sides of a rectangle are inside or
     * equal to this rectangle
     */
    fun contains(left: Int, top: Int, right: Int, bottom: Int): Boolean {
        // check for empty first
        // now check for containment
        return this.left < this.right
                && this.top < this.bottom
                && this.left <= left
                && this.top <= top
                && this.right >= right
                && this.bottom >= bottom
    }

    /**
     * Returns true iff the specified rectangle r is inside or equal to this
     * rectangle. An empty rectangle never contains another rectangle.
     *
     * @param r The rectangle being tested for containment.
     * @return true iff the specified rectangle r is inside or equal to this
     * rectangle
     */
    operator fun contains(r: Rect): Boolean {
        // check for empty first
        // now check for containment
        return left < right
                && top < bottom
                && left <= r.left
                && top <= r.top
                && right >= r.right
                && bottom >= r.bottom
    }

    /**
     * If the rectangle specified by left,top,right,bottom intersects this
     * rectangle, return true and set this rectangle to that intersection,
     * otherwise return false and do not change this rectangle. No check is
     * performed to see if either rectangle is empty. Note: To just test for
     * intersection, use [.intersects].
     *
     * @param left The left side of the rectangle being intersected with this
     * rectangle
     * @param top The top of the rectangle being intersected with this rectangle
     * @param right The right side of the rectangle being intersected with this
     * rectangle.
     * @param bottom The bottom of the rectangle being intersected with this
     * rectangle.
     * @return true if the specified rectangle and this rectangle intersect
     * (and this rectangle is then set to that intersection) else
     * return false and do not change this rectangle.
     */
    fun intersect(left: Int, top: Int, right: Int, bottom: Int): Boolean {
        if (this.left < right && left < this.right && this.top < bottom && top < this.bottom) {
            if (this.left < left) this.left = left
            if (this.top < top) this.top = top
            if (this.right > right) this.right = right
            if (this.bottom > bottom) this.bottom = bottom
            return true
        }
        return false
    }

    /**
     * If the specified rectangle intersects this rectangle, return true and set
     * this rectangle to that intersection, otherwise return false and do not
     * change this rectangle. No check is performed to see if either rectangle
     * is empty. To just test for intersection, use intersects()
     *
     * @param r The rectangle being intersected with this rectangle.
     * @return true if the specified rectangle and this rectangle intersect
     * (and this rectangle is then set to that intersection) else
     * return false and do not change this rectangle.
     */
    fun intersect(r: Rect): Boolean {
        return intersect(r.left, r.top, r.right, r.bottom)
    }

    /**
     * If the specified rectangle intersects this rectangle, set this rectangle to that
     * intersection, otherwise set this rectangle to the empty rectangle.
     * @see .inset
     * @hide
     */
    fun intersectUnchecked(other: Rect) {
        left = max(left, other.left)
        top = max(top, other.top)
        right = min(right, other.right)
        bottom = min(bottom, other.bottom)
    }

    /**
     * If rectangles a and b intersect, return true and set this rectangle to
     * that intersection, otherwise return false and do not change this
     * rectangle. No check is performed to see if either rectangle is empty.
     * To just test for intersection, use intersects()
     *
     * @param a The first rectangle being intersected with
     * @param b The second rectangle being intersected with
     * @return true iff the two specified rectangles intersect. If they do, set
     * this rectangle to that intersection. If they do not, return
     * false and do not change this rectangle.
     */
    fun setIntersect(a: Rect, b: Rect): Boolean {
        if (a.left < b.right && b.left < a.right && a.top < b.bottom && b.top < a.bottom) {
            left = max(a.left, b.left)
            top = max(a.top, b.top)
            right = min(a.right, b.right)
            bottom = min(a.bottom, b.bottom)
            return true
        }
        return false
    }

    /**
     * Returns true if this rectangle intersects the specified rectangle.
     * In no event is this rectangle modified. No check is performed to see
     * if either rectangle is empty. To record the intersection, use intersect()
     * or setIntersect().
     *
     * @param left The left side of the rectangle being tested for intersection
     * @param top The top of the rectangle being tested for intersection
     * @param right The right side of the rectangle being tested for
     * intersection
     * @param bottom The bottom of the rectangle being tested for intersection
     * @return true iff the specified rectangle intersects this rectangle. In
     * no event is this rectangle modified.
     */
    fun intersects(left: Int, top: Int, right: Int, bottom: Int): Boolean {
        return this.left < right && left < this.right && this.top < bottom && top < this.bottom
    }

    /**
     * Update this Rect to enclose itself and the specified rectangle. If the
     * specified rectangle is empty, nothing is done. If this rectangle is empty
     * it is set to the specified rectangle.
     *
     * @param left The left edge being unioned with this rectangle
     * @param top The top edge being unioned with this rectangle
     * @param right The right edge being unioned with this rectangle
     * @param bottom The bottom edge being unioned with this rectangle
     */
    fun union(left: Int, top: Int, right: Int, bottom: Int) {
        if (left < right && top < bottom) {
            if (this.left < this.right && this.top < this.bottom) {
                if (this.left > left) this.left = left
                if (this.top > top) this.top = top
                if (this.right < right) this.right = right
                if (this.bottom < bottom) this.bottom = bottom
            } else {
                this.left = left
                this.top = top
                this.right = right
                this.bottom = bottom
            }
        }
    }

    /**
     * Update this Rect to enclose itself and the specified rectangle. If the
     * specified rectangle is empty, nothing is done. If this rectangle is empty
     * it is set to the specified rectangle.
     *
     * @param r The rectangle being unioned with this rectangle
     */
    fun union(r: Rect) {
        union(r.left, r.top, r.right, r.bottom)
    }

    /**
     * Update this Rect to enclose itself and the [x,y] coordinate. There is no
     * check to see that this rectangle is non-empty.
     *
     * @param x The x coordinate of the point to add to the rectangle
     * @param y The y coordinate of the point to add to the rectangle
     */
    fun union(x: Int, y: Int) {
        if (x < left) {
            left = x
        } else if (x > right) {
            right = x
        }
        if (y < top) {
            top = y
        } else if (y > bottom) {
            bottom = y
        }
    }

    /**
     * Swap top/bottom or left/right if there are flipped (i.e. left > right
     * and/or top > bottom). This can be called if
     * the edges are computed separately, and may have crossed over each other.
     * If the edges are already correct (i.e. left <= right and top <= bottom)
     * then nothing is done.
     */
    fun sort() {
        if (left > right) {
            val temp = left
            left = right
            right = temp
        }
        if (top > bottom) {
            val temp = top
            top = bottom
            bottom = temp
        }
    }

    /**
     * Scales up the rect by the given scale.
     */
    fun scale(scale: Float) {
        if (scale != 1.0f) {
            left = (left * scale + 0.5f).toInt()
            top = (top * scale + 0.5f).toInt()
            right = (right * scale + 0.5f).toInt()
            bottom = (bottom * scale + 0.5f).toInt()
        }
    }

    companion object {
        /**
         * Returns a copy of `r` if `r` is not `null`, or `null` otherwise.
         */
        fun copyOrNull(r: Rect?): Rect? {
            return if (r == null) null else Rect(r)
        }

        /**
         * Returns true iff the two specified rectangles intersect. In no event are
         * either of the rectangles modified. To record the intersection,
         * use [.intersect] or [.setIntersect].
         *
         * @param a The first rectangle being tested for intersection
         * @param b The second rectangle being tested for intersection
         * @return true iff the two specified rectangles intersect. In no event are
         * either of the rectangles modified.
         */
        fun intersects(a: Rect, b: Rect): Boolean {
            return a.left < b.right && b.left < a.right && a.top < b.bottom && b.top < a.bottom
        }
    }
}

/**
 * Flip this rect horizontally or vertically within a given container
 */
fun Rect.flipped(spaceSize: Size, vertical: Boolean = false): Rect {
    return if (!vertical) {
        Rect(
            left = spaceSize.width - right,
            top = top,
            right = spaceSize.width - left,
            bottom = bottom
        )
    } else {
        Rect(
            left = left,
            top = spaceSize.height - bottom,
            right = right,
            bottom = spaceSize.height - top
        )
    }
}

/**
 * Rotate the space by [rotation] degrees, and then return the rotated Rect
 */
fun Rect.rotateInSpace(spaceSize: Size, rotation: Int): Rect {
    require(rotation % 90 == 0) { "rotation must be a multiple of 90, rotation: $rotation" }
    return when ((rotation % 360).let { if (it < 0) 360 + it else it }) {
        90 -> {
            Rect(
                left = spaceSize.height - this.bottom,
                top = this.left,
                right = spaceSize.height - this.top,
                bottom = this.right
            )
        }

        180 -> {
            Rect(
                left = spaceSize.width - this.right,
                top = spaceSize.height - this.bottom,
                right = spaceSize.width - this.left,
                bottom = spaceSize.height - this.top,
            )
        }

        270 -> {
            Rect(
                left = this.top,
                top = spaceSize.width - this.right,
                right = this.bottom,
                bottom = spaceSize.width - this.left,
            )
        }

        else -> this
    }
}