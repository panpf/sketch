package com.github.panpf.sketch.sample

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.ui.App
import platform.AppKit.NSApplication
import platform.AppKit.NSApplicationActivationPolicy
import platform.AppKit.NSApplicationDelegateProtocol
import platform.Foundation.NSNotification
import platform.darwin.NSObject

fun main() {
    val nsApplication = NSApplication.sharedApplication()
    nsApplication.setActivationPolicy(
        NSApplicationActivationPolicy.NSApplicationActivationPolicyRegular
    )
    nsApplication.delegate = object : NSObject(), NSApplicationDelegateProtocol {
        override fun applicationShouldTerminateAfterLastWindowClosed(sender: NSApplication) = true

        override fun applicationDidFinishLaunching(notification: NSNotification) {
            Window(
                title = "Sketch4",
                size = DpSize(1200.dp, 800.dp),
            ) {
                App()
            }
        }
    }
    initialApp(PlatformContext.INSTANCE)
    nsApplication.run()
}
