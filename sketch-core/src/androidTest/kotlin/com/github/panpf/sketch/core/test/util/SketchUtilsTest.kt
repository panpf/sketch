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
package com.github.panpf.sketch.core.test.util

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.core.test.ImageViewExtensionsTest
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.singleton.displayAssetImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.InternalDrawableWrapperImpl
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.sketch.util.forEachSketchCountBitmapDrawable
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SketchUtilsTest {

    @Test
    fun testRequestManagerOrNull() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val imageView = ImageView(context)

        Assert.assertNull(SketchUtils.requestManagerOrNull(imageView))
        imageView.displayAssetImage(AssetImages.jpeg.fileName)
        Assert.assertNotNull(SketchUtils.requestManagerOrNull(imageView))
    }

    @Test
    fun testForeachSketchCountDrawable() {
        val (context, sketch) = getTestContextAndSketch()
        val bitmap = Bitmap.createBitmap(100, 200, RGB_565)
        val countBitmap = CountBitmap(
            cacheKey = "cacheKey",
            originBitmap = bitmap,
            bitmapPool = sketch.bitmapPool,
            disallowReuseBitmap = false,
        )
        val resources = context.resources
        val countDrawable = SketchCountBitmapDrawable(
            resources = resources,
            countBitmap = countBitmap,
        )
        val colorDrawable = ColorDrawable(Color.BLUE)
        val colorDrawable2 = ColorDrawable(Color.GREEN)

        countDrawable.let { drawable ->
            mutableListOf<String>().also { list ->
                drawable.forEachSketchCountBitmapDrawable { list.add(it::class.java.simpleName) }
            }
        }.apply {
            Assert.assertEquals(listOf("SketchCountBitmapDrawable"), this)
        }

        CrossfadeDrawable(
            countDrawable,
            CrossfadeDrawable(colorDrawable2, countDrawable)
        ).let { drawable ->
            mutableListOf<String>().also { list ->
                drawable.forEachSketchCountBitmapDrawable { list.add(it::class.java.simpleName) }
            }
        }.apply {
            Assert.assertEquals(
                listOf("SketchCountBitmapDrawable", "SketchCountBitmapDrawable"),
                this
            )
        }

        CrossfadeDrawable(
            countDrawable,
            CrossfadeDrawable(countDrawable, colorDrawable2)
        ).let { drawable ->
            mutableListOf<String>().also { list ->
                drawable.forEachSketchCountBitmapDrawable { list.add(it::class.java.simpleName) }
            }
        }.apply {
            Assert.assertEquals(
                listOf("SketchCountBitmapDrawable", "SketchCountBitmapDrawable"),
                this
            )
        }

        TransitionDrawable(
            arrayOf(
                countDrawable,
                TransitionDrawable(arrayOf(colorDrawable2, countDrawable))
            )
        ).let { drawable ->
            mutableListOf<String>().also { list ->
                drawable.forEachSketchCountBitmapDrawable { list.add(it::class.java.simpleName) }
            }
        }.apply {
            Assert.assertEquals(
                listOf("SketchCountBitmapDrawable", "SketchCountBitmapDrawable"),
                this
            )
        }

        TransitionDrawable(
            arrayOf(
                countDrawable,
                TransitionDrawable(arrayOf(countDrawable, colorDrawable2))
            )
        ).let { drawable ->
            mutableListOf<String>().also { list ->
                drawable.forEachSketchCountBitmapDrawable { list.add(it::class.java.simpleName) }
            }
        }.apply {
            Assert.assertEquals(
                listOf("SketchCountBitmapDrawable", "SketchCountBitmapDrawable"),
                this
            )
        }

        DrawableWrapperCompat(countDrawable).let { drawable ->
            mutableListOf<String>().also { list ->
                drawable.forEachSketchCountBitmapDrawable { list.add(it::class.java.simpleName) }
            }
        }.apply {
            Assert.assertEquals(listOf("SketchCountBitmapDrawable"), this)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            InternalDrawableWrapperImpl(countDrawable).let { drawable ->
                mutableListOf<String>().also { list ->
                    drawable.forEachSketchCountBitmapDrawable { list.add(it::class.java.simpleName) }
                }
            }.apply {
                Assert.assertEquals(listOf("SketchCountBitmapDrawable"), this)
            }
        }


        colorDrawable.let { drawable ->
            mutableListOf<String>().also { list ->
                drawable.forEachSketchCountBitmapDrawable { list.add(it::class.java.simpleName) }
            }
        }.apply {
            Assert.assertEquals(listOf<String>(), this)
        }

        CrossfadeDrawable(
            colorDrawable,
            CrossfadeDrawable(colorDrawable2, colorDrawable)
        ).let { drawable ->
            mutableListOf<String>().also { list ->
                drawable.forEachSketchCountBitmapDrawable { list.add(it::class.java.simpleName) }
            }
        }.apply {
            Assert.assertEquals(listOf<String>(), this)
        }

        TransitionDrawable(
            arrayOf(
                colorDrawable,
                TransitionDrawable(arrayOf(colorDrawable2, colorDrawable))
            )
        ).let { drawable ->
            mutableListOf<String>().also { list ->
                drawable.forEachSketchCountBitmapDrawable { list.add(it::class.java.simpleName) }
            }
        }.apply {
            Assert.assertEquals(listOf<String>(), this)
        }

        DrawableWrapperCompat(colorDrawable).let { drawable ->
            mutableListOf<String>().also { list ->
                drawable.forEachSketchCountBitmapDrawable { list.add(it::class.java.simpleName) }
            }
        }.apply {
            Assert.assertEquals(listOf<String>(), this)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            InternalDrawableWrapperImpl(colorDrawable).let { drawable ->
                mutableListOf<String>().also { list ->
                    drawable.forEachSketchCountBitmapDrawable { list.add(it::class.java.simpleName) }
                }
            }.apply {
                Assert.assertEquals(listOf<String>(), this)
            }
        }
    }

    @Test
    fun testGetRequest() {
        val activity =
            ImageViewExtensionsTest.TestActivity::class.launchActivity().getActivitySync()
        val imageView = ImageView(activity)
        runBlocking(Dispatchers.Main) {
            activity.setContentView(imageView, LayoutParams(500, 500))
        }
        Thread.sleep(100)

        Assert.assertNull(SketchUtils.getRequest(imageView))
        imageView.displayAssetImage(AssetImages.jpeg.fileName)
        Thread.sleep(100)
        Assert.assertNotNull(SketchUtils.getRequest(imageView))
    }

    @Test
    fun testGetSketch() {
        val activity =
            ImageViewExtensionsTest.TestActivity::class.launchActivity().getActivitySync()
        val imageView = ImageView(activity)
        runBlocking(Dispatchers.Main) {
            activity.setContentView(imageView, LayoutParams(500, 500))
        }
        Thread.sleep(100)

        Assert.assertNull(SketchUtils.getSketch(imageView))
        imageView.displayAssetImage(AssetImages.jpeg.fileName)
        Thread.sleep(100)
        Assert.assertNotNull(SketchUtils.getSketch(imageView))
    }
}