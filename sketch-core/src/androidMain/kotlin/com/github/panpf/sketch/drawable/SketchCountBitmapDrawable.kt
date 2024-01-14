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
//package com.github.panpf.sketch.drawable
//
//import android.content.res.Resources
//import android.graphics.drawable.BitmapDrawable
//import com.github.panpf.sketch.cache.CountingBitmapImage
//
///**
// * BitmapDrawable with reference counting support
// */
//class SketchCountBitmapDrawable constructor(
//    resources: Resources?,
//    val countingBitmapImage: CountingBitmapImage,
//) : BitmapDrawable(resources, countingBitmapImage.bitmap) {
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//        other as SketchCountBitmapDrawable
//        if (countingBitmapImage != other.countingBitmapImage) return false
//        return true
//    }
//
//    override fun hashCode(): Int = countingBitmapImage.hashCode()
//
//    override fun toString(): String = "SketchCountBitmapDrawable($countingBitmapImage)"
//}