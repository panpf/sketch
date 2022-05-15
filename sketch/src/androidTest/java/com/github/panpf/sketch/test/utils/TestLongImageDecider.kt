package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.util.LongImageDecider

class TestLongImageDecider : LongImageDecider {

    override fun isLongImage(
        imageWidth: Int,
        imageHeight: Int,
        targetWidth: Int,
        targetHeight: Int
    ): Boolean = false

    override fun toString(): String {
        return "TestLongImageDecider"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}