package com.github.panpf.sketch.http.core.common.test.util

import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.NullableKey
import com.github.panpf.sketch.util.key
import com.github.panpf.sketch.util.keyOrNull
import kotlin.test.Test
import kotlin.test.assertEquals

class KeysTest {

    @Test
    fun testKey() {
        assertEquals("Key", key(TestKey1()))
        assertEquals("TestKey2", key(TestKey2()))
        assertEquals("Key3", key("Key3"))
    }

    @Test
    fun testKeyOrNull() {
        assertEquals("Key", keyOrNull(TestKey1()))
        assertEquals(null as String?, keyOrNull(TestKey2()))
        assertEquals("Key3", keyOrNull("Key3"))
        assertEquals(null as String?, keyOrNull(null as Any?))
    }

    private class TestKey1 : Key {
        override val key: String = "Key"
    }

    private class TestKey2 : NullableKey {

        override val key: String? = null

        override fun toString(): String {
            return "TestKey2"
        }
    }
}