# Mime type logo

Translations: [简体中文](mime_type_logo_zh.md)

Sketch provides extended functions for displaying image type logo for view and Compose, as follows:

![sample_mime_type_logo.png](../res/sample_mime_type_logo.png)

### View

> [!IMPORTANT]
> * Required import `sketch-extensions-view` module
> * Required [SketchImageView]

```kotlin
val sketchImageView = SketchImageView(context)
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

> View version functionality is implemented by [MimeTypeLogoAbility]

### Compose

> [!IMPORTANT]
> Required import `sketch-extensions-compose` module

```kotlin
val imageTypeIconMap = remember {
    mapOf(
        "image/gif" to painterResource(R.drawable.image_type_gif),
        "image/png" to painterResource(R.drawable.image_type_png),
        "image/jpeg" to painterResource(R.drawable.image_type_jpeg),
        "image/webp" to painterResource(R.drawable.image_type_webp),
        "image/bmp" to painterResource(R.drawable.image_type_bmp),
        "image/svg+xml" to painterResource(R.drawable.image_type_svg),
        "image/heif" to painterResource(R.drawable.image_type_heif),
    )
}

val state = rememberAsyncImageState()
AsyncImage(
    imageUri = "https://www.sample.com/image.jpg",
    modifier = Modifier
        .size(200.dp)
        .mimeTypeLogo(state, imageTypeIconMap, margin = 4.dp),
    state = state,
    contentDescription = "",
)
```

> Compose version function is implemented by [MimeTypeLogoModifier]

[SketchImageView]: ../../sketch-extensions-view-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[MimeTypeLogoAbility]: ../../sketch-extensions-view-core/src/main/kotlin/com/github/panpf/sketch/ability/MimeTypeLogoAbility.kt

[MimeTypeLogoModifier]: ../../sketch-extensions-compose/src/main/kotlin/com/github/panpf/sketch/compose/ability/MimeTypeLogoModifier.kt