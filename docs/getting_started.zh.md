# 开始使用

翻译：[English](getting_started.md)

## 加载图片

用 [Sketch] 加载图片非常简单，如下：

Compose Multiplatform：

```kotlin
// val imageUri = "/Users/my/Downloads/image.jpg"
// val imageUri = file:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/sample.png
val imageUri = "https://example.com/image.jpg"

AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)

AsyncImage(
    uri = imageUri,
    state = rememberAsyncImageState(ComposableImageOptions {
        placeholder(Res.drawable.placeholder)
        error(Res.drawable.error)
        crossfade()
        // There is a lot more...
    }),
    contentDescription = "photo"
)

AsyncImage(
    rqeuest = ComposableImageRequest(imageUri) {
        placeholder(Res.drawable.placeholder)
        error(Res.drawable.error)
        crossfade()
        // There is a lot more...
    },
    contentDescription = "photo"
)

Image(
    painter = rememberAsyncImagePainter(
        request = ComposableImageRequest(imageUri) {
            placeholder(Res.drawable.placeholder)
            error(Res.drawable.error)
            crossfade()
            // There is a lot more...
        }
    ),
    contentDescription = "photo"
)
```

> [!TIP]
> 1. 在 Compose Multiplatform 上你既可以直接使用 [AsyncImage]
     组件也可以使用 `Image + AsyncImagePainter` 来加载图片。
> 2. 但更推荐使用 [AsyncImage] 组件，因为 [AsyncImage] 会略快一些。
> 3. 这是由于 [Sketch] 依赖组件的确切大小才会开始加载图片，[AsyncImage]
     在布局阶段就可以获取到组件的大小，而 `Image + AsyncImagePainter` 则是要等到绘制阶段才能获取到组件大小。
> 4. `placeholder(Res.drawable.placeholder)` 需要导入 `sketch-compose-resources` 模块

Android View：

