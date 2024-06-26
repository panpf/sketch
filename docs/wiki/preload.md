## Preload

Translations: [简体中文](preload_zh.md)

Sometimes in order to improve the loading speed and prevent users from seeing the image loading
process, it is necessary to load the image into memory in advance.

We just need not to set [Target], and then ensure that the size, precision, and scale parameters are
the same as when used, as follows:

```kotlin
val request = ImageRequest(context, "https://example.com/image.jpg") {
    size(200, 200)
    precision(Precision.LESS_PIXELS)
    scale(Scale.CENTER_CROP)
}

sketch.enqueue(request)
// or
scope.launch {
    sketch.execute(request)
}
```

> [!TIP]
> When building [ImageRequest], you need to actively set and use consistent size, precision, and
> scale, because if there is no active setting when using Size, precision and scale will be obtained
> from [Target], which may cause inconsistencies in size, precision, and scale between preloading
> and use, resulting in failure to hit the cache.

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Target]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/Target.kt