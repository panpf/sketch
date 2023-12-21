package com.github.panpf.sketch.sample.ui.base

import android.os.Build
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.graphics.ColorUtils
import com.github.panpf.sketch.sample.ui.base.WindowInsetStyle.NonFullScreen.Style.TextColor
import com.github.panpf.sketch.sample.ui.theme.getWindowBackgroundColor

class WindowInsetStyleApplier private constructor(
    window: Window,
    windowInsetStyle: WindowInsetStyle
) {

    companion object {
        fun get(window: Window, windowInsetStyle: WindowInsetStyle): WindowInsetStyleApplier {
            return WindowInsetStyleApplier(window, windowInsetStyle)
        }
    }

    private val impl: Impl = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> Impl23(window, windowInsetStyle)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> Impl21(window, windowInsetStyle)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> Impl19(window, windowInsetStyle)
        else -> Impl(window, windowInsetStyle)    // Jelly Bean
    }

    fun apply() {
        impl.apply()
    }

    // Jelly Bean
    open class Impl(
        val window: Window,
        val windowInsetStyle: WindowInsetStyle,
    ) {

        val insetsManager = WindowsInsetsManager.get(window)

        open fun apply() {
            when (windowInsetStyle) {
                is WindowInsetStyle.FullScreen -> {
                    // 全屏文档在此：
                    // 新 API：https://developer.android.com/develop/ui/views/layout/immersive
                    // 旧 API：https://developer.android.com/training/system-ui/immersive?hl=zh-cn
                    insetsManager.setFullscreen(true)
                    insetsManager.setHideNavigation(true)
                    // todo hide systemBars, statusBars, navigationBars
                    // todo behavior
                }

                is WindowInsetStyle.NonFullScreen -> {
                    // Not support
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    class Impl19(
        window: Window, windowInsetStyle: WindowInsetStyle
    ) : Impl(window, windowInsetStyle) {

        override fun apply() {
            when (windowInsetStyle) {
                is WindowInsetStyle.FullScreen -> {
                    // 全屏文档在此：
                    // 新 API：https://developer.android.com/develop/ui/views/layout/immersive
                    // 旧 API：https://developer.android.com/training/system-ui/immersive?hl=zh-cn
                    insetsManager.setFullscreen(true)
                    insetsManager.setHideNavigation(true)
                    // todo hide systemBars, statusBars, navigationBars
                    // todo behavior
                }

                is WindowInsetStyle.NonFullScreen -> {
                    when (windowInsetStyle.statusBarMode) {
                        WindowInsetStyle.NonFullScreen.Mode.Linear -> {
                            // Nothing needs to be done
                        }

                        WindowInsetStyle.NonFullScreen.Mode.Floating -> {
                            // KITKAT cannot modify the background color and text color,
                            // so you only need to set the status bar background to translucent (default is black translucent)
                            insetsManager.setTranslucentStatus(true)
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    open class Impl21(
        window: Window, windowInsetStyle: WindowInsetStyle
    ) : Impl(window, windowInsetStyle) {

        override fun apply() {
            when (windowInsetStyle) {
                is WindowInsetStyle.FullScreen -> {
                    // 全屏文档在此：
                    // 新 API：https://developer.android.com/develop/ui/views/layout/immersive
                    // 旧 API：https://developer.android.com/training/system-ui/immersive?hl=zh-cn
                    insetsManager.setFullscreen(true)
                    insetsManager.setHideNavigation(true)
                    // todo hide systemBars, statusBars, navigationBars
                    // todo behavior
                }

                is WindowInsetStyle.NonFullScreen -> {


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
                    when (windowInsetStyle.statusBarMode) {
                        WindowInsetStyle.NonFullScreen.Mode.Linear -> {
                            // todo Waiting implemented
                        }

                        WindowInsetStyle.NonFullScreen.Mode.Floating -> {
                            insetsManager.setLayoutFullscreen(true)
                            val statusBarStyle = windowInsetStyle.statusBarStyle
                            val backgroundColor = statusBarStyle.backgroundColor
                            if (backgroundColor != null) {
                                // You can't set the status bar to be translucent because it has a higher priority than the statusBarColor property
                                insetsManager.setTranslucentStatus(false)
                                insetsManager.setDrawsSystemBarBackgrounds(true)
                                insetsManager.setStatusBarColor(backgroundColor)
                            } else {
                                insetsManager.setTranslucentStatus(true)
                            }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    class Impl23(
        window: Window, windowInsetStyle: WindowInsetStyle
    ) : Impl21(window, windowInsetStyle) {

        override fun apply() {
            when (windowInsetStyle) {
                is WindowInsetStyle.FullScreen -> {
                    // 全屏文档在此：
                    // 新 API：https://developer.android.com/develop/ui/views/layout/immersive
                    // 旧 API：https://developer.android.com/training/system-ui/immersive?hl=zh-cn
                    insetsManager.setFullscreen(true)
                    insetsManager.setHideNavigation(true)
                    // todo hide systemBars, statusBars, navigationBars
                    // todo behavior
                }

                is WindowInsetStyle.NonFullScreen -> {
                    applyStatusBarTextColor(windowInsetStyle.statusBarStyle)
                }
            }
        }

        private fun applyStatusBarTextColor(statusBarStyle: WindowInsetStyle.NonFullScreen.Style) {
            val backgroundColor = statusBarStyle.backgroundColor
            if (backgroundColor != null) {
                val textColor = statusBarStyle.textColor
                if (textColor != null) {
                    when (textColor) {
                        is TextColor.White -> {
                            insetsManager.setLightStatusBar(false)
                        }

                        is TextColor.Black -> {
                            insetsManager.setLightStatusBar(true)
                        }

                        is TextColor.DynamicWithWindowBackground -> {
                            val windowBackgroundColor =
                                window.context.getWindowBackgroundColor()
                            val isLightColor =
                                ColorUtils.calculateLuminance(windowBackgroundColor) >= 0.5
                            insetsManager.setLightStatusBar(isLightColor)
                        }

                        is TextColor.DynamicWithBackgroundColor -> {
                            val isLightColor =
                                ColorUtils.calculateLuminance(backgroundColor) >= 0.5
                            insetsManager.setLightStatusBar(isLightColor)
                        }

                        is TextColor.DynamicWithColor -> {
                            val isLightColor =
                                ColorUtils.calculateLuminance(textColor.color) >= 0.5
                            insetsManager.setLightStatusBar(isLightColor)
                        }
                    }
                }
            }
        }
    }
}