package com.github.panpf.sketch.extensions.test

import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.R
import com.github.panpf.sketch.displayAppIconImage
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.ViewTargetRequestDelegate
import com.github.panpf.sketch.request.internal.ViewTargetRequestManager
import com.github.panpf.tools4j.reflect.ktx.getFieldValue
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OtherImageViewExtensionsTest {

    @Test
    fun testDisplayAppIconImage() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val imageView = ImageView(context)

        imageView.displayAppIconImage(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_CODE)
        Thread.sleep(100)
        val manager = imageView.getTag(R.id.sketch_request_manager) as ViewTargetRequestManager
        val request = manager.getFieldValue<ViewTargetRequestDelegate>("currentRequest")!!
            .getFieldValue<DisplayRequest>("initialRequest")!!
        Assert.assertEquals(newAppIconUri(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_CODE), request.uriString)
    }
}