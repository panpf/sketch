package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.Listener

class CombinedListener<REQUEST : ImageRequest, SUCCESS_RESULT : ImageResult, ERROR_RESULT : ImageResult>(
    val listeners: List<Listener<REQUEST, SUCCESS_RESULT, ERROR_RESULT>>
) : Listener<REQUEST, SUCCESS_RESULT, ERROR_RESULT> {

    override fun onStart(request: REQUEST) {
        listeners.forEach {
            it.onStart(request)
        }
    }

    override fun onCancel(request: REQUEST) {
        listeners.forEach {
            it.onCancel(request)
        }
    }

    override fun onError(request: REQUEST, result: ERROR_RESULT) {
        listeners.forEach {
            it.onError(request, result)
        }
    }

    override fun onSuccess(request: REQUEST, result: SUCCESS_RESULT) {
        listeners.forEach {
            it.onSuccess(request, result)
        }
    }
}