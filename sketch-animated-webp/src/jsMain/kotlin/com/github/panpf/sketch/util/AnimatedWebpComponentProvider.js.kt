package com.github.panpf.sketch.util

@JsExport   // Required
@Suppress("DEPRECATION")
@OptIn(ExperimentalStdlibApi::class, ExperimentalJsExport::class)
@EagerInitialization
@Deprecated("", level = DeprecationLevel.HIDDEN)
val animatedWebpComponentProviderInitHook: Any =
    ComponentLoader.register(AnimatedWebpComponentProvider())