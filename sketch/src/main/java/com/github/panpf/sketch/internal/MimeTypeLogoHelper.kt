package com.github.panpf.sketch.internal

import android.graphics.Canvas
import android.graphics.drawable.Animatable
import android.view.View
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success

open class MimeTypeLogoHelper(val mimeTypeIconMap: Map<String, MimeTypeLogo>, val margin: Int = 0) {

    private var view: View? = null
    private var show: Boolean = false
    private var mimeTypeLogo: MimeTypeLogo? = null

    open val key: String by lazy {
        "MimeTypeLogoHelper(mimeTypes=${mimeTypeIconMap.keys.joinToString(separator = ",")}, margin=$margin)"
    }

    open fun onLayout(view: View) {

    }

    open fun onRequestStart(
        view: View,
        request: DisplayRequest,
    ) {
        this.view = view
        show = false
        mimeTypeLogo = null
        view.postInvalidate()
    }

    open fun onRequestError(
        view: View,
        request: DisplayRequest,
        result: Error,
    ) {
        this.view = view
        show = false
        mimeTypeLogo = null
        view.postInvalidate()
    }

    open fun onRequestSuccess(
        view: View,
        request: DisplayRequest,
        result: Success,
    ) {
        this.view = view
        val mimeTypeLogo = mimeTypeIconMap[result.data.info.mimeType]
        this.mimeTypeLogo = mimeTypeLogo
        show =
            mimeTypeLogo != null && (!mimeTypeLogo.hiddenWhenAnimatable || result.data.drawable !is Animatable)

        view.postInvalidate()
    }

    open fun onDraw(canvas: Canvas) {
        if (!show) return
        val view = view ?: return
        val mimeTypeLogo = mimeTypeLogo ?: return
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