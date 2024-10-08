package com.github.panpf.sketch.view.core.test.target

import android.R.id
import android.R.layout
import android.widget.RemoteViews
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.target.RemoteViewsTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.getDrawableCompat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteViewsTargetTest {

    @Test
    fun test() {
        val (context, sketch) = getTestContextAndSketch()
        val remoteViews =
            RemoteViews(context.packageName, layout.activity_list_item)
        val requestContext = RequestContext(sketch, ImageRequest(context, null))

        var callbackCount = 0
        RemoteViewsTarget(remoteViews, id.icon) {
            callbackCount++
        }.apply {
            Assert.assertEquals(0, callbackCount)

            onStart(
                requestContext,
                context.getDrawableCompat(android.R.drawable.bottom_bar).asSketchImage()
            )
            Assert.assertEquals(1, callbackCount)

            onStart(requestContext, null)
            Assert.assertEquals(1, callbackCount)

            onError(
                requestContext,
                context.getDrawableCompat(android.R.drawable.bottom_bar).asSketchImage()
            )
            Assert.assertEquals(2, callbackCount)

            onError(requestContext, null)
            Assert.assertEquals(2, callbackCount)

            onSuccess(
                requestContext,
                context.getDrawableCompat(android.R.drawable.bottom_bar).asSketchImage()
            )
            Assert.assertEquals(3, callbackCount)
        }

        callbackCount = 0
        val requestContext2 =
            RequestContext(sketch, ImageRequest(context, null) { allowNullImage() })
        RemoteViewsTarget(remoteViews, id.icon) {
            callbackCount++
        }.apply {
            Assert.assertEquals(0, callbackCount)

            onStart(
                requestContext2,
                context.getDrawableCompat(android.R.drawable.bottom_bar).asSketchImage()
            )
            Assert.assertEquals(1, callbackCount)

            onStart(requestContext2, null)
            Assert.assertEquals(2, callbackCount)

            onError(
                requestContext2,
                context.getDrawableCompat(android.R.drawable.bottom_bar).asSketchImage()
            )
            Assert.assertEquals(3, callbackCount)

            onError(requestContext2, null)
            Assert.assertEquals(4, callbackCount)

            onSuccess(
                requestContext2,
                context.getDrawableCompat(android.R.drawable.bottom_bar).asSketchImage()
            )
            Assert.assertEquals(5, callbackCount)
        }
    }
}