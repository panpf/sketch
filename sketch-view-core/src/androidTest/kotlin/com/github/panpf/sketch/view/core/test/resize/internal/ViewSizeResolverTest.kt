package com.github.panpf.sketch.view.core.test.resize.internal

import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.updatePadding
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.internal.RealViewSizeResolver
import com.github.panpf.sketch.resize.internal.ViewSizeResolver
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toHexString
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ViewSizeResolverTest {

    @Test
    fun testViewSizeResolver() {
        val context = getTestContext()
        val imageView = ImageView(context)
        ViewSizeResolver(imageView).apply {
            assertTrue(this is RealViewSizeResolver)
            assertTrue(subtractPadding)
            assertTrue(view === imageView)
        }

        ViewSizeResolver(imageView, subtractPadding = false).apply {
            assertFalse(subtractPadding)
        }
    }

    @Test
    fun testSizeAttached() = runTest {
        // Match parent, Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            }
            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView)
                })
            }
            block(100)
            assertEquals(LayoutParams.MATCH_PARENT, imageView.layoutParams.width)
            assertEquals(LayoutParams.MATCH_PARENT, imageView.layoutParams.height)

            val size = withContext(Dispatchers.Main) {
                ViewSizeResolver(imageView).size()
            }
            assertEquals(
                expected = Size(imageView.width, imageView.height),
                actual = size
            )
        }

        // Wrap content, Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity).apply {
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            }
            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView)
                })
            }
            block(100)
            assertEquals(LayoutParams.WRAP_CONTENT, imageView.layoutParams.width)
            assertEquals(LayoutParams.WRAP_CONTENT, imageView.layoutParams.height)

            val size = withContext(Dispatchers.Main) {
                ViewSizeResolver(imageView).size()
            }
            val displayMetrics = activity.resources.displayMetrics
            assertEquals(
                expected = Size(displayMetrics.widthPixels, displayMetrics.heightPixels),
                actual = size
            )
        }

        // Match and Wrap, Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            }
            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView)
                })
            }
            block(100)
            assertEquals(LayoutParams.MATCH_PARENT, imageView.layoutParams.width)
            assertEquals(LayoutParams.WRAP_CONTENT, imageView.layoutParams.height)

            val size = withContext(Dispatchers.Main) {
                ViewSizeResolver(imageView).size()
            }
            val displayMetrics = activity.resources.displayMetrics
            assertEquals(
                expected = Size(imageView.width, displayMetrics.heightPixels),
                actual = size
            )

            val imageView2 = ImageView(activity).apply {
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            }
            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView2)
                })
            }
            block(100)
            assertEquals(LayoutParams.WRAP_CONTENT, imageView2.layoutParams.width)
            assertEquals(LayoutParams.MATCH_PARENT, imageView2.layoutParams.height)

            val size2 = withContext(Dispatchers.Main) {
                ViewSizeResolver(imageView2).size()
            }
            assertEquals(
                expected = Size(displayMetrics.widthPixels, imageView2.height),
                actual = size2
            )
        }

        // Fixed Size, Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity).apply {
                layoutParams = LayoutParams(101, 202)
            }
            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView)
                })
            }
            block(100)
            assertEquals(101, imageView.layoutParams.width)
            assertEquals(202, imageView.layoutParams.height)

            val size = withContext(Dispatchers.Main) {
                ViewSizeResolver(imageView).size()
            }
            assertEquals(
                expected = Size(101, 202),
                actual = size
            )
        }

        // Padding subtract, Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity).apply {
                layoutParams = LayoutParams(500, 600)
                updatePadding(left = 40, top = 20, right = 20, bottom = 50)
            }
            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView)
                })
            }
            block(100)
            assertEquals(500, imageView.layoutParams.width)
            assertEquals(600, imageView.layoutParams.height)
            assertEquals(40, imageView.paddingLeft)
            assertEquals(20, imageView.paddingTop)
            assertEquals(20, imageView.paddingRight)
            assertEquals(50, imageView.paddingBottom)

            val size = withContext(Dispatchers.Main) {
                ViewSizeResolver(imageView).size()
            }
            assertEquals(
                expected = Size(440, 530),
                actual = size
            )
        }

        // Padding ignore, Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity).apply {
                layoutParams = LayoutParams(500, 600)
                updatePadding(left = 40, top = 20, right = 20, bottom = 50)
            }
            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView)
                })
            }
            block(100)
            assertEquals(500, imageView.layoutParams.width)
            assertEquals(600, imageView.layoutParams.height)
            assertEquals(40, imageView.paddingLeft)
            assertEquals(20, imageView.paddingTop)
            assertEquals(20, imageView.paddingRight)
            assertEquals(50, imageView.paddingBottom)

            val size = withContext(Dispatchers.Main) {
                ViewSizeResolver(imageView, subtractPadding = false).size()
            }
            assertEquals(
                expected = Size(500, 600),
                actual = size
            )
        }

        // Padding error, Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity).apply {
                layoutParams = LayoutParams(500, 600)
                updatePadding(left = 400, top = 200, right = 200, bottom = 500)
            }
            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView)
                })
            }
            block(100)
            assertEquals(500, imageView.layoutParams.width)
            assertEquals(600, imageView.layoutParams.height)
            assertEquals(400, imageView.paddingLeft)
            assertEquals(200, imageView.paddingTop)
            assertEquals(200, imageView.paddingRight)
            assertEquals(500, imageView.paddingBottom)

            val size = withContext(Dispatchers.Main) {
                ViewSizeResolver(imageView).size()
            }
            val displayMetrics = activity.resources.displayMetrics
            assertEquals(
                expected = Size(displayMetrics.widthPixels, displayMetrics.heightPixels),
                actual = size
            )
        }
    }

    @Test
    fun testSizeNoAttached() = runTest {
        // Match parent, No Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            }

            val job = async(Dispatchers.Main) {
                ViewSizeResolver(imageView).size()
            }
            block(100)

            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView)
                })
            }
            block(100)
            assertEquals(LayoutParams.MATCH_PARENT, imageView.layoutParams.width)
            assertEquals(LayoutParams.MATCH_PARENT, imageView.layoutParams.height)

            val size = job.await()
            assertEquals(
                expected = Size(imageView.width, imageView.height),
                actual = size
            )
        }

        // Wrap content, No Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity).apply {
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            }

            val job = async(Dispatchers.Main) {
                ViewSizeResolver(imageView).size()
            }
            block(100)

            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView)
                })
            }
            block(100)
            assertEquals(LayoutParams.WRAP_CONTENT, imageView.layoutParams.width)
            assertEquals(LayoutParams.WRAP_CONTENT, imageView.layoutParams.height)

            val size = job.await()
            val displayMetrics = activity.resources.displayMetrics
            assertEquals(
                expected = Size(displayMetrics.widthPixels, displayMetrics.heightPixels),
                actual = size
            )
        }

        // Match and Wrap, No Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            }

            val job = async(Dispatchers.Main) {
                ViewSizeResolver(imageView).size()
            }
            block(100)

            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView)
                })
            }
            block(100)
            assertEquals(LayoutParams.MATCH_PARENT, imageView.layoutParams.width)
            assertEquals(LayoutParams.WRAP_CONTENT, imageView.layoutParams.height)

            val size = job.await()
            val displayMetrics = activity.resources.displayMetrics
            assertEquals(
                expected = Size(imageView.width, displayMetrics.heightPixels),
                actual = size
            )

            val imageView2 = ImageView(activity).apply {
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            }

            val job2 = async(Dispatchers.Main) {
                ViewSizeResolver(imageView2).size()
            }
            block(100)

            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView2)
                })
            }
            block(100)
            assertEquals(LayoutParams.WRAP_CONTENT, imageView2.layoutParams.width)
            assertEquals(LayoutParams.MATCH_PARENT, imageView2.layoutParams.height)

            val size2 = job2.await()
            assertEquals(
                expected = Size(displayMetrics.widthPixels, imageView2.height),
                actual = size2
            )
        }

        // Fixed Size, No Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity).apply {
                layoutParams = LayoutParams(101, 202)
            }

            val job = async(Dispatchers.Main) {
                ViewSizeResolver(imageView).size()
            }
            block(100)

            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView)
                })
            }
            block(100)
            assertEquals(101, imageView.layoutParams.width)
            assertEquals(202, imageView.layoutParams.height)

            val size = job.await()
            assertEquals(
                expected = Size(101, 202),
                actual = size
            )
        }

        // Padding subtract, No Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity).apply {
                layoutParams = LayoutParams(500, 600)
                updatePadding(left = 40, top = 20, right = 20, bottom = 50)
            }

            val job = async(Dispatchers.Main) {
                ViewSizeResolver(imageView).size()
            }
            block(100)

            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView)
                })
            }
            block(100)
            assertEquals(500, imageView.layoutParams.width)
            assertEquals(600, imageView.layoutParams.height)
            assertEquals(40, imageView.paddingLeft)
            assertEquals(20, imageView.paddingTop)
            assertEquals(20, imageView.paddingRight)
            assertEquals(50, imageView.paddingBottom)

            val size = job.await()
            assertEquals(
                expected = Size(440, 530),
                actual = size
            )
        }

        // Padding ignore, No Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity).apply {
                layoutParams = LayoutParams(500, 600)
                updatePadding(left = 40, top = 20, right = 20, bottom = 50)
            }

            val job = async(Dispatchers.Main) {
                ViewSizeResolver(imageView, subtractPadding = false).size()
            }
            block(100)

            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView)
                })
            }
            block(100)
            assertEquals(500, imageView.layoutParams.width)
            assertEquals(600, imageView.layoutParams.height)
            assertEquals(40, imageView.paddingLeft)
            assertEquals(20, imageView.paddingTop)
            assertEquals(20, imageView.paddingRight)
            assertEquals(50, imageView.paddingBottom)

            val size = job.await()
            assertEquals(
                expected = Size(500, 600),
                actual = size
            )
        }

        // Padding error, No Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity).apply {
                layoutParams = LayoutParams(500, 600)
                updatePadding(left = 400, top = 200, right = 200, bottom = 500)
            }

            val job = async(Dispatchers.Main) {
                ViewSizeResolver(imageView).size()
            }
            block(100)

            withContext(Dispatchers.Main) {
                activity.setContentView(FrameLayout(activity).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(imageView)
                })
            }
            block(100)
            assertEquals(500, imageView.layoutParams.width)
            assertEquals(600, imageView.layoutParams.height)
            assertEquals(400, imageView.paddingLeft)
            assertEquals(200, imageView.paddingTop)
            assertEquals(200, imageView.paddingRight)
            assertEquals(500, imageView.paddingBottom)

            val size = job.await()
            val displayMetrics = activity.resources.displayMetrics
            assertEquals(
                expected = Size(displayMetrics.widthPixels, displayMetrics.heightPixels),
                actual = size
            )
        }
    }

    @Test
    fun testKey() {
        val context = getTestContext()
        val imageView = ImageView(context)
        val viewKey = "${imageView::class.simpleName}@${imageView.toHexString()}"
        assertEquals(
            expected = "ViewSize($viewKey,true)",
            actual = ViewSizeResolver(imageView).key
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val imageView = ImageView(context)
        val element1 = ViewSizeResolver(imageView)
        val element11 = ViewSizeResolver(imageView)
        val element2 = ViewSizeResolver(ImageView(context))
        val element3 = ViewSizeResolver(ImageView(context), subtractPadding = false)

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = element2)
        assertNotEquals(illegal = element1, actual = element3)
        assertNotEquals(illegal = element2, actual = element3)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element2.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element3.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val imageView = ImageView(context)
        val viewKey = "${imageView::class.simpleName}@${imageView.toHexString()}"
        assertEquals(
            expected = "ViewSizeResolver(view=$viewKey, subtractPadding=true)",
            actual = ViewSizeResolver(imageView).toString()
        )
    }
}