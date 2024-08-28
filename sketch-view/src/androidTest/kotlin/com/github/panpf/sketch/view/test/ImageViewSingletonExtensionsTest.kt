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
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ImageViewSingletonExtensionsTest {

    @Test
    fun testLoadImage() {
        val (context, sketch) = getTestContextAndSketch()

        val activity = MediumImageViewTestActivity::class.launchActivity().getActivitySync()
        val imageView = activity.imageView

        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.loadImage(ResourceImages.jpeg.uri).job.join()
        }
        Assert.assertNotNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.loadImage(Uri.parse(ResourceImages.png.uri)).job.join()
        }
        Assert.assertNotNull(imageView.drawable)
        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.loadImage(null as Uri?).job.join()
        }
        Assert.assertNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.loadImage(com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher).job.join()
        }
        Assert.assertNotNull(imageView.drawable)
        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.loadImage(null as Int?).job.join()
        }
        Assert.assertNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.loadResourceImage(com.github.panpf.sketch.test.utils.core.R.drawable.test).job.join()
        }
        Assert.assertNotNull(imageView.drawable)
        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.loadResourceImage(null).job.join()
        }
        Assert.assertNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.loadResourceImage(
                context.packageName,
                com.github.panpf.sketch.test.utils.core.R.drawable.test
            ).job.join()
        }
        Assert.assertNotNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.loadImage(ResourceImages.animGif.uri).job.join()
        }
        Assert.assertNotNull(imageView.drawable)
        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.loadAssetImage(null).job.join()
        }
        Assert.assertNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)

        val file = runBlocking {
            sketch.components.newFetcherOrThrow(ImageRequest(context, ResourceImages.png.uri))
                .fetch().getOrThrow().dataSource.getFile().toFile()
        }
        runBlocking {
            imageView.loadImage(file).job.join()
        }
        Assert.assertNotNull(imageView.drawable)
        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.loadImage(null as File?).job.join()
        }
        Assert.assertNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.loadImage(null as String?).job.join()
        }
        Assert.assertNull(imageView.drawable)
    }
}