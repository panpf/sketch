package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.target.Target

interface DisplayableRequest : LoadableRequest {
    val memoryCacheKey: String
    val memoryCachePolicy: CachePolicy
    val disabledAnimationDrawable: Boolean?
    val loadingImage: StateImage?
    val errorImage: StateImage?    // todo error 可根据异常决定显示什么样的 error，这样就能很容易实现暂停下载的时候显示特定的错误图片，或者添加专门的移动网络暂停下载功能
    val emptyImage: StateImage?
    val target: Target?
}