# Show image type badge

Translations: [简体中文](show_image_type_zh.md)

`Need to import sketch-extensions module`

The [SketchImageView] provided by the sketch-extensions module supports displaying the image type
subscript in the lower right corner of the View, as follows:

```kotlin
sketchImageView.showMimeTypeLogoWithRes(
    mimeTypeIconMap = mapOf(
        "image/gif" to R.drawable.image_type_gif,
        "image/png" to R.drawable.image_type_png,
        "image/jpeg" to R.drawable.image_type_jpeg,
        "image/webp" to R.drawable.image_type_webp,
        "image/bmp" to R.drawable.image_type_bmp,
        "image/svg+xml" to R.drawable.image_type_svg,
        "image/heif" to R.drawable.image_type_heif,
    ),
    margin = 4.dp2px
)
```

> The picture type logo function is implemented by [MimeTypeLogoAbility]

[SketchImageView]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[MimeTypeLogoAbility]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/viewability/MimeTypeLogoAbility.kt