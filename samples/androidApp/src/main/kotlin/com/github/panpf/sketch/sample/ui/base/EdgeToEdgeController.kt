package com.github.panpf.sketch.sample.ui.base

import android.graphics.Color
import android.os.Build
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.toColorInt
import androidx.core.view.WindowCompat

object EdgeToEdgeController {

    fun onCreateBefore(activity: ComponentActivity) {
        // Explicitly specify that the detection mechanism of statusBarStyle and navigationBarStyle is completely transparent
        with(activity) {
            val systemBarStyle = SystemBarStyle
                .auto(lightScrim = Color.TRANSPARENT, darkScrim = Color.TRANSPARENT)
            enableEdgeToEdge(statusBarStyle = systemBarStyle, navigationBarStyle = systemBarStyle)
        }
    }

    fun onCreateAfter(@Suppress("unused") activity: ComponentActivity) {
        // For API 29+ (Android 10+), forcefully turn off the system translucency mask under "Three-key navigation"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.window.isNavigationBarContrastEnforced = false
        }
    }

    fun setStatusBarStyle(window: Window, isLightMode: Boolean) {
        @Suppress("ObsoleteSdkInt")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.isAppearanceLightStatusBars = isLightMode
        } else {
            // API 21-22 (Android 5-5L), does not support modifying the status bar mode.
            // The icon is always white, so a translucent dark background is added to it to
            // ensure that the white icon can be clearly visible on the light background.
            @Suppress("DEPRECATION")
            window.statusBarColor = if (isLightMode) "#60000000".toColorInt() else Color.TRANSPARENT
        }
    }

    fun setNavigationBarStyle(window: Window, isLightMode: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // TODO API 35 navigation bar cannot be changed to dark color
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.isAppearanceLightNavigationBars = isLightMode
        } else {
            // API 21-25 (Android 5-7.1.1), does not support modifying the navigation bar mode.
            // The icon is always white, so a translucent dark background is added to it to
            // ensure that the white icon can be clearly visible on the light background.
            @Suppress("DEPRECATION")
            window.navigationBarColor =
                if (isLightMode) "#60000000".toColorInt() else Color.TRANSPARENT
        }
    }
}