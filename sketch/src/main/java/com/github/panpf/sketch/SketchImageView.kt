package com.github.panpf.sketch

import android.content.Context
import android.util.AttributeSet
import com.github.panpf.sketch.request.DisplayOptions
import com.github.panpf.sketch.request.DisplayOptions.Builder
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.target.DisplayOptionsProvider
import com.github.panpf.sketch.viewability.internal.AbsAbilityImageView

open class SketchImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AbsAbilityImageView(context, attrs, defStyle), DisplayOptionsProvider {

    override var displayOptions: DisplayOptions? = null

    override fun submitRequest(request: DisplayRequest) {
        val newRequest = request.newDisplayRequest {
            target(this@SketchImageView)
        }
        context.sketch.enqueueDisplay(newRequest)
    }

    fun updateDisplayOptions(configBlock: (Builder.() -> Unit)) {
        displayOptions =
            displayOptions?.newDisplayOptions(configBlock) ?: DisplayOptions.new(configBlock)
    }
}