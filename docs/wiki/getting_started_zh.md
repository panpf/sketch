# 开始使用

翻译：[English](image_request_zh.md)

用 Sketch 加载并显示图片非常简单，如下：

### Compose Multiplatform：

```kotlin
// val imageUri = "/Users/my/Downloads/image.jpg"
// val imageUri = "compose.resource://drawable/sample.png"
val imageUri = "https://www.sample.com/image.jpg"

AsyncImage(
    uri = imageUri,
    modifier = Modifier.size(300.dp, 200.dp),
    contentScale = ContentScale.Crop,
    contentDescription = "photo"
)

// config params
AsyncImage(
    rqeuest = ImageRequest(imageUri) {
        placeholder(Res.drawable.placeholder)
        error(Res.drawable.error)
        crossfade()
        // There is a lot more...
    },
    modifier = Modifier.size(300.dp, 200.dp),
    contentScale = ContentScale.Crop,
    contentDescription = "photo"
)

Image(
    painter = rememberAsyncImagePainter(
        request = ImageRequest(imageUri) {
            placeholder(Res.drawable.placeholder)
            error(Res.drawable.error)
            crossfade()
            // There is a lot more...
        },
        contentScale = ContentScale.Crop
    ),
    modifier = Modifier.size(300.dp, 200.dp),
    contentScale = ContentScale.Crop,
    contentDescription = "photo"
)
```

如上所示，在 Compose Multiplatform 上你既可以直接使用 AsyncImage 组件也可以使用 `Image + AsyncImagePainter`
来显示图片

虽然他们最终的效果是一样的，但 AsyncImage 组件会比 `Image + AsyncImagePainter` 略快一些，因为 Sketch
依赖组件的确切大小才会开始加载图片。AsyncImage 在布局阶段就可以获取到组件的大小，而 `Image + AsyncImagePainter`
则是要等到绘制阶段才能获取到组件大小。所以更推荐直接使用 AsyncImage 组件

### Android View：

```kotlin
// val imageUri = "/sdcard/download/image.jpg"
// val imageUri = "asset://image.jpg"
val imageUri = "https://www.sample.com/image.jpg"

imageView.displayImage("https://www.sample.com/image.jpg")

// config params
imageView.displayImage("https://www.sample.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
    crossfade()
    // There is a lot more...
}
```

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

## 平台差异

| 功能/平台                                                                                  | Android       | iOS             | Desktop         | Web             |
|:---------------------------------------------------------------------------------------|---------------|:----------------|:----------------|:----------------|
| 内存缓存                                                                                   | ✅             | ✅               | ✅               | ✅               |
| 结果缓存                                                                                   | ✅             | ✅               | ✅               | ❌               |
| 下载缓存                                                                                   | ✅             | ✅               | ✅               | ❌               |
| JPEG<br/>PNG<br/>WEBP<br/>BMP                                                          | ✅             | ✅               | ✅               | ✅               |
| HEIF                                                                                   | ✅ (API 28)    | ❌               | ❌               | ❌               |
| GIF 动图                                                                                 | ✅             | ✅               | ✅               | ✅               |
| WEBP 动图                                                                                | ✅ (API 28)    | ✅               | ✅               | ✅               |
| HEIF 动图                                                                                | ✅ (API 30)    | ❌               | ❌               | ❌               |
| SVG                                                                                    | ✅             | ✅<br/>(不支持 CSS) | ✅<br/>(不支持 CSS) | ✅<br/>(不支持 CSS) |
| 视频帧                                                                                    | ✅             | ❌               | ❌               | ❌               |
| http://<br/>https://<br/>/, file://<br/>compose.resource://<br/>data:image/jpeg;base64 | ✅             | ✅               | ✅               | ✅               |
| asset://<br/>content://<br/>android.resource://                                        | ✅             | ❌               | ❌               | ❌               |
| kotlin.resource://                                                                     | ❌             | ✅               | ✅               | ❌               |
| Exif Orientation                                                                       | ✅             | ✅               | ✅               | ✅               |
| 默认图片解码器                                                                                | BitmapFactory | Skia Image      | Skia Image      | Skia Image      |
| 最低 API                                                                                 | API 21        | -               | JDK 1.8         | -               |

> 最低 API 是 '-' 表示和 Compose Multiplatform 同步

