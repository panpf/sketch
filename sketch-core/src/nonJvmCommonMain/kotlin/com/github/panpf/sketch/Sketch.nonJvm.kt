package com.github.panpf.sketch

import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.KtorStack

internal actual fun defaultHttpStack(): HttpStack = KtorStack()