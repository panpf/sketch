package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.internal.BaseRequestDelegate
import com.github.panpf.sketch.request.internal.BaseRequestManager
import com.github.panpf.sketch.request.internal.RequestDelegate
import com.github.panpf.sketch.request.internal.RequestManager
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.Job

fun ImageRequest.toRequestContext(sketch: Sketch, size: Size): RequestContext {
    return RequestContext(sketch, this, size)
}

suspend fun ImageRequest.toRequestContext(sketch: Sketch): RequestContext {
    return RequestContext(sketch, this)
}

inline fun ImageRequest.Builder.target(
    crossinline onStart: (sketch: Sketch, request: ImageRequest, placeholder: Image?) -> Unit = { _, _, _ -> },
    crossinline onError: (sketch: Sketch, request: ImageRequest, error: Image?) -> Unit = { _, _, _ -> },
    crossinline onSuccess: (sketch: Sketch, request: ImageRequest, result: Image) -> Unit = { _, _, _ -> },
) = target(object : Target {

    private val requestManager = BaseRequestManager()

    override fun getRequestManager(): RequestManager = requestManager

    override fun newRequestDelegate(
        sketch: Sketch,
        initialRequest: ImageRequest,
        job: Job
    ): RequestDelegate = BaseRequestDelegate(sketch, initialRequest, this, job)

    override fun onStart(sketch: Sketch, request: ImageRequest, placeholder: Image?) =
        onStart(sketch, request, placeholder)

    override fun onError(sketch: Sketch, request: ImageRequest, error: Image?) =
        onError(sketch, request, error)

    override fun onSuccess(sketch: Sketch, request: ImageRequest, result: Image) =
        onSuccess(sketch, request, result)
})