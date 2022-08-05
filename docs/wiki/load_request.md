# 加载图片获取 Bitmap

使用 [LoadRequest] 可以加载图片获得 Bitmap，如下：

```kotlin
LoadRequest(context, "https://www.sample.com/image.jpg") {
    listener(
        onSuccess = { request: LoadRequest, result: LoadResult.Success ->
            val bitmap = result.bitmap
            // ...
        }, onSuccess = { request: LoadRequest, result: LoadResult.Error ->
            val exception: SketchException = result.exception
            // ...
        }
    )
}.enqueue()
```

当你需要同步获取加载结果时你可以使用 execute 方法，如下：

```kotlin
coroutineScope.launch(Dispatchers.Main) {
    val result: LoadResult = LoadRequest(context, "https://www.sample.com/image.jpg").execute()
    if (result is LoadResult.Success) {
        val bitmap = result.bitmap
        // ...
    } else if (result is LoadResult.Error) {
        val exception: SketchException = result.exception
        // ...
    }
}
```

> 注意：LoadRequest 不会从内存缓存中获取 Bitmap，也不会将得到的 Bitmap 放入内存缓存中，因为 LoadRequest 返回的 Bitmap 完全交给用户使用，不受 Sketch 控制

[LoadRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/LoadRequest.kt