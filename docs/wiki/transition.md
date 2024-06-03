# Transition

Translations: [简体中文](transition_zh.md)

[Transition] is used to configure the transition method between the old picture and the picture when
it is displayed. [CrossfadeTransition] is provided by default to support the fade-in and fade-out
effect.

### Configure

Both [ImageRequest] and [ImageOptions] provide the crossfade() method and transition() method for
configuring [Transition]
,as follows:

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
    crossfade()
    // or
    transitionFactory(CrossfadeTransition.Factory())
}
```

### Customize

Please refer to the implementation of [CrossfadeTransition]

### Make the perfect transition

[CrossfadeTransition] uses [CrossfadeDrawable] to implement transition, [CrossfadeDrawable] takes
placeholder image and The maximum width and height of the result image are used as the width and
height of the new Drawable, and then the placeholder image and result image are scaled.

#### Problem

If the size of the result image and the placeholder image are inconsistent, for example, the result
is larger than the placeholder, the placeholder image will be resized during the transition. The
display effect on the page is that the placeholder image will be quickly enlarged at the beginning
of the transition. If the aspect ratio Inconsistencies will lead to deformation. Although this
process is quick, it is still easy to see.

#### Solution

The best way to solve this problem is to keep the size of the placeholder image and the result image
consistent. This effect can be easily achieved with the help of the resizeApplyToDrawable attribute
of [ImageRequest] and [ImageOptions]

The resizeApplyToDrawable attribute uses ResizeDrawable to wrap the placeholder, error, and result
drawables in a layer, uses Resize as the new size, and internally uses the scale attribute of Resize
to scale the drawable. [View more resizeApplyToDrawable introduction][resize]

Therefore, it is usually recommended to use [CrossfadeTransition] and resizeApplyToDrawable
together, as follows:

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
    placeholder(R.drawable.im_placeholder)
    crossfade()
    resizeApplyToDrawable()
}
```

> The TransitionDrawable that comes with Android also uses the maximum size of the start and end
> images as the size of the new Drawable, which also has the same problem.

[Transition]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transition/Transition.kt

[CrossfadeTransition]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transition/CrossfadeTransition.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.kt

[CrossfadeDrawable]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/drawable/internal/CrossfadeDrawable.kt

[resize]: resize.md