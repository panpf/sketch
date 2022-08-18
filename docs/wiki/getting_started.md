# 入门

## 支持的 URI

|协议|描述|创建函数|
|:---|:---|:---|
|http://, https:// |File in network|_|
|/, file:// |File in SDCard|newFileUri()|
|content://|Content Resolver|_|
|asset:// |Asset Resource|newAssetUri()|
|android.resource:// |Android Resource|newResourceUri()|
|data:image/, data:img/ |Base64|newBase64Uri()|
|app.icon:// |App Icon|newAppIconUri()|

> 上表中的 `创建函数` 列展示了 Sketch 对部分 URI 提供的便捷创建函数

每一种 URI 都有对应的 Fetcher 对其提供支持，[点击查看更多 Fetcher 介绍以及如何扩展新的 URI][fetcher]

## 支持的图片类型

|类型|API 限制|依赖模块|
|:---|:---|:---|
|jpeg|_|_|
|png|_|_|
|bmp|_|_|
|webp|_|_|
|svg|_|sketch-svg|
|heif|Android 9+|_|
|gif|_|sketch-gif-movie<br>sketch-gif-koral|
|webp Animated|Android 9+|_|
|heif Animated|Android 11+|_|
|video frames|_|sketch-video<br>sketch-video-ffmpeg|

每一种图片类型都有对应的 Decoder 对其提供支持，[点击查看更多 Decoder 介绍以及如何扩展新的图片类型][decoder]

## Sketch

[Sketch] 类用来执行 [ImageRequest]，并处理图片下载、缓存、解码、转换、请求管理、内存管理等功能。

[Sketch] 类是单例的，可以通过 Context 的扩展函数获取，如下：

```kotlin
val sketch = context.sketch
```

您可以在你的 Application 类上实现 [SketchFactory] 接口来配置 Sketch ，如下：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch = Sketch.Builder(this).apply {
        logger(Logger(DEBUG))
        httpStack(OkHttpStack.Builder().build())
    }.build()
}
```

更多可配置参数请参考 [Sketch].Builder 类

## ImageRequest

[ImageRequest] 接口定义了显示图片所需的全部参数，例如 URI、ImageView、转换配置、调整尺寸等。

[ImageRequest] 分为以下三种：

* [DisplayRequest]：请求结果是 Drawable，用于显示图片到 ImageView、RemoteViews 或 Compose Painter 上
* [LoadRequest]：请求结果是 Bitmap，用于需要直接操作 Bitmap 的场景
* [DownloadRequest]：请求结果是 [DiskCache].Snapshot 或 ByteArray，用于提前下载图片或直接访问图片文件

### 创建请求

`以 DisplayRequest 为例，另外两种大同小异`

Builder 方式：

```kotlin
val request = DisplayRequest.Builder(context, "https://www.example.com/image.jpg")
    .placeholder(R.drawable.image)
    .transformations(CircleCropTransformation())
    .target(imageView)
    .build()
```

同名函数方式：

```kotlin
val request = DisplayRequest(context, "https://www.example.com/image.jpg") {
    placeholder(R.drawable.image)
    transformations(CircleCropTransformation())
    target(imageView)
}

// 或者

val request1 = DisplayRequest(imageView, "https://www.example.com/image.jpg") {
    placeholder(R.drawable.image)
    transformations(CircleCropTransformation())
}
```

您可以通过 Builder 提供的链式方法或同名函数提供的尾随 lambda 配置请求

更多可配置参数请参考 [DisplayRequest].Builder 类

### 执行请求

[ImageRequest] 创建好后调用其 enqueue() 或 execute() 方法将 [ImageRequest] 交给 [Sketch] 执行：

* enqueue()：将 [ImageRequest] 放入任务队列在后台线程上异步执行并返回一个 [Disposable]
* execute()：在当前协程中执行 [ImageRequest] 并返回一个 [ImageResult]

enqueue 示例：

```kotlin
DisplayRequest(imageView, "https://www.example.com/image.jpg").enqueue()
```

execute 示例：

```kotlin
coroutineScope.launch(Dispatchers.Main) {
    val result: DisplayResult = DisplayRequest(context, "https://www.example.com/image.jpg")
        .execute()
    imageView.setImageDrawable(result.drawable)
}
```

### 取消请求

[ImageRequest] 会在下列情况下自动取消:

* request.lifecycle 变为 DESTROYED 状态
* request.target 是一个 [ViewDisplayTarget] 并且 view 的 onViewDetachedFromWindow() 方法被执行

另外, enqueue() 方法会返回一个 [Disposable], 它可以用来取消请求，如下:

```kotlin
val disposable = DisplayRequest(imageView, "https://www.example.com/image.jpg").enqueue()

// 在你需要的时候取消请求
disposable.dispose()
```

## ImageView 扩展

Sketch 给 ImageView 提供了一系列的扩展，如下:

### 显示图片

displayImage() 扩展函数，用于将 URI 指向的图片显示到 ImageView 上

```kotlin
imageView.displayImage("https://www.example.com/image.jpg")
```

上述调用等价于：

```kotlin
DisplayRequest(imageView, "https://www.example.com/image.jpg").enqueue()
```

还可以通过 displayImage 函数尾随的 lambda 配置参数：

```kotlin
imageView.displayImage("https://www.example.com/image.jpg") {
    placeholder(R.drawable.image)
    transformations(CircleCropTransformation())
    crossfade(true)
}
```

### 取消请求

```kotlin
imageView.disposeDisplay()
```

### 获取结果

```kotlin
val displayResult = imageView.displayResult
when (displayResult) {
    is DisplayResult.Success -> {
        val drawable = displayResult.drawable
        val request = displayResult.request
        val imageInfo = displayResult.imageInfo
        val dataFrom = displayResult.dataFrom
        val transformedList = displayResult.transformedList
        // ...
    }
    is DisplayResult.Error -> {
        val drawable = displayResult.drawable
        val request = displayResult.request
        val exception = displayResult.exception
        // ...
    }
}
```

[comment]: <> (wiki)

[fetcher]: fetcher.md

[decoder]: decoder.md


[comment]: <> (class)

[Sketch]: ../../sketch/src/main/java/com/github/panpf/sketch/Sketch.kt

[SketchFactory]: ../../sketch/src/main/java/com/github/panpf/sketch/SketchFactory.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageResult]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageResult.kt

[Disposable]: ../../sketch/src/main/java/com/github/panpf/sketch/request/Disposable.kt

[DisplayRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/DisplayRequest.kt

[LoadRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/LoadRequest.kt

[DownloadRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/DownloadRequest.kt

[ViewDisplayTarget]: ../../sketch/src/main/java/com/github/panpf/sketch/target/ViewDisplayTarget.kt

[DiskCache]: ../../sketch/src/main/java/com/github/panpf/sketch/cache/DiskCache.kt
