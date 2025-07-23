package com.github.panpf.sketch.fetch

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

object BlurhashUtil {

    private val BASE83_REGEX = Regex("^[0-9A-Za-z#\\$%*+,\\-.:;=?@\\[\\]^_{|}~]+$")
    private const val CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz#$%*+,-.:;=?@[]^_{|}~"
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

    fun decodeByte(blurHash: String, width: Int, height: Int, punch: Float = 1f): ByteArray {
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

        return composeBitmapAsByteArray(width, height, numCompX, numCompY, colors)
    }

    private inline fun decode83(s: String, from: Int, to: Int): Int {
        var acc = 0
        for (p in from until to) acc = acc * 83 + CHAR_TO_CODE[s[p].code]
        return acc
    }

    private fun decodeDc(colorEnc: Int, out: FloatArray) {
        out[0] = srgbToLinear((colorEnc shr 16) and 0xFF)
        out[1] = srgbToLinear((colorEnc shr 8) and 0xFF)
        out[2] = srgbToLinear(colorEnc and 0xFF)
    }

    private inline fun srgbToLinear(enc: Int) = SRGB_TO_LINEAR[enc]
    private inline fun linearToSrgb(v: Float) = LINEAR_TO_SRGB[(v.coerceIn(0f, 1f) * 4095f).toInt()]

    private fun decodeAc(value: Int, maxAc: Float, out: FloatArray, outIndex: Int) {
        val oneNinth = 1f / 9f
        val r = value / (19 * 19)
        val g = (value / 19) % 19
        val b = value % 19

        out[outIndex] = signedPow2((r - 9) * oneNinth) * maxAc
        out[outIndex + 1] = signedPow2((g - 9) * oneNinth) * maxAc
        out[outIndex + 2] = signedPow2((b - 9) * oneNinth) * maxAc
    }

    private fun signedPow2(v: Float) = if (v < 0) -(v * v) else v * v

    private fun composeBitmapAsByteArray(
        width: Int,
        height: Int,
        numCompX: Int,
        numCompY: Int,
        colors: FloatArray
    ): ByteArray {
        val output = ByteArray(width * height * 4)
        val cosinesX = createCosines(width, numCompX)
        val cosinesY = if (width == height && numCompX == numCompY) cosinesX else createCosines(height, numCompY)

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