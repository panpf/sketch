package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.util.forEachIndexedIndices
import com.github.panpf.sketch.util.forEachIndices
import kotlin.test.Test
import kotlin.test.assertEquals

class CollectionsTest {

    @Test
    fun testForEachIndices() {
        var result = ""
        val list = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        list.forEachIndices {
            result += it
        }
        assertEquals("0123456789", result)
    }

    @Test
    fun testForEachIndexedIndices() {
        var result = ""
        val list = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        list.forEachIndexedIndices { index, it ->
            result += "($it,$index)"
        }
        assertEquals("(0,0)(1,1)(2,2)(3,3)(4,4)(5,5)(6,6)(7,7)(8,8)(9,9)", result)
    }
}