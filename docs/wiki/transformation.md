# Transformation

Translations: [简体中文](transformation_zh.md)

[Transformation] can convert the decoded Image. Sketch has the following built-in [Transformation]

* [CircleCropTransformation]: Crop the image into a circle
* [RotateTransformation]: Rotate the image by a specified angle
* [RoundedCornersTransformation]: Crop the image into a rounded rectangle
* [MaskTransformation]: Cover the image with a color mask, often used to darken the image when using
  it as the background of a window or module.
* [BlurTransformation]: Blur the image

> [!TIP]
> 1. [Transformation] does not support animated graphics. Please use the animatedTransformation()
     function for animated image.
> 2. When using [RoundedCornersTransformation], please use it with 'precision(Precision.EXACTLY)',
     because if the original image size is the same as When the resize size is inconsistent, the
     final fillet will be scaled during display, resulting in the fillet size being inconsistent
     with expectations.

### Configure

Both [ImageRequest] and [ImageOptions] provide transformations methods for
configuring [Transformation]

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    transformations(CircleCropTransformation(), RoundedCornersTransformation(20f))
}
```

### Customize

When customizing [Transformation], you need to pay attention to the implementation of the key
attribute. Because the key attribute is used to build the cache key, you must ensure the uniqueness
and certainty of the key attribute:

* The same [Transformation] inputs the same parameters and outputs the same key
* The same [Transformation] inputs different parameters and outputs different keys.

> [!TIP]
> For custom [Transformation], do not perform recycle() on the input Image of the transform
> method. This will cause unpredictable errors.

[Transformation]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transform/Transformation.kt

[CircleCropTransformation]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transform/CircleCropTransformation.common.kt

[RotateTransformation]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transform/RotateTransformation.common.kt

[RoundedCornersTransformation]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transform/RoundedCornersTransformation.common.kt

[MaskTransformation]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transform/MaskTransformation.common.kt

[BlurTransformation]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transform/BlurTransformation.common.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt