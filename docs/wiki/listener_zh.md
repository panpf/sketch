# Listener

翻译：[English](listener.md)

## Compose

在 Compose 中你必须通过 [AsyncImageState] 的 loadState 和 progress
属性来监听请求的状态和进度，具体原因请参考 [《Compose》](compose_zh.md#listenerprogresslistener)，如下：

```kotlin
val state = rememberAsyncImageState()
val loadState: LoadState? = state.loadState
when (loadState) {
    is Started -> {

    }
    is Success -> {

    }
    is Error -> {

    }
    is Canceled -> {

    }
    else -> {
        // null
    }
}
val progress: Progress? = state.progress
AsyncImage(
    uri = imageUri,
    contentDescription = "photo",
    state = state
)
```

## Android View

[ImageRequest] 通过 [Listener] 和 [ProgressListener] 可以监听开始、完成、错误、取消、进度，如下：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    addListener(object : Listener {
        override fun onStart(request: ImageRequest) {
            // ...
        }

        override fun onSuccess(request: ImageRequest, result: ImageResult.Success) {
            // ...
        }

        override fun onError(request: ImageRequest, error: ImageResult.Error) {
            // ...
        }

        override fun onCancel(request: ImageRequest) {
            // ...
        }
    })
    // 或
    addListener(
        onStart = { request: ImageRequest ->
            // ...
        },
        onSuccess = { request: ImageRequest, result: ImageResult.Success ->
            // ...
        },
        onError = { request: ImageRequest, error: ImageResult.Error ->
            // ...
        },
        onCancel = { request: ImageRequest ->
            // ...
        },
    )

    addProgressListener { request: ImageRequest, progress: Progress ->
        // ...
    }
}
```

> [!TIP]
> 所有回调都将在主线程执行

## SketchImageView

[SketchImageView] 提供了 Flow 的方式来监听请求的状态和进度，如下：

```kotlin
val sketchImageView = SketchImageView(context)
scope.launch {
    sketchImageView.requestState.loadState.collect { loadState ->
        when (loadState) {
            is Started -> {

            }
            is Success -> {

            }
            is Error -> {

            }
            is Canceled -> {

            }
            else -> {
                // null
            }
        }
    }
}

scope.launch {
    sketchImageView.requestState.progressState.collect { progress ->

    }
}
```

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Listener]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Listener.kt

[ProgressListener]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ProgressListener.kt

[SketchImageView]: ../../sketch-extensions-view/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[AsyncImageState]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImageState.common.kt