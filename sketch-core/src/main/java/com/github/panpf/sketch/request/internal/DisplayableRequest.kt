package com.github.panpf.sketch.request.internal

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.cache.CachePolicy

interface DisplayableRequest : LoadableRequest {
    val memoryCacheKey: String
    val memoryCachePolicy: CachePolicy
    val disabledAnimationDrawable: Boolean?
    val placeholderDrawable: Drawable?
    val errorDrawable: Drawable?
    val emptyDrawable: Drawable?
}