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
import com.github.panpf.sketch.animated.android.test.internal.TranslucentAnimatedTransformation
import com.github.panpf.sketch.drawable.MovieDrawable
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.images.MyResourceImage
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.tools4a.test.ktx.getFragmentSync
import com.github.panpf.tools4a.test.ktx.launchFragmentInContainer
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDrawableTest {

    @Test
    fun test() {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) return

        val context = InstrumentationRegistry.getInstrumentation().context

        val callbackList = mutableListOf<String>()
        val movie = context.assets.open(MyImages.animGif.asOrThrow<MyResourceImage>().fileName)
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
            Assert.assertFalse(unregisterAnimationCallback(callback))
            registerAnimationCallback(callback)
            Assert.assertTrue(unregisterAnimationCallback(callback))
            registerAnimationCallback(callback)

            setAnimatedTransformation(TranslucentAnimatedTransformation)

            assertThrow(IllegalArgumentException::class) {
                alpha = -1
            }
            assertThrow(IllegalArgumentException::class) {
                alpha = 256
            }
            alpha = 200

            colorFilter = null
        }

        MyTestFragment::class.launchFragmentInContainer().getFragmentSync().apply {
            runBlocking(Dispatchers.Main) {
                imageView.setImageDrawable(movieDrawable)
            }
            Assert.assertFalse(movieDrawable.isRunning)
            Assert.assertEquals(listOf<String>(), callbackList)

            runBlocking(Dispatchers.Main) {
                movieDrawable.start()
            }
            Assert.assertEquals(listOf("onAnimationStart"), callbackList)
            Assert.assertTrue(movieDrawable.isRunning)

            runBlocking(Dispatchers.Main) {
                delay(1000)
                movieDrawable.stop()
            }
            Assert.assertEquals(listOf("onAnimationStart", "onAnimationEnd"), callbackList)
            Assert.assertFalse(movieDrawable.isRunning)
        }
    }

    @Test
    fun test2() {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) return

        val context = InstrumentationRegistry.getInstrumentation().context

        val movie = context.assets.open(MyImages.animGif.asOrThrow<MyResourceImage>().fileName)
            .use { Movie.decodeStream(it) }
        val movieDrawable = MovieDrawable(movie)

        MyTestFragment::class.launchFragmentInContainer().getFragmentSync().apply {
            runBlocking(Dispatchers.Main) {
                imageView.setImageDrawable(movieDrawable)
            }
            runBlocking(Dispatchers.Main) {
                movieDrawable.start()
            }
            runBlocking(Dispatchers.Main) {
                delay(1000)
                movieDrawable.stop()
            }
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val movie = context.assets.open(MyImages.animGif.asOrThrow<MyResourceImage>().fileName)
            .use { Movie.decodeStream(it) }
        val movie2 =
            context.assets.open(MyImages.animGif.asOrThrow<MyResourceImage>().fileName)
                .use { Movie.decodeStream(it) }
        val element1 = MovieDrawable(movie, ARGB_8888)
        val element11 = MovieDrawable(movie, ARGB_8888)
        val element2 = MovieDrawable(movie2, ARGB_8888)
        val element3 = MovieDrawable(movie, RGB_565)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element2, element11)
        Assert.assertNotSame(element2, element3)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val movie = context.assets.open(MyImages.animGif.asOrThrow<MyResourceImage>().fileName)
            .use { Movie.decodeStream(it) }
        Assert.assertEquals(
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
}