package com.github.panpf.sketch.sample.util

import kotlinx.coroutines.await

@OptIn(ExperimentalWasmJsInterop::class)
@Suppress("INVISIBLE_REFERENCE")
internal actual suspend fun awaitSkiko(): JsAny =
    org.jetbrains.skiko.wasm.awaitSkiko.await()
