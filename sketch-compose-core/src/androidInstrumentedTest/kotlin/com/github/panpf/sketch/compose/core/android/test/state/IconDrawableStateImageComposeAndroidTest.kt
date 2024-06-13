package com.github.panpf.sketch.compose.core.android.test.state

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.state.rememberIconDrawableStateImage
import com.github.panpf.sketch.drawable.IntColor
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.drawable.asEquality

class IconDrawableStateImageComposeAndroidTest {
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
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconDrawableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
        )

        rememberIconDrawableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
        )

        rememberIconDrawableStateImage(
            icon = drawableIcon,
            iconTint = resIconTint
        )
        rememberIconDrawableStateImage(
            icon = drawableIcon,
            iconTint = intIconTint
        )

        rememberIconDrawableStateImage(
            icon = drawableIcon,
        )

        // res icon
        rememberIconDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconDrawableStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            background = resBackground,
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
        )

        rememberIconDrawableStateImage(
            icon = resIcon,
            iconSize = iconSize,
        )

        rememberIconDrawableStateImage(
            icon = resIcon,
            iconTint = resIconTint
        )
        rememberIconDrawableStateImage(
            icon = resIcon,
            iconTint = intIconTint
        )

        rememberIconDrawableStateImage(
            icon = resIcon,
        )
    }
}