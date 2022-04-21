# Resize

[Resize] 用来在解码时以及解码后调整图片的尺寸，解码时参与计算 inSampleSize，解码后如果尺寸依然不符合 [Resize] 要求就会再次调整

[Resize] 由以下几个概念构成：

* width、height：期望的宽和高
* [Precision]：精度。决定如何使用 width 和 height 去调整图片的尺寸
    * LESS_PIXELS（默认）：只要最终 Bitmap 的像素数（宽乘以高）约等于 [Resize] 的像素数即可，允许有 10% 的误差
    * SAME_ASPECT_RATIO：在 LESS_PIXELS 的基础上要求 Bitmap 的宽高比和 [Resize] 的宽高比一致，如果比例不一致会裁剪原图、优先使用
      BitmapRegionDecoder 裁剪
    * EXACTLY：最终 Bitmap 的尺寸一定和 [Resize] 一样，如果比例不一致会裁剪原图、优先使用 BitmapRegionDecoder 裁剪
* [PrecisionDecider]：精度决策器。针对具体的图片尺寸和 [Resize] 尺寸决定使用哪个 [Precision]
    * [FixedPrecisionDecider]：始终使用指定的 [Precision]
    * [LongImageClipPrecisionDecider]：如果是长图就使用指定的 [Precision]，否则始终使用 LESS_PIXELS
* [Scale]：缩放。需要对原图进行裁剪时决定如何裁剪原图
    * START_CROP：保留头部部分
    * CENTER_CROP：保留中间部分
    * END_CROP：保留尾部部分
    * FILL：全部保留，但会变形
* [ScaleDecider]：缩放决策器。针对具体的图片尺寸和 [Resize] 尺寸决定使用哪个 [Scale]
    * [FixedScaleDecider]：始终使用指定的 [Scale]
    * [LongImageScaleDecider]：指定两个 [Scale]，长图使用第一个，否则使用第二个

> 1. 长图的判定规则：[LongImageClipPrecisionDecider] 使用 [DefaultLongImageDecider] 来判定长图，具体规则为 [Resize] 的宽高比和原图的宽高比相差超过 2 倍
> 2. 使用 [LongImageClipPrecisionDecider] 有助于提高长图在网格列表中的清晰度，[查看具体介绍][long_image_grid_thumbnails]

### 配置

[ImageRequest] 和 [ImageOptions] 都提供了 resizeSize、resizePrecision、resizeScale 方法用于配置 [Resize]

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    resizeSize(100, 100)

    resizePrecision(Precision.SAME_ASPECT_RATIO)
    // 或
    resizePrecision(longImageClipPrecision(Precision.SAME_ASPECT_RATIO))

    resizeScale(Scale.END_CROP)
    // 或
    resizeScale(longImageScale(longImage = Scale.START_CROP, other = Scale.CENTER_CROP))
}
```

### 默认值

当你什么都不配置的情况下默认值为：

* width、height：如果 target 是 [ViewTarget] 就取 view 的宽高，否则取屏幕的宽高
* [Precision]：LESS_PIXELS
* [Scale]：CENTER_CROP

> 注意：如果 view 的宽高到 draw 阶段还是 0，那么请求不会继续执行

### resizeApplyToDrawable

[ImageRequest] 和 [ImageOptions] 的 resizeApplyToDrawable 属性用于将 [Resize] 应用到 [DisplayTarget] 的
placeholder, error, result Drawable 上

如果此属性为 true，Sketch 会用 [ResizeDrawable] 或 [ResizeAnimatableDrawable] 将 placeholder, error, result
Drawable 包一层，对外用 [Resize] 作为 intrinsicWidth 和 intrinsicHeight，内部用 [Resize] 的 scale 对 Drawable 进行缩放

此功能搭配 [CrossfadeTransition] 使用可实现完美过渡，[查看完美过渡介绍][transition]

[Resize]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/Resize.kt

[Scale]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/Scale.kt

[ScaleDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/ScaleDecider.kt

[FixedScaleDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/FixedScaleDecider.kt

[LongImageScaleDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/LongImageScaleDecider.kt

[FixedPrecisionDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/FixedPrecisionDecider.kt

[LongImageClipPrecisionDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/LongImageClipPrecisionDecider.kt

[PrecisionDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/PrecisionDecider.kt

[Precision]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/Precision.kt

[ViewTarget]: ../../sketch/src/main/java/com/github/panpf/sketch/target/ViewTarget.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageOptions.kt

[CrossfadeTransition]: ../../sketch/src/main/java/com/github/panpf/sketch/transition/CrossfadeTransition.kt

[DisplayTarget]: ../../sketch/src/main/java/com/github/panpf/sketch/target/DisplayTarget.kt

[ResizeDrawable]: ../../sketch/src/main/java/com/github/panpf/sketch/drawable/internal/ResizeDrawable.kt

[ResizeAnimatableDrawable]: ../../sketch/src/main/java/com/github/panpf/sketch/drawable/internal/ResizeAnimatableDrawable.kt

[DefaultLongImageDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/util/LongImageDecider.kt

[long_image_grid_thumbnails]: long_image_grid_thumbnails.md

[transition]: transition.md