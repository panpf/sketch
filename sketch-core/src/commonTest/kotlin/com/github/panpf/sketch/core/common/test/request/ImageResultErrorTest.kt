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

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.test.utils.createBitmapImage
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ImageResultErrorTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()
        ImageResult.Error(
            request = ImageRequest(context, "http://sample.com/sample.jpeg"),
            image = createBitmapImage(111, 222),
            throwable = Exception(),
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val element1 = ImageResult.Error(
            request = ImageRequest(context, "http://sample.com/sample.jpeg"),
            image = createBitmapImage(111, 222),
            throwable = Exception(),
        )
        val element1_1 = element1.copy()
        val element2 =
            element1.copy(request = ImageRequest(context, "http://sample.com/sample2.jpeg"))
        val element3 = element1.copy(image = createBitmapImage(222, 111))
        val element4 = element1.copy(throwable = Exception())

        assertEquals(element1, element1_1)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element3, element4)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1_1.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val image = createBitmapImage(111, 222)
        val throwable = Exception()
        val success = ImageResult.Error(
            request = request,
            image = image,
            throwable = throwable,
        )
        assertEquals(
            expected = "Error(request=$request, image=$image, throwable=$throwable)",
            actual = success.toString()
        )
    }
}