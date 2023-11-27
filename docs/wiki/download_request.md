# Download Image

Translations: [简体中文](download_request_zh.md)

Use [DownloadRequest] to download images to disk, as follows:

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

When you need to get the download result synchronously, you can use the execute method, as follows:

```kotlin
coroutineScope.launch(Dispatchers.Main) {
    val result: DownloadResult =
        DownloadRequest(context, "https://www.sample.com/image.jpg").execute()
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