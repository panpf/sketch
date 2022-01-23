package com.github.panpf.sketch.okhttp.test

import okio.BufferedSource
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8
import okio.buffer
import okio.source
import org.junit.Assert
import org.junit.Test

class SourceTest {

    @Test
    fun test() {
        val bytes = "abcdefghijklmnopqrstuvwxyz".toByteArray()
        bytes.inputStream().source().buffer().apply {
            Assert.assertEquals(0, indexOf('a'.code.toByte(), 0, 26))
            Assert.assertEquals(0, indexOf('a'.code.toByte(), 0, 1))
            Assert.assertEquals(12, indexOf('m'.code.toByte(), 0, 26))
            Assert.assertEquals(25, indexOf('z'.code.toByte(), 0, 26))
            Assert.assertEquals(-1, indexOf('a'.code.toByte(), 0, 0))
            Assert.assertEquals(-1, indexOf('a'.code.toByte(), 1, 26))
            Assert.assertEquals(-1, indexOf('m'.code.toByte(), 13, 26))
//            Assert.assertEquals(-1, indexOf('z'.code.toByte(), 28, 26))

            Assert.assertEquals(0, indexOf("abc".encodeUtf8(), 0, 26))
            Assert.assertEquals(12, indexOf("mno".encodeUtf8(), 0, 26))
            Assert.assertEquals(-1, indexOf("xyz".encodeUtf8(), 0, 26))
            Assert.assertEquals(23, indexOf("xyz".encodeUtf8(), 0, 27))
            Assert.assertEquals(-1, indexOf("abc".encodeUtf8(), 10, 26))
            Assert.assertEquals(-1, indexOf("mno".encodeUtf8(), 15, 26))
            Assert.assertEquals(-1, indexOf("xyz".encodeUtf8(), 25, 26))
        }
    }
}

internal fun BufferedSource.indexOf(bytes: ByteString, fromIndex: Long, toIndex: Long): Long {
    require(bytes.size > 0) { "bytes is empty" }

    val firstByte = bytes[0]
    val lastIndex = toIndex - bytes.size
    var currentIndex = fromIndex
    while (currentIndex < lastIndex) {
        currentIndex = indexOf(firstByte, currentIndex, lastIndex)
        if (currentIndex == -1L || rangeEquals(currentIndex, bytes)) {
            return currentIndex
        }
        currentIndex++
    }
    return -1
}