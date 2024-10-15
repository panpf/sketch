package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import kotlin.math.ceil
import kotlin.test.assertEquals
import kotlin.test.assertTrue


/**
 * @return top-left, top-right, bottom-left, bottom-right
 */
fun Rect.chunkingFour(): List<Rect> {
    val sourceRect = this
    val topLeftRect = Rect(
        left = sourceRect.left,
        top = sourceRect.top,
        right = sourceRect.left + (sourceRect.width() / 2),
        bottom = sourceRect.top + (sourceRect.height() / 2),
    ).apply {
        // The width and height of the first block must be an odd number so that it makes more sense to test
        if (width() % 2 == 0) {
            right += 1
        }
        if (height() % 2 == 0) {
            bottom += 1
        }
        assertTrue(right > left)
        assertTrue(bottom > top)
        assertEquals(sourceRect.left, actual = left)
        assertEquals(sourceRect.top, actual = top)
    }

    val topRightRect = Rect(
        left = topLeftRect.right,
        top = topLeftRect.top,
        right = sourceRect.right,
        bottom = topLeftRect.bottom
    ).apply {
        assertTrue(right > left)
        assertTrue(bottom > top)
        assertEquals(topLeftRect.right, actual = left)
        assertEquals(topLeftRect.top, actual = top)
        assertEquals(sourceRect.right, actual = right)
        assertEquals(topLeftRect.bottom, actual = bottom)
    }

    val bottomLeftRect = Rect(
        left = topLeftRect.left,
        top = topLeftRect.bottom,
        right = topLeftRect.right,
        bottom = sourceRect.bottom
    ).apply {
        assertTrue(right > left)
        assertTrue(bottom > top)
        assertEquals(topLeftRect.left, actual = left)
        assertEquals(topLeftRect.bottom, actual = top)
        assertEquals(topLeftRect.right, actual = right)
        assertEquals(sourceRect.bottom, actual = bottom)
    }

    val bottomRightRect = Rect(
        left = topLeftRect.right,
        top = topLeftRect.bottom,
        right = sourceRect.right,
        bottom = sourceRect.bottom
    ).apply {
        assertTrue(right > left)
        assertTrue(bottom > top)
        assertEquals(topLeftRect.right, actual = left)
        assertEquals(topLeftRect.bottom, actual = top)
        assertEquals(sourceRect.right, actual = right)
        assertEquals(sourceRect.bottom, actual = bottom)
    }
    assertEquals(
        expected = sourceRect.width(),
        actual = topLeftRect.width() + topRightRect.width(),
        message = "leftTopRect=$topLeftRect, rightTopRect=$topRightRect, sourceRect=$sourceRect"
    )
    assertEquals(
        expected = sourceRect.width(),
        actual = bottomLeftRect.width() + bottomRightRect.width(),
        message = "leftBottomRect=$bottomLeftRect, rightBottomRect=$bottomRightRect, sourceRect=$sourceRect"
    )
    assertEquals(
        expected = sourceRect.height(),
        actual = topLeftRect.height() + bottomLeftRect.height(),
        message = "leftTopRect=$topLeftRect, leftBottomRect=$bottomLeftRect, sourceRect=$sourceRect"
    )
    assertEquals(
        expected = sourceRect.height(),
        actual = topRightRect.height() + bottomRightRect.height(),
        message = "rightTopRect=$topRightRect, rightBottomRect=$bottomRightRect, sourceRect=$sourceRect"
    )

    return listOf(topLeftRect, topRightRect, bottomLeftRect, bottomRightRect)
}

fun Rect.div(number: Int): Rect {
    return Rect(
        left = ceil(left / number.toFloat()).toInt(),
        top = ceil(top / number.toFloat()).toInt(),
        right = ceil(right / number.toFloat()).toInt(),
        bottom = ceil(bottom / number.toFloat()).toInt(),
    )
}

fun Size.toRect(): Rect {
    return Rect(0, 0, width, height)
}