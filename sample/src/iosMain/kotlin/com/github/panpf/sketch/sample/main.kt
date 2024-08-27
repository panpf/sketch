package com.github.panpf.sketch.sample

import androidx.compose.ui.window.ComposeUIViewController
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.sample.ui.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initials()
    return ComposeUIViewController {
        App()
    }
}

private fun initials() {
    SingletonSketch.setSafe { newSketch(it) }
}
