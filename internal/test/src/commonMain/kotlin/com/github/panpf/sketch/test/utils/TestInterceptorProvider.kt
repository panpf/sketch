package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.util.InterceptorProvider

class TestInterceptorProvider : InterceptorProvider {

    override fun create(context: PlatformContext): Interceptor {
        return TestInterceptor()
    }
}