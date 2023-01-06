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
package com.github.panpf.sketch.test.util

import android.content.ComponentCallbacks2
import android.content.Context
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.util.awaitStarted
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.sketch.util.getCacheFileFromStreamDataSource
import com.github.panpf.sketch.util.getLifecycle
import com.github.panpf.sketch.util.getMimeTypeFromUrl
import com.github.panpf.sketch.util.getTrimLevelName
import com.github.panpf.sketch.util.intMerged
import com.github.panpf.sketch.util.intSplit
import com.github.panpf.sketch.util.isMainThread
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.util.requiredWorkThread
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileNotFoundException

@RunWith(AndroidJUnit4::class)
class UtilsTest {

    @Test
    fun testIsMainThread() {
        Assert.assertFalse(isMainThread())
        Assert.assertTrue(runBlocking(Dispatchers.Main) {
            isMainThread()
        })
    }

    @Test
    fun testRequiredMainThread() {
        assertThrow(IllegalStateException::class) {
            requiredMainThread()
        }
        runBlocking(Dispatchers.Main) {
            requiredMainThread()
        }
    }

    @Test
    fun testRequiredWorkThread() {
        requiredWorkThread()

        assertThrow(IllegalStateException::class) {
            runBlocking(Dispatchers.Main) {
                requiredWorkThread()
            }
        }
    }

    @Test
    fun testGetLifecycle() {
        val context = InstrumentationRegistry.getInstrumentation().context
        Assert.assertNull(context.getLifecycle())

        val activity = TestActivity::class.launchActivity().getActivitySync()
        Assert.assertNotNull((activity as Context).getLifecycle())
    }

    @Test
    fun testAwaitStarted() {
        val lifecycleOwner = object : LifecycleOwner {
            private var lifecycle: Lifecycle? = null
            override fun getLifecycle(): Lifecycle {
                return lifecycle ?: LifecycleRegistry(this).apply {
                    lifecycle = this
                }
            }
        }
        val myLifecycle = lifecycleOwner.lifecycle as LifecycleRegistry

        var state = ""
        runBlocking {
            async(Dispatchers.Main) {
                state = "waiting"
                myLifecycle.awaitStarted()
                state = "started"
            }
            delay(10)
            Assert.assertEquals("waiting", state)
            delay(10)
            Assert.assertEquals("waiting", state)
            runBlocking(Dispatchers.Main) {
                myLifecycle.currentState = STARTED
            }
            delay(10)
            Assert.assertEquals("started", state)

            state = ""
            async(Dispatchers.Main) {
                state = "waiting"
                myLifecycle.awaitStarted()
                state = "started"
            }
            delay(10)
            Assert.assertEquals("started", state)
        }
    }

    @Test
    fun testGetMimeTypeFromUrl() {
        Assert.assertEquals("image/jpeg", getMimeTypeFromUrl("http://sample.com/sample.jpeg"))
        Assert.assertEquals(
            "image/png",
            getMimeTypeFromUrl("http://sample.com/sample.png#path?name=david")
        )
    }

    @Test
    fun testGetTrimLevelName() {
        Assert.assertEquals("COMPLETE", getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_COMPLETE))
        Assert.assertEquals("MODERATE", getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_MODERATE))
        Assert.assertEquals(
            "BACKGROUND",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_BACKGROUND)
        )
        Assert.assertEquals(
            "UI_HIDDEN",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN)
        )
        Assert.assertEquals(
            "RUNNING_CRITICAL",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL)
        )
        Assert.assertEquals(
            "RUNNING_LOW",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW)
        )
        Assert.assertEquals(
            "RUNNING_MODERATE",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE)
        )
        Assert.assertEquals("UNKNOWN", getTrimLevelName(34))
        Assert.assertEquals("UNKNOWN", getTrimLevelName(-1))
    }

    @Test
    fun testFitScale() {
        val context = getTestContext()
        Assert.assertTrue(ImageView(context).apply { scaleType = ScaleType.FIT_START }.fitScale)
        Assert.assertTrue(ImageView(context).apply { scaleType = ScaleType.FIT_CENTER }.fitScale)
        Assert.assertTrue(ImageView(context).apply { scaleType = ScaleType.FIT_END }.fitScale)
        Assert.assertFalse(ImageView(context).apply { scaleType = ScaleType.FIT_XY }.fitScale)
        Assert.assertFalse(ImageView(context).apply { scaleType = ScaleType.CENTER_CROP }.fitScale)
        Assert.assertFalse(ImageView(context).apply { scaleType = ScaleType.CENTER }.fitScale)
        Assert.assertTrue(ImageView(context).apply { scaleType = ScaleType.CENTER_INSIDE }.fitScale)
        Assert.assertFalse(ImageView(context).apply { scaleType = ScaleType.MATRIX }.fitScale)
    }

    @Test
    fun testIntMergedAndIntSplit() {
        intSplit(intMerged(39, 25)).apply {
            Assert.assertEquals(39, first)
            Assert.assertEquals(25, second)
        }
        intSplit(intMerged(7, 43)).apply {
            Assert.assertEquals(7, first)
            Assert.assertEquals(43, second)
        }

        assertThrow(IllegalArgumentException::class) {
            intMerged(-1, 25)
        }
        assertThrow(IllegalArgumentException::class) {
            intMerged(Short.MAX_VALUE + 1, 25)
        }
        assertThrow(IllegalArgumentException::class) {
            intMerged(25, -1)
        }
        assertThrow(IllegalArgumentException::class) {
            intMerged(25, Short.MAX_VALUE + 1)
        }
    }

    @Test
    fun testGetCacheFileFromStreamDataSource() {
        val (context, sketch) = getTestContextAndNewSketch()
        AssetDataSource(
            sketch = sketch,
            request = LoadRequest(context, newAssetUri("sample.jpeg")),
            assetFileName = "sample.jpeg"
        ).apply {
            val file = getCacheFileFromStreamDataSource(sketch, request, this)
            Assert.assertTrue(file.path.contains("/cache/"))
            val file1 = getCacheFileFromStreamDataSource(sketch, request, this)
            Assert.assertEquals(file.path, file1.path)
        }

        assertThrow(FileNotFoundException::class) {
            AssetDataSource(
                sketch = sketch,
                request = LoadRequest(context, newAssetUri("not_found.jpeg")),
                assetFileName = "not_found.jpeg"
            ).apply {
                getCacheFileFromStreamDataSource(sketch, request, this)
            }
        }
    }
}