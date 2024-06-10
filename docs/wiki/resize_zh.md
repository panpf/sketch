# Resize

翻译：[English](resize.md)

[Sketch] 会调整图片的大小避免超出 Target 的需要造成内存浪费

调整图片大小依赖 [ImageRequest] 的 sizeResolver、precisionDecider、scaleDecider
属性，当他们都确定时会生成一个 [Resize] 交给 [Decoder] 使用

[Decoder] 先在解码时通过子采样或区域子采样降低图片大小，解码后如果大小依然不符合 [Resize] 要求就会再次调整

[Resize] 由 [Size]、[Precision]、[Scale] 构成

* [Size]：期望的宽和高
* [Precision]：精度。决定如何使用 [Size] 去调整图片的大小
    * LESS_PIXELS：只要最终 Image 的像素数（宽乘以高）小于等于 [Size] 的像素数即可
    * SMALLER_SIZE：只要最终 Image 的宽和高都小于等于 [Size] 即可
    * SAME_ASPECT_RATIO：确保最终 Image 的宽高比和 [Size] 的宽高比一致并且像素数小于等于 [Size]
      的像素数，如果不一致会根据 [Scale] 裁剪原图
    * EXACTLY：确保最终 Image 的大小和 [Size] 一致，如果不一致会根据 [Scale] 裁剪原图
* [Scale]：缩放。[Precision] 为 EXACTLY 或 SAME_ASPECT_RATIO 时决定如何裁剪原图
    * START_CROP：保留头部部分
    * CENTER_CROP：保留中间部分
    * END_CROP：保留尾部部分
    * FILL：全部保留，但会变形

## 配置

