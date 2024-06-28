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
package com.github.panpf.sketch.core.android.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.OneShotDisposable
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisposableTest {

    @Test
    fun testOneShotDisposable() {
        val context = getTestContext()
        runBlocking {
            val job = async {
                delay(100)
                delay(100)
                delay(100)
                ImageResult.Error(ImageRequest(context, ResourceImages.jpeg.uri), null, Exception("test"))
            }
            val disposable = OneShotDisposable(job)
            Assert.assertFalse(disposable.isDisposed)
            delay(100)
            Assert.assertFalse(disposable.isDisposed)
            disposable.dispose()
            delay(100)
            Assert.assertTrue(disposable.isDisposed)
            disposable.dispose()
        }
    }
}