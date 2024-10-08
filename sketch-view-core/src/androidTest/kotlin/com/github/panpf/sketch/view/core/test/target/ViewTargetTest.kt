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

package com.github.panpf.sketch.view.core.test.target

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageOptionsProvider
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ListenerProvider
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.ViewLifecycleResolver
import com.github.panpf.sketch.request.internal.ViewRequestDelegate
import com.github.panpf.sketch.resize.ViewResizeOnDrawHelper
import com.github.panpf.sketch.resize.internal.ViewSizeResolver
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestCrossfadeTransition
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.ViewCrossfadeTransition
import kotlinx.coroutines.Job
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ViewTargetTest {

    @Test
    fun testCurrentImage() {
        val context = getTestContext()
        val imageView = ImageView(context)
        val viewTarget = TestViewTarget(imageView)
        assertEquals(null, viewTarget.currentImage)

        viewTarget.drawable = ColorDrawable(Color.BLUE).asEquitable()
        assertEquals(
            expected = ColorDrawable(Color.BLUE).asEquitable().asImage(),
            actual = viewTarget.currentImage
        )

        viewTarget.drawable = null
        assertEquals(null, viewTarget.currentImage)
    }

    @Test
    fun testNewRequestDelegate() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)

        val imageView = ImageView(context)
        val viewTarget = TestViewTarget(imageView)
        val requestDelegate1 = viewTarget.newRequestDelegate(sketch, request, Job())
        val requestDelegate2 = viewTarget.newRequestDelegate(sketch, request, Job())

        assertNotEquals(requestDelegate1, requestDelegate2)
        assertNotSame(requestDelegate1, requestDelegate2)
        assertTrue(
            actual = requestDelegate1 is ViewRequestDelegate,
            message = "requestDelegate1=$requestDelegate1"
        )
    }

    @Test
    fun testGetListener() {
        val context = getTestContext()
        val imageView = TestImageView(context)
        val viewTarget = TestViewTarget(imageView)

        assertNull(imageView.mListener)
        assertNull(viewTarget.getListener())

        val listener = object : Listener {}
        imageView.mListener = listener

        assertSame(listener, viewTarget.getListener())

        val viewTarget2 = TestViewTarget(null)
        assertNull(viewTarget2.getListener())
    }

    @Test
    fun testGetProgressListener() {
        val context = getTestContext()
        val imageView = TestImageView(context)
        val viewTarget = TestViewTarget(imageView)

        assertNull(imageView.mProgressListener)
        assertNull(viewTarget.getProgressListener())

        val progressListener = ProgressListener { _, _ -> }
        imageView.mProgressListener = progressListener

        assertSame(progressListener, viewTarget.getProgressListener())

        val viewTarget2 = TestViewTarget(null)
        assertNull(viewTarget2.getProgressListener())
    }

    @Test
    fun testGetLifecycleResolver() {
        val context = getTestContext()

        val imageView = ImageView(context)
        val viewTarget = TestViewTarget(imageView)
        assertEquals(ViewLifecycleResolver(imageView), viewTarget.getLifecycleResolver())

        val viewTarget2 = TestViewTarget(null)
        assertEquals(null, viewTarget2.getLifecycleResolver())
    }

    @Test
    fun testGetSizeResolver() {
        val context = getTestContext()

        val imageView = ImageView(context)
        val viewTarget = TestViewTarget(imageView)
        assertEquals(ViewSizeResolver(imageView), viewTarget.getSizeResolver())

        val viewTarget2 = TestViewTarget(null)
        assertEquals(null, viewTarget2.getSizeResolver())
    }

    @Test
    fun testGetResizeOnDrawHelper() {
        val context = getTestContext()
        val imageView = ImageView(context)
        val viewTarget = TestViewTarget(imageView)
        val resizeOnDrawHelper1 = viewTarget.getResizeOnDrawHelper()
        val resizeOnDrawHelper2 = viewTarget.getResizeOnDrawHelper()

        assertEquals(resizeOnDrawHelper1, resizeOnDrawHelper2)
        assertSame(resizeOnDrawHelper1, resizeOnDrawHelper2)
        assertTrue(
            actual = resizeOnDrawHelper1 is ViewResizeOnDrawHelper,
            message = "resizeOnDrawHelper1=$resizeOnDrawHelper1"
        )
    }

    @Test
    fun testConvertTransition() {
        val context = getTestContext()
        val imageView = ImageView(context)
        val viewTarget = TestViewTarget(imageView)

        assertEquals(
            expected = ViewCrossfadeTransition.Factory(
                durationMillis = 2000,
                fadeStart = false,
                preferExactIntrinsicSize = false,
                alwaysUse = true
            ),
            actual = viewTarget.convertTransition(
                CrossfadeTransition.Factory(
                    durationMillis = 2000,
                    fadeStart = false,
                    preferExactIntrinsicSize = false,
                    alwaysUse = true
                )
            )
        )

        assertEquals(
            expected = null,
            actual = viewTarget.convertTransition(
                TestCrossfadeTransition.Factory(
                    durationMillis = 2000,
                    fadeStart = false,
                    preferExactIntrinsicSize = false,
                    alwaysUse = true
                )
            )
        )
    }

    @Test
    fun testGetImageOptions() {
        val context = getTestContext()
        val imageView = TestImageView(context)
        val viewTarget = TestViewTarget(imageView)

        assertNull(imageView.imageOptions)
        assertNull(viewTarget.getImageOptions())

        val imageOptions = ImageOptions()
        imageView.imageOptions = imageOptions

        assertSame(imageOptions, viewTarget.getImageOptions())

        val viewTarget2 = TestViewTarget(null)
        assertNull(viewTarget2.getImageOptions())
    }

    class TestImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
    ) : ImageView(context, attrs, defStyle), ListenerProvider, ImageOptionsProvider {

        var mListener: Listener? = null
        var mProgressListener: ProgressListener? = null

        override var imageOptions: ImageOptions? = null

        override fun getListener(): Listener? {
            return mListener
        }

        override fun getProgressListener(): ProgressListener? {
            return mProgressListener
        }
    }
}