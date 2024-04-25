package com.github.panpf.sketch.compose.core.android.test.state

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.state.rememberIconPainterStateImage
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asEquality

class IconPainterStateImageAndroidTest {
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
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconPainterStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = drawableBackground,
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = resBackground,
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            background = intColorBackground,
        )

        rememberIconPainterStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
        )

        rememberIconPainterStateImage(
            icon = drawableIcon,
            iconTint = resIconTint
        )
        rememberIconPainterStateImage(
            icon = drawableIcon,
            iconTint = intIconTint
        )

        rememberIconPainterStateImage(
            icon = drawableIcon,
        )

        // res icon
        rememberIconPainterStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconPainterStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconPainterStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconPainterStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconPainterStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconPainterStateImage(
            icon = resIcon,
            background = drawableBackground,
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = resBackground,
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            background = intColorBackground,
        )

        rememberIconPainterStateImage(
            icon = resIcon,
            iconSize = iconSize,
        )

        rememberIconPainterStateImage(
            icon = resIcon,
            iconTint = resIconTint
        )
        rememberIconPainterStateImage(
            icon = resIcon,
            iconTint = intIconTint
        )

        rememberIconPainterStateImage(
            icon = resIcon,
        )
    }
}