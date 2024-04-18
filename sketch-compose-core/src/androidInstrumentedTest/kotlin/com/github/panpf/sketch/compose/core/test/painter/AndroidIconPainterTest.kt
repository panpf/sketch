package com.github.panpf.sketch.compose.core.test.painter

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.compose.painter.rememberIconPainter
import com.github.panpf.sketch.state.IntColor
import com.github.panpf.sketch.util.Size
import org.junit.Test

class AndroidIconPainterTest {

    @Test
    @Composable
    fun CreateFunctionTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val drawableIcon = context.getDrawable(androidx.core.R.drawable.ic_call_decline)!!
        val resIcon = androidx.core.R.drawable.ic_call_answer
        val drawableBackground = context.getDrawable(androidx.core.R.drawable.notification_bg)!!
        val resBackground = androidx.core.R.drawable.notification_template_icon_bg
        val intColorBackground = IntColor(Color.BLUE)
        val iconSize = Size(100, 100)
        val intIconTint = IntColor(Color.GREEN)
        val resIconTint = android.R.color.black

        // drawable icon
        rememberIconPainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconPainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconPainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconPainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconPainter(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainter(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconPainter(
            icon = drawableIcon,
            background = drawableBackground,
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = resBackground,
        )
        rememberIconPainter(
            icon = drawableIcon,
            background = intColorBackground,
        )

        rememberIconPainter(
            icon = drawableIcon,
            iconSize = iconSize,
        )

        rememberIconPainter(
            icon = drawableIcon,
            iconTint = resIconTint
        )
        rememberIconPainter(
            icon = drawableIcon,
            iconTint = intIconTint
        )

        rememberIconPainter(
            icon = drawableIcon,
        )

        // res icon
        rememberIconPainter(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainter(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainter(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainter(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconPainter(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconPainter(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconPainter(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconPainter(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconPainter(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconPainter(
            icon = resIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconPainter(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconPainter(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconPainter(
            icon = resIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconPainter(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconPainter(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconPainter(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainter(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconPainter(
            icon = resIcon,
            background = drawableBackground,
        )
        rememberIconPainter(
            icon = resIcon,
            background = resBackground,
        )
        rememberIconPainter(
            icon = resIcon,
            background = intColorBackground,
        )

        rememberIconPainter(
            icon = resIcon,
            iconSize = iconSize,
        )

        rememberIconPainter(
            icon = resIcon,
            iconTint = resIconTint
        )
        rememberIconPainter(
            icon = resIcon,
            iconTint = intIconTint
        )

        rememberIconPainter(
            icon = resIcon,
        )
    }
}