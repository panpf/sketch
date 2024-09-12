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

import com.github.panpf.sketch.util.PixelsConverter.ALPHA_8
import com.github.panpf.sketch.util.PixelsConverter.ARGB_4444
import com.github.panpf.sketch.util.PixelsConverter.BGRA_1010102
import com.github.panpf.sketch.util.PixelsConverter.BGRA_8888
import com.github.panpf.sketch.util.PixelsConverter.BGR_101010X
import com.github.panpf.sketch.util.PixelsConverter.GRAY_8
import com.github.panpf.sketch.util.PixelsConverter.RGBA_1010102
import com.github.panpf.sketch.util.PixelsConverter.RGBA_8888
import com.github.panpf.sketch.util.PixelsConverter.RGB_101010X
import com.github.panpf.sketch.util.PixelsConverter.RGB_565
import com.github.panpf.sketch.util.PixelsConverter.RGB_888X
import com.github.panpf.sketch.util.PixelsConverter.UNKNOWN
import okio.Buffer
import org.jetbrains.skia.ColorType

/**
 * Get the corresponding converter according to the color type
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.PixelsConverterTest.testPixelsConverter
 */
fun PixelsConverter(colorType: ColorType): PixelsConverter? = when (colorType) {
    ColorType.UNKNOWN -> UNKNOWN    // bytesPerPixel 0
    ColorType.ALPHA_8 -> ALPHA_8    // bytesPerPixel 1
    ColorType.RGB_565 -> RGB_565    // bytesPerPixel 2
    ColorType.ARGB_4444 -> ARGB_4444    // bytesPerPixel 2
    ColorType.RGBA_8888 -> RGBA_8888    // bytesPerPixel 4
    ColorType.BGRA_8888 -> BGRA_8888    // bytesPerPixel 4
    ColorType.RGB_888X -> RGB_888X    // bytesPerPixel 4
    ColorType.RGBA_1010102 -> RGBA_1010102    // bytesPerPixel 4
    ColorType.RGB_101010X -> RGB_101010X    // bytesPerPixel 4
    ColorType.BGRA_1010102 -> BGRA_1010102    // bytesPerPixel 4
    ColorType.BGR_101010X -> BGR_101010X    // bytesPerPixel 4
    ColorType.BGR_101010X_XR -> BGR_101010X    // bytesPerPixel 4
    ColorType.GRAY_8 -> GRAY_8    // bytesPerPixel 1
//                ColorType.RGBA_F16NORM -> RGBA_F16NORM    // bytesPerPixel 8  // TODO To be completed
//                ColorType.RGBA_F16 -> RGBA_F16    // bytesPerPixel 8
//                ColorType.RGBA_F32 -> RGBA_F32    // bytesPerPixel 16
//                ColorType.R8G8_UNORM -> R8G8_UNORM    // bytesPerPixel 2
//                ColorType.A16_UNORM -> A16_UNORM    // bytesPerPixel 2
//                ColorType.R16G16_UNORM -> R16G16_UNORM    // bytesPerPixel 4
//                ColorType.A16_FLOAT -> A16_FLOAT    // bytesPerPixel 2
//                ColorType.R16G16_FLOAT -> R16G16_FLOAT    // bytesPerPixel 4
//                ColorType.R16G16B16A16_UNORM -> R16G16B16A16_UNORM    // bytesPerPixel 8
    else -> null
}

/**
 * Convert pixels between byte array and int array
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.PixelsConverterTest
 */
sealed interface PixelsConverter {

    /**
     * Convert byte pixels to int pixels
     *
     * int pixels format: [a, r, g, b]
     */
    fun bytePixelsToIntPixels(bytePixels: ByteArray): IntArray

    /**
     * Convert int pixels to byte pixels
     *
     * int pixels format: [a, r, g, b]
     */
    fun intPixelsToBytePixels(intPixels: IntArray): ByteArray

    /**
     * @see com.github.panpf.sketch.core.nonandroid.test.util.PixelsConverterTest.testUNKNOWN
     */
    data object UNKNOWN : PixelsConverter {

        override fun bytePixelsToIntPixels(bytePixels: ByteArray): IntArray {
            return IntArray(0)
        }

        override fun intPixelsToBytePixels(intPixels: IntArray): ByteArray {
            return ByteArray(0)
        }
    }

