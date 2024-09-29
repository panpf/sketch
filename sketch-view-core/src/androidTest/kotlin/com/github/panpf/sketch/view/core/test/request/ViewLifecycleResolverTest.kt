package com.github.panpf.sketch.view.core.test.request

import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ViewLifecycleResolver
import com.github.panpf.sketch.request.findLifecycle
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ViewLifecycleResolverTest {

    @Test
    fun testFindLifecycle() {
        val context = getTestContext()
        assertNull(context.findLifecycle())

        assertNull(context.applicationContext.findLifecycle())

        TestActivity::class.launchActivity().use { scenario ->
            val activity = scenario.getActivitySync()
            assertSame(activity.lifecycle, activity.findLifecycle())
        }
    }

    @Test
    fun testLifecycle() = runTest {
        val context = getTestContext()
        val imageView = ImageView(context)
        assertTrue(ViewLifecycleResolver(imageView).lifecycle() is GlobalLifecycle)

        TestActivity::class.launchActivity().use { scenario ->
            val activity = scenario.getActivitySync()
            withContext(Dispatchers.Main) {
                activity.setContentView(imageView, LayoutParams(500, 500))
            }
            block(100)
            assertTrue(ViewLifecycleResolver(imageView).lifecycle() !is GlobalLifecycle)
        }

        TestActivity::class.launchActivity().use { scenario ->
            val activity = scenario.getActivitySync()
            val imageView1 = ImageView(activity)
            assertTrue(ViewLifecycleResolver(imageView1).lifecycle() !is GlobalLifecycle)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val imageView1 = ImageView(context)
        val element1 = ViewLifecycleResolver(imageView1)
        val element11 = ViewLifecycleResolver(imageView1)
        val imageView2 = ImageView(context)
        val element2 = ViewLifecycleResolver(imageView2)

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = element2)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element2.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val imageView = ImageView(context)
        assertEquals(
            expected = "ViewLifecycleResolver(${imageView})",
            actual = ViewLifecycleResolver(imageView).toString()
        )
    }
}