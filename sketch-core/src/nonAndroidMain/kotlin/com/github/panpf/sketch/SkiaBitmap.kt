package com.github.panpf.sketch

import org.jetbrains.skia.ColorType

typealias SkiaBitmap = org.jetbrains.skia.Bitmap

internal fun SkiaBitmap.toLogString(): String {
    return "SkiaBitmap@${hashCode().toString(16)}(${width.toFloat()}x${height.toFloat()},${colorType})"
}

fun SkiaBitmap.readIntPixels(): IntArray? {
    val bytePixels = readPixels() ?: return null
    return convertToIntColorPixels(bytePixels, imageInfo.colorType)
}

fun SkiaBitmap.installIntPixels(intArray: IntArray): Boolean {
    val bytePixels = convertToByteColorPixels(intArray, imageInfo.colorType)
    return installPixels(bytePixels)
}

private fun convertToIntColorPixels(byteArray: ByteArray, colorType: ColorType): IntArray {
    return when (colorType) {
        ColorType.ALPHA_8 -> {
            val intArray = IntArray(byteArray.size)
            for (i in intArray.indices) {
                val a = byteArray[i].toInt() and 0xFF
                intArray[i] = a shl 24
            }
            intArray
        }

        ColorType.RGB_565 -> {
            val intArray = IntArray(byteArray.size / 2)
            for (i in intArray.indices) {
                val r = byteArray[i * 2].toInt() and 0xF8
                val g = byteArray[i * 2 + 1].toInt() and 0xFC
                val b = byteArray[i * 2 + 2].toInt() and 0xF8
                intArray[i] = (r shl 16) or (g shl 8) or b
            }
            intArray
        }

        ColorType.RGB_888X, ColorType.RGB_101010X -> {
            val intArray = IntArray(byteArray.size / 3)
            for (i in intArray.indices) {
                val r = byteArray[i * 3].toInt() and 0xFF
                val g = byteArray[i * 3 + 1].toInt() and 0xFF
                val b = byteArray[i * 3 + 2].toInt() and 0xFF
                intArray[i] = (r shl 16) or (g shl 8) or b
            }
            intArray
        }

        ColorType.ARGB_4444, ColorType.RGBA_8888, ColorType.BGRA_8888, ColorType.RGBA_1010102 -> {
            val intArray = IntArray(byteArray.size / 4)
            for (i in intArray.indices) {
                val r = byteArray[i * 4].toInt() and 0xFF
                val g = byteArray[i * 4 + 1].toInt() and 0xFF
                val b = byteArray[i * 4 + 2].toInt() and 0xFF
                val a = byteArray[i * 4 + 3].toInt() and 0xFF
                intArray[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
            intArray
        }

        else -> throw IllegalArgumentException("Unsupported color type: $colorType")
    }
}

private fun convertToByteColorPixels(intArray: IntArray, colorType: ColorType): ByteArray {
    return when (colorType) {
        ColorType.ALPHA_8 -> {
            val byteArray = ByteArray(intArray.size)
            for (i in intArray.indices) {
                val a = intArray[i] shr 24
                byteArray[i] = a.toByte()
            }
            byteArray
        }

        ColorType.RGB_565 -> {
            val byteArray = ByteArray(intArray.size * 2)
            for (i in intArray.indices) {
                val r = (intArray[i] shr 16) and 0xF8
                val g = (intArray[i] shr 8) and 0xFC
                val b = intArray[i] and 0xF8
                byteArray[i * 2] = r.toByte()
                byteArray[i * 2 + 1] = g.toByte()
                byteArray[i * 2 + 2] = b.toByte()
            }
            byteArray
        }

        ColorType.RGB_888X, ColorType.RGB_101010X -> {
            val byteArray = ByteArray(intArray.size * 3)
            for (i in intArray.indices) {
                val r = (intArray[i] shr 16) and 0xFF
                val g = (intArray[i] shr 8) and 0xFF
                val b = intArray[i] and 0xFF
                byteArray[i * 3] = r.toByte()
                byteArray[i * 3 + 1] = g.toByte()
                byteArray[i * 3 + 2] = b.toByte()
            }
            byteArray
        }

        ColorType.ARGB_4444, ColorType.RGBA_8888, ColorType.BGRA_8888, ColorType.RGBA_1010102 -> {
            val byteArray = ByteArray(intArray.size * 4)
            for (i in intArray.indices) {
                val r = (intArray[i] shr 16) and 0xFF
                val g = (intArray[i] shr 8) and 0xFF
                val b = intArray[i] and 0xFF
                val a = intArray[i] shr 24
                byteArray[i * 4] = r.toByte()
                byteArray[i * 4 + 1] = g.toByte()
                byteArray[i * 4 + 2] = b.toByte()
                byteArray[i * 4 + 3] = a.toByte()
            }
            byteArray
        }

        else -> throw IllegalArgumentException("Unsupported color type: $colorType")
    }
}