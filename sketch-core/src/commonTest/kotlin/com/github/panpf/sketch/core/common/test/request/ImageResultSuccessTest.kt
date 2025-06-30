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
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.utils.createBitmapImage
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.transform.createCircleCropTransformed
import com.github.panpf.sketch.transform.createRotateTransformed
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ImageResultSuccessTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()
        ImageResult.Success(
            request = ImageRequest(context, "http://sample.com/sample.jpeg"),
            image = createBitmapImage(111, 222),
            cacheKey = "cacheKey",
            memoryCacheKey = "memoryCacheKey",
            resultCacheKey = "resultCacheKey",
            downloadCacheKey = "downloadCacheKey",
            imageInfo = ImageInfo(100, 100, "image/jpeg"),
            dataFrom = LOCAL,
            resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
            transformeds = listOf(createCircleCropTransformed(END_CROP)),
            extras = mapOf("age" to "16"),
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val element1 = ImageResult.Success(
            request = ImageRequest(context, "http://sample.com/sample.jpeg"),
            image = createBitmapImage(111, 222),
            cacheKey = "cacheKey",
            memoryCacheKey = "memoryCacheKey",
            resultCacheKey = "resultCacheKey",
            downloadCacheKey = "downloadCacheKey",
            imageInfo = ImageInfo(100, 100, "image/jpeg"),
            dataFrom = DataFrom.LOCAL,
            resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
            transformeds = listOf(createCircleCropTransformed(END_CROP)),
            extras = mapOf("age" to "16"),
        )
        val element1_1 = element1.copy()
        val element2 =
            element1.copy(request = ImageRequest(context, "http://sample.com/sample2.jpeg"))
        val element3 = element1.copy(image = createBitmapImage(222, 111))
        val element4 = element1.copy(cacheKey = "cacheKey2")
        val element5 = element1.copy(memoryCacheKey = "memoryCacheKey2")
        val element6 = element1.copy(resultCacheKey = "resultCacheKey2")
        val element7 = element1.copy(downloadCacheKey = "downloadCacheKey2")
        val element8 = element1.copy(imageInfo = ImageInfo(200, 100, "image/jpeg"))
        val element9 = element1.copy(dataFrom = DataFrom.RESULT_CACHE)
        val element10 =
            element1.copy(resize = Resize(200, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP))
        val element11 = element1.copy(transformeds = listOf(createRotateTransformed(90)))
        val element12 = element1.copy(extras = mapOf("from" to "china"))

        assertEquals(element1, element1_1)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element1, element6)
        assertNotEquals(element1, element7)
        assertNotEquals(element1, element8)
        assertNotEquals(element1, element9)
        assertNotEquals(element1, element10)
        assertNotEquals(element1, element11)
        assertNotEquals(element1, element12)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element2, element5)
        assertNotEquals(element2, element6)
        assertNotEquals(element2, element7)
        assertNotEquals(element2, element8)
        assertNotEquals(element2, element9)
        assertNotEquals(element2, element10)
        assertNotEquals(element2, element11)
        assertNotEquals(element2, element12)
        assertNotEquals(element3, element4)
        assertNotEquals(element3, element5)
        assertNotEquals(element3, element6)
        assertNotEquals(element3, element7)
        assertNotEquals(element3, element8)
        assertNotEquals(element3, element9)
        assertNotEquals(element3, element10)
        assertNotEquals(element3, element11)
        assertNotEquals(element3, element12)
        assertNotEquals(element4, element5)
        assertNotEquals(element4, element6)
        assertNotEquals(element4, element7)
        assertNotEquals(element4, element8)
        assertNotEquals(element4, element9)
        assertNotEquals(element4, element10)
        assertNotEquals(element4, element11)
        assertNotEquals(element4, element12)
        assertNotEquals(element5, element6)
        assertNotEquals(element5, element7)
        assertNotEquals(element5, element8)
        assertNotEquals(element5, element9)
        assertNotEquals(element5, element10)
        assertNotEquals(element5, element11)
        assertNotEquals(element5, element12)
        assertNotEquals(element6, element7)
        assertNotEquals(element6, element8)
        assertNotEquals(element6, element9)
        assertNotEquals(element6, element10)
        assertNotEquals(element6, element11)
        assertNotEquals(element6, element12)
        assertNotEquals(element7, element8)
        assertNotEquals(element7, element9)
        assertNotEquals(element7, element10)
        assertNotEquals(element7, element11)
        assertNotEquals(element7, element12)
        assertNotEquals(element8, element9)
        assertNotEquals(element8, element10)
        assertNotEquals(element8, element11)
        assertNotEquals(element8, element12)
        assertNotEquals(element9, element10)
        assertNotEquals(element9, element11)
        assertNotEquals(element9, element12)
        assertNotEquals(element10, element11)
        assertNotEquals(element10, element12)
        assertNotEquals(element11, element12)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1_1.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element1.hashCode(), element6.hashCode())
        assertNotEquals(element1.hashCode(), element7.hashCode())
        assertNotEquals(element1.hashCode(), element8.hashCode())
        assertNotEquals(element1.hashCode(), element9.hashCode())
        assertNotEquals(element1.hashCode(), element10.hashCode())
        assertNotEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element12.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element5.hashCode())
        assertNotEquals(element2.hashCode(), element6.hashCode())
        assertNotEquals(element2.hashCode(), element7.hashCode())
        assertNotEquals(element2.hashCode(), element8.hashCode())
        assertNotEquals(element2.hashCode(), element9.hashCode())
        assertNotEquals(element2.hashCode(), element10.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
        assertNotEquals(element2.hashCode(), element12.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element5.hashCode())
        assertNotEquals(element3.hashCode(), element6.hashCode())
        assertNotEquals(element3.hashCode(), element7.hashCode())
        assertNotEquals(element3.hashCode(), element8.hashCode())
        assertNotEquals(element3.hashCode(), element9.hashCode())
        assertNotEquals(element3.hashCode(), element10.hashCode())
        assertNotEquals(element3.hashCode(), element11.hashCode())
        assertNotEquals(element3.hashCode(), element12.hashCode())
        assertNotEquals(element4.hashCode(), element5.hashCode())
        assertNotEquals(element4.hashCode(), element6.hashCode())
        assertNotEquals(element4.hashCode(), element7.hashCode())
        assertNotEquals(element4.hashCode(), element8.hashCode())
        assertNotEquals(element4.hashCode(), element9.hashCode())
        assertNotEquals(element4.hashCode(), element10.hashCode())
        assertNotEquals(element4.hashCode(), element11.hashCode())
        assertNotEquals(element4.hashCode(), element12.hashCode())
        assertNotEquals(element5.hashCode(), element6.hashCode())
        assertNotEquals(element5.hashCode(), element7.hashCode())
        assertNotEquals(element5.hashCode(), element8.hashCode())
        assertNotEquals(element5.hashCode(), element9.hashCode())
        assertNotEquals(element5.hashCode(), element10.hashCode())
        assertNotEquals(element5.hashCode(), element11.hashCode())
        assertNotEquals(element5.hashCode(), element12.hashCode())
        assertNotEquals(element6.hashCode(), element7.hashCode())
        assertNotEquals(element6.hashCode(), element8.hashCode())
        assertNotEquals(element6.hashCode(), element9.hashCode())
        assertNotEquals(element6.hashCode(), element10.hashCode())
        assertNotEquals(element6.hashCode(), element11.hashCode())
        assertNotEquals(element6.hashCode(), element12.hashCode())
        assertNotEquals(element7.hashCode(), element8.hashCode())
        assertNotEquals(element7.hashCode(), element9.hashCode())
        assertNotEquals(element7.hashCode(), element10.hashCode())
        assertNotEquals(element7.hashCode(), element11.hashCode())
        assertNotEquals(element7.hashCode(), element12.hashCode())
        assertNotEquals(element8.hashCode(), element9.hashCode())
        assertNotEquals(element8.hashCode(), element10.hashCode())
        assertNotEquals(element8.hashCode(), element11.hashCode())
        assertNotEquals(element8.hashCode(), element12.hashCode())
        assertNotEquals(element9.hashCode(), element10.hashCode())
        assertNotEquals(element9.hashCode(), element11.hashCode())
        assertNotEquals(element9.hashCode(), element12.hashCode())
        assertNotEquals(element10.hashCode(), element11.hashCode())
        assertNotEquals(element10.hashCode(), element12.hashCode())
        assertNotEquals(element11.hashCode(), element12.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val image = createBitmapImage(111, 222)
        val imageInfo = ImageInfo(100, 100, "image/jpeg")
        val resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP)
        val transformeds = listOf(createCircleCropTransformed(END_CROP))
        val extras = mapOf("age" to "16")
        val success = ImageResult.Success(
            request = request,
            image = image,
            cacheKey = "cacheKey1",
            memoryCacheKey = "memoryCacheKey1",
            resultCacheKey = "resultCacheKey1",
            downloadCacheKey = "downloadCacheKey1",
            imageInfo = imageInfo,
            dataFrom = LOCAL,
            resize = resize,
            transformeds = transformeds,
            extras = extras,
        )
        assertEquals(
            expected = "Success(" +
                    "request=$request, " +
                    "image=$image, " +
                    "cacheKey=cacheKey1, " +
                    "memoryCacheKey=memoryCacheKey1, " +
                    "resultCacheKey=resultCacheKey1, " +
                    "downloadCacheKey=downloadCacheKey1, " +
                    "imageInfo=$imageInfo, " +
                    "dataFrom=LOCAL, " +
                    "resize=$resize, " +
                    "transformeds=[CircleCropTransformed(END_CROP)], " +
                    "extras={age=16}" +
                    ")",
            actual = success.toString()
        )
    }
}