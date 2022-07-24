package com.github.panpf.sketch.test.util

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.VectorDrawable
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.getDrawableCompat
import com.github.panpf.sketch.util.getLifecycle
import com.github.panpf.sketch.util.getXmlDrawableCompat
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.xmlpull.v1.XmlPullParserException

@RunWith(AndroidJUnit4::class)
class ContextsTest {

    @Test
    fun testGetLifecycle() {
        val context = getTestContext()
        Assert.assertNull(context.getLifecycle())

        Assert.assertNull(context.applicationContext.getLifecycle())

        val activity = TestActivity::class.launchActivity().getActivitySync()
        Assert.assertSame(activity.lifecycle, activity.asOrThrow<Context>().getLifecycle())
    }

    @Test
    fun testGetDrawableCompat() {
        val context = getTestContext()

        Assert.assertNotNull(context.getDrawableCompat(android.R.drawable.ic_delete))
        assertThrow(Resources.NotFoundException::class) {
            context.getDrawableCompat(1101)
        }

        Assert.assertNotNull(
            context.resources.getDrawableCompat(android.R.drawable.ic_delete, null)
        )
        assertThrow(Resources.NotFoundException::class) {
            context.resources.getDrawableCompat(1101, null)
        }
    }

    @Test
    fun testGetXmlDrawableCompat() {
        val context = getTestContext()
        context.getXmlDrawableCompat(
            context.resources,
            com.github.panpf.sketch.test.R.drawable.ic_cloudy
        ).apply {
            if (Build.VERSION.SDK_INT >= 24) {
                Assert.assertTrue(this is VectorDrawable)
            } else {
                Assert.assertTrue(this is VectorDrawableCompat)
            }
        }

        if (Build.VERSION.SDK_INT < 24) {
            assertThrow(XmlPullParserException::class) {
                context.getXmlDrawableCompat(
                    context.resources,
                    com.github.panpf.sketch.test.R.drawable.test_error
                )
            }
        }
    }
}