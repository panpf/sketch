package com.github.panpf.sketch.util

import okio.ByteString.Companion.encodeUtf8
import kotlin.experimental.and

// https://www.matthewflickinger.com/lab/whatsinagif/bits_and_bytes.asp
private val GIF_HEADER_87A = "GIF87a".encodeUtf8().toByteArray()
private val GIF_HEADER_89A = "GIF89a".encodeUtf8().toByteArray()

// https://developers.google.com/speed/webp/docs/riff_container
private val WEBP_HEADER_RIFF = "RIFF".encodeUtf8().toByteArray()
private val WEBP_HEADER_WEBP = "WEBP".encodeUtf8().toByteArray()
private val WEBP_HEADER_VP8 = "VP8 ".encodeUtf8().toByteArray()    // Static WEBP image
private val WEBP_HEADER_VP8L = "VP8L".encodeUtf8().toByteArray()    // Static WEBP image
private val WEBP_HEADER_VP8X = "VP8X".encodeUtf8().toByteArray()    // Static or Animated WEBP image
private val WEBP_HEADER_ANIM = "ANIM".encodeUtf8().toByteArray()

// https://nokiatech.github.io/heif/technical.html
private val HEIF_HEADER_FTYP = "ftyp".encodeUtf8().toByteArray()
private val HEIF_HEADER_MIF1 = "mif1".encodeUtf8().toByteArray()    // Static HEIF image
private val HEIF_HEADER_HEIC = "heic".encodeUtf8().toByteArray()    // Static HEIF image
private val HEIF_HEADER_HEIX = "heix".encodeUtf8().toByteArray()    // Static HEIF image
private val HEIF_HEADER_MSF1 = "msf1".encodeUtf8().toByteArray()    // Animated HEIF image
private val HEIF_HEADER_HEVC = "hevc".encodeUtf8().toByteArray()    // Animated HEIF image
private val HEIF_HEADER_HEVX = "hevx".encodeUtf8().toByteArray()    // Animated HEIF image

private val AVIF_HEADER_FTYP = "ftyp".encodeUtf8().toByteArray()
private val AVIF_HEADER_AVIF = "avif".encodeUtf8().toByteArray()    // Static AVIF image
private val AVIF_HEADER_AVIS = "avis".encodeUtf8().toByteArray()    // Animated AVIF image

private val SVG_TAG = "<svg ".encodeUtf8().toByteArray()
private val LEFT_ANGLE_BRACKET = "<".encodeUtf8().toByteArray()

private val BMP_HEADER = "BM".encodeUtf8().toByteArray()
private val JPEG_HEADER = byteArrayOf(0xFF.toByte(), 0xD8.toByte())
private val PNG_HEADER = byteArrayOf(
    0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(), // invisible characters,'P','N','G'
    0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte()  // '\r','\n',Ctrl+Z,'\n'
)

/**
 * Return 'true' if the [ByteArray] contains a GIF image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testGif
 */
fun isGifFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals(offset = 0, bytes = GIF_HEADER_89A)
            || headerBytes.rangeEquals(offset = 0, bytes = GIF_HEADER_87A)


/**
 * Return 'true' if the [ByteArray] contains a WebP image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testWebP
 */
fun isWebPFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals(offset = 0, bytes = WEBP_HEADER_RIFF)
            && headerBytes.rangeEquals(offset = 8, bytes = WEBP_HEADER_WEBP)

/**
 * Return 'true' if the [ByteArray] contains a WebP image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testWebP
 */
fun isStaticsWebPFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals(offset = 0, bytes = WEBP_HEADER_RIFF)
            && headerBytes.rangeEquals(offset = 8, bytes = WEBP_HEADER_WEBP)
            && (headerBytes.rangeEquals(offset = 12, bytes = WEBP_HEADER_VP8)
            || headerBytes.rangeEquals(offset = 12, bytes = WEBP_HEADER_VP8L)
            || (headerBytes.rangeEquals(
        offset = 12,
        bytes = WEBP_HEADER_VP8X
    ) && !containsAnimatedWebpFlag(headerBytes)))

/**
 * Return 'true' if the [ByteArray] contains an animated WebP image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testWebP
 */
fun isAnimatedWebPFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals(0, WEBP_HEADER_RIFF)
            && headerBytes.rangeEquals(offset = 8, bytes = WEBP_HEADER_WEBP)
            && headerBytes.rangeEquals(offset = 12, bytes = WEBP_HEADER_VP8X)
            && containsAnimatedWebpFlag(headerBytes)

