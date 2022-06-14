package com.github.panpf.sketch.test.util

import android.content.ComponentCallbacks2
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.util.awaitStarted
import com.github.panpf.sketch.util.getLifecycle
import com.github.panpf.sketch.util.getMimeTypeFromUrl
import com.github.panpf.sketch.util.getTrimLevelName
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
        Assert.assertEquals("BACKGROUND", getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_BACKGROUND))
        Assert.assertEquals("UI_HIDDEN", getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN))
        Assert.assertEquals("RUNNING_CRITICAL", getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL))
        Assert.assertEquals("RUNNING_LOW", getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW))
        Assert.assertEquals("RUNNING_MODERATE", getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE))
        Assert.assertEquals("UNKNOWN", getTrimLevelName(34))
        Assert.assertEquals("UNKNOWN", getTrimLevelName(-1))
    }
}