[ImageRequest] 和 [ImageOptions] 都提供了 resize、size、precision、scale 方法用于配置 [Resize]，如下：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
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

    /* 仅设置大小属性 */
    size(100, 100)
    // 或
    size(Size(100, 100))
    // 或
    size(FixedSizeResolver(100, 100))

    /* 仅设置精度属性 */
    precision(Precision.SAME_ASPECT_RATIO)
    // 或
    precision(LongImageClipPrecisionDecider(Precision.SAME_ASPECT_RATIO))

    /* 仅设置缩放属性 */
    scale(Scale.END_CROP)
    // 或
    scale(LongImageScaleDecider(longImage = Scale.START_CROP, otherImage = Scale.CENTER_CROP))
}
```

## SizeResolver

[Sketch] 使用 Resolver 包装器为 [ImageRequest] 提供 [Size]，这是因为 View 或 Compose 组件的大小在构建
[ImageRequest] 时可能无法确定，需要等到绘制阶段才能获取，所以借助 [SizeResolver] 来解决这个问题

## PrecisionDecider 和 ScaleDecider

[Sketch] 同样使用 Decider 包装器为 [ImageRequest] 提供 [Precision] 和 [Scale]
，这是样就可以在解码时根据图片大小和 [Resize] 动态决定使用何种 [Precision] 和 [Scale]

默认提供了以下实现：

* [PrecisionDecider]：精度决策器。根据图片大小和 [Resize] 的 [Size] 决定使用何种 [Precision]
    * [FixedPrecisionDecider]：始终使用指定的 [Precision]
    * [LongImageClipPrecisionDecider]：如果是长图就使用指定的 [Precision]，否则始终使用 LESS_PIXELS
* [ScaleDecider]：缩放决策器。根据图片大小和 [Resize] 的 [Size] 决定使用何种 [Scale]
    * [FixedScaleDecider]：始终使用指定的 [Scale]
    * [LongImageScaleDecider]：指定两个 [Scale]，长图使用第一个，否则使用第二个

> [!TIP]
> 1. 使用 [LongImageClipPrecisionDecider] 和 [LongImageScaleDecider]
     有助于提高长图在网格列表中的清晰度。[了解更多][long_image_grid_thumbnails]
> 2. 长图规则的默认实现为 [DefaultLongImageDecider]，你还可以在创建 [LongImageClipPrecisionDecider]
     或 [LongImageScaleDecider] 时使用自定义的规则

## 构建顺序和默认值

在构建 [ImageRequest] 时确定这些属性的值还是有些复杂的，这里是一个简单的构建顺序：

* [Size]：
    1. [ImageRequest].Builder.sizeResolver
    2. [Target].getImageOptions().sizeResolver
    3. [ImageRequest].Builder.defaultOptions.sizeResolver
    4. [Sketch].globalImageOptions.sizeResolver
    5. [Target].getSizeResolver()
        1. View 或 Compose 组件的宽高
        2. DisplayMetrics size 或 LocalWindow containerSize
    6. [OriginSizeResolver]
* [Precision]：
    1. [ImageRequest].Builder.precisionDecider
    2. [Target].getImageOptions().precisionDecider
    3. [ImageRequest].Builder.defaultOptions.precisionDecider
    4. [Sketch].globalImageOptions.precisionDecider
    5. [Precision].LESS_PIXELS
* [Scale]：
    1. [ImageRequest].Builder.scaleDecider
    2. [Target].getImageOptions().scaleDecider
    3. [ImageRequest].Builder.defaultOptions.scaleDecider
    4. [Sketch].globalImageOptions.scaleDecider
    5. [Scale].CENTER_CROP

> [!TIP]
> 1. [Target] 是 [ViewTarget] 时优先取 View 的 LayoutParams 宽高，其次延迟到绘制阶段取 View
     的测量宽高，如果没有执行到绘制阶段那么请求也不会执行
> 2. 假如组件的宽是固定值（例如 100），高是 wrap 时，Size 将会是 '100x屏幕或容器的高'
> 3. 详细构建规则请参考 [ImageRequest].Builder.build() 方法

## resizeOnDraw

[ImageRequest] 和 [ImageOptions] 的 resizeOnDraw 属性用于将 [Resize] 应用到 [Target] 的 placeholder,
error, result [Image] 上，在绘制期间改变 [Image] 的大小

resizeOnDraw 依赖 [ResizeOnDrawHelper] 实现，[ResizeOnDrawHelper] 会用 [ResizeDrawable]
或 [ResizePainter] 将 placeholder, error, result [Image] 包一层，对外用 [Resize] 的 [Size]
作为宽和高，内部用 [Resize] 的 [Scale] 对 [Image] 进行缩放

resizeOnDraw 搭配 [CrossfadeTransition]
可实现完美过渡。[了解完美过度](transition_zh.md#完美过渡)

> [!IMPORTANT]
> 1. [ResizeOnDrawHelper] 由 [Target] 提供，因此如果没有设置 [Target]，resizeOnDraw 属性也将无效

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.kt

[Resize]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/Resize.kt

[Scale]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/Scale.kt

[ScaleDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/ScaleDecider.kt

[FixedScaleDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/ScaleDecider.kt

[LongImageScaleDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/ScaleDecider.kt

[FixedPrecisionDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/PrecisionDecider.kt

[LongImageClipPrecisionDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/PrecisionDecider.kt

[PrecisionDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/PrecisionDecider.kt

[Precision]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/Precision.kt

[ViewTarget]: ../../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/target/ViewTarget.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.kt

[CrossfadeTransition]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transition/CrossfadeTransition.kt

[Target]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/Target.kt

[ResizeDrawable]: ../../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/drawable/ResizeDrawable.kt

[ResizeAnimatableDrawable]: ../../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/drawable/ResizeAnimatableDrawable.kt

[DefaultLongImageDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/LongImageDecider.kt

[Decoder]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[Size]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/util/Size.kt

[OriginSizeResolver]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/SizeResolver.kt

[Image]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Image.kt

[ResizeOnDrawHelper]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/ResizeOnDraw.kt

[long_image_grid_thumbnails]: long_image_grid_thumbnails_zh.md