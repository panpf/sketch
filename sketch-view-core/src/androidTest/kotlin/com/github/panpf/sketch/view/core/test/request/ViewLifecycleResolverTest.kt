package com.github.panpf.sketch.view.core.test.request

import android.content.Context
import com.github.panpf.sketch.request.findLifecycle
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import org.junit.Assert
import org.junit.Test

class ViewLifecycleResolverTest {

    // TODO test

    @Test
    fun testFindLifecycle() {
        val context = getTestContext()
        Assert.assertNull(context.findLifecycle())

        Assert.assertNull(context.applicationContext.findLifecycle())

        val activity = TestActivity::class.launchActivity().getActivitySync()
        Assert.assertSame(activity.lifecycle, activity.asOrThrow<Context>().findLifecycle())
    }
}