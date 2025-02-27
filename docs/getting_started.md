# Getting Started

Translations: [简体中文](getting_started.zh.md)

## Load Image

Loading images with [Sketch] is very simple, as follows:

Compose Multiplatform:

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
> 1. On Compose Multiplatform you can use [AsyncImage] directly Components can also
     use `Image + AsyncImagePainter` to load the image.
> 2. But it is more recommended to use the [AsyncImage] component because [AsyncImage] is slightly
     faster.
> 3. This is because [Sketch] relies on the exact size of the component to start loading images,
     [AsyncImage] The size of the component can be obtained during the layout stage,
     while `Image + AsyncImagePainter` cannot obtain the component size until the drawing stage.
> 4. `placeholder(Res.drawable.placeholder)` needs to import the `sketch-compose-resources` module

Android View:

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

[Sketch] is smart. It will automatically adjust the size of the image according to the size of the
component to prevent the size of the image loaded into the memory from exceeding the size of the
component itself and cause memory waste. It will also automatically cancel the request when the
component is destroyed.

## Supported Image Formats

[Sketch] supports a variety of static and dynamic image types, as follows:

| Format        | Dependent Modules                                |
|:--------------|:-------------------------------------------------|
| jpeg          | _                                                |
| png           | _                                                |
| bmp           | _                                                |
| webp          | _                                                |
| heif          | _                                                |
| avif          | _                                                |
| svg           | sketch-svg                                       |
| gif           | sketch-animated-gif<br>sketch-animated-gif-koral |
| Animated webp | sketch-animated-webp                             |
| Animated heif | sketch-animated-heif                             |
| Video frames  | sketch-video<br>sketch-video-ffmpeg              |
| Apk icon      | sketch-extensions-apkicon                        |

Each image type has a corresponding Decoder support for it, [Learn more about Decoder][decoder]

## Supported URIs

[Sketch] supports loading images from different data sources such as the network, local machine, and
resources, as follows:

| URI                       | Describe                 | Create Function         | Dependent Modules                                                                |
|:--------------------------|:-------------------------|:------------------------|:---------------------------------------------------------------------------------|
| http://, https://         | File in network          | _                       | sketch-http-hurl<br>sketch-http-okhttp<br>sketch-http-ktor2<br>sketch-http-ktor3 |
| file://, /                | File in SDCard           | newFileUri()            | _                                                                                |
| content://                | Android Content Resolver | _                       | _                                                                                |
| file:///android_asset/    | Android Asset            | newAssetUri()           | _                                                                                |
| android.resource://       | Android Resource         | newResourceUri()        | _                                                                                |
| data:image/, data:img/    | Base64                   | newBase64Uri()          | _                                                                                |
| file:///compose_resource/ | Compose Resource         | newComposeResourceUri() | sketch-compose-resources                                                         |
| file:///kotlin_resource/  | Kotlin Resource          | newKotlinResourceUri()  | _                                                                                |
| app.icon://               | Android App Icon         | newAppIconUri()         | sketch-extensions-appicon                                                        |

Each URI has its own Fetcher to support it, [Learn more about Fetcher][fetcher]

## Platform differences

Due to limitations of platform characteristics, the functions on different platforms are also
different, as follows:

| Feature                                                                                      | Android       | iOS                     | Desktop                 | Web                     |
|:---------------------------------------------------------------------------------------------|:--------------|:------------------------|:------------------------|:------------------------|
| jpeg<br/>png<br/>webp<br/>bmp                                                                | ✅             | ✅                       | ✅                       | ✅                       |
| heif                                                                                         | ✅ (API 28)    | ❌                       | ❌                       | ❌                       |
| avif                                                                                         | ✅ (API 31)    | ❌                       | ❌                       | ❌                       |
| svg                                                                                          | ✅             | ✅<br/>(Not Support CSS) | ✅<br/>(Not Support CSS) | ✅<br/>(Not Support CSS) |
| gif                                                                                          | ✅             | ✅                       | ✅                       | ✅                       |
| Animated webp                                                                                | ✅ (API 28)    | ✅                       | ✅                       | ✅                       |
| Animated heif                                                                                | ✅ (API 30)    | ❌                       | ❌                       | ❌                       |
| Video frames                                                                                 | ✅             | ❌                       | ❌                       | ❌                       |
| http://<br/>https://<br/>file://, /<br/>file:///compose_resource/<br/>data:image/jpeg;base64 | ✅             | ✅                       | ✅                       | ✅                       |
| file:///android_asset/<br/>content://<br/>android.resource://                                | ✅             | ❌                       | ❌                       | ❌                       |
| file:///kotlin_resource/                                                                     | ❌             | ✅                       | ✅                       | ❌                       |
| Exif Orientation                                                                             | ✅             | ✅                       | ✅                       | ✅                       |
| Memory Cache                                                                                 | ✅             | ✅                       | ✅                       | ✅                       |
| Result Cache                                                                                 | ✅             | ✅                       | ✅                       | ❌                       |
| Download Cache                                                                               | ✅             | ✅                       | ✅                       | ❌                       |
| Default image decoder                                                                        | BitmapFactory | Skia Image              | Skia Image              | Skia Image              |
| Minimum API                                                                                  | API 21        | -                       | JDK 1.8                 | -                       |