## 创建 Sketch

Sketch 加载图片的流程一句话概括就是创建一个 ImageRequest，然后把 ImageRequest 交给 Sketch 执行，如下：

```
val imageRequest = ImageRequest(context, "https://www.example.com/image.jpg") {
    // config params
}
sketch.enqueue(imageRequest)
```

各自的职责如下：

* [ImageRequest] 用来定义图片的 uri、占位图、转换、过渡、新的尺寸、Target 以及 Listener 等
* [Sketch] 用来执行 [ImageRequest]，并处理下载、缓存、解码、转换以及请求管理、内存管理等工作

### 单例模式

单例模式下不需要主动创建 Sketch，可以通过以下方式获取共享的 Sketch 实例：

```kotlin
// Android
val sketch = context.sketch

// Non Android
val sketch = SingletonSketch.get()
```

需要自定义 Sketch 时可以通过以下方式主动创建 Sketch 并配置它：

```kotlin
// Android
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            logger(Logger(Logger.DEBUG))
            httpStack(OkHttpStack.Builder().build())
        }.build()
    }
}

// Non Android
SketchSingleton.setSafe {
    Sketch.Builder(context).apply {
        logger(Logger(Logger.DEBUG))
        httpStack(OkHttpStack.Builder().build())
    }.build()    
}
```

> [!TIP]
> 使用 SketchSingleton.setSafe() 方式自定义 Sketch 时需要尽可能早的调用它，最好是在 App 的入口函数中

### 非单例模式

非单例模式下需要你在合适的时候创建 Sketch 并记住它，然后在需要的时候使用你创建的实例，如下：

```kotlin
val sketch = Sketch.Builder(context).apply {
    logger(Logger(Logger.DEBUG))
    httpStack(OkHttpStack.Builder().build())
}.build()
```

## 创建 ImageRequest

Compose Multiplatform:

```kotlin
// Use a function with the same name
val request = ImageRequest("https://www.example.com/image.jpg") {
    placeholder(Res.drawable.placeholder)
    error(Res.drawable.error)
    crossfade()
    // There is a lot more...
}

// Use Builder
val context = LocalPlatformContext.current
val request1 = ImageRequest.Builder(context, "https://www.example.com/image.jpg")
    .placeholder(Res.drawable.placeholder)
    .error(Res.drawable.error)
    .crossfade()
    .build()
// There is a lot more...
```

Android View：

```kotlin
// Use a function with the same name
val request = ImageRequest(imageView, "https://www.example.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
    crossfade()
    // There is a lot more...
}

// Use Builder
val request1 = ImageRequest.Builder(context, "https://www.example.com/image.jpg")
    .placeholder(Res.drawable.placeholder)
    .error(Res.drawable.error)
    .crossfade()
    .target(imageView)
    .build()
// There is a lot more...
```

## 执行 ImageRequest

### 单例模式

单例模式下可以通过为 ImageRequest 提供的扩展函数 enqueue() 或 execute() 直接将 [ImageRequest]
交给共享的 [Sketch] 执行：

```kotlin
// 将 ImageRequest 放入任务队列在后台线程上异步执行
val request1 = ImageRequest(imageView, "https://www.example.com/image.jpg")
val disposable: Disposable = request1.enqueue()

// 将 ImageRequest 放入任务队列在后台线程上异步执行并在当前协程中等待返回结果
val request2 = ImageRequest(context, "https://www.example.com/image.jpg")
coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = request2.execute()
    imageView.setImageDrawable(result.image.asDrawable())
}
```

### 非单例模式

非单例模式下需要自行创建 Sketch 实例并通过其 enqueue() 或 execute() 方法执行请求：

```kotlin
val sketch = Sketch.Builder(context).build()

// 将 ImageRequest 放入任务队列在后台线程上异步执行
val request1 = ImageRequest(imageView, "https://www.example.com/image.jpg")
val disposable: Disposable = sketch.enqueue(request1)

// 将 ImageRequest 放入任务队列在后台线程上异步执行并在当前协程中等待返回结果
val request2 = ImageRequest(context, "https://www.example.com/image.jpg")
coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = sketch.execute(request2)
    imageView.setImageDrawable(result.image.asDrawable())
}
```

## 获取结果

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