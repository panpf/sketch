package com.github.panpf.sketch.test.util

import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Logger.Level.DEBUG
import com.github.panpf.sketch.util.Logger.Level.ERROR
import com.github.panpf.sketch.util.Logger.Level.INFO
import com.github.panpf.sketch.util.Logger.Level.NONE
import com.github.panpf.sketch.util.Logger.Level.VERBOSE
import com.github.panpf.sketch.util.Logger.Level.WARNING
import com.github.panpf.sketch.util.Logger.Proxy
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.LinkedList

@RunWith(AndroidJUnit4::class)
class LoggerTest {

    @Test
    fun testConstructor() {
        Logger().apply {
            Assert.assertEquals(INFO, level)
        }
        Logger(DEBUG).apply {
            Assert.assertEquals(DEBUG, level)
        }

        val testProxy = TestProxy()
        Logger(proxy = testProxy).apply {
            Assert.assertEquals(0, testProxy.logList.size)

            w("moduleTest1") { "messageTest1" }
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=WARNING, tag=Sketch, msg=moduleTest1. messageTest1, tr=null)",
                testProxy.logList[0].toString()
            )

            e("moduleTest2") { "messageTest2" }
            Assert.assertEquals(2, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=ERROR, tag=Sketch, msg=moduleTest2. messageTest2, tr=null)",
                testProxy.logList[1].toString()
            )
        }
    }

    @Test
    fun testLevel() {
        Logger().apply {
            Assert.assertEquals(INFO, level)
            level = ERROR
            Assert.assertEquals(ERROR, level)
        }
    }

    @Test
    fun testIsLoggable() {
        Logger().apply {
            level = VERBOSE
            Assert.assertTrue(isLoggable(VERBOSE))
            Assert.assertTrue(isLoggable(DEBUG))
            Assert.assertTrue(isLoggable(INFO))
            Assert.assertTrue(isLoggable(WARNING))
            Assert.assertTrue(isLoggable(ERROR))
            Assert.assertTrue(isLoggable(NONE))

            level = DEBUG
            Assert.assertFalse(isLoggable(VERBOSE))
            Assert.assertTrue(isLoggable(DEBUG))
            Assert.assertTrue(isLoggable(INFO))
            Assert.assertTrue(isLoggable(WARNING))
            Assert.assertTrue(isLoggable(ERROR))
            Assert.assertTrue(isLoggable(NONE))

            level = INFO
            Assert.assertFalse(isLoggable(VERBOSE))
            Assert.assertFalse(isLoggable(DEBUG))
            Assert.assertTrue(isLoggable(INFO))
            Assert.assertTrue(isLoggable(WARNING))
            Assert.assertTrue(isLoggable(ERROR))
            Assert.assertTrue(isLoggable(NONE))

            level = WARNING
            Assert.assertFalse(isLoggable(VERBOSE))
            Assert.assertFalse(isLoggable(DEBUG))
            Assert.assertFalse(isLoggable(INFO))
            Assert.assertTrue(isLoggable(WARNING))
            Assert.assertTrue(isLoggable(ERROR))
            Assert.assertTrue(isLoggable(NONE))

            level = ERROR
            Assert.assertFalse(isLoggable(VERBOSE))
            Assert.assertFalse(isLoggable(DEBUG))
            Assert.assertFalse(isLoggable(INFO))
            Assert.assertFalse(isLoggable(WARNING))
            Assert.assertTrue(isLoggable(ERROR))
            Assert.assertTrue(isLoggable(NONE))

            level = NONE
            Assert.assertFalse(isLoggable(VERBOSE))
            Assert.assertFalse(isLoggable(DEBUG))
            Assert.assertFalse(isLoggable(INFO))
            Assert.assertFalse(isLoggable(WARNING))
            Assert.assertFalse(isLoggable(ERROR))
            Assert.assertTrue(isLoggable(NONE))
        }
    }

    @Test
    fun testV() {
        val testProxy = TestProxy()
        Logger(proxy = testProxy).apply {
            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)
            level = NONE
            v("module") { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testProxy.logList.size)

            level = VERBOSE
            v("module") { "message" }
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=VERBOSE, tag=Sketch, msg=module. message, tr=null)",
                testProxy.logList[0].toString()
            )

            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)
            level = NONE
            v("module", Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testProxy.logList.size)

            level = VERBOSE
            v("module", Exception("exception")) { "message" }
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=VERBOSE, tag=Sketch, msg=module. message, tr=java.lang.Exception: exception)",
                testProxy.logList[0].toString()
            )
        }
    }

    @Test
    fun testD() {
        val testProxy = TestProxy()
        Logger(proxy = testProxy).apply {
            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)
            level = NONE
            d("module") { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testProxy.logList.size)

            level = DEBUG
            d("module") { "message" }
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=DEBUG, tag=Sketch, msg=module. message, tr=null)",
                testProxy.logList[0].toString()
            )

            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)
            level = NONE
            d("module", Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testProxy.logList.size)

            level = DEBUG
            d("module", Exception("exception")) { "message" }
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=DEBUG, tag=Sketch, msg=module. message, tr=java.lang.Exception: exception)",
                testProxy.logList[0].toString()
            )
        }
    }

    @Test
    fun testI() {
        val testProxy = TestProxy()
        Logger(proxy = testProxy).apply {
            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)
            level = NONE
            i("module") { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testProxy.logList.size)

            level = INFO
            i("module") { "message" }
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=INFO, tag=Sketch, msg=module. message, tr=null)",
                testProxy.logList[0].toString()
            )

            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)
            level = NONE
            i("module", Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testProxy.logList.size)

            level = INFO
            i("module", Exception("exception")) { "message" }
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=INFO, tag=Sketch, msg=module. message, tr=java.lang.Exception: exception)",
                testProxy.logList[0].toString()
            )
        }
    }

    @Test
    fun testW() {
        val testProxy = TestProxy()
        Logger(proxy = testProxy).apply {
            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)
            level = NONE
            w("module") { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testProxy.logList.size)

            level = WARNING
            w("module") { "message" }
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=WARNING, tag=Sketch, msg=module. message, tr=null)",
                testProxy.logList[0].toString()
            )

            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)
            level = NONE
            w("module", "message")
            Assert.assertEquals(0, testProxy.logList.size)

            level = WARNING
            w("module", "message")
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=WARNING, tag=Sketch, msg=module. message, tr=null)",
                testProxy.logList[0].toString()
            )

            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)
            level = NONE
            w("module", Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testProxy.logList.size)

            level = WARNING
            w("module", Exception("exception")) { "message" }
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=WARNING, tag=Sketch, msg=module. message, tr=java.lang.Exception: exception)",
                testProxy.logList[0].toString()
            )

            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)
            level = NONE
            w("module", Exception("exception"), "message")
            Assert.assertEquals(0, testProxy.logList.size)

            level = WARNING
            w("module", Exception("exception"), "message")
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=WARNING, tag=Sketch, msg=module. message, tr=java.lang.Exception: exception)",
                testProxy.logList[0].toString()
            )
        }
    }

    @Test
    fun testE() {
        val testProxy = TestProxy()
        Logger(proxy = testProxy).apply {
            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)
            level = NONE
            e("module") { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testProxy.logList.size)

            level = ERROR
            e("module") { "message" }
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=ERROR, tag=Sketch, msg=module. message, tr=null)",
                testProxy.logList[0].toString()
            )

            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)
            level = NONE
            e("module", "message")
            Assert.assertEquals(0, testProxy.logList.size)

            level = ERROR
            e("module", "message")
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=ERROR, tag=Sketch, msg=module. message, tr=null)",
                testProxy.logList[0].toString()
            )

            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)
            level = NONE
            e("module", Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testProxy.logList.size)

            level = ERROR
            e("module", Exception("exception")) { "message" }
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=ERROR, tag=Sketch, msg=module. message, tr=java.lang.Exception: exception)",
                testProxy.logList[0].toString()
            )

            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)
            level = NONE
            e("module", Exception("exception"), "message")
            Assert.assertEquals(0, testProxy.logList.size)

            level = ERROR
            e("module", Exception("exception"), "message")
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=ERROR, tag=Sketch, msg=module. message, tr=java.lang.Exception: exception)",
                testProxy.logList[0].toString()
            )
        }
    }

    @Test
    fun testFlush() {
        val testProxy = TestProxy()
        Logger(proxy = testProxy).apply {
            testProxy.logList.clear()
            Assert.assertEquals(0, testProxy.logList.size)

            flush()
            Assert.assertEquals(1, testProxy.logList.size)
            Assert.assertEquals(
                "LogEntry(level=NONE, tag=Sketch, msg=flush, tr=null)",
                testProxy.logList[0].toString()
            )
        }
    }

    private class TestProxy : Proxy {

        val logList = LinkedList<LogEntry>()

        override fun v(tag: String, msg: String, tr: Throwable?) {
            logList.add(LogEntry(VERBOSE, tag, msg, tr))
        }

        override fun d(tag: String, msg: String, tr: Throwable?) {
            logList.add(LogEntry(DEBUG, tag, msg, tr))
        }

        override fun i(tag: String, msg: String, tr: Throwable?) {
            logList.add(LogEntry(INFO, tag, msg, tr))
        }

        override fun w(tag: String, msg: String, tr: Throwable?) {
            logList.add(LogEntry(WARNING, tag, msg, tr))
        }

        override fun e(tag: String, msg: String, tr: Throwable?) {
            logList.add(LogEntry(ERROR, tag, msg, tr))
        }

        override fun flush() {
            logList.add(LogEntry(NONE, Logger.TAG, "flush", null))
        }

        private data class LogEntry(
            val level: Logger.Level,
            val tag: String,
            val msg: String,
            val tr: Throwable?
        )
    }
}