package com.github.panpf.sketch.test

import com.github.panpf.sketch.SLog.Companion.setLevel
import com.github.panpf.sketch.SLog.Companion.isLoggable
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.SLog
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SLogTest {
    @Test
    fun testLevelLoggable() {
        setLevel(SLog.INFO)
        Assert.assertTrue("LEVEL_INFO invalid", isLoggable(SLog.INFO))
        Assert.assertTrue("LEVEL_WARNING invalid", isLoggable(SLog.WARNING))
        Assert.assertTrue("LEVEL_ERROR invalid", isLoggable(SLog.ERROR))
        Assert.assertTrue("LEVEL_NONE invalid", isLoggable(SLog.NONE))
        Assert.assertFalse("LEVEL_DEBUG valid", isLoggable(SLog.DEBUG))
        Assert.assertFalse("LEVEL_VERBOSE valid", isLoggable(SLog.VERBOSE))
    }
}