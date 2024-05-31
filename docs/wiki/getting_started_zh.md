# 开始使用

翻译：[English](image_request_zh.md)

## 快速上手

用 Sketch 加载并显示图片非常简单，如下：

Compose Multiplatform：

```kotlin
// val imageUri = "/Users/my/Downloads/image.jpg"
// val imageUri = "compose.resource://files/sample.png"
val imageUri = "https://www.sample.com/image.jpg"

AsyncImage(
    uri = imageUri,
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
    contentScale = ContentScale.Crop,
    contentDescription = "photo"
)
```

> [!TIP]
> 1. 在 Compose Multiplatform 上你既可以直接使用 AsyncImage 组件也可以使用 `Image + AsyncImagePainter`
     来显示图片。
> 2. 但更推荐直接使用 AsyncImage 组件，因为 AsyncImage 会略快一些。
> 3. 这是由于 Sketch 依赖组件的确切大小才会开始加载图片，AsyncImage
     在布局阶段就可以获取到组件的大小，而 `Image + AsyncImagePainter` 则是要等到绘制阶段才能获取到组件大小。

Android View：

```kotlin
// val imageUri = "/sdcard/download/image.jpg"
// val imageUri = "asset://image.jpg"
// val imageUri = "content://media/external/images/media/88484"
val imageUri = "https://www.sample.com/image.jpg"

imageView.displayImage(imageUri)

// config params
imageView.displayImage(imageUri) {
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
    crossfade()
    // There is a lot more...
}

val request = ImageRequest(context, imageUri) {
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
    crossfade()
    target(imageView)
    // There is a lot more...
}
context.sketch.enqueue(request)
```

Sketch 会自动根据组件的大小来调整图片的尺寸，防止加载到内存的图片的尺寸超出组件自身的大小造成内存浪费。Sketch还会在组件销毁时自动取消请求

## 支持的图片类型

| 类型           | 依赖模块                                        |
|:-------------|---------------------------------------------|
| jpeg         | _                                           |
| png          | _                                           |
| bmp          | _                                           |
| webp         | _                                           |
| heif         | _                                           |
| svg          | sketch-svg                                  |
| gif 动图       | sketch-animated<br>sketch-animated-koralgif |
| webp 动图      | sketch-animated                             |
| heif 动图      | sketch-animated                             |
| video frames | sketch-video<br>sketch-video-ffmpeg         |
| apk icon     | sketch-extensions-core                      |

每一种图片类型都有对应的 Decoder 对其提供支持，[查看更多 Decoder 介绍以及如何扩展新的图片类型](decoder_zh.md)

## 支持的 URI

| URI                    | 描述                       | 创建函数                    | 依赖模块                   |
|:-----------------------|:-------------------------|:------------------------|:-----------------------|
| http://, https://      | File in network          | _                       | _                      |
| /, file://             | File in SDCard           | newFileUri()            | _                      |
| content://             | Android Content Resolver | _                       | _                      |
| asset://               | Android Asset            | newAssetUri()           | _                      |
| android.resource://    | Android Resource         | newResourceUri()        | _                      |
| data:image/, data:img/ | Base64                   | newBase64Uri()          | _                      |
| compose.resource://    | Compose Resource         | newComposeResourceUri() | _                      |
| kotlin.resource://     | Kotlin Resource          | newKotlinResourceUri()  | _                      |
| app.icon://            | Android App Icon         | newAppIconUri()         | sketch-extensions-core |

每一种 URI 都有对应的 Fetcher 对其提供支持，[查看更多 Fetcher 介绍以及如何扩展新的 URI](fetcher_zh.md)

## 平台差异

| 功能/平台                                                                                  | Android       | iOS             | Desktop         | Web             |
|:---------------------------------------------------------------------------------------|---------------|:----------------|:----------------|:----------------|
| jpeg<br/>png<br/>webp<br/>bmp                                                          | ✅             | ✅               | ✅               | ✅               |
| heif                                                                                   | ✅ (API 28)    | ❌               | ❌               | ❌               |
| gif 动图                                                                                 | ✅             | ✅               | ✅               | ✅               |
| webp 动图                                                                                | ✅ (API 28)    | ✅               | ✅               | ✅               |
| heif 动图                                                                                | ✅ (API 30)    | ❌               | ❌               | ❌               |
| svg                                                                                    | ✅             | ✅<br/>(不支持 CSS) | ✅<br/>(不支持 CSS) | ✅<br/>(不支持 CSS) |
| 视频帧                                                                                    | ✅             | ❌               | ❌               | ❌               |
| http://<br/>https://<br/>/, file://<br/>compose.resource://<br/>data:image/jpeg;base64 | ✅             | ✅               | ✅               | ✅               |
| asset://<br/>content://<br/>android.resource://                                        | ✅             | ❌               | ❌               | ❌               |
| kotlin.resource://                                                                     | ❌             | ✅               | ✅               | ❌               |
| Exif Orientation                                                                       | ✅             | ✅               | ✅               | ✅               |
| 内存缓存                                                                                   | ✅             | ✅               | ✅               | ✅               |
| 结果缓存                                                                                   | ✅             | ✅               | ✅               | ❌               |
| 下载缓存                                                                                   | ✅             | ✅               | ✅               | ❌               |
| 默认图片解码器                                                                                | BitmapFactory | Skia Image      | Skia Image      | Skia Image      |
| 最低 API                                                                                 | API 21        | -               | JDK 1.8         | -               |

> 最低 API 是 '-' 表示和 Compose Multiplatform 同步

## Sketch

[Sketch] 类是整个框架的核心，它用来执行 [ImageRequest]，并处理下载、缓存、解码、转换以及请求管理等工作

### 单例模式

