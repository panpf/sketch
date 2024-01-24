package com.github.panpf.sketch.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal fun ioCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
//internal expect fun ioCoroutineDispatcher(): CoroutineDispatcher
//internal actual fun ioCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO // nonJs
//internal actual fun ioCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Default // js