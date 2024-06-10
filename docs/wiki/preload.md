## Preload

## Pre-download to disk cache

> [!IMPORTANT]
> The `sketch-extensions-core` module must be imported

You can pre-download network images to the disk cache through the [enqueueDownload]
or [executeDownload] function, as follows:

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

## Preload into memory cache

If you want to preload the image into memory, you only need to not set [Target], but other
parameters need to be the same as when used, as follows:

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
> When building [ImageRequest], you need to actively set and use consistent size, precision, and
> scale, because if there is no active setting when using Size, precision and scale will be obtained
> from [Target], which may cause inconsistencies in size, precision, and scale between preloading
> and use, resulting in failure to hit the cache.

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[Target]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/Target.kt

[enqueueDownload]: ../../sketch-extensions-core/src/commonMain/kotlin/com/github/panpf/sketch/util/download.kt

[executeDownload]: ../../sketch-extensions-core/src/commonMain/kotlin/com/github/panpf/sketch/util/download.kt
