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

import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.CurrentStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.TestTarget
import com.github.panpf.sketch.util.SketchSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CurrentStateImageTest {

    @Test
    fun testConstructor() {
        CurrentStateImage().apply {
            assertNull(defaultImage)
        }

        val stateImage = FakeStateImage()
        CurrentStateImage(stateImage).apply {
            assertNotNull(defaultImage)
            assertEquals(stateImage, defaultImage)
        }
    }

    @Test
    fun testKey() {
        CurrentStateImage().apply {
            assertEquals("Current(null)", key)
        }

        val stateImage = FakeStateImage()
        CurrentStateImage(stateImage).apply {
            assertEquals("Current(${stateImage.key})", key)
        }
    }

    @Test
    fun testGetImage() {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, ComposeResImageFiles.jpeg.uri)
        assertNull(request.target)
        CurrentStateImage().apply {
            assertNull(getImage(sketch, request, null))
        }

        val stateImage = FakeStateImage()
        CurrentStateImage(stateImage).apply {
            assertEquals(stateImage.image, getImage(sketch, request, null))
        }

        val stateImage2 = FakeStateImage(FakeImage(SketchSize(200, 200)))
        val request2 = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            target(TestTarget(currentImage = stateImage2.image))
        }
        assertEquals(stateImage2.image, request2.target?.currentImage)
        CurrentStateImage().apply {
            assertEquals(stateImage2.image, getImage(sketch, request2, null))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = CurrentStateImage()
        val element11 = CurrentStateImage()
        val element2 = CurrentStateImage(FakeStateImage())

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        CurrentStateImage().apply {
            assertEquals(expected = "CurrentStateImage(defaultImage=null)", actual = toString())
        }

        val defaultImage = FakeStateImage()
        CurrentStateImage(defaultImage).apply {
            assertEquals(
                expected = "CurrentStateImage(defaultImage=$defaultImage)",
                actual = toString()
            )
        }
    }
}