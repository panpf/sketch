# ImageOptions

翻译：[English](image_options.md)

[ImageOptions] 用来批量定义图片请求配置，支持 [ImageRequest] 全部图片相关属性

可以在以下位置使用 [ImageOptions]：

* [Target].getImageOptions()
    * [SketchImageView].imageOptions
    * [rememberAsyncImageState(ImageOptions)][AsyncImageState]
* [ImageRequest].Builder.merge(ImageOptions)/default(ImageOptions)
* [Sketch].Builder.globalImageOptions(ImageOptions)

最终在构建 [ImageRequest] 时相同属性的优先级为：

1. [ImageRequest].Builder
2. [Target].getImageOptions()
3. [ImageRequest].Builder.default(ImageOptions)
4. [Sketch].globalImageOptions

### 示例

Global：

```kotlin
Sketch.Builder(context).apply {
    globalImageOptions(ImageOptions {
        placeholer(R.drawable.placeholder)
        error(R.drawable.error)
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
        placeholer(R.drawable.placeholder)
        error(R.drawable.error)
        // more ...
    })
    default(ImageOptions {
        placeholer(R.drawable.placeholder)
        error(R.drawable.error)
        // more ...
    })
}
```

AsyncImageState：

```kotlin
val state = rememberAsyncImageState(ComposableImageOptions {
  placeholer(Res.drawable.placeholder)
  error(Res.drawable.error)
  // more ...
})
AsyncImage(
    uri = "https://example.com/image.jpg",
    contentDescription = "",
    state = state,
)
```

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[Target]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/Target.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt

[SketchImageView]: ../../sketch-extensions-view/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[AsyncImageState]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImageState.kt