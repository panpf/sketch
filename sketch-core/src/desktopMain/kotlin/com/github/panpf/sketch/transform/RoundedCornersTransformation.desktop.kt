///*
// * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.github.panpf.sketch.transform
//
//import com.github.panpf.sketch.Image
//import com.github.panpf.sketch.JvmBitmap
//import com.github.panpf.sketch.JvmBitmapImage
//import com.github.panpf.sketch.SkiaBitmap
//import com.github.panpf.sketch.SkiaBitmapImage
//import com.github.panpf.sketch.asSketchImage
//import com.github.panpf.sketch.util.roundedCornered
//
///**
// * @param radiusArray Array of 8 values, 4 pairs of [X,Y] radii. The corners are ordered top-left, top-right, bottom-right, bottom-left
// */
//internal actual fun roundedCornersTransformation(image: Image, radiusArray: FloatArray): Image =
//    when (image) {
//        is JvmBitmapImage -> {
//            val inputBitmap: JvmBitmap = image.bitmap
//            val outBitmap: JvmBitmap = inputBitmap.roundedCornered(radiusArray)
//            outBitmap.asSketchImage()
//        }
//
//        is SkiaBitmapImage -> {
//            val inputBitmap: SkiaBitmap = image.bitmap
//            val outBitmap: SkiaBitmap = inputBitmap.roundedCornered(radiusArray)
//            outBitmap.asSketchImage()
//        }
//
//        else -> {
//            throw IllegalArgumentException("Only JvmBitmapImage or SkiaBitmapImage is supported: ${image::class.qualifiedName}")
//        }
//    }