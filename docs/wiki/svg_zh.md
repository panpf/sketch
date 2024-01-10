# SVG

翻译：[English](svg.md)

> [!IMPORTANT]
> 必须导入 `sketch-svg` 模块

Sketch 支持解码 SVG 静态图片，由 [SvgDecoder] 提供支持

### 注册

注册 [SvgDecoder]，如下：

```kotlin
/* 为所有 ImageRequest 注册 */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addDecoder(SvgDecoder.Factory())
            }
        }.build()
    }
}

/* 为单个 ImageRequest 注册 */
imageView.displayImage("https://www.example.com/image.svg") {
    components {
        addDecoder(SvgDecoder.Factory())
    }
}
```

### 配置

[ImageRequest] 和 [ImageOptions] 支持一些 svg 相关的配置，如下：

```kotlin
imageView.displayImage("https://www.example.com/image.svg") {
    svgBackgroundColor(Color.WHITE)
}
```

[SvgDecoder]: ../../sketch-svg/src/main/kotlin/com/github/panpf/sketch/decode/SvgDecoder.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageOptions.kt