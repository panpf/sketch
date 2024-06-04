


[//]: # (TODO)

## 下载图片到磁盘

翻译：[English](preload.md)

使用 [DownloadRequest] 可以将图片下载到磁盘，如下：

```kotlin
DownloadRequest(context, "https://example.com/image.jpg") {
    listener(
        onSuccess = { request: DownloadRequest, result: DownloadResult.Success ->
            val input: InputStream = result.data.newInputStream()
            // ...
        },
        onError = { request: DownloadRequest, result: DownloadResult.Error ->
            val throwable: Throwable = result.throwable
            // ...
        }
    )
}.enqueue()
```

当你需要同步获取下载结果时你可以使用 execute 方法，如下：

```kotlin
coroutineScope.launch(Dispatchers.Main) {
    val result: DownloadResult = DownloadRequest(context, "https://example.com/image.jpg").execute()
    if (result is DownloadResult.Success) {
        val input: InputStream = result.data.newInputStream()
        // ...
    } else if (result is DownloadResult.Error) {
        val throwable: Throwable = result.throwable
        // ...
    }
}
```

[DownloadRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/DownloadRequest.kt

# LoadRequest

翻译：[English](load_request.md)

使用 [LoadRequest] 可以加载图片获得 Bitmap，如下：

```kotlin
LoadRequest(context, "https://example.com/image.jpg") {
    listener(
        onSuccess = { request: LoadRequest, result: LoadResult.Success ->
            val bitmap = result.bitmap
            // ...
        },
        onError = { request: LoadRequest, result: LoadResult.Error ->
            val throwable: Throwable = result.throwable
            // ...
        }
    )
}.enqueue()
```

当你需要同步获取加载结果时你可以使用 execute 方法，如下：

```kotlin
coroutineScope.launch(Dispatchers.Main) {
    val result: LoadResult = LoadRequest(context, "https://example.com/image.jpg").execute()
    if (result is LoadResult.Success) {
        val bitmap = result.bitmap
        // ...
    } else if (result is LoadResult.Error) {
        val throwable: Throwable = result.throwable
        // ...
    }
}
```

> [!TIP]
> LoadRequest 不会从内存缓存中获取 Bitmap，也不会将得到的 Bitmap 放入内存缓存中，因为 LoadRequest
> 返回的 Bitmap 完全交给用户使用，不受 Sketch 控制

[LoadRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/LoadRequest.kt

## 预加载图片到内存

翻译：[English](preloading.md)

要想将图片预加载到内存中只需要不设置 target 即可，如下：

```kotlin
DisplayImage(context, "https://example.com/image.jpg") {
    // more ...
}.enqueue()
```

为了确保在后面使用时准确的命中缓存，需要预加载时的配置和使用时一模一样，特别是 resizeSize