package com.github.panpf.sketch.view.core.test.request.internal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult.Error
import com.github.panpf.sketch.request.ReusableDisposable
import com.github.panpf.sketch.request.internal.requestManager
import com.github.panpf.sketch.test.singleton.loadImage
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.view.core.R.id
import com.github.panpf.tools4a.test.ktx.getFragmentSync
import com.github.panpf.tools4a.test.ktx.launchFragmentInContainer
import com.github.panpf.tools4j.reflect.ktx.getFieldValue
import com.github.panpf.tools4j.reflect.ktx.setFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ViewRequestManagerTest {

    @Test
    fun testRequestManagerProperty() {
        val context = getTestContext()

        val imageView = ImageView(context)
        assertNull(imageView.getTag(id.sketch_request_manager))

        val requestManager = imageView.requestManager
        assertNotNull(imageView.getTag(id.sketch_request_manager))
        assertSame(imageView.getTag(id.sketch_request_manager), requestManager)

        val requestManager2 = imageView.requestManager
        assertSame(requestManager, requestManager2)
    }

    @Test
    fun testDispose() = runTest {
        val context = getTestContext()

        val imageView = ImageView(context)
        val request = ImageRequest(imageView, ResourceImages.jpeg.uri)

        withContext(Dispatchers.Main) {
            val deferred = async {
                Error(request, null, Exception(""))
            }
            val requestManager = imageView.requestManager
            val disposable = requestManager.getDisposable(deferred)
            val disposable1 = ReusableDisposable(imageView.requestManager, deferred)

            assertFalse(requestManager.isDisposed(disposable))
            assertTrue(requestManager.isDisposed(disposable1))

            val disposable2 = requestManager.getDisposable(deferred)
            assertNotSame(disposable, disposable2)

            requestManager.setFieldValue("isRestart", true)

            val disposable3 = requestManager.getDisposable(deferred)
            assertSame(disposable2, disposable3)

            requestManager.dispose()
            assertTrue(requestManager.isDisposed(disposable))
            assertTrue(requestManager.isDisposed(disposable1))
        }
    }

    @Test
    fun testAttachedAndDetached() = runTest {
        val fragment = MyTestFragment::class.launchFragmentInContainer().getFragmentSync()
        assertEquals(RESUMED, fragment.lifecycle.currentState)

        val imageView = fragment.imageView
        assertFalse(imageView.isAttachedToWindow)
        assertNull(imageView.drawable)
        assertNull(imageView.requestManager.getFieldValue("currentRequestDelegate"))

        // If there is no attached to the window, the display will inevitably fail
        imageView.loadImage(ResourceImages.jpeg.uri)
        block(1500)
        assertFalse(imageView.isAttachedToWindow)
        assertNull(imageView.drawable)
        assertNotNull(imageView.requestManager.getFieldValue("currentRequestDelegate"))

        // Automatically restart tasks when attached to a window
        withContext(Dispatchers.Main) {
            fragment.attachImageView()
        }
        block(1500)
        assertTrue(imageView.isAttachedToWindow)
        assertNotNull(imageView.drawable)
        assertNotNull(imageView.requestManager.getFieldValue("currentRequestDelegate"))

        // detached from window, request and drawable is null

        // Automatically restart tasks when attached to a window
        withContext(Dispatchers.Main) {
            fragment.detachImageView()
        }
        block(1500)
        assertFalse(imageView.isAttachedToWindow)
        assertNotNull(imageView.drawable)
        assertNotNull(imageView.requestManager.getFieldValue("currentRequestDelegate"))

        // Automatically restart tasks when attached to a window
        withContext(Dispatchers.Main) {
            fragment.attachImageView()
        }
        block(1500)
        assertTrue(imageView.isAttachedToWindow)
        assertNotNull(imageView.drawable)
        assertNotNull(imageView.requestManager.getFieldValue("currentRequestDelegate"))
    }

    class MyTestFragment : Fragment() {

        val imageView: ImageView by lazy {
            ImageView(requireContext())
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View = FrameLayout(requireContext()).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }

        fun attachImageView() {
            (view!! as FrameLayout).addView(imageView.apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            })
        }

        fun detachImageView() {
            (view!! as FrameLayout).removeView(imageView)
        }
    }
}