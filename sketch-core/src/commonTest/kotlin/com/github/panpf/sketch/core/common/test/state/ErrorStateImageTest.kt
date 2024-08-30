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

package com.github.panpf.sketch.core.common.test.state

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.UriInvalidCondition
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.SketchSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ErrorStateImageTest {

    @Test
    fun testFun() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "")
        val stateImage = FakeStateImage()
        val stateImage2 = FakeStateImage(FakeImage(SketchSize(200, 200)))

        ErrorStateImage().apply {
            assertTrue(stateList.isEmpty())
            assertNull(getImage(sketch, request, null))
            assertNull(getImage(sketch, request, UriInvalidException("")))
        }

        ErrorStateImage(stateImage).apply {
            assertFalse(stateList.isEmpty())
            assertEquals(stateImage.image, getImage(sketch, request, null))
            assertEquals(stateImage.image, getImage(sketch, request, UriInvalidException("")))
        }

        ErrorStateImage(stateImage) {
            addState(UriInvalidCondition, stateImage2)
        }.apply {
            assertFalse(stateList.isEmpty())
            assertEquals(stateImage.image, getImage(sketch, request, null))
            assertEquals(stateImage2.image, getImage(sketch, request, UriInvalidException("")))
        }
    }

    @Test
    fun testKey() {
        val stateImage = FakeStateImage()
        ErrorStateImage(stateImage).apply {
            assertEquals(
                "ErrorStateImage([DefaultCondition:${stateImage.key}])",
                key
            )
        }

        val stateImage2 = FakeStateImage(FakeImage(SketchSize(200, 200)))
        val stateImage3 = FakeStateImage(FakeImage(SketchSize(300, 300)))
        ErrorStateImage(stateImage2) {
            addState(UriInvalidCondition, stateImage3)
        }.apply {
            assertEquals(
                "ErrorStateImage([UriInvalidCondition:${stateImage3.key},DefaultCondition:${stateImage2.key}])",
                key
            )
        }
    }

    @Test
    fun testGetImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "")
        val stateImage = FakeStateImage()
        val stateImage2 = FakeStateImage(FakeImage(SketchSize(200, 200)))

        ErrorStateImage().apply {
            assertTrue(stateList.isEmpty())
            assertNull(getImage(sketch, request, null))
            assertNull(getImage(sketch, request, UriInvalidException("")))
        }

        ErrorStateImage(stateImage).apply {
            assertFalse(stateList.isEmpty())
            assertEquals(stateImage.image, getImage(sketch, request, null))
            assertEquals(stateImage.image, getImage(sketch, request, UriInvalidException("")))
        }

        ErrorStateImage(stateImage) {
            addState(UriInvalidCondition, stateImage2)
        }.apply {
            assertFalse(stateList.isEmpty())
            assertEquals(stateImage.image, getImage(sketch, request, null))
            assertEquals(stateImage2.image, getImage(sketch, request, UriInvalidException("")))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ErrorStateImage(FakeStateImage())
        val element11 = ErrorStateImage(FakeStateImage())
        val element2 = ErrorStateImage(FakeStateImage(FakeImage(SketchSize(200, 200))))
        val element3 = ErrorStateImage(FakeStateImage(FakeImage(SketchSize(300, 300))))

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element1, element3)
        assertNotSame(element2, element11)
        assertNotSame(element2, element3)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element11)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        val stateImage = FakeStateImage()
        ErrorStateImage(stateImage).apply {
            assertEquals(
                "ErrorStateImage([DefaultCondition:${stateImage.key}])",
                toString()
            )
        }

        val stateImage2 = FakeStateImage(FakeImage(SketchSize(200, 200)))
        val stateImage3 = FakeStateImage(FakeImage(SketchSize(300, 300)))
        ErrorStateImage(stateImage2) {
            addState(UriInvalidCondition, stateImage3)
        }.apply {
            assertEquals(
                "ErrorStateImage([UriInvalidCondition:${stateImage3.key}, DefaultCondition:${stateImage2.key}])",
                toString()
            )
        }
    }

    @Test
    fun testDefaultCondition() {
        val context = getTestContext()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)

        ErrorStateImage.DefaultCondition.apply {
            assertTrue(accept(request, null))
            assertTrue(accept(request, null))
            assertEquals("DefaultCondition", toString())
        }
    }
}