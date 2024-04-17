package com.github.panpf.sketch.compose.core.test.painter

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.compose.painter.rememberIconAnimatablePainter
import com.github.panpf.sketch.state.IntColor
import com.github.panpf.sketch.util.Size
import org.junit.Test

class IconAnimatablePainterTest {

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
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTine
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = drawableBackground,
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainter(
            icon = drawableIcon,
        )

        // icon res, background res
        val resIcon = androidx.core.R.drawable.ic_call_answer
        val resBackground = androidx.core.R.drawable.notification_template_icon_bg
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainter(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTine
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainter(
            icon = resIcon,
            background = resBackground,
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainter(
            icon = resIcon,
        )

        // icon drawable, background int color
        val intColorBackground = IntColor(Color.BLUE)
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainter(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTine
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTine
        )

        rememberIconAnimatablePainter(
            icon = resIcon,
            background = intColorBackground,
        )
    }

}