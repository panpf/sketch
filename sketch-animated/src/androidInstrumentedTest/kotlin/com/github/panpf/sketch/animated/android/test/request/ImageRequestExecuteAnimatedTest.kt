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

package com.github.panpf.sketch.animated.android.test.request

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.DrawableImage
import com.github.panpf.sketch.decode.GifAnimatedDecoder
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.asOrNull
import com.github.panpf.sketch.test.utils.runInNewSketchWithUse
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ImageRequestExecuteAnimatedTest {

    @Test
    fun testDisallowAnimatedImage() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.P) return@runTest

        runInNewSketchWithUse({
            components {
                addDecoder(GifAnimatedDecoder.Factory())
            }
            httpStack(TestHttpStack(it))
        }) { context, sketch ->
            val imageUri = ResourceImages.animGif.uri
            val request = ImageRequest(context, imageUri)

            request.let { sketch.execute(it) }
                .asOrNull<ImageResult.Success>()!!.apply {
                    assertTrue(image.asOrNull<DrawableImage>()!!.drawable is AnimatableDrawable)
                }

            request.newRequest {
                disallowAnimatedImage(false)
            }.let { sketch.execute(it) }
                .asOrNull<ImageResult.Success>()!!.apply {
                    assertTrue(image.asOrNull<DrawableImage>()!!.drawable is AnimatableDrawable)
                }

            request.newRequest {
                disallowAnimatedImage(null)
            }.let { sketch.execute(it) }
                .asOrNull<ImageResult.Success>()!!.apply {
                    assertTrue(image.asOrNull<DrawableImage>()!!.drawable is AnimatableDrawable)
                }

            request.newRequest {
                disallowAnimatedImage(true)
            }.let { sketch.execute(it) }
                .asOrNull<ImageResult.Success>()!!.apply {
                    assertTrue(image.asOrNull<BitmapImage>() != null)
                }
        }
    }
}