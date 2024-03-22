package com.github.panpf.sketch.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal fun ioCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
//internal actual fun ioCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO // nonJs
//internal actual fun ioCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Default // js
//internal actual fun ioCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Default // js wasm