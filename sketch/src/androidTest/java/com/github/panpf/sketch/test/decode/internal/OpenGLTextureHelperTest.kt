package com.github.panpf.sketch.test.decode.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.OpenGLTextureHelper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OpenGLTextureHelperTest {

    @Test
    fun test() {
        Assert.assertEquals(16384, OpenGLTextureHelper.maxSize)
    }
}