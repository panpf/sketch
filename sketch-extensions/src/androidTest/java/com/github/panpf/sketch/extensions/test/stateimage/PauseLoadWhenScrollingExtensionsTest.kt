package com.github.panpf.sketch.extensions.test.stateimage

import android.R.drawable
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest.Builder
import com.github.panpf.sketch.request.ignorePauseLoadWhenScrolling
import com.github.panpf.sketch.request.isCausedByPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isDepthFromPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isIgnoredPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isPauseLoadWhenScrolling
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.request.setDepthFromPauseLoadWhenScrolling
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.ColorStateImage
import com.github.panpf.sketch.stateimage.IntColor
import com.github.panpf.sketch.stateimage.PauseLoadWhenScrollingMatcher
import com.github.panpf.sketch.stateimage.newErrorStateImage
import com.github.panpf.sketch.stateimage.pauseLoadWhenScrollingError
import com.github.panpf.sketch.util.OtherException
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PauseLoadWhenScrollingExtensionsTest {

    @Test
    fun testPauseLoadWhenScrollingError() {
        newErrorStateImage(ColorStateImage(IntColor(Color.BLACK))).apply {
            Assert.assertNull(matcherList.find { it is PauseLoadWhenScrollingMatcher })
        }

        newErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            pauseLoadWhenScrollingError()
        }.apply {
            Assert.assertNotNull(matcherList.find { it is PauseLoadWhenScrollingMatcher })
        }

        newErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            pauseLoadWhenScrollingError(ColorStateImage(IntColor(Color.BLUE)))
        }.apply {
            Assert.assertNotNull(matcherList.find { it is PauseLoadWhenScrollingMatcher })
        }

        newErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            pauseLoadWhenScrollingError(ColorDrawable(Color.GREEN))
        }.apply {
            Assert.assertNotNull(matcherList.find { it is PauseLoadWhenScrollingMatcher })
        }

        newErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            pauseLoadWhenScrollingError(drawable.btn_dialog)
        }.apply {
            Assert.assertNotNull(matcherList.find { it is PauseLoadWhenScrollingMatcher })
        }
    }

    @Test
    fun testPauseLoadWhenScrollingMatcher() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch

        PauseLoadWhenScrollingMatcher(null).apply {
            val request = DisplayRequest(context, "http://sample.com/sample.jpeg") {
                setDepthFromPauseLoadWhenScrolling()
            }
            Assert.assertTrue(match(request, DepthException(request, MEMORY)))
            Assert.assertFalse(match(request, DepthException(request, LOCAL)))
            Assert.assertFalse(match(request, null))

            Assert.assertNull(getDrawable(sketch, request, null))
        }

        PauseLoadWhenScrollingMatcher(ColorStateImage(IntColor(Color.BLUE))).apply {
            val request = DisplayRequest(context, "http://sample.com/sample.jpeg") {
                setDepthFromPauseLoadWhenScrolling()
            }

            Assert.assertTrue(getDrawable(sketch, request, null) is ColorDrawable)
        }
    }
}