package com.github.panpf.sketch.sample.util

import com.github.panpf.sketch.PlatformContext
import platform.AppKit.NSPasteboard
import platform.AppKit.NSPasteboardTypeString

actual fun copyToClipboard(context: PlatformContext, text: String) {
    val pasteboard = NSPasteboard.generalPasteboard
    pasteboard.clearContents()
    pasteboard.setString(
        text,
        forType = NSPasteboardTypeString
    )
}