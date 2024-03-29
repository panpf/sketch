# Jetpack Compose

翻译：[English](jetpack_compose.md)

> [!IMPORTANT]
> 必须导入 `sketch-compose` 模块

### AsyncImage

AsyncImage 是一个异步执行图像请求并呈现结果的可组合函数。它与标准 Image 可组合函数拥有相同的参数，此外它还支持设置
placeholder, error 和 onLoading, onSuccess, onError 回调

```kotlin
AsyncImage(
    imageUri = "https://example.com/image.jpg",
    contentDescription = stringResource(R.string.description),
    contentScale = ContentScale.Crop,
    modifier = Modifier.clip(CircleShape)
)

// 配置参数
AsyncImage(
    rqeuest = DisplayRequest(LocalContext.current, "https://example.com/image.jpg") {
        placeholder(R.drawable.placeholder)
        error(R.drawable.error)
        transformations(BlurTransformation())
        crossfade(true)
        // There is a lot more...
    },
    contentDescription = stringResource(R.string.description),
    contentScale = ContentScale.Crop,
    modifier = Modifier.clip(CircleShape)
)
```

### SubcomposeAsyncImage

SubcomposeAsyncImage 是 AsyncImage 的变体，它使用 subcomposition 为 AsyncImagePainter 的状态提供插槽
API，而不是使用 Painters

下面是一个例子：

```kotlin
SubcomposeAsyncImage(
    imageUri = "https://example.com/image.jpg",
    loading = {
        CircularProgressIndicator()
    },
    contentDescription = stringResource(R.string.description)
)
```

此外，你可以使用其 content 参数和渲染当前状态的 SubcomposeAsyncImageContent 实现更复杂的逻辑：

```kotlin
SubcomposeAsyncImage(
    imageUri = "https://example.com/image.jpg",
    contentDescription = stringResource(R.string.description),
    content = {
        val state = painter.state
        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
            CircularProgressIndicator()
        } else {
            SubcomposeAsyncImageContent()
        }
    }
)
```

子组合的性能不如常规组合，因此这种组合可能不适合对高性能至关重要的 UI 部分（例如列表）。

> 如果你使用 DisplayRequest.Builder.resizeSize 为 DisplayRequest 设置自定义大小（例如 resizeSize(100,
> 100)），SubcomposeAsyncImage 将不会使用子组合，因为它不需要解析可组合的约束。

### AsyncImagePainter

AsyncImage 和 SubcomposeAsyncImage 使用 AsyncImagePainter 来加载图像。如果你需要 Painter 并且不能使用
AsyncImage，你可以使用 rememberAsyncImagePainter() 加载图像：

```kotlin
val painter = rememberAsyncImagePainter(imageUri = "https://example.com/image.jpg")

// 配置参数
val painter = rememberAsyncImagePainter(
    rqeuest = DisplayRequest(LocalContext.current, "https://example.com/image.jpg") {
        placeholder(R.drawable.placeholder)
        error(R.drawable.error)
        transformations(BlurTransformation())
        crossfade(true)
        // There is a lot more...
    }
)
```

rememberAsyncImagePainter 是一个较低级别的 API，可能无法在所有情况下都按预期运行。有关更多信息，请阅读该方法的文档。

> 如果在呈现 AsyncImagePainter 的图像上设置自定义 ContentScale，则还应该在 rememberAsyncImagePainter
> 中设置它。有必要确定加载图像的正确尺寸。

### AsyncImageState

AsyncImageState 是 AsyncImagePainter 依赖的核心，AsyncImagePainter 只负责从 AsyncImageState 读取
painter 参数，然后绘制它

AsyncImageState 负责加载图像并将加载结果转换为 Painter，他还负责保存请求的状态、进度、painter 以及
painter 的状态，你还可以通过其 restart 方法重新加载图像

```kotlin
val state = rememberAsyncImageState()
AsyncImage(
    imageUri = "https://example.com/image.jpg",
    contentDescription = stringResource(R.string.description),
    contentScale = ContentScale.Crop,
    modifier = Modifier.clip(CircleShape),
    state = state,
)

val result: DisplayResult? = state.result
val loadState: LoadState? = state.loadState
when (loadState) {
    is Started -> {}
    is Success -> {}
    is Error -> {}
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

// 重新加载图像
state.restart()
```

### listener/ProgressListener/target

AsyncImage、AsyncImagePainter、SubcomposeAsyncImage 不允许使用 DisplayRequest 的
listener、ProgressListener、target 属性，检测到不为 null 就会抛异常

原因是 listener、ProgressListener、target 这几个属性通常在使用的时候时都是直接 new 一个，这会导致
DisplayRequest 会作为 AsyncImage 和 SubcomposeAsyncImage 的参数时会因为其 equals 结果是 false 而触发重组

因此你必须通过 AsyncImageState 来代替 listener、ProgressListener、target 属性

### Size

图像请求需要一个大小来确定输出图像的尺寸。默认情况下，AsyncImage 在确定尺寸时解析请求的大小，而单独使用
AsyncImagePainter
在将绘制第一帧时解析请求的大小。它以这种方式解决以最大限度地提高性能。

你可以主动设置 resizeSize 避免图像请求等待确定组件大小来进一步提高性能，如下：

```kotlin
val painter = rememberAsyncImagePainter(
    rqeuest = DisplayRequest(LocalContext.current, "https://example.com/image.jpg") {
        resizeSize(100, 100)
    }
)

Image(
    painter = painter,
    contentDescription = stringResource(R.string.description)
)
```

### Transitions

你可以使用 DisplayRequest.Builder.crossfade 启用内置的交叉淡入淡出过渡：

```kotlin
AsyncImage(
    rqeuest = DisplayRequest(LocalContext.current, "https://example.com/image.jpg") {
        crossfade(true)
    },
    contentDescription = null
)
```

自定义过渡不适用于 AsyncImage、SubcomposeAsyncImage 或 rememberAsyncImagePainter()，因为它们需要 View
引用。

由于特殊的内部支持，仅 CrossfadeTransition 有效

也就是说，可以通过观察 AsyncImagePainter 的状态在 Compose 中创建自定义过渡：

```kotlin
val painter = rememberAsyncImagePainter("https://example.com/image.jpg")

val state = painter.state
if (state is AsyncImagePainter.State.Success && state.result.dataFrom != DataFrom.MEMORY_CACHE) {
    // 执行过渡动画
}

Image(
    painter = painter,
    contentDescription = stringResource(R.string.description)
)
```