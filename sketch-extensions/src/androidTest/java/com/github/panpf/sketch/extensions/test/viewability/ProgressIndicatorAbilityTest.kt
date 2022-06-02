package com.github.panpf.sketch.extensions.test.viewability

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.drawable.MaskProgressDrawable
import com.github.panpf.sketch.drawable.RingProgressDrawable
import com.github.panpf.sketch.drawable.SectorProgressDrawable
import com.github.panpf.sketch.viewability.ProgressIndicatorAbility
import com.github.panpf.sketch.viewability.isShowProgressIndicator
import com.github.panpf.sketch.viewability.removeProgressIndicator
import com.github.panpf.sketch.viewability.showMaskProgressIndicator
import com.github.panpf.sketch.viewability.showRingProgressIndicator
import com.github.panpf.sketch.viewability.showSectorProgressIndicator
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProgressIndicatorAbilityTest {

    @Test
    fun testExtensions() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val imageView = SketchImageView(context)

        Assert.assertFalse(imageView.isShowProgressIndicator)

        imageView.showSectorProgressIndicator()
        Assert.assertTrue(imageView.isShowProgressIndicator)
        imageView.viewAbilityList.find { it is ProgressIndicatorAbility }!!
            .let { it as ProgressIndicatorAbility }.apply {
                Assert.assertTrue(this.progressDrawable is SectorProgressDrawable)
            }

        imageView.removeProgressIndicator()
        Assert.assertFalse(imageView.isShowProgressIndicator)

        imageView.showMaskProgressIndicator()
        Assert.assertTrue(imageView.isShowProgressIndicator)
        imageView.viewAbilityList.find { it is ProgressIndicatorAbility }!!
            .let { it as ProgressIndicatorAbility }.apply {
                Assert.assertTrue(this.progressDrawable is MaskProgressDrawable)
            }

        imageView.removeProgressIndicator()
        Assert.assertFalse(imageView.isShowProgressIndicator)

        imageView.showRingProgressIndicator()
        Assert.assertTrue(imageView.isShowProgressIndicator)
        imageView.viewAbilityList.find { it is ProgressIndicatorAbility }!!
            .let { it as ProgressIndicatorAbility }.apply {
                Assert.assertTrue(this.progressDrawable is RingProgressDrawable)
            }
    }
}