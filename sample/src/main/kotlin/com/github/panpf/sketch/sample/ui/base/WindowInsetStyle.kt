package com.github.panpf.sketch.sample.ui.base

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import android.view.Window
import android.view.WindowManager

//fun unsetSystemUiFlag(systemUiFlag: Int) {
//    val decorView: View = mWindow.getDecorView()
//    decorView.systemUiVisibility = (
//            decorView.systemUiVisibility
//                    and systemUiFlag.inv())
//}

// todo Complete WindowInsetStyle
// SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：内容可以延伸到状态栏区域，但状态烂和导航栏依然显示在内容上方
@Suppress("DEPRECATION")
fun setupWindowInsetStyle(window: Window, windowInsetStyle: WindowInsetStyle) {
    if (VERSION.SDK_INT < VERSION_CODES.KITKAT) {
        return
    }
    val systemUiVisibility = window.decorView.systemUiVisibility
    if (windowInsetStyle is WindowInsetStyle.FullScreen) {
        // 全屏文档在此：
        // 新 API：https://developer.android.com/develop/ui/views/layout/immersive
        // 旧 API：https://developer.android.com/training/system-ui/immersive?hl=zh-cn
//        val systemUiVisibility = window.decorView.systemUiVisibility
//        window.decorView.systemUiVisibility =
//            systemUiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    } else if (windowInsetStyle is WindowInsetStyle.NonFullScreen) {
        val statusBarMode = windowInsetStyle.statusBarMode
        if (statusBarMode == WindowInsetStyle.NonFullScreen.Mode.Linear) {
            // todo
        } else if (statusBarMode == WindowInsetStyle.NonFullScreen.Mode.Floating) {
            window.apply {
                // 将内容延伸到状态栏下面
                decorView.systemUiVisibility =
                    systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN


                if (VERSION.SDK_INT >= VERSION_CODES.M) {

                } else if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                    // 不能设置状态栏半透明，他的优先级比 statusBarColor 高
                    val backgroundColor = windowInsetStyle.statusBarStyle.backgroundColor
                    if (backgroundColor != null) {
                        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        statusBarColor = windowInsetStyle.statusBarStyle.backgroundColor
//                    statusBarColor = Color.RED
                    }
                } else {
                    // 将状态栏背景设置为半透明（默认为黑色半透明）
                    addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                }
            }
        } else {
            throw IllegalArgumentException("Unsupported mode: $statusBarMode")
        }
    } else {
        throw IllegalArgumentException("Unsupported windowInsetStyle: $windowInsetStyle")
    }

//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        val window = window
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = Color.TRANSPARENT
//    }
//    val insetsController =
//        WindowCompat.getInsetsController(requireActivity().window, requireView())
//    val statusBarTextStyle =
//        statusBarTextStyle ?: if (requireContext().isNightMode()) White else Black
//    requireActivity().window.decorView.apply {
//        insetsController.isAppearanceLightStatusBars = statusBarTextStyle == Black
//    }
}

/**
 * 一个配置当前窗口的 Inset 的样式，支持 状态栏和导航栏
 */

sealed interface WindowInsetStyle {

    data class FullScreen(val mode: FullScreenMode) : WindowInsetStyle

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
                class DynamicWithBackgroundColor(val color: Int) : TextColor
            }
        }
    }
}
