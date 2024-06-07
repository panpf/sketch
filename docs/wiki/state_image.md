# StateImage

Translations: [简体中文](state_image_zh.md)

[StateImage] is used to provide images for loading status and error status. There are several
implementations:

For View:

* [DrawableStateImage]: Use Drawable as status picture
* [ColorDrawableStateImage]: Create a ColorDrawable using colors as status picture
* [IconDrawableStateImage]: Use IconDrawable as status picture. It can ensure that the size of the
  icon remains unchanged and is not affected by the scaling of the component. It is suitable for use
  in waterfall layouts.
* [IconAnimatableDrawableStateImage]: Use IconAnimatableDrawable as state image. It can ensure that
  the size of the icon remains unchanged and is not affected by the scaling of the component. It is
  suitable for use in waterfall layouts.

For Compose:

* [PainterStateImage]: Use Painter as status picture
* [ColorPainterStateImage]: Create a ColorPainter using colors as a status picture
* [IconPainterStateImage]: Use IconPainter as status image. It can ensure that the size of the icon
  remains unchanged and is not affected by the scaling of the component. It is suitable for use in
  waterfall layouts.
* [IconAnimatablePainterStateImage]: Use IconAnimatablePainter as state image. It can ensure that
  the size of the icon remains unchanged and is not affected by the scaling of the component. It is
  suitable for use in waterfall layouts.

Generic:

* [CurrentStateImage]: Use the component's current Image as the state image
* [MemoryCacheStateImage]: Use the given memory cache key to obtain the Image from the memory cache
  as the status image, and use crossfade to achieve a perfect transition from small to large images.
* [ThumbnailMemoryCacheStateImage]: A simplified version of [MemoryCacheStateImage] that uses the
  given or currently requested uri to match the aspect ratio of the image in the memory cache to be
  consistent with the original image, and the thumbnail that has not been modified by Transformation
  is used as the state image. It can also be used with crossfade to achieve a perfect transition
  from small images to large images.
* [ErrorStateImage]: Specially used for error status, you can choose different status pictures
  according to the error type

## Configuration

[StateImage] is used in the placeholder(), uriEmpty(), and error() methods of [ImageRequest]
and [ImageOptions], as follows:

```kotlin
// View
ImageRequest(context, "https://example.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    placeholder(context.getEqualityDrawable(R.drawable.placeholder))
    placeholder(IntColorDrawableStateImage(Color.RED))
    placeholder(DrawableStateImage(R.drawable.placeholder))
    placeholder(IconDrawableStateImage(R.drawable.placeholder, IntColor(Color.GRAY)))

    uriEmpty(R.drawable.uri_empty)
    uriEmpty(context.getEqualityDrawable(R.drawable.uri_empty))
    uriEmpty(IntColorDrawableStateImage(Color.RED))
    uriEmpty(DrawableStateImage(R.drawable.uri_empty))
    uriEmpty(IconDrawableStateImage(R.drawable.uri_empty, IntColor(Color.GRAY)))

    error(R.drawable.error) {
        uriEmptyError(DrawableStateImage(R.drawable.uri_empty))
    }
}

// Compose
val placeholder = rememberPainterStateImage(Res.drawable.placeholder)
//    val placeholder = rememberColorPainterStateImage(Color.Red)
//    val placeholder = rememberIconPainterStateImage(Res.drawable.placeholder, background = Color.Gray)
val uriEmpty = rememberPainterStateImage(Res.drawable.uri_empty)
//    val uriEmpty = rememberColorPainterStateImage(Color.Red)
//    val uriEmpty = rememberIconPainterStateImage(Res.drawable.uri_empty, background = Color.Gray)
ImageRequest("https://example.com/image.jpg") {
    placeholder(placeholder)
    uriEmpty(uriEmpty)
    error(Res.drawable.error) {
        uriEmptyError(uriEmpty)
    }
}
```

### Customize

You can refer to the existing implementation of [StateImage]

### ErrorStateImage

[ErrorStateImage] supports returning different status images according to different error types

By default, Sketch only provides one type, uriEmptyError. You can implement the [ErrorStateImage]
.Condition interface to extend the new type, and then use the custom type through [ErrorStateImage]
.Builder.addState(), as follows:

```kotlin
object MyCondition : ErrorStateImage.Condition {

    override fun accept(
        request: ImageRequest,
        throwable: Throwable?
    ): Boolean = throwable is IOException
}

ImageRequest(context, "https://example.com/image.jpg")
{
    error(R.drawable.error) {
        addState(MyCondition to DrawableStateImage(R.drawable.mystate))
    }
}
```

### Icon*StateImage

In the waterfall flow layout, since the size of each item may be different, when all items use the
same placeholder, the placeholder will appear to be larger or smaller on the page due to the scaling
of the component.

For this situation, using Icon\*StateImage can perfectly solve the problem. Icon\*StateImage
consists of an icon and a background. The icon is not affected by component scaling. The icon always
remains a fixed size, so that all placeholders on the page look the same. the size of

### ThumbnailMemoryCacheStateImage

When jumping from the image list page to the image details page, we hope to use the thumbnail image
loaded on the list page as a placeholder image when the details page loads the large image.

In this way, in conjunction with `crossfade(fadeStart = false)`, when the large image is loaded, the
page will gradually change from a blurry image to a clear image, which will have a better effect.

[ThumbnailMemoryCacheStateImage] can help us find thumbnails from the memory cache very
conveniently, as follows:

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
    placeholder(ThumbnailMemoryCacheStateImage())
    crossfade(fadeStart = false)
}
```

[ThumbnailMemoryCacheStateImage] By default, the uri of the current [ImageRequest] will be used to
find thumbnails in the memory, but if the list page and the details page use different
uri, you need to actively specify the uri of the list page, as follows:

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
    placeholder(ThumbnailMemoryCacheStateImage("https://www.sample.com/image.jpg?widht=300"))
    crossfade(fadeStart = false)
}
```

> [!TIP]
> The standard for thumbnails is images with the same aspect ratio and without any Transformation
> modification.

[StateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/state/StateImage.kt

[ColorDrawableStateImage]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/state/ColorDrawableStateImage.common.kt

[ColorPainterStateImage]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/state/ColorPainterStateImage.kt

[DrawableStateImage]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/state/DrawableStateImage.common.kt

[ErrorStateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/state/ErrorStateImage.common.kt

[IconDrawableStateImage]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/state/IconDrawableStateImage.common.kt

[IconAnimatableDrawableStateImage]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/state/IconAnimatableDrawableStateImage.common.kt

[IconPainterStateImage]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/state/IconPainterStateImage.common.kt

[IconAnimatablePainterStateImage]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/state/IconAnimatablePainterStateImage.common.kt

[MemoryCacheStateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/state/MemoryCacheStateImage.kt

[ThumbnailMemoryCacheStateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/state/ThumbnailMemoryCacheStateImage.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.kt

[CurrentStateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/state/CurrentStateImage.kt

[PainterStateImage]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/state/PainterStateImage.kt