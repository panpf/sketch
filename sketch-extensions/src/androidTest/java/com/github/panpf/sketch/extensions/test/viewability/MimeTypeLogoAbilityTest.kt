package com.github.panpf.sketch.extensions.test.viewability

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.viewability.isShowMimeTypeLogo
import com.github.panpf.sketch.viewability.removeMimeTypeLogo
import com.github.panpf.sketch.viewability.showMimeTypeLogoWithDrawable
import com.github.panpf.sketch.viewability.showMimeTypeLogoWithRes
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MimeTypeLogoAbilityTest {

    @Test
    fun testExtensions() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val imageView = SketchImageView(context)

        Assert.assertFalse(imageView.isShowMimeTypeLogo)

        imageView.showMimeTypeLogoWithDrawable(mapOf("image/jpeg" to ColorDrawable(Color.BLUE)))
        Assert.assertTrue(imageView.isShowMimeTypeLogo)

        imageView.removeMimeTypeLogo()
        Assert.assertFalse(imageView.isShowMimeTypeLogo)

        imageView.showMimeTypeLogoWithRes(mapOf("image/jpeg" to android.R.drawable.btn_dialog))
        Assert.assertTrue(imageView.isShowMimeTypeLogo)
    }
}