package com.github.panpf.sketch.zoom.test.internal

import android.graphics.Matrix
import android.graphics.PointF
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.zoom.internal.getRotateDegrees
import com.github.panpf.sketch.zoom.internal.getScale
import com.github.panpf.sketch.zoom.internal.getTranslation
import com.github.panpf.sketch.zoom.internal.getValue
import com.github.panpf.tools4a.run.ktx.runOnMainThreadSync
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ZoomUtilsTest {

    @Test
    fun testMatrixGetValue() {
        Matrix().apply {
            assertThrow(IllegalStateException::class) {
                getValue(Matrix.MSKEW_X)
            }
        }

        Matrix().apply {
            runOnMainThreadSync {
                Assert.assertEquals(0f, getValue(Matrix.MSKEW_X))
                Assert.assertEquals(0f, getValue(Matrix.MSKEW_Y))
                Assert.assertEquals(1f, getValue(Matrix.MSCALE_X))
                Assert.assertEquals(1f, getValue(Matrix.MSCALE_Y))
                Assert.assertEquals(0f, getValue(Matrix.MTRANS_X))
                Assert.assertEquals(0f, getValue(Matrix.MTRANS_Y))
                Assert.assertEquals(0f, getValue(Matrix.MPERSP_0))
                Assert.assertEquals(0f, getValue(Matrix.MPERSP_1))
                Assert.assertEquals(1f, getValue(Matrix.MPERSP_2))
            }
        }

        Matrix().apply {
            setSkew(0.3f, 0.3f)
            runOnMainThreadSync {
                Assert.assertEquals(0.3f, getValue(Matrix.MSKEW_X))
                Assert.assertEquals(0.3f, getValue(Matrix.MSKEW_Y))
                Assert.assertEquals(1f, getValue(Matrix.MSCALE_X))
                Assert.assertEquals(1f, getValue(Matrix.MSCALE_Y))
                Assert.assertEquals(0f, getValue(Matrix.MTRANS_X))
                Assert.assertEquals(0f, getValue(Matrix.MTRANS_Y))
                Assert.assertEquals(0f, getValue(Matrix.MPERSP_0))
                Assert.assertEquals(0f, getValue(Matrix.MPERSP_1))
                Assert.assertEquals(1f, getValue(Matrix.MPERSP_2))
            }
        }

        Matrix().apply {
            setScale(1.5f, 1.5f, 300f, 200f)
            runOnMainThreadSync {
                Assert.assertEquals(0f, getValue(Matrix.MSKEW_X))
                Assert.assertEquals(0f, getValue(Matrix.MSKEW_Y))
                Assert.assertEquals(1.5f, getValue(Matrix.MSCALE_X))
                Assert.assertEquals(1.5f, getValue(Matrix.MSCALE_Y))
                Assert.assertEquals(-150f, getValue(Matrix.MTRANS_X))
                Assert.assertEquals(-100f, getValue(Matrix.MTRANS_Y))
                Assert.assertEquals(0f, getValue(Matrix.MPERSP_0))
                Assert.assertEquals(0f, getValue(Matrix.MPERSP_1))
                Assert.assertEquals(1f, getValue(Matrix.MPERSP_2))
            }
        }

        Matrix().apply {
            setTranslate(45f, 390f)
            runOnMainThreadSync {
                Assert.assertEquals(0f, getValue(Matrix.MSKEW_X))
                Assert.assertEquals(0f, getValue(Matrix.MSKEW_Y))
                Assert.assertEquals(1f, getValue(Matrix.MSCALE_X))
                Assert.assertEquals(1f, getValue(Matrix.MSCALE_Y))
                Assert.assertEquals(45f, getValue(Matrix.MTRANS_X))
                Assert.assertEquals(390f, getValue(Matrix.MTRANS_Y))
                Assert.assertEquals(0f, getValue(Matrix.MPERSP_0))
                Assert.assertEquals(0f, getValue(Matrix.MPERSP_1))
                Assert.assertEquals(1f, getValue(Matrix.MPERSP_2))
            }
        }

        Matrix().apply {
            setRotate(110f, 46f, 240f)
            runOnMainThreadSync {
                Assert.assertEquals(-0.9396926f, getValue(Matrix.MSKEW_X))
                Assert.assertEquals(0.9396926f, getValue(Matrix.MSKEW_Y))
                Assert.assertEquals(-0.34202012f, getValue(Matrix.MSCALE_X))
                Assert.assertEquals(-0.34202012f, getValue(Matrix.MSCALE_Y))
                Assert.assertEquals(287.25916f, getValue(Matrix.MTRANS_X))
                Assert.assertEquals(278.85898f, getValue(Matrix.MTRANS_Y))
                Assert.assertEquals(0f, getValue(Matrix.MPERSP_0))
                Assert.assertEquals(0f, getValue(Matrix.MPERSP_1))
                Assert.assertEquals(1f, getValue(Matrix.MPERSP_2))
            }
        }
    }

    @Test
    fun testMatrixGetScale() {
        Matrix().apply {
            assertThrow(IllegalStateException::class) {
                getScale()
            }
        }

        Matrix().apply {
            runOnMainThreadSync {
                Assert.assertEquals(1f, getScale())
            }
        }

        Matrix().apply {
            setScale(1.5f, 1.2f)
            runOnMainThreadSync {
                Assert.assertEquals(1.5f, getScale())
            }
        }
    }

    @Test
    fun testMatrixGetRotateDegrees() {
        Matrix().apply {
            assertThrow(IllegalStateException::class) {
                getRotateDegrees()
            }
        }

        Matrix().apply {
            runOnMainThreadSync {
                Assert.assertEquals(0, getRotateDegrees())
            }
        }

        Matrix().apply {
            setRotate(110f)
            runOnMainThreadSync {
                Assert.assertEquals(110, getRotateDegrees())
            }
        }
    }

    @Test
    fun testMatrixGetTranslation() {
        Matrix().apply {
            assertThrow(IllegalStateException::class) {
                getTranslation(PointF())
            }
        }

        Matrix().apply {
            runOnMainThreadSync {
                Assert.assertEquals(
                    "PointF(0.0, 0.0)",
                    PointF().apply { getTranslation(this) }.toString()
                )
            }
        }

        Matrix().apply {
            setTranslate(267f, 462f)
            runOnMainThreadSync {
                Assert.assertEquals(
                    "PointF(267.0, 462.0)",
                    PointF().apply { getTranslation(this) }.toString()
                )
            }
        }
    }
}