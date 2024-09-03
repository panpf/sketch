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

package com.github.panpf.sketch.extensions.core.android.test.state

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.drawable.ColorDrawableEqualizer
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.panpf.sketch.state.SaveCellularTrafficCondition
import com.github.panpf.sketch.state.saveCellularTrafficError
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class SaveCellularTrafficExtensionsAndroidTest {

    @Test
    fun testSaveCellularTrafficError() {
        ErrorStateImage(IntColorDrawableStateImage(Color.BLACK)).apply {
            assertNull(stateList.find { it.first is SaveCellularTrafficCondition })
        }

        ErrorStateImage(IntColorDrawableStateImage(Color.BLACK)) {
            saveCellularTrafficError()
        }.apply {
            assertNotNull(stateList.find { it.first is SaveCellularTrafficCondition })
        }

        ErrorStateImage(IntColorDrawableStateImage(Color.BLACK)) {
            saveCellularTrafficError(IntColorDrawableStateImage(Color.BLUE))
        }.apply {
            assertNotNull(stateList.find { it.first is SaveCellularTrafficCondition })
        }

        ErrorStateImage(IntColorDrawableStateImage(Color.BLACK)) {
            saveCellularTrafficError(ColorDrawableEqualizer(Color.GREEN))
        }.apply {
            assertNotNull(stateList.find { it.first is SaveCellularTrafficCondition })
        }

        ErrorStateImage(IntColorDrawableStateImage(Color.BLACK)) {
            saveCellularTrafficError(android.R.drawable.btn_dialog)
        }.apply {
            assertNotNull(stateList.find { it.first is SaveCellularTrafficCondition })
        }

        // TODO Drawable, res
    }
}