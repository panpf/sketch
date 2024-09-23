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

package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Logger.Level
import com.github.panpf.sketch.util.Logger.Pipeline
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class LoggerTest {

    @Test
    fun testConstructor() {
        Logger().apply {
            assertEquals(Level.Info, level)
        }
        Logger(level = Level.Debug).apply {
            assertEquals(Level.Debug, level)
        }

        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            assertEquals(0, testPipeline.logList.size)

            w { "messageTest1" }
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Warn, tag=Sketch, msg=messageTest1, tr=null)",
                testPipeline.logList[0].toString()
            )

            e { "messageTest2" }
            assertEquals(2, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Error, tag=Sketch, msg=messageTest2, tr=null)",
                testPipeline.logList[1].toString()
            )
        }
    }

    @Test
    fun testLevelProperty() {
        Logger().apply {
            assertEquals(Level.Info, level)
            level = Level.Error
            assertEquals(Level.Error, level)
        }
    }

    @Test
    fun testIsLoggable() {
        Logger().apply {
            level = Level.Verbose
            assertTrue(isLoggable(Level.Verbose))
            assertTrue(isLoggable(Level.Debug))
            assertTrue(isLoggable(Level.Info))
            assertTrue(isLoggable(Level.Warn))
            assertTrue(isLoggable(Level.Error))
            assertTrue(isLoggable(Level.Assert))

            level = Level.Debug
            assertFalse(isLoggable(Level.Verbose))
            assertTrue(isLoggable(Level.Debug))
            assertTrue(isLoggable(Level.Info))
            assertTrue(isLoggable(Level.Warn))
            assertTrue(isLoggable(Level.Error))
            assertTrue(isLoggable(Level.Assert))

            level = Level.Info
            assertFalse(isLoggable(Level.Verbose))
            assertFalse(isLoggable(Level.Debug))
            assertTrue(isLoggable(Level.Info))
            assertTrue(isLoggable(Level.Warn))
            assertTrue(isLoggable(Level.Error))
            assertTrue(isLoggable(Level.Assert))

            level = Level.Warn
            assertFalse(isLoggable(Level.Verbose))
            assertFalse(isLoggable(Level.Debug))
            assertFalse(isLoggable(Level.Info))
            assertTrue(isLoggable(Level.Warn))
            assertTrue(isLoggable(Level.Error))
            assertTrue(isLoggable(Level.Assert))

            level = Level.Error
            assertFalse(isLoggable(Level.Verbose))
            assertFalse(isLoggable(Level.Debug))
            assertFalse(isLoggable(Level.Info))
            assertFalse(isLoggable(Level.Warn))
            assertTrue(isLoggable(Level.Error))
            assertTrue(isLoggable(Level.Assert))

            level = Level.Assert
            assertFalse(isLoggable(Level.Verbose))
            assertFalse(isLoggable(Level.Debug))
            assertFalse(isLoggable(Level.Info))
            assertFalse(isLoggable(Level.Warn))
            assertFalse(isLoggable(Level.Error))
            assertTrue(isLoggable(Level.Assert))
        }
    }

    @Test
    fun testV() {
        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)
            level = Level.Assert
            v { throw Exception("It's not supposed to be here") }
            assertEquals(0, testPipeline.logList.size)

            level = Level.Verbose
            v { "message" }
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Verbose, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)
            level = Level.Assert
            v(Exception("exception")) {
                throw Exception("It's not supposed to be here")
            }
            assertEquals(0, testPipeline.logList.size)

            level = Level.Verbose
            v(Exception("exception")) { "message" }
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Verbose, tag=Sketch, msg=message, tr=${Exception("exception")})",
                testPipeline.logList[0].toString()
            )
        }
    }

    @Test
    fun testD() {
        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)
            level = Level.Assert
            d { throw Exception("It's not supposed to be here") }
            assertEquals(0, testPipeline.logList.size)

            level = Level.Debug
            d { "message" }
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Debug, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)
            level = Level.Assert
            d(Exception("exception")) {
                throw Exception("It's not supposed to be here")
            }
            assertEquals(0, testPipeline.logList.size)

            level = Level.Debug
            d(Exception("exception")) { "message" }
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Debug, tag=Sketch, msg=message, tr=${Exception("exception")})",
                testPipeline.logList[0].toString()
            )
        }
    }

    @Test
    fun testI() {
        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)
            level = Level.Assert
            i { throw Exception("It's not supposed to be here") }
            assertEquals(0, testPipeline.logList.size)

            level = Level.Info
            i { "message" }
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Info, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)
            level = Level.Assert
            i(Exception("exception")) {
                throw Exception("It's not supposed to be here")
            }
            assertEquals(0, testPipeline.logList.size)

            level = Level.Info
            i(Exception("exception")) { "message" }
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Info, tag=Sketch, msg=message, tr=${Exception("exception")})",
                testPipeline.logList[0].toString()
            )
        }
    }

    @Test
    fun testW() {
        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)
            level = Level.Assert
            w { throw Exception("It's not supposed to be here") }
            assertEquals(0, testPipeline.logList.size)

            level = Level.Warn
            w { "message" }
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Warn, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)
            level = Level.Assert
            w("message")
            assertEquals(0, testPipeline.logList.size)

            level = Level.Warn
            w("message")
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Warn, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)
            level = Level.Assert
            w(Exception("exception")) {
                throw Exception("It's not supposed to be here")
            }
            assertEquals(0, testPipeline.logList.size)

            level = Level.Warn
            w(Exception("exception")) { "message" }
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Warn, tag=Sketch, msg=message, tr=${Exception("exception")})",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)
            level = Level.Assert
            w(Exception("exception"), "message")
            assertEquals(0, testPipeline.logList.size)

            level = Level.Warn
            w(Exception("exception"), "message")
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Warn, tag=Sketch, msg=message, tr=${Exception("exception")})",
                testPipeline.logList[0].toString()
            )
        }
    }

    @Test
    fun testE() {
        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)
            level = Level.Assert
            e { throw Exception("It's not supposed to be here") }
            assertEquals(0, testPipeline.logList.size)

            level = Level.Error
            e { "message" }
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Error, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)
            level = Level.Assert
            e("message")
            assertEquals(0, testPipeline.logList.size)

            level = Level.Error
            e("message")
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Error, tag=Sketch, msg=message, tr=null)",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)
            level = Level.Assert
            e(Exception("exception")) {
                throw Exception("It's not supposed to be here")
            }
            assertEquals(0, testPipeline.logList.size)

            level = Level.Error
            e(Exception("exception")) { "message" }
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Error, tag=Sketch, msg=message, tr=${Exception("exception")})",
                testPipeline.logList[0].toString()
            )

            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)
            level = Level.Assert
            e(Exception("exception"), "message")
            assertEquals(0, testPipeline.logList.size)

            level = Level.Error
            e(Exception("exception"), "message")
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Error, tag=Sketch, msg=message, tr=${Exception("exception")})",
                testPipeline.logList[0].toString()
            )
        }
    }

    @Test
    fun testFlush() {
        val testPipeline = TestPipeline()
        Logger(pipeline = testPipeline).apply {
            testPipeline.logList.clear()
            assertEquals(0, testPipeline.logList.size)

            flush()
            assertEquals(1, testPipeline.logList.size)
            assertEquals(
                "LogEntry(level=Assert, tag=Sketch, msg=flush, tr=null)",
                testPipeline.logList[0].toString()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val logger1 = Logger()
        val logger11 = Logger()
        val logger2 = Logger(level = Level.Error)
        val logger3 = Logger(level = Level.Error, pipeline = TestPipeline())

        assertEquals(logger1, logger11)
        assertNotEquals(logger1, logger2)
        assertNotEquals(logger1, logger3)
        assertNotEquals(logger1.hashCode(), logger2.hashCode())
        assertNotEquals(logger1.hashCode(), logger3.hashCode())
    }

    @Test
    fun testLevelEnum() {
        @Suppress("EnumValuesSoftDeprecate")
        assertEquals(
            expected = "Verbose, Debug, Info, Warn, Error, Assert",
            actual = Logger.Level.values().joinToString()
        )
    }

    private class TestPipeline : Pipeline {

        val logList = mutableListOf<LogEntry>()

        override fun log(level: Level, tag: String, msg: String, tr: Throwable?) {
            if (!msg.contains("setLevel")) {
                logList.add(LogEntry(level, tag, msg, tr))
            }
        }

        override fun flush() {
            logList.add(LogEntry(Level.Assert, "Sketch", "flush", null))
        }

        private data class LogEntry(
            val level: Level,
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