```kotlin
// val imageUri = "/sdcard/download/image.jpg"
// val imageUri = "file:///android_asset/image.jpg"
// val imageUri = "content://media/external/images/media/88484"
val imageUri = "https://example.com/image.jpg"

imageView.loadImage(imageUri)

imageView.loadImage(imageUri) {
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

[Sketch] 会自动根据组件的大小来调整图片的尺寸，防止加载到内存的图片的尺寸超出组件自身的大小造成内存浪费，还会在组件销毁时自动取消请求

## 支持的图片类型

[Sketch] 支持多种类型的静态图片和动态图片，如下：

| 类型      | 依赖模块                                             |
|:--------|--------------------------------------------------|
| jpeg    | _                                                |
| png     | _                                                |
| bmp     | _                                                |
| webp    | _                                                |
| heif    | _                                                |
| avif    | _                                                |
| svg     | sketch-svg                                       |
| gif     | sketch-animated-gif<br>sketch-animated-gif-koral |
| webp 动图 | sketch-animated-webp                             |
| heif 动图 | sketch-animated-heif                             |
| 视频帧     | sketch-video<br>sketch-video-ffmpeg              |
| Apk 图标  | sketch-extensions-apkicon                        |

每一种图片类型都有对应的 Decoder 提供支持，[详细了解 Decoder][decoder]

## 支持的 URI

[Sketch] 支持从网络、本机、资源等不同的数据源加载图片，如下：

| URI                       | 描述                       | 创建函数                    | 依赖模块                                                                             |
|:--------------------------|:-------------------------|:------------------------|:---------------------------------------------------------------------------------|
| http://, https://         | File in network          | _                       | sketch-http-hurl<br>sketch-http-okhttp<br>sketch-http-ktor2<br>sketch-http-ktor3 |
| file://, /, D:/           | File in SDCard           | newFileUri()            | _                                                                                |
| content://                | Android Content Resolver | _                       | _                                                                                |
| file:///android_asset/    | Android Asset            | newAssetUri()           | _                                                                                |
| android.resource://       | Android Resource         | newResourceUri()        | _                                                                                |
| data:image/, data:img/    | Base64                   | newBase64Uri()          | _                                                                                |
| file:///compose_resource/ | Compose Resource         | newComposeResourceUri() | sketch-compose-resources                                                         |
| file:///kotlin_resource/  | Kotlin Resource          | newKotlinResourceUri()  | _                                                                                |
| app.icon://               | Android App Icon         | newAppIconUri()         | sketch-extensions-appicon                                                        |

每一种 URI 都有对应的 Fetcher 对其提供支持，[详细了解 Fetcher][fetcher]

## 平台差异

由于受平台特性所限，在不同平台上的功能也有所不同，如下：

| 功能                                                                                        | Android       | iOS             | Desktop         | Web             |
|:------------------------------------------------------------------------------------------|---------------|:----------------|:----------------|:----------------|
| jpeg<br/>png<br/>webp<br/>bmp                                                             | ✅             | ✅               | ✅               | ✅               |
| heif                                                                                      | ✅ (API 28)    | ❌               | ❌               | ❌               |
| avif                                                                                      | ✅ (API 31)    | ❌               | ❌               | ❌               |
| svg                                                                                       | ✅             | ✅<br/>(不支持 CSS) | ✅<br/>(不支持 CSS) | ✅<br/>(不支持 CSS) |
| gif                                                                                       | ✅             | ✅               | ✅               | ✅               |
| webp 动图                                                                                   | ✅ (API 28)    | ✅               | ✅               | ✅               |
| heif 动图                                                                                   | ✅ (API 30)    | ❌               | ❌               | ❌               |
| 视频帧                                                                                       | ✅             | ❌               | ❌               | ❌               |
| http://, https://<br/>file://, /<br/>file:///compose_resource/<br/>data:image/, data:img/ | ✅             | ✅               | ✅               | ✅               |
| file:///android_asset/<br/>content://<br/>android.resource://                             | ✅             | ❌               | ❌               | ❌               |
| file:///kotlin_resource/                                                                  | ❌             | ✅               | ✅               | ❌               |
| Exif Orientation                                                                          | ✅             | ✅               | ✅               | ✅               |
| 内存缓存                                                                                      | ✅             | ✅               | ✅               | ✅               |
| 结果缓存                                                                                      | ✅             | ✅               | ✅               | ❌               |
| 下载缓存                                                                                      | ✅             | ✅               | ✅               | ❌               |
| 默认图片解码器                                                                                   | BitmapFactory | Skia Image      | Skia Image      | Skia Image      |
| 最低 API                                                                                    | API 21        | -               | JDK 1.8         | -               |

> 最低 API 是 '-' 表示和 Compose Multiplatform 同步

## Sketch

[Sketch] 类是整个框架的核心，它用来执行并管理 [ImageRequest]

`sketch-compose-core` 和 `sketch-view-core` 模块提供了 ImageRequest, AsyncImage
等组件来加载图片，但他们还需要你创建 [Sketch] 实例，然后在加载图片的时候使用它，如下：

```kotlin
val sketch = Sketch.Builder(context).build()

// Compose
AsyncImage(
     uri = "https://www.example.com/image.jpg",
     sketch = sketch,
     moidifier = Modifier.fillMaxSize(),
     contentDescription = "photo",
)

// View
val request = ImageRequest(imageView, uri = "https://www.example.com/image.jpg")
sketch.enqueue(request)
```

为了更加便捷，Sketch 提供了单例模式和 Koin 模式，可以在加载图片的时候直接使用共享的 [Sketch] 实例

### 单例模式

你可以直接依赖 `sketch-compose` 或 `sketch-view` 模块使用单例模式，同时还提供了更加便捷的组件或加载函数，如下：

```kotlin
// Android
val sketch = context.sketch
val sketch = SingletonSketch.get(context)

// Non Android
val sketch = SingletonSketch.get()

// Compose
AsyncImage(
     uri = "https://www.example.com/image.jpg",
     moidifier = Modifier.fillMaxSize(),
     contentDescription = "photo",
)

