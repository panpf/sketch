package com.github.panpf.sketch.test.utils

import android.os.Looper
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult.Error
import com.github.panpf.sketch.request.LoadResult.Success

class LoadListenerSupervisor constructor(
    private val name: String? = null,
    private val callbackOnStart: (() -> Unit)? = null
) : Listener<LoadRequest, Success, Error> {

    val callbackActionList = mutableListOf<String>()

    override fun onStart(request: LoadRequest) {
        super.onStart(request)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onStart" + (name?.let { ":$it" } ?: ""))
        callbackOnStart?.invoke()
    }

    override fun onCancel(request: LoadRequest) {
        super.onCancel(request)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onCancel" + (name?.let { ":$it" } ?: ""))
    }

    override fun onError(request: LoadRequest, result: Error) {
        super.onError(request, result)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onError" + (name?.let { ":$it" } ?: ""))
    }

    override fun onSuccess(request: LoadRequest, result: Success) {
        super.onSuccess(request, result)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onSuccess" + (name?.let { ":$it" } ?: ""))
    }
}