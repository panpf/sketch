package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.target.DownloadTarget
import com.github.panpf.sketch.util.SketchException

class TestDownloadTarget : DownloadTarget {

    var start: String? = null
    var successBitmap: DownloadData? = null
    var exception: SketchException? = null

    override fun onStart() {
        super.onStart()
        this.start = "onStart"
    }

    override fun onSuccess(result: DownloadData) {
        super.onSuccess(result)
        this.successBitmap = result
    }

    override fun onError(exception: SketchException) {
        super.onError(exception)
        this.exception = exception
    }
}