    /**
     * Convert pixels between ALPHA_8 and ARGB_8888
     *
     * @see com.github.panpf.sketch.core.nonandroid.test.util.PixelsConverterTest.testALPHA_8
     */
    data object ALPHA_8 : PixelsConverter {

        override fun bytePixelsToIntPixels(bytePixels: ByteArray): IntArray {
            val intPixels = IntArray(bytePixels.size)
            for (i in intPixels.indices) {
                val a = bytePixels[i].toInt() and 0xFF
                intPixels[i] = a shl 24
            }
            return intPixels
        }

        override fun intPixelsToBytePixels(intPixels: IntArray): ByteArray {
            val bytePixels = ByteArray(intPixels.size)
            for (i in intPixels.indices) {
                val a = intPixels[i] shr 24
                bytePixels[i] = a.toByte()
            }
            return bytePixels
        }
    }

    /**
     * Convert pixels between RGB_565 and ARGB_8888
     *
     * @see com.github.panpf.sketch.core.nonandroid.test.util.PixelsConverterTest.testRGB_565
     */
    data object RGB_565 : PixelsConverter {

        override fun bytePixelsToIntPixels(bytePixels: ByteArray): IntArray {
            require(bytePixels.size % 2 == 0) {
                "The size of byte array must be a multiple of 2."
            }
            val intPixels = IntArray(bytePixels.size / 2)
            for (i in intPixels.indices) {
                val byte1 = bytePixels[i * 2]
                val byte2 = bytePixels[i * 2 + 1]
                val r = byte1.toInt() and 0xF8
                val g = (byte1.toInt() and 0x07 shl 5) or (byte2.toInt() and 0xE0 shr 3)
                val b = byte2.toInt() and 0x1F shl 3
                intPixels[i] = (r shl 16) or (g shl 8) or b
            }
            return intPixels
        }

        override fun intPixelsToBytePixels(intPixels: IntArray): ByteArray {
            val bytePixels = ByteArray(intPixels.size * 2)
            for (i in intPixels.indices) {
                val pixel = intPixels[i]
                val r = pixel shr 16 and 0xF8
                val g = pixel shr 8 and 0xFC
                val b = pixel and 0xF8
                bytePixels[i * 2] = (r or (g shr 5)).toByte()
                bytePixels[i * 2 + 1] = ((g shl 3) or (b shr 3)).toByte()
            }
            return bytePixels
        }
    }

    /**
     * Convert pixels between ARGB_4444 and ARGB_8888
     *
     * @see com.github.panpf.sketch.core.nonandroid.test.util.PixelsConverterTest.testARGB_4444
     */
    data object ARGB_4444 : PixelsConverter {

        override fun bytePixelsToIntPixels(bytePixels: ByteArray): IntArray {
            require(bytePixels.size % 2 == 0) {
                "The size of byte array must be a multiple of 2."
            }
            val intPixels = IntArray(bytePixels.size / 2)
            for (i in intPixels.indices) {
                val byte1 = bytePixels[i * 2].toInt() and 0xFF
                val byte2 = bytePixels[i * 2 + 1].toInt() and 0xFF
                val a = (byte1 ushr 4 and 0xF)
                val r = (byte1 and 0xF)
                val g = (byte2 ushr 4 and 0xF)
                val b = (byte2 and 0xF)
                intPixels[i] = (a shl 28) or (r shl 20) or (g shl 12) or (b shl 4)
            }
            return intPixels
        }

        override fun intPixelsToBytePixels(intPixels: IntArray): ByteArray {
            val bytePixels = ByteArray(intPixels.size * 2)
            for (i in intPixels.indices) {
                val pixel = intPixels[i]
                val a = (pixel ushr 28) and 0xF
                val r = (pixel ushr 20) and 0xF
                val g = (pixel ushr 12) and 0xF
                val b = (pixel ushr 4) and 0xF
                bytePixels[i * 2] = (a shl 4 or r).toByte()
                bytePixels[i * 2 + 1] = (g shl 4 or b).toByte()
            }
            return bytePixels
        }
    }

    /**
     * Convert pixels between RGBA_8888 and ARGB_8888
     *
     * @see com.github.panpf.sketch.core.nonandroid.test.util.PixelsConverterTest.testRGBA_8888
     */
    data object RGBA_8888 : PixelsConverter {

