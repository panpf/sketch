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

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.fetch.newBlurHashUri
import com.github.panpf.sketch.fetch.readSizeFromBlurHashUri
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

/**
 * Default size for decoding BlurHash bitmap.
 *
 * @see com.github.panpf.sketch.blurhash.common.test.util.BlurHashUtilCommonTest.testDefaultBlurHashBitmapSize
 */
val defaultBlurHashBitmapSize = Size(100, 100)

/**
 * Resolve the size of the bitmap to decode the BlurHash.
 *
 * @see com.github.panpf.sketch.blurhash.common.test.util.BlurHashUtilCommonTest.testResolveBlurHashBitmapSize
 */
fun resolveBlurHashBitmapSize(blurHashUri: Uri?, size: Size?): Size {
    var result = size
    if (result != null && result.isNotEmpty) {
        return result
    }

    result = blurHashUri?.let { readSizeFromBlurHashUri(it) }
    if (result != null && result.isNotEmpty) {
        return result
    }

    return defaultBlurHashBitmapSize
}

/**
 * Create a memory cache key for the BlurHash. The uri format is not supported in the blurHash here
 *
 * @see com.github.panpf.sketch.blurhash.common.test.util.BlurHashUtilCommonTest.testBlurHashMemoryCacheKey
 */
fun blurHashMemoryCacheKey(blurHash: String, size: Size): String = newBlurHashUri(blurHash, size)

/**
 * Create a [Bitmap] for decoding BlurHash.
 *
 * @see com.github.panpf.sketch.blurhash.android.test.util.BlurHashUtilAndroidTest.testCreateBlurHashBitmap
 * @see com.github.panpf.sketch.blurhash.nonandroid.test.util.BlurHashUtilNonAndroidTest.testCreateBlurHashBitmap
 */
expect fun createBlurHashBitmap(width: Int, height: Int, decodeConfig: DecodeConfig? = null): Bitmap

/**
 * https://github.com/cbeyls/BlurHashAndroidBenchmark/
 */
object BlurHashUtil {

    private val BASE83_REGEX = Regex("^[0-9A-Za-z#\\$%*+,\\-.:;=?@\\[\\]^_{|}~]+$")
    private const val CHARS =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz#$%*+,-.:;=?@[]^_{|}~"
    private val CHAR_TO_CODE = IntArray(128) { -1 }.also { table ->
        CHARS.forEachIndexed { i, c -> table[c.code] = i }
    }

    private val SRGB_TO_LINEAR = FloatArray(256) { i ->
        val v = i / 255f
        if (v <= 0.04045f) v / 12.92f
        else ((v + 0.055f) / 1.055f).pow(2.4f)
    }

    private val LINEAR_TO_SRGB = IntArray(4096) { i ->
        val v = i / 4095f
        if (v <= 0.0031308f) (v * 12.92f * 255f + .5f).toInt()
        else ((1.055f * v.pow(1 / 2.4f) - 0.055f) * 255f + .5f).toInt()
    }

    fun decodeByte(
        blurHash: String,
        width: Int,
        height: Int,
        punch: Float = 1f
    ): ByteArray {
        val numCompEnc = decode83(blurHash, 0, 1)
        val numCompX = (numCompEnc % 9) + 1
        val numCompY = (numCompEnc / 9) + 1
        val totalComp = numCompX * numCompY

        val maxAc = (decode83(blurHash, 1, 2) + 1) / 166f
        val colors = FloatArray(totalComp * 3)

        decodeDc(decode83(blurHash, 2, 6), colors)

        for (i in 1 until totalComp) {
            val index = 4 + i * 2
            decodeAc(decode83(blurHash, index, index + 2), maxAc * punch, colors, i * 3)
        }

        return composeBitmapAsByteArray(
            width,
            height,
            numCompX,
            numCompY,
            colors,
        )
    }

    private inline fun decode83(str: String, from: Int, to: Int): Int {
        var acc = 0
        for (p in from until to) acc = acc * 83 + CHAR_TO_CODE[str[p].code]
        return acc
    }

    private fun decodeDc(colorEnc: Int, out: FloatArray) {
        out[0] = srgbToLinear((colorEnc shr 16) and 0xFF)
        out[1] = srgbToLinear((colorEnc shr 8) and 0xFF)
        out[2] = srgbToLinear(colorEnc and 0xFF)
    }

    private inline fun srgbToLinear(enc: Int) = SRGB_TO_LINEAR[enc]

    private fun decodeAc(value: Int, maxAc: Float, out: FloatArray, outIndex: Int) {
        val oneNinth = 1f / 9f
        val r = value / (19 * 19)
        val g = (value / 19) % 19
        val b = value % 19

        out[outIndex] = signedPow2((r - 9) * oneNinth) * maxAc
        out[outIndex + 1] = signedPow2((g - 9) * oneNinth) * maxAc
        out[outIndex + 2] = signedPow2((b - 9) * oneNinth) * maxAc
    }

    private fun signedPow2(value: Float) = if (value < 0) -(value * value) else value * value

    private fun composeBitmapAsByteArray(
        width: Int,
        height: Int,
        numCompX: Int,
        numCompY: Int,
        colors: FloatArray,
    ): ByteArray {
        val output = ByteArray(width * height * 4)
        val cosinesX = createCosines(width, numCompX)
        val cosinesY = if (width == height && numCompX == numCompY) cosinesX else createCosines(
            height,
            numCompY
        )

        var pixelIndex = 0

        for (y in 0 until height) {
            val yCompOffset = y * numCompY
            for (x in 0 until width) {
                val xCompOffset = x * numCompX
                var r = 0f
                var g = 0f
                var b = 0f
                for (j in 0 until numCompY) {

                    val cosY = cosinesY[yCompOffset + j]   // hoist index calc once
                    val baseIndex = j * numCompX * 3       // pre-mul for cache friendliness
                    for (i in 0 until numCompX) {
                        val basis = cosY * cosinesX[xCompOffset + i]
                        val cIdx = baseIndex + i * 3
                        r += colors[cIdx] * basis
                        g += colors[cIdx + 1] * basis
                        b += colors[cIdx + 2] * basis
                    }
                }

                val base = pixelIndex * 4
                output[base] = linearToSrgb(r).toByte()
                output[base + 1] = linearToSrgb(g).toByte()
                output[base + 2] = linearToSrgb(b).toByte()
                output[base + 3] = 255.toByte()
                pixelIndex++
            }
        }

        return output
    }

    private fun createCosines(size: Int, numComp: Int) = FloatArray(size * numComp) { index ->
        val x = index / numComp
        val i = index % numComp
        cos(PI * x * i / size).toFloat()
    }

    private inline fun linearToSrgb(v: Float) = LINEAR_TO_SRGB[(v.coerceIn(0f, 1f) * 4095f).toInt()]

    fun isValid(blurHash: String?): Boolean {
        if (blurHash == null || blurHash.length < 6) return false
        if (!BASE83_REGEX.matches(blurHash)) return false

        // Decode first char ⇒ size flag
        val sizeFlag = decode83(blurHash, 0, 1)
        if (sizeFlag < 0) return false

        val numX = sizeFlag % 9 + 1
        val numY = sizeFlag / 9 + 1

        // Verify total length
        val expectedLength = 4 + 2 * numX * numY
        return blurHash.length == expectedLength
    }
}