//package com.github.panpf.sketch3.download.internal
//
//import com.github.panpf.sketch3.common.ExecuteResult
//import com.github.panpf.sketch3.download.DownloadListener
//import com.github.panpf.sketch3.download.DownloadRequest
//import com.github.panpf.sketch3.download.DownloadResult
//
//class DownloadResultCapture(private val listener: DownloadListener?) : DownloadListener {
//
//    private var result: DownloadResult? = null
//    private var throwable: Throwable? = null
//    private var canceled: Boolean? = null
//
//    override fun onStart(request: DownloadRequest) {
//        super.onStart(request)
//        listener?.onStart(request)
//    }
//
//    override fun onCancel(request: DownloadRequest) {
//        super.onCancel(request)
//        listener?.onCancel(request)
//        this.canceled = true
//    }
//
//    override fun onError(request: DownloadRequest, throwable: Throwable) {
//        super.onError(request, throwable)
//        listener?.onError(request, throwable)
//        this.throwable = throwable
//    }
//
//    override fun onSuccess(request: DownloadRequest, result: DownloadResult) {
//        super.onSuccess(request, result)
//        listener?.onSuccess(request, result)
//        this.result = result
//    }
//
//    fun toExecuteResult(): ExecuteResult<DownloadResult> {
//        return ExecuteResult(result, throwable, canceled ?: false)
//    }
//}