sketch-compose 和 sketch-view 模块依赖了 sketch-singleton 模块，因此直接依赖他们就可以使用单例模式

单例模式下不需要主动创建 Sketch 实例，可以通过以下方式获取共享的 Sketch 实例：

```kotlin
// Android
val sketch = context.sketch
val sketch = SingletonSketch.get(context)

// Non Android
val sketch = SingletonSketch.get()
```

需要自定义 Sketch 时可以通过以下方式创建 Sketch 并配置它：

```kotlin
// Android
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            logger(Logger(Logger.DEBUG))
            httpStack(OkHttpStack.Builder().build())
            // There is a lot more...
        }.build()
    }
}

// Non Android
SketchSingleton.setSafe {
    Sketch.Builder(PlatformContext.INSTANCE).apply {
        logger(Logger(Logger.DEBUG))
        httpStack(OkHttpStack.Builder().build())
        // There is a lot more...
    }.build()    
}
```

> [!TIP]
> 使用 SketchSingleton.setSafe() 方式自定义 Sketch 时需要尽可能早的调用它，最好是在 App 的入口函数中

### 非单例模式

非单例模式下需要你自己创建 Sketch 并记住它，然后在需要的时候使用你创建的实例，如下：

```kotlin
val sketch = Sketch.Builder(context).apply {
    logger(Logger(Logger.DEBUG))
    httpStack(OkHttpStack.Builder().build())
    // There is a lot more...
}.build()

GloablScope.launch {
    val imageUri = "https://www.example.com/image.jpg"
    val result: ImageResult = sketch.execute(ImageRequest(context, imageUri))
}
```

> [!TIP]
> 关于 Sketch 的更多自定义配置请参考 Sketch.Builder 类

## ImageRequest

[ImageRequest] 用来描述一次图片加载请求，它包含图片的 uri 以及占位图、转换、过渡、新的尺寸、Target、Listener 等配置

### 创建 ImageRequest

创建一个简单的 ImageRequest，它限制图片的最大像素数为 300x300

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg") {
     size(300, 300)
     // There is a lot more...
}
```

#### 配置 Target

要想将加载结果直接显示到组件上还需要配置 Target

在 Compose 上 Target 由 AsyncImage 或 AsyncImagePainter 背后的 AsyncImageState 来配置，你只需将 ImageRequest 交给
AsyncImage 或 AsyncImagePainter 即可，如下：

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg") {
     size(300, 300)
     // There is a lot more...
}

AsyncImage(
    request = request,
    contentScale = ContentScale.Crop,
    contentDescription = "photo"
)

Image(
    painter = rememberAsyncImagePainter(
        request = request,
        contentScale = ContentScale.Crop
    ),
    contentScale = ContentScale.Crop,
    contentDescription = "photo"
)
```

> [!CAUTION]
> 你不能调用 target() 函数，这会导致 App 崩溃

在 Android View 系统中则需要你主动调用 target() 函数传入 ImageView，你可以使用 ImageRequest(ImageView, String) 或
ImageView.displayImage() 扩展函数，它们会帮你调用 target()，如下：

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg") {
    size(300, 300)
    target(imageView)
    // There is a lot more...
}
context.sketch.enqueue(request)

val request = ImageRequest(imageView, "https://www.example.com/image.jpg") {
    size(300, 300)
    // There is a lot more...
}
context.sketch.enqueue(request)

imageView.displayImage(){
     size(300, 300)
    // There is a lot more...
}
```

> [!TIP]
> 关于 ImageRequest 的更多配置请参考 ImageRequest.Builder 类

### 执行 ImageRequest

ImageRequest 创建好后需要交由 Sketch 去执行，Sketch 支持异步和同步两种方式执行 ImageRequest，如下：

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg")

// 异步执行 ImageRequest 不阻塞当前线程或协程
val disposable: Disposable = sketch.enqueue(request)

// 同步执行 ImageRequest 阻塞当前协程直到返回结果
coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = sketch.execute(request)
    val image: Image = result.image
}
```

> [!NOTE]
> 单例模式下为 ImageRequest 提供了 ImageRequest.enqueue() 和 ImageRequest.execute() 扩展函数，方便顺序书写

#### 获取结果

配置了 Target 时 [Sketch] 会将结果交给 Target 去显示，但有时候需要通过结果做一些事情或者没有配置 Target 就需要主动获取结果了

使用 enqueue() 方法异步执行请求时可以通过返回的 [Disposable].job 获取结果，如下:

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg")
val disposable = sketch.enqueue(request)
coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = disposable.job.await()
    val image: Image = result.image
    // Your logic
}
```

使用 execute() 方法同步执行请求时可以直接获取结果，如下：

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg")
coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = sketch.execute(request)
    val image: Image = result.image
    // Your logic
}
```

#### 取消请求

配置了 Target 时 [ImageRequest] 会在下列情况下自动取消:

* AsyncImage 组件被忘记
* lifecycle 变为 DESTROYED 状态
* Target 是 [ViewTarget] 并且 View 的 onViewDetachedFromWindow() 方法被执行

未配置 Target 时或需要主动取消时可以通过 Disposable 或 Job 来取消，如下：

```kotlin
// 使用 enqueue() 方法异步执行请求时会返回一个 [Disposable], 可以用来它在需要的时候取消请求
val request = ImageRequest(context, "https://www.example.com/image.jpg")
val disposable = sketch.enqueue(request)
disposable.dispose()

// 使用 execute() 方法同步执行请求时可以通过其协程的 Job 在需要的时候取消请求
val job = coroutineScope.launch(Dispatchers.Main) {
     val request = ImageRequest(context, "https://www.example.com/image.jpg")
     val result: ImageResult = sketch.execute(request)
}
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