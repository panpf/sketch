package com.github.panpf.sketch.internal

import android.graphics.Canvas
import android.graphics.drawable.Animatable
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.util.getLastDrawable

open class MimeTypeLogoHelper(val mimeTypeIconMap: Map<String, MimeTypeLogo>, val margin: Int = 0) {

    var view: SketchImageView? = null

    open val key: String by lazy {
        "MimeTypeLogoHelper(mimeTypes=${mimeTypeIconMap.keys.joinToString(separator = ",")}, margin=$margin)"
    }

    open fun onRequestStart(request: DisplayRequest) {
        view?.postInvalidate()
    }

    open fun onRequestError(request: DisplayRequest, result: Error) {
        view?.postInvalidate()
    }

    open fun onRequestSuccess(request: DisplayRequest, result: Success) {
        view?.postInvalidate()
    }

    open fun onDraw(canvas: Canvas) {
        val view = view ?: return
        val lastDrawable = view.drawable?.getLastDrawable() ?: return
        if (lastDrawable !is SketchDrawable) return
        val mimeType = lastDrawable.mimeType ?: return
        val mimeTypeLogo = mimeTypeIconMap[mimeType] ?: return
        if (mimeTypeLogo.hiddenWhenAnimatable && lastDrawable is Animatable) return
        val logoDrawable = mimeTypeLogo.getDrawable(view.context)
        logoDrawable.setBounds(
            view.right - view.paddingRight - logoDrawable.intrinsicWidth,
            view.bottom - view.paddingBottom - logoDrawable.intrinsicHeight,
            view.right - view.paddingRight,
            view.bottom - view.paddingBottom
        )
        logoDrawable.draw(canvas)
    }
}