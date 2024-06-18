# SVG

Translations: [简体中文](svg_zh.md)

> [!IMPORTANT]
> Required import `sketch-svg` module

Sketch supports decoding SVG static images, powered by [SvgDecoder]

### Registered

Register [SvgDecoder], as follows:

```kotlin
// Register for all ImageRequests when customizing Sketch
Sketch.Builder(context).apply {
    components {
        addDecoder(SvgDecoder.Factory())
    }
}.build()

// Register for a single ImageRequest when loading an image
ImageRequest(context, "https://www.example.com/image.svg") {
    components {
        addDecoder(SvgDecoder.Factory())
    }
}
```

### Configure

[ImageRequest] and [ImageOptions] support some svg-related configurations, as follows:

```kotlin
ImageRequest(context, "https://www.example.com/image.svg") {
    svgBackgroundColor(Color.WHITE)
    svgCss("...")    // Only Android
}
```

[SvgDecoder]: ../../sketch-svg/src/commonMain/kotlin/com/github/panpf/sketch/decode/SvgDecoder.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt