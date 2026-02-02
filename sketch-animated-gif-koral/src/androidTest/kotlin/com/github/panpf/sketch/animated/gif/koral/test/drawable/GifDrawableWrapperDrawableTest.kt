package com.github.panpf.sketch.animated.gif.koral.test.drawable

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
import com.github.panpf.sketch.images.ComposeResImageFiles
import kotlinx.coroutines.test.runTest
import okio.buffer
import org.junit.runner.RunWith
import pl.droidsonroids.gif.GifDrawable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class GifDrawableWrapperDrawableTest {

    @Test
    fun test() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().context
        val gifDrawable = GifDrawable(
            ComposeResImageFiles.animGif
                .toDataSource(context).openSource()
                .buffer().use { it.readByteArray() }
        )
        val drawable = GifDrawableWrapperDrawable(gifDrawable)

        if (drawable.isRunning) {
            drawable.stop()
            assertFalse(drawable.isRunning)
        } else {
            drawable.start()
            assertTrue(drawable.isRunning)
        }

        drawable.pause()
        drawable.duration
        drawable.currentPosition
        drawable.seekTo(1)
        drawable.isPlaying
        drawable.bufferPercentage
        drawable.canPause()
        drawable.canSeekBackward()
        drawable.canSeekForward()
        drawable.audioSessionId
        drawable.mutate()
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) return@runTest

        val context = InstrumentationRegistry.getInstrumentation().context
        val gifDrawable = GifDrawable(
            ComposeResImageFiles.animGif
                .toDataSource(context).openSource()
                .buffer().use { it.readByteArray() }
        )
        val gifDrawable2 = GifDrawable(
            ComposeResImageFiles.animGif
                .toDataSource(context).openSource()
                .buffer().use { it.readByteArray() }
        )
        val gifDrawable3 = GifDrawable(
            ComposeResImageFiles.animGif
                .toDataSource(context).openSource()
                .buffer().use { it.readByteArray() }
        )
        val element1 = GifDrawableWrapperDrawable(gifDrawable)
        val element11 = GifDrawableWrapperDrawable(gifDrawable)
        val element2 = GifDrawableWrapperDrawable(gifDrawable2)
        val element3 = GifDrawableWrapperDrawable(gifDrawable3)

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
    fun testToString() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) return@runTest

        val context = InstrumentationRegistry.getInstrumentation().context
        val gifDrawable = GifDrawable(
            ComposeResImageFiles.animGif
                .toDataSource(context).openSource()
                .buffer().use { it.readByteArray() }
        )
        assertEquals(
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