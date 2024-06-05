# ImageOptions

Translations: [简体中文](image_options_zh.md)

[ImageOptions] is used to define image request configurations in batches and supports all
image-related attributes of [ImageRequest].

[ImageOptions] can be used in the following locations:

* [Target].getImageOptions()
    * [SketchImageView].imageOptions
    * [AsyncImageState].options
* [ImageRequest].Builder.merge(ImageOptions)/default(ImageOptions)
* [Sketch].Builder.globalImageOptions(ImageOptions)

The final priority of the same properties when constructing the [ImageRequest] is:

1. [ImageRequest].Builder
2. [Target].getImageOptions()
3. [ImageRequest].Builder.default(ImageOptions)
4. [Sketch].globalImageOptions

### Example

Global：

```kotlin
Sketch.Builder(this).apply {
    globalImageOptions(ImageOptions {
        placeholer(Res.drawable.placeholder)
        error(Res.drawable.error)
        // more ...
    })
}.build()
```

View：

```kotlin
sketchImageView.imageOptions = ImageOptions {
    placeholer(R.drawable.placeholder)
    // more ...
}
```

ImageRequest：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    merge(ImageOptions {
        placeholer(Res.drawable.placeholder)
        error(Res.drawable.error)
        // more ...
    })
    default(ImageOptions {
        placeholer(Res.drawable.placeholder)
        error(Res.drawable.error)
        // more ...
    })
}
```

AsyncImageState：

```kotlin
val state = rememberAsyncImageState()
LaunchEffect(state) {
    state.options = ImageOptions {
        placeholer(Res.drawable.placeholder)
        error(Res.drawable.error)
        // more ...
    }
}
AsyncImage(
    uri = "https://example.com/image.jpg",
    contentDescription = "",
    state = state,
)
```

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.kt

[Target]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/Target.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.kt

[SketchImageView]: ../../sketch-extensions-view-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[AsyncImageState]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImageState.common.kt