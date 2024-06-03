# Listener

Translations: [简体中文](listener_zh.md)

[ImageRequest] You can monitor start, completion, error, cancellation, and progress
through [Listener] and [ProgressListener], as follows:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    listener(object : Listener {
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
    // or
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
}
```

It also supports kotlin function monitoring status:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
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
}
```

Monitor download progress:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    addProgressListener { request: ImageRequest, progress: Progress ->
        // ...
    }
}
```

> Notice:
> 1. All methods will be execute on the main thread


[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[Listener]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Listener.kt

[ProgressListener]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ProgressListener.kt