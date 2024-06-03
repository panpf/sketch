# Resize

Translations: [简体中文](resize_zh.md)

[Resize] is used to adjust the size of the picture. It participates in the calculation of
inSampleSize during decoding. If the size still does not meet the requirements of [Resize] after
decoding, it will be adjusted again.

[Resize] consists of the following concepts:

* width、height: Desired width and height
* [Precision]: Decide how to use width and height to resize the image
    * LESS_PIXELS: Default precision. As long as the number of pixels of the final Bitmap (width
      times height) is less than or equal to the number of pixels of [Resize]
    * SMALLER_SIZE: As long as the width and height of the final Bitmap are less than or equal to
      the width and height of [Resize]
    * SAME_ASPECT_RATIO: The final aspect ratio of Bitmap is consistent with the aspect ratio
      of [Resize] and the number of pixels must be less than [Resize]. If the ratio is inconsistent,
      it will be calculated according to [Scale]
      Crop the original image, preferably using BitmapRegionDecoder.
    * EXACTLY: The final Bitmap size must be the same as [Resize]. If the size is inconsistent, the
      original image will be cropped according to [Scale], and BitmapRegionDecoder will be used
      first for cropping.
* [PrecisionDecider]: Decide which [Precision] to use based on image size and [Resize]
    * [FixedPrecisionDecider]: Always use the specified [Precision]
    * [LongImageClipPrecisionDecider]: If it is a long image, use the specified [Precision],
      otherwise always use LESS_PIXELS
* [Scale]: Determines how to crop the original image when [Precision] is EXACTLY or
  SAME_ASPECT_RATIO
    * START_CROP: Keep the start part
    * CENTER_CROP: Keep the middle part
    * END_CROP: Keep the end part
    * FILL: Keep all, but deformed
* [ScaleDecider]: Decide which [Scale] to use based on image size and [Resize]
    * [FixedScaleDecider]: Always use the specified [Scale]
    * [LongImageScaleDecider]: Specify two [Scale], the first one is used for long images, otherwise
      the second one is used

> 1. The default implementation of long image rules is [DefaultLongImageDecider], you can also
     create [LongImageClipPrecisionDecider] or [LongImageScaleDecider] when using custom rules
> 2. Using [LongImageClipPrecisionDecider] helps improve the clarity of long images in the grid
     list, [View detailed introduction][long_image_grid_thumbnails]

### Configure

[ImageRequest] and [ImageOptions] both provide resize, resizeSize, resizePrecision, resizeScale
Method used to configure [Resize]

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
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
    resizeSize(100, 100)
    // or
    resizeSize(Size(100, 100))
    // or
    resizeSize(FixedSizeResolver(100, 100))

    /* Set precision properties only */
    resizePrecision(Precision.SAME_ASPECT_RATIO)
    // or
    resizePrecision(LongImageClipPrecisionDecider(Precision.SAME_ASPECT_RATIO))

    /* Set scale properties only */
    resizeScale(Scale.END_CROP)
    // or
    resizeScale(LongImageScaleDecider(longImage = Scale.START_CROP, otherImage = Scale.CENTER_CROP))
}
```

### Default Value

* width、height:
    1. If target is [ViewTarget]
        1. Prioritize the width and height of view's LayoutParams
        2. Secondly, it is delayed to the drawing stage to obtain the width and height of the View.
           If the width and height are still 0 in the drawing stage, the request will not continue
           to be executed.
    2. If used in compose
        1. If AsyncImage is used, take the measured width and height
        2. If AsyncImagePainter is used directly, the drawing width and height are obtained in the
           drawing stage. Similarly, the width and height are still the same in the drawing stage.
           0, then the request will not continue to execute
    3. Get the width and height of the screen
* [Precision]：LESS_PIXELS
* [Scale]：CENTER_CROP

### resizeApplyToDrawable

The resizeApplyToDrawable attribute of [ImageRequest] and [ImageOptions] is used to apply [Resize]
to the placeholder, error, result Drawable of [Target]

Sketch will use [ResizeDrawable] or [ResizeAnimatableDrawable] to convert placeholder, error, result
Drawable Wrapping one layer, externally using [Resize] as intrinsicWidth and intrinsicHeight,
internally using [Resize]'s scale for Drawable Zoom

This function can be used with [CrossfadeTransition] to achieve a perfect
transition, [View introduction to perfect transition][transition]

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

[ViewTarget]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/ViewTarget.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.kt

[CrossfadeTransition]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transition/CrossfadeTransition.kt

[Target]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/Target.kt

[ResizeDrawable]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/drawable/internal/ResizeDrawable.kt

[ResizeAnimatableDrawable]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/drawable/internal/ResizeDrawable.kt

[DefaultLongImageDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/LongImageDecider.kt

[long_image_grid_thumbnails]: long_image_grid_thumbnails.md

[transition]: transition.md