package com.github.panpf.sketch.compose.core.android.test.painter

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.painter.rememberIconAnimatablePainter
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.asEquality
import com.github.panpf.sketch.util.Size

class IconAnimatablePainterAndroidTest {
    // TODO test

    @Composable
    fun CreateFunctionTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val drawableIcon = androidx.core.R.drawable.ic_call_decline.let { context.getDrawable(it)!!.asEquality(it)}
        val resIcon = androidx.core.R.drawable.ic_call_answer
        val drawableBackground = androidx.core.R.drawable.notification_bg.let { context.getDrawable(it)!!.asEquality(it)}
        val resBackground = androidx.core.R.drawable.notification_template_icon_bg
        val intColorBackground = IntColor(Color.BLUE)
        val iconSize = Size(100, 100)
        val intIconTint = IntColor(Color.GREEN)
        val resIconTint = android.R.color.black

        // drawable icon
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainter(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = drawableBackground,
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = resBackground,
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            background = intColorBackground,
        )

        rememberIconAnimatablePainter(
            icon = drawableIcon,
            iconSize = iconSize,
        )

        rememberIconAnimatablePainter(
            icon = drawableIcon,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainter(
            icon = drawableIcon,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainter(
            icon = drawableIcon,
        )

        // res icon
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainter(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconAnimatablePainter(
            icon = resIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconAnimatablePainter(
            icon = resIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainter(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainter(
            icon = resIcon,
            background = drawableBackground,
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = resBackground,
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            background = intColorBackground,
        )

        rememberIconAnimatablePainter(
            icon = resIcon,
            iconSize = iconSize,
        )

        rememberIconAnimatablePainter(
            icon = resIcon,
            iconTint = resIconTint
        )
        rememberIconAnimatablePainter(
            icon = resIcon,
            iconTint = intIconTint
        )

        rememberIconAnimatablePainter(
            icon = resIcon,
        )
    }
}