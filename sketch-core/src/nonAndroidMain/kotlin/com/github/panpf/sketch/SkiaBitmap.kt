//package com.github.panpf.sketch
//
//import org.jetbrains.skia.ColorType
//
//typealias SkiaBitmap = org.jetbrains.skia.Bitmap
//
//internal fun SkiaBitmap.toLogString(): String {
//    return "SkiaBitmap@${hashCode().toString(16)}(${width.toFloat()}x${height.toFloat()},${colorType})"
//}
//
//fun SkiaBitmap.readIntPixels(): IntArray? {
//    val bytePixels = readPixels() ?: return null
//    return convertToIntColorPixels(bytePixels, imageInfo.colorType)
//}
//
//private fun convertToIntColorPixels(byteArray: ByteArray, colorType: ColorType): IntArray {
//    return when (colorType) {
//        ColorType.ALPHA_8 -> {
//            val intArray = IntArray(byteArray.size)
//            for (i in intArray.indices) {
//                val a = byteArray[i].toInt() and 0xFF
//                intArray[i] = a shl 24
//            }
//            intArray
//        }
//
//        ColorType.RGB_565 -> {
//            val intArray = IntArray(byteArray.size / 2)
//            for (i in intArray.indices) {
//                val r = byteArray[i * 2].toInt() and 0xF8
//                val g = byteArray[i * 2 + 1].toInt() and 0xFC
//                val b = byteArray[i * 2 + 2].toInt() and 0xF8
//                intArray[i] = (r shl 16) or (g shl 8) or b
//            }
//            intArray
//        }
//
//        ColorType.RGB_888X, ColorType.RGB_101010X -> {
//            val intArray = IntArray(byteArray.size / 3)
//            for (i in intArray.indices) {
//                val r = byteArray[i * 3].toInt() and 0xFF
//                val g = byteArray[i * 3 + 1].toInt() and 0xFF
//                val b = byteArray[i * 3 + 2].toInt() and 0xFF
//                intArray[i] = (r shl 16) or (g shl 8) or b
//            }
//            intArray
//        }
//
//        ColorType.ARGB_4444, ColorType.RGBA_8888, ColorType.BGRA_8888, ColorType.RGBA_1010102 -> {
//            val intArray = IntArray(byteArray.size / 4)
//            for (i in intArray.indices) {
//                val r = byteArray[i * 4].toInt() and 0xFF
//                val g = byteArray[i * 4 + 1].toInt() and 0xFF
//                val b = byteArray[i * 4 + 2].toInt() and 0xFF
//                val a = byteArray[i * 4 + 3].toInt() and 0xFF
//                intArray[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
//            }
//            intArray
//        }
//
//        else -> throw IllegalArgumentException("Unsupported color type: $colorType")
//    }
//}