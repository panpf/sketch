@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.animated.android.test.drawable

import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Movie
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
import com.github.panpf.sketch.drawable.MovieDrawable
import com.github.panpf.sketch.images.ResourceImageFile
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.transform.PixelOpacity
import com.github.panpf.sketch.util.Rect
import com.github.panpf.tools4a.test.ktx.getFragmentSync
import com.github.panpf.tools4a.test.ktx.launchFragmentInContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class MovieDrawableTest {

    @Test
    fun test() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) return@runTest

        val context = InstrumentationRegistry.getInstrumentation().context

        val callbackList = mutableListOf<String>()
        val movie =
            context.assets.open(ResourceImages.animGif.asOrThrow<ResourceImageFile>().resourceName)
                .use { Movie.decodeStream(it) }
        val movieDrawable = MovieDrawable(movie).apply {
            clearAnimationCallbacks()
            val callback = object : AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable?) {
                    super.onAnimationStart(drawable)
                    callbackList.add("onAnimationStart")
                }

                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    callbackList.add("onAnimationEnd")
                }
            }
            assertFalse(unregisterAnimationCallback(callback))
            registerAnimationCallback(callback)
            assertTrue(unregisterAnimationCallback(callback))
            registerAnimationCallback(callback)

            setAnimatedTransformation(TranslucentAnimatedTransformation)

            assertFailsWith(IllegalArgumentException::class) {
                alpha = -1
            }
            assertFailsWith(IllegalArgumentException::class) {
                alpha = 256
            }
            alpha = 200

            colorFilter = null
        }

        MyTestFragment::class.launchFragmentInContainer().getFragmentSync().apply {
            withContext(Dispatchers.Main) {
                imageView.setImageDrawable(movieDrawable)
            }
            assertFalse(movieDrawable.isRunning)
            assertEquals(listOf(), callbackList)

            withContext(Dispatchers.Main) {
                movieDrawable.start()
            }
            assertEquals(listOf("onAnimationStart"), callbackList)
            assertTrue(movieDrawable.isRunning)

            block(1000)
            withContext(Dispatchers.Main) {
                movieDrawable.stop()
            }
            assertEquals(listOf("onAnimationStart", "onAnimationEnd"), callbackList)
            assertFalse(movieDrawable.isRunning)
        }
    }

    @Test
    fun test2() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) return@runTest

        val context = InstrumentationRegistry.getInstrumentation().context

        val movie =
            context.assets.open(ResourceImages.animGif.asOrThrow<ResourceImageFile>().resourceName)
                .use { Movie.decodeStream(it) }
        val movieDrawable = MovieDrawable(movie)

        MyTestFragment::class.launchFragmentInContainer().getFragmentSync().apply {
            withContext(Dispatchers.Main) {
                imageView.setImageDrawable(movieDrawable)
            }
            withContext(Dispatchers.Main) {
                movieDrawable.start()
            }
            block(1000)
            withContext(Dispatchers.Main) {
                movieDrawable.stop()
            }
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val movie =
            context.assets.open(ResourceImages.animGif.asOrThrow<ResourceImageFile>().resourceName)
                .use { Movie.decodeStream(it) }
        val movie2 =
            context.assets.open(ResourceImages.animGif.asOrThrow<ResourceImageFile>().resourceName)
                .use { Movie.decodeStream(it) }
        val element1 = MovieDrawable(movie, ARGB_8888)
        val element11 = MovieDrawable(movie, ARGB_8888)
        val element2 = MovieDrawable(movie2, ARGB_8888)
        val element3 = MovieDrawable(movie, RGB_565)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val movie =
            context.assets.open(ResourceImages.animGif.asOrThrow<ResourceImageFile>().resourceName)
                .use { Movie.decodeStream(it) }
        assertEquals(
            "MovieDrawable(size=480x480, config=ARGB_8888)",
            MovieDrawable(movie, ARGB_8888).toString()
        )
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
            layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                    addView(imageView)
                }
        }
    }

    private data object TranslucentAnimatedTransformation : AnimatedTransformation {
        override val key: String = "TranslucentAnimatedTransformation"

        override fun transform(canvas: Any, bounds: Rect): PixelOpacity {
            return PixelOpacity.TRANSLUCENT
        }
    }
}