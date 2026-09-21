package com.github.panpf.sketch.sample.util

import kotlinx.coroutines.await
import org.jetbrains.skiko.InternalSkikoApi

@OptIn(ExperimentalWasmJsInterop::class, InternalSkikoApi::class)
internal actual suspend fun awaitSkiko(): JsAny =
    org.jetbrains.skiko.wasm.awaitSkiko.await()
