package com.github.panpf.sketch.resize

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.resize.internal.DisplaySizeResolver

actual fun defaultSizeResolver(context: PlatformContext): SizeResolver {
    return DisplaySizeResolver(context)
}