# 提高长图在网格列表中的清晰度

翻译：[English](long_image_grid_thumbnails.md)

例如在 GirdLayoutManager 中 ImageView 宽高为 400x400，图片宽高为 30000x960，Sketch 自动计算出 [Resize]
为 400x400 且
[Precision] 默认为 LESS_PIXELS，这时根据 [Resize] 计算得出的 inSampleSize 为 16，而解码得到的缩略图尺寸为
1875x60，这张缩略图是极其模糊，无法辨别任何内容

针对这种情况可以用 [LongImageClipPrecisionDecider]
动态计算 [Precision]，[LongImageClipPrecisionDecider] 在遇到长图时会返回
SAME_ASPECT_RATIO 或 EXACTLY（创建时指定），否则返回 LESS_PIXELS，这样既确保了长图有一个清晰的缩略图，又保证了非长图的快速加载

> 注意：
> 1. 长图规则默认实现为 [DefaultLongImageDecider]，你还可以在创建 [LongImageClipPrecisionDecider]
     时使用自定义的规则
> 2. SAME_ASPECT_RATIO 和 EXACTLY 会使用 BitmapRegionDecoder 对原图进行裁剪，因此可以得到一张较清晰的缩略图

### 使用

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
    resizePrecision(LongImageClipPrecisionDecider(Precision.SAME_ASPECT_RATIO))
}
```

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.kt

[Resize]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/Resize.kt

[Precision]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/Precision.kt

[LongImageClipPrecisionDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/PrecisionDecider.kt

[DefaultLongImageDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/LongImageDecider.kt