package com.github.panpf.sketch.test.utils

import android.os.Looper
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.Listener

class DisplayListenerSupervisor constructor(
    private val name: String? = null,
    private val callbackOnStart: (() -> Unit)? = null
) : Listener<DisplayRequest, Success, Error> {

    val callbackActionList = mutableListOf<String>()

    override fun onStart(request: DisplayRequest) {
        super.onStart(request)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onStart" + (name?.let { ":$it" } ?: ""))
        callbackOnStart?.invoke()
    }

    override fun onCancel(request: DisplayRequest) {
        super.onCancel(request)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onCancel" + (name?.let { ":$it" } ?: ""))
    }

    override fun onError(request: DisplayRequest, result: Error) {
        super.onError(request, result)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onError" + (name?.let { ":$it" } ?: ""))
    }

    override fun onSuccess(request: DisplayRequest, result: Success) {
        super.onSuccess(request, result)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onSuccess" + (name?.let { ":$it" } ?: ""))
    }
}