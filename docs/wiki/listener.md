# Listener

Translations: [简体中文](listener_zh.md)

## Compose

In Compose you have to pass loadState and progress of [AsyncImageState] Properties to monitor the
status and progress of the request. For specific reasons, please refer
to [《Compose》](compose.md#listenerprogresslistener), as follows:

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

[ImageRequest] You can monitor start, completion, error, cancellation, and progress
through [Listener] and [ProgressListener], as follows:

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
> All callbacks will be executed on the main thread

## SketchImageView

[SketchImageView] provides Flow method to monitor the status and progress of requests, as follows:

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

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[Listener]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Listener.kt

[ProgressListener]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ProgressListener.kt

[SketchImageView]: ../../sketch-extensions-view-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[AsyncImageState]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImageState.common.kt