        override fun bytePixelsToIntPixels(bytePixels: ByteArray): IntArray {
            require(bytePixels.size % 4 == 0) {
                "The size of byte array must be a multiple of 4."
            }
            val intPixels = IntArray(bytePixels.size / 4)
            for (i in intPixels.indices) {
                val r = bytePixels[i * 4].toInt() and 0xFF
                val g = bytePixels[i * 4 + 1].toInt() and 0xFF
                val b = bytePixels[i * 4 + 2].toInt() and 0xFF
                val a = bytePixels[i * 4 + 3].toInt() and 0xFF
                intPixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
            return intPixels
        }

        override fun intPixelsToBytePixels(intPixels: IntArray): ByteArray {
            val bytePixels = ByteArray(intPixels.size * 4)
            for (i in intPixels.indices) {
                val pixel = intPixels[i]
                val a = pixel shr 24 and 0xFF
                val r = pixel shr 16 and 0xFF
                val g = pixel shr 8 and 0xFF
                val b = pixel and 0xFF
                bytePixels[i * 4] = r.toByte()
                bytePixels[i * 4 + 1] = g.toByte()
                bytePixels[i * 4 + 2] = b.toByte()
                bytePixels[i * 4 + 3] = a.toByte()
            }
            return bytePixels
        }
    }

    /**
     * Convert pixels between BGRA_8888 and ARGB_8888
     *
     * @see com.github.panpf.sketch.core.nonandroid.test.util.PixelsConverterTest.testBGRA_8888
     */
    data object BGRA_8888 : PixelsConverter {

        override fun bytePixelsToIntPixels(bytePixels: ByteArray): IntArray {
            require(bytePixels.size % 4 == 0) {
                "The size of byte array must be a multiple of 4."
            }
            val intPixels = IntArray(bytePixels.size / 4)
            for (i in intPixels.indices) {
                val b = bytePixels[i * 4].toInt() and 0xFF
                val g = bytePixels[i * 4 + 1].toInt() and 0xFF
                val r = bytePixels[i * 4 + 2].toInt() and 0xFF
                val a = bytePixels[i * 4 + 3].toInt() and 0xFF
                intPixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
            return intPixels
        }

        override fun intPixelsToBytePixels(intPixels: IntArray): ByteArray {
            val bytePixels = ByteArray(intPixels.size * 4)
            for (i in intPixels.indices) {
                val pixel = intPixels[i]
                val a = pixel shr 24 and 0xFF
                val r = pixel shr 16 and 0xFF
                val g = pixel shr 8 and 0xFF
                val b = pixel and 0xFF
                bytePixels[i * 4] = b.toByte()
                bytePixels[i * 4 + 1] = g.toByte()
                bytePixels[i * 4 + 2] = r.toByte()
                bytePixels[i * 4 + 3] = a.toByte()
            }
            return bytePixels
        }
    }

    /**
     * Convert pixels between RGB_888X and ARGB_8888
     *
     * @see com.github.panpf.sketch.core.nonandroid.test.util.PixelsConverterTest.testRGB_888X
     */
    data object RGB_888X : PixelsConverter {

        override fun bytePixelsToIntPixels(bytePixels: ByteArray): IntArray {
            require(bytePixels.size % 4 == 0) {
                "The size of byte array must be a multiple of 4."
            }
            val intPixels = IntArray(bytePixels.size / 4)
            for (i in intPixels.indices) {
                val r = bytePixels[i * 4].toInt() and 0xFF
                val g = bytePixels[i * 4 + 1].toInt() and 0xFF
                val b = bytePixels[i * 4 + 2].toInt() and 0xFF
                intPixels[i] = (r shl 16) or (g shl 8) or b
            }
            return intPixels
        }

        override fun intPixelsToBytePixels(intPixels: IntArray): ByteArray {
            val bytePixels = ByteArray(intPixels.size * 4)
            for (i in intPixels.indices) {
                val pixel = intPixels[i]
                val r = pixel shr 16 and 0xFF
                val g = pixel shr 8 and 0xFF
                val b = pixel and 0xFF
                bytePixels[i * 4] = r.toByte()
                bytePixels[i * 4 + 1] = g.toByte()
                bytePixels[i * 4 + 2] = b.toByte()
                bytePixels[i * 4 + 3] = 0.toByte()
            }
            return bytePixels
        }
    }

    /**
     * Convert pixels between RGBA_1010102 and ARGB_8888
     *
     * Note: This will reduce accuracy
     *
     * @see com.github.panpf.sketch.core.nonandroid.test.util.PixelsConverterTest.testRGBA_1010102
     */
    data object RGBA_1010102 : PixelsConverter {

