package com.github.panpf.sketch.compose.core.android.test.state

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.state.rememberIconAnimatablePainterStateImage
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asEquality

class IconAnimatablePainterStateImageAndroidTest {
    // TODO test

    @Composable
    fun CreateFunctionTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val drawableIcon = androidx.core.R.drawable.ic_call_decline.let {
            context.getDrawable(it)!!.asEquality(it)
        }
        val resIcon = androidx.core.R.drawable.ic_call_answer
        val drawableBackground = androidx.core.R.drawable.notification_bg.let {
            context.getDrawable(it)!!.asEquality(it)
        }
        val resBackground = androidx.core.R.drawable.notification_template_icon_bg
        val intColorBackground = IntColor(Color.BLUE)
        val iconSize = Size(100, 100)
        val intIconTint = IntColor(Color.GREEN)
        val resIconTint = android.R.color.black

        // drawable icon
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = resBackground,
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = intColorBackground,
        )

        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
        )

        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
        )

        // res icon
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = drawableBackground,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = resBackground,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = intColorBackground,
        )

        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            iconSize = iconSize,
        )

        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
        )
    }

}