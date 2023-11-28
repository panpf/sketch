# SVG

Translations: [简体中文](svg_zh.md)

Sketch supports decoding SVG static images, powered by [SvgBitmapDecoder]

### Registered

First import the `sketch-svg` module, and then register [SvgBitmapDecoder], as follows:

```kotlin
/* Register for all ImageRequests */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addBitmapDecoder(SvgBitmapDecoder.Factory())
            }
        }.build()
    }
}

/* Register for a single ImageRequest */
imageView.displayImage("https://www.example.com/image.svg") {
    components {
        addBitmapDecoder(SvgBitmapDecoder.Factory())
    }
}
```

### Configure

[DisplayRequest] and [LoadRequest] support some svg-related configurations, as follows:

```kotlin
imageView.displayImage("https://www.example.com/image.svg") {
    svgBackgroundColor(Color.WHITE)
}
```

[SvgBitmapDecoder]: ../../sketch-svg/src/main/kotlin/com/github/panpf/sketch/decode/SvgBitmapDecoder.kt

[DisplayRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DisplayRequest.kt

[LoadRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/LoadRequest.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt