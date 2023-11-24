# 开始使用

## 支持的 URI

| 协议                     | 描述               | 创建函数             |
|:-----------------------|:-----------------|:-----------------|
| http://, https://      | File in network  | _                |
| /, file://             | File in SDCard   | newFileUri()     |
| content://             | Content Resolver | _                |
| asset://               | Asset Resource   | newAssetUri()    |
| android.resource://    | Android Resource | newResourceUri() |
| data:image/, data:img/ | Base64           | newBase64Uri()   |
| app.icon://            | App Icon         | newAppIconUri()  |

> 上表中的 `创建函数` 列展示了 Sketch 对部分 URI 提供的便捷创建函数

每一种 URI 都有对应的 Fetcher 对其提供支持，[查看更多 Fetcher 介绍以及如何扩展新的 URI][fetcher]

## 支持的图片类型

| 类型            | API 限制      | 额外依赖模块                               |
|:--------------|:------------|:-------------------------------------|
| jpeg          | _           | _                                    |
| png           | _           | _                                    |
| bmp           | _           | _                                    |
| webp          | _           | _                                    |
| svg           | _           | sketch-svg                           |
| heif          | Android 9+  | _                                    |
| gif           | _           | sketch-gif-movie<br>sketch-gif-koral |
| webp Animated | Android 9+  | _                                    |
| heif Animated | Android 11+ | _                                    |
| video frames  | _           | sketch-video<br>sketch-video-ffmpeg  |

每一种图片类型都有对应的 Decoder
对其提供支持，[查看更多 Decoder 介绍以及如何扩展新的图片类型][decoder]

## Sketch

[Sketch] 类用来执行 [ImageRequest]，并处理图片下载、缓存、解码、转换、请求管理、内存管理等功能。

### 单例模式

默认情况下推荐依赖 `sketch` 模块，它提供了 Sketch 的单例以及一些便捷的扩展函数

单例模式下可以通过 Context 的扩展函数获取 Sketch，如下：

```kotlin
val sketch = context.sketch
```

#### 自定义 Sketch

方法 1：在 Application 类上实现 [SketchFactory] 接口来创建并配置 Sketch ，如下：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            logger(Logger(DEBUG))
            httpStack(OkHttpStack.Builder().build())
        }.build()
    }
}
```

方法 2：创建并配置 Sketch 然后通过 `SketchSingleton.setSketch()` 方法设置为单例，如下：

```kotlin
val sketch = Sketch.Builder(context).apply {
    logger(Logger(DEBUG))
    httpStack(OkHttpStack.Builder().build())
}.build()

SketchSingleton.setSketch(sketch)

// 或者

SketchSingleton.setSketch(SketchFactory {
    Sketch.Builder(context).apply {
        logger(Logger(DEBUG))
        httpStack(OkHttpStack.Builder().build())
    }.build()
})
```

### 非单例模式

如果不想使用单例模式，可以依赖 `sketch-core` 模块，然后通过 [Sketch].Builder 创建一个 [Sketch]
实例，如下：

```kotlin
val sketch = Sketch.Builder(context).apply {
    logger(Logger(DEBUG))
    httpStack(OkHttpStack.Builder().build())
}.build()
```

> 更多可配置参数请参考 [Sketch].Builder 类

## ImageRequest

[ImageRequest] 接口定义了显示图片所需的全部参数，例如 uri、Target、转换配置、调整尺寸等。

[ImageRequest] 分为以下三种：

* [DisplayRequest]：请求结果是 Drawable，用于显示图片到 ImageView、RemoteViews 或 Compose Painter
* [LoadRequest]：请求结果是 Bitmap，用于需要直接操作 Bitmap 的场景，不支持内存缓存，所有动图将会被解码成静态图
* [DownloadRequest]：请求结果是 [DiskCache].Snapshot 或 ByteArray，用于提前下载图片或直接访问图片文件

### 创建 ImageRequest

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

可以通过 DisplayRequest.Builder 提供的链式方法或同名函数提供的尾随 lambda
配置请求，更多配置参数请参考 [DisplayRequest].Builder 类

### 执行 ImageRequest

#### 单例模式

单例模式下可以通过提供的扩展函数 enqueue() 或 execute() 将 [ImageRequest] 交给 [Sketch] 执行：

```kotlin
/*
 * 将 ImageRequest 放入任务队列在后台线程上异步执行并返回一个 Disposable
 */
val request1 = DisplayRequest(imageView, "https://www.example.com/image.jpg")
request1.enqueue()

/*
 * 将 ImageRequest 放入任务队列在后台线程上异步执行并在当前协程中等待返回结果
 */
