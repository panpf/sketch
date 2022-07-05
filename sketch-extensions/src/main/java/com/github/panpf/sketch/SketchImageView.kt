package com.github.panpf.sketch

import android.content.Context
import android.util.AttributeSet
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.internal.ImageXmlAttributes
import com.github.panpf.sketch.internal.parseImageXmlAttributes
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageOptionsProvider
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.Listeners
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.ProgressListeners
import com.github.panpf.sketch.viewability.AbsAbilityImageView

open class SketchImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AbsAbilityImageView(context, attrs, defStyle), ImageOptionsProvider {

    override var displayImageOptions: ImageOptions? = null
    private var listenerList: MutableList<Listener<DisplayRequest, Success, Error>>? = null
    private var progressListenerList: MutableList<ProgressListener<DisplayRequest>>? = null

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

    override fun getDisplayListener(): Listener<DisplayRequest, Success, Error>? {
        val myListeners = listenerList?.takeIf { it.isNotEmpty() }
        val superListener = super.getDisplayListener()
        if (myListeners == null && superListener == null) return superListener

        val listenerList = (myListeners?.toMutableList() ?: mutableListOf()).apply {
            if (superListener != null) add(superListener)
        }.toList()
        return Listeners(listenerList)
    }

    override fun getDisplayProgressListener(): ProgressListener<DisplayRequest>? {
        val myProgressListeners = progressListenerList?.takeIf { it.isNotEmpty() }
        val superProgressListener = super.getDisplayProgressListener()
        if (myProgressListeners == null && superProgressListener == null) return superProgressListener

        val progressListenerList = (myProgressListeners?.toMutableList() ?: mutableListOf()).apply {
            if (superProgressListener != null) add(superProgressListener)
        }.toList()
        return ProgressListeners(progressListenerList)
    }

    fun registerListener(listener: Listener<DisplayRequest, Success, Error>) {
        this.listenerList = (this.listenerList ?: mutableListOf()).apply {
            add(listener)
        }
    }

    fun unregisterListener(listener: Listener<DisplayRequest, Success, Error>) {
        this.listenerList?.remove(listener)
    }

    fun registerProgressListener(listener: ProgressListener<DisplayRequest>) {
        this.progressListenerList = (this.progressListenerList ?: mutableListOf()).apply {
            add(listener)
        }
    }

    fun unregisterProgressListener(listener: ProgressListener<DisplayRequest>) {
        this.progressListenerList?.remove(listener)
    }
}