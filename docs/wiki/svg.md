# SVG

Translations: [简体中文](svg_zh.md)

Sketch supports decoding SVG static images, powered by [SvgDecoder]

## Install dependencies

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (Not included 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-svg:${LAST_VERSION}")
```

> [!IMPORTANT]
> `sketch-svg` The module supports automatic registration of components. For details
> on component registration, please see the
> documentation: [《Register component》](register_component.md)

### Configure

[ImageRequest] and [ImageOptions] support some svg-related configurations, as follows:

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