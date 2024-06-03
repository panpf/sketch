# Getting Started

Translations: [简体中文](getting_started_zh.md)

## Display Image

Loading and displaying images with [Sketch] is very simple, as follows:

Compose Multiplatform：

```kotlin
// val imageUri = "/Users/my/Downloads/image.jpg"
// val imageUri = "compose.resource://files/sample.png"
val imageUri = "https://example.com/image.jpg"

AsyncImage(
    uri = imageUri,
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
    contentDescription = "photo"
)
```

> [!TIP]
> 1. On Compose Multiplatform you can use [AsyncImage] directly Components can also
     use `Image + AsyncImagePainter` to display the image.
> 2. But it is more recommended to use the [AsyncImage] component because [AsyncImage] is slightly
     faster.
> 3. This is because [Sketch] relies on the exact size of the component to start loading images,
     [AsyncImage] The size of the component can be obtained during the layout stage,
     while `Image + AsyncImagePainter` cannot obtain the component size until the drawing stage.

Android View：

```kotlin
// val imageUri = "/sdcard/download/image.jpg"
// val imageUri = "asset://image.jpg"
// val imageUri = "content://media/external/images/media/88484"
val imageUri = "https://example.com/image.jpg"

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

By default, [Sketch] will automatically adjust the size of the image according to the size of the
component to prevent the size of the image loaded into memory from exceeding the size of the
component itself and causing memory waste.

[Sketch] will also automatically cancel the request when the component is destroyed.

## Supported Image Formats

[Sketch] supports a variety of static and dynamic image types, as follows:

| Format        | Dependent Modules                           |
|:--------------|:--------------------------------------------|
| jpeg          | _                                           |
| png           | _                                           |
| bmp           | _                                           |
| webp          | _                                           |
| heif          | _                                           |
| svg           | sketch-svg                                  |
| gif           | sketch-animated<br>sketch-animated-koralgif |
| Animated webp | sketch-animated                             |
| Animated heif | sketch-animated                             |
| Video frames  | sketch-video<br>sketch-video-ffmpeg         |
| Apk icon      | sketch-extensions-core                      |

Each image type has a corresponding Decoder support for
it, [see more about Decoder and how to extend new image types][decoder]

## Supported URIs

[Sketch] supports loading images from different data sources such as the network, local machine, and
resources, as follows:

| URI                    | Describe                 | Create Function         | Dependent Modules      |
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

Each URI has its own Fetcher to support
it, [see more about Fetcher and how to extend new URIs][fetcher]

## Platform differences

Due to limitations of platform characteristics, the functions on different platforms are also
different, as follows:

| Feature                                                                                | Android       | iOS                     | Desktop                 | Web                     |
|:---------------------------------------------------------------------------------------|:--------------|:------------------------|:------------------------|:------------------------|
| jpeg<br/>png<br/>webp<br/>bmp                                                          | ✅             | ✅                       | ✅                       | ✅                       |
| heif                                                                                   | ✅ (API 28)    | ❌                       | ❌                       | ❌                       |
| gif                                                                                    | ✅             | ✅                       | ✅                       | ✅                       |
| Animated webp                                                                          | ✅ (API 28)    | ✅                       | ✅                       | ✅                       |
| Animated heif                                                                          | ✅ (API 30)    | ❌                       | ❌                       | ❌                       |
| svg                                                                                    | ✅             | ✅<br/>(Not Support CSS) | ✅<br/>(Not Support CSS) | ✅<br/>(Not Support CSS) |
| Video frames                                                                           | ✅             | ❌                       | ❌                       | ❌                       |
| http://<br/>https://<br/>/, file://<br/>compose.resource://<br/>data:image/jpeg;base64 | ✅             | ✅                       | ✅                       | ✅                       |
| asset://<br/>content://<br/>android.resource://                                        | ✅             | ❌                       | ❌                       | ❌                       |
| kotlin.resource://                                                                     | ❌             | ✅                       | ✅                       | ❌                       |
| Exif Orientation                                                                       | ✅             | ✅                       | ✅                       | ✅                       |
| Memory Cache                                                                           | ✅             | ✅                       | ✅                       | ✅                       |
| Result Cache                                                                           | ✅             | ✅                       | ✅                       | ❌                       |
| Download Cache                                                                         | ✅             | ✅                       | ✅                       | ❌                       |
| Default image decoder                                                                  | BitmapFactory | Skia Image              | Skia Image              | Skia Image              |
| Minimum API                                                                            | API 21        | -                       | JDK 1.8                 | -                       |

> The minimum API is '-' to synchronize with Compose Multiplatform

## Sketch

The [Sketch] class is the core of the entire framework. It is used to execute [ImageRequest] and
handle downloading, caching, decoding, conversion, and request management.

### Singleton Mode

The `sketch-compose` and `sketch-view` modules depend on the `sketch-singleton` module, so you can
use the
singleton mode by directly relying on them.

In singleton mode, you do not need to actively create a [Sketch] instance. You can directly obtain
the
shared [Sketch] instance, as follows:

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
        return Sketch.Builder(this).apply {
            logger(Logger(Logger.DEBUG))
            httpStack(OkHttpStack.Builder().build())
            // There is a lot more...
        }.build()
    }
}

// Non Android
SingletonSketch.setSafe {
    Sketch.Builder(PlatformContext.INSTANCE).apply {
        logger(Logger(Logger.DEBUG))
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
instance
you created when needed, as follows:

```kotlin
val sketch = Sketch.Builder(context).apply {
    logger(Logger(Logger.DEBUG))
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

To display the loading results directly on the component, you also need to configure [Target]

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
    painter = rememberAsyncImagePainter(
        request = request,
        contentScale = ContentScale.Crop
    ),
    contentDescription = "photo"
)
```

> [!CAUTION]
> You cannot call the target() function in AsyncImage and AsyncImagePainter, which will cause the
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

You can also use [ImageRequest][ImageRequest_ViewExtensions](ImageView, String) or
ImageView.[displayImage()][displayImage] extension functions, they will call target() for you, as
follows:

```kotlin
val request = ImageRequest(imageView, "https://www.example.com/image.jpg") {
    size(300, 300)
    // There is a lot more...
}
context.sketch.enqueue(request)

