# SVG

翻译：[English](svg.md)

Sketch 支持解码 SVG 静态图片，由 [SvgDecoder] 提供支持

### 安装依赖

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-svg:${LAST_VERSION}")
```

> [!IMPORTANT]
> `sketch-svg`
> 模块支持自动注册组件，有关组件注册的详细内容请查看文档：[《注册组件》](register_component_zh.md)

### 配置

[ImageRequest] 和 [ImageOptions] 支持一些 svg 相关的配置，如下：

```kotlin
ImageRequest(context, "https://www.example.com/image.svg") {
    svgBackgroundColor(Color.WHITE)
    svgCss("...")    // Only Android 
}
```

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/

[SvgDecoder]: ../../sketch-svg/src/commonMain/kotlin/com/github/panpf/sketch/decode/SvgDecoder.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt