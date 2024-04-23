package com.github.panpf.sketch.view.core.test.resize.internal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.internal.RealViewSizeResolver
import com.github.panpf.sketch.resize.internal.ViewSizeResolver
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4a.test.ktx.getFragmentSync
import com.github.panpf.tools4a.test.ktx.launchFragmentInContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewSizeResolverTest {

    @Test
    fun testCreateFunction() {
        val context = getTestContext()
        val imageView = ImageView(context)
        ViewSizeResolver(imageView).apply {
            Assert.assertTrue(this is RealViewSizeResolver)
            Assert.assertTrue(subtractPadding)
            Assert.assertTrue(view === imageView)
        }

        ViewSizeResolver(imageView, subtractPadding = false).apply {
            Assert.assertFalse(subtractPadding)
        }
    }

    @Test
    fun test() {
        val context = getTestContext()
        val displaySize = context.resources.displayMetrics.let {
            Size(it.widthPixels, it.heightPixels)
        }

        MatchParentTestFragment::class.launchFragmentInContainer().getFragmentSync()
            .let { fragment ->
                Assert.assertEquals(RESUMED, fragment.lifecycle.currentState)

                runBlocking(Dispatchers.Main) {
                    ViewSizeResolver(fragment.imageView).size()
                }.apply {
                    Assert.assertTrue(
                        "displaySize=$displaySize, viewSize=$this",
                        this.height > displaySize.height / 2 && this.height <= displaySize.height
                    )
                }
            }

        WrapContentTestFragment::class.launchFragmentInContainer().getFragmentSync()
            .let { fragment ->
                Assert.assertEquals(RESUMED, fragment.lifecycle.currentState)

                runBlocking(Dispatchers.Main) {
                    ViewSizeResolver(fragment.imageView).size()
                }.apply {
                    Assert.assertEquals(displaySize.width, this.width)
                    Assert.assertTrue(
                        "displaySize=$displaySize, viewSize=$this",
                        this.height > displaySize.height / 2 && this.height <= displaySize.height
                    )
                }
            }

        FixedSizeTestFragment::class.launchFragmentInContainer().getFragmentSync()
            .let { fragment ->
                Assert.assertEquals(RESUMED, fragment.lifecycle.currentState)

                runBlocking(Dispatchers.Main) {
                    ViewSizeResolver(fragment.imageView).size()
                }.apply {
                    Assert.assertEquals(Size(500 - 40, 600 - 60), this)
                }
                runBlocking(Dispatchers.Main) {
                    ViewSizeResolver(fragment.imageView, subtractPadding = false).size()
                }.apply {
                    Assert.assertEquals(Size(500, 600), this)
                }
            }

        ErrorPaddingTestFragment::class.launchFragmentInContainer().getFragmentSync()
            .let { fragment ->
                Assert.assertEquals(RESUMED, fragment.lifecycle.currentState)

                runBlocking(Dispatchers.Main) {
                    val de = async {
                        ViewSizeResolver(fragment.imageView).size()
                    }
                    delay(1000)
                    val completed = de.isCompleted
                    de.cancel()
                    completed
                }.apply {
                    Assert.assertFalse(this)
                }
            }

        runBlocking(Dispatchers.Main) {
            ViewSizeResolver(ImageView(context)).size()
        }.apply {
            Assert.assertTrue(
                "displaySize=$displaySize, viewSize=$this",
                this.height > displaySize.height / 2 && this.height <= displaySize.height
            )
        }

        ContainerTestFragment::class.launchFragmentInContainer().getFragmentSync()
            .let { fragment ->
                Assert.assertEquals(RESUMED, fragment.lifecycle.currentState)

                runBlocking(Dispatchers.Main) {
                    val imageView = ImageView(context)
                    val deferred = async {
                        ViewSizeResolver(imageView).size()
                    }
                    fragment.container.addView(
                        imageView,
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    )
                    deferred.await()
                }.apply {
                    Assert.assertTrue(
                        "displaySize=$displaySize, viewSize=$this",
                        this.height > displaySize.height / 2 && this.height <= displaySize.height
                    )
                }
            }
    }

    class MatchParentTestFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View = ImageView(inflater.context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }

        val imageView: ImageView
            get() = view!! as ImageView
    }

    class WrapContentTestFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View = ImageView(inflater.context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }

        val imageView: ImageView
            get() = view!! as ImageView
    }

    class FixedSizeTestFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View = ImageView(inflater.context).apply {
            layoutParams = LayoutParams(500, 600)
            updatePadding(left = 10, top = 20, right = 30, bottom = 40)
        }

        val imageView: ImageView
            get() = view!! as ImageView
    }

    class ErrorPaddingTestFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View = ImageView(inflater.context).apply {
            layoutParams = LayoutParams(500, 600)
            updatePadding(left = 400, top = 200, right = 200, bottom = 500)
        }

        val imageView: ImageView
            get() = view!! as ImageView
    }

    class ContainerTestFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View = FrameLayout(inflater.context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }

        val container: FrameLayout
            get() = view!! as FrameLayout
    }
}