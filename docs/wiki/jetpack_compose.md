# Jetpack Compose

Translations: [简体中文](jetpack_compose_zh.md)

> [!IMPORTANT]
> Required import `sketch-compose` module

### AsyncImage

AsyncImage is a composable function that performs image requests asynchronously and renders the
results. It has the same arguments as the standard Image composable function, plus it supports
setting placeholder, error and onLoading, onSuccess, onError callbacks

```kotlin
AsyncImage(
    uri = "https://example.com/image.jpg",
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

SubcomposeAsyncImage is a variant of AsyncImage that uses subcomposition to provide a slot for the
state of AsyncImagePainter API instead of using Painters

Below is an example:

```kotlin
SubcomposeAsyncImage(
    uri = "https://example.com/image.jpg",
    loading = {
        CircularProgressIndicator()
    },
    contentDescription = stringResource(R.string.description)
)
```

Additionally, you can implement more complex logic using SubcomposeAsyncImageContent using its
content parameter and rendering the current state:

```kotlin
SubcomposeAsyncImage(
    uri = "https://example.com/image.jpg",
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

Subcompose do not perform as well as regular compositions, so this composition may not be suitable
for parts of the UI where performance is critical (such as lists).

> If you use DisplayRequest.Builder.resizeSize to set a custom size for a DisplayRequest (e.g.
> resizeSize(100, 100)), SubcomposeAsyncImage will not use subcomposition because it does not need
> to
> resolve composable constraints.

### AsyncImagePainter

AsyncImage and SubcomposeAsyncImage use AsyncImagePainter to load images. If you need Painter and
can't use AsyncImage, you can use rememberAsyncImagePainter() to load the image:

```kotlin
val painter = rememberAsyncImagePainter(uri = "https://example.com/image.jpg")

// config params
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

rememberAsyncImagePainter is a lower-level API and may not function as expected in all situations.
For more information, read the documentation for this method.

> If you set a custom ContentScale on the image that AsyncImagePainter is rendered on, you should
> also set it in rememberAsyncImagePainter. It is necessary to determine the correct size of the
> loaded image.

### AsyncImageState

AsyncImageState is the core dependency of AsyncImagePainter. AsyncImagePainter is only responsible
for reading from AsyncImageState
painter parameter and then draw it

AsyncImageState is responsible for loading images and converting the loading results into Painter.
It is also responsible for saving the status, progress, painter and
The state of the painter, you can also reload the image through its restart method

```kotlin
val state = rememberAsyncImageState()
AsyncImage(
    uri = "https://example.com/image.jpg",
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

// Reload image
state.restart()
```

### listener/ProgressListener/target

AsyncImage, AsyncImagePainter, and SubcomposeAsyncImage are not allowed to use the listener,
ProgressListener, and target properties of DisplayRequest. If they are not null, an exception will
be thrown.

The reason is that the attributes listener, ProgressListener, and target are usually directly new
when used. Now, when DisplayRequest is used as a parameter of AsyncImage and SubcomposeAsyncImage,
reorganization is triggered because its equals result is false.

Therefore you must pass AsyncImageState instead of listener, ProgressListener, target attributes

### Size

The image request requires a size to determine the dimensions of the output image. By default,
AsyncImage resolves the requested size when determining dimensions, whereas AsyncImagePainter alone
resolves the requested size when the first frame will be drawn. It's solved this way to maximize
performance.

You can further improve performance by proactively setting resizeSize to avoid image requests
waiting to determine component size, as follows:

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

You can enable the built-in crossfade transition using DisplayRequest.Builder.crossfade:

```kotlin
AsyncImage(
    rqeuest = DisplayRequest(LocalContext.current, "https://example.com/image.jpg") {
        crossfade(true)
    },
    contentDescription = null
)
```

Custom transitions do not work with AsyncImage, SubcomposeAsyncImage or rememberAsyncImagePainter()
because they require a View Quote.

Due to special internal support, only CrossfadeTransition works

That is, you can create custom transitions in Compose by observing the state of AsyncImagePainter:

```kotlin
val painter = rememberAsyncImagePainter("https://example.com/image.jpg")

val state = painter.state
if (state is AsyncImagePainter.State.Success && state.result.dataFrom != DataFrom.MEMORY_CACHE) {
    // Perform transition animation
}

Image(
    painter = painter,
    contentDescription = stringResource(R.string.description)
)
```