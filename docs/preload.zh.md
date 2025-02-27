# 预加载

翻译：[English](preload.md)

有时为了提高加载速度不让用户看到图片加载的过程，就需要提前将图片加载到内存里。

我们只需要不设置 [Target]，然后确保 size、precision、scale 参数和使用时一样即可，如下：

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
> 在构建 [ImageRequest] 时需要你主动设置和使用时一致的 size、precision、scale，因为在使用时如果没有主动设置
> size、precision、scale 就会从 [Target] 上获取，这样可能会造成预加载时和使用时的 size、precision、scale
> 不一致导致无法命中缓存

[ImageRequest]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Target]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/Target.kt