package com.github.panpf.sketch.util

import kotlin.math.min

// https://www.matthewflickinger.com/lab/whatsinagif/bits_and_bytes.asp
private val GIF_HEADER_87A = "GIF87a".encodeToByteArray()
private val GIF_HEADER_89A = "GIF89a".encodeToByteArray()

// https://developers.google.com/speed/webp/docs/riff_container
private val WEBP_HEADER_RIFF = "RIFF".encodeToByteArray()
private val WEBP_HEADER_WEBP = "WEBP".encodeToByteArray()
private val WEBP_HEADER_VP8 = "VP8 ".encodeToByteArray()    // Statics WEBP image
private val WEBP_HEADER_VP8L = "VP8L".encodeToByteArray()   // Statics WEBP image
private val WEBP_HEADER_VP8X = "VP8X".encodeToByteArray()   // Statics or Animated WEBP image
private val WEBP_HEADER_ANIM = "ANIM".encodeToByteArray()

private val ISOMEDIA_HEADER_FTYP = "ftyp".encodeToByteArray()
private val ISOMEDIA_BRAND_MIF1 = "mif1".encodeToByteArray()    // Statics image container
private val ISOMEDIA_BRAND_MSF1 = "msf1".encodeToByteArray()    // Animated image container

// https://nokiatech.github.io/heif/technical.html
private val HEIF_BRAND_HEIC = "heic".encodeToByteArray()    // Statics HEIF image
private val HEIF_BRAND_HEIX = "heix".encodeToByteArray()    // Statics HEIF image
private val HEIF_BRAND_HEVC = "hevc".encodeToByteArray()    // Animated HEIF image
private val HEIF_BRAND_HEVX = "hevx".encodeToByteArray()    // Animated HEIF image

private val AVIF_BRAND_AVIF = "avif".encodeToByteArray()    // Statics AVIF image
private val AVIF_BRAND_AVIS = "avis".encodeToByteArray()    // Animated AVIF image

private val SVG_TAG = "<svg ".encodeToByteArray()
private val LEFT_ANGLE_BRACKET = "<".encodeToByteArray()

private val BMP_HEADER = "BM".encodeToByteArray()
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
    headerBytes.rangeEquals2(offset = 0, bytes = GIF_HEADER_89A)
            || headerBytes.rangeEquals2(offset = 0, bytes = GIF_HEADER_87A)


/**
 * Return 'true' if the [ByteArray] contains a WebP image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testWebP
 */
fun isWebPFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals2(offset = 0, bytes = WEBP_HEADER_RIFF)
            && headerBytes.rangeEquals2(offset = 8, bytes = WEBP_HEADER_WEBP)

/**
 * Return 'true' if the [ByteArray] contains a WebP image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testWebP
 */
fun isStaticsWebPFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals2(offset = 0, bytes = WEBP_HEADER_RIFF)
            && headerBytes.rangeEquals2(offset = 8, bytes = WEBP_HEADER_WEBP)
            && (headerBytes.rangeEquals2(offset = 12, bytes = WEBP_HEADER_VP8)
            || headerBytes.rangeEquals2(offset = 12, bytes = WEBP_HEADER_VP8L)
            || (headerBytes.rangeEquals2(
        offset = 12,
        bytes = WEBP_HEADER_VP8X
    ) && !containsAnimatedWebpFlag(headerBytes)))

/**
 * Return 'true' if the [ByteArray] contains an animated WebP image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testWebP
 */
fun isAnimatedWebPFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals2(0, WEBP_HEADER_RIFF)
            && headerBytes.rangeEquals2(offset = 8, bytes = WEBP_HEADER_WEBP)
            && headerBytes.rangeEquals2(offset = 12, bytes = WEBP_HEADER_VP8X)
            && containsAnimatedWebpFlag(headerBytes)

private fun containsAnimatedWebpFlag(headerBytes: ByteArray): Boolean {
    return (headerBytes[16] and 0b00000010) > 0
            // Some webp images do not comply with standard protocols, obviously not GIFs but have GIF markup, here to do a fault tolerance
            // The VP8X block is fixed at 9 bytes, plus the first 16 bytes, for a total of 25 bytes, so an anim block can only start at 25
            && containsRiffAnimChunk(headerBytes, offset = 25)
}

