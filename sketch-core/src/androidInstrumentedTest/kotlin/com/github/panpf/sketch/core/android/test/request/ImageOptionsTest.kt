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
package com.github.panpf.sketch.core.android.test.request

import android.graphics.Bitmap.Config.ALPHA_8
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.ACES
import android.graphics.ColorSpace.Named.BT709
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.Parameters
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.error
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.isNotEmpty
import com.github.panpf.sketch.request.placeholder
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.request.uriEmpty
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
import com.github.panpf.sketch.resize.internal.DisplaySizeResolver
import com.github.panpf.sketch.resize.internal.ViewSizeResolver
import com.github.panpf.sketch.state.ColorStateImage
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.uriEmptyError
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor2
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.TestTransition
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.ColorDrawableEqualizer
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageOptionsTest {

    @Test
    fun testFun() {
        ImageOptions().apply {
            Assert.assertNotNull(this)
            Assert.assertTrue(this.isEmpty())
        }
        ImageOptions {
            depth(LOCAL)
        }.apply {
            Assert.assertNotNull(this)
            Assert.assertFalse(this.isEmpty())
        }
    }

    @Test
    fun testIsEmpty() {
        ImageOptions().apply {
            Assert.assertTrue(this.isEmpty())
            Assert.assertFalse(this.isNotEmpty())
            Assert.assertNull(this.depth)
            Assert.assertNull(this.parameters)
            Assert.assertNull(this.httpHeaders)
            Assert.assertNull(this.downloadCachePolicy)
            Assert.assertNull(this.bitmapConfig)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                Assert.assertNull(this.colorSpace)
            }
            @Suppress("DEPRECATION")
            Assert.assertNull(this.preferQualityOverSpeed)
            Assert.assertNull(this.sizeResolver)
            Assert.assertNull(this.precisionDecider)
            Assert.assertNull(this.scaleDecider)
            Assert.assertNull(this.transformations)
            Assert.assertNull(this.resultCachePolicy)
            Assert.assertNull(this.placeholder)
            Assert.assertNull(this.uriEmpty)
            Assert.assertNull(this.error)
            Assert.assertNull(this.transitionFactory)
            Assert.assertNull(this.disallowAnimatedImage)
            Assert.assertNull(this.resizeOnDrawHelper)
            Assert.assertNull(this.memoryCachePolicy)
            Assert.assertNull(this.componentRegistry)
        }

        ImageOptions {
            depth(LOCAL)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertEquals(LOCAL, this.depth)
        }

        ImageOptions {
            setParameter("key", "value")
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.parameters)
        }

        ImageOptions {
            addHttpHeader("headerKey", "headerValue")
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.httpHeaders)
        }

        ImageOptions {
            downloadCachePolicy(READ_ONLY)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.downloadCachePolicy)
        }

        ImageOptions {
            bitmapConfig(ALPHA_8)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.bitmapConfig)
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            ImageOptions {
                colorSpace(BT709)
            }.apply {
                Assert.assertFalse(this.isEmpty())
                Assert.assertTrue(this.isNotEmpty())
                Assert.assertNotNull(this.colorSpace)
            }
        }

        ImageOptions {
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(true)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            @Suppress("DEPRECATION")
            Assert.assertNotNull(this.preferQualityOverSpeed)
        }

        ImageOptions {
            size(100, 100)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.sizeResolver)
        }

        ImageOptions {
            precision(EXACTLY)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.precisionDecider)
        }

        ImageOptions {
            scale(CENTER_CROP)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.scaleDecider)
        }

        ImageOptions {
            transformations(RoundedCornersTransformation())
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.transformations)
        }

        ImageOptions {
            resultCachePolicy(ENABLED)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.resultCachePolicy)
        }

        ImageOptions {
            disallowAnimatedImage(false)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.disallowAnimatedImage)
        }

        ImageOptions {
            placeholder(ColorDrawableEqualizer(Color.BLUE))
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.placeholder)
        }

        ImageOptions {
            uriEmpty(ColorDrawableEqualizer(Color.BLUE))
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.uriEmpty)
        }

        ImageOptions {
            error(ColorDrawableEqualizer(Color.BLUE))
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.error)
        }

        ImageOptions {
            transitionFactory(CrossfadeTransition.Factory())
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.transitionFactory)
        }

        ImageOptions {
            resizeOnDraw(true)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.resizeOnDrawHelper)
        }

        ImageOptions {
            memoryCachePolicy(ENABLED)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.memoryCachePolicy)
        }

        ImageOptions {
            components {
                addFetcher(HttpUriFetcher.Factory())
            }
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertTrue(this.isNotEmpty())
            Assert.assertNotNull(this.componentRegistry)
        }
    }


    @Test
    fun testNewBuilder() {
        ImageOptions().apply {
            Assert.assertTrue(this.isEmpty())
        }

        ImageOptions().newBuilder().build().apply {
            Assert.assertTrue(this.isEmpty())
        }

        ImageOptions().newBuilder {
            depth(NETWORK)
        }.build().apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(depth)
        }

        ImageOptions().newBuilder {
            downloadCachePolicy(DISABLED)
        }.build().apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(downloadCachePolicy)
        }
    }

    @Test
    fun testNewOptions() {
        ImageOptions().apply {
            Assert.assertTrue(this.isEmpty())
        }

        ImageOptions().newOptions().apply {
            Assert.assertTrue(this.isEmpty())
        }

        ImageOptions().newOptions {
            depth(NETWORK)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(depth)
        }

        ImageOptions().newOptions {
            downloadCachePolicy(DISABLED)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(downloadCachePolicy)
        }

        val options = ImageOptions()
        Assert.assertTrue(options == options.newOptions())
        Assert.assertFalse(options == options.newOptions {
            downloadCachePolicy(DISABLED)
        })
        Assert.assertFalse(options === options.newOptions())
    }

    @Test
    fun testMerged() {
        val context = getTestContext()

        val options = ImageOptions()
        Assert.assertTrue(options == options.merged(ImageOptions()))
        Assert.assertTrue(options != options.merged(ImageOptions {
            depth(MEMORY)
        }))
        Assert.assertFalse(options === options.merged(ImageOptions()))

        ImageOptions().apply {
            Assert.assertEquals(null, this.depth)
        }.merged(ImageOptions {
            depth(LOCAL)
        }).apply {
            Assert.assertEquals(LOCAL, this.depth)
        }.merged(ImageOptions {
            depth(NETWORK)
        }).apply {
            Assert.assertEquals(LOCAL, this.depth)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.parameters)
        }.merged(ImageOptions {
            setParameter("key", "value")
        }).apply {
            Assert.assertEquals("value", this.parameters?.get("key"))
        }.merged(ImageOptions {
            setParameter("key", "value1")
        }).apply {
            Assert.assertEquals("value", this.parameters?.get("key"))
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.httpHeaders)
        }.merged(ImageOptions {
            addHttpHeader("addKey", "addValue")
            setHttpHeader("setKey", "setValue")
        }).apply {
            Assert.assertEquals(listOf("addValue"), this.httpHeaders?.getAdd("addKey"))
            Assert.assertEquals("setValue", this.httpHeaders?.getSet("setKey"))
        }.merged(ImageOptions {
            addHttpHeader("addKey", "addValue1")
            setHttpHeader("setKey", "setValue1")
        }).apply {
            Assert.assertEquals(listOf("addValue", "addValue1"), this.httpHeaders?.getAdd("addKey"))
            Assert.assertEquals("setValue", this.httpHeaders?.getSet("setKey"))
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.downloadCachePolicy)
        }.merged(ImageOptions {
            downloadCachePolicy(DISABLED)
        }).apply {
            Assert.assertEquals(DISABLED, this.downloadCachePolicy)
        }.merged(ImageOptions {
            downloadCachePolicy(READ_ONLY)
        }).apply {
            Assert.assertEquals(DISABLED, this.downloadCachePolicy)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.bitmapConfig)
        }.merged(ImageOptions {
            bitmapConfig(ARGB_8888)
        }).apply {
            Assert.assertEquals(BitmapConfig(ARGB_8888), this.bitmapConfig)
        }.merged(ImageOptions {
            bitmapConfig(RGB_565)
        }).apply {
            Assert.assertEquals(BitmapConfig(ARGB_8888), this.bitmapConfig)
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            ImageOptions().apply {
                Assert.assertEquals(null, this.colorSpace)
            }.merged(ImageOptions {
                colorSpace(BT709)
            }).apply {
                Assert.assertEquals(ColorSpace.get(BT709), this.colorSpace)
            }.merged(ImageOptions {
                colorSpace(ACES)
            }).apply {
                Assert.assertEquals(ColorSpace.get(BT709), this.colorSpace)
            }
        }

        @Suppress("DEPRECATION")
        ImageOptions().apply {
            Assert.assertEquals(null, this.preferQualityOverSpeed)
        }.merged(ImageOptions {
            preferQualityOverSpeed(true)
        }).apply {
            Assert.assertEquals(true, this.preferQualityOverSpeed)
        }.merged(ImageOptions {
            preferQualityOverSpeed(false)
        }).apply {
            Assert.assertEquals(true, this.preferQualityOverSpeed)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.sizeResolver)
        }.merged(ImageOptions {
            size(DisplaySizeResolver(context))
        }).apply {
            Assert.assertEquals(DisplaySizeResolver(context), this.sizeResolver)
        }.merged(ImageOptions {
            size(ViewSizeResolver(TextView(context)))
        }).apply {
            Assert.assertEquals(DisplaySizeResolver(context), this.sizeResolver)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.precisionDecider)
        }.merged(ImageOptions {
            precision(EXACTLY)
        }).apply {
            Assert.assertEquals(FixedPrecisionDecider(EXACTLY), this.precisionDecider)
        }.merged(ImageOptions {
            precision(LESS_PIXELS)
        }).apply {
            Assert.assertEquals(FixedPrecisionDecider(EXACTLY), this.precisionDecider)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.scaleDecider)
        }.merged(ImageOptions {
            scale(END_CROP)
        }).apply {
            Assert.assertEquals(FixedScaleDecider(END_CROP), this.scaleDecider)
        }.merged(ImageOptions {
            scale(FILL)
        }).apply {
            Assert.assertEquals(FixedScaleDecider(END_CROP), this.scaleDecider)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.transformations)
        }.merged(ImageOptions {
            transformations(CircleCropTransformation(), RotateTransformation(40))
        }).apply {
            Assert.assertEquals(
                listOf(CircleCropTransformation(), RotateTransformation(40)),
                this.transformations
            )
        }.merged(ImageOptions {
            transformations(CircleCropTransformation(), RoundedCornersTransformation())
        }).apply {
            Assert.assertEquals(
                listOf(
                    CircleCropTransformation(),
                    RotateTransformation(40),
                    RoundedCornersTransformation()
                ),
                this.transformations
            )
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.resultCachePolicy)
        }.merged(ImageOptions {
            resultCachePolicy(DISABLED)
        }).apply {
            Assert.assertEquals(DISABLED, this.resultCachePolicy)
        }.merged(ImageOptions {
            resultCachePolicy(READ_ONLY)
        }).apply {
            Assert.assertEquals(DISABLED, this.resultCachePolicy)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.disallowAnimatedImage)
        }.merged(ImageOptions {
            disallowAnimatedImage(true)
        }).apply {
            Assert.assertEquals(true, this.disallowAnimatedImage)
        }.merged(ImageOptions {
            disallowAnimatedImage(false)
        }).apply {
            Assert.assertEquals(true, this.disallowAnimatedImage)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.placeholder)
        }.merged(ImageOptions {
            placeholder(android.R.drawable.bottom_bar)
        }).apply {
            Assert.assertEquals(
                DrawableStateImage(android.R.drawable.bottom_bar),
                this.placeholder
            )
        }.merged(ImageOptions {
            placeholder(android.R.drawable.arrow_up_float)
        }).apply {
            Assert.assertEquals(
                DrawableStateImage(android.R.drawable.bottom_bar),
                this.placeholder
            )
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.uriEmpty)
        }.merged(ImageOptions {
            uriEmpty(android.R.drawable.bottom_bar)
        }).apply {
            Assert.assertEquals(
                DrawableStateImage(android.R.drawable.bottom_bar),
                this.uriEmpty
            )
        }.merged(ImageOptions {
            uriEmpty(android.R.drawable.arrow_up_float)
        }).apply {
            Assert.assertEquals(
                DrawableStateImage(android.R.drawable.bottom_bar),
                this.uriEmpty
            )
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.error)
        }.merged(ImageOptions {
            error(android.R.drawable.bottom_bar)
        }).apply {
            Assert.assertEquals(
                ErrorStateImage(DrawableStateImage(android.R.drawable.bottom_bar)),
                this.error
            )
        }.merged(ImageOptions {
            error(android.R.drawable.arrow_up_float)
        }).apply {
            Assert.assertEquals(
                ErrorStateImage(DrawableStateImage(android.R.drawable.bottom_bar)),
                this.error
            )
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.transitionFactory)
        }.merged(ImageOptions {
            transitionFactory(CrossfadeTransition.Factory())
        }).apply {
            Assert.assertEquals(CrossfadeTransition.Factory(), this.transitionFactory)
        }.merged(ImageOptions {
            transitionFactory(TestTransition.Factory())
        }).apply {
            Assert.assertEquals(CrossfadeTransition.Factory(), this.transitionFactory)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.resizeOnDrawHelper)
        }.merged(ImageOptions {
            resizeOnDraw(true)
        }).apply {
            Assert.assertEquals(true, this.resizeOnDrawHelper)
        }.merged(ImageOptions {
            resizeOnDraw(false)
        }).apply {
            Assert.assertEquals(true, this.resizeOnDrawHelper)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.memoryCachePolicy)
        }.merged(ImageOptions {
            memoryCachePolicy(DISABLED)
        }).apply {
            Assert.assertEquals(DISABLED, this.memoryCachePolicy)
        }.merged(ImageOptions {
            memoryCachePolicy(READ_ONLY)
        }).apply {
            Assert.assertEquals(DISABLED, this.memoryCachePolicy)
        }

        ImageOptions().apply {
            Assert.assertNull(componentRegistry)
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
            Assert.assertEquals(
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
                addDecoder(BitmapFactoryDecoder.Factory())
                addRequestInterceptor(EngineRequestInterceptor())
                addDecodeInterceptor(TestDecodeInterceptor2())
            }
        }).apply {
            Assert.assertEquals(
                ComponentRegistry.Builder().apply {
                    addFetcher(TestFetcher.Factory())
                    addDecoder(TestDecoder.Factory())
                    addRequestInterceptor(TestRequestInterceptor())
                    addDecodeInterceptor(TestDecodeInterceptor())
                    addFetcher(HttpUriFetcher.Factory())
                    addDecoder(BitmapFactoryDecoder.Factory())
                    addRequestInterceptor(EngineRequestInterceptor())
                    addDecodeInterceptor(TestDecodeInterceptor2())
                }.build(),
                componentRegistry
            )
        }
    }

    @Test
    fun testEqualsHashCodeToString() {
        val optionsList = buildList {
            ImageOptions()
                .apply { add(this) }.newOptions {
                    depth(LOCAL)
                }.apply { add(this) }.newOptions {
                    setParameter("key", "value")
                }.apply { add(this) }.newOptions {
                    setHttpHeader("key1", "value1")
                }.apply { add(this) }.newOptions {
                    downloadCachePolicy(WRITE_ONLY)
                }.apply { add(this) }.newOptions {
                    bitmapConfig(RGB_565)
                }.apply { add(this) }.let { options ->
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        options.newOptions {
                            colorSpace(ACES)
                        }.apply { add(this) }
                    } else {
                        options
                    }
                }.newOptions {
                    @Suppress("DEPRECATION")
                    preferQualityOverSpeed(true)
                }.apply { add(this) }.newOptions {
                    size(100, 100)
                }.apply { add(this) }.newOptions {
                    precision(SAME_ASPECT_RATIO)
                }.apply { add(this) }.newOptions {
                    scale(FILL)
                }.apply { add(this) }.newOptions {
                    transformations(RotateTransformation(40))
                }.apply { add(this) }.newOptions {
                    resultCachePolicy(READ_ONLY)
                }.apply { add(this) }.newOptions {
                    disallowAnimatedImage(true)
                }.apply { add(this) }.newOptions {
                    placeholder(android.R.drawable.bottom_bar)
                }.apply { add(this) }.newOptions {
                    error(android.R.drawable.btn_dialog)
                }.apply { add(this) }.newOptions {
                    transitionFactory(CrossfadeTransition.Factory())
                }.apply { add(this) }.newOptions {
                    resizeOnDraw(true)
                }.apply { add(this) }.newOptions {
                    memoryCachePolicy(ENABLED)
                }.apply { add(this) }.newOptions {
                    components {
                        addFetcher(HttpUriFetcher.Factory())
                    }
                }.apply { add(this) }
        }

        optionsList.forEachIndexed { index, imageOptions ->
            optionsList.forEachIndexed { index1, imageOptions1 ->
                if (index != index1) {
                    Assert.assertNotEquals(imageOptions, imageOptions1)
                    Assert.assertNotEquals(imageOptions.hashCode(), imageOptions1.hashCode())
                    Assert.assertNotEquals(imageOptions.toString(), imageOptions1.toString())
                }
            }
        }

        val optionsList2 = optionsList.map { it.newOptions() }
        optionsList.forEachIndexed { index, imageOptions ->
            Assert.assertEquals(imageOptions, optionsList2[index])
            Assert.assertEquals(imageOptions.hashCode(), optionsList2[index].hashCode())
            Assert.assertEquals(imageOptions.toString(), optionsList2[index].toString())
        }

        Assert.assertEquals(optionsList[0], optionsList[0])
        Assert.assertNotEquals(optionsList[0], Any())
        Assert.assertNotEquals(optionsList[0], null)
    }

    @Test
    fun testDepth() {
        ImageOptions().apply {
            Assert.assertNull(depth)
            Assert.assertNull(depthFrom)
        }

        ImageOptions {
            depth(null)
        }.apply {
            Assert.assertNull(depth)
            Assert.assertNull(depthFrom)
        }

        ImageOptions {
            depth(LOCAL)
        }.apply {
            Assert.assertEquals(LOCAL, depth)
            Assert.assertNull(depthFrom)
        }

        ImageOptions {
            depth(LOCAL, null)
        }.apply {
            Assert.assertEquals(LOCAL, depth)
            Assert.assertNull(depthFrom)
        }

        ImageOptions {
            depth(null, "TestDepthFrom")
        }.apply {
            Assert.assertNull(depth)
            Assert.assertNull(depthFrom)
        }

        ImageOptions {
            depth(LOCAL, "TestDepthFrom")
        }.apply {
            Assert.assertEquals(LOCAL, depth)
            Assert.assertEquals("TestDepthFrom", depthFrom)
        }
    }

    @Test
    fun testParameters() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(parameters)
            }

            /* parameters() */
            parameters(Parameters())
            build().apply {
                Assert.assertNull(parameters)
            }

            parameters(Parameters.Builder().set("key1", "value1").build())
            build().apply {
                Assert.assertEquals(1, parameters?.size)
                Assert.assertEquals("value1", parameters?.get("key1"))
            }

            parameters(null)
            build().apply {
                Assert.assertNull(parameters)
            }

            /* setParameter(), removeParameter() */
            setParameter("key1", "value1")
            setParameter("key2", "value2", "value2")
            build().apply {
                Assert.assertEquals(2, parameters?.size)
                Assert.assertEquals("value1", parameters?.get("key1"))
                Assert.assertEquals("value2", parameters?.get("key2"))
            }

            setParameter("key2", "value2.1", null)
            build().apply {
                Assert.assertEquals(2, parameters?.size)
                Assert.assertEquals("value1", parameters?.get("key1"))
                Assert.assertEquals("value2.1", parameters?.get("key2"))
            }

            removeParameter("key2")
            build().apply {
                Assert.assertEquals(1, parameters?.size)
                Assert.assertEquals("value1", parameters?.get("key1"))
            }

            removeParameter("key1")
            build().apply {
                Assert.assertNull(parameters)
            }
        }
    }

    @Test
    fun testHttpHeaders() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(httpHeaders)
            }

            /* httpHeaders() */
            httpHeaders(HttpHeaders())
            build().apply {
                Assert.assertNull(httpHeaders)
            }

            httpHeaders(HttpHeaders.Builder().set("key1", "value1").build())
            build().apply {
                Assert.assertEquals(1, httpHeaders?.size)
                Assert.assertEquals("value1", httpHeaders?.getSet("key1"))
            }

            httpHeaders(null)
            build().apply {
                Assert.assertNull(httpHeaders)
            }

            /* setHttpHeader(), addHttpHeader(), removeHttpHeader() */
            setHttpHeader("key1", "value1")
            setHttpHeader("key2", "value2")
            addHttpHeader("key3", "value3")
            addHttpHeader("key3", "value3.1")
            build().apply {
                Assert.assertEquals(4, httpHeaders?.size)
                Assert.assertEquals(2, httpHeaders?.setSize)
                Assert.assertEquals(2, httpHeaders?.addSize)
                Assert.assertEquals("value1", httpHeaders?.getSet("key1"))
                Assert.assertEquals("value2", httpHeaders?.getSet("key2"))
                Assert.assertEquals(listOf("value3", "value3.1"), httpHeaders?.getAdd("key3"))
            }

            setHttpHeader("key2", "value2.1")
            build().apply {
                Assert.assertEquals(4, httpHeaders?.size)
                Assert.assertEquals(2, httpHeaders?.setSize)
                Assert.assertEquals(2, httpHeaders?.addSize)
                Assert.assertEquals("value1", httpHeaders?.getSet("key1"))
                Assert.assertEquals("value2.1", httpHeaders?.getSet("key2"))
                Assert.assertEquals(listOf("value3", "value3.1"), httpHeaders?.getAdd("key3"))
            }

            removeHttpHeader("key3")
            build().apply {
                Assert.assertEquals(2, httpHeaders?.size)
                Assert.assertEquals("value1", httpHeaders?.getSet("key1"))
                Assert.assertEquals("value2.1", httpHeaders?.getSet("key2"))
            }

            removeHttpHeader("key2")
            build().apply {
                Assert.assertEquals(1, httpHeaders?.size)
                Assert.assertEquals("value1", httpHeaders?.getSet("key1"))
            }

            removeHttpHeader("key1")
            build().apply {
                Assert.assertNull(httpHeaders)
            }
        }
    }

    @Test
    fun testDownloadCachePolicy() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(downloadCachePolicy)
            }

            downloadCachePolicy(ENABLED)
            build().apply {
                Assert.assertEquals(ENABLED, downloadCachePolicy)
            }

            downloadCachePolicy(DISABLED)
            build().apply {
                Assert.assertEquals(DISABLED, downloadCachePolicy)
            }

            downloadCachePolicy(null)
            build().apply {
                Assert.assertNull(downloadCachePolicy)
            }
        }
    }

    @Test
    fun testBitmapConfig() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(bitmapConfig)
            }

            bitmapConfig(BitmapConfig(RGB_565))
            build().apply {
                Assert.assertEquals(BitmapConfig(RGB_565), bitmapConfig)
            }

            bitmapConfig(ARGB_8888)
            build().apply {
                Assert.assertEquals(BitmapConfig(ARGB_8888), bitmapConfig)
            }

            bitmapConfig(BitmapConfig.LowQuality)
            build().apply {
                Assert.assertEquals(BitmapConfig.LowQuality, bitmapConfig)
            }

            bitmapConfig(BitmapConfig.HighQuality)
            build().apply {
                Assert.assertEquals(BitmapConfig.HighQuality, bitmapConfig)
            }

            bitmapConfig(null)
            build().apply {
                Assert.assertNull(bitmapConfig)
            }
        }
    }

    @Test
    fun testColorSpace() {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(colorSpace)
            }

            colorSpace(ACES)
            build().apply {
                Assert.assertEquals(ColorSpace.get(ACES), colorSpace)
            }

            colorSpace(BT709)
            build().apply {
                Assert.assertEquals(ColorSpace.get(BT709), colorSpace)
            }

            colorSpace(null)
            build().apply {
                Assert.assertNull(colorSpace)
            }
        }
    }

    @Test
    @Suppress("DEPRECATION")
    fun testPreferQualityOverSpeed() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(preferQualityOverSpeed)
            }

            preferQualityOverSpeed()
            build().apply {
                Assert.assertEquals(true, preferQualityOverSpeed)
            }

            preferQualityOverSpeed(false)
            build().apply {
                Assert.assertEquals(false, preferQualityOverSpeed)
            }

            preferQualityOverSpeed(null)
            build().apply {
                Assert.assertNull(preferQualityOverSpeed)
            }
        }
    }

    @Test
    fun testResize() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(sizeResolver)
                Assert.assertNull(precisionDecider)
                Assert.assertNull(scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                Assert.assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    precisionDecider
                )
                Assert.assertEquals(FixedScaleDecider(START_CROP), scaleDecider)
            }

            resize(100, 100)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                Assert.assertNull(precisionDecider)
                Assert.assertNull(scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(100, 100, EXACTLY)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), precisionDecider)
                Assert.assertNull(scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(100, 100, scale = END_CROP)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                Assert.assertNull(precisionDecider)
                Assert.assertEquals(FixedScaleDecider(END_CROP), scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(null)
            build().apply {
                Assert.assertNull(sizeResolver)
                Assert.assertNull(precisionDecider)
                Assert.assertNull(scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                Assert.assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    precisionDecider
                )
                Assert.assertEquals(FixedScaleDecider(START_CROP), scaleDecider)
            }

            resize(Size(100, 100))
            build().apply {
                Assert.assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                Assert.assertNull(precisionDecider)
                Assert.assertNull(scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(Size(100, 100), EXACTLY)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), precisionDecider)
                Assert.assertNull(scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(Size(100, 100), scale = END_CROP)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                Assert.assertNull(precisionDecider)
                Assert.assertEquals(FixedScaleDecider(END_CROP), scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(null)
            build().apply {
                Assert.assertNull(sizeResolver)
                Assert.assertNull(precisionDecider)
                Assert.assertNull(scaleDecider)
            }
        }
    }

    @Test
    fun testResizeSize() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(sizeResolver)
            }

            size(Size(100, 100))
            build().apply {
                Assert.assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
            }

            size(200, 200)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(Size(200, 200)), sizeResolver)
            }

            size(FixedSizeResolver(300, 200))
            build().apply {
                Assert.assertEquals(FixedSizeResolver(300, 200), sizeResolver)
            }

            size(null)
            build().apply {
                Assert.assertNull(sizeResolver)
            }
        }
    }

    @Test
    fun testResizeSizeResolver() {
        val context = getTestContext()

        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(sizeResolver)
            }

            size(DisplaySizeResolver(context))
            build().apply {
                Assert.assertEquals(DisplaySizeResolver(context), sizeResolver)
            }

            this.size(null)
            build().apply {
                Assert.assertNull(sizeResolver)
            }
        }
    }

    @Test
    fun testResizePrecision() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(precisionDecider)
            }

            precision(LongImageClipPrecisionDecider(EXACTLY))
            build().apply {
                Assert.assertEquals(LongImageClipPrecisionDecider(EXACTLY), precisionDecider)
            }

            precision(SAME_ASPECT_RATIO)
            build().apply {
                Assert.assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    precisionDecider
                )
            }

            precision(null)
            build().apply {
                Assert.assertNull(precisionDecider)
            }
        }
    }

    @Test
    fun testResizeScale() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(scaleDecider)
            }

            scale(LongImageStartCropScaleDecider(START_CROP, END_CROP))
            build().apply {
                Assert.assertEquals(
                    LongImageStartCropScaleDecider(START_CROP, END_CROP),
                    scaleDecider
                )
            }

            scale(FILL)
            build().apply {
                Assert.assertEquals(FixedScaleDecider(FILL), scaleDecider)
            }

            scale(null)
            build().apply {
                Assert.assertNull(scaleDecider)
            }
        }
    }

    @Test
    fun testTransformations() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(transformations)
            }

            /* transformations() */
            transformations(listOf(CircleCropTransformation()))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }

            transformations(RoundedCornersTransformation(), RotateTransformation(40))
            build().apply {
                Assert.assertEquals(
                    listOf(RoundedCornersTransformation(), RotateTransformation(40)),
                    transformations
                )
            }

            transformations(null)
            build().apply {
                Assert.assertNull(transformations)
            }

            /* addTransformations(List), removeTransformations(List) */
            addTransformations(listOf(CircleCropTransformation()))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            addTransformations(listOf(CircleCropTransformation(), RotateTransformation(40)))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation(), RotateTransformation(40)),
                    transformations
                )
            }
            removeTransformations(listOf(RotateTransformation(40)))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            removeTransformations(listOf(CircleCropTransformation()))
            build().apply {
                Assert.assertNull(transformations)
            }

            /* addTransformations(vararg), removeTransformations(vararg) */
            addTransformations(CircleCropTransformation())
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            addTransformations(CircleCropTransformation(), RotateTransformation(40))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation(), RotateTransformation(40)),
                    transformations
                )
            }
            removeTransformations(RotateTransformation(40))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            removeTransformations(CircleCropTransformation())
            build().apply {
                Assert.assertNull(transformations)
            }
        }
    }

    @Test
    fun testResultCachePolicy() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(resultCachePolicy)
            }

            resultCachePolicy(ENABLED)
            build().apply {
                Assert.assertEquals(ENABLED, resultCachePolicy)
            }

            resultCachePolicy(DISABLED)
            build().apply {
                Assert.assertEquals(DISABLED, resultCachePolicy)
            }

            resultCachePolicy(null)
            build().apply {
                Assert.assertNull(resultCachePolicy)
            }
        }
    }

    @Test
    fun testDisallowAnimatedImage() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(disallowAnimatedImage)
            }

            disallowAnimatedImage()
            build().apply {
                Assert.assertEquals(true, disallowAnimatedImage)
            }

            disallowAnimatedImage(false)
            build().apply {
                Assert.assertEquals(false, disallowAnimatedImage)
            }

            disallowAnimatedImage(null)
            build().apply {
                Assert.assertNull(disallowAnimatedImage)
            }
        }
    }

    @Test
    fun testPlaceholder() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(placeholder)
            }

            placeholder(ColorStateImage(IntColor(Color.BLUE)))
            build().apply {
                Assert.assertEquals(ColorStateImage(IntColor(Color.BLUE)), placeholder)
            }

            placeholder(ColorDrawableEqualizer(Color.GREEN))
            build().apply {
                Assert.assertEquals(true, placeholder is DrawableStateImage)
            }

            placeholder(android.R.drawable.bottom_bar)
            build().apply {
                Assert.assertEquals(
                    DrawableStateImage(android.R.drawable.bottom_bar),
                    placeholder
                )
            }

            placeholder(null)
            build().apply {
                Assert.assertNull(placeholder)
            }
        }
    }

    @Test
    fun testUriEmpty() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(uriEmpty)
            }

            uriEmpty(ColorStateImage(IntColor(Color.BLUE)))
            build().apply {
                Assert.assertEquals(ColorStateImage(IntColor(Color.BLUE)), uriEmpty)
            }

            uriEmpty(ColorDrawableEqualizer(Color.GREEN))
            build().apply {
                Assert.assertEquals(true, uriEmpty is DrawableStateImage)
            }

            uriEmpty(android.R.drawable.bottom_bar)
            build().apply {
                Assert.assertEquals(
                    DrawableStateImage(android.R.drawable.bottom_bar),
                    uriEmpty
                )
            }

            uriEmpty(null)
            build().apply {
                Assert.assertNull(uriEmpty)
            }
        }
    }

    @Test
    fun testError() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(error)
            }

            error(ColorStateImage(IntColor(Color.BLUE)))
            build().apply {
                Assert.assertEquals(
                    ErrorStateImage(ColorStateImage(IntColor(Color.BLUE))),
                    error
                )
            }

            error(ColorDrawableEqualizer(Color.GREEN))
            build().apply {
                Assert.assertEquals(true, error is ErrorStateImage)
            }

            error(android.R.drawable.bottom_bar)
            build().apply {
                Assert.assertEquals(
                    ErrorStateImage(DrawableStateImage(android.R.drawable.bottom_bar)),
                    error
                )
            }

            error(android.R.drawable.bottom_bar) {
                uriEmptyError(android.R.drawable.alert_dark_frame)
            }
            build().apply {
                Assert.assertEquals(
                    ErrorStateImage(DrawableStateImage(android.R.drawable.bottom_bar)) {
                        uriEmptyError(android.R.drawable.alert_dark_frame)
                    },
                    error
                )
            }

            error()
            build().apply {
                Assert.assertNull(error)
            }

            error {
                uriEmptyError(android.R.drawable.btn_dialog)
            }
            build().apply {
                Assert.assertNotNull(error)
            }
        }
    }

    @Test
    fun testTransition() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(transitionFactory)
            }

            transitionFactory(CrossfadeTransition.Factory())
            build().apply {
                Assert.assertEquals(CrossfadeTransition.Factory(), transitionFactory)
            }

            transitionFactory(null)
            build().apply {
                Assert.assertNull(transitionFactory)
            }
        }
    }

    @Test
    fun testResizeApplyToDrawable() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(resizeOnDrawHelper)
            }

            resizeOnDraw()
            build().apply {
                Assert.assertEquals(true, resizeOnDrawHelper)
            }

            resizeOnDraw(false)
            build().apply {
                Assert.assertEquals(false, resizeOnDrawHelper)
            }

            resizeOnDraw(null)
            build().apply {
                Assert.assertNull(resizeOnDrawHelper)
            }
        }
    }

    @Test
    fun testMemoryCachePolicy() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(memoryCachePolicy)
            }

            memoryCachePolicy(ENABLED)
            build().apply {
                Assert.assertEquals(ENABLED, memoryCachePolicy)
            }

            memoryCachePolicy(DISABLED)
            build().apply {
                Assert.assertEquals(DISABLED, memoryCachePolicy)
            }

            memoryCachePolicy(null)
            build().apply {
                Assert.assertNull(memoryCachePolicy)
            }
        }
    }

    @Test
    fun testComponentRegistry() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(componentRegistry)
            }

            components {
                addFetcher(HttpUriFetcher.Factory())
            }
            val options1 = build().apply {
                Assert.assertEquals(ComponentRegistry.Builder().apply {
                    addFetcher(HttpUriFetcher.Factory())
                }.build(), componentRegistry)
            }

            components {
                addDecoder(TestDecoder.Factory())
            }
            val options2 = build().apply {
                Assert.assertEquals(ComponentRegistry.Builder().apply {
                    addDecoder(TestDecoder.Factory())
                }.build(), componentRegistry)
            }

            Assert.assertNotEquals(options1, options2)

            components(null)
            build().apply {
                Assert.assertNull(componentRegistry)
            }
        }
    }

    @Test
    fun testMergeComponents() {
        // TODO test mergeComponents
    }

    @Test
    fun testSizeMultiplier() {
        // TODO test sizeMultiplier
    }
}