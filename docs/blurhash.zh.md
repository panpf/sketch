# BlurHash

翻译：[English](blurhash.md)

BlurHash 是一种用于生成模糊图像占位符的算法。它可以将图像压缩为一个短字符串，表示图像的模糊版本。这个字符串可以在加载实际图像之前显示，以提高用户体验。

* Github：https://github.com/woltapp/blurhash

Sketch 集成了 BlurHash，让你可以方便的在占位图或其它需要模糊图像的地方使用。

### 安装组件

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-blurhash:${LAST_VERSION}")
```

> [!IMPORTANT]
> `sketch-blurhash`
> 模块支持自动注册组件，有关组件注册的详细内容请查看文档：[《注册组件》](register_component.zh.md)

### 在占位图中使用

```kotlin
ImageRequest(context, "https://www.example.com/image.svg") {
    placeholder(
        BlurHashStateImage(
            blurHash = "d7D+0q5W00^h01~A~B0gInR%?G9vR%R+NH=_I;NG$$-o",
            size = Size(200, 300)
        )
    )
    // or
    blurHashPlaceholder(
        blurHash = "d7D+0q5W00^h01~A~B0gInR%?G9vR%R+NH=_I;NG$$-o",
        size = Size(200, 300)
    )

    // 还可以通过 uri 的方式传递大小
    blurHashPlaceholder(
        blurHash = newBlurHashUri(
            blurHash = "d7D+0q5W00^h01~A~B0gInR%?G9vR%R+NH=_I;NG$$-o",
            size = Size(200, 300)
        )
    )

    // 还可以通过 maxSide 属性限制大小，BlurHashStateImage 会等比缩放模糊图像
    blurHashPlaceholder(
        blurHash = "d7D+0q5W00^h01~A~B0gInR%?G9vR%R+NH=_I;NG$$-o",
        size = Size(200, 300),
        maxSide = 100
    )

    // BlurHashStateImage 会使用内存缓存加速解码，你可以通过 cachePolicy 属性控制 BlurHashStateImage 使用内存缓存
    blurHashPlaceholder(
        blurHash = "d7D+0q5W00^h01~A~B0gInR%?G9vR%R+NH=_I;NG$$-o",
        size = Size(200, 300),
        maxSide = 100,
        cachePolicy = CachePolicy.DISABLED
    )

    // fallback 和 error 也可以使用 BlurHashStateImage
}
```

> [!IMPORTANT]
> 1. 你需要为 BlurHashStateImage 指定一个和原图一样高宽比的大小，否则将使用默认的大小（100x100），宽高比不一致会导致模糊图像变形。
> 2. BlurHash 在 UI 线程中解码生成 Bitmap，所以要尽可能的使用较小的大小，否则将会造成卡顿。

### 解码后作为图片使用

```kotlin
val blurHashUri = newBlurHashUri("d7D+0q5W00^h01~A~B0gInR%?G9vR%R+NH=_I;NG$$-o", Size(200, 300))
ImageRequest(context, blurHashUri) {
    colorType("RGB_565")
    colorSpace("DISPLAY_P3")
}
```

> [!IMPORTANT]
> 你需要在 uri 中指定一个和原图一样高宽比的大小，否则将使用默认的大小（100x100），宽高比不一致会导致模糊图像变形。


[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/

