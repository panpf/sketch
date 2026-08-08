package com.github.panpf.sketch.sample.util

import com.github.panpf.sketch.PlatformContext
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

actual fun copyToClipboard(context: PlatformContext, text: String) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(StringSelection(text), null)
}