// View
imageView.loadImage(uri = "https://www.example.com/image.jpg")
// or
ImageRequest(imageView, uri = "https://www.example.com/image.jpg").enqueue(request)
```

需要自定义 [Sketch] 时可以通过以下方式创建 [Sketch] 并配置它：

```kotlin
// Android
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(context).apply {
            logger(level = Logger.Level.Debug)
            // There is a lot more...
        }.build()
    }
}

// Non Android. 在 App 入口函数中调用
SingletonSketch.setSafe {
    Sketch.Builder(PlatformContext.INSTANCE).apply {
        logger(level = Logger.Level.Debug)
        // There is a lot more...
    }.build()
}
```

### Koin 模式

如果你使用 Koin 作为依赖注入框架，就可以依赖 `sketch-compose-koin` 或 `sketch-view-koin` 模块使用
Koin 模式，同样也提供了更加便捷的组件或加载函数，如下：

```kotlin
// 在 App 的入口函数或 Application 的 onCreate 中初始化 koin
startKoin {
    modules(
         module {
             single<Sketch> { Sketch.Builder(get()).apply{
                 logger(level = Logger.Level.Debug)
                 // There is a lot more...
             }.build() 
        }
    })
}

// 在任意位置获取实例
val sketch = KoinPlatform.getKoin().get<Sketch>()

// Compose
AsyncImage(
     uri = "https://www.example.com/image.jpg",
     moidifier = Modifier.fillMaxSize(),
     contentDescription = "photo",
)

// View
imageView.loadImage(uri = "https://www.example.com/image.jpg")
// or
ImageRequest(imageView, uri = "https://www.example.com/image.jpg").enqueue(request)
```

## ImageRequest

[ImageRequest] 用来描述一次图片加载请求，它包含图片的 uri 以及占位图、转换、过渡、新的尺寸、[Target]
、Listener 等配置

### 创建 ImageRequest

创建一个简单的 [ImageRequest]，它限制图片的最大像素数为 300x300

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg") {
    size(300, 300)
    // There is a lot more...
}
```

> [!TIP]
> 关于 [ImageRequest] 的更多配置请参考 [ImageRequest].Builder 类

#### 配置 Target

要想将结果直接加载到组件上还需要配置 [Target]

在 Compose 上 [Target] 由 [AsyncImage] 和 [AsyncImagePainter] 的基石 [AsyncImageState] 来配置，你只需将
[ImageRequest] 交给 [AsyncImage] 或 [AsyncImagePainter] 即可，如下：

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg") {
    size(300, 300)
    // There is a lot more...
}

AsyncImage(
    request = request,
    contentDescription = "photo"
)

Image(
    painter = rememberAsyncImagePainter(request),
    contentDescription = "photo"
)
```

> [!CAUTION]
> 在 [AsyncImage] 和 [AsyncImagePainter] 中你不能调用 target() 函数，这会导致 App 崩溃

在 Android View 系统中则需要你主动调用 target() 函数传入 ImageView，如下：

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg") {
    size(300, 300)
    target(imageView)
    // There is a lot more...
}
context.sketch.enqueue(request)
```

你还可以使用 [ImageRequest(ImageView, String)][ImageRequest_ViewExtensions] 或
[ImageView.loadImage()][loadImage] 扩展函数，它们会帮你调用 target()，如下：

```kotlin
val request = ImageRequest(imageView, "https://www.example.com/image.jpg") {
    size(300, 300)
    // There is a lot more...
}
context.sketch.enqueue(request)

imageView.loadImage() {
    size(300, 300)
    // There is a lot more...
}
```

### 执行 ImageRequest

[ImageRequest] 创建好后要交由 [Sketch] 去执行，[Sketch] 支持异步和同步两种方式执行 [ImageRequest]，如下：

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg")

// 异步执行 ImageRequest 不阻塞当前线程，也不会挂起当前协程
val disposable: Disposable = sketch.enqueue(request)