> The minimum API is '-' to synchronize with Compose Multiplatform

## Sketch

The [Sketch] class is the core of the entire framework, which is used to execute and
manage [ImageRequest]

### Singleton Mode

The `sketch-compose` and `sketch-view` modules depend on the `sketch-singleton` module, so you can
use the singleton mode by directly relying on them.

In singleton mode, you do not need to actively create a [Sketch] instance. You can directly obtain
the shared [Sketch] instance, as follows:

```kotlin
// Android
val sketch = context.sketch
val sketch = SingletonSketch.get(context)

// Non Android
val sketch = SingletonSketch.get()
```

When you need to customize [Sketch], you can create [Sketch] and configure it in the following ways:

```kotlin
// Android
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(context).apply {
            logger(level = Logger.Level.Debug)
            httpStack(OkHttpStack.Builder().build())
            // There is a lot more...
        }.build()
    }
}

// Non Android
SingletonSketch.setSafe {
    Sketch.Builder(PlatformContext.INSTANCE).apply {
        logger(level = Logger.Level.Debug)
        httpStack(OkHttpStack.Builder().build())
        // There is a lot more...
    }.build()
}
```

> [!TIP]
> When using [SingletonSketch].setSafe() to customize [Sketch], you need to call it as early as
> possible, preferably in the entry function of the App

### Non-singleton mode

In non-singleton mode, you need to create [Sketch] yourself and remember it, and then use the
instance you created when needed, as follows:

```kotlin
val sketch = Sketch.Builder(context).apply {
    logger(level = Logger.Level.Debug)
    httpStack(OkHttpStack.Builder().build())
    // There is a lot more...
}.build()

val imageUri = "https://www.example.com/image.jpg"
val request = ImageRequest(context, imageUri)
GloablScope.launch {
    val imageResult: ImageResult = sketch.execute(request)
}
```

> [!TIP]
> For more custom configurations of [Sketch], please refer to the [Sketch].Builder class

## ImageRequest

[ImageRequest] is used to describe an image loading request, which includes the uri of the image and
placeholder image, transform, transition, new size, [Target], Listener and other configurations

### Create ImageRequest

Create a simple [ImageRequest] that limits the maximum number of pixels of the image to 300x300

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg") {
    size(300, 300)
    // There is a lot more...
}
```

> [!TIP]
> For more configuration of [ImageRequest], please refer to the [ImageRequest].Builder class

#### Configure Target

To load the results directly into the component, you also need to configure [Target]

On Compose [Target] is configured by [AsyncImage] and [AsyncImagePainter]'s
cornerstone [AsyncImageState], you just need to
Just pass [ImageRequest] to [AsyncImage] or [AsyncImagePainter], as follows:

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
> You cannot call the target() function in [AsyncImage] and [AsyncImagePainter], which will cause
> the
> app to crash

In the Android View system, you need to actively call the target() function and pass in the
ImageView, as follows:

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg") {
    size(300, 300)
    target(imageView)
    // There is a lot more...
}
context.sketch.enqueue(request)
```

You can also use [ImageRequest(ImageView, String)][ImageRequest_ViewExtensions] or
[ImageView.loadImage()][loadImage] extension functions, they will call target() for you, as
follows:

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

### Execute ImageRequest

After [ImageRequest] is created, it is handed over to [Sketch] for execution. [Sketch] supports
asynchronous and synchronous execution of [ImageRequest], as follows:

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg")

// Asynchronous execution of ImageRequest does not block the current thread or suspend the current coroutine.
val disposable: Disposable = sketch.enqueue(request)

// Synchronously execute ImageRequest and suspend the current coroutine until the result is returned.
coroutineScope.launch(Dispatchers.Main) {
    val imageResult: ImageResult = sketch.execute(request)
    val image: Image = imageResult.image
}
```

> [!NOTE]
> The singleton mode provides [ImageRequest][ImageRequest_SingletonExtensions].enqueue()
> and [ImageRequest][ImageRequest_SingletonExtensions].execute() extension functions
> for [ImageRequest] to facilitate sequential writing.

#### Get Result

When [Target] is configured, [Sketch] will hand over the results to [Target] for display, but
sometimes you need to do something with the results or when [Target] is not configured, you need to
actively obtain the results, as follows:

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg")

// When using the enqueue() method to asynchronously execute a request, you can obtain the result through the returned Disposable.job
val disposable = sketch.enqueue(request)
coroutineScope.launch(Dispatchers.Main) {
    val imageResult: ImageResult = disposable.job.await()
}

// You can directly obtain the results when executing a request synchronously using the execute() method.
coroutineScope.launch(Dispatchers.Main) {
    val imageResult: ImageResult = sketch.execute(request)
}
```

