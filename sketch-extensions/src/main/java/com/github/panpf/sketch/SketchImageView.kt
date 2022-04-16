package com.github.panpf.sketch

import android.content.Context
import android.util.AttributeSet
import com.github.panpf.sketch.request.ImageOptionsProvider
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.viewability.AbsAbilityImageView

// todo 提供一些属性
open class SketchImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AbsAbilityImageView(context, attrs, defStyle), ImageOptionsProvider {

    override var displayImageOptions: ImageOptions? = null

    override fun submitRequest(request: DisplayRequest) {
        context.sketch.enqueue(request)
    }
}