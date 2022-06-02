package com.github.panpf.sketch.extensions.test.viewability

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.viewability.isShowDataFromLogo
import com.github.panpf.sketch.viewability.removeDataFromLogo
import com.github.panpf.sketch.viewability.showDataFromLogo
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataFromLogoAbilityTest {

    @Test
    fun testExtensions() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val imageView = SketchImageView(context)

        Assert.assertFalse(imageView.isShowDataFromLogo)

        imageView.showDataFromLogo()
        Assert.assertTrue(imageView.isShowDataFromLogo)

        imageView.removeDataFromLogo()
        Assert.assertFalse(imageView.isShowDataFromLogo)
    }
}