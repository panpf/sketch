# 下载图片到磁盘

翻译：[English](download_request.md)

使用 [DownloadRequest] 可以将图片下载到磁盘，如下：

```kotlin
DownloadRequest(context, "https://www.sample.com/image.jpg") {
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
    val result: DownloadResult = DownloadRequest(context, "https://www.sample.com/image.jpg").execute()
    if (result is DownloadResult.Success) {
        val input: InputStream = result.data.newInputStream()
        // ...
    } else if (result is DownloadResult.Error) {
        val throwable: Throwable = result.throwable
        // ...
    }
}
```

[DownloadRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DownloadRequest.kt