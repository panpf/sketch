package com.github.panpf.sketch.view.core.test.target

import android.R.id
import android.R.layout
import android.widget.RemoteViews
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.target.RemoteViewsTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.fakeErrorImageResult
import com.github.panpf.sketch.test.utils.fakeSuccessImageResult
import com.github.panpf.sketch.test.utils.getDrawableCompat
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class RemoteViewsTargetTest {

    @Test
    fun test() {
        val (context, sketch) = getTestContextAndSketch()
        val remoteViews =
            RemoteViews(context.packageName, layout.activity_list_item)
        val request = ImageRequest(context, null)

        var callbackCount = 0
        RemoteViewsTarget(remoteViews, id.icon) {
            callbackCount++
        }.apply {
            assertEquals(0, callbackCount)

            onStart(
                sketch, request,
                context.getDrawableCompat(android.R.drawable.ic_lock_lock).asImage()
            )
            assertEquals(1, callbackCount)

            onStart(sketch, request, null)
            assertEquals(1, callbackCount)

            onError(
                sketch,
                request,
                fakeErrorImageResult(context),
                context.getDrawableCompat(android.R.drawable.ic_lock_lock).asImage()
            )
            assertEquals(2, callbackCount)

            onError(sketch, request, fakeErrorImageResult(context), null)
            assertEquals(2, callbackCount)

            onSuccess(
                sketch,
                request,
                fakeSuccessImageResult(context),
                context.getDrawableCompat(android.R.drawable.ic_lock_lock).asImage()
            )
            assertEquals(3, callbackCount)
        }

        callbackCount = 0
        val request2 = ImageRequest(context, null) { allowNullImage() }
        RemoteViewsTarget(remoteViews, id.icon) {
            callbackCount++
        }.apply {
            assertEquals(0, callbackCount)

            onStart(
                sketch, request2,
                context.getDrawableCompat(android.R.drawable.ic_lock_lock).asImage()
            )
            assertEquals(1, callbackCount)

            onStart(sketch, request2, null)
            assertEquals(2, callbackCount)

            onError(
                sketch,
                request2,
                fakeErrorImageResult(context),
                context.getDrawableCompat(android.R.drawable.ic_lock_lock).asImage()
            )
            assertEquals(3, callbackCount)

            onError(sketch, request2, fakeErrorImageResult(context), null)
            assertEquals(4, callbackCount)

            onSuccess(
                sketch,
                request2,
                fakeSuccessImageResult(context),
                context.getDrawableCompat(android.R.drawable.ic_lock_lock).asImage()
            )
            assertEquals(5, callbackCount)
        }
    }
}