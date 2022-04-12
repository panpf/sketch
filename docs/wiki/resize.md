# Resize

[Resize] 用来在解码时以及解码后调整图片的尺寸，解码时参与计算 inSampleSize，解码后如果尺寸依然不符合 [Resize] 要求就会再次调整

[Resize] 由以下几部分构成：

* width、height：期望的宽和高
* [Precision]：精度。决定如何使用 width 和 height 去调整图片的尺寸
    * LESS_PIXELS（默认）：只要最终 Bitmap 的像素数（宽乘以高）约等于 [Resize] 的像素数即可，允许有 10% 的误差
    * KEEP_ASPECT_RATIO：在 LESS_PIXELS 的基础上要求 Bitmap 的宽高比和 [Resize] 的宽高比一致
    * EXACTLY：最终 Bitmap 的尺寸一定和 [Resize] 一样
* [PrecisionDecider]：精度决策器。针对具体的图片尺寸和 [Resize] 尺寸决定使用哪个 [Precision]
    * [FixedPrecisionDecider]：始终使用指定的 [Precision]
    * [LongImageClipPrecisionDecider]：如果是长图就使用指定的 [Precision]，否则始终使用 LESS_PIXELS
* [Scale]：缩放。需要对原图进行裁剪时决定如何裁剪原图
    * START_CROP：保留头部部分
    * CENTER_CROP：保留中间部分
    * END_CROP：保留尾部部分
    * FILL：全部保留，但会变形

> 1. 如何判定是长图？图片的宽高比和 resize 的宽高比相差超过 1 倍
> 2. 使用 [LongImageClipPrecisionDecider] 有助于提高长图在网格列表中的清晰度，[点我查看使用方法][long_image_grid_thumbnails]

### 配置

[ImageRequest] 和 [ImageOptions] 都提供了 resizeSize、resizePrecision、resizeScale 方法用于配置 [Resize]

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    resizeSize(100, 100)

    resizePrecision(Precision.KEEP_ASPECT_RATIO)
    // 或
    resizePrecision(longImageClipPrecision(Precision.KEEP_ASPECT_RATIO))

    resizeScale(Scale.END_CROP)
}
```

### 默认值

当你什么都不配置的情况下默认值为：

* width、height：如果 target 是 [ViewTarget] 就取 view 的宽高，否则取屏幕的宽高
* [Precision]：LESS_PIXELS
* [Scale]：CENTER_CROP

> 注意：如果 view 的宽高到 draw 阶段还是 0，那么请求不会继续执行

[Resize]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/Resize.kt

[Scale]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/Scale.kt

[FixedPrecisionDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/FixedPrecisionDecider.kt

[LongImageClipPrecisionDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/LongImageClipPrecisionDecider.kt

[PrecisionDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/PrecisionDecider.kt

[Precision]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/Precision.kt

[ViewTarget]: ../../sketch/src/main/java/com/github/panpf/sketch/target/ViewTarget.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageOptions.kt

[long_image_grid_thumbnails]: long_image_grid_thumbnails.md