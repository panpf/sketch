# 提高长图在网格列表中的清晰度

翻译：[English](long_image_grid_thumbnails.md)

长图在网格列表中会因为缩略图尺寸过小而导致显示效果很模糊。

例如组件大为 400x400，图片宽高为 30000x960 时 Sketch 自动计算出 [Resize] 的 size 为
400x400， [Precision] 默认为 LESS_PIXELS。 最终计算得出的 inSampleSize 为 16，缩略图尺寸为
1875x60，这张缩略图是极其模糊，无法辨别任何内容的

这时可以使用 [LongImagePrecisionDecider] 根据图片宽高和 ImageRequest.size 动态计算 [Precision]
，判断是长图时使用 [Precision].SAME_ASPECT_RATIO 来提高缩略图的清晰度，否则依然使用 [Precision]
.LESS_PIXELS，这样既确保了长图有一个清晰的缩略图，又保证了非长图的快速加载

> [!TIP]
> 1. 长图规则默认实现为 [DefaultLongImageDecider]，你还可以在创建 [LongImagePrecisionDecider]
     时使用自定义的规则
> 2. SAME_ASPECT_RATIO 会仅读取原图中的一部分，因此可以得到一张较清晰的缩略图

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    precision(LongImagePrecisionDecider(Precision.SAME_ASPECT_RATIO))
    scale(LongImageScaleDecider(longImage = Scale.START_CROP, otherImage = Scale.CENTER_CROP))
}
```

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[Resize]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/Resize.kt

[Precision]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/Precision.kt

[LongImagePrecisionDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/PrecisionDecider.kt

[DefaultLongImageDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/LongImageDecider.kt