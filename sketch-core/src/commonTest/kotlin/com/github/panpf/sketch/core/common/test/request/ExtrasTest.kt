package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.request.Extras
import com.github.panpf.sketch.request.count
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.request.isNotEmpty
import com.github.panpf.sketch.request.merged
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ExtrasTest {

    @Test
    fun testSizeAndCount() {
        Extras.Builder().build().apply {
            assertEquals(0, size)
            assertEquals(0, count())
        }

        Extras.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertEquals(1, size)
            assertEquals(1, count())
        }

        Extras.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().apply {
            assertEquals(2, size)
            assertEquals(2, count())
        }
    }

    @Test
    fun testIsEmptyAndIsNotEmpty() {
        Extras.Builder().build().apply {
            assertTrue(isEmpty())
            assertFalse(isNotEmpty())
        }

        Extras.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }

        Extras.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }
    }

    @Test
    fun testValueAndGetAndCount() {
        Extras.Builder().build().apply {
            assertNull(value("key1"))
            assertNull(this["key1"])
            assertNull(value("key2"))
            assertNull(this["key2"])
            assertEquals(0, count())
        }

        Extras.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertEquals("value1", value("key1"))
            assertEquals("value1", this["key1"])
            assertNull(value("key2"))
            assertNull(this["key2"])
            assertEquals(1, count())
        }

        Extras.Builder().apply {
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
        Extras.Builder().build().apply {
            assertEquals("Extras()", key)
        }

        Extras.Builder().apply {
            set("key1", null)
        }.build().apply {
            assertEquals("Extras(key1:null)", key)
        }

        Extras.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertEquals("Extras(key1:value1)", key)
        }

        Extras.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().apply {
            assertEquals("Extras(key1:value1,key2:value2)", key)
        }

        // sorted
        Extras.Builder().apply {
            set("key2", "value2")
            set("key1", "value1")
        }.build().apply {
            assertEquals("Extras(key1:value1,key2:value2)", key)
        }
    }

    @Test
    fun testRequestKey() {
        Extras.Builder().apply {
            set(key = "key1", value = "value1")
            set(key = "key2", value = "value2", requestKey = null)
            set(key = "key3", value = "value3", requestKey = "requestKey3")
        }.build().apply {
            assertEquals("value1", entry("key1")?.requestKey)
            assertNull(entry("key2")?.requestKey)
            assertEquals("requestKey3", entry("key3")?.requestKey)

            assertEquals("Extras(key1:value1,key3:requestKey3)", requestKey)
            assertEquals(
                mapOf(
                    "key1" to "value1",
                    "key3" to "requestKey3",
                ),
                requestKeys()
            )
        }

        Extras.Builder().build().apply {
            assertNull(entry("key1")?.requestKey)
            assertNull(entry("key2")?.requestKey)
            assertNull(entry("key3")?.requestKey)

            assertNull(requestKey)
            assertEquals(mapOf(), requestKeys())
        }
    }

    @Test
    fun testCacheKey() {
        Extras.Builder().apply {
            set(key = "key1", value = "value1")
            set(key = "key2", value = "value2", cacheKey = null)
            set(key = "key3", value = "value3", cacheKey = "cacheKey3")
        }.build().apply {
            assertEquals("value1", entry("key1")?.cacheKey)
            assertNull(entry("key2")?.cacheKey)
            assertEquals("cacheKey3", entry("key3")?.cacheKey)

            assertEquals("Extras(key1:value1,key3:cacheKey3)", cacheKey)
            assertEquals(
                mapOf(
                    "key1" to "value1",
                    "key3" to "cacheKey3",
                ),
                cacheKeys()
            )
        }

        Extras.Builder().build().apply {
            assertNull(entry("key1")?.cacheKey)
            assertNull(entry("key2")?.cacheKey)
            assertNull(entry("key3")?.cacheKey)

            assertNull(cacheKey)
            assertEquals(mapOf(), cacheKeys())
        }
    }

    @Test
    fun testEntry() {
        Extras.Builder().apply {
            set(key = "key1", value = "value1")
            set(key = "key2", value = "value2", cacheKey = null)
            set(key = "key3", value = "value3", cacheKey = "cacheKey3")
        }.build().apply {
            assertEquals(
                Extras.Entry(value = "value1", cacheKey = "value1", requestKey = "value1"),
                entry("key1")
            )
            assertEquals(
                Extras.Entry(value = "value2", cacheKey = null, requestKey = "value2"),
                entry("key2")
            )
            assertEquals(
                Extras.Entry(
                    value = "value3",
                    cacheKey = "cacheKey3",
                    requestKey = "value3"
                ), entry("key3")
            )
            assertNull(entry("key4"))
        }
    }

    @Test
    fun testValues() {
        Extras.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertEquals(
                mapOf("key1" to "value1"),
                values()
            )
        }

        Extras.Builder().apply {
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
        Extras.Builder().build().joinToString {
            "${it.first}:${it.second.value}:${it.second.cacheKey}"
        }.apply {
            assertEquals("", this)
        }

        Extras.Builder().apply {
            set("key1", "value1")
        }.build().joinToString {
            "${it.first}:${it.second.value}:${it.second.cacheKey}"
        }.apply {
            assertEquals("key1:value1:value1", this)
        }

        Extras.Builder().apply {
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
        val extras1 = Extras.Builder().build()
        val extras11 = Extras.Builder().build()

        val extras2 = Extras.Builder().apply {
            set("key1", "value1")
        }.build()
        val extras3 = Extras.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build()

        assertEquals(extras1, extras11)
        assertNotEquals(extras1, extras2)
        assertNotEquals(extras1, extras3)
        assertNotEquals(extras2, extras3)
        assertNotEquals(extras2, Any())
        assertNotEquals(extras2, null as Extras?)

        assertEquals(extras1.hashCode(), extras11.hashCode())
        assertNotEquals(extras1.hashCode(), extras2.hashCode())
        assertNotEquals(extras1.hashCode(), extras3.hashCode())
        assertNotEquals(extras2.hashCode(), extras3.hashCode())
    }

    @Test
    fun testToString() {
        Extras.Builder().build().apply {
            assertEquals("Extras({})", toString())
        }

        Extras.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertEquals(
                "Extras({key1=Entry(value=value1, cacheKey=value1, requestKey=value1)})",
                toString()
            )
        }

        Extras.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().apply {
            assertEquals(
                "Extras({key1=Entry(value=value1, cacheKey=value1, requestKey=value1), key2=Entry(value=value2, cacheKey=value2, requestKey=value2)})",
                toString()
            )
        }
    }

    @Test
    fun testNewBuilder() {
        Extras.Builder().build().apply {
            assertEquals("Extras({})", toString())
        }.newBuilder().build().apply {
            assertEquals("Extras({})", toString())
        }.newBuilder {
            set("key1", "value1")
        }.build().apply {
            assertEquals(
                "Extras({key1=Entry(value=value1, cacheKey=value1, requestKey=value1)})",
                toString()
            )
        }.newBuilder {
            set("key2", "value2")
        }.build().apply {
            assertEquals(
                "Extras({key1=Entry(value=value1, cacheKey=value1, requestKey=value1), key2=Entry(value=value2, cacheKey=value2, requestKey=value2)})",
                toString()
            )
        }
    }

    @Test
    fun testNewExtras() {
        Extras.Builder().build().apply {
            assertEquals("Extras({})", toString())
        }.newExtras().apply {
            assertEquals("Extras({})", toString())
        }.newExtras {
            set("key1", "value1")
        }.apply {
            assertEquals(
                "Extras({key1=Entry(value=value1, cacheKey=value1, requestKey=value1)})",
                toString()
            )
        }.newExtras {
            set("key2", "value2", "value2")
        }.apply {
            assertEquals(
                "Extras({key1=Entry(value=value1, cacheKey=value1, requestKey=value1), key2=Entry(value=value2, cacheKey=value2, requestKey=value2)})",
                toString()
            )
        }
    }

    @Test
    fun testMerged() {
        val extras0 = Extras.Builder().build().apply {
            assertEquals("Extras({})", toString())
        }

        val extras1 = Extras.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertEquals(
                "Extras({key1=Entry(value=value1, cacheKey=value1, requestKey=value1)})",
                toString()
            )
        }

        val extras11 = Extras.Builder().apply {
            set("key1", "value11")
        }.build().apply {
            assertEquals(
                "Extras({key1=Entry(value=value11, cacheKey=value11, requestKey=value11)})",
                toString()
            )
        }

        val extras2 = Extras.Builder().apply {
            set("key21", "value21")
            set("key22", "value22")
        }.build().apply {
            assertEquals(
                "Extras({key21=Entry(value=value21, cacheKey=value21, requestKey=value21), key22=Entry(value=value22, cacheKey=value22, requestKey=value22)})",
                toString()
            )
        }

        extras0.merged(extras0).apply {
            assertEquals("Extras({})", toString())
        }
        extras0.merged(extras1).apply {
            assertEquals(
                "Extras({key1=Entry(value=value1, cacheKey=value1, requestKey=value1)})",
                toString()
            )
        }
        extras0.merged(extras2).apply {
            assertEquals(
                "Extras({key21=Entry(value=value21, cacheKey=value21, requestKey=value21), key22=Entry(value=value22, cacheKey=value22, requestKey=value22)})",
                toString()
            )
        }

        extras1.merged(extras2).apply {
            assertEquals(
                "Extras({key1=Entry(value=value1, cacheKey=value1, requestKey=value1), key21=Entry(value=value21, cacheKey=value21, requestKey=value21), key22=Entry(value=value22, cacheKey=value22, requestKey=value22)})",
                toString()
            )
        }

        assertNotNull(extras1["key1"])
        assertNotNull(extras11["key1"])
        assertNotEquals(extras1["key1"], extras11["key1"])

        extras1.merged(extras11).apply {
            assertEquals(
                "Extras({key1=Entry(value=value1, cacheKey=value1, requestKey=value1)})",
                toString()
            )
        }
        extras11.merged(extras1).apply {
            assertEquals(
                "Extras({key1=Entry(value=value11, cacheKey=value11, requestKey=value11)})",
                toString()
            )
        }

        extras1.merged(null).apply {
            assertSame(extras1, this)
        }
        null.merged(extras1).apply {
            assertSame(extras1, this)
        }
    }

    /**
     * Returns a map of keys to non-null cache keys. Keys with a null cache key are filtered.
     */
    private fun Extras.cacheKeys(): Map<String, String> {
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

    /**
     * Returns a map of keys to non-null request keys. Keys with a null cache key are filtered.
     */
    private fun Extras.requestKeys(): Map<String, String> {
        return if (isEmpty()) {
            emptyMap()
        } else {
            entries.mapNotNull {
                it.value.requestKey?.let { requestKey ->
                    it.key to requestKey
                }
            }.toMap()
        }
    }
}