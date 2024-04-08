package com.github.panpf.sketch.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

actual fun ioCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO