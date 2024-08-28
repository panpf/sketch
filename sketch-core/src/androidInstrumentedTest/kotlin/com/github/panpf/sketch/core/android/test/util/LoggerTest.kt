/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.core.android.test.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.util.Logger
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.LinkedList

@RunWith(AndroidJUnit4::class)
class LoggerTest {

    @Test
    fun testConstructor() {
        Logger().apply {
            Assert.assertEquals(Logger.Level.Info, level)
        }
        Logger(level = Logger.Level.Debug).apply {
            Assert.assertEquals(Logger.Level.Debug, level)
        }

        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            Assert.assertEquals(0, testPipeline.logList.size)

            w { "messageTest1" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Warn, tag=Sketch, msg=messageTest1, tr=null)",
                testPipeline.logList[0].toString()
            )

            e { "messageTest2" }
            Assert.assertEquals(2, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Error, tag=Sketch, msg=messageTest2, tr=null)",
                testPipeline.logList[1].toString()
            )
        }
    }

    @Test
    fun testLevel() {
        Logger().apply {
            Assert.assertEquals(Logger.Level.Info, level)
            level = Logger.Level.Error
            Assert.assertEquals(Logger.Level.Error, level)
        }
    }

    @Test
    fun testIsLoggable() {
        Logger().apply {
            level = Logger.Level.Verbose
            Assert.assertTrue(isLoggable(Logger.Level.Verbose))
            Assert.assertTrue(isLoggable(Logger.Level.Debug))
            Assert.assertTrue(isLoggable(Logger.Level.Info))
            Assert.assertTrue(isLoggable(Logger.Level.Warn))
            Assert.assertTrue(isLoggable(Logger.Level.Error))
            Assert.assertTrue(isLoggable(Logger.Level.Assert))

            level = Logger.Level.Debug
            Assert.assertFalse(isLoggable(Logger.Level.Verbose))
            Assert.assertTrue(isLoggable(Logger.Level.Debug))
            Assert.assertTrue(isLoggable(Logger.Level.Info))
            Assert.assertTrue(isLoggable(Logger.Level.Warn))
            Assert.assertTrue(isLoggable(Logger.Level.Error))
            Assert.assertTrue(isLoggable(Logger.Level.Assert))

            level = Logger.Level.Info
            Assert.assertFalse(isLoggable(Logger.Level.Verbose))
            Assert.assertFalse(isLoggable(Logger.Level.Debug))
            Assert.assertTrue(isLoggable(Logger.Level.Info))
            Assert.assertTrue(isLoggable(Logger.Level.Warn))
            Assert.assertTrue(isLoggable(Logger.Level.Error))
            Assert.assertTrue(isLoggable(Logger.Level.Assert))

            level = Logger.Level.Warn
            Assert.assertFalse(isLoggable(Logger.Level.Verbose))
            Assert.assertFalse(isLoggable(Logger.Level.Debug))
            Assert.assertFalse(isLoggable(Logger.Level.Info))
            Assert.assertTrue(isLoggable(Logger.Level.Warn))
            Assert.assertTrue(isLoggable(Logger.Level.Error))
            Assert.assertTrue(isLoggable(Logger.Level.Assert))

            level = Logger.Level.Error
            Assert.assertFalse(isLoggable(Logger.Level.Verbose))
            Assert.assertFalse(isLoggable(Logger.Level.Debug))
            Assert.assertFalse(isLoggable(Logger.Level.Info))
            Assert.assertFalse(isLoggable(Logger.Level.Warn))
            Assert.assertTrue(isLoggable(Logger.Level.Error))
            Assert.assertTrue(isLoggable(Logger.Level.Assert))

            level = Logger.Level.Assert
            Assert.assertFalse(isLoggable(Logger.Level.Verbose))
            Assert.assertFalse(isLoggable(Logger.Level.Debug))
            Assert.assertFalse(isLoggable(Logger.Level.Info))
            Assert.assertFalse(isLoggable(Logger.Level.Warn))
            Assert.assertFalse(isLoggable(Logger.Level.Error))
            Assert.assertTrue(isLoggable(Logger.Level.Assert))
        }
    }

    @Test
    fun testV() {
        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Level.Assert
            v { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Level.Verbose
            v { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Verbose, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Level.Assert
            v(Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Level.Verbose
            v(Exception("exception")) { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Verbose, tag=Sketch, msg=message, tr=java.lang.Exception: exception)",
                testPipeline.logList[0].toString()
            )
        }
    }

    @Test
    fun testD() {
        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Level.Assert
            d { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Level.Debug
            d { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Debug, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Level.Assert
            d(Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Level.Debug
            d(Exception("exception")) { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Debug, tag=Sketch, msg=message, tr=java.lang.Exception: exception)",
                testPipeline.logList[0].toString()
            )
        }
    }

    @Test
    fun testI() {
        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Level.Assert
            i { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Level.Info
            i { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Info, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Level.Assert
            i(Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Level.Info
            i(Exception("exception")) { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Info, tag=Sketch, msg=message, tr=java.lang.Exception: exception)",
                testPipeline.logList[0].toString()
            )
        }
    }

    @Test
    fun testW() {
        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Level.Assert
            w { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Level.Warn
            w { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Warn, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Level.Assert
            w("message")
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Level.Warn
            w("message")
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Warn, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Level.Assert
            w(Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Level.Warn
            w(Exception("exception")) { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Warn, tag=Sketch, msg=message, tr=java.lang.Exception: exception)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Level.Assert
            w(Exception("exception"), "message")
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Level.Warn
            w(Exception("exception"), "message")
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Warn, tag=Sketch, msg=message, tr=java.lang.Exception: exception)",
                testPipeline.logList[0].toString()
            )
        }
    }

    @Test
    fun testE() {
        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Level.Assert
            e { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Level.Error
            e { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Error, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Level.Assert
            e("message")
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Level.Error
            e("message")
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Error, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Level.Assert
            e(Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Level.Error
            e(Exception("exception")) { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Error, tag=Sketch, msg=message, tr=java.lang.Exception: exception)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Level.Assert
            e(Exception("exception"), "message")
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Level.Error
            e(Exception("exception"), "message")
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Error, tag=Sketch, msg=message, tr=java.lang.Exception: exception)",
                testPipeline.logList[0].toString()
            )
        }
    }

    @Test
    fun testFlush() {
        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)

            flush()
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Assert, tag=Sketch, msg=flush, tr=null)",
                testPipeline.logList[0].toString()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val logger1 = Logger()
        val logger11 = Logger()
        val logger2 = Logger(level = Logger.Level.Error)
        val logger3 = Logger(level = Logger.Level.Error, pipeline = TestPipeline())

        Assert.assertEquals(logger1, logger11)
        Assert.assertNotEquals(logger1, logger2)
        Assert.assertNotEquals(logger1, logger3)
        Assert.assertNotEquals(logger1.hashCode(), logger2.hashCode())
        Assert.assertNotEquals(logger1.hashCode(), logger3.hashCode())
    }

    private class TestPipeline : Logger.Pipeline {

        val logList = LinkedList<LogEntry>()

        override fun log(level: Logger.Level, tag: String, msg: String, tr: Throwable?) {
            if (!msg.contains("setLevel")) {
                logList.add(LogEntry(level, tag, msg, tr))
            }
        }

        override fun flush() {
            logList.add(LogEntry(Logger.Level.Assert, "Sketch", "flush", null))
        }

        private data class LogEntry(
            val level: Logger.Level,
            val tag: String,
            val msg: String,
            val tr: Throwable?
        ) {
            override fun toString(): String {
                return "LogEntry(level=$level, tag=$tag, msg=$msg, tr=$tr)"
            }
        }
    }
}