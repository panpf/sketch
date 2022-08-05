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
package com.github.panpf.sketch.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.Parameters
import com.github.panpf.sketch.request.Parameters.Entry
import com.github.panpf.sketch.request.count
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.request.isNotEmpty
import com.github.panpf.sketch.request.merged
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParametersTest {

    @Test
    fun testSizeAndCount() {
        Parameters.Builder().build().apply {
            Assert.assertEquals(0, size)
            Assert.assertEquals(0, count())
        }

        Parameters.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            Assert.assertEquals(1, size)
            Assert.assertEquals(1, count())
        }

        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().apply {
            Assert.assertEquals(2, size)
            Assert.assertEquals(2, count())
        }
    }

    @Test
    fun testIsEmptyAndIsNotEmpty() {
        Parameters.Builder().build().apply {
            Assert.assertTrue(isEmpty())
            Assert.assertFalse(isNotEmpty())
        }

        Parameters.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            Assert.assertFalse(isEmpty())
            Assert.assertTrue(isNotEmpty())
        }

        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().apply {
            Assert.assertFalse(isEmpty())
            Assert.assertTrue(isNotEmpty())
        }
    }

    @Test
    fun testValueAndGetAndCount() {
        Parameters.Builder().build().apply {
            Assert.assertNull(value("key1"))
            Assert.assertNull(this["key1"])
            Assert.assertNull(value("key2"))
            Assert.assertNull(this["key2"])
            Assert.assertEquals(0, count())
        }

        Parameters.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            Assert.assertEquals("value1", value("key1"))
            Assert.assertEquals("value1", this["key1"])
            Assert.assertNull(value("key2"))
            Assert.assertNull(this["key2"])
            Assert.assertEquals(1, count())
        }

        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().apply {
            Assert.assertEquals("value1", value("key1"))
            Assert.assertEquals("value1", this["key1"])
            Assert.assertEquals("value2", value("key2"))
            Assert.assertEquals("value2", this["key2"])
            Assert.assertEquals(2, count())
        }
    }

    @Test
    fun testKey() {
        Parameters.Builder().build().apply {
            Assert.assertNull(key)
        }

        Parameters.Builder().apply {
            set("key1", null)
        }.build().apply {
            Assert.assertNull(key)
        }

        Parameters.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            Assert.assertEquals("Parameters(key1:value1)", key)
        }

        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().apply {
            Assert.assertEquals("Parameters(key1:value1,key2:value2)", key)
        }

        // sorted
        Parameters.Builder().apply {
            set("key2", "value2")
            set("key1", "value1")
        }.build().apply {
            Assert.assertEquals("Parameters(key1:value1,key2:value2)", key)
        }
    }

    @Test
    fun testCacheKey() {
        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2", null)
            set("key3", "value3", "cacheKey3")
        }.build().apply {
            Assert.assertEquals("value1", cacheKey("key1"))
            Assert.assertNull(cacheKey("key2"))
            Assert.assertEquals("cacheKey3", cacheKey("key3"))

            Assert.assertEquals("Parameters(key1:value1,key3:cacheKey3)", cacheKey)
            Assert.assertEquals(
                mapOf(
                    "key1" to "value1",
                    "key3" to "cacheKey3",
                ),
                cacheKeys()
            )
        }

        Parameters.Builder().build().apply {
            Assert.assertNull(cacheKey("key1"))
            Assert.assertNull(cacheKey("key2"))
            Assert.assertNull(cacheKey("key3"))

            Assert.assertNull(cacheKey)
            Assert.assertEquals(mapOf<String, String>(), cacheKeys())
        }
    }

    @Test
    fun testEntry() {
        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2", null)
            set("key3", "value3", "cacheKey3")
        }.build().apply {
            Assert.assertEquals(Entry("value1", "value1"), entry("key1"))
            Assert.assertEquals(Entry("value2", null), entry("key2"))
            Assert.assertEquals(Entry("value3", "cacheKey3"), entry("key3"))
            Assert.assertNull(entry("key4"))
        }
    }

    @Test
    fun testValues() {
        Parameters.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            Assert.assertEquals(
                mapOf("key1" to "value1"),
                values()
            )
        }

        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2", null)
            set("key3", "value3", "cacheKey3")
        }.build().apply {
            Assert.assertEquals(
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
            Assert.assertEquals("", this)
        }

        Parameters.Builder().apply {
            set("key1", "value1")
        }.build().joinToString {
            "${it.first}:${it.second.value}:${it.second.cacheKey}"
        }.apply {
            Assert.assertEquals("key1:value1:value1", this)
        }

        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().joinToString {
            "${it.first}:${it.second.value}:${it.second.cacheKey}"
        }.apply {
            Assert.assertEquals("key1:value1:value1, key2:value2:value2", this)
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

        Assert.assertNotSame(parameters1, parameters11)
        Assert.assertNotSame(parameters2, parameters21)
        Assert.assertNotSame(parameters3, parameters31)

        Assert.assertEquals(parameters1, parameters1)
        Assert.assertEquals(parameters1, parameters11)
        Assert.assertEquals(parameters2, parameters21)
        Assert.assertEquals(parameters3, parameters31)

        Assert.assertNotEquals(parameters1, parameters2)
        Assert.assertNotEquals(parameters1, parameters3)
        Assert.assertNotEquals(parameters2, parameters3)

        Assert.assertNotEquals(parameters2, Any())
        Assert.assertNotEquals(parameters2, null)
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

        Assert.assertEquals(parameters1.hashCode(), parameters11.hashCode())
        Assert.assertEquals(parameters2.hashCode(), parameters21.hashCode())
        Assert.assertEquals(parameters3.hashCode(), parameters31.hashCode())

        Assert.assertNotEquals(parameters1.hashCode(), parameters2.hashCode())
        Assert.assertNotEquals(parameters1.hashCode(), parameters3.hashCode())
        Assert.assertNotEquals(parameters2.hashCode(), parameters3.hashCode())
    }

    @Test
    fun testToString() {
        Parameters.Builder().build().apply {
            Assert.assertEquals("Parameters(map={})", toString())
        }

        Parameters.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            Assert.assertEquals(
                "Parameters(map={key1=Entry(value=value1, cacheKey=value1)})",
                toString()
            )
        }

        Parameters.Builder().apply {
            set("key1", "value1")
            set("key2", "value2")
        }.build().apply {
            Assert.assertEquals(
                "Parameters(map={key1=Entry(value=value1, cacheKey=value1), key2=Entry(value=value2, cacheKey=value2)})",
                toString()
            )
        }
    }

    @Test
    fun testNewBuilder() {
        Parameters.Builder().build().apply {
            Assert.assertEquals("Parameters(map={})", toString())
        }.newBuilder().build().apply {
            Assert.assertEquals("Parameters(map={})", toString())
        }.newBuilder {
            set("key1", "value1")
        }.build().apply {
            Assert.assertEquals(
                "Parameters(map={key1=Entry(value=value1, cacheKey=value1)})",
                toString()
            )
        }.newBuilder {
            set("key2", "value2")
        }.build().apply {
            Assert.assertEquals(
                "Parameters(map={key1=Entry(value=value1, cacheKey=value1), key2=Entry(value=value2, cacheKey=value2)})",
                toString()
            )
        }
    }

    @Test
    fun testNewParameters() {
        Parameters.Builder().build().apply {
            Assert.assertEquals("Parameters(map={})", toString())
        }.newParameters().apply {
            Assert.assertEquals("Parameters(map={})", toString())
        }.newParameters {
            set("key1", "value1")
        }.apply {
            Assert.assertEquals(
                "Parameters(map={key1=Entry(value=value1, cacheKey=value1)})",
                toString()
            )
        }.newParameters {
            set("key2", "value2", "value2")
        }.apply {
            Assert.assertEquals(
                "Parameters(map={key1=Entry(value=value1, cacheKey=value1), key2=Entry(value=value2, cacheKey=value2)})",
                toString()
            )
        }
    }

    @Test
    fun testMerged() {
        val parameters0 = Parameters.Builder().build().apply {
            Assert.assertEquals("Parameters(map={})", toString())
        }

        val parameters1 = Parameters.Builder().apply {
            set("key1", "value1")
        }.build().apply {
            Assert.assertEquals(
                "Parameters(map={key1=Entry(value=value1, cacheKey=value1)})",
                toString()
            )
        }

        val parameters11 = Parameters.Builder().apply {
            set("key1", "value11")
        }.build().apply {
            Assert.assertEquals(
                "Parameters(map={key1=Entry(value=value11, cacheKey=value11)})",
                toString()
            )
        }

        val parameters2 = Parameters.Builder().apply {
            set("key21", "value21")
            set("key22", "value22")
        }.build().apply {
            Assert.assertEquals(
                "Parameters(map={key21=Entry(value=value21, cacheKey=value21), key22=Entry(value=value22, cacheKey=value22)})",
                toString()
            )
        }

        parameters0.merged(parameters0).apply {
            Assert.assertEquals("Parameters(map={})", toString())
        }
        parameters0.merged(parameters1).apply {
            Assert.assertEquals(
                "Parameters(map={key1=Entry(value=value1, cacheKey=value1)})",
                toString()
            )
        }
        parameters0.merged(parameters2).apply {
            Assert.assertEquals(
                "Parameters(map={key21=Entry(value=value21, cacheKey=value21), key22=Entry(value=value22, cacheKey=value22)})",
                toString()
            )
        }

        parameters1.merged(parameters2).apply {
            Assert.assertEquals(
                "Parameters(map={key1=Entry(value=value1, cacheKey=value1), key21=Entry(value=value21, cacheKey=value21), key22=Entry(value=value22, cacheKey=value22)})",
                toString()
            )
        }

        Assert.assertNotNull(parameters1["key1"])
        Assert.assertNotNull(parameters11["key1"])
        Assert.assertNotEquals(parameters1["key1"], parameters11["key1"])

        parameters1.merged(parameters11).apply {
            Assert.assertEquals(
                "Parameters(map={key1=Entry(value=value1, cacheKey=value1)})",
                toString()
            )
        }
        parameters11.merged(parameters1).apply {
            Assert.assertEquals(
                "Parameters(map={key1=Entry(value=value11, cacheKey=value11)})",
                toString()
            )
        }

        parameters1.merged(null).apply {
            Assert.assertSame(parameters1, this)
        }
        null.merged(parameters1).apply {
            Assert.assertSame(parameters1, this)
        }
    }
}