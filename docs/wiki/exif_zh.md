# Exif

翻译：[English](exif.md)

Sketch 支持根据图片的 Exif 信息恢复图片的方向，此功能默认开启，你可以通过 [ImageRequest]
和 [ImageOptions] 提供的
`ignoreExifOrientation` 属性禁用此功能，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    ignoreExifOrientation()
}
```

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageOptions.kt