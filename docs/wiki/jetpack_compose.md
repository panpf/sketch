# Jetpack Compose

Translations: [简体中文](jetpack_compose_zh.md)

`需要导入 sketch-compose 模块`

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

// config params
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

// config params
val painter = rememberAsyncImagePainter(rqeuest = DisplayRequest(LocalContext.current, "https://example.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
    transformations(BlurTransformation())
    crossfade(true)
    // There is a lot more...
})
```

rememberAsyncImagePainter 是一个较低级别的 API，可能无法在所有情况下都按预期运行。有关更多信息，请阅读该方法的文档。

> 如果在呈现 AsyncImagePainter 的图像上设置自定义 ContentScale，则还应该在 rememberAsyncImagePainter
> 中设置它。有必要确定加载图像的正确尺寸。

### Observing AsyncImagePainter.state

图像请求需要一个大小来确定输出图像的尺寸。默认情况下，AsyncImage 和 AsyncImagePainter
在合成发生后，在绘制第一帧之前解析请求的大小。它以这种方式解决以最大限度地提高性能。

这意味着 AsyncImagePainter.state 将为第一个合成加载 - 即使图像存在于内存缓存中并且它将在第一帧中绘制。

如果你需要 AsyncImagePainter.state 在第一次合成期间保持最新，请使用 SubcomposeAsyncImage 或使用
DisplayRequest.Builder.resizeSize 为图像请求设置自定义大小。例如，在此示例中，AsyncImagePainter.state
在第一次合成期间将始终是最新的：

```kotlin
val painter = rememberAsyncImagePainter(rqeuest = DisplayRequest(LocalContext.current, "https://example.com/image.jpg") {
    resizeSize(100, 100)
})

if (painter.state is AsyncImagePainter.State.Success) {
// 如果图像在内存缓存中，这将在第一次合成期间执行。
}

Image(
    painter = painter,
    contentDescription = stringResource(R.string.description)
)
```

### Transitions

你可以使用 DisplayRequest.Builder.crossfade 启用内置的交叉淡入淡出过渡：

```kotlin
AsyncImage(
    imageUri = "https://example.com/image.jpg",
    contentDescription = null
) {
    crossfade(true)
}
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