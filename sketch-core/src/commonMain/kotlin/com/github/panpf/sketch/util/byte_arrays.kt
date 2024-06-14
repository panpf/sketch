package com.github.panpf.sketch.util

fun ByteArray.rangeEquals(offset: Int, bytes: ByteArray): Boolean {
    require(bytes.isNotEmpty()) { "bytes is empty" }

    var index = 0
    var result = false
    while (index < bytes.size && (index + offset) < this.size) {
        result = bytes[index] == this[offset + index]
        if (!result) {
            return false
        } else {
            index++
        }
    }
    return result
}

/**
 * @param fromIndex   the begin index, inclusive.
 * @param toIndex     the end index, inclusive.
 */
fun ByteArray.indexOf(byte: Byte, fromIndex: Int, toIndex: Int): Int {
    require(fromIndex in 0L..toIndex) { "fromIndex=$fromIndex toIndex=$toIndex" }
    var index = fromIndex
    while (index < toIndex && index < this.size) {
        if (this[index] == byte) {
            return index
        } else {
            index++
        }
    }
    return -1
}

/**
 * @param fromIndex   the begin index, inclusive.
 * @param toIndex     the end index, exclusive.
 */
fun ByteArray.indexOf(bytes: ByteArray, fromIndex: Int, toIndex: Int): Int {
    require(fromIndex in 0L..toIndex) { "fromIndex=$fromIndex toIndex=$toIndex" }
    require(bytes.isNotEmpty()) { "bytes is empty" }

    val firstByte = bytes[0]
    val lastIndex = toIndex + 1 - bytes.size
    var currentIndex = fromIndex
    while (currentIndex < lastIndex) {
        currentIndex = indexOf(firstByte, currentIndex, lastIndex)
        if (currentIndex == -1 || rangeEquals(currentIndex, bytes)) {
            return currentIndex
        }
        currentIndex++
    }
    return -1
}