[ImageResult] contains a lot of useful information, as follows:

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

#### Cancel request

When [Target] is configured, [ImageRequest] will automatically cancel the request under the
following circumstances:

* [AsyncImage] or [AsyncImagePainter] component forgotten
* ImageView's onViewDetachedFromWindow() method is executed
* Lifecycle changes to DESTROYED state

When [Target] is not configured or when active cancellation is required, it can be canceled
through [Disposable] or Job, as follows:

```kotlin
// When using the enqueue() method to asynchronously execute a request, a Disposable will be returned, which can be used to cancel the request when needed.
val request = ImageRequest(context, "https://www.example.com/image.jpg")
val disposable = sketch.enqueue(request)
disposable.dispose()

// When using the execute() method to execute a request synchronously, you can cancel the request through its coroutine's Job when needed.
val job = coroutineScope.launch(Dispatchers.Main) {
    val request = ImageRequest(context, "https://www.example.com/image.jpg")
    val imageResult: ImageResult = sketch.execute(request)
}
job.cancel()
```

## ImageView extensions

[Sketch] provides a series of extensions for ImageView, as follows:

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

> [loadImage()][loadImage] is only available in singleton mode

## Document

Basic functions:

* [Register component][register_component]
* [Compose][compose]
* [Http: Load network images][http]
* [AnimatedImage: GIF、WEBP、HEIF][animated_image]
* [Resize: Modify the image size][resize]
* [Transformation: Transformation image][transformation]
* [Transition: Display images in cool transitions][transition]
* [StateImage: Placeholder and error images][state_image]
* [Listener: Listen for request status and download progress][listener]
* [DownloadCache: Understand download caching to avoid repeated downloads][download_cache]
* [ResultCache: Understand result caching to avoid duplicate conversions][result_cache]
* [MemoryCache: Understand memory caching to avoid repeated loading][memory_cache]
* [Fetcher: Learn about Fetcher and extend new URI types][fetcher]
* [Decoder: Understand the decoding process of Sketch][decoder]
* [Target: Apply the load results to the target][target]
* [SVG: Decode SVG still images][svg]
* [VideoFrames: Decode video frames][video_frame]
* [ExifOrientation: Correct the image orientation][exif_orientation]
* [ImageOptions: Manage image configurations in a unified manner][image_options]
* [RequestInterceptor: Intercept ImageRequest][request_interceptor]
* [DecodeInterceptor: Intercept the decoding process][decode_interceptor]
* [Preload images into memory][preload]
* [Download images][download]
* [Lifecycle][lifecycle]
* [Log][log]
* [Migrate][migrate]

Featured functions:

* [SketchImageView: Configure the request through XML attributes][sketch_image_view]
* [Improve the clarity of long images in grid lists][long_image_grid_thumbnails]
* [Displays the download progress][progress_indicator]
* [Displays the image type corner][mime_type_logo]
* [Pause image downloads on cellular data to save data][save_cellular_traffic]
* [The list slides to pause the loading of images][pause_load_when_scrolling]
* [Displays an icon for an apk file or installed app][apk_app_icon]

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

[animated_image]: animated_image.md

[apk_app_icon]: apk_app_icon.md

[register_component]: register_component.md

[compose]: compose.md

[decoder]: decoder.md

[download_cache]: download_cache.md

[exif_orientation]: exif_orientation.md

[fetcher]: fetcher.md

[getting_started]: getting_started.md

[http]: http.md

[image_options]: image_options.md

[lifecycle]: lifecycle.md

[listener]: listener.md

[log]: log.md

[long_image_grid_thumbnails]: long_image_grid_thumbnails.md

[memory_cache]: memory_cache.md

[mime_type_logo]: mime_type_logo.md

[pause_load_when_scrolling]: pause_load_when_scrolling.md

[preload]: preload.md

[download]: download_image.md

[progress_indicator]: progress_indicator.md

[request_interceptor]: request_interceptor.md

[decode_interceptor]: decode_interceptor.md

[resize]: resize.md

[result_cache]: result_cache.md

[save_cellular_traffic]: save_cellular_traffic.md

[sketch_image_view]: sketch_image_view.md

[state_image]: state_image.md

[svg]: svg.md

[target]: target.md

[transformation]: transformation.md

[transition]: transition.md

[video_frame]: video_frame.md

[migrate]: migrate.md