package com.github.panpf.sketch

import android.content.Context
import android.util.AttributeSet
import com.github.panpf.sketch.request.ImageOptionsProvider
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.viewability.AbsAbilityImageView

open class SketchImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AbsAbilityImageView(context, attrs, defStyle), ImageOptionsProvider {

    override var displayImageOptions: ImageOptions? = null

    override fun submitRequest(request: DisplayRequest) {
        context.sketch.enqueue(request)
    }

    fun updateDisplayImageOptions(configBlock: (ImageOptions.Builder.() -> Unit)) {
        displayImageOptions =
            displayImageOptions?.newOptions(configBlock) ?: ImageOptions(configBlock)
    }
}