package com.github.panpf.sketch.internal

import android.graphics.Canvas
import android.view.View
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success

interface ProgressIndicator {
    val key: String

    var view: View?

    fun onDraw(canvas: Canvas)

    fun onLayout()

    fun onProgressChanged(request: DisplayRequest, totalLength: Long, completedLength: Long)

    fun onRequestStart(request: DisplayRequest)

    fun onRequestError(request: DisplayRequest, result: Error)

    fun onRequestSuccess(request: DisplayRequest, result: Success)
}