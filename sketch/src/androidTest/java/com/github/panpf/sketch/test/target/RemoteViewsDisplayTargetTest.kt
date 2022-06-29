package com.github.panpf.sketch.test.target

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.RemoteViews
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.target.RemoteViewsDisplayTarget
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteViewsDisplayTargetTest {

    @Test
    fun test() {
        val context = getTestContext()
        val remoteViews =
            RemoteViews(context.packageName, android.R.layout.activity_list_item)

        var callbackCount = 0
        RemoteViewsDisplayTarget(remoteViews, android.R.id.icon) {
            callbackCount++
        }.apply {
            Assert.assertEquals(0, callbackCount)

            onStart(context.getDrawable(android.R.drawable.bottom_bar)!!)
            Assert.assertEquals(1, callbackCount)

            onStart(null)
            Assert.assertEquals(2, callbackCount)

            onError(context.getDrawable(android.R.drawable.bottom_bar)!!)
            Assert.assertEquals(3, callbackCount)

            onError(null)
            Assert.assertEquals(4, callbackCount)

            onSuccess(context.getDrawable(android.R.drawable.bottom_bar)!!)
            Assert.assertEquals(5, callbackCount)
        }

        callbackCount = 0
        RemoteViewsDisplayTarget(remoteViews, android.R.id.icon, ignoreNullDrawable = true) {
            callbackCount++
        }.apply {
            Assert.assertEquals(0, callbackCount)

            onStart(context.getDrawable(android.R.drawable.bottom_bar)!!)
            Assert.assertEquals(1, callbackCount)

            onStart(null)
            Assert.assertEquals(1, callbackCount)

            onError(context.getDrawable(android.R.drawable.bottom_bar)!!)
            Assert.assertEquals(2, callbackCount)

            onError(null)
            Assert.assertEquals(2, callbackCount)

            onSuccess(context.getDrawable(android.R.drawable.bottom_bar)!!)
            Assert.assertEquals(3, callbackCount)
        }
    }
}