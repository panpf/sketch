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

[LoadRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/LoadRequest.kt