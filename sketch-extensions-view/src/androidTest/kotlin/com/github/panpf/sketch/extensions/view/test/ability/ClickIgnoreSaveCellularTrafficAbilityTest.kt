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

package com.github.panpf.sketch.extensions.view.test.ability

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.ability.isClickIgnoreSaveCellularTrafficEnabled
import com.github.panpf.sketch.ability.setClickIgnoreSaveCellularTrafficEnabled
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ClickIgnoreSaveCellularTrafficAbilityTest {

    @Test
    fun testClickIgnoreSaveCellularTrafficEnabled() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val imageView = SketchImageView(context)

        assertFalse(imageView.isClickIgnoreSaveCellularTrafficEnabled)

        imageView.setClickIgnoreSaveCellularTrafficEnabled()
        assertTrue(imageView.isClickIgnoreSaveCellularTrafficEnabled)

        imageView.setClickIgnoreSaveCellularTrafficEnabled(false)
        assertFalse(imageView.isClickIgnoreSaveCellularTrafficEnabled)

        imageView.setClickIgnoreSaveCellularTrafficEnabled(true)
        assertTrue(imageView.isClickIgnoreSaveCellularTrafficEnabled)
    }

    // TODO test
}