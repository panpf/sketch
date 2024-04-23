package com.github.panpf.sketch.http.test.http

import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.http.isNotEmpty
import com.github.panpf.sketch.http.merged
import kotlin.test.*

class HttpHeadersTest {

    @Test
    fun testNewBuilder() {
        val httpHeaders = HttpHeaders.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
            add("key3", "value3")
            add("key3", "value31")
        }.build()

        assertEquals(httpHeaders, httpHeaders.newBuilder().build())
        assertNotEquals(httpHeaders, httpHeaders.newBuilder {
            add("key3", "value32")
        }.build())

        assertEquals(httpHeaders, httpHeaders.newHttpHeaders())
        assertNotEquals(httpHeaders, httpHeaders.newHttpHeaders() {
            add("key3", "value32")
        })
    }

    @Test
    fun testSizeAndCount() {
        HttpHeaders.Builder().build().apply {
            assertEquals(0, size)
            assertEquals(0, addSize)
            assertEquals(0, setSize)
        }

        HttpHeaders.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertEquals(1, size)
            assertEquals(0, addSize)
            assertEquals(1, setSize)
        }

        HttpHeaders.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
            set("key1", "value11")
            add("key3", "value3")
            add("key3", "value31")
        }.build().apply {
            assertEquals(4, size)
            assertEquals(2, addSize)
            assertEquals(2, setSize)
        }
    }

    @Test
    fun testIsEmptyAndIsNotEmpty() {
        HttpHeaders.Builder().build().apply {
            assertTrue(isEmpty())
            assertFalse(isNotEmpty())
        }

        HttpHeaders.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }

        HttpHeaders.Builder().apply {
            add("key1", "value1")
        }.build().apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }

        HttpHeaders.Builder().apply {
            set("key1", "value1")
            add("key2", "value2")
        }.build().apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }
    }

    @Test
    fun testAddSetGetRemove() {
        HttpHeaders.Builder().build().apply {
            assertNull(getSet("key1"))
            assertNull(getAdd("key2"))
        }

        HttpHeaders.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            assertEquals("value1", getSet("key1"))
            assertNull(getAdd("key2"))
        }

        HttpHeaders.Builder().apply {
            add("key2", "value2")
        }.build().apply {
            assertNull(getSet("key1"))
            assertEquals(listOf("value2"), getAdd("key2"))
        }

        HttpHeaders.Builder().apply {
            set("key1", "value1")
            add("key2", "value2")
        }.build().apply {
            assertEquals("value1", getSet("key1"))
            assertEquals(listOf("value2"), getAdd("key2"))
        }

        // key conflict
        HttpHeaders.Builder().apply {
            set("key1", "value1")
            set("key1", "value11")
            add("key2", "value2")
            add("key2", "value21")
        }.build().apply {
            assertEquals("value11", getSet("key1"))
            assertEquals(listOf("value2", "value21"), getAdd("key2"))
        }

        // key conflict on add set
        HttpHeaders.Builder().apply {
            set("key1", "value1")
            add("key1", "value11")
        }.build().apply {
            assertNull(getSet("key1"))
            assertEquals(listOf("value11"), getAdd("key1"))
        }
        HttpHeaders.Builder().apply {
            add("key1", "value11")
            set("key1", "value1")
        }.build().apply {
            assertEquals("value1", getSet("key1"))
            assertNull(getAdd("key1"))
        }

        // remove
        HttpHeaders.Builder().apply {
            set("key1", "value1")
            add("key2", "value2")
        }.build().apply {
            assertEquals("value1", getSet("key1"))
            assertEquals(listOf("value2"), getAdd("key2"))
        }.newHttpHeaders {
            removeAll("key1")
        }.apply {
            assertNull(getSet("key1"))
            assertEquals(listOf("value2"), getAdd("key2"))
        }
        HttpHeaders.Builder().apply {
            set("key1", "value1")
            add("key2", "value2")
        }.build().apply {
            assertEquals("value1", getSet("key1"))
            assertEquals(listOf("value2"), getAdd("key2"))
        }.newHttpHeaders {
            removeAll("key2")
        }.apply {
            assertEquals("value1", getSet("key1"))
            assertNull(getAdd("key2"))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = HttpHeaders.Builder().apply {
            set("key1", "value1")
            add("key2", "value2")
        }.build()
        val element11 = HttpHeaders.Builder().apply {
            set("key1", "value1")
            add("key2", "value2")
        }.build()
        val element2 = HttpHeaders.Builder().apply {
            set("key1", "value1")
            add("key3", "value3")
        }.build()
        val element3 = HttpHeaders.Builder().apply {
            set("key3", "value3")
            add("key2", "value2")
        }.build()

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element1, element3)
        assertNotSame(element2, element11)
        assertNotSame(element2, element3)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element11)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as HttpHeaders?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        HttpHeaders.Builder().build().apply {
            assertEquals("HttpHeaders(sets=[],adds=[])", toString())
        }

        HttpHeaders.Builder().apply {
            set("key1", "value1")
            add("key2", "value2")
        }.build().apply {
            assertEquals("HttpHeaders(sets=[key1:value1],adds=[key2:value2])", toString())
        }

        HttpHeaders.Builder().apply {
            set("key1", "value1")
            add("key2", "value2")
            add("key2", "value21")
        }.build().apply {
            assertEquals(
                "HttpHeaders(sets=[key1:value1],adds=[key2:value2,key2:value21])",
                toString()
            )
        }
    }

    @Test
    fun testMerged() {
        val httpHeaders0 = HttpHeaders.Builder().build().apply {
            assertEquals("HttpHeaders(sets=[],adds=[])", toString())
        }

        val httpHeaders1 = HttpHeaders.Builder().apply {
            set("set1", "setValue1")
            add("add1", "addValue1")
        }.build().apply {
            assertEquals(
                "HttpHeaders(sets=[set1:setValue1],adds=[add1:addValue1])",
                toString()
            )
        }

        val httpHeaders11 = HttpHeaders.Builder().apply {
            set("set1", "setValue11")
            add("add1", "addValue11")
        }.build().apply {
            assertEquals(
                "HttpHeaders(sets=[set1:setValue11],adds=[add1:addValue11])",
                toString()
            )
        }

        val httpHeaders2 = HttpHeaders.Builder().apply {
            set("set21", "setValue21")
            set("set22", "setValue22")
            add("add21", "addValue21")
            add("add22", "addValue22")
        }.build().apply {
            assertEquals(
                "HttpHeaders(sets=[set21:setValue21,set22:setValue22],adds=[add21:addValue21,add22:addValue22])",
                toString()
            )
        }

        httpHeaders0.merged(httpHeaders0).apply {
            assertEquals("HttpHeaders(sets=[],adds=[])", toString())
        }
        httpHeaders0.merged(httpHeaders1).apply {
            assertEquals(
                "HttpHeaders(sets=[set1:setValue1],adds=[add1:addValue1])",
                toString()
            )
        }
        httpHeaders0.merged(httpHeaders2).apply {
            assertEquals(
                "HttpHeaders(sets=[set21:setValue21,set22:setValue22],adds=[add21:addValue21,add22:addValue22])",
                toString()
            )
        }

        httpHeaders1.merged(httpHeaders2).apply {
            assertEquals(
                "HttpHeaders(sets=[set1:setValue1,set21:setValue21,set22:setValue22],adds=[add1:addValue1,add21:addValue21,add22:addValue22])",
                toString()
            )
        }

        httpHeaders1.merged(httpHeaders11).apply {
            assertEquals(
                "HttpHeaders(sets=[set1:setValue1],adds=[add1:addValue1,add1:addValue11])",
                toString()
            )
        }
        httpHeaders11.merged(httpHeaders1).apply {
            assertEquals(
                "HttpHeaders(sets=[set1:setValue11],adds=[add1:addValue11,add1:addValue1])",
                toString()
            )
        }

        httpHeaders1.merged(null).apply {
            assertSame(httpHeaders1, this)
        }
        null.merged(httpHeaders1).apply {
            assertSame(httpHeaders1, this)
        }
    }
}