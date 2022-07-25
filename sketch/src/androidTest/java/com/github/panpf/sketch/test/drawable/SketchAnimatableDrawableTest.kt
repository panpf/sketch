package com.github.panpf.sketch.test.drawable

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.datasource.DataFrom.RESULT_CACHE
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable1
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable2
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable3
import com.github.panpf.sketch.test.utils.TestNewMutateDrawable
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.BitmapInfo
import com.github.panpf.sketch.util.getBitmapByteSize
import com.github.panpf.sketch.util.getDrawableCompat
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SketchAnimatableDrawableTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()
        SketchAnimatableDrawable(
            animatableDrawable = TestAnimatableDrawable1(
                BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
            ),
            imageUri = "imageUri1",
            requestKey = "requestKey1",
            requestCacheKey = "requestKey1",
            imageInfo = ImageInfo(100, 100, "image/gif", 0),
            dataFrom = LOCAL,
            transformedList = null as List<String>?
        )
        if (Build.VERSION.SDK_INT >= 23) {
            SketchAnimatableDrawable(
                animatableDrawable = TestAnimatableDrawable2(
                    BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
                ),
                imageUri = "imageUri1",
                requestKey = "requestKey1",
                requestCacheKey = "requestKey1",
                imageInfo = ImageInfo(100, 100, "image/gif", 0),
                dataFrom = LOCAL,
                transformedList = null as List<String>?
            )
        }
        SketchAnimatableDrawable(
            animatableDrawable = TestAnimatableDrawable3(
                BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
            ),
            imageUri = "imageUri1",
            requestKey = "requestKey1",
            requestCacheKey = "requestKey1",
            imageInfo = ImageInfo(100, 100, "image/gif", 0),
            dataFrom = LOCAL,
            transformedList = null as List<String>?
        )
        assertThrow(IllegalArgumentException::class) {
            SketchAnimatableDrawable(
                animatableDrawable = BitmapDrawable(
                    context.resources,
                    Bitmap.createBitmap(100, 100, ARGB_8888)
                ),
                imageUri = "imageUri1",
                requestKey = "requestKey1",
                requestCacheKey = "requestKey1",
                imageInfo = ImageInfo(100, 100, "image/gif", 0),
                dataFrom = LOCAL,
                transformedList = null as List<String>?
            )
        }
    }

    @Test
    fun testStartStopIsRunning() {
        val context = getTestContext()

        SketchAnimatableDrawable(
            animatableDrawable = TestAnimatableDrawable1(
                BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
            ),
            imageUri = "imageUri1",
            requestKey = "requestKey1",
            requestCacheKey = "requestKey1",
            imageInfo = ImageInfo(100, 100, "image/gif", 0),
            dataFrom = LOCAL,
            transformedList = null as List<String>?
        ).apply {
            val callbackAction = mutableListOf<String>()
            val callback3 = object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable?) {
                    super.onAnimationStart(drawable)
                    callbackAction.add("onAnimationStart")
                }

                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    callbackAction.add("onAnimationEnd")
                }
            }
            runBlocking(Dispatchers.Main) {
                registerAnimationCallback(callback3)
            }

            Assert.assertFalse(isRunning)
            Assert.assertEquals(listOf<String>(), callbackAction)

            start()
            Thread.sleep(100)
            Assert.assertTrue(isRunning)
            Assert.assertEquals(listOf("onAnimationStart"), callbackAction)

            stop()
            Thread.sleep(100)
            Assert.assertFalse(isRunning)
            Assert.assertEquals(listOf("onAnimationStart", "onAnimationEnd"), callbackAction)
        }

        if (Build.VERSION.SDK_INT >= 23) {
            SketchAnimatableDrawable(
                animatableDrawable = TestAnimatableDrawable2(
                    BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
                ),
                imageUri = "imageUri1",
                requestKey = "requestKey1",
                requestCacheKey = "requestKey1",
                imageInfo = ImageInfo(100, 100, "image/gif", 0),
                dataFrom = LOCAL,
                transformedList = null as List<String>?
            ).apply {
                val callbackAction = mutableListOf<String>()
                val callback3 = object : Animatable2Compat.AnimationCallback() {
                    override fun onAnimationStart(drawable: Drawable?) {
                        super.onAnimationStart(drawable)
                        callbackAction.add("onAnimationStart")
                    }

                    override fun onAnimationEnd(drawable: Drawable?) {
                        super.onAnimationEnd(drawable)
                        callbackAction.add("onAnimationEnd")
                    }
                }
                runBlocking(Dispatchers.Main) {
                    registerAnimationCallback(callback3)
                }

                Assert.assertFalse(isRunning)
                Assert.assertEquals(listOf<String>(), callbackAction)

                start()
                Thread.sleep(100)
                Assert.assertTrue(isRunning)
                Assert.assertEquals(listOf("onAnimationStart"), callbackAction)

                stop()
                Thread.sleep(100)
                Assert.assertFalse(isRunning)
                Assert.assertEquals(listOf("onAnimationStart", "onAnimationEnd"), callbackAction)
            }
        }

        SketchAnimatableDrawable(
            animatableDrawable = TestAnimatableDrawable3(
                BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
            ),
            imageUri = "imageUri1",
            requestKey = "requestKey1",
            requestCacheKey = "requestKey1",
            imageInfo = ImageInfo(100, 100, "image/gif", 0),
            dataFrom = LOCAL,
            transformedList = null as List<String>?
        ).apply {
            val callbackAction = mutableListOf<String>()
            val callback3 = object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable?) {
                    super.onAnimationStart(drawable)
                    callbackAction.add("onAnimationStart")
                }

                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    callbackAction.add("onAnimationEnd")
                }
            }
            runBlocking(Dispatchers.Main) {
                registerAnimationCallback(callback3)
            }

            Assert.assertFalse(isRunning)
            Assert.assertEquals(listOf<String>(), callbackAction)

            start()
            Thread.sleep(100)
            Assert.assertTrue(isRunning)
            Assert.assertEquals(listOf("onAnimationStart"), callbackAction)

            stop()
            Thread.sleep(100)
            Assert.assertFalse(isRunning)
            Assert.assertEquals(listOf("onAnimationStart", "onAnimationEnd"), callbackAction)
        }
    }

    @Test
    fun testCallback() {
        val context = getTestContext()

        SketchAnimatableDrawable(
            animatableDrawable = TestAnimatableDrawable1(
                BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
            ),
            imageUri = "imageUri1",
            requestKey = "requestKey1",
            requestCacheKey = "requestKey1",
            imageInfo = ImageInfo(100, 100, "image/gif", 0),
            dataFrom = LOCAL,
            transformedList = null as List<String>?
        ).apply {
            val callback = object : Animatable2Compat.AnimationCallback() {}
            runBlocking(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            unregisterAnimationCallback(callback)
            runBlocking(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            clearAnimationCallbacks()
        }


        if (Build.VERSION.SDK_INT >= 23) {
            SketchAnimatableDrawable(
                animatableDrawable = TestAnimatableDrawable2(
                    BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
                ),
                imageUri = "imageUri1",
                requestKey = "requestKey1",
                requestCacheKey = "requestKey1",
                imageInfo = ImageInfo(100, 100, "image/gif", 0),
                dataFrom = LOCAL,
                transformedList = null as List<String>?
            ).apply {
                val callback = object : Animatable2Compat.AnimationCallback() {}
                runBlocking(Dispatchers.Main) {
                    registerAnimationCallback(callback)
                }
                unregisterAnimationCallback(callback)
                runBlocking(Dispatchers.Main) {
                    registerAnimationCallback(callback)
                }
                clearAnimationCallbacks()
            }
        }

        SketchAnimatableDrawable(
            animatableDrawable = TestAnimatableDrawable3(
                BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
            ),
            imageUri = "imageUri1",
            requestKey = "requestKey1",
            requestCacheKey = "requestKey1",
            imageInfo = ImageInfo(100, 100, "image/gif", 0),
            dataFrom = LOCAL,
            transformedList = null as List<String>?
        ).apply {
            val callback = object : Animatable2Compat.AnimationCallback() {}
            runBlocking(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            unregisterAnimationCallback(callback)
            runBlocking(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            clearAnimationCallbacks()
        }
    }

    @Test
    fun testMutate() {
        val context = getTestContext()

        SketchAnimatableDrawable(
            animatableDrawable = TestAnimatableDrawable3(
                context.getDrawableCompat(android.R.drawable.bottom_bar)
            ),
            imageUri = "imageUri1",
            requestKey = "requestKey1",
            requestCacheKey = "requestKey1",
            imageInfo = ImageInfo(100, 100, "image/gif", 0),
            dataFrom = LOCAL,
            transformedList = null as List<String>?
        ).apply {
            mutate()
            alpha = 146

            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Assert.assertEquals(255, it.alpha)
                }
            }
        }

        SketchAnimatableDrawable(
            animatableDrawable = TestAnimatableDrawable3(
                TestNewMutateDrawable(context.getDrawableCompat(android.R.drawable.bottom_bar))
            ),
            imageUri = "imageUri1",
            requestKey = "requestKey1",
            requestCacheKey = "requestKey1",
            imageInfo = ImageInfo(100, 100, "image/gif", 0),
            dataFrom = LOCAL,
            transformedList = null as List<String>?
        ).apply {
            mutate()
            alpha = 146

            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Assert.assertEquals(255, it.alpha)
                }
            }
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()

        val drawable = TestAnimatableDrawable3(
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
        )
        val drawable1 = TestAnimatableDrawable3(
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
        )
        val imageInfo = ImageInfo(100, 100, "image/gif", 0)
        val dataFrom = LOCAL
        val transformedList: List<String>? = null
        val imageUri = "imageUri1"
        val requestKey = "requestKey1"

        val element1 = SketchAnimatableDrawable(
            animatableDrawable = drawable,
            imageUri = imageUri,
            requestKey = requestKey,
            requestCacheKey = requestKey,
            imageInfo = imageInfo,
            dataFrom = dataFrom,
            transformedList = transformedList
        )
        val element11 = SketchAnimatableDrawable(
            animatableDrawable = drawable,
            imageUri = imageUri,
            requestKey = requestKey,
            requestCacheKey = requestKey,
            imageInfo = imageInfo,
            dataFrom = dataFrom,
            transformedList = transformedList
        )
        val element2 = SketchAnimatableDrawable(
            animatableDrawable = drawable1,
            imageUri = imageUri,
            requestKey = requestKey,
            requestCacheKey = requestKey,
            imageInfo = imageInfo,
            dataFrom = dataFrom,
            transformedList = transformedList
        )
        val element3 = SketchAnimatableDrawable(
            animatableDrawable = drawable,
            imageUri = imageUri + "2",
            requestKey = requestKey,
            requestCacheKey = requestKey,
            imageInfo = imageInfo,
            dataFrom = dataFrom,
            transformedList = transformedList
        )
        val element4 = SketchAnimatableDrawable(
            animatableDrawable = drawable,
            imageUri = imageUri,
            requestKey = requestKey + "2",
            requestCacheKey = requestKey,
            imageInfo = imageInfo,
            dataFrom = dataFrom,
            transformedList = transformedList
        )
        val element5 = SketchAnimatableDrawable(
            animatableDrawable = drawable,
            imageUri = imageUri,
            requestKey = requestKey,
            requestCacheKey = requestKey + "2",
            imageInfo = imageInfo,
            dataFrom = dataFrom,
            transformedList = transformedList
        )
        val element6 = SketchAnimatableDrawable(
            animatableDrawable = drawable,
            imageUri = imageUri,
            requestKey = requestKey,
            requestCacheKey = requestKey,
            imageInfo = imageInfo.newImageInfo(width = 300),
            dataFrom = dataFrom,
            transformedList = transformedList
        )
        val element7 = SketchAnimatableDrawable(
            animatableDrawable = drawable,
            imageUri = imageUri,
            requestKey = requestKey,
            requestCacheKey = requestKey,
            imageInfo = imageInfo,
            dataFrom = RESULT_CACHE,
            transformedList = transformedList
        )
        val element8 = SketchAnimatableDrawable(
            animatableDrawable = drawable,
            imageUri = imageUri,
            requestKey = requestKey,
            requestCacheKey = requestKey,
            imageInfo = imageInfo,
            dataFrom = dataFrom,
            transformedList = listOf("transformed1")
        )

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element1, element4)
        Assert.assertNotSame(element1, element5)
        Assert.assertNotSame(element1, element6)
        Assert.assertNotSame(element1, element7)
        Assert.assertNotSame(element1, element8)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element1, element4)
        Assert.assertNotEquals(element1, element5)
        Assert.assertNotEquals(element1, element6)
        Assert.assertNotEquals(element1, element7)
        Assert.assertNotEquals(element1, element8)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element5.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element6.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element7.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element8.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()

        val drawable = TestAnimatableDrawable3(
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
        )
        val imageInfo = ImageInfo(100, 100, "image/gif", 0)
        val dataFrom = LOCAL
        val bitmapInfo = BitmapInfo(
            width = drawable.intrinsicWidth,
            height = drawable.intrinsicHeight,
            byteCount = getBitmapByteSize(
                drawable.intrinsicWidth, drawable.intrinsicHeight, ARGB_8888
            ),
            config = ARGB_8888
        )
        val transformedList: List<String>? = null
        val imageUri = "imageUri1"
        val requestKey = "requestKey1"

        val sketchAnimatableDrawable = SketchAnimatableDrawable(
            animatableDrawable = drawable,
            imageUri = imageUri,
            requestKey = requestKey,
            requestCacheKey = requestKey,
            imageInfo = imageInfo,
            dataFrom = dataFrom,
            transformedList = transformedList
        )
        Assert.assertEquals(
            "SketchAnimatableDrawable(" +
                    drawable +
                    ", " + imageInfo.toShortString() +
                    "," + dataFrom +
                    "," + bitmapInfo.toShortString() +
                    "," + transformedList +
                    "," + requestKey +
                    ")",
            sketchAnimatableDrawable.toString()
        )
    }

}