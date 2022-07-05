package com.github.panpf.sketch.target

import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener

interface DisplayListenerProvider {

    fun getDisplayListener(): Listener<DisplayRequest, DisplayResult.Success, DisplayResult.Error>?

    fun getDisplayProgressListener(): ProgressListener<DisplayRequest>?
}