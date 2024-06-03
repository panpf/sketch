# Exif

Translations: [简体中文](exif_orientation_zh)

Sketch supports restoring the orientation of images based on their Exif information. This feature is
enabled by default. You can use [ImageRequest] and [ImageOptions] to provide The
`ignoreExifOrientation` attribute disables this functionality, as follows:

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
    ignoreExifOrientation()
}
```

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.kt