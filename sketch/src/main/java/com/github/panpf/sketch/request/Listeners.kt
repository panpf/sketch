package com.github.panpf.sketch.request

class Listeners<REQUEST : ImageRequest, SUCCESS_RESULT : ImageResult.Success, ERROR_RESULT : ImageResult.Error>(
    val listenerList: List<Listener<REQUEST, SUCCESS_RESULT, ERROR_RESULT>>
) : Listener<REQUEST, SUCCESS_RESULT, ERROR_RESULT> {

    constructor(vararg listeners: Listener<REQUEST, SUCCESS_RESULT, ERROR_RESULT>) : this(listeners.toList())

    override fun onStart(request: REQUEST) {
        listenerList.forEach {
            it.onStart(request)
        }
    }

    override fun onCancel(request: REQUEST) {
        listenerList.forEach {
            it.onCancel(request)
        }
    }

    override fun onError(request: REQUEST, result: ERROR_RESULT) {
        listenerList.forEach {
            it.onError(request, result)
        }
    }

    override fun onSuccess(request: REQUEST, result: SUCCESS_RESULT) {
        listenerList.forEach {
            it.onSuccess(request, result)
        }
    }
}