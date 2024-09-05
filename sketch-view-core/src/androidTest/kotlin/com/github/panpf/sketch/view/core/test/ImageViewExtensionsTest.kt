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

package com.github.panpf.sketch.view.core.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.disposeLoad
import com.github.panpf.sketch.imageResult
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.test.singleton.loadImage
import com.github.panpf.sketch.test.utils.DelayDecodeInterceptor
import com.github.panpf.sketch.test.utils.MediumImageViewTestActivity
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ImageViewExtensionsTest {

    @Test
    fun testDisposeLoad() = runTest {
        val activity = MediumImageViewTestActivity::class.launchActivity().getActivitySync()
        val imageView = activity.imageView

        assertNull(imageView.drawable)
        imageView.loadImage(ResourceImages.jpeg.uri).job.join()
        assertNotNull(imageView.drawable)

        withContext(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        assertNull(imageView.drawable)
        imageView.loadImage(ResourceImages.png.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            components {
                addDecodeInterceptor(DelayDecodeInterceptor(1000) {
                    imageView.disposeLoad()
                })
            }
        }.job.join()
        assertNull(imageView.drawable)
    }

    @Test
    fun testImageResult() = runTest {
        val activity = MediumImageViewTestActivity::class.launchActivity().getActivitySync()
        val imageView = activity.imageView

        assertNull(imageView.imageResult)

        imageView.loadImage(ResourceImages.jpeg.uri).job.join()
        assertTrue(imageView.imageResult is ImageResult.Success)

        imageView.loadImage("file:///android_asset/fake.jpeg").job.join()
        assertTrue(imageView.imageResult is ImageResult.Error)

        imageView.loadImage(ResourceImages.png.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            components {
                addDecodeInterceptor(DelayDecodeInterceptor(1000) {
                    imageView.disposeLoad()
                })
            }
        }.job.join()
        assertNull(imageView.imageResult)
    }
}