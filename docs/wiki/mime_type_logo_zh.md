# 图片类型角标

翻译：[English](mime_type_logo.md)

Sketch 为 view 和 Compose 提供了显示图片类型角标的扩展功能，如下：

![sample_mime_type_logo.png](../res/sample_mime_type_logo.png)

### Compose

> [!IMPORTANT]
> 必须导入 `sketch-extensions-compose` 模块

```kotlin
val imageTypeIconMap = remember {
    mapOf(
        "image/gif" to painterResource(Res.drawable.image_type_gif),
        "image/png" to painterResource(Res.drawable.image_type_png),
        "image/jpeg" to painterResource(Res.drawable.image_type_jpeg),
        "image/webp" to painterResource(Res.drawable.image_type_webp),
        "image/bmp" to painterResource(Res.drawable.image_type_bmp),
        "image/svg+xml" to painterResource(Res.drawable.image_type_svg),
        "image/heif" to painterResource(Res.drawable.image_type_heif),
    )
}

val state = rememberAsyncImageState()
AsyncImage(
    uri = "https://example.com/image.jpg",
    modifier = Modifier
        .size(200.dp)
        .mimeTypeLogo(state, imageTypeIconMap, margin = 4.dp),
    state = state,
    contentDescription = "",
)
```

> Compose 版本功能由 [MimeTypeLogoModifier] 实现

### View

> [!IMPORTANT]
> * 必须导入 `sketch-extensions-view` 模块
> * 必须使用 [SketchImageView]

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

> View 版本功能由 [MimeTypeLogoAbility] 实现

[SketchImageView]: ../../sketch-extensions-view/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[MimeTypeLogoAbility]: ../../sketch-extensions-view/src/main/kotlin/com/github/panpf/sketch/ability/MimeTypeLogoAbility.kt

[MimeTypeLogoModifier]: ../../sketch-extensions-compose/src/commonMain/kotlin/com/github/panpf/sketch/ability/MimeTypeLogoModifier.kt