//package com.github.panpf.sketch.transform
//
//import com.github.panpf.sketch.Image
//import com.github.panpf.sketch.JvmBitmapImage
//import com.github.panpf.sketch.SkiaBitmapImage
//import com.github.panpf.sketch.asSketchImage
//import com.github.panpf.sketch.util.backgrounded
//import com.github.panpf.sketch.util.blur
//import com.github.panpf.sketch.util.hasAlpha
//import com.github.panpf.sketch.util.mask
//
//
//internal actual fun blurTransformation(
//    image: Image,
//    radius: Int,
//    hasAlphaBitmapBgColor: Int?,
//    maskColor: Int?
//): Image = when (image) {
//    is JvmBitmapImage -> {
//        val inputBitmap = image.bitmap
//        // Transparent pixels cannot be blurred
//        val compatAlphaBitmap = if (hasAlphaBitmapBgColor != null && inputBitmap.hasAlpha()) {
//            inputBitmap.backgrounded(hasAlphaBitmapBgColor)
//        } else {
//            inputBitmap
//        }
//        val blurImage = compatAlphaBitmap.apply { blur(radius) }
//        val maskImage = blurImage.apply { if (maskColor != null) mask(maskColor) }
//        maskImage.asSketchImage()
//    }
//
//    is SkiaBitmapImage -> {
//        val inputBitmap = image.bitmap
//        // Transparent pixels cannot be blurred
//        val compatAlphaBitmap = if (hasAlphaBitmapBgColor != null && inputBitmap.hasAlpha()) {
//            inputBitmap.backgrounded(hasAlphaBitmapBgColor)
//        } else {
//            inputBitmap
//        }
//        val blurImage = compatAlphaBitmap.apply { blur(radius) }
//        val maskImage = blurImage.apply { if (maskColor != null) mask(maskColor) }
//        maskImage.asSketchImage()
//    }
//
//    else -> {
//        throw IllegalArgumentException("Only JvmBitmapImage or SkiaBitmapImage is supported: ${image::class}")
//    }
//}