/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.core.test.request

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.DrawableImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.createCircleCropTransformed
import com.github.panpf.sketch.util.asOrThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageResultTest {

    @Test
    fun test() {
        val (context, sketch) = getTestContextAndSketch()
        val request1 = ImageRequest(context, "http://sample.com/sample.jpeg")

        ImageResult.Success(
            request = request1,
            cacheKey = request1.toRequestContext(sketch).cacheKey,
            image = ColorDrawable(Color.BLACK).asSketchImage(),
            imageInfo = ImageInfo(100, 100, "image/jpeg", ExifInterface.ORIENTATION_ROTATE_90),
            dataFrom = LOCAL,
            transformedList = listOf(createCircleCropTransformed(END_CROP)),
            extras = mapOf("age" to "16"),
        ).apply {
            Assert.assertSame(request1, request)
            Assert.assertTrue(image.asOrThrow<DrawableImage>().drawable is ColorDrawable)
            Assert.assertEquals(
                ImageInfo(
                    width = 100,
                    height = 100,
                    mimeType = "image/jpeg",
                    exifOrientation = ExifInterface.ORIENTATION_ROTATE_90
                ),
                imageInfo
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(listOf(createCircleCropTransformed(END_CROP)), transformedList)
            Assert.assertEquals(mapOf("age" to "16"), extras)
        }

        ImageResult.Error(request1, ColorDrawable(Color.BLACK).asSketchImage(), Exception(""))
            .apply {
                Assert.assertSame(request1, request)
                Assert.assertTrue(image?.asOrThrow<DrawableImage>()?.drawable is ColorDrawable)
                Assert.assertTrue(throwable is Exception)
            }
    }
}