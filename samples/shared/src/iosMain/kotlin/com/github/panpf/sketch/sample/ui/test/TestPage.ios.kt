package com.github.panpf.sketch.sample.ui.test

actual fun platformTestScreens(): List<TestItem> = listOf(
    TestItem("Main Thread Test", MainThreadTestScreen()),
    TestItem("SkiaColorTypeTest", SkiaColorTypeTestScreen()),
)