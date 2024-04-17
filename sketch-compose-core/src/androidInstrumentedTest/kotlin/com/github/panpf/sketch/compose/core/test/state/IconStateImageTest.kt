package com.github.panpf.sketch.compose.core.test.state

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.compose.state.rememberIconStateImage
import com.github.panpf.sketch.state.IntColor
import com.github.panpf.sketch.util.Size
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IconStateImageTest {

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
        rememberIconStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTine
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTine
        )
        rememberIconStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconStateImage(
            icon = drawableIcon,
            background = drawableBackground,
        )
        rememberIconStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
        )
        rememberIconStateImage(
            icon = drawableIcon,
            iconTint = resIconTine
        )
        rememberIconStateImage(
            icon = drawableIcon,
            iconTint = intIconTine
        )

        rememberIconStateImage(
            icon = drawableIcon,
        )

        // icon res, background res
        val resIcon = androidx.core.R.drawable.ic_call_answer
        val resBackground = androidx.core.R.drawable.notification_template_icon_bg
        rememberIconStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTine
        )
        rememberIconStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTine
        )
        rememberIconStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconStateImage(
            icon = resIcon,
            background = resBackground,
        )
        rememberIconStateImage(
            icon = resIcon,
            iconSize = iconSize,
        )
        rememberIconStateImage(
            icon = resIcon,
            iconTint = resIconTine
        )
        rememberIconStateImage(
            icon = resIcon,
            iconTint = intIconTine
        )

        rememberIconStateImage(
            icon = resIcon,
        )

        // icon drawable, background int color
        val intColorBackground = IntColor(Color.BLUE)
        rememberIconStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        rememberIconStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        rememberIconStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )
        rememberIconStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTine
        )
        rememberIconStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTine
        )

        rememberIconStateImage(
            icon = resIcon,
            background = intColorBackground,
        )
    }
}