# Getting Started

Translations: [简体中文](getting_started_zh.md)

## Display Image

Loading and displaying images with Sketch is very simple, as follows:

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
> 1. On Compose Multiplatform you can use AsyncImage directly Components can also
     use `Image + AsyncImagePainter` to display the image.
> 2. But it is more recommended to use the AsyncImage component because AsyncImage is slightly
     faster.
> 3. This is because Sketch relies on the exact size of the component to start loading images,
     AsyncImage The size of the component can be obtained during the layout stage,
     while `Image + AsyncImagePainter` cannot obtain the component size until the drawing stage.

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

By default, Sketch will automatically adjust the size of the image according to the size of the
component to prevent the size of the image loaded into memory from exceeding the size of the
component itself and causing memory waste.

Sketch will also automatically cancel the request when the component is destroyed.

## Supported Image Formats

Sketch supports a variety of static and dynamic image types, as follows:

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

Sketch supports loading images from different data sources such as the network, local machine, and
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

The [Sketch] class is used to execute [ImageRequest] and handle image downloading, caching,
decoding, transformation, request management, memory management, and more.

### Singleton Mode

By default, it is recommended to rely on the 'sketch' module, which provides a singleton of Sketch
as well as some handy extension functions

In singleton mode, you can get Sketch through the Context's extension function, as follows:

```kotlin
val sketch = context.sketch
```

#### Customize Sketch

Method 1: Implement the [SketchFactory] interface on the Application class to create and configure
Sketch as follows:

```kotlin
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            logger(Logger(Logger.DEBUG))
            httpStack(OkHttpStack.Builder().build())
        }.build()
    }
}
```

Method 2: Create and configure Sketch and set it up as a singleton via the '
SingletonSketch.setSketch()' method, as follows:

```kotlin
val sketch = Sketch.Builder(context).apply {
    logger(Logger(Logger.DEBUG))
    httpStack(OkHttpStack.Builder().build())
}.build()

SingletonSketch.setSketch(sketch)

// or

SingletonSketch.setSketch(SketchFactory {
    Sketch.Builder(context).apply {
        logger(Logger(Logger.DEBUG))
        httpStack(OkHttpStack.Builder().build())
    }.build()
})
```

### Non Singleton Mode

If you don't want to use the singleton pattern, you can rely on the 'sketch-core' module and then
use the [Sketch]. Builder creates an instance of [Sketch] as follows:

```kotlin
val sketch = Sketch.Builder(context).apply {
    logger(Logger(Logger.DEBUG))
    httpStack(OkHttpStack.Builder().build())
}.build()
```

> For more configurable parameters, please refer to [Sketch].Builder class

## ImageRequest

The [ImageRequest] interface defines all the parameters required to display the image, such as uri,
target, conversion configuration, resizing, and so on.

### Build ImageRequest

Build with Builder:

```kotlin
val request = ImageRequest.Builder(context, "https://www.example.com/image.jpg")
    .placeholder(R.drawable.image)
    .transformations(CircleCropTransformation())
    .target(imageView)
    .build()
```

Build with a function of the same name:

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg") {
    placeholder(R.drawable.image)
    transformations(CircleCropTransformation())
    target(imageView)
}

// or

val request1 = ImageRequest(imageView, "https://www.example.com/image.jpg") {
    placeholder(R.drawable.image)
    transformations(CircleCropTransformation())
}
```

This can be done through the chained method provided by ImageRequest.Builder or the trailing
lambda provided by the function of the same name For configuration requests, please refer
to [ImageRequest] for more configuration parameters. Builder class

### Execute ImageRequest

#### Singleton Mode

In singleton mode, you can hand over [ImageRequest] to [Sketch] for execution via the provided
extension function enqueue() or execute():

```kotlin
/*
 * Put an ImageRequest into a task queue, execute asynchronously on a background thread, and return a Disposable
 */
val request1 = ImageRequest(imageView, "https://www.example.com/image.jpg")
request1.enqueue()

/*
 * Place an ImageRequest in a task queue, execute asynchronously on a background thread, 
 * and wait for the return result in the current coroutine
 */
val request2 = ImageRequest(context, "https://www.example.com/image.jpg")
coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = request2.execute()
    imageView.setImageDrawable(result.image.asDrawable())
}
```

#### Non Singleton Mode

In non-singleton mode, you need to create your own Sketch instance and execute the request through
its enqueue() or execute() method:

```kotlin
val sketch = Sketch.Builder(context).build()

/*
 * Put an ImageRequest into a task queue, execute asynchronously on a background thread, and return a Disposable
 */
val request1 = ImageRequest(imageView, "https://www.example.com/image.jpg")
sketch.enqueue(request1)

/*
 * Place an ImageRequest in a task queue, execute asynchronously on a background thread, 
 * and wait for the return result in the current coroutine
 */
val request2 = ImageRequest(context, "https://www.example.com/image.jpg")
coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = sketch.execute(request2)
    imageView.setImageDrawable(result.image.asDrawable())
}
```

### Get The Results

[Sketch] will hand the result to the [ImageRequest] target to display the [Image], and if the
target is not set, you will need to actively obtain the result to process it

When a request is executed using the enqueue() method, the result can be obtained by
returning [Disposable].job, as follows:

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg")
val disposable = request.enqueue()
coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = disposable.job.await()
    imageView.setImageDrawable(result.image.asDrawable())
}
```