private fun containsRiffAnimChunk(headerBytes: ByteArray, offset: Int = 0): Boolean {
    (offset until headerBytes.size - WEBP_HEADER_ANIM.size).forEach {
        if (headerBytes.rangeEquals2(offset = it, bytes = WEBP_HEADER_ANIM)) {
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
fun isHeifFile(headerBytes: ByteArray): Boolean = when {
    !headerBytes.rangeEquals2(offset = 4, bytes = ISOMEDIA_HEADER_FTYP) -> false

    headerBytes.rangeEquals2(offset = 8, bytes = HEIF_BRAND_HEIC) -> true
    headerBytes.rangeEquals2(offset = 8, bytes = HEIF_BRAND_HEIX) -> true
    headerBytes.rangeEquals2(offset = 8, bytes = HEIF_BRAND_HEVC) -> true
    headerBytes.rangeEquals2(offset = 8, bytes = HEIF_BRAND_HEVX) -> true

    headerBytes.rangeEquals2(offset = 8, bytes = ISOMEDIA_BRAND_MIF1) -> {
        val boxLength = readIsoMediaFileTypeBoxLength(headerBytes = headerBytes, offset = 0).toInt()
        // @formatter:off
        headerBytes.slidingRangeEquals2(offset = 12, step = 4, brand = HEIF_BRAND_HEIC, end = boxLength)
            || headerBytes.slidingRangeEquals2(offset = 12, step = 4, brand = HEIF_BRAND_HEIX, end = boxLength)
        // @formatter:on
    }

    headerBytes.rangeEquals2(offset = 8, bytes = ISOMEDIA_BRAND_MSF1) -> {
        val boxLength = readIsoMediaFileTypeBoxLength(headerBytes = headerBytes, offset = 0).toInt()
        // @formatter:off
        headerBytes.slidingRangeEquals2(offset = 12, step = 4,  brand = HEIF_BRAND_HEIC, end = boxLength)
            || headerBytes.slidingRangeEquals2(offset = 12, step = 4,  brand = HEIF_BRAND_HEIX, end = boxLength)
            || headerBytes.slidingRangeEquals2(offset = 12, step = 4,  brand = HEIF_BRAND_HEVC, end = boxLength)
            || headerBytes.slidingRangeEquals2(offset = 12, step = 4,  brand = HEIF_BRAND_HEVX, end = boxLength)
        // @formatter:on
    }

    else -> false
}

/**
 * Return 'true' if the [ByteArray] contains an statics HEIF image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testHeif
 */
fun isStaticsHeifFile(headerBytes: ByteArray): Boolean = when {
    !headerBytes.rangeEquals2(offset = 4, bytes = ISOMEDIA_HEADER_FTYP) -> false

    headerBytes.rangeEquals2(offset = 8, bytes = HEIF_BRAND_HEIC) -> true
    headerBytes.rangeEquals2(offset = 8, bytes = HEIF_BRAND_HEIX) -> true

    headerBytes.rangeEquals2(offset = 8, bytes = ISOMEDIA_BRAND_MIF1) -> {
        val boxLength = readIsoMediaFileTypeBoxLength(headerBytes, offset = 0).toInt()
        // @formatter:off
        headerBytes.slidingRangeEquals2(offset = 12, step = 4, brand = HEIF_BRAND_HEIC, end = boxLength)
            || headerBytes.slidingRangeEquals2(offset = 12, step = 4, brand = HEIF_BRAND_HEIX, end = boxLength)
        // @formatter:on
    }

    headerBytes.rangeEquals2(offset = 8, bytes = ISOMEDIA_BRAND_MSF1) -> {
        val boxLength = readIsoMediaFileTypeBoxLength(headerBytes, offset = 0).toInt()
        // @formatter:off
        val hasStaticBrand = headerBytes.slidingRangeEquals2(offset = 12, step = 4, brand = HEIF_BRAND_HEIC, end = boxLength)
                || headerBytes.slidingRangeEquals2(offset = 12, step = 4, HEIF_BRAND_HEIX, end = boxLength)
        val noAnimatedBrand = !headerBytes.slidingRangeEquals2(offset = 12, step = 4, brand = HEIF_BRAND_HEVC, end = boxLength)
                && !headerBytes.slidingRangeEquals2(offset = 12, step = 4, HEIF_BRAND_HEVX, end = boxLength)
        // @formatter:on
        hasStaticBrand && noAnimatedBrand
    }

    else -> false
}

/**
 * Return 'true' if the [ByteArray] contains an animated HEIF image sequence.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testHeif
 */
fun isAnimatedHeifFile(headerBytes: ByteArray): Boolean = when {
    !headerBytes.rangeEquals2(offset = 4, bytes = ISOMEDIA_HEADER_FTYP) -> false

    headerBytes.rangeEquals2(offset = 8, bytes = HEIF_BRAND_HEVC) -> true
    headerBytes.rangeEquals2(offset = 8, bytes = HEIF_BRAND_HEVX) -> true

    headerBytes.rangeEquals2(offset = 8, bytes = ISOMEDIA_BRAND_MSF1) -> {
        val boxLength = readIsoMediaFileTypeBoxLength(headerBytes, offset = 0).toInt()
        // @formatter:off
        headerBytes.slidingRangeEquals2(offset = 12, step = 4, brand = HEIF_BRAND_HEVC, end = boxLength)
            || headerBytes.slidingRangeEquals2(offset = 12, step = 4, brand = HEIF_BRAND_HEVX, end = boxLength)
        // @formatter:on
    }

    else -> false
}


/**
 * Return 'true' if the [ByteArray] contains an statics or animated AVIF image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testAvif
 */
fun isAvifFile(headerBytes: ByteArray): Boolean = when {
    !headerBytes.rangeEquals2(offset = 4, bytes = ISOMEDIA_HEADER_FTYP) -> false

    headerBytes.rangeEquals2(offset = 8, bytes = AVIF_BRAND_AVIF) -> true
    headerBytes.rangeEquals2(offset = 8, bytes = AVIF_BRAND_AVIS) -> true

    headerBytes.rangeEquals2(offset = 8, bytes = ISOMEDIA_BRAND_MIF1) -> {
        val boxLength = readIsoMediaFileTypeBoxLength(headerBytes = headerBytes, offset = 0).toInt()
        // @formatter:off
        headerBytes.slidingRangeEquals2(offset = 12, step = 4, brand = AVIF_BRAND_AVIF, end = boxLength)
        // @formatter:on
    }

    headerBytes.rangeEquals2(offset = 8, bytes = ISOMEDIA_BRAND_MSF1) -> {
        val boxLength = readIsoMediaFileTypeBoxLength(headerBytes = headerBytes, offset = 0).toInt()
        // @formatter:off
        headerBytes.slidingRangeEquals2(offset = 12, step = 4,  brand = AVIF_BRAND_AVIF, end = boxLength)
            || headerBytes.slidingRangeEquals2(offset = 12, step = 4,  brand = AVIF_BRAND_AVIS, end = boxLength)
        // @formatter:on
    }

    else -> false
}

/**
 * Return 'true' if the [ByteArray] contains an statics AVIF image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testAvif
 */
fun isStaticsAvifFile(headerBytes: ByteArray): Boolean = when {
    !headerBytes.rangeEquals2(offset = 4, bytes = ISOMEDIA_HEADER_FTYP) -> false

    headerBytes.rangeEquals2(offset = 8, bytes = AVIF_BRAND_AVIF) -> true

    headerBytes.rangeEquals2(offset = 8, bytes = ISOMEDIA_BRAND_MIF1) -> {
        val boxLength = readIsoMediaFileTypeBoxLength(headerBytes, offset = 0).toInt()
        // @formatter:off
        headerBytes.slidingRangeEquals2(offset = 12, step = 4, brand = AVIF_BRAND_AVIF, end = boxLength)
        // @formatter:on
    }

    headerBytes.rangeEquals2(offset = 8, bytes = ISOMEDIA_BRAND_MSF1) -> {
        val boxLength = readIsoMediaFileTypeBoxLength(headerBytes, offset = 0).toInt()
        // @formatter:off
        val hasStaticBrand = headerBytes.slidingRangeEquals2(offset = 12, step = 4, brand = AVIF_BRAND_AVIF, end = boxLength)
        val noAnimatedBrand = !headerBytes.slidingRangeEquals2(offset = 12, step = 4, brand = AVIF_BRAND_AVIS, end = boxLength)
        // @formatter:on
        hasStaticBrand && noAnimatedBrand
    }

    else -> false
}

/**
 * Return 'true' if the [ByteArray] contains an animated AVIF image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testAvif
 */
fun isAnimatedAvifFile(headerBytes: ByteArray): Boolean = when {
    !headerBytes.rangeEquals2(offset = 4, bytes = ISOMEDIA_HEADER_FTYP) -> false

    headerBytes.rangeEquals2(offset = 8, bytes = AVIF_BRAND_AVIS) -> true

    headerBytes.rangeEquals2(offset = 8, bytes = ISOMEDIA_BRAND_MSF1) -> {
        val boxLength = readIsoMediaFileTypeBoxLength(headerBytes, offset = 0).toInt()
        // @formatter:off
        headerBytes.slidingRangeEquals2(offset = 12, step = 4, brand = AVIF_BRAND_AVIS, end = boxLength)
        // @formatter:on
    }

    else -> false
}


/**
 * Check if the data is an SVG image
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testSvg
 */
fun isSvgFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals2(0, LEFT_ANGLE_BRACKET)
            && headerBytes.indexOf2(SVG_TAG, 0, 1024) != -1

/**
 * Return 'true' if the [ByteArray] contains an animated BMP image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testBmp
 */
fun isBmpFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals2(offset = 0, bytes = BMP_HEADER)

/**
 * Return 'true' if the [ByteArray] contains an animated JPEG image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testJpeg
 */
fun isJpegFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals2(offset = 0, bytes = JPEG_HEADER)

/**
 * Return 'true' if the [ByteArray] contains an animated PNH image.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testPng
 */
fun isPngFile(headerBytes: ByteArray): Boolean =
    headerBytes.rangeEquals2(offset = 0, bytes = PNG_HEADER)


/**
 * When parsing MP4, HEIF, AVIF and other file formats based on ISO Base Media File Format,
 * the first 4 bytes represent the length of the box.
 * If the length is 1, it means that the length of the box exceeds 4GB,
 * and the actual length is stored in the next 8 bytes.
 *
 * @see com.github.panpf.sketch.core.common.test.util.FileTypesTest.testReadIsoMediaFileTypeBoxLength
 */
internal fun readIsoMediaFileTypeBoxLength(headerBytes: ByteArray, offset: Int = 0): Long {
    require(offset + 4 <= headerBytes.size) {
        "Invalid offset: $offset, headerBytes size: ${headerBytes.size}"
    }
    val intLength = ((headerBytes[offset].toInt() and 0xFF) shl 24) or
            ((headerBytes[offset + 1].toInt() and 0xFF) shl 16) or
            ((headerBytes[offset + 2].toInt() and 0xFF) shl 8) or
            (headerBytes[offset + 3].toInt() and 0xFF)
    // 0 means that the box extends to the end of the file, and the actual length is the size of the file minus the offset
    if (intLength != 1) {
        return intLength.toLong()
    }

    // intLength is 1 the box is larger than 4GB, and the actual length is stored in the next 8 bytes
    require(offset + 8 <= headerBytes.size) {
        "Invalid offset: $offset, headerBytes size: ${headerBytes.size}"
    }
    val longOffset = offset + 4
    val longLength = ((headerBytes[longOffset].toLong() and 0xFF) shl 56) or
            ((headerBytes[longOffset + 1].toLong() and 0xFF) shl 48) or
            ((headerBytes[longOffset + 2].toLong() and 0xFF) shl 40) or
            ((headerBytes[longOffset + 3].toLong() and 0xFF) shl 32) or
            ((headerBytes[longOffset + 4].toLong() and 0xFF) shl 24) or
            ((headerBytes[longOffset + 5].toLong() and 0xFF) shl 16) or
            ((headerBytes[longOffset + 6].toLong() and 0xFF) shl 8) or
            (headerBytes[longOffset + 7].toLong() and 0xFF)
    return longLength
}

/**
 * Returns `true` if the specified range in this byte array is equal to the specified byte array.
 */
private fun ByteArray.rangeEquals2(offset: Int, bytes: ByteArray, end: Int = 0): Boolean {
    require(bytes.isNotEmpty()) { "bytes is empty" }

    var index = 0
    var result = false
    val finalEnd = if (end > 0) min(this.size, end) else this.size
    while (index < bytes.size && (index + offset) < finalEnd) {
        result = bytes[index] == this[offset + index]
        if (!result) {
            return false
        } else {
            index++
        }
    }
    return result
}

@Suppress("SameParameterValue")
private fun ByteArray.slidingRangeEquals2(
    offset: Int,
    step: Int,
    brand: ByteArray,
    end: Int = 0,
): Boolean {
    var index = offset
    while (true) {
        if (this@slidingRangeEquals2.rangeEquals2(offset = index, bytes = brand, end = end)) {
            return true
        } else {
            index += step
            if (index >= this@slidingRangeEquals2.size) {
                return false
            }
        }
    }
}

/**
 * Returns the index within this byte array of the first occurrence of the specified byte array, starting from the specified index.
 *
 * @param fromIndex   the begin index, inclusive.
 * @param toIndex     the end index, exclusive.
 */
private fun ByteArray.indexOf2(bytes: ByteArray, fromIndex: Int, toIndex: Int): Int {
    require(fromIndex in 0L..toIndex) { "fromIndex=$fromIndex toIndex=$toIndex" }
    require(bytes.isNotEmpty()) { "bytes is empty" }

    val firstByte = bytes[0]
    val lastIndex = toIndex + 1 - bytes.size
    var currentIndex = fromIndex
    while (currentIndex < lastIndex) {
        currentIndex = indexOf2(firstByte, currentIndex, lastIndex)
        if (currentIndex == -1 || rangeEquals2(currentIndex, bytes)) {
            return currentIndex
        }
        currentIndex++
    }
    return -1
}

/**
 * Returns the index within this byte array of the first occurrence of the specified byte, starting from the specified index.
 *
 * @param fromIndex   the begin index, inclusive.
 * @param toIndex     the end index, inclusive.
 */
private fun ByteArray.indexOf2(byte: Byte, fromIndex: Int, toIndex: Int): Int {
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

private inline infix fun Byte.and(other: Byte): Byte = (this.toInt() and other.toInt()).toByte()