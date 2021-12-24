package com.github.panpf.sketch3.download.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch3.Sketch3
import com.github.panpf.sketch3.download.DownloadErrorResult
import com.github.panpf.sketch3.download.DownloadRequest
import com.github.panpf.sketch3.download.DownloadResult
import com.github.panpf.sketch3.download.DownloadSuccessResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class DownloadExecutor(private val sketch3: Sketch3) {

    @WorkerThread
    suspend fun execute(request: DownloadRequest): DownloadResult {
        try {
            withContext(Dispatchers.Main) {
                request.listener?.onStart(request)
            }

            val result: DownloadResult = DownloadInterceptorChain(
                initialRequest = request,
                interceptors = sketch3.downloadInterceptors,
                index = 0,
                request = request,
            ).proceed(sketch3, request)

            when (result) {
                is DownloadSuccessResult -> {
                    withContext(Dispatchers.Main) {
                        request.listener?.onSuccess(request, result.data)
                    }
                }
                is DownloadErrorResult -> {
                    withContext(Dispatchers.Main) {
                        request.listener?.onError(request, result.throwable)
                    }
                }
            }

            return result
        } catch (throwable: Throwable) {
            // todo 捕获不到取消和异常
            if (throwable is CancellationException) {
                withContext(Dispatchers.Main) {
                    request.listener?.onCancel(request)
                }
                throw throwable
            } else {
                return DownloadErrorResult(throwable)
            }
        } finally {
            println("")
        }
    }
}