val request2 = DisplayRequest(context, "https://www.example.com/image.jpg")
coroutineScope.launch(Dispatchers.Main) {
    val result: DisplayResult = request2.execute()
    imageView.setImageDrawable(result.drawable)
}
```

#### 非单例模式

非单例模式下需要创建 Sketch 实例并通过其 enqueue() 或 execute() 方法执行请求：

```kotlin
val sketch = Sketch.Builder(context).build()

/*
 * 将 ImageRequest 放入任务队列在后台线程上异步执行并返回一个 Disposable
 */
val request1 = DisplayRequest(imageView, "https://www.example.com/image.jpg")
sketch.enqueue(request1)

/*
 * 将 ImageRequest 放入任务队列在后台线程上异步执行并在当前协程中等待返回结果
 */
val request2 = DisplayRequest(context, "https://www.example.com/image.jpg")
coroutineScope.launch(Dispatchers.Main) {
    val result: DisplayResult = sketch.execute(request2)
    imageView.setImageDrawable(result.drawable)
}
```

### 获取结果

[Sketch] 会将结果交给 [DisplayRequest] 的 target 去显示 Drawable，如果没有设置 target 就需要主动获取结果来处理它了

使用 enqueue() 方法执行请求时通过返回的 [Disposable].job 即可获取结果，如下:

```kotlin
val request = DisplayRequest(context, "https://www.example.com/image.jpg")
val disposable = request.enqueue()
coroutineScope.launch(Dispatchers.Main) {
    val result: DisplayResult = disposable.job.await()
    imageView.setImageDrawable(result.drawable)
}
```

使用 execute() 方法执行请求时可直接获取结果，如下：

```kotlin
val request = DisplayRequest(context, "https://www.example.com/image.jpg")
coroutineScope.launch(Dispatchers.Main) {
    val result: DisplayResult = request.execute()
    imageView.setImageDrawable(result.drawable)
}
```

### 取消 ImageRequest

#### 自动取消

[ImageRequest] 会在下列情况下自动取消:

* request.lifecycle 变为 DESTROYED 状态
* request.target 是一个 [ViewDisplayTarget] 并且 view 的 onViewDetachedFromWindow() 方法被执行

#### 主动取消

使用 enqueue() 方法执行请求时会返回一个 [Disposable], 可以用来它取消请求，如下:

```kotlin
val disposable = DisplayRequest(imageView, "https://www.example.com/image.jpg").enqueue()

// 在需要的时候取消请求
disposable.dispose()
```

使用 execute() 方法执行请求时可以通过其协程的 Job 来取消，如下：

```kotlin
val job = coroutineScope.launch(Dispatchers.Main) {
    val result: DisplayResult = DisplayRequest(context, "https://www.example.com/image.jpg")
        .execute()
    imageView.setImageDrawable(result.drawable)
}

// 在需要的时候取消请求
job.cancel()
```

## ImageView 扩展

Sketch 给 ImageView 提供了一系列的扩展，如下:

### 显示图片

> 仅单例模式下可用

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
        val request: DisplayRequest = displayResult.request
        val requestKey: String = displayResult.requestKey
        val requestCacheKey: String = displayResult.requestCacheKey
        val drawable: Drawable = displayResult.drawable
        val imageInfo: ImageInfo = displayResult.imageInfo
        val dataFrom: DataFrom = displayResult.dataFrom
        val transformedList: List<String>? = displayResult.transformedList
        val extras: Map<String, String>? = displayResult.extras
        // ...
    }
    is DisplayResult.Error -> {
        val request: DisplayRequest = displayResult.request
        val drawable: Drawable = displayResult.drawable
        val throwable: Throwable = displayResult.throwable
        // ...
    }
}
```

[comment]: <> (wiki)

[fetcher]: fetcher.md

[decoder]: decoder.md


[comment]: <> (class)

[Sketch]: ../../sketch-core/src/main/java/com/github/panpf/sketch/Sketch.kt

[SketchFactory]: ../../sketch/src/main/java/com/github/panpf/sketch/SketchFactory.kt

[ImageRequest]: ../../sketch-core/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageResult]: ../../sketch-core/src/main/java/com/github/panpf/sketch/request/ImageResult.kt

[Disposable]: ../../sketch-core/src/main/java/com/github/panpf/sketch/request/Disposable.kt

[DisplayRequest]: ../../sketch-core/src/main/java/com/github/panpf/sketch/request/DisplayRequest.kt

[LoadRequest]: ../../sketch-core/src/main/java/com/github/panpf/sketch/request/LoadRequest.kt

[DownloadRequest]: ../../sketch-core/src/main/java/com/github/panpf/sketch/request/DownloadRequest.kt

[ViewDisplayTarget]: ../../sketch-core/src/main/java/com/github/panpf/sketch/target/ViewDisplayTarget.kt

[DiskCache]: ../../sketch-core/src/main/java/com/github/panpf/sketch/cache/DiskCache.kt
