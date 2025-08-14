# BlurHash

Translations: [简体中文](blurhash.zh.md)

## Install component

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (Not included 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-blurhash:${LAST_VERSION}")
```

> [!IMPORTANT]
> `sketch-blurhash` The module supports automatic registration of components. For details
> on component registration, please see the
> documentation: [《Register component》](register_component.md)

### Use in placeholder

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

    // You can also pass the size through uri
    blurHashPlaceholder(
        blurHash = newBlurHashUri(
            blurHash = "d7D+0q5W00^h01~A~B0gInR%?G9vR%R+NH=_I;NG$$-o",
            size = Size(200, 300)
        )
    )

    // You can also limit the size by using the maxSide property, BlurHashStateImage will scale blur images in a ratio
    blurHashPlaceholder(
        blurHash = "d7D+0q5W00^h01~A~B0gInR%?G9vR%R+NH=_I;NG$$-o",
        size = Size(200, 300),
        maxSide = 100
    )

    // BlurHashStateImage will use memory cache to accelerate decoding. You can control the cachePolicy attribute. BlurHashStateImage to use memory cache.
    blurHashPlaceholder(
        blurHash = "d7D+0q5W00^h01~A~B0gInR%?G9vR%R+NH=_I;NG$$-o",
        size = Size(200, 300),
        maxSide = 100,
        cachePolicy = CachePolicy.DISABLED
    )

    // Fallback and error can also be used with BlurHashStateImage
}
```

> [!IMPORTANT]
> 1. You need to specify a size with the same aspect ratio as the original image for
     BlurHashStateImage, otherwise the default size (100x100) will be used, and inconsistent aspect
     ratios will cause blurred image deformation.
> 2. BlurHash decodes and generates Bitmap in UI threads, so use a smaller size as much as possible,
     otherwise it will cause lag.

### Used as a picture after decoding

```kotlin
val blurHashUri = newBlurHashUri("d7D+0q5W00^h01~A~B0gInR%?G9vR%R+NH=_I;NG$$-o", Size(200, 300))
ImageRequest(context, blurHashUri) {
    colorType("RGB_565")
    colorSpace("DISPLAY_P3")
}
```

> [!IMPORTANT]
> You need to specify a size with the same aspect ratio as the original image in the uri, otherwise
> the default size (100x100) will be used, and inconsistent aspect ratios will cause blurred image
> deformation.


[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/