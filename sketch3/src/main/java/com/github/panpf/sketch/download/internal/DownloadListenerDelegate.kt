package com.github.panpf.sketch.download.internal

import com.github.panpf.sketch.download.DownloadData
import com.github.panpf.sketch.download.DownloadListener
import com.github.panpf.sketch.download.DownloadRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class DownloadListenerDelegate(private val downloadListener: DownloadListener) {

    suspend fun onStart(request: DownloadRequest) {
        withContext(Dispatchers.Main) {
            downloadListener.onStart(request)
        }
    }

    suspend fun onCancel(request: DownloadRequest) {
        // Because the current coroutine has been cancelled at this time, you must specify NonCancellable to execute successfully
        withContext(Dispatchers.Main + NonCancellable) {
            downloadListener.onCancel(request)
        }
    }

    suspend fun onError(request: DownloadRequest, throwable: Throwable) {
        withContext(Dispatchers.Main) {
            downloadListener.onError(request, throwable)
        }
    }

    suspend fun onSuccess(request: DownloadRequest, result: DownloadData) {
        withContext(Dispatchers.Main) {
            downloadListener.onSuccess(request, result)
        }
    }
}