// 同步执行 ImageRequest 挂起当前协程直到返回结果
coroutineScope.launch(Dispatchers.Main) {
    val imageResult: ImageResult = sketch.execute(request)
    val image: Image = imageResult.image
}
```

> [!NOTE]
> 单例模式为 [ImageRequest] 提供了 [ImageRequest][ImageRequest_SingletonExtensions].enqueue()
> 和 [ImageRequest][ImageRequest_SingletonExtensions].execute() 扩展函数，方便顺序书写

#### 获取结果

配置了 [Target] 时 [Sketch] 会将结果交给 [Target] 去显示，但有时候需要通过结果做一些事情或者没有配置
[Target] 时就需要主动获取结果，如下：

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg")

// 使用 enqueue() 方法异步执行请求时可以通过返回的 Disposable.job 获取结果
val disposable = sketch.enqueue(request)
coroutineScope.launch(Dispatchers.Main) {
    val imageResult: ImageResult = disposable.job.await()
}

// 使用 execute() 方法同步执行请求时可以直接获取结果
coroutineScope.launch(Dispatchers.Main) {
    val imageResult: ImageResult = sketch.execute(request)
}
```

[ImageResult] 包含了很多有用的信息，如下：

```kotlin
val imageResult: ImageResult = ...
val request: ImageRequest = imageResult.request
val image: Image = imageResult.image
when (image) {
    is BitmapImage -> {
        val bitmap: Bitmap = image.bitmap
    }
    is DrawableImage -> {
        val drawable: Drawable = image.drawable
    }
    is PainterImage -> {
        val painter: Painter = image.painter
    }
    is AnimatedImage -> {
        val codec: Codec = image.codec
    }
}
if (imageResult is ImageResult.Success) {
    val cacheKey: String = imageResult.cacheKey
    val imageInfo: ImageInfo = imageResult.imageInfo
    val dataFrom: DataFrom = imageResult.dataFrom
    val resize: Resize = imageResult.resize
    val transformeds: List<String>? = imageResult.transformeds
    val extras: Map<String, String>? = imageResult.extras
} else if (imageResult is ImageResult.Error) {
    val throwable: Throwable = imageResult.throwable
}
```

#### 取消请求

配置了 [Target] 时 [ImageRequest] 会在下列情况下自动取消请求:

* [AsyncImage] 或 [AsyncImagePainter] 组件被忘记
* ImageView 的 onViewDetachedFromWindow() 方法被执行
* Lifecycle 变为 DESTROYED 状态

未配置 [Target] 或需要主动取消时可以通过 [Disposable] 或 Job 来取消，如下：

```kotlin
// 使用 enqueue() 方法异步执行请求时会返回一个 Disposable, 可以用来它在需要的时候取消请求
val request = ImageRequest(context, "https://www.example.com/image.jpg")
val disposable = sketch.enqueue(request)
disposable.dispose()

// 使用 execute() 方法同步执行请求时可以通过其协程的 Job 在需要的时候取消请求
val job = coroutineScope.launch(Dispatchers.Main) {
    val request = ImageRequest(context, "https://www.example.com/image.jpg")
    val imageResult: ImageResult = sketch.execute(request)
}
job.cancel()
```

## ImageView 扩展

[Sketch] 为 ImageView 提供了一系列的扩展，如下:

```kotlin
// load
imageView.loadImage("https://www.example.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
    crossfade(true)
}

// cancel
imageView.disposeLoad()

// result
val imageResult: ImageResult? = imageView.imageResult
```

> [loadImage()][loadImage] 仅单例模式下可用

## 文档

基础功能：

