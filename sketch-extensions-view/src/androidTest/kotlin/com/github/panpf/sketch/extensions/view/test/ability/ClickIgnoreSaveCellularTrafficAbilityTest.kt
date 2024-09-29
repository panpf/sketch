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

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.ability.isClickIgnoreSaveCellularTrafficEnabled
import com.github.panpf.sketch.ability.setClickIgnoreSaveCellularTrafficEnabled
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.SaveCellularTrafficRequestInterceptor
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.request.target
import com.github.panpf.sketch.state.ColorDrawableStateImage
import com.github.panpf.sketch.state.saveCellularTrafficError
import com.github.panpf.sketch.test.singleton.request.execute
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
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

    @Test
    fun test() = runTest {
        val (_, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }
        TestActivity::class.launchActivity().use { scenario ->
            val activity = scenario.getActivitySync()
            val imageView1: SketchImageView
            withContext(Dispatchers.Main) {
                val imageView = SketchImageView(activity).apply {
                    setClickIgnoreSaveCellularTrafficEnabled(true)
                    imageView1 = this
                }
                activity.setContentView(imageView)

                ImageRequest(activity, TestHttpStack.testImages.first().uri) {
                    components {
                        addRequestInterceptor(
                            SaveCellularTrafficRequestInterceptor(
                                isCellularNetworkConnected = {
                                    true
                                }
                            )
                        )
                    }
                    saveCellularTraffic(true)
                    memoryCachePolicy(CachePolicy.DISABLED)
                    resultCachePolicy(CachePolicy.DISABLED)
                    downloadCachePolicy(CachePolicy.DISABLED)
                    error(ColorDrawableStateImage(IntColor(Color.RED))) {
                        saveCellularTrafficError(ColorDrawableStateImage(IntColor(Color.YELLOW)))
                    }
                    target(imageView)
                }.execute(sketch)
            }
            block(100)
            assertEquals(
                expected = Color.YELLOW,
                actual = imageView1.drawable!!.asOrThrow<ColorDrawable>().color
            )

            withContext(Dispatchers.Main) {
                imageView1.performClick()
            }
            block(100)
            assertTrue(
                actual = imageView1.drawable is BitmapDrawable,
                message = "drawable=${imageView1.drawable}"
            )
        }
    }
}