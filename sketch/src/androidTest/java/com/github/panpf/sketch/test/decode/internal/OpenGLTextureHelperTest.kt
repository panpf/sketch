package com.github.panpf.sketch.test.decode.internal

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.OpenGLTextureHelper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OpenGLTextureHelperTest {

    @Test
    fun test() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            Assert.assertNull(OpenGLTextureHelper.maxSize)
        } else {
            Assert.assertEquals(16384, OpenGLTextureHelper.maxSize)
        }
    }
}