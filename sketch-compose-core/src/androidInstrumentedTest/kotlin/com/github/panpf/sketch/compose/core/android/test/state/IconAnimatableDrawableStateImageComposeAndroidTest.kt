package com.github.panpf.sketch.compose.core.android.test.state

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.drawable.asEquality
import com.github.panpf.sketch.state.rememberIconAnimatableDrawableStateImage
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.Size

class IconAnimatableDrawableStateImageComposeAndroidTest {
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
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
        )

        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
        )

        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            iconTint = resIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
            iconTint = intIconTint
        )

        rememberIconAnimatableDrawableStateImage(
            icon = drawableIcon,
        )

        // res icon
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = resBackground,
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
        )

        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            iconSize = iconSize,
        )

        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            iconTint = resIconTint
        )
        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
            iconTint = intIconTint
        )

        rememberIconAnimatableDrawableStateImage(
            icon = resIcon,
        )
    }
}