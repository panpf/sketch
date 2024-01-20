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
package com.github.panpf.sketch.extensions.view.core.test.viewability

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.drawable.MaskProgressDrawable
import com.github.panpf.sketch.drawable.RingProgressDrawable
import com.github.panpf.sketch.drawable.SectorProgressDrawable
import com.github.panpf.sketch.ability.ProgressIndicatorAbility
import com.github.panpf.sketch.ability.isShowProgressIndicator
import com.github.panpf.sketch.ability.removeProgressIndicator
import com.github.panpf.sketch.ability.showMaskProgressIndicator
import com.github.panpf.sketch.ability.showRingProgressIndicator
import com.github.panpf.sketch.ability.showSectorProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProgressIndicatorAbilityTest {

    @Test
    fun testExtensions() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val imageView = SketchImageView(context)

        Assert.assertFalse(imageView.isShowProgressIndicator)

        runBlocking(Dispatchers.Main) {
            imageView.showSectorProgressIndicator()
        }
        Assert.assertTrue(imageView.isShowProgressIndicator)
        imageView.viewAbilityList.find { it is ProgressIndicatorAbility }!!
            .let { it as ProgressIndicatorAbility }.apply {
                Assert.assertTrue(this.progressDrawable is SectorProgressDrawable)
            }

        runBlocking(Dispatchers.Main) {
            imageView.removeProgressIndicator()
        }
        Assert.assertFalse(imageView.isShowProgressIndicator)

        runBlocking(Dispatchers.Main) {
            imageView.showMaskProgressIndicator()
        }
        Assert.assertTrue(imageView.isShowProgressIndicator)
        imageView.viewAbilityList.find { it is ProgressIndicatorAbility }!!
            .let { it as ProgressIndicatorAbility }.apply {
                Assert.assertTrue(this.progressDrawable is MaskProgressDrawable)
            }

        runBlocking(Dispatchers.Main) {
            imageView.removeProgressIndicator()
        }
        Assert.assertFalse(imageView.isShowProgressIndicator)

        runBlocking(Dispatchers.Main) {
            imageView.showRingProgressIndicator()
        }
        Assert.assertTrue(imageView.isShowProgressIndicator)
        imageView.viewAbilityList.find { it is ProgressIndicatorAbility }!!
            .let { it as ProgressIndicatorAbility }.apply {
                Assert.assertTrue(this.progressDrawable is RingProgressDrawable)
            }
    }
}