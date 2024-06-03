# Listener

翻译：[English](listener.md)

[ImageRequest] 通过 [Listener] 和 [ProgressListener] 可以监听开始、完成、错误、取消、进度，如下：

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
    // 或 
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

还支持 kotlin 函数方式监听状态：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    listener(
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
}
```

监听下载进度：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    progressListener { request: ImageRequest, progress: Progress ->
        // ...
    }
    // 或 
    addProgressListener { request: ImageRequest, progress: Progress ->
        // ...
    }
}
```

> 注意：
> 1. 所有方法都将在主线程执行


[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[Listener]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Listener.kt

[ProgressListener]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ProgressListener.kt