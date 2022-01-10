package com.github.panpf.sketch.internal

import android.graphics.Canvas
import android.view.View
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success

interface ProgressIndicator {
    val key: String

    fun onDraw(canvas: Canvas)

    fun onLayout(view: View)

    fun onProgressChanged(
        view: View,
        request: DisplayRequest,
        totalLength: Long,
        completedLength: Long
    )

    fun onRequestStart(
        view: View,
        request: DisplayRequest,
    )

    fun onRequestError(
        view: View,
        request: DisplayRequest,
        result: Error,
    )

    fun onRequestSuccess(
        view: View,
        request: DisplayRequest,
        result: Success,
    )
}