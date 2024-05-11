package com.github.panpf.sketch.http.core.test.request

import com.github.panpf.sketch.request.Parameters
import com.github.panpf.sketch.request.Parameters.Entry
import com.github.panpf.sketch.request.count
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.request.isNotEmpty
import com.github.panpf.sketch.request.merged
import kotlin.test.Test
import kotlin.test.*

class ParametersTest {

    @Test
    fun testSizeAndCount() {
        Parameters.Builder().build().apply {
            assertEquals(0, size)
            assertEquals(0, count())
        }

        Parameters.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertEquals(1, size)
            assertEquals(1, count())
        }

        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().apply {
            assertEquals(2, size)
            assertEquals(2, count())
        }
    }

    @Test
    fun testIsEmptyAndIsNotEmpty() {
        Parameters.Builder().build().apply {
            assertTrue(isEmpty())
            assertFalse(isNotEmpty())
        }

        Parameters.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }

        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }
    }

    @Test
    fun testValueAndGetAndCount() {
        Parameters.Builder().build().apply {
            assertNull(value("key1"))
            assertNull(this["key1"])
            assertNull(value("key2"))
            assertNull(this["key2"])
            assertEquals(0, count())
        }

        Parameters.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertEquals("value1", value("key1"))
            assertEquals("value1", this["key1"])
            assertNull(value("key2"))
            assertNull(this["key2"])
            assertEquals(1, count())
        }

        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().apply {
            assertEquals("value1", value("key1"))
            assertEquals("value1", this["key1"])
            assertEquals("value2", value("key2"))
            assertEquals("value2", this["key2"])
            assertEquals(2, count())
        }
    }

    @Test
    fun testKey() {
        Parameters.Builder().build().apply {
            assertNull(key)
        }

        Parameters.Builder().apply {
            set("key1", null)
        }.build().apply {
            assertNull(key)
        }

        Parameters.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertEquals("Parameters(key1:value1)", key)
        }

        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().apply {
            assertEquals("Parameters(key1:value1,key2:value2)", key)
        }

        // sorted
        Parameters.Builder().apply {
            set("key2", "value2")
            set("key1", "value1")
        }.build().apply {
            assertEquals("Parameters(key1:value1,key2:value2)", key)
        }
    }

    // TODO testRequestKey

    @Test
    fun testCacheKey() {
        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2", null)
            set("key3", "value3", "cacheKey3")
        }.build().apply {
            assertEquals("value1", entry("key1")?.cacheKey)
            assertNull(entry("key2")?.cacheKey)
            assertEquals("cacheKey3", entry("key3")?.cacheKey)

            assertEquals("Parameters(key1:value1,key3:cacheKey3)", cacheKey)
            assertEquals(
                mapOf(
                    "key1" to "value1",
                    "key3" to "cacheKey3",
                ),
                cacheKeys()
            )
        }

        Parameters.Builder().build().apply {
            assertNull(entry("key1")?.cacheKey)
            assertNull(entry("key2")?.cacheKey)
            assertNull(entry("key3")?.cacheKey)

            assertNull(cacheKey)
            assertEquals(mapOf(), cacheKeys())
        }
    }

    @Test
    fun testEntry() {
        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2", null)
            set("key3", "value3", "cacheKey3")
        }.build().apply {
            assertEquals(Entry("value1", "value1"), entry("key1"))
            assertEquals(Entry("value2", null), entry("key2"))
            assertEquals(Entry("value3", "cacheKey3"), entry("key3"))
            assertNull(entry("key4"))
        }

        // TODO notJoinRequestKey
    }

    @Test
    fun testValues() {
        Parameters.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertEquals(
                mapOf("key1" to "value1"),
                values()
            )
        }

        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2", null)
            set("key3", "value3", "cacheKey3")
        }.build().apply {
            assertEquals(
                mapOf(
                    "key1" to "value1",
                    "key2" to "value2",
                    "key3" to "value3",
                ),
                values()
            )
        }
    }

    @Test
    fun testIterator() {
        Parameters.Builder().build().joinToString {
            "${it.first}:${it.second.value}:${it.second.cacheKey}"
        }.apply {
            assertEquals("", this)
        }

        Parameters.Builder().apply {
            set("key1", "value1")
        }.build().joinToString {
            "${it.first}:${it.second.value}:${it.second.cacheKey}"
        }.apply {
            assertEquals("key1:value1:value1", this)
        }

        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().joinToString {
            "${it.first}:${it.second.value}:${it.second.cacheKey}"
        }.apply {
            assertEquals("key1:value1:value1, key2:value2:value2", this)
        }
    }

    @Test
    fun testEquals() {
        val parameters1 = Parameters.Builder().build()
        val parameters11 = Parameters.Builder().build()

        val parameters2 = Parameters.Builder().apply {
            set("key1", "value1")
        }.build()
        val parameters21 = Parameters.Builder().apply {
            set("key1", "value1")
        }.build()

        val parameters3 = Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build()
        val parameters31 = Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build()

        assertNotSame(parameters1, parameters11)
        assertNotSame(parameters2, parameters21)
        assertNotSame(parameters3, parameters31)

        assertEquals(parameters1, parameters1)
        assertEquals(parameters1, parameters11)
        assertEquals(parameters2, parameters21)
        assertEquals(parameters3, parameters31)

        assertNotEquals(parameters1, parameters2)
        assertNotEquals(parameters1, parameters3)
        assertNotEquals(parameters2, parameters3)

        assertNotEquals(parameters2, Any())
        assertNotEquals(parameters2, null as Parameters?)
    }

    @Test
    fun testHashCode() {
        val parameters1 = Parameters.Builder().build()
        val parameters11 = Parameters.Builder().build()

        val parameters2 = Parameters.Builder().apply {
            set("key1", "value1")
        }.build()
        val parameters21 = Parameters.Builder().apply {
            set("key1", "value1")
        }.build()

        val parameters3 = Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build()
        val parameters31 = Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build()

        assertEquals(parameters1.hashCode(), parameters11.hashCode())
        assertEquals(parameters2.hashCode(), parameters21.hashCode())
        assertEquals(parameters3.hashCode(), parameters31.hashCode())

        assertNotEquals(parameters1.hashCode(), parameters2.hashCode())
        assertNotEquals(parameters1.hashCode(), parameters3.hashCode())
        assertNotEquals(parameters2.hashCode(), parameters3.hashCode())
    }

    @Test
    fun testToString() {
        Parameters.Builder().build().apply {
            assertEquals("Parameters({})", toString())
        }

        Parameters.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertEquals(
                "Parameters({key1=Entry(value=value1, cacheKey=value1, notJoinRequestKey=false)})",
                toString()
            )
        }

        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().apply {
            assertEquals(
                "Parameters({key1=Entry(value=value1, cacheKey=value1, notJoinRequestKey=false), key2=Entry(value=value2, cacheKey=value2, notJoinRequestKey=false)})",
                toString()
            )
        }
    }

    @Test
    fun testNewBuilder() {
        Parameters.Builder().build().apply {
            assertEquals("Parameters({})", toString())
        }.newBuilder().build().apply {
            assertEquals("Parameters({})", toString())
        }.newBuilder {
            set("key1", "value1")
        }.build().apply {
            assertEquals(
                "Parameters({key1=Entry(value=value1, cacheKey=value1, notJoinRequestKey=false)})",
                toString()
            )
        }.newBuilder {
            set("key2", "value2")
        }.build().apply {
            assertEquals(
                "Parameters({key1=Entry(value=value1, cacheKey=value1, notJoinRequestKey=false), key2=Entry(value=value2, cacheKey=value2, notJoinRequestKey=false)})",
                toString()
            )
        }
    }

    @Test
    fun testNewParameters() {
        Parameters.Builder().build().apply {
            assertEquals("Parameters({})", toString())
        }.newParameters().apply {
            assertEquals("Parameters({})", toString())
        }.newParameters {
            set("key1", "value1")
        }.apply {
            assertEquals(
                "Parameters({key1=Entry(value=value1, cacheKey=value1, notJoinRequestKey=false)})",
                toString()
            )
        }.newParameters {
            set("key2", "value2", "value2")
        }.apply {
            assertEquals(
                "Parameters({key1=Entry(value=value1, cacheKey=value1, notJoinRequestKey=false), key2=Entry(value=value2, cacheKey=value2, notJoinRequestKey=false)})",
                toString()
            )
        }
    }

    @Test
    fun testMerged() {
        val parameters0 = Parameters.Builder().build().apply {
            assertEquals("Parameters({})", toString())
        }

        val parameters1 = Parameters.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertEquals(
                "Parameters({key1=Entry(value=value1, cacheKey=value1, notJoinRequestKey=false)})",
                toString()
            )
        }

        val parameters11 = Parameters.Builder().apply {
            set("key1", "value11")
        }.build().apply {
            assertEquals(
                "Parameters({key1=Entry(value=value11, cacheKey=value11, notJoinRequestKey=false)})",
                toString()
            )
        }

        val parameters2 = Parameters.Builder().apply {
            set("key21", "value21")
            set("key22", "value22")
        }.build().apply {
            assertEquals(
                "Parameters({key21=Entry(value=value21, cacheKey=value21, notJoinRequestKey=false), key22=Entry(value=value22, cacheKey=value22, notJoinRequestKey=false)})",
                toString()
            )
        }

        parameters0.merged(parameters0).apply {
            assertEquals("Parameters({})", toString())
        }
        parameters0.merged(parameters1).apply {
            assertEquals(
                "Parameters({key1=Entry(value=value1, cacheKey=value1, notJoinRequestKey=false)})",
                toString()
            )
        }
        parameters0.merged(parameters2).apply {
            assertEquals(
                "Parameters({key21=Entry(value=value21, cacheKey=value21, notJoinRequestKey=false), key22=Entry(value=value22, cacheKey=value22, notJoinRequestKey=false)})",
                toString()
            )
        }

        parameters1.merged(parameters2).apply {
            assertEquals(
                "Parameters({key1=Entry(value=value1, cacheKey=value1, notJoinRequestKey=false), key21=Entry(value=value21, cacheKey=value21, notJoinRequestKey=false), key22=Entry(value=value22, cacheKey=value22, notJoinRequestKey=false)})",
                toString()
            )
        }

        assertNotNull(parameters1["key1"])
        assertNotNull(parameters11["key1"])
        assertNotEquals(parameters1["key1"], parameters11["key1"])

        parameters1.merged(parameters11).apply {
            assertEquals(
                "Parameters({key1=Entry(value=value1, cacheKey=value1, notJoinRequestKey=false)})",
                toString()
            )
        }
        parameters11.merged(parameters1).apply {
            assertEquals(
                "Parameters({key1=Entry(value=value11, cacheKey=value11, notJoinRequestKey=false)})",
                toString()
            )
        }

        parameters1.merged(null).apply {
            assertSame(parameters1, this)
        }
        null.merged(parameters1).apply {
            assertSame(parameters1, this)
        }
    }

    /** Returns a map of keys to non-null cache keys. Keys with a null cache key are filtered. */
    private fun Parameters.cacheKeys(): Map<String, String> {
        return if (isEmpty()) {
            emptyMap()
        } else {
            entries.mapNotNull {
                it.value.cacheKey?.let { cacheKey ->
                    it.key to cacheKey
                }
            }.toMap()
        }
    }
}