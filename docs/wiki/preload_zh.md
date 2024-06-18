# 预加载

翻译：[English](preload.md)

## 预下载到磁盘缓存

> [!IMPORTANT]
> 必须导入 `sketch-extensions-core` 模块

你可以通过 [enqueueDownload] 或 [executeDownload] 函数将网络图片预下载到磁盘缓存，如下：

```kotlin
val imageUri = "https://example.com/image.jpg"

val disposable = sketch.enqueueDownload(imageUri)
scope.launch {
    disposable.job.await()
    val snapshot = sketch.downloadCache.withLock {
        openSnapshot(imageUri)
    }
    try {
        val bytes: ByteArray = sketch.downloadCache.fileSystem
            .source(snapshot.data).buffer().use { it.readByteArray() }
        // ...
    } finally {
        snapshot.close()
    }
}

// or
scope.launch {
    sketch.executeDownload(imageUri)
    val snapshot = sketch.downloadCache.withLock {
        openSnapshot(imageUri)
    }
    try {
        val bytes: ByteArray = sketch.downloadCache.fileSystem
            .source(snapshot.data).buffer().use { it.readByteArray() }
        // ...
    } finally {
        snapshot.close()
    }
}
```

## 预加载到内存缓存

想要将图片预加载到内存只需要不设置 [Target] 即可，但其它参数需要和使用时一样，如下：

```kotlin
val request = ImageRequest(context, "https://example.com/image.jpg") {
    size(200, 200)
    precision(Precision.LESS_PIXELS)
    scale(Scale.CENTER_CROP)
}

sketch.enqueue(request)
// or
scope.launch {
    sketch.execute(request)
}
```

> [!TIP]
> 在构建 [ImageRequest] 时需要你主动设置和使用时一致的 size、precision、scale，因为在使用时如果没有主动设置
> size、precision、scale 就会从 [Target] 上获取，这样可能会造成预加载时和使用时的 size、precision、scale
> 不一致导致无法命中缓存

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Target]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/Target.kt

[enqueueDownload]: ../../sketch-extensions-core/src/commonMain/kotlin/com/github/panpf/sketch/util/download.kt

[executeDownload]: ../../sketch-extensions-core/src/commonMain/kotlin/com/github/panpf/sketch/util/download.kt