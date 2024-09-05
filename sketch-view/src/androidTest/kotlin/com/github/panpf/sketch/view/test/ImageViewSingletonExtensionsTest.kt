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

package com.github.panpf.sketch.view.test

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.loadAssetImage
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.loadResourceImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.MediumImageViewTestActivity
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import java.io.File
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class ImageViewSingletonExtensionsTest {

    @Test
    fun testLoadImage() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val activity = MediumImageViewTestActivity::class.launchActivity().getActivitySync()
        val imageView = activity.imageView

        assertNull(imageView.drawable)
        imageView.loadImage(ResourceImages.jpeg.uri).job.join()
        assertNotNull(imageView.drawable)

        withContext(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        assertNull(imageView.drawable)
        imageView.loadImage(Uri.parse(ResourceImages.png.uri)).job.join()
        assertNotNull(imageView.drawable)
        imageView.setImageDrawable(null)
        assertNull(imageView.drawable)
        imageView.loadImage(null as Uri?).job.join()
        assertNull(imageView.drawable)

        withContext(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        assertNull(imageView.drawable)
        imageView.loadImage(com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher).job.join()
        assertNotNull(imageView.drawable)
        withContext(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        assertNull(imageView.drawable)
        imageView.loadImage(null as Int?).job.join()
        assertNull(imageView.drawable)

        withContext(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        assertNull(imageView.drawable)
        imageView.loadResourceImage(com.github.panpf.sketch.test.utils.core.R.drawable.test).job.join()
        assertNotNull(imageView.drawable)
        withContext(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        assertNull(imageView.drawable)
        imageView.loadResourceImage(null).job.join()
        assertNull(imageView.drawable)

        withContext(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        assertNull(imageView.drawable)
        imageView.loadResourceImage(
            context.packageName,
            com.github.panpf.sketch.test.utils.core.R.drawable.test
        ).job.join()
        assertNotNull(imageView.drawable)

        withContext(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        assertNull(imageView.drawable)
        imageView.loadImage(ResourceImages.animGif.uri).job.join()
        assertNotNull(imageView.drawable)
        withContext(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        assertNull(imageView.drawable)
        imageView.loadAssetImage(null).job.join()
        assertNull(imageView.drawable)

        withContext(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        assertNull(imageView.drawable)

        val file = sketch.components.newFetcherOrThrow(
                ImageRequest(context, ResourceImages.png.uri).toRequestContext(sketch, Size.Empty)
            ).fetch().getOrThrow().dataSource.getFile(sketch).toFile()
        imageView.loadImage(file).job.join()
        assertNotNull(imageView.drawable)
        withContext(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        assertNull(imageView.drawable)
        imageView.loadImage(null as File?).job.join()
        assertNull(imageView.drawable)

        withContext(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        assertNull(imageView.drawable)
        imageView.loadImage(null as String?).job.join()
        assertNull(imageView.drawable)
    }
}