package com.github.panpf.sketch.compose.core.android.test.state

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.state.rememberIconAnimatableStateImage
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asEquality
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IconAnimatableStateImageRememberTest {
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
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = resBackground,
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
        )

        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
        )

        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            iconTint = resIconTint
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            iconTint = intIconTint
        )

        rememberIconAnimatableStateImage(
            icon = drawableIcon,
        )

        // res icon
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconAnimatableStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = drawableBackground,
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
        )

        rememberIconAnimatableStateImage(
            icon = resIcon,
            iconSize = iconSize,
        )

        rememberIconAnimatableStateImage(
            icon = resIcon,
            iconTint = resIconTint
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            iconTint = intIconTint
        )

        rememberIconAnimatableStateImage(
            icon = resIcon,
        )
    }
}