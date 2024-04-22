package com.github.panpf.sketch.compose.core.test.state

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.state.rememberIconStateImage
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.asEquality
import com.github.panpf.sketch.util.Size
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IconStateImageTest {

    @Test
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
        rememberIconStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconStateImage(
            icon = drawableIcon,
            background = drawableBackground,
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = resBackground,
        )
        rememberIconStateImage(
            icon = drawableIcon,
            background = intColorBackground,
        )

        rememberIconStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
        )

        rememberIconStateImage(
            icon = drawableIcon,
            iconTint = resIconTint
        )
        rememberIconStateImage(
            icon = drawableIcon,
            iconTint = intIconTint
        )

        rememberIconStateImage(
            icon = drawableIcon,
        )

        // res icon
        rememberIconStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        rememberIconStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        rememberIconStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        rememberIconStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        rememberIconStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        rememberIconStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        rememberIconStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        rememberIconStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        rememberIconStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        rememberIconStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        rememberIconStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        rememberIconStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        rememberIconStateImage(
            icon = resIcon,
            background = drawableBackground,
        )
        rememberIconStateImage(
            icon = resIcon,
            background = resBackground,
        )
        rememberIconStateImage(
            icon = resIcon,
            background = intColorBackground,
        )

        rememberIconStateImage(
            icon = resIcon,
            iconSize = iconSize,
        )

        rememberIconStateImage(
            icon = resIcon,
            iconTint = resIconTint
        )
        rememberIconStateImage(
            icon = resIcon,
            iconTint = intIconTint
        )

        rememberIconStateImage(
            icon = resIcon,
        )
    }
}