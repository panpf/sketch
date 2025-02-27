# 下载图片

翻译：[English](download_image.md)

有时候我们需要提前下载图片到磁盘缓存中，或者需要将网络图片导出到相册中，这时候都需要先下载图片。

你可以通过 [Sketch].enqueueDownload() 或 executeDownload() 方法来下载图片，如下：

```kotlin
val imageUri = "https://example.com/image.jpg"

val deferred: Deferred<Result<DownloadData>> = sketch
    .enqueueDownload(ImageRequest(context, imageUri))
scope.launch {
    val result = deferred.await()
    val data: DownloadData = result.getOrNull()
    if (data != null) {
        // success
        if (data is DownloadData.Cache) {
            val path: Path = data.path
        } else if (data is DownloadData.Bytes) {
            val bytes: ByteArray = data.bytes
        }
    } else {
        // failed
        val throwable = result.exceptionOrNull()
    }
}

// or
scope.launch {
    val result = sketch.executeDownload(ImageRequest(context, imageUri))
    val data: DownloadData = result.getOrNull()
    if (data != null) {
        // success
        if (data is DownloadData.Cache) {
            val path: Path = data.path
        } else if (data is DownloadData.Bytes) {
            val bytes: ByteArray = data.bytes
        }
    } else {
        // failed
        val throwable = result.exceptionOrNull()
    }
}
```

> [!TIP]
> 1. 优先返回 [DownloadData].Cache 类型的 [DownloadData]
> 2. [Sketch].downloadCache 不可用（JS）或 [ImageRequest].downloadCachePolicy.readEnabled 为 false
     时返回的是 [DownloadData].Bytes

[ImageRequest]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Sketch]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[DownloadData]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/util/DownloadData.kt