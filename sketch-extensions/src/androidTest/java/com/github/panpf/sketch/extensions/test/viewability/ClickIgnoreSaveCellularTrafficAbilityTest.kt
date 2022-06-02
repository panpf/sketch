package com.github.panpf.sketch.extensions.test.viewability

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.viewability.isClickIgnoreSaveCellularTrafficEnabled
import com.github.panpf.sketch.viewability.setClickIgnoreSaveCellularTrafficEnabled
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClickIgnoreSaveCellularTrafficAbilityTest {

    @Test
    fun testExtensions() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val imageView = SketchImageView(context)

        Assert.assertFalse(imageView.isClickIgnoreSaveCellularTrafficEnabled)

        imageView.setClickIgnoreSaveCellularTrafficEnabled()
        Assert.assertTrue(imageView.isClickIgnoreSaveCellularTrafficEnabled)

        imageView.setClickIgnoreSaveCellularTrafficEnabled(false)
        Assert.assertFalse(imageView.isClickIgnoreSaveCellularTrafficEnabled)

        imageView.setClickIgnoreSaveCellularTrafficEnabled(true)
        Assert.assertTrue(imageView.isClickIgnoreSaveCellularTrafficEnabled)
    }
}