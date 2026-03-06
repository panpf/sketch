package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.sample.ui.MainThreadTestRoute
import com.github.panpf.sketch.sample.ui.SkiaColorTypeTestRoute

actual fun platformTestScreens(): List<TestItem> = listOf(
    TestItem("Main Thread Test", MainThreadTestRoute),
    TestItem("SkiaColorTypeTest", SkiaColorTypeTestRoute),
)