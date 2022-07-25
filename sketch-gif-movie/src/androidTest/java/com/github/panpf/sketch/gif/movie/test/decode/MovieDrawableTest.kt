@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.gif.movie.test.decode

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Movie
import android.graphics.drawable.Drawable
import android.os.Build
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
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.drawable.MovieDrawable
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.transform.PixelOpacity.TRANSLUCENT
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch

        val callbackList = mutableListOf<String>()
        val movie = context.assets.open("sample_anim.gif").use { Movie.decodeStream(it) }
        val movieDrawable =
            MovieDrawable(movie, bitmapCreator = object : MovieDrawable.BitmapCreator {
                override fun createBitmap(width: Int, height: Int, config: Config): Bitmap =
                    sketch.bitmapPool.getOrCreate(width, height, config)

                override fun freeBitmap(bitmap: Bitmap) {
                    sketch.bitmapPool.free(bitmap, "MovieDrawable:recycle")
                }
            }).apply {
                clearAnimationCallbacks()
                val callback = object : Animatable2Compat.AnimationCallback() {
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

                setAnimatedTransformation { TRANSLUCENT }

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

            Assert.assertEquals(
                "BitmapInfo(width=1080, height=1080, byteCount=4.45MB, config=ARGB_8888)",
                movieDrawable.bitmapInfo.toString()
            )
        }
    }

    @Test
    fun test2() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch

        val movie = context.assets.open("sample_anim.gif").use { Movie.decodeStream(it) }
        val movieDrawable =
            MovieDrawable(movie, bitmapCreator = object : MovieDrawable.BitmapCreator {
                override fun createBitmap(width: Int, height: Int, config: Config): Bitmap =
                    sketch.bitmapPool.getOrCreate(width, height, config)

                override fun freeBitmap(bitmap: Bitmap) {
                    sketch.bitmapPool.free(bitmap, "MovieDrawable:recycle")
                }
            })

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