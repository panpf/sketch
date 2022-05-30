package com.github.panpf.sketch.viewability.test

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.widget.ImageView.ScaleType
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.viewability.AbsAbilityImageView
import com.github.panpf.sketch.viewability.Host
import com.github.panpf.sketch.viewability.ImageMatrixObserver
import com.github.panpf.sketch.viewability.ScaleTypeObserver
import com.github.panpf.sketch.viewability.ViewAbility
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RealViewAbilityManagerTest {

    @Test
    fun testOnlyLimit() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val view = TestAbilityImageView(context)

        view.addViewAbility(TestScaleTypeViewAbility())
        assertThrow(IllegalArgumentException::class) {
            view.addViewAbility(TestScaleTypeViewAbility())
        }

        view.addViewAbility(TestImageMatrixViewAbility())
        assertThrow(IllegalArgumentException::class) {
            view.addViewAbility(TestImageMatrixViewAbility())
        }
    }

    @Test
    fun testAddAndRemove() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val view = TestAbilityImageView(context)
        Assert.assertEquals(0, view.viewAbilityList.size)

        val scaleTypeViewAbility = TestScaleTypeViewAbility()
        val imageMatrixViewAbility = TestImageMatrixViewAbility()

        view.addViewAbility(scaleTypeViewAbility)
        Assert.assertEquals(1, view.viewAbilityList.size)

        view.addViewAbility(imageMatrixViewAbility)
        Assert.assertEquals(2, view.viewAbilityList.size)

        view.removeViewAbility(scaleTypeViewAbility)
        Assert.assertEquals(1, view.viewAbilityList.size)

        view.removeViewAbility(imageMatrixViewAbility)
        Assert.assertEquals(0, view.viewAbilityList.size)
    }

    class TestAbilityImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
    ) : AbsAbilityImageView(context, attrs) {
        override fun submitRequest(request: DisplayRequest) {
            context.sketch.enqueue(request)
        }
    }

    class TestScaleTypeViewAbility : ViewAbility, ScaleTypeObserver {
        override var host: Host? = null

        override fun setScaleType(scaleType: ScaleType): Boolean {
            return false
        }

        override fun getScaleType(): ScaleType? {
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