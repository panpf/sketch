package com.github.panpf.sketch.sample.ui.base

import android.view.Window

// todo Complete WindowInsetStyle
fun setupWindowInsetStyle(window: Window, windowInsetStyle: WindowInsetStyle) {
    WindowInsetStyleApplier.get(window, windowInsetStyle).apply()
}

/**
 * 一个配置当前窗口的 Inset 的样式，支持 状态栏和导航栏
 */

sealed interface WindowInsetStyle {

    // todo 支持 model
    // todo 支持 behavior
    data object FullScreen : WindowInsetStyle

    enum class FullScreenMode {
        // systemBars
        // statusBars
        // navigationBars
    }

    data class NonFullScreen(
        val statusBarMode: Mode,
        val statusBarStyle: Style,
//    val navigationBarStyle: Style
//    val navigationBarMode: Mode
        // fullScreen
    ) : WindowInsetStyle {

        enum class Mode {
            /**
             * 状态栏和内容竖向排列
             */
            Linear,

            /**
             * 状态栏悬浮在内容上
             */
            Floating
        }

        data class Style(
            val backgroundColor: Int? = null,
            val textColor: TextColor? = null,
        ) {
            sealed interface TextColor {
                data object Black : TextColor
                data object White : TextColor
                data object DynamicWithWindowBackground : TextColor
                data object DynamicWithBackgroundColor : TextColor
                class DynamicWithColor(val color: Int) : TextColor
            }
        }
    }
}
