package com.github.panpf.sketch.compose.core.test.state

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.compose.state.rememberIconAnimatablePainterStateImage
import com.github.panpf.sketch.state.IntColor
import com.github.panpf.sketch.util.Size
import org.junit.Test

class IconAnimatablePainterStateImageTest {

    @Test
    @Composable
    fun CreateFunctionTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val iconSize = Size(100, 100)
        val intIconTine = IntColor(Color.GREEN)
        val resIconTine = android.R.color.black

        // icon drawable, background drawable
        val drawableIcon = context.getDrawable(androidx.core.R.drawable.ic_call_decline)!!
        val drawableBackground = context.getDrawable(androidx.core.R.drawable.notification_bg)
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTine
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainterStateImage(
            icon = drawableIcon,
        )

        // icon res, background res
        val resIcon = androidx.core.R.drawable.ic_call_answer
        val resBackground = androidx.core.R.drawable.notification_template_icon_bg
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTine
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = resBackground,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
        )

        // icon drawable, background int color
        val intColorBackground = IntColor(Color.BLUE)
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainterStateImage(
            icon = resIcon,
            background = intColorBackground,
        )
    }

}