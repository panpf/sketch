# Transition

Translations: [简体中文](transition_zh.md)

[Transition] is used to configure the transition method between the new picture and the old picture when it is displayed. [CrossfadeTransition] is provided by default to support the fade-in and fade-out effect.

### Configuration

Both [ImageRequest] and [ImageOptions] provide the crossfade() method and transitionFactory() method for configuring [Transition] ,as follows:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    crossfade()
    // or
    transitionFactory(CrossfadeTransition.Factory())
}
```

### Customize

Please refer to the implementation of [CrossfadeTransition]

### Perfect transition

[CrossfadeTransition] Use the maximum width and height of the placeholder image and result image as the width and height of the new image, and then change the placeholder Image and result image are scaled

If the size of the result image and the placeholder image are inconsistent, for example, the result is larger than the placeholder, the placeholder image will be enlarged at the beginning of the transition. Although this process is fast, it is still easy to see.

The best way to solve this problem is to keep the size of the placeholder image and the result image consistent. This effect can be easily achieved with the help of the resizeOnDraw attribute of [ImageRequest] and [ImageOptions]

The resizeOnDraw attribute will use [ResizePainter] or [ResizeDrawable] to wrap the placeholder, error, and result images, use [ImageRequest].size as the new size, and then use the [ImageRequest].scale attribute to scale the image. [Learn about resizeOnDraw][resizeOnDraw]

Therefore, it is usually recommended to use [CrossfadeTransition] and resizeOnDraw together, as follows:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    placeholder(R.drawable.im_placeholder)
    crossfade()
    resizeOnDraw()
}
```

[Transition]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transition/Transition.kt

[CrossfadeTransition]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transition/CrossfadeTransition.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt

[ResizePainter]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/painter/ResizePainter.kt

[ResizeDrawable]: ../../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/drawable/ResizeDrawable.kt

[resizeOnDraw]: resize.md#resizeOnDraw