* [注册组件][register_component]
* [Compose][compose]
* [Http：加载网络图片][http]
* [AnimatedImage：GIF、WEBP、HEIF][animated_image]
* [Resize：修改图片尺寸][resize]
* [Transformation：转换图片][transformation]
* [Transition：用炫酷的过渡方式显示图片][transition]
* [StateImage：占位图和错误图][state_image]
* [Listener：监听请求状态和下载进度][listener]
* [DownloadCache：了解下载缓存，避免重复下载][download_cache]
* [ResultCache：了解结果缓存，避免重复转换][result_cache]
* [MemoryCache：了解内存缓存，避免重复加载][memory_cache]
* [Fetcher：了解 Fetcher 及扩展新的 URI 类型][fetcher]
* [Decoder：了解 Sketch 的解码过程][decoder]
* [Target：将加载结果应用到目标上][target]
* [SVG：解码 SVG 静态图片][svg]
* [VideoFrames：解码视频帧][video_frame]
* [ExifOrientation：纠正图片方向][exif_orientation]
* [ImageOptions：统一管理图片配置][image_options]
* [RequestInterceptor：拦截 ImageRequest][request_interceptor]
* [DecodeInterceptor：拦截解码过程][decode_interceptor]
* [预加载图片到内存中][preload]
* [下载图片][download]
* [Lifecycle][lifecycle]
* [日志][log]
* [迁移][migrate]

特色功能：

* [SketchImageView：通过 XML 属性配置请求][sketch_image_view]
* [提高长图在网格列表中的清晰度][long_image_grid_thumbnails]
* [显示下载进度][progress_indicator]
* [显示图片类型角标][mime_type_logo]
* [蜂窝数据网络下暂停下载图片节省流量][save_cellular_traffic]
* [列表滑动中暂停加载图片][pause_load_when_scrolling]
* [显示 APK 文件或已安装 APP 的图标][apk_app_icon]

[comment]: <> (classs)

[AsyncImage]: ../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImage.kt

[AsyncImagePainter]: ../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImagePainter.kt

[AsyncImageState]: ../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImageState.kt

[DiskCache]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/DiskCache.common.kt

[loadImage]: ../sketch-view/src/main/kotlin/com/github/panpf/sketch/image_view_singleton_extensions.kt

[Disposable]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Disposable.kt

[Image]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Image.kt

[ImageRequest]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageRequest_SingletonExtensions]: ../sketch-singleton/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.singleton.kt

[ImageRequest_ViewExtensions]: ../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.view.kt

[ImageResult]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageResult.kt

[SingletonSketch]: ../sketch-singleton/src/commonMain/kotlin/com/github/panpf/sketch/SingletonSketch.common.kt

[Sketch]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[Target]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/Target.kt

[ViewTarget]: ../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/target/ViewTarget.kt


[comment]: <> (wiki)

[animated_image]: animated_image.zh.md

[apk_app_icon]: apk_app_icon.zh.md

[register_component]: register_component.zh.md

[compose]: compose.zh.md

[decoder]: decoder.zh.md

[download_cache]: download_cache.zh.md

[exif_orientation]: exif_orientation.zh.md

[fetcher]: fetcher.zh.md

[getting_started]: getting_started.zh.md

[http]: http.zh.md

[image_options]: image_options.zh.md

[lifecycle]: lifecycle.zh.md

[listener]: listener.zh.md

[log]: log.zh.md

[long_image_grid_thumbnails]: long_image_grid_thumbnails.zh.md

[memory_cache]: memory_cache.zh.md

[mime_type_logo]: mime_type_logo.zh.md

[pause_load_when_scrolling]: pause_load_when_scrolling.zh.md

[preload]: preload.zh.md

[download]: download_image.zh.md

[progress_indicator]: progress_indicator.zh.md

[request_interceptor]: request_interceptor.zh.md

[decode_interceptor]: decode_interceptor.zh.md

[resize]: resize.zh.md

[result_cache]: result_cache.zh.md

[save_cellular_traffic]: save_cellular_traffic.zh.md

[sketch_image_view]: sketch_image_view.zh.md

[state_image]: state_image.zh.md

[svg]: svg.zh.md

[target]: target.zh.md

[transformation]: transformation.zh.md

[transition]: transition.zh.md

[video_frame]: video_frame.zh.md

[migrate]: migrate.zh.md