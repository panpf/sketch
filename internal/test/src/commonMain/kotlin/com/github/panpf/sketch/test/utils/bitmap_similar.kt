package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.util.readIntPixel
import com.github.panpf.sketch.util.thumbnail
import kotlin.math.pow

/*
 * copy from https://github.com/zangliguang/ImageSimilar/blob/master/app/src/main/java/tool/ImageHelper.java
 */

/**
 * Compare the similarity of two images.
 *  A result less than 5 means the two pictures are very similar;
 *  a result greater than 10 means they are two different pictures.
 */
fun Bitmap.similarity(other: Bitmap): Int {
    return hammingDistance(
        fingerPrint = this.produceFingerPrint(),
        otherFingerPrint = other.produceFingerPrint()
    )
}

/**
 * Generate image fingerprint. Then you can calculate the similarity using the [hammingDistance] function
 */
fun Bitmap.produceFingerPrint(): String {
    val source: Bitmap = this
    val thumbnailWidth = 8
    val thumbnailHeight = 8

    // Step one, downsize.
    //  Reduce the image to 8x8 size, a total of 64 pixels.
    //  The purpose of this step is to remove the details of the picture,
    //  retain only basic information such as structure, light and shade,
    //  and discard the differences in pictures caused by different sizes and proportions.
    val thumbnail = source.thumbnail(thumbnailWidth, thumbnailHeight)

    // The second step is to simplify the colors.
    //  Convert the reduced image to 64 levels of grayscale.
    //  In other words, there are only 64 colors in total for all pixels.
    val pixels = IntArray(thumbnailWidth * thumbnailHeight)
    for (i in 0 until thumbnailWidth) {
        for (j in 0 until thumbnailHeight) {
            pixels[i * thumbnailHeight + j] = argbToGray(thumbnail.readIntPixel(i, j))
        }
    }

    // The third step is to calculate the average.
    //  Calculate the grayscale average of all 64 pixels.
    val avgPixel = average(pixels)

    // The fourth step is to compare the grayscale of pixels.
    //  Compare the gray level of each pixel with the average value.
    //  If it is greater than or equal to the average, it will be recorded as 1;
    //  if it is less than the average, it will be recorded as 0.
    val comps = IntArray(thumbnailWidth * thumbnailHeight)
    for (i in comps.indices) {
        if (pixels[i] >= avgPixel) {
            comps[i] = 1
        } else {
            comps[i] = 0
        }
    }

    // The fifth step is to calculate the hash value.
    //  Combining the comparison results of the previous step together forms a 64-bit integer,
    //  which is the fingerprint of this image.
    //  The order of the combinations doesn't matter,
    //  just make sure that all the images are in the same order.
    val hashCode = StringBuilder()
    var i = 0
    while (i < comps.size) {
        val result = comps[i] * 2.0.pow(3.0).toInt() + (comps[i + 1]
                * 2.0.pow(2.0).toInt()) + (comps[i + 2]
                * 2.0.pow(1.0).toInt()) + comps[i + 3]
        hashCode.append(binaryToHex(result))
        i += 4
    }

    // After getting the fingerprint, you can compare different pictures to see how many of the 64 bits are different.
    return hashCode.toString()
}

/**
 * Calculate the "Hamming distance".
 * If the number of different data bits does not exceed 5, it means that the two pictures are very similar;
 * if it is greater than 10, it means that these are two different pictures.
 */
fun hammingDistance(fingerPrint: String, otherFingerPrint: String): Int {
    var difference = 0
    val len = fingerPrint.length
    for (i in 0 until len) {
        if (fingerPrint[i] != otherFingerPrint[i]) {
            difference++
        }
    }
    return difference
}

private fun argbToGray(pixel: Int): Int {
    val r = (pixel shr 16) and 0xFF
    val g = (pixel shr 8) and 0xFF
    val b = (pixel) and 0xFF
    return (0.3 * r + 0.59 * g + 0.11 * b).toInt()
}

private fun average(pixels: IntArray): Int {
    var m = 0f
    for (i in pixels.indices) {
        m += pixels[i].toFloat()
    }
    m /= pixels.size
    return m.toInt()
}

private fun binaryToHex(binary: Int): Char = when (binary) {
    0 -> '0'
    1 -> '1'
    2 -> '2'
    3 -> '3'
    4 -> '4'
    5 -> '5'
    6 -> '6'
    7 -> '7'
    8 -> '8'
    9 -> '9'
    10 -> 'a'
    11 -> 'b'
    12 -> 'c'
    13 -> 'd'
    14 -> 'e'
    15 -> 'f'
    else -> ' '
}