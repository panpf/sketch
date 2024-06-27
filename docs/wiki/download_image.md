# Download Image

Translations: [简体中文](download_image_zh.md)

Sometimes we need to download pictures to the disk cache in advance, or export pictures from the
Internet to the album. In this case, we need to download the pictures first.

You can download images through the [Sketch].enqueueDownload() or executeDownload() method, as
follows:

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
> 1. Prioritize returning [DownloadData] of type [DownloadData].Cache
> 2. When [Sketch].downloadCache is not available (JS) or [ImageRequest]
     .downloadCachePolicy.readEnabled is false, [DownloadData].Bytes is returned

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[DownloadData]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/util/DownloadData.kt