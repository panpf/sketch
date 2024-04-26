/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
            Assert.assertEquals(Logger.Info, level)
        }
        Logger(level = Logger.Debug).apply {
            Assert.assertEquals(Logger.Debug, level)
        }

        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            Assert.assertEquals(0, testPipeline.logList.size)

            w { "messageTest1" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Warn, tag=Sketch, msg=messageTest1, tr=null)",
                testPipeline.logList[0].toString()
            )

            e { "messageTest2" }
            Assert.assertEquals(2, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Error, tag=Sketch, msg=messageTest2, tr=null)",
                testPipeline.logList[1].toString()
            )
        }
    }

    // TODO test module

    @Test
    fun testLevel() {
        Logger().apply {
            Assert.assertEquals(Logger.Info, level)
            level = Logger.Error
            Assert.assertEquals(Logger.Error, level)
        }
    }

    @Test
    fun testIsLoggable() {
        Logger().apply {
            level = Logger.Verbose
            Assert.assertTrue(isLoggable(Logger.Verbose))
            Assert.assertTrue(isLoggable(Logger.Debug))
            Assert.assertTrue(isLoggable(Logger.Info))
            Assert.assertTrue(isLoggable(Logger.Warn))
            Assert.assertTrue(isLoggable(Logger.Error))
            Assert.assertTrue(isLoggable(Logger.Assert))

            level = Logger.Debug
            Assert.assertFalse(isLoggable(Logger.Verbose))
            Assert.assertTrue(isLoggable(Logger.Debug))
            Assert.assertTrue(isLoggable(Logger.Info))
            Assert.assertTrue(isLoggable(Logger.Warn))
            Assert.assertTrue(isLoggable(Logger.Error))
            Assert.assertTrue(isLoggable(Logger.Assert))

            level = Logger.Info
            Assert.assertFalse(isLoggable(Logger.Verbose))
            Assert.assertFalse(isLoggable(Logger.Debug))
            Assert.assertTrue(isLoggable(Logger.Info))
            Assert.assertTrue(isLoggable(Logger.Warn))
            Assert.assertTrue(isLoggable(Logger.Error))
            Assert.assertTrue(isLoggable(Logger.Assert))

            level = Logger.Warn
            Assert.assertFalse(isLoggable(Logger.Verbose))
            Assert.assertFalse(isLoggable(Logger.Debug))
            Assert.assertFalse(isLoggable(Logger.Info))
            Assert.assertTrue(isLoggable(Logger.Warn))
            Assert.assertTrue(isLoggable(Logger.Error))
            Assert.assertTrue(isLoggable(Logger.Assert))

            level = Logger.Error
            Assert.assertFalse(isLoggable(Logger.Verbose))
            Assert.assertFalse(isLoggable(Logger.Debug))
            Assert.assertFalse(isLoggable(Logger.Info))
            Assert.assertFalse(isLoggable(Logger.Warn))
            Assert.assertTrue(isLoggable(Logger.Error))
            Assert.assertTrue(isLoggable(Logger.Assert))

            level = Logger.Assert
            Assert.assertFalse(isLoggable(Logger.Verbose))
            Assert.assertFalse(isLoggable(Logger.Debug))
            Assert.assertFalse(isLoggable(Logger.Info))
            Assert.assertFalse(isLoggable(Logger.Warn))
            Assert.assertFalse(isLoggable(Logger.Error))
            Assert.assertTrue(isLoggable(Logger.Assert))
        }
    }

    @Test
    fun testV() {
        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Assert
            v { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Verbose
            v { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Verbose, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Assert
            v(Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Verbose
            v(Exception("exception")) { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Verbose, tag=Sketch, msg=message, tr=java.lang.Exception: exception)",
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
            level = Logger.Assert
            d { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Debug
            d { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Debug, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Assert
            d(Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Debug
            d(Exception("exception")) { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Debug, tag=Sketch, msg=message, tr=java.lang.Exception: exception)",
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
            level = Logger.Assert
            i { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Info
            i { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Info, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Assert
            i(Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Info
            i(Exception("exception")) { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Info, tag=Sketch, msg=message, tr=java.lang.Exception: exception)",
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
            level = Logger.Assert
            w { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Warn
            w { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Warn, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Assert
            w("message")
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Warn
            w("message")
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Warn, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Assert
            w(Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Warn
            w(Exception("exception")) { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Warn, tag=Sketch, msg=message, tr=java.lang.Exception: exception)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Assert
            w(Exception("exception"), "message")
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Warn
            w(Exception("exception"), "message")
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Warn, tag=Sketch, msg=message, tr=java.lang.Exception: exception)",
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
            level = Logger.Assert
            e { throw java.lang.Exception("It's not supposed to be here") }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Error
            e { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Error, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Assert
            e("message")
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Error
            e("message")
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Error, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Assert
            e(Exception("exception")) {
                throw java.lang.Exception("It's not supposed to be here")
            }
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Error
            e(Exception("exception")) { "message" }
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Error, tag=Sketch, msg=message, tr=java.lang.Exception: exception)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            Assert.assertEquals(0, testPipeline.logList.size)
            level = Logger.Assert
            e(Exception("exception"), "message")
            Assert.assertEquals(0, testPipeline.logList.size)

            level = Logger.Error
            e(Exception("exception"), "message")
            Assert.assertEquals(1, testPipeline.logList.size)
            Assert.assertEquals(
                "LogEntry(level=Logger.Error, tag=Sketch, msg=message, tr=java.lang.Exception: exception)",
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
                "LogEntry(level=Logger.Assert, tag=Sketch, msg=flush, tr=null)",
                testPipeline.logList[0].toString()
            )
        }
    }

    private class TestPipeline : Logger.Pipeline {

        val logList = LinkedList<LogEntry>()

        override fun log(level: Int, tag: String, msg: String, tr: Throwable?) {
            logList.add(LogEntry(level, tag, msg, tr))
        }

        override fun flush() {
            logList.add(LogEntry(Logger.Assert, "Sketch", "flush", null))
        }

        private data class LogEntry(
            @Logger.Level val level: Int,
            val tag: String,
            val msg: String,
            val tr: Throwable?
        )
    }
}