package com.github.panpf.sketch.test.target

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.target.ViewDisplayTarget
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewDisplayTargetTest {

    @Test
    fun test() {
        val context = getTestContext()
        TestImageViewDisplayTarget(ImageView(context)).apply {
            onStart(null)
            onError(null)
            onSuccess(ColorDrawable(Color.RED))
        }
    }

    class TestImageViewDisplayTarget(override val view: ImageView) : ViewDisplayTarget<ImageView> {
        override var drawable: Drawable?
            get() = view.drawable
            set(value) {
                view.setImageDrawable(value)
            }
    }
}