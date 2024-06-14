# Improve the clarity of long images in grid lists

Translations: [简体中文](long_image_grid_thumbnails_zh.md)

Long images in the grid list will be displayed very blurry because the thumbnail size is too small.

For example, if the component is 400x400 and the image width and height are 30000x960, Sketch
automatically calculates the [Resize] size to be 400x400, and the [Precision] default is
LESS_PIXELS. The final calculated inSampleSize is 16 and the thumbnail size is 1875x60. This
thumbnail is extremely blurry and cannot identify any content.

At this time, you can use [LongImageClipPrecisionDecider] to dynamically calculate [Precision] based
on the image width, height and ImageRequest.size. If it is judged to be a long image,
use [Precision].SAME_ASPECT_RATIO to improve the clarity of the thumbnail. Otherwise, still
use [Precision].LESS_PIXELS, so It not only ensures that long images have a clear thumbnail, but
also ensures fast loading of non-long images.

> [!TIP]
> 1. The default implementation of long image rules is [DefaultLongImageDecider], you can also use
     custom rules when creating [LongImageClipPrecisionDecider]
> 2. SAME_ASPECT_RATIO will only read part of the original image, so you can get a clearer
     thumbnail.

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    precision(LongImageClipPrecisionDecider(Precision.SAME_ASPECT_RATIO))
    scale(LongImageScaleDecider(longImage = Scale.START_CROP, otherImage = Scale.CENTER_CROP))
}
```

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[Resize]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/Resize.kt

[Precision]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/Precision.kt

[LongImageClipPrecisionDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/PrecisionDecider.kt

[DefaultLongImageDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/LongImageDecider.kt