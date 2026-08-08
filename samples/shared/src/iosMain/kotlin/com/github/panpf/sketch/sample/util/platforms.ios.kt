package com.github.panpf.sketch.sample.util

import com.github.panpf.sketch.PlatformContext
import platform.UIKit.UIPasteboard

actual fun copyToClipboard(context: PlatformContext, text: String) {
    UIPasteboard.generalPasteboard.string = text
}