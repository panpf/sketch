# ImageOptions

Translations: [简体中文](image_options_zh.md)

[ImageOptions] is used to define image request configurations in batches and supports all
image-related attributes of [ImageRequest].

[ImageOptions] can currently be used in three places:

* View
    * [SketchImageView].displayImageOptions
    * [SketchZoomImageView].displayImageOptions
* [ImageRequest].Builder.merge()/default()
* [Sketch].globalImageOptions

Ultimately when building [ImageRequest] it will end up as [ImageRequest].Builder >
View > [ImageRequest] Sequential build of .Builder.defaultOptions > [Sketch].globalImageOptions

### Example

Global：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            globalImageOptions(ImageOptions {
                placeholer(R.drawable.placeholder)
                error(R.drawable.error)
                // more ...
            })
        }.build()
    }
}
```

View：

```kotlin
sketchImageView.displayImageOptions = ImageOptions {
    placeholer(R.drawable.placeholder)
    // more ...
}

// Update based on existing ImageOptions
sketchImageView.updateDisplayImageOptions {
    error(R.drawable.error)
}
```

ImageRequest：

```kotlin
DisplayRequest(context, "http://sample.com/sample.jpeg") {
    merge(ImageOptions {
        // ...
    })
    default(ImageOptions {
        // ...
    })
}
```

[Sketch]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/Sketch.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageOptions.kt

[SketchImageView]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[SketchZoomImageView]: ../../sketch-zoom/src/main/kotlin/com/github/panpf/sketch/zoom/SketchZoomImageView.kt