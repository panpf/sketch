package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.BaseRequestDelegate
import com.github.panpf.sketch.request.internal.BaseRequestManager
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.internal.RequestDelegate
import com.github.panpf.sketch.request.internal.RequestManager
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.coerceAtLeast
import com.github.panpf.sketch.util.times
import kotlinx.coroutines.Job

suspend fun ImageRequest.toRequestContext(sketch: Sketch, size: Size? = null): RequestContext {
    val targetSize =
        size ?: (sizeResolver.size().coerceAtLeast(Size.Empty) * (sizeMultiplier ?: 1f))
    val requestContext = RequestContext(sketch, this)
    requestContext.size = targetSize
    return requestContext
}

inline fun ImageRequest.Builder.target(
    crossinline onStart: (requestContext: RequestContext, placeholder: Image?) -> Unit = { _, _ -> },
    crossinline onError: (requestContext: RequestContext, error: Image?) -> Unit = { _, _ -> },
    crossinline onSuccess: (requestContext: RequestContext, result: Image) -> Unit = { _, _ -> },
) = target(object : Target {

    private val requestManager = BaseRequestManager()

    override fun getRequestManager(): RequestManager = requestManager

    override fun newRequestDelegate(
        sketch: Sketch,
        initialRequest: ImageRequest,
        job: Job
    ): RequestDelegate = BaseRequestDelegate(sketch, initialRequest, this, job)

    override fun onStart(requestContext: RequestContext, placeholder: Image?) =
        onStart(requestContext, placeholder)

    override fun onError(requestContext: RequestContext, error: Image?) =
        onError(requestContext, error)

    override fun onSuccess(requestContext: RequestContext, result: Image) =
        onSuccess(requestContext, result)
})