imageView.displayImage() {
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
     is AndroidBitmapImage -> {
          val bitmap: Bitmap = image.bitmap
     }
     is AndroidDrawableImage -> {
          val drawable: Drawable = image.drawable
     }
     is ComposeBitmapImage -> {
          val bitmap: ComposeBitmap = image.bitmap
     }
     is PainterImage -> {
          val painter: Painter = image.painter
     }
     is SkiaAnimatedImage -> {
          val codec: Codec = image.codec
     }
}
if (imageResult is ImageResult.Success) {
     val cacheKey: String = imageResult.cacheKey
     val imageInfo: ImageInfo = imageResult.imageInfo
     val dataFrom: DataFrom = imageResult.dataFrom
     val transformedList: List<String>? = imageResult.transformedList
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
// display
imageView.displayImage("https://www.example.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
    crossfade(true)
}

// cancel
imageView.disposeDisplay()

// result
val imageResult: ImageResult? = imageView.imageResult
```

> [displayImage()][displayImage] is only available in singleton mode

## Document

Basic functions:

* [Compose][compose]
* [AnimatedImage：GIF、WEBP、HEIF][animated_image]
* [Resize：Modify the image size][resize]
* [Transformation：Transformation image][transformation]
* [Transition：Display images in cool transitions][transition]
* [StateImage：Placeholder and error images][state_image]
* [Listener：Listen for request status and download progress][listener]
* [Cache：Learn about downloads, results, memory caching][cache]
* [Fetcher：Learn about Fetcher and extend new URI types][fetcher]
* [Decode：Understand the decoding process of Sketch][decode]
* [Target：Apply the load results to the target][target]
* [HttpStack：Learn about the HTTP section and using okhttp][http_stack]
* [SVG：Decode SVG still images][svg]
* [VideoFrames：Decode video frames][video_frame]
* [ExifOrientation：Correct the image orientation][exif_orientation]
* [ImageOptions：Manage image configurations in a unified manner][image_options]
* [RequestInterceptor：Intercept ImageRequest][request_interceptor]
* [Preload][preload]
* [Lifecycle][lifecycle]
* [Log][log]

Featured functions：

* [SketchImageView：Configure the request through XML attributes][sketch_image_view]
* [Improve the clarity of long images in grid lists][long_image_grid_thumbnails]
* [Displays the download progress][progress_indicator]
* [Displays the image type corner][mime_type_logo]
* [Pause image downloads on cellular data to save data][save_cellular_traffic]
* [The list slides to pause the loading of images][pause_load_when_scrolling]
* [Displays an icon for an apk file or installed app][apk_app_icon]

[comment]: <> (class)

[AsyncImage]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImage.kt

[AsyncImagePainter]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImagePainter.kt

[AsyncImageState]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImageState.common.kt

[DiskCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/DiskCache.kt

[displayImage]: ../../sketch-view/src/main/kotlin/com/github/panpf/sketch/SingletonImageViewExtensions.kt

[Disposable]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Disposable.kt

[Image]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Image.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageRequest_SingletonExtensions]: ../../sketch-singleton/src/commonMain/kotlin/com/github/panpf/sketch/request/SingletonRequestExtensions.kt

[ImageRequest_ViewExtensions]: ../../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequestViewExtensions.kt

[ImageResult]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageResult.kt

[SingletonSketch]: ../../sketch-singleton/src/commonMain/kotlin/com/github/panpf/sketch/SingletonSketch.kt

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.kt

[Target]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/Target.kt

[ViewTarget]: ../../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/target/ViewTarget.kt


[comment]: <> (wiki)

[animated_image]: animated_image.md

[apk_app_icon]: apk_app_icon.md

[cache]: cache.md

[compose]: compose.md

[decode]: decode.md

[exif_orientation]: exif_orientation.md

[fetcher]: fetcher.md

[getting_started]: getting_started.md

[http_stack]: http_stack.md

[image_options]: image_options.md

[lifecycle]: lifecycle.md

[listener]: listener.md

[log]: log.md

[long_image_grid_thumbnails]: long_image_grid_thumbnails.md

[mime_type_logo]: mime_type_logo.md

[pause_load_when_scrolling]: pause_load_when_scrolling.md

[preload]: preload.md

[progress_indicator]: progress_indicator.md

[request_interceptor]: request_interceptor.md

[resize]: resize.md

[save_cellular_traffic]: save_cellular_traffic.md

[sketch_image_view]: sketch_image_view.md

[state_image]: state_image.md

[svg]: svg.md

[target]: target.md

[transformation]: transformation.md

[transition]: transition.md

[video_frame]: video_frame.md