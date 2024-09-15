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

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.github.panpf.sketch

/**
 * Convert [Bitmap] to [BitmapImage]
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapImageAndroidTest.testAsImage
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapImageNonAndroidTest.testAsImage
 */
expect fun Bitmap.asImage(): BitmapImage

/**
 * Bitmap image, which is a wrapper for [Bitmap]
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapImageAndroidTest
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapImageNonAndroidTest
 */
expect class BitmapImage : Image {

    val bitmap: Bitmap

    override val width: Int

    override val height: Int

    override val byteCount: Long

    override val shareable: Boolean

    override val cachedInMemory: Boolean

    override fun checkValid(): Boolean
}