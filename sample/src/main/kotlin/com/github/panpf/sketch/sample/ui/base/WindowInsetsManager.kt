package com.github.panpf.sketch.sample.ui.base

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi

class WindowInsetsManager private constructor(window: Window) {

    companion object {
        fun get(window: Window): WindowInsetsManager {
            return WindowInsetsManager(window)
        }
    }

    private val impl: Impl = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Impl30(window)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> Impl26(window)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> Impl23(window)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH -> Impl20(window)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> Impl19(window)
        else -> Impl(window)    // Jelly Bean
    }


    fun setFullscreen(fullscreen: Boolean) {
        impl.setFullscreen(fullscreen)
    }

    fun isFullscreen(): Boolean {
        return impl.isFullscreen()
    }


    fun setHideNavigation(hide: Boolean) {
        impl.setHideNavigation(hide)
    }

    fun isHideNavigation(): Boolean {
        return impl.isHideNavigation()
    }


    /*
     * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：内容可以延伸到状态栏区域，但状态烂和导航栏依然显示在内容上方
     */
    fun setLayoutFullscreen(fullscreen: Boolean) {
        impl.setLayoutFullscreen(fullscreen)
    }

    /*
     * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：内容可以延伸到状态栏区域，但状态烂和导航栏依然显示在内容上方
     */
    fun isLayoutFullscreen(): Boolean {
        return impl.isLayoutFullscreen()
    }


    fun setTranslucentStatus(translucent: Boolean) {
        impl.setTranslucentStatus(translucent)
    }

    fun isTranslucentStatus(): Boolean {
        return impl.isTranslucentStatus()
    }


    fun setDrawsSystemBarBackgrounds(draw: Boolean) {
        impl.setDrawsSystemBarBackgrounds(draw)
    }

    fun isDrawsSystemBarBackgrounds(): Boolean {
        return impl.isDrawsSystemBarBackgrounds()
    }


    fun setStatusBarColor(color: Int) {
        impl.setStatusBarColor(color)
    }

    fun getStatusBarColor(): Int {
        return impl.getStatusBarColor()
    }


    fun setNavigationBarColor(color: Int) {
        impl.setNavigationBarColor(color)
    }

    fun getNavigationBarColor(): Int {
        return impl.getNavigationBarColor()
    }


    fun setLightStatusBar(light: Boolean) {
        impl.setLightStatusBar(light)
    }

    fun isLightStatusBar(): Boolean {
        return impl.isLightStatusBar()
    }


    // Jelly Bean
    private open class Impl(val window: Window) {

        fun setFullscreen(fullscreen: Boolean) {
            if (fullscreen) {
                window.setSystemUiFlag(View.SYSTEM_UI_FLAG_FULLSCREEN)
            } else {
                window.unsetSystemUiFlag(View.SYSTEM_UI_FLAG_FULLSCREEN)
            }
        }

        fun isFullscreen(): Boolean {
            return window.isSetSystemUiFlag(View.SYSTEM_UI_FLAG_FULLSCREEN)
        }


        open fun setHideNavigation(hide: Boolean) {
            if (hide) {
                window.setSystemUiFlag(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            } else {
                window.unsetSystemUiFlag(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            }
        }

        open fun isHideNavigation(): Boolean {
            return window.isSetSystemUiFlag(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }


        fun setLayoutFullscreen(fullscreen: Boolean) {
            if (fullscreen) {
                window.setSystemUiFlag(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            } else {
                window.unsetSystemUiFlag(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            }
        }

        fun isLayoutFullscreen(): Boolean {
            return window.isSetSystemUiFlag(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }


        // Impl19 provides implementation
        open fun setTranslucentStatus(translucent: Boolean) {

        }

        // Impl19 provides implementation
        open fun isTranslucentStatus(): Boolean {
            return false
        }


        // Impl21 provides implementation
        open fun setDrawsSystemBarBackgrounds(draw: Boolean) {
        }

        // Impl21 provides implementation
        open fun isDrawsSystemBarBackgrounds(): Boolean {
            return false
        }


        // Impl21 provides implementation
        open fun setStatusBarColor(color: Int) {

        }

        // Impl21 provides implementation
        open fun getStatusBarColor(): Int {
            return 0
        }


        // Impl21 provides implementation
        open fun setNavigationBarColor(color: Int) {

        }

        // Impl21 provides implementation
        open fun getNavigationBarColor(): Int {
            return 0
        }

        // Impl23 provides implementation
        open fun setLightStatusBar(light: Boolean) {
        }

        // Impl23 provides implementation
        open fun isLightStatusBar(): Boolean {
            return false
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private open class Impl19(window: Window) : Impl(window) {
        override fun setTranslucentStatus(translucent: Boolean) {
            if (translucent) {
                window.setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            } else {
                window.unsetWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        }

        override fun isTranslucentStatus(): Boolean {
            return window.isSetWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    private open class Impl20(window: Window) : Impl19(window) {

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private open class Impl21(window: Window) : Impl20(window) {
        override fun setDrawsSystemBarBackgrounds(draw: Boolean) {
            if (draw) {
                window.setWindowFlag(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            } else {
                window.unsetWindowFlag(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
        }

        override fun isDrawsSystemBarBackgrounds(): Boolean {
            return window.isSetWindowFlag(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        override fun setStatusBarColor(color: Int) {
            window.statusBarColor = color
        }

        override fun getStatusBarColor(): Int {
            return window.statusBarColor
        }

        override fun setNavigationBarColor(color: Int) {
            window.navigationBarColor = color
        }

        override fun getNavigationBarColor(): Int {
            return window.navigationBarColor
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private open class Impl23(window: Window) : Impl21(window) {
        override fun setLightStatusBar(light: Boolean) {
            if (light) {
                window.setSystemUiFlag(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            } else {
                window.unsetSystemUiFlag(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
        }

        override fun isLightStatusBar(): Boolean {
            return window.isSetSystemUiFlag(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private open class Impl26(window: Window) : Impl23(window) {

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private open class Impl30(window: Window) : Impl26(window) {

    }
}

private fun Window.setSystemUiFlag(systemUiFlag: Int) {
    val decorView: View = decorView
    decorView.systemUiVisibility = (decorView.systemUiVisibility or systemUiFlag)
}

private fun Window.unsetSystemUiFlag(systemUiFlag: Int) {
    val decorView: View = decorView
    decorView.systemUiVisibility = (decorView.systemUiVisibility and systemUiFlag.inv())
}

private fun Window.isSetSystemUiFlag(systemUiFlag: Int): Boolean {
    val decorView: View = decorView
    return decorView.systemUiVisibility and systemUiFlag != 0
}

private fun Window.setWindowFlag(windowFlag: Int) {
    addFlags(windowFlag)
}

private fun Window.unsetWindowFlag(windowFlag: Int) {
    clearFlags(windowFlag)
}

private fun Window.isSetWindowFlag(windowFlag: Int): Boolean {
    return attributes.flags and windowFlag != 0
}