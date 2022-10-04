# Resize

[Resize] 用来在解码时以及解码后调整图片的尺寸，解码时参与计算 inSampleSize，解码后如果尺寸依然不符合 [Resize] 要求就会再次调整

[Resize] 由以下几个概念构成：

* width、height：期望的宽和高
* [Precision]：精度。决定如何使用 width 和 height 去调整图片的尺寸
    * LESS_PIXELS：默认精度。只要最终 Bitmap 的像素数（宽乘以高）小于等于 [Resize] 的像素数即可
    * SAME_ASPECT_RATIO：最终 Bitmap 的宽高比和 [Resize] 的宽高比一致并且像素数一定少于 [Resize]，如果比例不一致会根据 [Scale]
      裁剪原图、优先使用 BitmapRegionDecoder 裁剪
    * EXACTLY：最终 Bitmap 的尺寸一定和 [Resize] 一样，如果尺寸不一致会根据 [Scale] 裁剪原图、优先使用 BitmapRegionDecoder 裁剪
* [PrecisionDecider]：精度决策器。针对具体的图片尺寸和 [Resize] 尺寸决定使用哪个 [Precision]
    * [FixedPrecisionDecider]：始终使用指定的 [Precision]
    * [LongImageClipPrecisionDecider]：如果是长图就使用指定的 [Precision]，否则始终使用 LESS_PIXELS
* [Scale]：缩放。[Precision] 为 EXACTLY 或 SAME_ASPECT_RATIO 时决定如何裁剪原图
    * START_CROP：保留头部部分
    * CENTER_CROP：保留中间部分
    * END_CROP：保留尾部部分
    * FILL：全部保留，但会变形
* [ScaleDecider]：缩放决策器。针对具体的图片尺寸和 [Resize] 尺寸决定使用哪个 [Scale]
    * [FixedScaleDecider]：始终使用指定的 [Scale]
    * [LongImageScaleDecider]：指定两个 [Scale]，长图使用第一个，否则使用第二个

> 1. 长图规则默认实现为 [DefaultLongImageDecider]，你还可以在创建 [LongImageClipPrecisionDecider] 或 [LongImageScaleDecider] 时使用自定义的规则
> 2. 使用 [LongImageClipPrecisionDecider] 有助于提高长图在网格列表中的清晰度，[查看具体介绍][long_image_grid_thumbnails]

### 配置

[ImageRequest] 和 [ImageOptions] 都提供了 resize、resizeSize、resizePrecision、resizeScale 方法用于配置 [Resize]

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    /* 一次设置三个属性 */
    resize(
        width = 100,
        height = 100,
        precision = Precision.SAME_ASPECT_RATIO,
        scale = Scale.END_CROP
    )
    // 或
    resize(
        size = Size(100, 100),
        precision = LongImageClipPrecisionDecider(Precision.SAME_ASPECT_RATIO),
        scale = LongImageScaleDecider(longImage = Scale.START_CROP, otherImage = Scale.CENTER_CROP)
    )
    // 或
    resize(
        size = FixedSizeResolver(100, 100),
        precision = LongImageClipPrecisionDecider(Precision.SAME_ASPECT_RATIO),
        scale = LongImageScaleDecider(longImage = Scale.START_CROP, otherImage = Scale.CENTER_CROP)
    )

    /* 仅设置尺寸属性 */
    resizeSize(100, 100)
    // 或
    resizeSize(Size(100, 100))
    // 或
    resizeSize(FixedSizeResolver(100, 100))

    /* 仅设置精度属性 */
    resizePrecision(Precision.SAME_ASPECT_RATIO)
    // 或
    resizePrecision(LongImageClipPrecisionDecider(Precision.SAME_ASPECT_RATIO))

    /* 仅设置缩放属性 */
    resizeScale(Scale.END_CROP)
    // 或
    resizeScale(LongImageScaleDecider(longImage = Scale.START_CROP, otherImage = Scale.CENTER_CROP))
}
```

### 默认值

* width、height：
    1. 如果 target 是 [ViewDisplayTarget]
        1. 优先取 view 的 LayoutParams 宽高
        2. 其次延迟到绘制阶段取 View 的宽高，如果到绘制阶段还是宽高还是 0，那么请求不会继续执行
    2. 如果是在 compose 中使用
        1. 如果是使用的 AsyncImage 则取测量宽高
        2. 如果是直接使用的 AsyncImagePainter 则在绘制阶段取绘制宽高，同样到绘制阶段还是宽高还是 0，那么请求不会继续执行
    3. 取屏幕的宽高
* [Precision]：LESS_PIXELS
* [Scale]：CENTER_CROP

### resizeApplyToDrawable

[ImageRequest] 和 [ImageOptions] 的 resizeApplyToDrawable 属性用于将 [Resize] 应用到 [DisplayTarget] 的
placeholder, error, result Drawable 上

Sketch 会用 [ResizeDrawable] 或 [ResizeAnimatableDrawable] 将 placeholder, error, result Drawable
包一层，对外用 [Resize] 作为 intrinsicWidth 和 intrinsicHeight，内部用 [Resize] 的 scale 对 Drawable 进行缩放

此功能搭配 [CrossfadeTransition] 使用可实现完美过渡，[查看完美过渡介绍][transition]

[Sketch]: ../../sketch/src/main/java/com/github/panpf/sketch/Sketch.kt

[Resize]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/Resize.kt

[Scale]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/Scale.kt

[ScaleDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/ScaleDecider.kt

[FixedScaleDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/ScaleDecider.kt

[LongImageScaleDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/ScaleDecider.kt

[FixedPrecisionDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/PrecisionDecider.kt

[LongImageClipPrecisionDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/PrecisionDecider.kt

[PrecisionDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/PrecisionDecider.kt

[Precision]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/Precision.kt

[ViewDisplayTarget]: ../../sketch/src/main/java/com/github/panpf/sketch/target/ViewDisplayTarget.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageOptions.kt

[CrossfadeTransition]: ../../sketch/src/main/java/com/github/panpf/sketch/transition/CrossfadeTransition.kt

[DisplayTarget]: ../../sketch/src/main/java/com/github/panpf/sketch/target/DisplayTarget.kt

[ResizeDrawable]: ../../sketch/src/main/java/com/github/panpf/sketch/drawable/internal/ResizeDrawable.kt

[ResizeAnimatableDrawable]: ../../sketch/src/main/java/com/github/panpf/sketch/drawable/internal/ResizeAnimatableDrawable.kt

[DefaultLongImageDecider]: ../../sketch/src/main/java/com/github/panpf/sketch/resize/LongImageDecider.kt

[long_image_grid_thumbnails]: long_image_grid_thumbnails.md

[transition]: transition.md