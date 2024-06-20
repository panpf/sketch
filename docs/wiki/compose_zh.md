# Compose

翻译：[English](compose.md)

## AsyncImage

[AsyncImage] 是一个异步执行图片请求并呈现结果的可组合函数，你可以直接使用它加载图片，如下：

```kotlin
// val imageUri = "/Users/my/Downloads/image.jpg"
// val imageUri = "compose.resource://files/sample.png"
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
```

> [!TIP]
> `placeholder(Res.drawable.placeholder)` 需要导入 `sketch-compose-resources` 模块

### SubcomposeAsyncImage

[SubcomposeAsyncImage] 是 [AsyncImage] 的变体，它允许你完全自主的绘制内容，如下：

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
> [SubcomposeAsyncImage] 的性能可能不如 [AsyncImage]，因此这种组合可能不适合对性能至关重要的列表部分

### AsyncImagePainter

如果你必须使用 Image 组件，那么你还可以直接使用 [AsyncImagePainter] 来加载图片，如下：

```kotlin
Image(
    painter = rememberAsyncImagePainter(uri = "https://example.com/image.jpg"),
    contentDescription = "photo"
)

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
> 1. `Image + AsyncImagePainter` 会比 [AsyncImage] 会略慢一些，这是由于 [Sketch]
     依赖组件的确切大小才会开始加载图片，[AsyncImage]
     在布局阶段就可以获取到组件的大小，而 `Image + AsyncImagePainter` 则是要等到绘制阶段才能获取到组件大小。
> 2. 如果在 Image 上修改了 contentScale，则也要同步修改 rememberAsyncImagePainter 的 contentScale
> 3. `placeholder(Res.drawable.placeholder)` 需要导入 `sketch-compose-resources` 模块

### AsyncImageState

[AsyncImageState] 是 [AsyncImage] 和 [AsyncImagePainter] 的核心。[AsyncImageState] 负责执行
[ImageRequest]
和管理状态，[AsyncImagePainter] 负责从 [AsyncImageState] 读取 Painter 并绘制，[AsyncImage] 负责布局

你可以从 [AsyncImageState] 读取请求的状态、进度、Painter，你还可以通过其 restart() 方法重新加载图片，如下：

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
        val resize: Resize = loadState.result.resize
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

// 重新加载
state.restart()
```

### listener/progressListener

在使用 [AsyncImage]、[SubcomposeAsyncImage] 以及 [AsyncImagePainter] 时你不能调用 [ImageRequest] 的
listener()、progressListener() 方法，这会导致 App 崩溃

原因是 [Listener]、[ProgressListener] 在使用的时候时都大部分情况下都是直接 new 一个新的实例，这会导致
[ImageRequest] 的 equals 结果是 false 而触发重组，从而降低性能

因此你必须用 [AsyncImageState] 的 loadState 和 progress 属性来代替 listener()、progressListener()

## Target

在使用 [AsyncImage]、[SubcomposeAsyncImage] 以及 [AsyncImagePainter] 时你不能调用 [ImageRequest] 的
target() 方法，这会导致 App 崩溃，因为 Target 必须由 [AsyncImageState] 配置


[comment]: <> (classs)

[AsyncImage]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImage.kt

[AsyncImagePainter]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImagePainter.kt

[AsyncImageState]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImageState.common.kt

[SubcomposeAsyncImage]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/SubcomposeAsyncImage.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Listener]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Listener.kt

[ProgressListener]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ProgressListener.kt