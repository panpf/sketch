package com.github.panpf.sketch.test.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.util.ifOrNull
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KotlinUtilsTest {

    @Test
    fun testIfOrNull() {
        Assert.assertEquals("yes", ifOrNull(true) { "yes" })
        Assert.assertEquals(null, ifOrNull(false) { "yes" })
    }
}