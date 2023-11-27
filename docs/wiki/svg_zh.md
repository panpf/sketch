# SVG

翻译：[English](svg.md)

Sketch 支持解码 SVG 静态图片，由 [SvgBitmapDecoder] 提供支持

### 注册

先导入 `sketch-svg` 模块，然后注册 [SvgBitmapDecoder]，如下：

```kotlin
/* 为所有 ImageRequest 注册 */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addBitmapDecoder(SvgBitmapDecoder.Factory())
            }
        }.build()
    }
}

/* 为单个 ImageRequest 注册 */
imageView.displayImage("https://www.example.com/image.svg") {
    components {
        addBitmapDecoder(SvgBitmapDecoder.Factory())
    }
}
```

### 配置

[DisplayRequest] 和 [LoadRequest] 支持一些 svg 相关的配置，如下：

```kotlin
imageView.displayImage("https://www.example.com/image.svg") {
    svgBackgroundColor(Color.WHITE)
}
```

[SvgBitmapDecoder]: ../../sketch-svg/src/main/kotlin/com/github/panpf/sketch/decode/SvgBitmapDecoder.kt

[DisplayRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DisplayRequest.kt

[LoadRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/LoadRequest.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt