package com.github.panpf.sketch.resize

import com.github.panpf.sketch.PlatformContext

actual fun defaultSizeResolver(context: PlatformContext): SizeResolver {
    return OriginSizeResolver
}