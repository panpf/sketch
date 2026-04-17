package com.github.panpf.sketch.sample

import androidx.compose.ui.window.ComposeUIViewController
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.ui.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initialApp(PlatformContext.INSTANCE)
    return ComposeUIViewController {
        App()
    }
}