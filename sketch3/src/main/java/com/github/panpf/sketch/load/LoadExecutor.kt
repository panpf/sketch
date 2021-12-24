//package com.github.panpf.sketch.load
//
//import com.github.panpf.sketch.download.LoadErrorResult
//import com.github.panpf.sketch.download.LoadRequest
//import com.github.panpf.sketch.download.LoadResult
//import com.github.panpf.sketch.download.LoadSuccessResult
//import com.github.panpf.sketch.download.internal.LoadEngineInterceptor
//import com.github.panpf.sketch.download.internal.LoadInterceptorChain
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class LoadExecutor {
//    private val interceptors = listOf(LoadEngineInterceptor())
//
//    suspend fun executeOnMain(request: LoadRequest): LoadResult {
//        try {
//            request.listener?.onStart(request)
//
//            val result: LoadResult = withContext(sketch.taskDispatcher) {
//                LoadInterceptorChain(
//                    initialRequest = request,
//                    interceptors = interceptors,
//                    index = 0,
//                    request = request,
//                ).proceed(request)
//            }
//
//            when (result) {
//                is LoadSuccessResult -> {
//                    request.listener?.onSuccess(request, result.data)
//                }
//                is LoadErrorResult -> {
//                    request.listener?.onError(request, result.throwable)
//                }
//            }
//
//            return result
//        } catch (throwable: Throwable) {
//            if (throwable is CancellationException) {
//                request.listener?.onCancel(request)
//                throw throwable
//            } else {
//                return LoadErrorResult(throwable)
//            }
//        }
//    }
//}