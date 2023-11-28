# Listener

Translations: [简体中文](listener_zh.md)

[ImageRequest] You can monitor start, completion, error, cancellation, and progress
through [Listener] and [ProgressListener], as follows:

```kotlin
DisplayRequest(context, "https://www.sample.com/image.jpg") {
    listener(object : Listener {
        override fun onStart(request: DisplayRequest) {
            // ...
        }

        override fun onSuccess(request: DisplayRequest, result: DisplayResult.Success) {
            // ...
        }

        override fun onError(request: DisplayRequest, result: DisplayResult.Error) {
            // ...
        }

        override fun onCancel(request: DisplayRequest) {
            // ...
        }
    })
}
```

It also supports kotlin function monitoring status:

```kotlin
DisplayRequest(context, "https://www.sample.com/image.jpg") {
    listener(
        onStart = { request: DisplayRequest ->
            // ...
        },
        onSuccess = { request: DisplayRequest, result: DisplayResult.Success ->
            // ...
        },
        onError = { request: DisplayRequest, result: DisplayResult.Error ->
            // ...
        },
        onCancel = { request: DisplayRequest ->
            // ...
        },
    )
}
```

Monitor download progress:

```kotlin
DisplayRequest(context, "https://www.sample.com/image.jpg") {
    progressListener { request: DisplayRequest, totalLength: Long, completedLength: Long ->
        // ...
    }
}
```

> Notice:
> 1. All methods will be execute on the main thread
> 2. [LoadRequest] and [DownloadRequest] are used exactly the same as [DisplayRequest], except that
     the types of Request and Result in the callback method are different. This is because they
     require different results.


[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[LoadRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/LoadRequest.kt

[DownloadRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DownloadRequest.kt

[DisplayRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DisplayRequest.kt

[Listener]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/Listener.kt

[ProgressListener]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ProgressListener.kt