        private val rMask: Int = 0xFFC00000L.toInt()  // 1111 1111 1100 0000 0000 0000 0000 0000
        private val gMask: Int = 0x003FF000L.toInt()  // 0000 0000 0011 1111 1111 0000 0000 0000
        private val bMask: Int = 0x00000FFCL.toInt()  // 0000 0000 0000 0000 0000 1111 1111 1100
        private val aMask: Int = 0x00000003L.toInt()  // 0000 0000 0000 0000 0000 0000 0000 0011

        override fun bytePixelsToIntPixels(bytePixels: ByteArray): IntArray {
            require(bytePixels.size % 4 == 0) {
                "The size of byte array must be a multiple of 4."
            }
            val buffer = Buffer().apply {
                write(bytePixels)
            }
            // TODO Error: The conversion result color is reddish
            val intPixels = IntArray(bytePixels.size / 4)
            for (i in intPixels.indices) {
                val int = buffer.readInt()
                val r = (int and rMask shr 22).toByte().toInt() and 0xFF
                val g = (int and gMask shr 12).toByte().toInt() and 0xFF
                val b = (int and bMask shr 2).toByte().toInt() and 0xFF
                val a = (int and aMask).toByte().toInt() and 0xFF
                intPixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
            return intPixels
        }

        override fun intPixelsToBytePixels(intPixels: IntArray): ByteArray {
            val buffer = Buffer()
            for (pixel in intPixels) {
                val a = pixel shr 24 and 0xFF
                val r = pixel shr 16 and 0xFF
                val g = pixel shr 8 and 0xFF
                val b = pixel and 0xFF
                buffer.writeInt((r shl 22) or (g shl 12) or (b shl 2) or a)
            }
            return buffer.readByteArray()
        }
    }

    /**
     * Convert pixels between RGB_101010X and ARGB_8888
     *
     * Note: This will reduce accuracy
     *
     * @see com.github.panpf.sketch.core.nonandroid.test.util.PixelsConverterTest.testRGB_101010X
     */
    data object RGB_101010X : PixelsConverter {

        private val rMask: Int = 0xFFC00000L.toInt()  // 1111 1111 1100 0000 0000 0000 0000 0000
        private val gMask: Int = 0x003FF000L.toInt()  // 0000 0000 0011 1111 1111 0000 0000 0000
        private val bMask: Int = 0x00000FFCL.toInt()  // 0000 0000 0000 0000 0000 1111 1111 1100

        override fun bytePixelsToIntPixels(bytePixels: ByteArray): IntArray {
            require(bytePixels.size % 4 == 0) {
                "The size of byte array must be a multiple of 4."
            }
            val buffer = Buffer().apply {
                write(bytePixels)
            }
            // TODO Error: The conversion result color is reddish
            val intPixels = IntArray(bytePixels.size / 4)
            for (i in intPixels.indices) {
                val int = buffer.readInt()
                val r = (int and rMask shr 22).toByte().toInt() and 0xFF
                val g = (int and gMask shr 12).toByte().toInt() and 0xFF
                val b = (int and bMask shr 2).toByte().toInt() and 0xFF
                intPixels[i] = (r shl 16) or (g shl 8) or b
            }
            return intPixels
        }

        override fun intPixelsToBytePixels(intPixels: IntArray): ByteArray {
            val buffer = Buffer()
            for (pixel in intPixels) {
                val r = pixel shr 16 and 0xFF
                val g = pixel shr 8 and 0xFF
                val b = pixel and 0xFF
                buffer.writeInt((r shl 22) or (g shl 12) or (b shl 2))
            }
            return buffer.readByteArray()
        }
    }

    /**
     * Convert pixels between BGRA_1010102 and ARGB_8888
     *
     * Note: This will reduce accuracy
     *
     * @see com.github.panpf.sketch.core.nonandroid.test.util.PixelsConverterTest.testBGRA_1010102
     */
    data object BGRA_1010102 : PixelsConverter {

        private val bMask: Int = 0xFFC00000L.toInt()  // 1111 1111 1100 0000 0000 0000 0000 0000
        private val gMask: Int = 0x003FF000L.toInt()  // 0000 0000 0011 1111 1111 0000 0000 0000
        private val rMask: Int = 0x00000FFCL.toInt()  // 0000 0000 0000 0000 0000 1111 1111 1100
        private val aMask: Int = 0x00000003L.toInt()  // 0000 0000 0000 0000 0000 0000 0000 0011

