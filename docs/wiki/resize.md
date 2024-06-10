# Resize

Translations: [简体中文](resize_zh.md)

[Sketch] will adjust the size of the image to avoid exceeding the target and causing memory waste.

Adjusting the image size depends on the sizeResolver, precisionDecider and scaleDecider
of [ImageRequest]
Attributes, when they are determined, a [Resize] will be generated and handed over to [Decoder] for
use.

[Decoder] First reduce the image size through subsampling or regional subsampling during decoding.
If the size still does not meet the [Resize] requirements after decoding, it will be adjusted again.

[Resize] consists of [Size], [Precision], [Scale]

* [Size]: Desired width and height
* [Precision]: Decide how to use [Size] to resize images
    * LESS_PIXELS: As long as the number of pixels of the final Image (width times height) is less
      than or equal to the number of pixels of [Size]
    * SMALLER_SIZE: As long as the width and height of the final Image are less than or equal
      to [Size]
    * SAME_ASPECT_RATIO: Ensure that the aspect ratio of the final Image is consistent with the
      aspect ratio of [Size] and the number of pixels is less than or equal to the number of pixels
      of [Size]. If they are inconsistent, the original image will be cropped according to [Scale]
    * EXACTLY: Make sure the size of the final Image is consistent with [Size], if not the original
      image will be cropped according to [Scale]
* [Scale]: Determines how to crop the original image when [Precision] is EXACTLY or
  SAME_ASPECT_RATIO
    * START_CROP: Keep the head part
    * CENTER_CROP: Keep the center part
    * END_CROP: Keep the end part
    * FILL: All retained but deformed

## Configuration

Both [ImageRequest] and [ImageOptions] provide resize, size, precision, and scale methods for
configuring [Resize], as follows:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    /* Set three properties at once */
    resize(
        width = 100,
        height = 100,
        precision = Precision.SAME_ASPECT_RATIO,
        scale = Scale.END_CROP
    )
    // or
    resize(
        size = Size(100, 100),
        precision = LongImageClipPrecisionDecider(Precision.SAME_ASPECT_RATIO),
        scale = LongImageScaleDecider(longImage = Scale.START_CROP, otherImage = Scale.CENTER_CROP)
    )
    // or
    resize(
        size = FixedSizeResolver(100, 100),
        precision = LongImageClipPrecisionDecider(Precision.SAME_ASPECT_RATIO),
        scale = LongImageScaleDecider(longImage = Scale.START_CROP, otherImage = Scale.CENTER_CROP)
    )

    /* Set size properties only */
    size(100, 100)
    // or
    size(Size(100, 100))
    // or
    size(FixedSizeResolver(100, 100))

    /* Set precision properties only */
    precision(Precision.SAME_ASPECT_RATIO)
    // or
    precision(LongImageClipPrecisionDecider(Precision.SAME_ASPECT_RATIO))

    /* Set only scale properties */
    scale(Scale.END_CROP)
    // or
    scale(LongImageScaleDecider(longImage = Scale.START_CROP, otherImage = Scale.CENTER_CROP))
}
```

## SizeResolver

[Sketch] Use Resolver wrapper to provide [Size] for [ImageRequest], this is because the size of the
View or Compose component changes when building [ImageRequest] may not be determined and needs to
wait until the drawing stage to obtain it, so use [SizeResolver] to solve this problem

## PrecisionDecider 和 ScaleDecider

[Sketch] also uses the Decider wrapper to provide [Precision] and [Scale] for [ImageRequest], so
that when decoding, you can dynamically decide which [Precision] and [Scale] to use based on the
image size and [Resize]

The following implementations are provided by default:

* [PrecisionDecider]: Determine which [Precision] to use based on the image size and [Size]
  of [Resize]
    * [FixedPrecisionDecider]: Always use the specified [Precision]
    * [LongImageClipPrecisionDecider]: If it is a long image, use the specified [Precision],
      otherwise always use LESS_PIXELS
* [ScaleDecider]: Decide which [Scale] to use based on the image size and [Size] of [Resize]
    * [FixedScaleDecider]: Always use the specified [Scale]
    * [LongImageScaleDecider]: Specify two [Scale], the first one is used for long images, otherwise
      the second one is used

> [!TIP]
> 1. Using [LongImageClipPrecisionDecider] and [LongImageScaleDecider] helps improve the clarity of
     long images in grid lists. [Learn more][long_image_grid_thumbnails]
> 2. The default implementation of long image rules is [DefaultLongImageDecider]. You can also use
     custom rules when creating [LongImageClipPrecisionDecider] or [LongImageScaleDecider]

## Build order and defaults

Determining the values of these properties when building [ImageRequest] is still a bit complicated,
here is a simple building sequence:

* [Size]:
    1. [ImageRequest].Builder.sizeResolver
    2. [Target].getImageOptions().sizeResolver
    3. [ImageRequest].Builder.defaultOptions.sizeResolver
    4. [Sketch].globalImageOptions.sizeResolver
    5. [Target].getSizeResolver()
        1. View 或 Compose 组件的宽高
        2. DisplayMetrics size 或 LocalWindow containerSize
    6. [OriginSizeResolver]
* [Precision]:
    1. [ImageRequest].Builder.precisionDecider
    2. [Target].getImageOptions().precisionDecider
    3. [ImageRequest].Builder.defaultOptions.precisionDecider
    4. [Sketch].globalImageOptions.precisionDecider
    5. [Precision].LESS_PIXELS
* [Scale]:
    1. [ImageRequest].Builder.scaleDecider
    2. [Target].getImageOptions().scaleDecider
    3. [ImageRequest].Builder.defaultOptions.scaleDecider
    4. [Sketch].globalImageOptions.scaleDecider
    5. [Scale].CENTER_CROP

> [!TIP]
> 1. When [Target] is [ViewTarget], the LayoutParams width and height of the View are taken first,
     and then the measured width and height of the View are delayed until the drawing stage. If the
     drawing stage is not executed, the request will not be executed.
> 2. If the width of the component is a fixed value (for example, 100) and the height is wrap, Size
     will be '100xContainerHeight'
> 3. For detailed build rules, please refer to the [ImageRequest].Builder.build() method

## resizeOnDraw

The resizeOnDraw properties of [ImageRequest] and [ImageOptions] are used to apply [Resize] to the
placeholder, error, result [Image] of [Target] to change the size of [Image] during drawing

resizeOnDraw relies on [ResizeOnDrawHelper] implementation, [ResizeOnDrawHelper] will
use [ResizeDrawable] or [ResizePainter] to wrap placeholder, error, result [Image] with a
layer, [Size] of [Resize] is used externally as the width and height, and [Scale] of [Resize] is
used internally to scale [Image]

resizeOnDraw paired with [CrossfadeTransition] can achieve a perfect
transition. [Understanding Perfect Transition](transition.md#perfect-transition)

> [!IMPORTANT]
> 1. [ResizeOnDrawHelper] is provided by [Target], so if [Target] is not set, the resizeOnDraw
     property will have no effect

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

[long_image_grid_thumbnails]: long_image_grid_thumbnails.md