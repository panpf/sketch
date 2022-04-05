package com.github.panpf.sketch

import android.content.Context
import android.util.AttributeSet
import com.github.panpf.sketch.request.DisplayImageOptionsProvider
import com.github.panpf.sketch.request.DisplayOptions
import com.github.panpf.sketch.request.DisplayOptions.Builder
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.viewability.AbsAbilityImageView

open class SketchImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AbsAbilityImageView(context, attrs, defStyle), DisplayImageOptionsProvider {

    override var displayImageOptions: DisplayOptions? = null

    override fun submitRequest(request: DisplayRequest) {
        context.sketch.enqueueDisplay(request)
    }

    fun updateDisplayImageOptions(configBlock: (Builder.() -> Unit)) {
        displayImageOptions =
            displayImageOptions?.newDisplayOptions(configBlock) ?: DisplayOptions(configBlock)
    }
}