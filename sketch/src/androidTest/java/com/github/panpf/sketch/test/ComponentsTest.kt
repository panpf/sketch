package com.github.panpf.sketch.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Components
import com.github.panpf.sketch.decode.internal.BitmapEngineDecodeInterceptor
import com.github.panpf.sketch.decode.internal.DefaultBitmapDecoder
import com.github.panpf.sketch.decode.internal.DefaultDrawableDecoder
import com.github.panpf.sketch.decode.internal.DrawableEngineDecodeInterceptor
import com.github.panpf.sketch.decode.internal.XmlDrawableBitmapDecoder
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.getContext
import com.github.panpf.sketch.test.utils.getSketch
import com.github.panpf.sketch.transform.internal.BitmapTransformationDecodeInterceptor
import com.github.panpf.tools4j.test.ktx.assertNoThrow
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComponentsTest {

    @Test
    fun testInterceptors() {
        val sketch = getSketch()
        Components(sketch, ComponentRegistry.Builder().build()).apply {
            Assert.assertTrue(requestInterceptorList.isEmpty())
            Assert.assertTrue(bitmapDecodeInterceptorList.isEmpty())
            Assert.assertTrue(drawableDecodeInterceptorList.isEmpty())
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addFetcher(Base64UriFetcher.Factory())
            addFetcher(ResourceUriFetcher.Factory())
            addBitmapDecoder(XmlDrawableBitmapDecoder.Factory())
            addBitmapDecoder(DefaultBitmapDecoder.Factory())
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addBitmapDecodeInterceptor(BitmapEngineDecodeInterceptor())
            addBitmapDecodeInterceptor(BitmapTransformationDecodeInterceptor())
            addDrawableDecodeInterceptor(DrawableEngineDecodeInterceptor())
        }.build()).apply {
            Assert.assertEquals(listOf(EngineRequestInterceptor()), requestInterceptorList)
            Assert.assertEquals(
                listOf(
                    BitmapEngineDecodeInterceptor(),
                    BitmapTransformationDecodeInterceptor()
                ),
                bitmapDecodeInterceptorList
            )
            Assert.assertEquals(
                listOf(DrawableEngineDecodeInterceptor()),
                drawableDecodeInterceptorList
            )
        }
    }

    @Test
    fun testNewFetcher() {
        val context = getContext()
        val sketch = getSketch()

        Components(sketch, ComponentRegistry.Builder().build()).apply {
            assertThrow(IllegalStateException::class) {
                runBlocking(Dispatchers.Main) {
                    newFetcher(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
                }
            }
        }

        Components(sketch, ComponentRegistry.Builder().build()).apply {
            assertThrow(IllegalArgumentException::class) {
                newFetcher(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            }
            assertThrow(IllegalArgumentException::class) {
                newFetcher(DisplayRequest(context, "http://sample.com/sample.jpeg"))
            }
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build()).apply {
            assertNoThrow {
                newFetcher(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            }
            assertThrow(IllegalArgumentException::class) {
                newFetcher(DisplayRequest(context, "http://sample.com/sample.jpeg"))
            }
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addFetcher(HttpUriFetcher.Factory())
        }.build()).apply {
            assertNoThrow {
                newFetcher(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            }
            assertNoThrow {
                newFetcher(DisplayRequest(context, "http://sample.com/sample.jpeg"))
            }
        }
    }

    @Test
    fun testBitmapDecoder() {
        val context = getContext()
        val sketch = getSketch()
        val requestContext = RequestContext()
        val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI)

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build()).apply {
            val fetcher = newFetcher(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertThrow(IllegalStateException::class) {
                runBlocking(Dispatchers.Main) {
                    newBitmapDecoder(request, requestContext, fetchResult)
                }
            }
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build()).apply {
            val fetcher = newFetcher(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertThrow(IllegalArgumentException::class) {
                newBitmapDecoder(request, requestContext, fetchResult)
            }
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addBitmapDecoder(DefaultBitmapDecoder.Factory())
        }.build()).apply {
            val fetcher = newFetcher(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertNoThrow {
                newBitmapDecoder(request, requestContext, fetchResult)
            }
        }
    }

    @Test
    fun testDrawableDecoder() {
        val context = getContext()
        val sketch = getSketch()
        val requestContext = RequestContext()
        val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI)

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build()).apply {
            val fetcher = newFetcher(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertThrow(IllegalStateException::class) {
                runBlocking(Dispatchers.Main) {
                    newDrawableDecoder(request, requestContext, fetchResult)
                }
            }
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build()).apply {
            val fetcher = newFetcher(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertThrow(IllegalArgumentException::class) {
                newDrawableDecoder(request, requestContext, fetchResult)
            }
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
        }.build()).apply {
            val fetcher = newFetcher(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertNoThrow {
                newDrawableDecoder(request, requestContext, fetchResult)
            }
        }
    }

    @Test
    fun testToString() {
        val sketch = getSketch()
        Components(sketch, ComponentRegistry.Builder().build()).apply {
            Assert.assertEquals(
                "Components(registry=ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "bitmapDecoderFactoryList=[]," +
                        "drawableDecoderFactoryList=[]," +
                        "requestInterceptorList=[]," +
                        "bitmapDecodeInterceptorList=[]," +
                        "drawableDecodeInterceptorList=[]" +
                        "))",
                toString()
            )
        }
        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addFetcher(Base64UriFetcher.Factory())
            addFetcher(ResourceUriFetcher.Factory())
            addBitmapDecoder(XmlDrawableBitmapDecoder.Factory())
            addBitmapDecoder(DefaultBitmapDecoder.Factory())
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addBitmapDecodeInterceptor(BitmapEngineDecodeInterceptor())
            addBitmapDecodeInterceptor(BitmapTransformationDecodeInterceptor())
            addDrawableDecodeInterceptor(DrawableEngineDecodeInterceptor())
        }.build()).apply {
            Assert.assertEquals(
                "Components(registry=ComponentRegistry(" +
                        "fetcherFactoryList=[HttpUriFetcher,Base64UriFetcher,ResourceUriFetcher]," +
                        "bitmapDecoderFactoryList=[XmlDrawableBitmapDecoder,DefaultBitmapDecoder]," +
                        "drawableDecoderFactoryList=[DefaultDrawableDecoder]," +
                        "requestInterceptorList=[EngineRequestInterceptor]," +
                        "bitmapDecodeInterceptorList=[BitmapEngineDecodeInterceptor,BitmapTransformationDecodeInterceptor]," +
                        "drawableDecodeInterceptorList=[DrawableEngineDecodeInterceptor]" +
                        "))",
                toString()
            )
        }
    }

    @Test
    fun testEquals() {
        val sketch = getSketch()
        val components0 = Components(sketch, ComponentRegistry.Builder().build())
        val components1 = Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
        }.build())
        val components2 = Components(sketch, ComponentRegistry.Builder().apply {
            addBitmapDecoder(XmlDrawableBitmapDecoder.Factory())
        }.build())
        val components3 = Components(sketch, ComponentRegistry.Builder().apply {
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
        }.build())
        val components4 = Components(sketch, ComponentRegistry.Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
        }.build())
        val components5 = Components(sketch, ComponentRegistry.Builder().apply {
            addBitmapDecodeInterceptor(BitmapEngineDecodeInterceptor())
        }.build())
        val components6 = Components(sketch, ComponentRegistry.Builder().apply {
            addDrawableDecodeInterceptor(DrawableEngineDecodeInterceptor())
        }.build())

        Assert.assertNotEquals(components0, components1)
        Assert.assertNotEquals(components0, components2)
        Assert.assertNotEquals(components0, components3)
        Assert.assertNotEquals(components0, components4)
        Assert.assertNotEquals(components0, components5)
        Assert.assertNotEquals(components0, components6)
        Assert.assertNotEquals(components1, components2)
        Assert.assertNotEquals(components1, components3)
        Assert.assertNotEquals(components1, components4)
        Assert.assertNotEquals(components1, components5)
        Assert.assertNotEquals(components1, components6)
        Assert.assertNotEquals(components2, components3)
        Assert.assertNotEquals(components2, components4)
        Assert.assertNotEquals(components2, components5)
        Assert.assertNotEquals(components2, components6)
        Assert.assertNotEquals(components3, components4)
        Assert.assertNotEquals(components3, components5)
        Assert.assertNotEquals(components3, components6)
        Assert.assertNotEquals(components4, components5)
        Assert.assertNotEquals(components4, components6)
        Assert.assertNotEquals(components5, components6)
    }

    @Test
    fun testHashCode() {
        val sketch = getSketch()
        val components0 = Components(sketch, ComponentRegistry.Builder().build())
        val components1 = Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
        }.build())
        val components2 = Components(sketch, ComponentRegistry.Builder().apply {
            addBitmapDecoder(XmlDrawableBitmapDecoder.Factory())
        }.build())
        val components3 = Components(sketch, ComponentRegistry.Builder().apply {
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
        }.build())
        val components4 = Components(sketch, ComponentRegistry.Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
        }.build())
        val components5 = Components(sketch, ComponentRegistry.Builder().apply {
            addBitmapDecodeInterceptor(BitmapEngineDecodeInterceptor())
        }.build())
        val components6 = Components(sketch, ComponentRegistry.Builder().apply {
            addDrawableDecodeInterceptor(DrawableEngineDecodeInterceptor())
        }.build())

        Assert.assertNotEquals(components0.hashCode(), components1.hashCode())
        Assert.assertNotEquals(components0.hashCode(), components2.hashCode())
        Assert.assertNotEquals(components0.hashCode(), components3.hashCode())
        Assert.assertNotEquals(components0.hashCode(), components4.hashCode())
        Assert.assertNotEquals(components0.hashCode(), components5.hashCode())
        Assert.assertNotEquals(components0.hashCode(), components6.hashCode())
        Assert.assertNotEquals(components1.hashCode(), components2.hashCode())
        Assert.assertNotEquals(components1.hashCode(), components3.hashCode())
        Assert.assertNotEquals(components1.hashCode(), components4.hashCode())
        Assert.assertNotEquals(components1.hashCode(), components5.hashCode())
        Assert.assertNotEquals(components1.hashCode(), components6.hashCode())
        Assert.assertNotEquals(components2.hashCode(), components3.hashCode())
        Assert.assertNotEquals(components2.hashCode(), components4.hashCode())
        Assert.assertNotEquals(components2.hashCode(), components5.hashCode())
        Assert.assertNotEquals(components2.hashCode(), components6.hashCode())
        Assert.assertNotEquals(components3.hashCode(), components4.hashCode())
        Assert.assertNotEquals(components3.hashCode(), components5.hashCode())
        Assert.assertNotEquals(components3.hashCode(), components6.hashCode())
        Assert.assertNotEquals(components4.hashCode(), components5.hashCode())
        Assert.assertNotEquals(components4.hashCode(), components6.hashCode())
        Assert.assertNotEquals(components5.hashCode(), components6.hashCode())
    }
}