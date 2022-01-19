package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.Listener

class CombinedListener<REQUEST : ImageRequest, SUCCESS_RESULT : ImageResult, ERROR_RESULT : ImageResult>(
    val fromViewListener: Listener<REQUEST, SUCCESS_RESULT, ERROR_RESULT>,
    val fromBuilderListener: Listener<REQUEST, SUCCESS_RESULT, ERROR_RESULT>,
) : Listener<REQUEST, SUCCESS_RESULT, ERROR_RESULT> {

    override fun onStart(request: REQUEST) {
        fromViewListener.onStart(request)
        fromBuilderListener.onStart(request)
    }

    override fun onCancel(request: REQUEST) {
        fromViewListener.onCancel(request)
        fromBuilderListener.onCancel(request)
    }

    override fun onError(request: REQUEST, result: ERROR_RESULT) {
        fromViewListener.onError(request, result)
        fromBuilderListener.onError(request, result)
    }

    override fun onSuccess(request: REQUEST, result: SUCCESS_RESULT) {
        fromViewListener.onSuccess(request, result)
        fromBuilderListener.onSuccess(request, result)
    }
}