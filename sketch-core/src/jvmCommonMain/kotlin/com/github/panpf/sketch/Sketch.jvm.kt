package com.github.panpf.sketch

import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.HurlStack

internal actual fun defaultHttpStack(): HttpStack = HurlStack.Builder().build()