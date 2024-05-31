# Getting Started

Translations: [简体中文](getting_started_zh.md)

## Supported URIs

| Scheme                 | Description      | Create Function  |
|:-----------------------|:-----------------|:-----------------|
| http://, https://      | File in network  | _                |
| /, file://             | File in SDCard   | newFileUri()     |
| content://             | Content Resolver | _                |
| asset://               | Asset Resource   | newAssetUri()    |
| android.resource://    | Android Resource | newResourceUri() |
| data:image/, data:img/ | Base64           | newBase64Uri()   |
| app.icon://            | App Icon         | newAppIconUri()  |

> The `Create Function` column in the table above shows the convenient creation function that Sketch
> provides for some URIs

Each URI has its own Fetcher to support
it, [see more about Fetcher and how to extend new URIs][fetcher]

## Supported Image Formats

| Format        | API Limit   | Dependency module                           |
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

Each image type has a corresponding Decoder support for
it, [see more about Decoder and how to extend new image types][decoder]

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