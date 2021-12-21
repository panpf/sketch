package com.github.panpf.sketch3.download.internal

import android.content.Context
import com.github.panpf.sketch3.download.DownloadErrorResult
import com.github.panpf.sketch3.download.DownloadRequest
import com.github.panpf.sketch3.download.DownloadResult
import com.github.panpf.sketch3.download.DownloadSuccessResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadExecutor(private val context: Context) {

    private val interceptors = listOf(DownloadEngineInterceptor())

    suspend fun executeOnMain(request: DownloadRequest): DownloadResult {
        try {
            request.listener?.onStart(request)

            val result: DownloadResult = withContext(Dispatchers.IO) {
                DownloadInterceptorChain(
                    initialRequest = request,
                    interceptors = interceptors,
                    index = 0,
                    request = request,
                ).proceed(request)
            }

            when (result) {
                is DownloadSuccessResult -> {
                    request.listener?.onSuccess(request, result.data)
                }
                is DownloadErrorResult -> {
                    request.listener?.onError(request, result.throwable)
                }
            }

            return result
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                request.listener?.onCancel(request)
                throw throwable
            } else {
                return DownloadErrorResult(throwable)
            }
        }
    }
}