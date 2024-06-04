# Improve the clarity of long images in grid lists

Translations: [简体中文](long_image_grid_thumbnails_zh.md)

For example, in GirdLayoutManager, the width and height of ImageView is 400x400, and the width and
height of the image are 30000x960. Sketch automatically calculates that [Resize] is 400x400 and
[Precision] defaults to LESS_PIXELS. In this case, the inSampleSize calculated according to [Resize]
is 16, and the decoded thumbnail size is 1875x60, this thumbnail is extremely blurry and no content
can be discerned

For this situation, you can use [LongImageClipPrecisionDecider] to dynamically
calculate [Precision]. [LongImageClipPrecisionDecider] will return when encountering a long image.
SAME_ASPECT_RATIO or EXACTLY (specified when creating), otherwise LESS_PIXELS is returned. This not
only ensures that long images have a clear thumbnail, but also ensures fast loading of non-long
images.

> [!TIP]
> 1. The default implementation of long image rules is [DefaultLongImageDecider], you can also
     create [LongImageClipPrecisionDecider] Use custom rules when
> 2. SAME_ASPECT_RATIO and EXACTLY will use BitmapRegionDecoder to crop the original image, so you
     can get a clearer thumbnail.

### Use

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