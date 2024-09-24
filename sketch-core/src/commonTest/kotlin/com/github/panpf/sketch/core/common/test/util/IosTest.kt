package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.util.closeQuietly
import okio.Closeable
import kotlin.test.Test
import kotlin.test.assertFailsWith

class IosTest {

    @Test
    fun testCloseQuietly() {
        // Test case where close() does not throw an exception
        val closeable = object : Closeable {
            override fun close() {
                // No-op
            }
        }
        closeable.closeQuietly()

        // Test case where close() throws a RuntimeException
        val runtimeExceptionCloseable = object : Closeable {
            override fun close() {
                throw RuntimeException("Test RuntimeException")
            }
        }
        assertFailsWith<RuntimeException> {
            runtimeExceptionCloseable.closeQuietly()
        }

        // Test case where close() throws a general Exception
        val exceptionCloseable = object : Closeable {
            override fun close() {
                throw Exception("Test Exception")
            }
        }
        exceptionCloseable.closeQuietly() // Should not throw
    }
}