package com.github.panpf.sketch.compose.core.test.state

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.compose.state.rememberIconPainterStateImage
import com.github.panpf.sketch.state.IntColor
import com.github.panpf.sketch.util.Size
import org.junit.Test

class IconPainterStateImageTest {

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
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTine
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTine
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            iconTint = resIconTine
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            iconTint = intIconTine
        )

        rememberIconPainterStateImage(
            icon = drawableIcon,
        )

        // icon res, background res
        val resIcon = androidx.core.R.drawable.ic_call_answer
        val resBackground = androidx.core.R.drawable.notification_template_icon_bg
        rememberIconPainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconPainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTine
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTine
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconPainterStateImage(
            icon = resIcon,
            background = resBackground,
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            iconTint = resIconTine
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            iconTint = intIconTine
        )

        rememberIconPainterStateImage(
            icon = resIcon,
        )

        // icon drawable, background int color
        val intColorBackground = IntColor(Color.BLUE)
        rememberIconPainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconPainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTine
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTine
        )

        rememberIconPainterStateImage(
            icon = resIcon,
            background = intColorBackground,
        )
    }

}