private fun containsAnimatedWebpFlag(headerBytes: ByteArray): Boolean {
    return (headerBytes[16] and 0b00000010) > 0
            // Some webp images do not comply with standard protocols, obviously not GIFs but have GIF markup, here to do a fault tolerance
            // The VP8X block is fixed at 9 bytes, plus the first 16 bytes, for a total of 25 bytes, so an anim block can only start at 25
            && containsRiffAnimChunk(headerBytes, offset = 25)
}

private fun containsRiffAnimChunk(headerBytes: ByteArray, offset: Int = 0): Boolean {
    (offset until headerBytes.size - WEBP_HEADER_ANIM.size).forEach {
        if (headerBytes.rangeEquals(offset = it, bytes = WEBP_HEADER_ANIM)) {
            return true
        }
    }
    return false
}


/**
 * Return 'true' if the [ByteArray] contains an statics or animated HEIF image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testHeif
 */
fun isHeifFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals(offset = 4, bytes = HEIF_HEADER_FTYP)
            && (headerBytes.rangeEquals(offset = 8, bytes = HEIF_HEADER_MIF1)
            || headerBytes.rangeEquals(offset = 8, bytes = HEIF_HEADER_HEIC)
            || headerBytes.rangeEquals(offset = 8, bytes = HEIF_HEADER_HEIX)
            || headerBytes.rangeEquals(offset = 8, bytes = HEIF_HEADER_MSF1)
            || headerBytes.rangeEquals(offset = 8, bytes = HEIF_HEADER_HEVC)
            || headerBytes.rangeEquals(offset = 8, bytes = HEIF_HEADER_HEVX))

/**
 * Return 'true' if the [ByteArray] contains an statics HEIF image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testHeif
 */
fun isStaticsHeifFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals(offset = 4, bytes = HEIF_HEADER_FTYP)
            && (headerBytes.rangeEquals(offset = 8, bytes = HEIF_HEADER_MIF1)
            || headerBytes.rangeEquals(offset = 8, bytes = HEIF_HEADER_HEIC)
            || headerBytes.rangeEquals(offset = 8, bytes = HEIF_HEADER_HEIX))

/**
 * Return 'true' if the [ByteArray] contains an animated HEIF image sequence.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testHeif
 */
fun isAnimatedHeifFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals(offset = 4, bytes = HEIF_HEADER_FTYP)
            && (headerBytes.rangeEquals(offset = 8, bytes = HEIF_HEADER_MSF1)
            || headerBytes.rangeEquals(offset = 8, bytes = HEIF_HEADER_HEVC)
            || headerBytes.rangeEquals(offset = 8, bytes = HEIF_HEADER_HEVX))


/**
 * Return 'true' if the [ByteArray] contains an statics or animated AVIF image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testAvif
 */
fun isAvifFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals(offset = 4, bytes = AVIF_HEADER_FTYP)
            && (headerBytes.rangeEquals(offset = 8, bytes = AVIF_HEADER_AVIF)
            || headerBytes.rangeEquals(offset = 8, bytes = AVIF_HEADER_AVIS))

/**
 * Return 'true' if the [ByteArray] contains an statics AVIF image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testAvif
 */
fun isStaticsAvifFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals(offset = 4, bytes = AVIF_HEADER_FTYP)
            && headerBytes.rangeEquals(offset = 8, bytes = AVIF_HEADER_AVIF)

/**
 * Return 'true' if the [ByteArray] contains an animated AVIF image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testAvif
 */
fun isAnimatedAvifFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals(offset = 4, bytes = AVIF_HEADER_FTYP)
            && headerBytes.rangeEquals(offset = 8, bytes = AVIF_HEADER_AVIS)


/**
 * Check if the data is an SVG image
 *
 * @see com.github.panpf.sketch.svg.common.test.decode.internal.SvgsTest.testSvg
 */
fun isSvgFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals(0, LEFT_ANGLE_BRACKET)
            && headerBytes.indexOf(SVG_TAG, 0, 1024) != -1

/**
 * Return 'true' if the [ByteArray] contains an animated BMP image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testBmp
 */
fun isBmpFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals(offset = 0, bytes = BMP_HEADER)

/**
 * Return 'true' if the [ByteArray] contains an animated JPEG image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testJpeg
 */
fun isJpegFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals(offset = 0, bytes = JPEG_HEADER)

/**
 * Return 'true' if the [ByteArray] contains an animated PNH image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testPng
 */
fun isPngFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals(offset = 0, bytes = PNG_HEADER)