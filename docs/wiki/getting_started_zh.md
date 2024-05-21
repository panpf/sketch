# 开始使用

翻译：[English](getting_started.md)

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

| 类型            | API 限制      | 额外依赖模块                                      |
|:--------------|:------------|:--------------------------------------------|
| jpeg          | _           | _                                           |
| png           | _           | _                                           |
| bmp           | _           | _                                           |
| webp          | _           | _                                           |
| svg           | _           | sketch-svg                                  |
| heif          | Android 9+  | _                                           |
| gif           | _           | sketch-animated<br>sketch-animated-koralgif |
| webp Animated | Android 9+  | _                                           |
| heif Animated | Android 11+ | _                                           |
| video frames  | _           | sketch-video<br>sketch-video-ffmpeg         |

每一种图片类型都有对应的 Decoder
对其提供支持，[查看更多 Decoder 介绍以及如何扩展新的图片类型][decoder]

## Sketch

[Sketch] 类用来执行 [ImageRequest]，并处理图片下载、缓存、解码、转换、请求管理、内存管理等工作。

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
            logger(Logger(Logger.DEBUG))
            httpStack(OkHttpStack.Builder().build())
        }.build()
    }
}
```

方法 2：创建并配置 Sketch 然后通过 `SketchSingleton.setSketch()` 方法设置为单例，如下：

```kotlin
val sketch = Sketch.Builder(context).apply {
    logger(Logger(Logger.DEBUG))
    httpStack(OkHttpStack.Builder().build())
}.build()

SketchSingleton.setSketch(sketch)

// 或者

SketchSingleton.setSketch(SketchFactory {
    Sketch.Builder(context).apply {
        logger(Logger(Logger.DEBUG))
        httpStack(OkHttpStack.Builder().build())
    }.build()
})
```

### 非单例模式

如果不想使用单例模式，可以依赖 `sketch-core` 模块，然后通过 [Sketch].Builder 创建一个 [Sketch]
实例，如下：

```kotlin
val sketch = Sketch.Builder(context).apply {
    logger(Logger(Logger.DEBUG))
    httpStack(OkHttpStack.Builder().build())
}.build()
```

> 更多可配置参数请参考 [Sketch].Builder 类

## ImageRequest

[ImageRequest] 接口定义了显示图片所需的全部参数，例如 uri、Target、转换配置、调整尺寸等。

### 创建 ImageRequest

Builder 方式：

```kotlin
val request = ImageRequest.Builder(context, "https://www.example.com/image.jpg")
    .placeholder(R.drawable.image)
    .transformations(CircleCropTransformation())
    .target(imageView)
    .build()
```

同名函数方式：

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg") {
    placeholder(R.drawable.image)
    transformations(CircleCropTransformation())
    target(imageView)
}

// 或者

val request1 = ImageRequest(imageView, "https://www.example.com/image.jpg") {
    placeholder(R.drawable.image)
    transformations(CircleCropTransformation())
}
```

可以通过 ImageRequest.Builder 提供的链式方法或同名函数提供的尾随 lambda
配置请求，更多配置参数请参考 [ImageRequest].Builder 类

### 执行 ImageRequest

#### 单例模式

单例模式下可以通过提供的扩展函数 enqueue() 或 execute() 将 [ImageRequest] 交给 [Sketch] 执行：

```kotlin
/*
 * 将 ImageRequest 放入任务队列在后台线程上异步执行并返回一个 Disposable
 */
val request1 = ImageRequest(imageView, "https://www.example.com/image.jpg")
request1.enqueue()

/*
 * 将 ImageRequest 放入任务队列在后台线程上异步执行并在当前协程中等待返回结果
 */
val request2 = ImageRequest(context, "https://www.example.com/image.jpg")
coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = request2.execute()
    imageView.setImageDrawable(result.image.asDrawable())
}
```

#### 非单例模式

非单例模式下需要自行创建 Sketch 实例并通过其 enqueue() 或 execute() 方法执行请求：

```kotlin
val sketch = Sketch.Builder(context).build()

/*
 * 将 ImageRequest 放入任务队列在后台线程上异步执行并返回一个 Disposable
 */
val request1 = ImageRequest(imageView, "https://www.example.com/image.jpg")
sketch.enqueue(request1)

/*
 * 将 ImageRequest 放入任务队列在后台线程上异步执行并在当前协程中等待返回结果
 */
val request2 = ImageRequest(context, "https://www.example.com/image.jpg")
coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = sketch.execute(request2)
    imageView.setImageDrawable(result.image.asDrawable())
}
```

### 获取结果

[Sketch] 会将结果交给 [ImageRequest] 的 target 去显示 [Image]，如果没有设置 target 就需要主动获取结果来处理它了

使用 enqueue() 方法执行请求时通过返回的 [Disposable].job 即可获取结果，如下:

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg")
val disposable = request.enqueue()
coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = disposable.job.await()
    imageView.setImageDrawable(result.image.asDrawable())
}
```

使用 execute() 方法执行请求时可直接获取结果，如下：

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg")
coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = request.execute()
    imageView.setImageDrawable(result.image.asDrawable())
}
```

### 取消 ImageRequest

#### 自动取消

[ImageRequest] 会在下列情况下自动取消:

* request.lifecycle 变为 DESTROYED 状态
* request.target 是一个 [ViewTarget] 并且 view 的 onViewDetachedFromWindow() 方法被执行

#### 主动取消

