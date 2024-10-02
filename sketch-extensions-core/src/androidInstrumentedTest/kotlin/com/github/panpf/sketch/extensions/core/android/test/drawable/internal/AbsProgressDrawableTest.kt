package com.github.panpf.sketch.extensions.core.android.test.drawable.internal

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.ImageView
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.drawable.ProgressDrawable
import com.github.panpf.sketch.drawable.internal.AbsProgressDrawable
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AbsProgressDrawableTest {

    @Test
    fun testProgress() = runTest {
        // It was found that the test can only be completed stably on Android 8.0, so.
        // Maybe you can change the test plan when View supports screenshot testing.
        if (VERSION.SDK_INT != VERSION_CODES.O) return@runTest

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = false,
                hiddenWhenCompleted = true,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = 0.25f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            checkElements(actions)
            assertTrue(actual = actions.size >= 5, message = "$actions")
            assertTrue(actual = actions.first().toFloat() >= 0.0f, message = "$actions")
            assertEquals(expected = "0.2", actual = actions.last(), message = "$actions")
            assertEquals(expected = 0.2f, actual = testProgressDrawable.progress)
        }

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = false,
                hiddenWhenCompleted = true,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = 0.5f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            checkElements(actions)
            assertTrue(actual = actions.size >= 5, message = "$actions")
            assertTrue(actual = actions.first().toFloat() >= 0.0f, message = "$actions")
            assertEquals(expected = "0.5", actual = actions.last(), message = "$actions")
            assertEquals(expected = 0.5f, actual = testProgressDrawable.progress)
        }

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = false,
                hiddenWhenCompleted = true,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = 0.75f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            checkElements(actions)
            assertTrue(actual = actions.size >= 5, message = "$actions")
            assertTrue(actual = actions.first().toFloat() >= 0.0f, message = "$actions")
            assertEquals(expected = "0.8", actual = actions.last(), message = "$actions")
            assertEquals(expected = 0.8f, actual = testProgressDrawable.progress)
        }

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = false,
                hiddenWhenCompleted = true,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = 1f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            assertTrue(actual = actions.isEmpty(), message = "$actions")
            assertEquals(expected = 1f, actual = testProgressDrawable.progress)
        }

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = false,
                hiddenWhenCompleted = true,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = 0.2f
                testProgressDrawable.progress = 1f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            checkElements(actions)
            assertTrue(actual = actions.size >= 5, message = "$actions")
            assertTrue(actual = actions.first().toFloat() >= 0.0f, message = "$actions")
            assertEquals(expected = "1.0", actual = actions.last(), message = "$actions")
            assertEquals(expected = 1f, actual = testProgressDrawable.progress)
        }

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = false,
                hiddenWhenCompleted = true,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = -1.25f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            assertTrue(actual = actions.isEmpty(), message = "$actions")
            assertEquals(expected = -1f, actual = testProgressDrawable.progress)
        }

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = false,
                hiddenWhenCompleted = true,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = 1f
                testProgressDrawable.progress = 1.2f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            assertTrue(actual = actions.isEmpty(), message = "$actions")
            assertEquals(expected = 1f, actual = testProgressDrawable.progress)
        }
    }

    @Test
    fun testProgress2() = runTest {
        // It was found that the test can only be completed stably on Android 8.0, so.
        // Maybe you can change the test plan when View supports screenshot testing.
        if (VERSION.SDK_INT != VERSION_CODES.O) return@runTest

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = true,
                hiddenWhenCompleted = false,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = 0.25f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            checkElements(actions)
            assertTrue(actual = actions.size >= 5, message = "$actions")
            assertTrue(actual = actions.first().toFloat() >= 0.0f, message = "$actions")
            assertEquals(expected = "0.2", actual = actions.last(), message = "$actions")
            assertEquals(expected = 0.2f, actual = testProgressDrawable.progress)
        }

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = true,
                hiddenWhenCompleted = false,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = 0.5f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            checkElements(actions)
            assertTrue(actual = actions.size >= 5, message = "$actions")
            assertTrue(actual = actions.first().toFloat() >= 0.0f, message = "$actions")
            assertEquals(expected = "0.5", actual = actions.last(), message = "$actions")
            assertEquals(expected = 0.5f, actual = testProgressDrawable.progress)
        }

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = true,
                hiddenWhenCompleted = false,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = 0.75f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            checkElements(actions)
            assertTrue(actual = actions.size >= 5, message = "$actions")
            assertTrue(actual = actions.first().toFloat() >= 0.0f, message = "$actions")
            assertEquals(expected = "0.8", actual = actions.last(), message = "$actions")
            assertEquals(expected = 0.8f, actual = testProgressDrawable.progress)
        }

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = true,
                hiddenWhenCompleted = false,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = 1f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            assertTrue(actual = actions.size >= 5, message = "$actions")
            assertTrue(actual = actions.first().toFloat() >= 0.0f, message = "$actions")
            assertEquals(expected = "1.0", actual = actions.last(), message = "$actions")
            assertEquals(expected = 1f, actual = testProgressDrawable.progress)
        }

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = true,
                hiddenWhenCompleted = false,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = 0.2f
                testProgressDrawable.progress = 1f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            checkElements(actions)
            assertTrue(actual = actions.size >= 5, message = "$actions")
            assertTrue(actual = actions.first().toFloat() >= 0.0f, message = "$actions")
            assertEquals(expected = "1.0", actual = actions.last(), message = "$actions")
            assertEquals(expected = 1f, actual = testProgressDrawable.progress)
        }

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = true,
                hiddenWhenCompleted = false,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = -1.25f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            assertTrue(actual = actions.isEmpty(), message = "$actions")
            assertEquals(expected = -1f, actual = testProgressDrawable.progress)
        }

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = true,
                hiddenWhenCompleted = false,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = 1.2f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            checkElements(actions)
            assertTrue(actual = actions.size >= 5, message = "$actions")
            assertTrue(actual = actions.first().toFloat() >= 0.0f, message = "$actions")
            assertEquals(expected = "1.0", actual = actions.last(), message = "$actions")
            assertEquals(expected = 1f, actual = testProgressDrawable.progress)
        }
    }

    @Test
    fun testSetVisible() = runTest {
        // It was found that the test can only be completed stably on Android 8.0, so.
        // Maybe you can change the test plan when View supports screenshot testing.
        if (VERSION.SDK_INT != VERSION_CODES.O) return@runTest

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = true,
                hiddenWhenCompleted = false,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = 0.2f
                testProgressDrawable.progress = 1f
            }
            block(200)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            assertTrue(actual = actions.size >= 5, message = "$actions")
            assertTrue(actual = actions.first().toFloat() >= 0.0f, message = "$actions")
            assertEquals(expected = "1.0", actual = actions.last(), message = "$actions")
            assertEquals(expected = 1f, actual = testProgressDrawable.progress)
        }

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()
            val testProgressDrawable = TestProgressDrawable(
                size = Size(100, 100),
                hiddenWhenIndeterminate = true,
                hiddenWhenCompleted = false,
                stepAnimationDuration = 150,
            )
            withContext(Dispatchers.Main) {
                val imageView = ImageView(activity).apply {
                    setImageDrawable(testProgressDrawable)
                }
                activity.setContentView(imageView)
                testProgressDrawable.progress = 1f
            }
            block(50)
            withContext(Dispatchers.Main) {
                testProgressDrawable.setVisible(visible = false, restart = false)
            }
            block(50)

            val actions = testProgressDrawable.drawProgressHistory.distinct()
            assertTrue(actual = actions.size in 0.until(5), message = "$actions")
            assertTrue(actual = actions.first().toFloat() >= 0.0f, message = "$actions")
            assertEquals(expected = "1.0", actual = actions.last(), message = "$actions")
            assertEquals(expected = 1f, actual = testProgressDrawable.progress)
        }
    }

    private fun checkElements(it: List<String>) {
        it.forEachIndexed { index, _ ->
            if (index > 0) {
                assertTrue(
                    actual = it[index].toFloat() >= it[index - 1].toFloat(),
                    message = "index: $index, $it"
                )
            }
        }
    }

    class TestProgressDrawable(
        val size: Size,
        hiddenWhenIndeterminate: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
        hiddenWhenCompleted: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
        stepAnimationDuration: Int = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION,
    ) : AbsProgressDrawable(
        hiddenWhenIndeterminate = hiddenWhenIndeterminate,
        hiddenWhenCompleted = hiddenWhenCompleted,
        stepAnimationDuration = stepAnimationDuration
    ) {

        val drawProgressHistory = mutableListOf<String>()

        override fun drawProgress(canvas: Canvas, drawProgress: Float) {
            drawProgressHistory.add(drawProgress.format(2).toString())
        }

        override fun getIntrinsicWidth(): Int = size.width

        override fun getIntrinsicHeight(): Int = size.height

        override fun mutate(): ProgressDrawable {
            return this
        }

        override fun setAlpha(alpha: Int) {

        }

        override fun setColorFilter(colorFilter: ColorFilter?) {

        }

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated("Deprecated in Java")
        override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    }
}