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

package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.createBitmapImage
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.createCircleCropTransformed
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ImageResultTest {

    @Test
    fun test() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request1 = ImageRequest(context, "http://sample.com/sample.jpeg")
        val image1 = createBitmapImage(111, 222)
        val image2 = createBitmapImage(222, 111)

        ImageResult.Success(
            request = request1,
            cacheKey = request1.toRequestContext(sketch).cacheKey,
            image = image1,
            imageInfo = ImageInfo(100, 100, "image/jpeg"),
            dataFrom = LOCAL,
            resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
            transformeds = listOf(createCircleCropTransformed(END_CROP)),
            extras = mapOf("age" to "16"),
        ).apply {
            assertSame(request1, request)
            assertSame(image1, image)
            assertEquals(ImageInfo(100, 100, "image/jpeg"), imageInfo)
            assertEquals(LOCAL, dataFrom)
            assertEquals(listOf(createCircleCropTransformed(END_CROP)), transformeds)
            assertEquals(mapOf("age" to "16"), extras)
        }

        ImageResult.Error(request1, image2, Exception(""))
            .apply {
                assertSame(request1, request)
                assertSame(image2, image)
                assertTrue(throwable is Exception)
            }
    }
}