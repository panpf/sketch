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

package com.github.panpf.sketch.core.android.test.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.SystemCallbacks
import com.github.panpf.tools4a.network.ktx.isCellularNetworkConnected
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SystemCallbacksTest {

    @Test
    fun test() {
        val (context, sketch) = getTestContextAndSketch()
        val systemCallbacks = SystemCallbacks(sketch)
        try {
            systemCallbacks.apply {
                Assert.assertEquals(false, isCellularNetworkConnected)

                systemCallbacks.register()
                Assert.assertEquals(
                    context.isCellularNetworkConnected(),
                    isCellularNetworkConnected
                )

                Assert.assertFalse(isShutdown)
                shutdown()
                Assert.assertTrue(isShutdown)
            }
        } finally {
            systemCallbacks.shutdown()
        }
    }
}