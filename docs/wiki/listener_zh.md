# Listener

翻译：[English](listener.md)

[ImageRequest] 通过 [Listener] 和 [ProgressListener] 可以监听开始、完成、错误、取消、进度，如下：

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

还支持 kotlin 函数方式监听状态：

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

监听下载进度：

```kotlin
DisplayRequest(context, "https://www.sample.com/image.jpg") {
    progressListener { request: DisplayRequest, totalLength: Long, completedLength: Long ->
        // ...
    }
}
```

> 注意：
> 1. 所有方法都将在主线程回调
> 2. [LoadRequest] 和 [DownloadRequest] 同 [DisplayRequest] 用法一模一样，只是回调方法的 Request 和 Result 的类型不一样，这是因为他们需要的结果不一样


[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[LoadRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/LoadRequest.kt

[DownloadRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DownloadRequest.kt

[DisplayRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DisplayRequest.kt

[Listener]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/Listener.kt

[ProgressListener]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ProgressListener.kt