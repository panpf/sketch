package com.github.panpf.sketch.test.decode.resize

import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.decode.resize.NewSize
import com.github.panpf.sketch.decode.resize.RealNewSize
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewSizeTest {

    @Test
    fun test() {
        NewSize(100, 50).apply {
            Assert.assertEquals(100, width)
            Assert.assertEquals(50, height)
            Assert.assertTrue(this is RealNewSize)
            Assert.assertEquals("RealNewSize(100x50)", toString())
        }

        NewSize(Size(50, 100)).apply {
            Assert.assertEquals(50, width)
            Assert.assertEquals(100, height)
            Assert.assertTrue(this is RealNewSize)
            Assert.assertEquals("RealNewSize(50x100)", toString())
        }
    }
}