package com.github.panpf.sketch.test.utils

import android.os.Looper
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult.Error
import com.github.panpf.sketch.request.DownloadResult.Success
import com.github.panpf.sketch.request.Listener

class DownloadListenerSupervisor(private val callbackOnStart: (() -> Unit)? = null) : Listener<DownloadRequest, Success, Error> {

    val callbackActionList = mutableListOf<String>()

    override fun onStart(request: DownloadRequest) {
        super.onStart(request)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onStart")
        callbackOnStart?.invoke()
    }

    override fun onCancel(request: DownloadRequest) {
        super.onCancel(request)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onCancel")
    }

    override fun onError(request: DownloadRequest, result: Error) {
        super.onError(request, result)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onError")
    }

    override fun onSuccess(request: DownloadRequest, result: Success) {
        super.onSuccess(request, result)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onSuccess")
    }
}