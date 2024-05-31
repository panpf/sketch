package com.github.panpf.sketch.target

import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.internal.SkiaBitmapToComposeBitmapRequestInterceptor

actual fun getToComposeBitmapRequestInterceptor(): RequestInterceptor? =
    SkiaBitmapToComposeBitmapRequestInterceptor