package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.cache.MemoryCache

expect fun createImage(width: Int, height: Int): Image

expect fun createCacheValue(image: Image, extras: Map<String, Any?>): MemoryCache.Value