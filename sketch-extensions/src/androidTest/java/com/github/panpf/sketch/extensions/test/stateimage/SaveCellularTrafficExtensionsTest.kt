package com.github.panpf.sketch.extensions.test.stateimage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.setDepthFromSaveCellularTraffic
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.ColorStateImage
import com.github.panpf.sketch.stateimage.IntColor
import com.github.panpf.sketch.stateimage.SaveCellularTrafficMatcher
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.saveCellularTrafficError
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SaveCellularTrafficExtensionsTest {

    @Test
    fun testSaveCellularTrafficError() {
        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))).apply {
            Assert.assertNull(matcherList.find { it is SaveCellularTrafficMatcher })
        }

        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            saveCellularTrafficError()
        }.apply {
            Assert.assertNotNull(matcherList.find { it is SaveCellularTrafficMatcher })
        }

        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            saveCellularTrafficError(ColorStateImage(IntColor(Color.BLUE)))
        }.apply {
            Assert.assertNotNull(matcherList.find { it is SaveCellularTrafficMatcher })
        }

        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            saveCellularTrafficError(ColorDrawable(Color.GREEN))
        }.apply {
            Assert.assertNotNull(matcherList.find { it is SaveCellularTrafficMatcher })
        }

        ErrorStateImage(ColorStateImage(IntColor(Color.BLACK))) {
            saveCellularTrafficError(android.R.drawable.btn_dialog)
        }.apply {
            Assert.assertNotNull(matcherList.find { it is SaveCellularTrafficMatcher })
        }
    }

    @Test
    fun testSaveCellularTrafficMatcher() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch

        SaveCellularTrafficMatcher(null).apply {
            val request = DisplayRequest(context, "http://sample.com/sample.jpeg") {
                setDepthFromSaveCellularTraffic()
            }
            Assert.assertTrue(match(request, DepthException(LOCAL)))
            Assert.assertFalse(match(request, DepthException(NETWORK)))
            Assert.assertFalse(match(request, null))

            Assert.assertNull(getDrawable(sketch, request, null))
        }

        SaveCellularTrafficMatcher(ColorStateImage(IntColor(Color.BLUE))).apply {
            val request = DisplayRequest(context, "http://sample.com/sample.jpeg") {
                setDepthFromSaveCellularTraffic()
            }

            Assert.assertTrue(getDrawable(sketch, request, null) is ColorDrawable)
        }
    }
}