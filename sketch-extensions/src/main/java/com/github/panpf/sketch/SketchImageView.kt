package com.github.panpf.sketch

import android.content.Context
import android.util.AttributeSet
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.internal.ImageXmlAttributes
import com.github.panpf.sketch.internal.parseImageXmlAttributes
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageOptionsProvider
import com.github.panpf.sketch.viewability.AbsAbilityImageView

open class SketchImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AbsAbilityImageView(context, attrs, defStyle), ImageOptionsProvider {

    override var displayImageOptions: ImageOptions? = null

    private val imageXmlAttributes: ImageXmlAttributes

    init {
        imageXmlAttributes = parseImageXmlAttributes(context, attrs)
        displayImageOptions = imageXmlAttributes.options
        displaySrc()
    }

    private fun displaySrc() {
        val displaySrcResId = imageXmlAttributes.srcResId
        if (displaySrcResId != null) {
            if (isInEditMode) {
                setImageResource(displaySrcResId)
            } else {
                post {
                    displayImage(context.newResourceUri(displaySrcResId))
                }
            }
        }
    }

    override fun submitRequest(request: DisplayRequest) {
        context.sketch.enqueue(request)
    }
}