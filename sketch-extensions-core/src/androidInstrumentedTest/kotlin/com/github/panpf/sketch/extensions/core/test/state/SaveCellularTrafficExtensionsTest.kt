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
package com.github.panpf.sketch.extensions.core.test.state

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.SAVE_CELLULAR_TRAFFIC_KEY
import com.github.panpf.sketch.state.ColorStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.state.SaveCellularTrafficCondition
import com.github.panpf.sketch.state.saveCellularTrafficError
import com.github.panpf.sketch.util.Logger.Companion.Assert
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SaveCellularTrafficExtensionsTest {

    @Test
    fun testSaveCellularTrafficError() {
        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))).apply {
            Assert.assertNull(stateList.find { it.first is SaveCellularTrafficCondition })
        }

        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            saveCellularTrafficError()
        }.apply {
            Assert.assertNotNull(stateList.find { it.first is SaveCellularTrafficCondition })
        }

        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            saveCellularTrafficError(ColorStateImage(IntColor(Color.BLUE)))
        }.apply {
            Assert.assertNotNull(stateList.find { it.first is SaveCellularTrafficCondition })
        }

        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            saveCellularTrafficError(ColorDrawable(Color.GREEN))
        }.apply {
            Assert.assertNotNull(stateList.find { it.first is SaveCellularTrafficCondition })
        }

        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            saveCellularTrafficError(android.R.drawable.btn_dialog)
        }.apply {
            Assert.assertNotNull(stateList.find { it.first is SaveCellularTrafficCondition })
        }
    }

    @Test
    fun testSaveCellularTrafficCondition() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val request = ImageRequest(context, "http://sample.com/sample.jpeg") {
            depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
        }

        SaveCellularTrafficCondition.apply {
            Assert.assertTrue(
                accept(
                    request.newRequest {
                        depth(LOCAL, SAVE_CELLULAR_TRAFFIC_KEY)
                    },
                    DepthException("")
                )
            )
            Assert.assertFalse(
                accept(
                    request.newRequest {
                        depth(MEMORY, SAVE_CELLULAR_TRAFFIC_KEY)
                    },
                    DepthException("")
                )
            )
            Assert.assertFalse(accept(request, null))

            Assert.assertEquals(
                "SaveCellularTrafficCondition",
                toString()
            )
        }
    }
}