        override fun bytePixelsToIntPixels(bytePixels: ByteArray): IntArray {
            require(bytePixels.size % 4 == 0) {
                "The size of byte array must be a multiple of 4."
            }
            val buffer = Buffer().apply {
                write(bytePixels)
            }
            // TODO Error: The conversion result color is reddish
            val intPixels = IntArray(bytePixels.size / 4)
            for (i in intPixels.indices) {
                val int = buffer.readInt()
                val b = (int and bMask shr 22).toByte().toInt() and 0xFF
                val g = (int and gMask shr 12).toByte().toInt() and 0xFF
                val r = (int and rMask shr 2).toByte().toInt() and 0xFF
                val a = (int and aMask).toByte().toInt() and 0xFF
                intPixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
            return intPixels
        }

        override fun intPixelsToBytePixels(intPixels: IntArray): ByteArray {
            val buffer = Buffer()
            for (pixel in intPixels) {
                val a = pixel shr 24 and 0xFF
                val r = pixel shr 16 and 0xFF
                val g = pixel shr 8 and 0xFF
                val b = pixel and 0xFF
                buffer.writeInt((b shl 22) or (g shl 12) or (r shl 2) or a)
            }
            return buffer.readByteArray()
        }
    }

    /**
     * Convert pixels between BGR_101010X and ARGB_8888
     *
     * Note: This will reduce accuracy
     *
     * @see com.github.panpf.sketch.core.nonandroid.test.util.PixelsConverterTest.testBGR_101010X
     */
    data object BGR_101010X : PixelsConverter {

        private val bMask: Int = 0xFFC00000L.toInt()  // 1111 1111 1100 0000 0000 0000 0000 0000
        private val gMask: Int = 0x003FF000L.toInt()  // 0000 0000 0011 1111 1111 0000 0000 0000
        private val rMask: Int = 0x00000FFCL.toInt()  // 0000 0000 0000 0000 0000 1111 1111 1100

        override fun bytePixelsToIntPixels(bytePixels: ByteArray): IntArray {
            require(bytePixels.size % 4 == 0) {
                "The size of byte array must be a multiple of 4."
            }
            val buffer = Buffer().apply {
                write(bytePixels)
            }
            // TODO Error: The conversion result color is reddish
            val intPixels = IntArray(bytePixels.size / 4)
            for (i in intPixels.indices) {
                val int = buffer.readInt()
                val b = (int and bMask shr 22).toByte().toInt() and 0xFF
                val g = (int and gMask shr 12).toByte().toInt() and 0xFF
                val r = (int and rMask shr 2).toByte().toInt() and 0xFF
                intPixels[i] = (r shl 16) or (g shl 8) or b
            }
            return intPixels
        }

        override fun intPixelsToBytePixels(intPixels: IntArray): ByteArray {
            val buffer = Buffer()
            for (pixel in intPixels) {
                val r = pixel shr 16 and 0xFF
                val g = pixel shr 8 and 0xFF
                val b = pixel and 0xFF
                buffer.writeInt((b shl 22) or (g shl 12) or (r shl 2))
            }
            return buffer.readByteArray()
        }
    }

    /**
     * Convert pixels between GRAY_8 and ARGB_8888
     *
     * Note: This will reduce accuracy
     *
     * @see com.github.panpf.sketch.core.nonandroid.test.util.PixelsConverterTest.testGRAY_8
     */
    data object GRAY_8 : PixelsConverter {
        override fun bytePixelsToIntPixels(bytePixels: ByteArray): IntArray {
            val intPixels = IntArray(bytePixels.size)
            for (i in intPixels.indices) {
                // Grayscale images have no color information, only brightness information. So every pixel has the same RGB value
                val gray = bytePixels[i].toInt() and 0xFF
                intPixels[i] = (gray shl 16) or (gray shl 8) or gray
            }
            return intPixels
        }

        override fun intPixelsToBytePixels(intPixels: IntArray): ByteArray {
            val bytePixels = ByteArray(intPixels.size)
            for (i in intPixels.indices) {
                val pixel = intPixels[i]
                val r = pixel shr 16 and 0xFF
                val g = pixel shr 8 and 0xFF
                val b = pixel and 0xFF
                val gray = (0.2126 * r) + (0.7152 * g) + (0.0722 * b)
                bytePixels[i] = gray.toInt().toByte()
            }
            return bytePixels
        }
    }
}