package com.github.panpf.sketch.fetch.internal

import kotlin.experimental.and

class HeaderBytes constructor(val bytes: ByteArray) {

    fun rangeEquals(offset: Int, bytes: ByteArray): Boolean {
        require(bytes.isNotEmpty()) { "bytes is empty" }

        var index = 0
        var result = false
        while (index < bytes.size && (index + offset) < this.bytes.size) {
            result = bytes[index] == this.bytes[offset + index]
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
    fun indexOf(byte: Byte, fromIndex: Int, toIndex: Int): Int {
        require(fromIndex in 0L..toIndex) { "fromIndex=$fromIndex toIndex=$toIndex" }
        var index = fromIndex
        while (index < toIndex && index < bytes.size) {
            if (bytes[index] == byte) {
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
    fun indexOf(bytes: ByteArray, fromIndex: Int, toIndex: Int): Int {
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

    fun get(position: Int): Byte = bytes[position]
}

// https://developers.google.com/speed/webp/docs/riff_container
private val WEBP_HEADER_RIFF = "RIFF".toByteArray()
private val WEBP_HEADER_WEBP = "WEBP".toByteArray()
private val WEBP_HEADER_VP8X = "VP8X".toByteArray()

// https://nokiatech.github.io/heif/technical.html
private val HEIF_HEADER_FTYP = "ftyp".toByteArray()
private val HEIF_HEADER_MSF1 = "msf1".toByteArray()
private val HEIF_HEADER_HEVC = "hevc".toByteArray()
private val HEIF_HEADER_HEVX = "hevx".toByteArray()

// https://www.matthewflickinger.com/lab/whatsinagif/bits_and_bytes.asp
private val GIF_HEADER_87A = "GIF87a".toByteArray()
private val GIF_HEADER_89A = "GIF89a".toByteArray()

/**
 * Return 'true' if the [HeaderBytes] contains a WebP image.
 */
fun HeaderBytes.isWebP(): Boolean =
    rangeEquals(0, WEBP_HEADER_RIFF) && rangeEquals(8, WEBP_HEADER_WEBP)

/**
 * Return 'true' if the [HeaderBytes] contains an animated WebP image.
 */
fun HeaderBytes.isAnimatedWebP(): Boolean =
    isWebP() && rangeEquals(12, WEBP_HEADER_VP8X) && (get(16) and 0b00000010) > 0

/**
 * Return 'true' if the [HeaderBytes] contains an HEIF image. The [HeaderBytes] is not consumed.
 */
fun HeaderBytes.isHeif(): Boolean = rangeEquals(4, HEIF_HEADER_FTYP)

/**
 * Return 'true' if the [HeaderBytes] contains an animated HEIF image sequence.
 */
fun HeaderBytes.isAnimatedHeif(): Boolean = isHeif()
        && (rangeEquals(8, HEIF_HEADER_MSF1)
        || rangeEquals(8, HEIF_HEADER_HEVC)
        || rangeEquals(8, HEIF_HEADER_HEVX))

/**
 * Return 'true' if the [HeaderBytes] contains a GIF image.
 */
fun HeaderBytes.isGif(): Boolean =
    rangeEquals(0, GIF_HEADER_89A) || rangeEquals(0, GIF_HEADER_87A)