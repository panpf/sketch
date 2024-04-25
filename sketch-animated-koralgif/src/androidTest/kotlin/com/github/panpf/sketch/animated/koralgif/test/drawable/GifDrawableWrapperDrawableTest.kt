package com.github.panpf.sketch.animated.koralgif.test.drawable

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
import com.github.panpf.sketch.drawable.GifDrawableWrapperDrawable
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.images.MyResourceImage
import com.github.panpf.sketch.test.utils.asOrThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import pl.droidsonroids.gif.GifDrawable

@RunWith(AndroidJUnit4::class)
class GifDrawableWrapperDrawableTest {

    @Test
    fun testEqualsAndHashCode() {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val movie = GifDrawable(context.assets, MyImages.animGif.asOrThrow<MyResourceImage>().fileName)
        val movie2 = GifDrawable(context.assets, MyImages.animGif.asOrThrow<MyResourceImage>().fileName)
        val movie3 = GifDrawable(context.assets, MyImages.animGif.asOrThrow<MyResourceImage>().fileName)
        val element1 = GifDrawableWrapperDrawable(movie)
        val element11 = GifDrawableWrapperDrawable(movie)
        val element2 = GifDrawableWrapperDrawable(movie2)
        val element3 = GifDrawableWrapperDrawable(movie3)

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
        val gifDrawable = GifDrawable(context.assets, MyImages.animGif.asOrThrow<MyResourceImage>().fileName)
        Assert.assertEquals(
            "GifDrawableWrapperDrawable(480x480)",
            GifDrawableWrapperDrawable(gifDrawable).toString()
        )
    }

    class MyTestFragment : Fragment() {

        private val imageView: ImageView by lazy {
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