package com.github.panpf.sketch.extensions.view.ability.test

import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView.ScaleType.FIT_XY
import android.widget.ImageView.ScaleType.MATRIX
import androidx.core.view.isVisible
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.target
import com.github.panpf.sketch.test.singleton.request.execute
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.TestHttpUriFetcher
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.runInNewSketchWithUse
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class AbsAbilityImageViewTest {

    @Test
    fun testAttachObserver() {
        val activity: ViewAbilityTestActivity
        ViewAbilityTestActivity::class.launchActivity().use { activityScenario ->
            activity = activityScenario.getActivitySync()
        }
        block(100)
        assertEquals(
            expected = listOf("onAttachedToWindow", "onDetachedFromWindow"),
            actual = activity.viewAbility.attachActions
        )
    }

    @Test
    fun testLayoutObserver() {
        ViewAbilityTestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            assertTrue(
                actual = activity.viewAbility.layoutActions.size > 0,
                message = "size: ${activity.viewAbility.layoutActions.size}"
            )
        }
    }

    @Test
    fun testDrawObserver() = runTest {
        ViewAbilityTestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                assertEquals(
                    expected = listOf(
                        "onDrawBefore",
                        "onDraw",
                        "onDrawForegroundBefore",
                        "onDrawForeground"
                    ),
                    actual = activity.viewAbility.drawActions
                )
            } else {
                assertEquals(
                    expected = listOf("onDrawBefore", "onDraw"),
                    actual = activity.viewAbility.drawActions
                )
            }
        }
    }

    @Test
    fun testSizeChangedObserver() = runTest {
        ViewAbilityTestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val containerSize = activity.findViewById<View>(android.R.id.content)
                .let { Size(width = it.width, height = it.height) }

            assertEquals(
                expected = listOf("onSizeChanged:$containerSize"),
                actual = activity.viewAbility.sizeActions
            )

            withContext(Dispatchers.Main) {
                activity.abilityView.layoutParams = FrameLayout.LayoutParams(100, 100)
            }
            block(100)

            assertEquals(
                expected = listOf("onSizeChanged:$containerSize", "onSizeChanged:100x100"),
                actual = activity.viewAbility.sizeActions
            )
        }
    }

    @Test
    fun testVisibilityChangedObserver() = runTest {
        ViewAbilityTestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                assertEquals(
                    expected = listOf("onVisibilityChanged:true"),
                    actual = activity.viewAbility.visibilityActions
                )
            } else {
                assertEquals(
                    expected = listOf("onVisibilityChanged:false", "onVisibilityChanged:true"),
                    actual = activity.viewAbility.visibilityActions
                )
            }

            withContext(Dispatchers.Main) {
                activity.abilityView.isVisible = false
            }
            block(100)

            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                assertEquals(
                    expected = listOf("onVisibilityChanged:true", "onVisibilityChanged:false"),
                    actual = activity.viewAbility.visibilityActions
                )
            } else {
                assertEquals(
                    expected = listOf(
                        "onVisibilityChanged:false",
                        "onVisibilityChanged:true",
                        "onVisibilityChanged:false"
                    ),
                    actual = activity.viewAbility.visibilityActions
                )
            }

            withContext(Dispatchers.Main) {
                activity.abilityView.isVisible = true
            }
            block(100)

            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                assertEquals(
                    expected = listOf(
                        "onVisibilityChanged:true",
                        "onVisibilityChanged:false",
                        "onVisibilityChanged:true"
                    ),
                    actual = activity.viewAbility.visibilityActions
                )
            } else {
                assertEquals(
                    expected = listOf(
                        "onVisibilityChanged:false",
                        "onVisibilityChanged:true",
                        "onVisibilityChanged:false",
                        "onVisibilityChanged:true"
                    ),
                    actual = activity.viewAbility.visibilityActions
                )
            }
        }
    }

    @Test
    fun testTouchEventObserver() = runTest {
        // TODO test: Not testable. no way to send touch events has been found yet
//        ViewAbilityTestActivity::class.launchActivity().use { activityScenario ->
//            val activity = activityScenario.getActivitySync()
//            sendTouchClick(500, 500)
//            block(100)
//            assertEquals(
//                expected = listOf("onTouchEvent"),
//                actual = touchEventViewAbility.actions
//            )
//        }
    }
