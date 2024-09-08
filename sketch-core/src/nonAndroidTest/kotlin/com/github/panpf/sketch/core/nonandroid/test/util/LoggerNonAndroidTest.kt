package com.github.panpf.sketch.core.nonandroid.test.util

import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.PrintLogPipeline
import com.github.panpf.sketch.util.defaultLogPipeline
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoggerNonAndroidTest {

    @Test
    fun testDefaultLogPipeline() {
        assertTrue(defaultLogPipeline() is PrintLogPipeline)
    }

    @Test
    fun testPrintLogPipeline() {
        PrintLogPipeline.apply {
            assertEquals(
                expected = "PrintLogPipeline",
                actual = toString()
            )

            log(Logger.Level.Verbose, "tag", "message", tr = null)
            log(Logger.Level.Debug, "tag", "message", tr = null)
            log(Logger.Level.Info, "tag", "message", tr = null)
            log(Logger.Level.Warn, "tag", "message", tr = null)
            log(Logger.Level.Error, "tag", "message", tr = Exception("from test"))
            log(Logger.Level.Assert, "tag", "message", tr = null)

            flush()
        }
    }
}