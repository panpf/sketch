package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.sample.ui.DesktopTempTestRoute
import com.github.panpf.sketch.sample.ui.SkiaColorTypeTestRoute

actual fun platformTestScreens(): List<TestItem> = listOf(
    TestItem("Desktop Temp", DesktopTempTestRoute),
    TestItem("SkiaColorTypeTest", SkiaColorTypeTestRoute),
)