package com.github.panpf.sketch.extensions.view.ability.test.internal

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.ability.AbsAbilityImageView
import com.github.panpf.sketch.ability.Host
import com.github.panpf.sketch.ability.ImageMatrixObserver
import com.github.panpf.sketch.ability.ScaleTypeObserver
import com.github.panpf.sketch.ability.ViewAbility
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.RequestState
import com.github.panpf.sketch.request.internal.Listeners
import com.github.panpf.sketch.request.internal.ProgressListeners
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@RunWith(AndroidJUnit4::class)
class RealViewAbilityManagerTest {

    @Test
    fun testOnlyLimit() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val view = TestAbilityImageView(context)

        view.addViewAbility(TestScaleTypeViewAbility())
        assertFailsWith(IllegalArgumentException::class) {
            view.addViewAbility(TestScaleTypeViewAbility())
        }

        view.addViewAbility(TestImageMatrixViewAbility())
        assertFailsWith(IllegalArgumentException::class) {
            view.addViewAbility(TestImageMatrixViewAbility())
        }
    }

    @Test
    fun testAddAndRemove() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val view = TestAbilityImageView(context)
        assertEquals(0, view.viewAbilityList.size)

        val scaleTypeViewAbility = TestScaleTypeViewAbility()
        val imageMatrixViewAbility = TestImageMatrixViewAbility()

        view.addViewAbility(scaleTypeViewAbility)
        assertEquals(1, view.viewAbilityList.size)

        view.addViewAbility(imageMatrixViewAbility)
        assertEquals(2, view.viewAbilityList.size)

        view.removeViewAbility(scaleTypeViewAbility)
        assertEquals(1, view.viewAbilityList.size)

        view.removeViewAbility(imageMatrixViewAbility)
        assertEquals(0, view.viewAbilityList.size)
    }

    class TestAbilityImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
    ) : AbsAbilityImageView(context, attrs) {

        override val requestState = RequestState()

        override fun getListener(): Listener {
            return Listeners(listOfNotNull(super.getListener(), requestState))
        }

        override fun getProgressListener(): ProgressListener {
            return ProgressListeners(listOfNotNull(super.getProgressListener(), requestState))
        }
    }

    class TestScaleTypeViewAbility : ViewAbility, ScaleTypeObserver {
        override var host: Host? = null

        override fun setScaleType(scaleType: ImageView.ScaleType): Boolean {
            return false
        }

        override fun getScaleType(): ImageView.ScaleType? {
            return null
        }
    }

    class TestImageMatrixViewAbility : ViewAbility, ImageMatrixObserver {
        override var host: Host? = null

        override fun setImageMatrix(imageMatrix: Matrix?): Boolean {
            return false
        }

        override fun getImageMatrix(): Matrix? {
            return null
        }
    }
}