When you use the execute() method to execute a request, you can get the result directly, as follows:

```kotlin
val request = ImageRequest(context, "https://www.example.com/image.jpg")
coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = request.execute()
    imageView.setImageDrawable(result.image.asDrawable())
}
```

### Cancel ImageRequest

#### Auto cancel

[ImageRequest] is automatically canceled in the following cases:

* request.lifecycle changes to DESTROYED state
* request.target is a [ViewTarget] and view's onViewDetachedFromWindow() method is executed

#### Proactive cancellation

Executing a request using the enqueue() method returns a [Disposable] that can be used to cancel the
request, as follows:

```kotlin
val disposable = ImageRequest(imageView, "https://www.example.com/image.jpg").enqueue()

// Cancel the request when you need to
disposable.dispose()
```

When a request is executed using the execute() method, it can be canceled by the job of its
coroutine, as follows:

```kotlin
val job = coroutineScope.launch(Dispatchers.Main) {
    val result: ImageResult = ImageRequest(context, "https://www.example.com/image.jpg")
        .execute()
    imageView.setImageDrawable(result.image.asDrawable())
}

// Cancel the request when you need to
job.cancel()
```

## ImageView Extensions

Sketch provides a series of extensions to ImageView, as follows:

### Display Image

> Available only in singleton mode

displayImage() extension function to display the image pointed to by the URI onto the ImageView

```kotlin
imageView.displayImage("https://www.example.com/image.jpg")
```

The above call is equivalent to:

```kotlin
ImageRequest(imageView, "https://www.example.com/image.jpg").enqueue()
```

You can also configure parameters via the lambda trailing with the displayImage function:

```kotlin
imageView.displayImage("https://www.example.com/image.jpg") {
    placeholder(R.drawable.image)
    transformations(CircleCropTransformation())
    crossfade(true)
}
```

### Cancel The Request

```kotlin
imageView.disposeDisplay()
```

### Get The Results

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
        val image: Image = imageResult.image
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

## Document

Basic functions:

* [Get Started][getting_started]
* [AnimatedImage: GIF、WEBP、HEIF][animated_image]
* [Resize: Modify the image size][resize]
* [Transformation: Transformation image][transformation]
* [Transition: Display images in cool transitions][transition]
* [StateImage: Placeholder and error images][state_image]
* [Listener: Listen for request status and download progress][listener]
* [Cache: Learn about downloads, results, memory caching][cache]
* [Fetcher: Learn about Fetcher and extend new URI types][fetcher]
* [Decoder: Learn about Decoder and expand into new image types][decoder]
* [Target: Apply the load results to the target][target]
* [HttpStack: Learn about the HTTP section and using okhttp][http_stack]
* [SVG: Decode SVG still images][svg]
* [VideoFrames: Decode video frames][video_frame]
* [Exif: Correct the image orientation][exif]
* [ImageOptions: Manage image configurations in a unified manner][image_options]
* [RequestInterceptor: Intercept ImageRequest][request_interceptor]
* [DecodeInterceptor: Intercept Bitmap or Drawable decoding][decode_interceptor]
* [DownloadRequest: Download the image to disk][download_request]
* [LoadRequest: Load the image to get the Bitmap][load_request]
* [Preload images into memory][preloading]
* [Lifecycle][lifecycle]
* [Jetpack Compose][jetpack_compose]
* [Log][log]

Featured functions:

* [SketchImageView: Configure the request through XML attributes][sketch_image_view]
* [Improve the clarity of long images in grid lists][long_image_grid_thumbnails]
* [Displays the download progress][show_download_progress]
* [Displays the image type corner][show_image_type]
* [Pause image downloads on cellular data to save data][save_cellular_traffic]
* [The list slides to pause the loading of images][pause_load_when_scrolling]
* [Displays an icon for an apk file or installed app][apk_app_icon]

[comment]: <> (class)

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.kt

[SketchFactory]: ../../sketch/src/main/kotlin/com/github/panpf/sketch/SketchFactory.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageResult]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageResult.kt

[Image]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Image.kt

[Disposable]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Disposable.kt

[ViewTarget]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/ViewTarget.kt

[DiskCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/DiskCache.kt


[comment]: <> (wiki)

[getting_started]: getting_started.md

[fetcher]: fetcher.md

[decoder]: decoder.md

[animated_image]: animated_image.md

[resize]: resize.md

[transformation]: transformation.md

[transition]: transition.md

[state_image]: state_image.md

[listener]: listener.md

[cache]: cache.md

[target]: target.md

[http_stack]: http_stack.md

[svg]: svg.md

[video_frame]: video_frame.md

[exif]: exif.md

[image_options]: image_options.md

[request_interceptor]: request_interceptor.md

[decode_interceptor]: decode_interceptor.md

[preloading]: preloading.md

[download_request]: download_request.md

[load_request]: load_request.md

[long_image_grid_thumbnails]: long_image_grid_thumbnails.md

[show_image_type]: mime_type_logo.md

[show_download_progress]: download_progress_indicator.md

[sketch_image_view]: sketch_image_view.md

[save_cellular_traffic]: save_cellular_traffic.md

[pause_load_when_scrolling]: pause_load_when_scrolling.md

[apk_app_icon]: apk_app_icon.md

[log]: log.md

[lifecycle]: lifecycle.md

[jetpack_compose]: jetpack_compose.md