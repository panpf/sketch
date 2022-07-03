package com.github.panpf.sketch.test.target

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.transition.TransitionDisplayTarget
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransitionDisplayTargetTest {

    @Test
    fun test() {
        val context = getTestContext()
        TestTransitionViewDisplayTarget(ImageView(context)).apply {
            onStart(null)
            onError(null)
            onSuccess(ColorDrawable(Color.RED))
        }
    }

    class TestTransitionViewDisplayTarget(private val view: ImageView) : TransitionDisplayTarget {
        override var drawable: Drawable?
            get() = view.drawable
            set(value) {
                view.setImageDrawable(value)
            }
    }
}