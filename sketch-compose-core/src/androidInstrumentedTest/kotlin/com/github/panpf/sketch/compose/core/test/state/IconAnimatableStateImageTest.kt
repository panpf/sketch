package com.github.panpf.sketch.compose.core.test.state

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.compose.state.rememberIconAnimatableStateImage
import com.github.panpf.sketch.state.IntColor
import com.github.panpf.sketch.util.Size
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IconAnimatableStateImageTest {

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
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTine
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTine
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            iconTint = resIconTine
        )
        rememberIconAnimatableStateImage(
            icon = drawableIcon,
            iconTint = intIconTine
        )

        rememberIconAnimatableStateImage(
            icon = drawableIcon,
        )

        // icon res, background res
        val resIcon = androidx.core.R.drawable.ic_call_answer
        val resBackground = androidx.core.R.drawable.notification_template_icon_bg
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTine
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTine
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            iconSize = iconSize,
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            iconTint = resIconTine
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            iconTint = intIconTine
        )

        rememberIconAnimatableStateImage(
            icon = resIcon,
        )

        // icon drawable, background int color
        val intColorBackground = IntColor(Color.BLUE)
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTine
        )
        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTine
        )

        rememberIconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
        )
    }
}