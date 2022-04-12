# 加载图片获取 Bitmap

使用 [LoadRequest] 可以加载图片获得 Bitmap，如下：

```kotlin
val request = LoadRequest(context, "https://www.sample.com/image.jpg") {
    listener(
        onSuccess = { request: LoadRequest, result: LoadResult.Success ->
            val bitmap = result.bitmap
            // ...
        }, onSuccess = { request: LoadRequest, result: LoadResult.Error ->
            val exception: SketchException = result.exception
            // ...
        }
    )
}
sketch.enqueue(request)
```

当你需要同步获取加载结果时你可以使用 execute 方法，如下：

```kotlin
coroutineScope.launch(Dispatchers.Main) {
    val result: LoadResult = withContext(Dispatchers.IO) {
        sketch.execute(LoadRequest(context, "https://www.sample.com/image.jpg"))
    }
    if (result is LoadResult.Success) {
        val bitmap = result.bitmap
        // ...
    } else if (result is LoadResult.Error) {
        val exception: SketchException = result.exception
        // ...
    }
}
```

[LoadRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/LoadRequest.kt