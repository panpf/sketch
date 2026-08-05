package com.github.panpf.sketch.util

@Suppress("DEPRECATION")
@OptIn(ExperimentalStdlibApi::class)
@EagerInitialization
@Deprecated("", level = DeprecationLevel.HIDDEN)
val blurHashComponentProviderInitHook: Any = ComponentLoader.register(BlurHashComponentProvider())