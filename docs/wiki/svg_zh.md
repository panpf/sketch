# SVG

翻译：[English](svg.md)

> [!IMPORTANT]
> 必须导入 `sketch-svg` 模块

Sketch 支持解码 SVG 静态图片，由 [SvgDecoder] 提供支持

### 注册

注册 [SvgDecoder]，如下：

```kotlin
// 在自定义 Sketch 时为所有 ImageRequest 注册
Sketch.Builder(context).apply {
    components {
        addDecoder(SvgDecoder.Factory())
    }
}.build()

// 加载图片时为单个 ImageRequest 注册
ImageRequest(context, "https://www.example.com/image.svg") {
    components {
        addDecoder(SvgDecoder.Factory())
    }
}
```

### 配置

[ImageRequest] 和 [ImageOptions] 支持一些 svg 相关的配置，如下：

```kotlin
ImageRequest(context, "https://www.example.com/image.svg") {
    svgBackgroundColor(Color.WHITE)
    svgCss("...")    // Only Android 
}
```

[SvgDecoder]: ../../sketch-svg/src/commonMain/kotlin/com/github/panpf/sketch/decode/SvgDecoder.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.kt