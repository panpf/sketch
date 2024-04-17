package com.github.panpf.sketch.compose.core.test.painter

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.compose.painter.rememberIconPainter
import com.github.panpf.sketch.state.IntColor
import com.github.panpf.sketch.util.Size
import org.junit.Test

class IconPainterTest {

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
        rememberIconPainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconPainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTine
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTine
        )
        rememberIconPainter(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconPainter(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconPainter(
            icon = drawableIcon,
            background = drawableBackground,
        )
        rememberIconPainter(
            icon = drawableIcon,
            iconSize = iconSize,
        )
        rememberIconPainter(
            icon = drawableIcon,
            iconTint = resIconTine
        )
        rememberIconPainter(
            icon = drawableIcon,
            iconTint = intIconTine
        )

        rememberIconPainter(
            icon = drawableIcon,
        )

        // icon res, background res
        val resIcon = androidx.core.R.drawable.ic_call_answer
        val resBackground = androidx.core.R.drawable.notification_template_icon_bg
        rememberIconPainter(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconPainter(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconPainter(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconPainter(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTine
        )
        rememberIconPainter(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTine
        )
        rememberIconPainter(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconPainter(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconPainter(
            icon = resIcon,
            background = resBackground,
        )
        rememberIconPainter(
            icon = resIcon,
            iconSize = iconSize,
        )
        rememberIconPainter(
            icon = resIcon,
            iconTint = resIconTine
        )
        rememberIconPainter(
            icon = resIcon,
            iconTint = intIconTine
        )

        rememberIconPainter(
            icon = resIcon,
        )

        // icon drawable, background int color
        val intColorBackground = IntColor(Color.BLUE)
        rememberIconPainter(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconPainter(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconPainter(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )
        rememberIconPainter(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTine
        )
        rememberIconPainter(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTine
        )

        rememberIconPainter(
            icon = resIcon,
            background = intColorBackground,
        )
    }

}