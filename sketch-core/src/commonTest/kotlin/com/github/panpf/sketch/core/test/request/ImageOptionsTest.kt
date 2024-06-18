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
package com.github.panpf.sketch.core.test.request

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.Extras
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.isNotEmpty
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.LongImageStartCropScaleDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.FakeTransition
import com.github.panpf.sketch.test.utils.ScopeAction
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor2
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestDecoder2
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.TestTransition
import com.github.panpf.sketch.test.utils.UriInvalidCondition
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchSize
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImageOptionsTest {
// TODO

    @Test
    fun testFun() {
        ImageOptions().apply {
            assertNotNull(this)
            assertTrue(this.isEmpty())
        }
        ImageOptions {
            depth(LOCAL)
        }.apply {
            assertNotNull(this)
            assertFalse(this.isEmpty())
        }
    }

    @Test
    fun testIsEmpty() {
        ImageOptions().apply {
            assertTrue(this.isEmpty())
            assertFalse(this.isNotEmpty())
            assertNull(this.depthHolder)
            assertNull(this.extras)
            assertNull(this.httpHeaders)
            assertNull(this.downloadCachePolicy)
            assertNull(this.sizeResolver)
            assertNull(this.precisionDecider)
            assertNull(this.scaleDecider)
            assertNull(this.transformations)
            assertNull(this.resultCachePolicy)
            assertNull(this.placeholder)
            assertNull(this.fallback)
            assertNull(this.error)
            assertNull(this.transitionFactory)
            assertNull(this.disallowAnimatedImage)
            assertNull(this.resizeOnDraw)
            assertNull(this.memoryCachePolicy)
            assertNull(this.componentRegistry)
        }

        ImageOptions {
            depth(LOCAL)
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertEquals(LOCAL, this.depthHolder?.depth)
        }

        ImageOptions {
            setExtra("key", "value")
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertNotNull(this.extras)
        }

        ImageOptions {
            addHttpHeader("headerKey", "headerValue")
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertNotNull(this.httpHeaders)
        }

        ImageOptions {
            downloadCachePolicy(READ_ONLY)
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertNotNull(this.downloadCachePolicy)
        }

        ImageOptions {
            size(100, 100)
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertNotNull(this.sizeResolver)
        }

        ImageOptions {
            precision(EXACTLY)
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertNotNull(this.precisionDecider)
        }

        ImageOptions {
            scale(CENTER_CROP)
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertNotNull(this.scaleDecider)
        }

        ImageOptions {
            transformations(RoundedCornersTransformation())
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertNotNull(this.transformations)
        }

        ImageOptions {
            resultCachePolicy(ENABLED)
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertNotNull(this.resultCachePolicy)
        }

        ImageOptions {
            disallowAnimatedImage(false)
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertNotNull(this.disallowAnimatedImage)
        }

        ImageOptions {
            placeholder(FakeStateImage())
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertEquals(FakeStateImage(), this.placeholder)
        }

        ImageOptions {
            fallback(FakeStateImage())
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertEquals(FakeStateImage(), this.fallback)
        }

        ImageOptions {
            error(FakeStateImage())
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertEquals(ErrorStateImage(FakeStateImage()), this.error)
        }

        ImageOptions {
            transitionFactory(FakeTransition.Factory())
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertEquals(FakeTransition.Factory(), this.transitionFactory)
        }

        ImageOptions {
            resizeOnDraw(true)
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertTrue(this.resizeOnDraw ?: false)
        }

        ImageOptions {
            memoryCachePolicy(ENABLED)
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertNotNull(this.memoryCachePolicy)
        }

        ImageOptions {
            components {
                addFetcher(HttpUriFetcher.Factory())
            }
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertNotNull(this.componentRegistry)
        }
    }


    @Test
    fun testNewBuilder() {
        ImageOptions().apply {
            assertTrue(this.isEmpty())
        }

        ImageOptions().newBuilder().build().apply {
            assertTrue(this.isEmpty())
        }

        ImageOptions().newBuilder {
            depth(NETWORK)
        }.build().apply {
            assertFalse(this.isEmpty())
            assertNotNull(depthHolder)
        }

        ImageOptions().newBuilder {
            downloadCachePolicy(DISABLED)
        }.build().apply {
            assertFalse(this.isEmpty())
            assertNotNull(downloadCachePolicy)
        }
    }

    @Test
    fun testNewOptions() {
        ImageOptions().apply {
            assertTrue(this.isEmpty())
        }

        ImageOptions().newOptions().apply {
            assertTrue(this.isEmpty())
        }

        ImageOptions().newOptions {
            depth(NETWORK)
        }.apply {
            assertFalse(this.isEmpty())
            assertNotNull(depthHolder)
        }

        ImageOptions().newOptions {
            downloadCachePolicy(DISABLED)
        }.apply {
            assertFalse(this.isEmpty())
            assertNotNull(downloadCachePolicy)
        }

        val options = ImageOptions()
        assertEquals(options, options.newOptions())
        assertNotEquals(options, options.newOptions {
            downloadCachePolicy(DISABLED)
        })
        assertNotSame(options, options.newOptions())
    }

    @Test
    fun testMerged() {
        val options = ImageOptions()
        assertEquals(options, options.merged(ImageOptions()))
        assertNotEquals(options, options.merged(ImageOptions {
            depth(MEMORY)
        }))
        assertNotSame(options, options.merged(ImageOptions()))

        ImageOptions().apply {
            assertEquals(null, this.depthHolder)
        }.merged(ImageOptions {
            depth(LOCAL)
        }).apply {
            assertEquals(LOCAL, this.depthHolder?.depth)
        }.merged(ImageOptions {
            depth(NETWORK)
        }).apply {
            assertEquals(LOCAL, this.depthHolder?.depth)
        }

        ImageOptions().apply {
            assertEquals(null, this.extras)
        }.merged(ImageOptions {
            setExtra("key", "value")
        }).apply {
            assertEquals("value", this.extras?.get("key"))
        }.merged(ImageOptions {
            setExtra("key", "value1")
        }).apply {
            assertEquals("value", this.extras?.get("key"))
        }

        ImageOptions().apply {
            assertEquals(null, this.httpHeaders)
        }.merged(ImageOptions {
            addHttpHeader("addKey", "addValue")
            setHttpHeader("setKey", "setValue")
        }).apply {
            assertEquals(listOf("addValue"), this.httpHeaders?.getAdd("addKey"))
            assertEquals("setValue", this.httpHeaders?.getSet("setKey"))
        }.merged(ImageOptions {
            addHttpHeader("addKey", "addValue1")
            setHttpHeader("setKey", "setValue1")
        }).apply {
            assertEquals(listOf("addValue", "addValue1"), this.httpHeaders?.getAdd("addKey"))
            assertEquals("setValue", this.httpHeaders?.getSet("setKey"))
        }

        ImageOptions().apply {
            assertEquals(null, this.downloadCachePolicy)
        }.merged(ImageOptions {
            downloadCachePolicy(DISABLED)
        }).apply {
            assertEquals(DISABLED, this.downloadCachePolicy)
        }.merged(ImageOptions {
            downloadCachePolicy(READ_ONLY)
        }).apply {
            assertEquals(DISABLED, this.downloadCachePolicy)
        }

        ImageOptions().apply {
            assertEquals(null, this.sizeResolver)
        }.merged(ImageOptions {
            size(FixedSizeResolver(SketchSize(100, 100)))
        }).apply {
            assertEquals(FixedSizeResolver(SketchSize(100, 100)), this.sizeResolver)
        }.merged(ImageOptions {
            size(FixedSizeResolver(SketchSize(200, 200)))
        }).apply {
            assertEquals(FixedSizeResolver(SketchSize(100, 100)), this.sizeResolver)
        }

        ImageOptions().apply {
            assertEquals(null, this.precisionDecider)
        }.merged(ImageOptions {
            precision(EXACTLY)
        }).apply {
            assertEquals(FixedPrecisionDecider(EXACTLY), this.precisionDecider)
        }.merged(ImageOptions {
            precision(LESS_PIXELS)
        }).apply {
            assertEquals(FixedPrecisionDecider(EXACTLY), this.precisionDecider)
        }

        ImageOptions().apply {
            assertEquals(null, this.scaleDecider)
        }.merged(ImageOptions {
            scale(END_CROP)
        }).apply {
            assertEquals(FixedScaleDecider(END_CROP), this.scaleDecider)
        }.merged(ImageOptions {
            scale(FILL)
        }).apply {
            assertEquals(FixedScaleDecider(END_CROP), this.scaleDecider)
        }

        ImageOptions().apply {
            assertEquals(null, this.transformations)
        }.merged(ImageOptions {
            transformations(CircleCropTransformation(), RotateTransformation(40))
        }).apply {
            assertEquals(
                listOf(CircleCropTransformation(), RotateTransformation(40)),
                this.transformations
            )
        }.merged(ImageOptions {
            transformations(CircleCropTransformation(), RoundedCornersTransformation())
        }).apply {
            assertEquals(
                listOf(
                    CircleCropTransformation(),
                    RotateTransformation(40),
                    RoundedCornersTransformation()
                ),
                this.transformations
            )
        }

        ImageOptions().apply {
            assertEquals(null, this.resultCachePolicy)
        }.merged(ImageOptions {
            resultCachePolicy(DISABLED)
        }).apply {
            assertEquals(DISABLED, this.resultCachePolicy)
        }.merged(ImageOptions {
            resultCachePolicy(READ_ONLY)
        }).apply {
            assertEquals(DISABLED, this.resultCachePolicy)
        }

        ImageOptions().apply {
            assertEquals(null, this.disallowAnimatedImage)
        }.merged(ImageOptions {
            disallowAnimatedImage(true)
        }).apply {
            assertEquals(true, this.disallowAnimatedImage)
        }.merged(ImageOptions {
            disallowAnimatedImage(false)
        }).apply {
            assertEquals(true, this.disallowAnimatedImage)
        }

        ImageOptions().apply {
            assertEquals(null, this.placeholder)
        }.merged(ImageOptions {
            placeholder(FakeStateImage(FakeImage(SketchSize(100, 100))))
        }).apply {
            assertEquals(
                FakeStateImage(FakeImage(SketchSize(100, 100))),
                this.placeholder
            )
        }.merged(ImageOptions {
            placeholder(FakeStateImage(FakeImage(SketchSize(200, 200))))
        }).apply {
            assertEquals(
                FakeStateImage(FakeImage(SketchSize(100, 100))),
                this.placeholder
            )
        }

        ImageOptions().apply {
            assertEquals(null, this.fallback)
        }.merged(ImageOptions {
            fallback(FakeStateImage(FakeImage(SketchSize(100, 100))))
        }).apply {
            assertEquals(
                FakeStateImage(FakeImage(SketchSize(100, 100))),
                this.fallback
            )
        }.merged(ImageOptions {
            fallback(FakeStateImage(FakeImage(SketchSize(200, 200))))
        }).apply {
            assertEquals(
                FakeStateImage(FakeImage(SketchSize(100, 100))),
                this.fallback
            )
        }

        ImageOptions().apply {
            assertEquals(null, this.error)
        }.merged(ImageOptions {
            error(FakeStateImage(FakeImage(SketchSize(100, 100))))
        }).apply {
            assertEquals(
                ErrorStateImage(FakeStateImage(FakeImage(SketchSize(100, 100)))),
                this.error
            )
        }.merged(ImageOptions {
            error(FakeStateImage(FakeImage(SketchSize(200, 200))))
        }).apply {
            assertEquals(
                ErrorStateImage(FakeStateImage(FakeImage(SketchSize(100, 100)))),
                this.error
            )
        }

        ImageOptions().apply {
            assertEquals(null, this.transitionFactory)
        }.merged(ImageOptions {
            transitionFactory(FakeTransition.Factory())
        }).apply {
            assertEquals(FakeTransition.Factory(), this.transitionFactory)
        }.merged(ImageOptions {
            transitionFactory(TestTransition.Factory())
        }).apply {
            assertEquals(FakeTransition.Factory(), this.transitionFactory)
        }

        ImageOptions().apply {
            assertFalse(this.resizeOnDraw ?: false)
        }.merged(ImageOptions {
            resizeOnDraw(true)
        }).apply {
            assertTrue(this.resizeOnDraw ?: false)
        }.merged(ImageOptions {
            resizeOnDraw(false)
        }).apply {
            assertTrue(this.resizeOnDraw ?: false)
        }

        ImageOptions().apply {
            assertEquals(null, this.memoryCachePolicy)
        }.merged(ImageOptions {
            memoryCachePolicy(DISABLED)
        }).apply {
            assertEquals(DISABLED, this.memoryCachePolicy)
        }.merged(ImageOptions {
            memoryCachePolicy(READ_ONLY)
        }).apply {
            assertEquals(DISABLED, this.memoryCachePolicy)
        }

        ImageOptions().apply {
            assertNull(componentRegistry)
        }.merged(
            ImageOptions {
                components {
                    addFetcher(TestFetcher.Factory())
                    addDecoder(TestDecoder.Factory())
                    addRequestInterceptor(TestRequestInterceptor())
                    addDecodeInterceptor(TestDecodeInterceptor())
                }
            }
        ).apply {
            assertEquals(
                ComponentRegistry.Builder().apply {
                    addFetcher(TestFetcher.Factory())
                    addDecoder(TestDecoder.Factory())
                    addRequestInterceptor(TestRequestInterceptor())
                    addDecodeInterceptor(TestDecodeInterceptor())
                }.build(),
                componentRegistry
            )
        }.merged(ImageOptions {
            components {
                addFetcher(HttpUriFetcher.Factory())
                addDecoder(TestDecoder2.Factory())
                addRequestInterceptor(EngineRequestInterceptor())
                addDecodeInterceptor(TestDecodeInterceptor2())
            }
        }).apply {
            assertEquals(
                ComponentRegistry.Builder().apply {
                    addFetcher(TestFetcher.Factory())
                    addDecoder(TestDecoder.Factory())
                    addRequestInterceptor(TestRequestInterceptor())
                    addDecodeInterceptor(TestDecodeInterceptor())
                    addFetcher(HttpUriFetcher.Factory())
                    addDecoder(TestDecoder2.Factory())
                    addRequestInterceptor(EngineRequestInterceptor())
                    addDecodeInterceptor(TestDecodeInterceptor2())
                }.build(),
                componentRegistry
            )
        }
    }

    @Test
    fun testEqualsHashCodeToString() {
        val scopeActions = listOfNotNull<ScopeAction<ImageOptions.Builder>>(
            ScopeAction {
                depth(LOCAL)
            },
            ScopeAction {
                setExtra("key", "value")
            },
            ScopeAction {
                setHttpHeader("key1", "value1")
            },
            ScopeAction {
                downloadCachePolicy(WRITE_ONLY)
            },
            ScopeAction {
                size(100, 100)
            },
            ScopeAction {
                precision(SAME_ASPECT_RATIO)
            },
            ScopeAction {
                scale(FILL)
            },
            ScopeAction {
                transformations(RotateTransformation(40))
            },
            ScopeAction {
                resultCachePolicy(READ_ONLY)
            },
            ScopeAction {
                disallowAnimatedImage(true)
            },
            ScopeAction {
                placeholder(FakeStateImage(FakeImage(SketchSize(100, 100))))
            },
            ScopeAction {
                fallback(FakeStateImage(FakeImage(SketchSize(100, 100))))
            },
            ScopeAction {
                error(ErrorStateImage(FakeStateImage(FakeImage(SketchSize(100, 100)))))
            },
            ScopeAction {
                transitionFactory(FakeTransition.Factory())
            },
            ScopeAction {
                resizeOnDraw(true)
            },
            ScopeAction {
                memoryCachePolicy(ENABLED)
            },
            ScopeAction {
                components {
                    addFetcher(HttpUriFetcher.Factory())
                }
            },
        )

        val optionsList = mutableListOf<ImageOptions>()
        scopeActions.forEachIndexed { itemIndex, action ->
            val options = optionsList.lastOrNull() ?: ImageOptions()
            val builder = options.newBuilder()
            with(action) {
                builder.invoke()
            }
            val newOptions = builder.build()
            assertEquals(
                expected = newOptions, actual = newOptions.newOptions(),
                message = "itemIndex=$itemIndex, newOptions=$newOptions",
            )
            assertEquals(
                expected = newOptions.hashCode(),
                actual = newOptions.newOptions().hashCode(),
                message = "itemIndex=$itemIndex, newOptions=$newOptions",
            )
            optionsList.forEachIndexed { lastOptionsIndex, lastOptions ->
                assertNotEquals(
                    illegal = lastOptions,
                    actual = newOptions,
                    message = "itemIndex=$itemIndex, lastOptionsIndex=$lastOptionsIndex, lastOptions=$lastOptions, newOptions=$newOptions",
                )
                assertNotEquals(
                    illegal = lastOptions.hashCode(),
                    actual = newOptions.hashCode(),
                    message = "itemIndex=$itemIndex, lastOptionsIndex=$lastOptionsIndex, lastOptions=$lastOptions, newOptions=$newOptions",
                )
            }
            optionsList.add(newOptions)
        }
    }

    @Test
    fun testDepth() {
        ImageOptions().apply {
            assertNull(depthHolder?.depth)
            assertNull(depthHolder?.from)
        }

        ImageOptions {
            depth(null)
        }.apply {
            assertNull(depthHolder?.depth)
            assertNull(depthHolder?.from)
        }

        ImageOptions {
            depth(LOCAL)
        }.apply {
            assertEquals(LOCAL, depthHolder?.depth)
            assertNull(depthHolder?.from)
        }

        ImageOptions {
            depth(LOCAL, null)
        }.apply {
            assertEquals(LOCAL, depthHolder?.depth)
            assertNull(depthHolder?.from)
        }

        ImageOptions {
            depth(null, "TestDepthFrom")
        }.apply {
            assertNull(depthHolder?.depth)
            assertNull(depthHolder?.from)
        }

        ImageOptions {
            depth(LOCAL, "TestDepthFrom")
        }.apply {
            assertEquals(LOCAL, depthHolder?.depth)
            assertEquals("TestDepthFrom", depthHolder?.from)
        }
    }

    @Test
    fun testExtras() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(extras)
            }

            /* extras() */
            extras(Extras())
            build().apply {
                assertNull(extras)
            }

            extras(Extras.Builder().set("key1", "value1").build())
            build().apply {
                assertEquals(1, extras?.size)
                assertEquals("value1", extras?.get("key1"))
            }

            extras(null)
            build().apply {
                assertNull(extras)
            }

            /* setParameter(), removeParameter() */
            setExtra("key1", "value1")
            setExtra("key2", "value2", "value2")
            build().apply {
                assertEquals(2, extras?.size)
                assertEquals("value1", extras?.get("key1"))
                assertEquals("value2", extras?.get("key2"))
            }

            setExtra("key2", "value2.1", null)
            build().apply {
                assertEquals(2, extras?.size)
                assertEquals("value1", extras?.get("key1"))
                assertEquals("value2.1", extras?.get("key2"))
            }

            removeExtra("key2")
            build().apply {
                assertEquals(1, extras?.size)
                assertEquals("value1", extras?.get("key1"))
            }

            removeExtra("key1")
            build().apply {
                assertNull(extras)
            }
        }
    }

    @Test
    fun testHttpHeaders() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(httpHeaders)
            }

            /* httpHeaders() */
            httpHeaders(HttpHeaders())
            build().apply {
                assertNull(httpHeaders)
            }

            httpHeaders(HttpHeaders.Builder().set("key1", "value1").build())
            build().apply {
                assertEquals(1, httpHeaders?.size)
                assertEquals("value1", httpHeaders?.getSet("key1"))
            }

            httpHeaders(null)
            build().apply {
                assertNull(httpHeaders)
            }

            /* setHttpHeader(), addHttpHeader(), removeHttpHeader() */
            setHttpHeader("key1", "value1")
            setHttpHeader("key2", "value2")
            addHttpHeader("key3", "value3")
            addHttpHeader("key3", "value3.1")
            build().apply {
                assertEquals(4, httpHeaders?.size)
                assertEquals(2, httpHeaders?.setSize)
                assertEquals(2, httpHeaders?.addSize)
                assertEquals("value1", httpHeaders?.getSet("key1"))
                assertEquals("value2", httpHeaders?.getSet("key2"))
                assertEquals(listOf("value3", "value3.1"), httpHeaders?.getAdd("key3"))
            }

            setHttpHeader("key2", "value2.1")
            build().apply {
                assertEquals(4, httpHeaders?.size)
                assertEquals(2, httpHeaders?.setSize)
                assertEquals(2, httpHeaders?.addSize)
                assertEquals("value1", httpHeaders?.getSet("key1"))
                assertEquals("value2.1", httpHeaders?.getSet("key2"))
                assertEquals(listOf("value3", "value3.1"), httpHeaders?.getAdd("key3"))
            }

            removeHttpHeader("key3")
            build().apply {
                assertEquals(2, httpHeaders?.size)
                assertEquals("value1", httpHeaders?.getSet("key1"))
                assertEquals("value2.1", httpHeaders?.getSet("key2"))
            }

            removeHttpHeader("key2")
            build().apply {
                assertEquals(1, httpHeaders?.size)
                assertEquals("value1", httpHeaders?.getSet("key1"))
            }

            removeHttpHeader("key1")
            build().apply {
                assertNull(httpHeaders)
            }
        }
    }

    @Test
    fun testDownloadCachePolicy() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(downloadCachePolicy)
            }

            downloadCachePolicy(ENABLED)
            build().apply {
                assertEquals(ENABLED, downloadCachePolicy)
            }

            downloadCachePolicy(DISABLED)
            build().apply {
                assertEquals(DISABLED, downloadCachePolicy)
            }

            downloadCachePolicy(null)
            build().apply {
                assertNull(downloadCachePolicy)
            }
        }
    }

    @Test
    fun testResize() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(sizeResolver)
                assertNull(precisionDecider)
                assertNull(scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            build().apply {
                assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    precisionDecider
                )
                assertEquals(FixedScaleDecider(START_CROP), scaleDecider)
            }

            resize(100, 100)
            build().apply {
                assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                assertNull(precisionDecider)
                assertNull(scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(100, 100, EXACTLY)
            build().apply {
                assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                assertEquals(FixedPrecisionDecider(EXACTLY), precisionDecider)
                assertNull(scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(100, 100, scale = END_CROP)
            build().apply {
                assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                assertNull(precisionDecider)
                assertEquals(FixedScaleDecider(END_CROP), scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(null)
            build().apply {
                assertNull(sizeResolver)
                assertNull(precisionDecider)
                assertNull(scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            build().apply {
                assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    precisionDecider
                )
                assertEquals(FixedScaleDecider(START_CROP), scaleDecider)
            }

            resize(Size(100, 100))
            build().apply {
                assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                assertNull(precisionDecider)
                assertNull(scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(Size(100, 100), EXACTLY)
            build().apply {
                assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                assertEquals(FixedPrecisionDecider(EXACTLY), precisionDecider)
                assertNull(scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(Size(100, 100), scale = END_CROP)
            build().apply {
                assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                assertNull(precisionDecider)
                assertEquals(FixedScaleDecider(END_CROP), scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(null)
            build().apply {
                assertNull(sizeResolver)
                assertNull(precisionDecider)
                assertNull(scaleDecider)
            }
        }
    }

    @Test
    fun testResizeSize() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(sizeResolver)
            }

            size(Size(100, 100))
            build().apply {
                assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
            }

            size(200, 200)
            build().apply {
                assertEquals(FixedSizeResolver(Size(200, 200)), sizeResolver)
            }

            size(FixedSizeResolver(300, 200))
            build().apply {
                assertEquals(FixedSizeResolver(300, 200), sizeResolver)
            }

            size(null)
            build().apply {
                assertNull(sizeResolver)
            }
        }
    }

    @Test
    fun testResizeSizeResolver() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(sizeResolver)
            }

            size(FixedSizeResolver(SketchSize(100, 100)))
            build().apply {
                assertEquals(FixedSizeResolver(SketchSize(100, 100)), sizeResolver)
            }

            this.size(null)
            build().apply {
                assertNull(sizeResolver)
            }
        }
    }

    @Test
    fun testResizePrecision() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(precisionDecider)
            }

            precision(LongImageClipPrecisionDecider(EXACTLY))
            build().apply {
                assertEquals(LongImageClipPrecisionDecider(EXACTLY), precisionDecider)
            }

            precision(SAME_ASPECT_RATIO)
            build().apply {
                assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    precisionDecider
                )
            }

            precision(null)
            build().apply {
                assertNull(precisionDecider)
            }
        }
    }

    @Test
    fun testResizeScale() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(scaleDecider)
            }

            scale(LongImageStartCropScaleDecider(START_CROP, END_CROP))
            build().apply {
                assertEquals(
                    LongImageStartCropScaleDecider(START_CROP, END_CROP),
                    scaleDecider
                )
            }

            scale(FILL)
            build().apply {
                assertEquals(FixedScaleDecider(FILL), scaleDecider)
            }

            scale(null)
            build().apply {
                assertNull(scaleDecider)
            }
        }
    }

    @Test
    fun testTransformations() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(transformations)
            }

            /* transformations() */
            transformations(listOf(CircleCropTransformation()))
            build().apply {
                assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }

            transformations(RoundedCornersTransformation(), RotateTransformation(40))
            build().apply {
                assertEquals(
                    listOf(RoundedCornersTransformation(), RotateTransformation(40)),
                    transformations
                )
            }

            transformations(null)
            build().apply {
                assertNull(transformations)
            }

            /* addTransformations(List), removeTransformations(List) */
            addTransformations(listOf(CircleCropTransformation()))
            build().apply {
                assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            addTransformations(listOf(CircleCropTransformation(), RotateTransformation(40)))
            build().apply {
                assertEquals(
                    listOf(CircleCropTransformation(), RotateTransformation(40)),
                    transformations
                )
            }
            removeTransformations(listOf(RotateTransformation(40)))
            build().apply {
                assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            removeTransformations(listOf(CircleCropTransformation()))
            build().apply {
                assertNull(transformations)
            }

            /* addTransformations(vararg), removeTransformations(vararg) */
            addTransformations(CircleCropTransformation())
            build().apply {
                assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            addTransformations(CircleCropTransformation(), RotateTransformation(40))
            build().apply {
                assertEquals(
                    listOf(CircleCropTransformation(), RotateTransformation(40)),
                    transformations
                )
            }
            removeTransformations(RotateTransformation(40))
            build().apply {
                assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            removeTransformations(CircleCropTransformation())
            build().apply {
                assertNull(transformations)
            }
        }
    }

    @Test
    fun testResultCachePolicy() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(resultCachePolicy)
            }

            resultCachePolicy(ENABLED)
            build().apply {
                assertEquals(ENABLED, resultCachePolicy)
            }

            resultCachePolicy(DISABLED)
            build().apply {
                assertEquals(DISABLED, resultCachePolicy)
            }

            resultCachePolicy(null)
            build().apply {
                assertNull(resultCachePolicy)
            }
        }
    }

    @Test
    fun testDisallowAnimatedImage() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(disallowAnimatedImage)
            }

            disallowAnimatedImage()
            build().apply {
                assertEquals(true, disallowAnimatedImage)
            }

            disallowAnimatedImage(false)
            build().apply {
                assertEquals(false, disallowAnimatedImage)
            }

            disallowAnimatedImage(null)
            build().apply {
                assertNull(disallowAnimatedImage)
            }
        }
    }

    @Test
    fun testPlaceholder() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(placeholder)
            }

            placeholder(FakeStateImage(FakeImage(SketchSize(100, 100))))
            build().apply {
                assertEquals(FakeStateImage(FakeImage(SketchSize(100, 100))), placeholder)
            }

            placeholder(FakeStateImage(FakeImage(SketchSize(200, 200))))
            build().apply {
                assertEquals(FakeStateImage(FakeImage(SketchSize(200, 200))), placeholder)
            }

            placeholder(null)
            build().apply {
                assertNull(placeholder)
            }
        }
    }

    @Test
    fun testFallback() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(fallback)
            }

            fallback(FakeStateImage(FakeImage(SketchSize(100, 100))))
            build().apply {
                assertEquals(FakeStateImage(FakeImage(SketchSize(100, 100))), fallback)
            }

            fallback(FakeStateImage(FakeImage(SketchSize(200, 200))))
            build().apply {
                assertEquals(FakeStateImage(FakeImage(SketchSize(200, 200))), fallback)
            }

            fallback(null)
            build().apply {
                assertNull(fallback)
            }
        }
    }

    @Test
    fun testError() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(error)
            }

            error(FakeStateImage(FakeImage(SketchSize(100, 100))))
            build().apply {
                assertEquals(
                    ErrorStateImage(FakeStateImage(FakeImage(SketchSize(100, 100)))),
                    error
                )
            }

            error(FakeStateImage(FakeImage(SketchSize(200, 200))))
            build().apply {
                assertEquals(
                    ErrorStateImage(FakeStateImage(FakeImage(SketchSize(200, 200)))),
                    error
                )
            }

            error(FakeStateImage(FakeImage(SketchSize(200, 200)))) {
                addState(UriInvalidCondition, FakeStateImage(FakeImage(SketchSize(300, 300))))
            }
            build().apply {
                assertEquals(
                    ErrorStateImage(FakeStateImage(FakeImage(SketchSize(200, 200)))) {
                        addState(UriInvalidCondition, FakeStateImage(FakeImage(SketchSize(300, 300))))
                    },
                    error
                )
            }

            error()
            build().apply {
                assertNull(error)
            }
        }
    }

    @Test
    fun testTransition() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(transitionFactory)
            }

            transitionFactory(FakeTransition.Factory())
            build().apply {
                assertEquals(FakeTransition.Factory(), transitionFactory)
            }

            transitionFactory(TestTransition.Factory())
            build().apply {
                assertEquals(TestTransition.Factory(), transitionFactory)
            }

            transitionFactory(null)
            build().apply {
                assertNull(transitionFactory)
            }
        }
    }

    @Test
    fun testResizeOnDraw() {
        ImageOptions.Builder().apply {
            build().apply {
                assertFalse(resizeOnDraw ?: false)
            }

            resizeOnDraw()
            build().apply {
                assertTrue(resizeOnDraw ?: false)
            }

            resizeOnDraw(false)
            build().apply {
                assertFalse(resizeOnDraw ?: false)
            }

            resizeOnDraw(null)
            build().apply {
                assertFalse(resizeOnDraw ?: false)
            }
        }
    }

    @Test
    fun testAllowNullImage() = runTest {
        ImageOptions().apply {
            assertNull(allowNullImage)
        }
        ImageOptions {
            allowNullImage()
        }.apply {
            assertTrue(allowNullImage!!)
        }
        ImageOptions {
            allowNullImage(true)
        }.apply {
            assertTrue(allowNullImage!!)
        }
        ImageOptions {
            allowNullImage(false)
        }.apply {
            assertFalse(allowNullImage!!)
        }
    }

    @Test
    fun testMemoryCachePolicy() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(memoryCachePolicy)
            }

            memoryCachePolicy(ENABLED)
            build().apply {
                assertEquals(ENABLED, memoryCachePolicy)
            }

            memoryCachePolicy(DISABLED)
            build().apply {
                assertEquals(DISABLED, memoryCachePolicy)
            }

            memoryCachePolicy(null)
            build().apply {
                assertNull(memoryCachePolicy)
            }
        }
    }

    @Test
    fun testComponentRegistry() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(componentRegistry)
            }

            components {
                addFetcher(HttpUriFetcher.Factory())
            }
            val options1 = build().apply {
                assertEquals(ComponentRegistry.Builder().apply {
                    addFetcher(HttpUriFetcher.Factory())
                }.build(), componentRegistry)
            }

            components {
                addDecoder(TestDecoder.Factory())
            }
            val options2 = build().apply {
                assertEquals(ComponentRegistry.Builder().apply {
                    addDecoder(TestDecoder.Factory())
                }.build(), componentRegistry)
            }

            assertNotEquals(options1, options2)

            components(null)
            build().apply {
                assertNull(componentRegistry)
            }
        }
    }

    // TODO test mergeComponents
    // TODO test sizeMultiplier
    // TODO test crossfade
}