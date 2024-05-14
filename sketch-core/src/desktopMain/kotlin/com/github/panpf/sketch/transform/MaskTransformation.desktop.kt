//package com.github.panpf.sketch.transform
//
//import com.github.panpf.sketch.Image
//import com.github.panpf.sketch.JvmBitmap
//import com.github.panpf.sketch.JvmBitmapImage
//import com.github.panpf.sketch.SkiaBitmap
//import com.github.panpf.sketch.SkiaBitmapImage
//import com.github.panpf.sketch.asSketchImage
//import com.github.panpf.sketch.util.mask
//
//internal actual fun maskTransformation(image: Image, maskColor: Int): Image = when (image) {
//    is JvmBitmapImage -> {
//        val inputBitmap: JvmBitmap = image.bitmap
//        val outBitmap: JvmBitmap = inputBitmap.apply { mask(maskColor) }
//        outBitmap.asSketchImage()
//    }
//
//    is SkiaBitmapImage -> {
//        val inputBitmap: SkiaBitmap = image.bitmap
//        val outBitmap: SkiaBitmap = inputBitmap.apply { mask(maskColor) }
//        outBitmap.asSketchImage()
//    }
//
//    else -> {
//        throw IllegalArgumentException("Only JvmBitmapImage or SkiaBitmapImage is supported: ${image::class.qualifiedName}")
//    }
//}