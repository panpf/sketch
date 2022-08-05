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
package com.github.panpf.sketch.extensions.test.stateimage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.SAVE_CELLULAR_TRAFFIC_KEY
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.ColorStateImage
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.IntColor
import com.github.panpf.sketch.stateimage.SaveCellularTrafficMatcher
import com.github.panpf.sketch.stateimage.saveCellularTrafficError
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SaveCellularTrafficExtensionsTest {

    @Test
    fun testSaveCellularTrafficError() {
        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))).apply {
            Assert.assertNull(matcherList.find { it is SaveCellularTrafficMatcher })
        }

        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            saveCellularTrafficError()
        }.apply {
            Assert.assertNotNull(matcherList.find { it is SaveCellularTrafficMatcher })
        }

        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            saveCellularTrafficError(ColorStateImage(IntColor(Color.BLUE)))
        }.apply {
            Assert.assertNotNull(matcherList.find { it is SaveCellularTrafficMatcher })
        }

        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            saveCellularTrafficError(ColorDrawable(Color.GREEN))
        }.apply {
            Assert.assertNotNull(matcherList.find { it is SaveCellularTrafficMatcher })
        }

        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            saveCellularTrafficError(android.R.drawable.btn_dialog)
        }.apply {
            Assert.assertNotNull(matcherList.find { it is SaveCellularTrafficMatcher })
        }
    }

    @Test
    fun testSaveCellularTrafficMatcher() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch

        SaveCellularTrafficMatcher(null).apply {
            val request = DisplayRequest(context, "http://sample.com/sample.jpeg") {
                depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
            }
            Assert.assertTrue(match(request.newDisplayRequest {
                depth(LOCAL, SAVE_CELLULAR_TRAFFIC_KEY)
            }, DepthException("")))
            Assert.assertFalse(
                match(
                    request.newDisplayRequest {
                        depth(MEMORY, SAVE_CELLULAR_TRAFFIC_KEY)
                    },
                    DepthException("")
                )
            )
            Assert.assertFalse(match(request, null))

            Assert.assertNull(getDrawable(sketch, request, null))
        }

        SaveCellularTrafficMatcher(ColorStateImage(IntColor(Color.BLUE))).apply {
            val request = DisplayRequest(context, "http://sample.com/sample.jpeg") {
                depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
            }

            Assert.assertTrue(getDrawable(sketch, request, null) is ColorDrawable)
        }

        val element1 = SaveCellularTrafficMatcher(ColorStateImage(Color.BLUE))
        val element11 = SaveCellularTrafficMatcher(ColorStateImage(Color.BLUE))
        val element2 = SaveCellularTrafficMatcher(null)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())

        SaveCellularTrafficMatcher(ColorStateImage(Color.BLUE)).apply {
            Assert.assertEquals(
                "SaveCellularTrafficMatcher(${stateImage})",
                toString()
            )
        }
    }
}