//
//    @WorkerThread
//    private fun sendTouchClick(x: Int, y: Int) {
//        val instrumentation = InstrumentationRegistry.getInstrumentation()
//
//        // Create a MotionEvent object to represent the press operation
//        val downTime = SystemClock.uptimeMillis()
//        val eventTime = SystemClock.uptimeMillis()
//        val downEvent = MotionEvent.obtain(
//            downTime, eventTime, MotionEvent.ACTION_DOWN, x.toFloat(), y.toFloat(), 0
//        )
//        // Send pressed event
//        instrumentation.sendPointerSync(downEvent)
//
//        // Create a MotionEvent object to represent the lift operation
//        val upEvent = MotionEvent.obtain(
//            downTime, eventTime, MotionEvent.ACTION_UP, x.toFloat(), y.toFloat(), 0
//        )
//        // Send lift event
//        instrumentation.sendPointerSync(upEvent)
//
//        // Recycling MotionEvent objects
//        downEvent.recycle()
//        upEvent.recycle()
//    }

    @Test
    fun testClickObserver() = runTest {
        ViewAbilityTestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            withContext(Dispatchers.Main) {
                activity.abilityView.performClick()
            }
            block(100)

            assertEquals(
                expected = listOf("onClick"),
                actual = activity.viewAbility.clickActions
            )
        }
    }

    @Test
    fun testLongClickObserver() = runTest {
        ViewAbilityTestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            withContext(Dispatchers.Main) {
                activity.abilityView.performLongClick()
            }
            block(100)

            assertEquals(
                expected = listOf("onLongClick"),
                actual = activity.viewAbility.longClickActions
            )
        }
    }

    @Test
    fun testDrawableObserver() = runTest {
        ViewAbilityTestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            withContext(Dispatchers.Main) {
                activity.abilityView.setImageDrawable(null)
                val colorDrawable = ColorDrawable(TestColor.RED)
                activity.abilityView.setImageDrawable(colorDrawable)
                activity.abilityView.setImageDrawable(colorDrawable)
                activity.abilityView.setImageDrawable(null)
                activity.abilityView.setImageDrawable(colorDrawable.asEquitable())
                activity.abilityView.setImageDrawable(colorDrawable.asEquitable())
            }
            block(100)

            assertEquals(
                expected = listOf(
                    "onDrawableChanged:Null->NotNull",
                    "onDrawableChanged:NotNull->Null",
                    "onDrawableChanged:Null->NotNull",
                    "onDrawableChanged:NotNull->Null",
                    "onDrawableChanged:Null->NotNull",
                    "onDrawableChanged:NotNull->NotNull",
                ),
                actual = activity.viewAbility.drawableActions
            )
        }
    }

    @Test
    fun testScaleTypeObserver() = runTest {
        ViewAbilityTestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            withContext(Dispatchers.Main) {
                activity.abilityView.scaleType
                activity.abilityView.scaleType = FIT_XY
                activity.abilityView.scaleType
            }
            block(100)

            assertEquals(
                expected = listOf(
                    "getScaleType:FIT_CENTER",
                    "setScaleType:FIT_XY",
                    "getScaleType:FIT_XY"
                ),
                actual = activity.viewAbility.scaleTypeActions
            )
        }
    }

    @Test
    fun testImageMatrixObserver() = runTest {
        ViewAbilityTestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            assertEquals(
                expected = listOf(),
                actual = activity.viewAbility.imageMatrixActions
            )

            val matrix = Matrix().apply {
                postRotate(90f)
                postScale(1.5f, 1.5f)
            }
            withContext(Dispatchers.Main) {
                activity.abilityView.scaleType = MATRIX
                activity.abilityView.imageMatrix = matrix
            }
            block(100)

            assertEquals(
                expected = listOf("setImageMatrix:Matrix{[0.0, -1.5, 0.0][1.5, 0.0, 0.0][0.0, 0.0, 1.0]}"),
                actual = activity.viewAbility.imageMatrixActions
            )
        }
    }

    @Test
    fun testRequestListenerObserver() = runTest {
        ViewAbilityTestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            assertEquals(
                expected = listOf(),
                actual = activity.viewAbility.requestListenerActions
            )

            ImageRequest(activity, ResourceImages.jpeg.uri) {
                target(activity.abilityView)
                resultCachePolicy(DISABLED)
            }.execute()
            assertEquals(
                expected = listOf("onRequestStart", "onRequestSuccess"),
                actual = activity.viewAbility.requestListenerActions
            )

            ImageRequest(activity, ResourceImages.jpeg.uri + "1") {
                target(activity.abilityView)
                resultCachePolicy(DISABLED)
            }.execute()
            assertEquals(
                expected = listOf(
                    "onRequestStart",
                    "onRequestSuccess",
                    "onRequestStart",
                    "onRequestError"
                ),
                actual = activity.viewAbility.requestListenerActions
            )
        }
    }

    @Test
    fun testRequestProgressListenerObserver() = runTest {
        runInNewSketchWithUse({
            components {
                addFetcher(TestHttpUriFetcher.Factory(it))
            }
        }) { _, sketch ->
            ViewAbilityTestActivity::class.launchActivity().use { activityScenario ->
                val activity = activityScenario.getActivitySync()

                assertEquals(
                    expected = listOf(),
                    actual = activity.viewAbility.requestProgressListenerActions
                )

                ImageRequest(activity, TestHttpStack.testImages.first().uri) {
                    target(activity.abilityView)
                    downloadCachePolicy(DISABLED)
                    resultCachePolicy(DISABLED)
                    memoryCachePolicy(DISABLED)
                }.execute(sketch)
                assertTrue(
                    actual = activity.viewAbility.requestProgressListenerActions.size > 0,
                    message = "size: ${activity.viewAbility.requestProgressListenerActions.size}"
                )
            }
        }
    }

    @Test
    fun testInstanceStateObserver() {
        ViewAbilityTestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            assertEquals(expected = listOf(), actual = activity.viewAbility.instanceStateActions)

            activityScenario.recreate()
            block(100)
            assertEquals(
                expected = listOf("onSaveInstanceState"),
                actual = activity.viewAbility.instanceStateActions
            )

            val activity1 = activityScenario.getActivitySync()
            assertNotSame(illegal = activity, actual = activity1)
            assertEquals(
                expected = listOf("onRestoreInstanceState"),
                actual = activity1.viewAbility.instanceStateActions
            )
        }
    }
}