使用 enqueue() 方法执行请求时会返回一个 [Disposable], 可以用来它取消请求，如下:

```kotlin
val disposable = ImageRequest(imageView, "https://www.example.com/image.jpg").enqueue()

// 在需要的时候取消请求
disposable.dispose()
```

使用 execute() 方法执行请求时可以通过其协程的 Job 来取消，如下：

```kotlin
val job = coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = ImageRequest(context, "https://www.example.com/image.jpg")
        .execute()
    imageView.setImageDrawable(result.image.asDrawable())
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
ImageRequest(imageView, "https://www.example.com/image.jpg").enqueue()
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
val imageResult = imageView.imageResult
when (imageResult) {
    is ImageResult.Success -> {
        val request: ImageRequest = imageResult.request
        val requestKey: String = imageResult.requestKey
        val requestCacheKey: String = imageResult.requestCacheKey
        val image: Image = imageResult.image
        when (image) {
            is BitmapImage -> {
                val bitmap: Bitmap = image.bitmap
            }
            is DrawableImage -> {
                val drawable: Drawable = image.drawable
            }
        }
        val imageInfo: ImageInfo = imageResult.imageInfo
        val dataFrom: DataFrom = imageResult.dataFrom
        val transformedList: List<String>? = imageResult.transformedList
        val extras: Map<String, String>? = imageResult.extras
        // ...
    }
    is ImageResult.Error -> {
        val request: ImageRequest = imageResult.request
        val image: Image? = imageResult.image
        when (image) {
            is BitmapImage -> {
                val bitmap: Bitmap = image.bitmap
            }
            is DrawableImage -> {
                val drawable: Drawable = image.drawable
            }
        }
        val throwable: Throwable = imageResult.throwable
        // ...
    }
}
```

## 文档

基础功能：

* [AnimatedImage：GIF、WEBP、HEIF][animated_image]
* [Resize：修改图片尺寸][resize]
* [Transformation：转换图片][transformation]
* [Transition：用炫酷的过渡方式显示图片][transition]
* [StateImage：占位图和错误图][state_image]
* [Listener：监听请求状态和下载进度][listener]
* [Cache：了解下载、结果、内存缓存][cache]
* [Fetcher：了解 Fetcher 及扩展新的 URI 类型][fetcher]
* [Decoder：了解 Decoder 及扩展新的图片类型][decoder]
* [Target：将加载结果应用到目标上][target]
* [HttpStack：了解 http 部分及使用 okhttp][http_stack]
* [SVG：解码 SVG 静态图片][svg]
* [VideoFrames：解码视频帧][video_frame]
* [Exif：纠正图片方向][exif]
* [ImageOptions：统一管理图片配置][image_options]
* [RequestInterceptor：拦截 ImageRequest][request_interceptor]
* [DecodeInterceptor：拦截 Bitmap 或 Drawable 解码][decode_interceptor]
* [DownloadRequest：下载图片到磁盘][download_request]
* [LoadRequest：加载图片获取 Bitmap][load_request]
* [预加载图片到内存][preloading]
* [Lifecycle][lifecycle]
* [Jetpack Compose][jetpack_compose]
* [日志][log]

特色功能：

* [SketchImageView：通过 XML 属性配置请求][sketch_image_view]
* [提高长图在网格列表中的清晰度][long_image_grid_thumbnails]
* [显示下载进度][show_download_progress]
* [显示图片类型角标][show_image_type]
* [蜂窝数据网络下暂停下载图片节省流量][save_cellular_traffic]
* [列表滑动中暂停加载图片][pause_load_when_scrolling]
* [显示 APK 文件或已安装 APP 的图标][apk_app_icon]

[comment]: <> (class)

[Sketch]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/Sketch.kt

[SketchFactory]: ../../sketch/src/main/kotlin/com/github/panpf/sketch/SketchFactory.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageResult]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageResult.kt

[Image]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/Image.kt

[Disposable]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/Disposable.kt

[ViewTarget]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/target/ViewTarget.kt

[DiskCache]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/cache/DiskCache.kt


[comment]: <> (wiki)

[getting_started]: getting_started_zh.md

[fetcher]: fetcher_zh.md

[decoder]: decoder_zh.md

[animated_image]: animated_image_zh.md

[resize]: resize_zh.md

[transformation]: transformation_zh.md

[transition]: transition_zh.md

[state_image]: state_image_zh.md

[listener]: listener_zh.md

[cache]: cache_zh.md

[target]: target_zh.md

[http_stack]: http_stack_zh.md

[svg]: svg_zh.md

[video_frame]: video_frame_zh.md

[exif]: exif_zh.md

[image_options]: image_options_zh.md

[request_interceptor]: request_interceptor_zh.md

[decode_interceptor]: decode_interceptor_zh.md

[preloading]: preloading_zh.md

[download_request]: download_request_zh.md

[load_request]: load_request_zh.md

[long_image_grid_thumbnails]: long_image_grid_thumbnails_zh.md

[show_image_type]: mime_type_logo_zh.md

[show_download_progress]: download_progress_indicator_zh.md

[sketch_image_view]: sketch_image_view_zh.md

[save_cellular_traffic]: save_cellular_traffic_zh.md

[pause_load_when_scrolling]: pause_load_when_scrolling_zh.md

[apk_app_icon]: apk_app_icon_zh.md

[log]: log_zh.md

[lifecycle]: lifecycle_zh.md

[jetpack_compose]: jetpack_compose_zh.md