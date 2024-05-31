package com.github.panpf.sketch.target

import com.github.panpf.sketch.request.RequestInterceptor

// Conversion is not possible on Android because Android requires sharing Bitmap between View and Compose
actual fun getToComposeBitmapRequestInterceptor(): RequestInterceptor? = null
