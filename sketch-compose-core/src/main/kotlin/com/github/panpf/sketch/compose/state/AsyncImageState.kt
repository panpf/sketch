package com.github.panpf.sketch.compose.state

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.compose.internal.AsyncImageDisplayTarget
import com.github.panpf.sketch.compose.isEmpty
import com.github.panpf.sketch.compose.state.AsyncImagePainter2.State
import com.github.panpf.sketch.compose.toPainter
import com.github.panpf.sketch.compose.toScale
import com.github.panpf.sketch.compose.toSketchSize
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.isDefault
import com.github.panpf.sketch.target.DisplayTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

@Composable
fun rememberAsyncImageState(request: DisplayRequest, sketch: Sketch): AsyncImageState {
    require(request.listener == null) {
        "listener is not supported in compose, please use AsyncImageState.loadState instead"
    }
    require(request.progressListener == null) {
        "progressListener is not supported in compose, please use AsyncImageState.downloadProgress instead"
    }
    require(request.target == null) {
        "target is not supported in compose"
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    return remember() {
        AsyncImageState(sketch, request, lifecycle).apply {
            Log.d("NewAsyncImageTest", "$logModule. new. ${request.uriString}")
        }
    }
}

@Stable
class AsyncImageState internal constructor(
    val sketch: Sketch,
    val request: DisplayRequest,
    val lifecycle: Lifecycle
) : RememberObserver {

    val logModule = "AsyncImageState@${Integer.toHexString(hashCode())}"

    var loadState: LoadState? by mutableStateOf(null)
    var progress: Progress? by mutableStateOf(null)
    var painterState: State? by mutableStateOf(null)

    var size: IntSize? by mutableStateOf(null)
    var contentScale: ContentScale? by mutableStateOf(null)

    private var coroutineScope: CoroutineScope? = null
    private var loadImageJob: Disposable<DisplayResult>? = null

    //    private val sizeResolver = ComposeSizeResolver(snapshotFlow { size })
    private val listener = object : Listener<DisplayRequest, Success, Error> {
        override fun onStart(request: DisplayRequest) {
            loadState = LoadState.Started
            progress = null
        }

        override fun onSuccess(request: DisplayRequest, result: Success) {
            loadState = LoadState.Success(result)
            painterState = State.Success(result.drawable.toPainter(), result)
        }

        override fun onError(request: DisplayRequest, result: Error) {
            loadState = LoadState.Error(result)
            painterState = State.Error(result.drawable?.toPainter(), result)
        }

        override fun onCancel(request: DisplayRequest) {
            loadState = LoadState.Canceled
        }
    }
    private val progressListener =
        ProgressListener<DisplayRequest> { _, totalLength, completedLength ->
            progress = Progress(
                totalLength = totalLength,
                completedLength = completedLength
            )
        }
    private val target = AsyncImageDisplayTarget(object : DisplayTarget {

        override val supportDisplayCount: Boolean = true

        override fun onStart(placeholder: Drawable?) {
            painterState = State.Loading(placeholder?.toPainter())
        }

//        override fun onSuccess(result: Drawable) {
//            super.onSuccess(result)
//        }
//
//        override fun onError(error: Drawable?) {
//            super.onError(error)
//        }
    })

    fun restart() {
        cancel()
        loadImage()
    }

    override fun onRemembered() {
        // onRemembered will be executed multiple times
        if (coroutineScope != null) return

        val coroutineScope1 = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        this.coroutineScope = coroutineScope1

//        coroutineScope1.launch {
//            snapshotFlow { size }.filterNotNull().collect {
//                Log.d("NewAsyncImageTest", "$logModule. sizeChanged: $size. ${request.uriString}")
//                loadImage()
//            }
//        }
//        coroutineScope1.launch {
//            snapshotFlow { contentScale }.filterNotNull().collect {
//                Log.d("NewAsyncImageTest", "$logModule. contentScaleChanged: ${contentScale?.name}. ${request.uriString}")
//                loadImage()
//            }
//        }

        Log.d("NewAsyncImageTest", "$logModule. onRemembered. ${request.uriString}")
//        loadImage()
    }

    override fun onAbandoned()  {
        Log.d("NewAsyncImageTest", "$logModule. onAbandoned. ${request.uriString}")
        onForgotten()
    }

    override fun onForgotten() {
        Log.d("NewAsyncImageTest", "$logModule. onForgotten. ${request.uriString}")
        coroutineScope?.cancel()
        coroutineScope = null
        cancel()
    }

    private fun loadImage() {
        val noSetSize = request.definedOptions.resizeSizeResolver == null
        val noSetScale = request.definedOptions.resizeScaleDecider == null
        val defaultLifecycleResolver = request.lifecycleResolver.isDefault()
        val contentScale = contentScale
        val size = size?.takeIf { !it.isEmpty() }
        if (noSetScale && contentScale == null) {
            Log.d("NewAsyncImageTest", "$logModule. loadImage. contentScale is null. ${request.uriString}")
            return
        }
        if (noSetSize && size == null) {
            Log.d("NewAsyncImageTest", "$logModule. loadImage. size is null. ${request.uriString}")
            return
        }
        Log.d("NewAsyncImageTest", "$logModule. loadImage. enqueue. ${request.uriString}")
        val fullRequest = request.newDisplayRequest {
            if (noSetSize && size != null) {
                resizeSize(size.toSketchSize())
            }
            if (noSetScale && contentScale != null) {
                resizeScale(contentScale.toScale())
            }
            if (defaultLifecycleResolver) {
                lifecycle(lifecycle)
            }
            target(target)
            listener(listener)
            progressListener(progressListener)
        }
        loadImageJob = sketch.enqueue(fullRequest)
    }

    private fun cancel() {
        val loadJob = loadImageJob
        if (loadJob != null && !loadJob.isDisposed) {
            loadJob.dispose()
        }
    }

//    private fun updateRequest(request: DisplayRequest): DisplayRequest {
//        val noSetSize = request.definedOptions.resizeSizeResolver == null
//        val defaultLifecycleResolver = request.lifecycleResolver.isDefault()
//        return if (noSetSize || defaultLifecycleResolver) {
//            request.newDisplayRequest {
//                if (noSetSize) {
//                    resizeSize(composeSizeResolver)
//                }
//                if (defaultLifecycleResolver) {
//                    lifecycle(lifecycle)
//                }
//            }
//        } else {
//            request
//        }
//    }

    sealed interface LoadState {
        data object Started : LoadState
        data class Success(val result: DisplayResult.Success) : LoadState
        data class Error(val result: DisplayResult.Error) : LoadState
        data object Canceled : LoadState
    }

//    private class ComposeSizeResolver(val sizeFlow: Flow<IntSize?>) : SizeResolver {
//
//        override suspend fun size(): SketchSize {
//            return sizeFlow
//                .filterNotNull()
//                .mapNotNull {
//                    if (it.width > 0 && it.height > 0)
//                        SketchSize(it.width, it.height) else null
//                }.first()
//        }
//    }
}

data class Progress(val totalLength: Long, val completedLength: Long)