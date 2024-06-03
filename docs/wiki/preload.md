

## Download Image

Translations: [简体中文](preload_zh)

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

[DownloadRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/DownloadRequest.kt

# LoadRequest

Translations: [简体中文](load_request_zh.md)

Use [LoadRequest] to load an image and obtain a Bitmap, as follows:

```kotlin
LoadRequest(context, "https://www.sample.com/image.jpg") {
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

When you need to obtain the loading results synchronously, you can use the execute method, as
follows:

```kotlin
coroutineScope.launch(Dispatchers.Main) {
    val result: LoadResult = LoadRequest(context, "https://www.sample.com/image.jpg").execute()
    if (result is LoadResult.Success) {
        val bitmap = result.bitmap
        // ...
    } else if (result is LoadResult.Error) {
        val throwable: Throwable = result.throwable
        // ...
    }
}
```

> Note: LoadRequest will not obtain the Bitmap from the memory cache, nor will it put the obtained
> Bitmap into the memory cache, because the Bitmap returned by LoadRequest is completely handed over
> to the user and is not controlled by Sketch.

[LoadRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/LoadRequest.kt

## Preload images into memory

Translations: [简体中文](preloading_zh.md)

To preload images into memory, you only need to not set target, as follows:

```kotlin
DisplayImage(context, "https://www.sample.com/image.jpg") {
    // more ...
}.enqueue()
```

In order to ensure that the cache is accurately hit when used later, the configuration during
preloading needs to be exactly the same as when used, especially resizeSize
