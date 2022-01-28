package com.github.panpf.sketch.request

import android.view.View
import com.github.panpf.sketch.R
import com.github.panpf.sketch.request.internal.ViewTargetRequestManager

class RequestManagerUtils {
    companion object {
        fun requestManagerOrNull(view: View): ViewTargetRequestManager? =
            view.getTag(R.id.sketch_request_manager) as ViewTargetRequestManager?

        fun dispose(view: View) {
            requestManagerOrNull(view)?.dispose()
        }

        fun result(view: View): DisplayResult? = requestManagerOrNull(view)?.getResult()
    }
}