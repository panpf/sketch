# Compose

Translations: [简体中文](compose_zh.md)

## AsyncImage

[AsyncImage] is a composable function that asynchronously executes image requests and renders the
results. You can use it directly to load and load images, as follows:

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
    rqeuest = ComposableImageRequest(imageUri) {
        placeholder(Res.drawable.placeholder)
        error(Res.drawable.error)
        crossfade()
        // There is a lot more...
    },
    contentDescription = "photo"
)
```

> [!TIP]
> `placeholder(Res.drawable.placeholder)` needs to import the `sketch-compose-resources` module

### SubcomposeAsyncImage

[SubcomposeAsyncImage] is a variant of [AsyncImage], which allows you to draw content completely
independently, as follows:

```kotlin
SubcomposeAsyncImage(
    uri = "https://example.com/image.jpg",
    loading = {
        Text("Loading")
    },
    contentDescription = "photo"
)

SubcomposeAsyncImage(
    uri = "https://example.com/image.jpg",
    contentDescription = "photo",
    content = {
        when (state.painterState) {
            is PainterState.Loading -> {
                Text("Loading")
            }
            is PainterState.Error -> {
                Text("Error")
            }
            else -> {
                Image(
                    painter = painter,
                    contentDescription = "photo"
                )
            }
        }
    }
)
```

> [!TIP]
> [SubcomposeAsyncImage] may not perform as well as [AsyncImage], so this combination may not be
> suitable for performance-critical parts of the list

### AsyncImagePainter

If you must use the Image component, you can also use [AsyncImagePainter] directly
to load images, as follows:

```kotlin
Image(
    painter = rememberAsyncImagePainter(uri = "https://example.com/image.jpg"),
    contentDescription = "photo"
)

// config params
Image(
    painter = rememberAsyncImagePainter(
        rqeuest = ComposableImageRequest("https://example.com/image.jpg") {
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
> 1. `Image + AsyncImagePainter` will be slightly slower than [AsyncImage], this is due to [Sketch]
     Depends on the exact size of the component before loading the image, [AsyncImage]
     The size of the component can be obtained during the layout stage,
     while `Image + AsyncImagePainter` cannot obtain the component size until the drawing stage.
> 2. If the contentScale is modified on the Image, the contentScale of rememberAsyncImagePainter
     must also be modified simultaneously.
> 3. `placeholder(Res.drawable.placeholder)` needs to import the `sketch-compose-resources` module

### AsyncImageState

[AsyncImageState] is the core of [AsyncImage] and [AsyncImagePainter]. [AsyncImageState] is
responsible for execution [ImageRequest] and management state, [AsyncImagePainter] is responsible
for reading Painter from [AsyncImageState] and drawing, [AsyncImage] is responsible for layout

You can read the status, progress, and Painter of the request from [AsyncImageState], and you can
also reload the image through its restart() method, as follows:

```kotlin
val state = rememberAsyncImageState()
AsyncImage(
    uri = "https://example.com/image.jpg",
    contentDescription = "photo",
    state = state,
)

val result: ImageResult? = state.result
val loadState: LoadState? = state.loadState
val request: ImageRequest = loadState.request
when (loadState) {
    is Started -> {

    }
    is Success -> {
        val cacheKey: String = loadState.result.cacheKey
        val imageInfo: ImageInfo = loadState.result.imageInfo
        val dataFrom: DataFrom = loadState.result.dataFrom
        val transformeds: List<String>? = loadState.result.transformeds
        val extras: Map<String, String>? = loadState.result.extras
    }
    is Error -> {
        val throwable: Throwable = loadState.result.throwable
    }
    is Canceled -> {}
    else -> {
        // null
    }
}
val progress: Progress? = state.progress
val painterState: PainterState = state.painterState
when (painterState) {
    is Loading -> {}
    is Success -> {}
    is Error -> {}
    is Empty -> {}
}
val painter: Painter? = state.painter

// Reload
state.restart()
```

### listener/progressListener

When using [AsyncImage], [SubcomposeAsyncImage] and [AsyncImagePainter], you cannot
call [ImageRequest] listener(), progressListener() methods, which will cause the app to crash

The reason is that when using [Listener] and [ProgressListener], in most cases, they directly new a
new instance, which will cause The equals result of [ImageRequest] is false and triggers
reorganization, thus reducing performance

Therefore you must use the loadState and progress properties of [AsyncImageState] instead of
listener(), progressListener()

## Target

When using [AsyncImage], [SubcomposeAsyncImage] and [AsyncImagePainter], you cannot
call [ImageRequest] target() method, which will cause the app to crash because the Target must be
configured by [AsyncImageState]


[comment]: <> (classs)

[AsyncImage]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImage.kt

[AsyncImagePainter]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImagePainter.kt

[AsyncImageState]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImageState.common.kt

[SubcomposeAsyncImage]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/SubcomposeAsyncImage.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Listener]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Listener.kt

[ProgressListener]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ProgressListener.kt
