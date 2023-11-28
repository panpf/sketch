# Jetpack Compose

Translations: [简体中文](jetpack_compose_zh.md)

`Need to import sketch-compose module`

### AsyncImage

AsyncImage is a composable function that performs image requests asynchronously and renders the
results. It has the same arguments as the standard Image composable function, plus it supports
setting placeholder, error and onLoading, onSuccess, onError callbacks

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

SubcomposeAsyncImage is a variant of AsyncImage that uses subcomposition to provide a slot for the
state of AsyncImagePainter API instead of using Painters

Below is an example:

```kotlin
SubcomposeAsyncImage(
    imageUri = "https://example.com/image.jpg",
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

Subcompose do not perform as well as regular compositions, so this composition may not be suitable
for parts of the UI where performance is critical (such as lists).

> If you use DisplayRequest.Builder.resizeSize to set a custom size for a DisplayRequest (e.g.
> resizeSize(100, 100)), SubcomposeAsyncImage will not use subcomposition because it does not need to
> resolve composable constraints.

### AsyncImagePainter

AsyncImage and SubcomposeAsyncImage use AsyncImagePainter to load images. If you need Painter and
can't use
AsyncImage, you can use rememberAsyncImagePainter() to load the image:

```kotlin
val painter = rememberAsyncImagePainter(imageUri = "https://example.com/image.jpg")

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

### Observing AsyncImagePainter.state

The image request requires a size to determine the dimensions of the output image. By default,
AsyncImage and AsyncImagePainter After compositing occurs, the requested size is parsed before the
first frame is drawn. It's solved this way to maximize performance.

This means that AsyncImagePainter.state will be loaded for the first composition - even though the
image exists in the memory cache and it will be drawn on the first frame.

If you need AsyncImagePainter.state to stay up to date during the first composition, use
SubcomposeAsyncImage or use
DisplayRequest.Builder.resizeSize sets a custom size for image requests. For example, in this
example, AsyncImagePainter.state Will always be up to date during the first composition:

```kotlin
val painter = rememberAsyncImagePainter(
    rqeuest = DisplayRequest(LocalContext.current, "https://example.com/image.jpg") {
        resizeSize(100, 100)
    }
)

if (painter.state is AsyncImagePainter.State.Success) {
    // If the image is in the memory cache, this will be performed during the first composition.
}

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