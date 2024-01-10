# SVG

Translations: [简体中文](svg_zh.md)

> [!IMPORTANT]
> Required import `sketch-svg` module

Sketch supports decoding SVG static images, powered by [SvgDecoder]

### Registered

Register [SvgDecoder], as follows:

```kotlin
/* Register for all ImageRequests */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addDecoder(SvgDecoder.Factory())
            }
        }.build()
    }
}

/* Register for a single ImageRequest */
imageView.displayImage("https://www.example.com/image.svg") {
    components {
        addDecoder(SvgDecoder.Factory())
    }
}
```

### Configure

[ImageRequest] and [ImageOptions] support some svg-related configurations, as follows:

```kotlin
imageView.displayImage("https://www.example.com/image.svg") {
    svgBackgroundColor(Color.WHITE)
}
```

[SvgDecoder]: ../../sketch-svg/src/main/kotlin/com/github/panpf/sketch/decode/SvgDecoder.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageOptions.kt