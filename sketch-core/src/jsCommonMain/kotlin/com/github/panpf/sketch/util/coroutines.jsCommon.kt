package com.github.panpf.sketch.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual fun ioCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Default