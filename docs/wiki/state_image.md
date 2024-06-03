# StateImage

Translations: [简体中文](state_image_zh.md)

StateImage is used to provide images for loading status and error status. There are several
implementations:

* [CurrentStateImage]: Use the current drawable of ImageView as the state image
* [ColorStateImage]: Create a ColorDrawable as a state image using the given color value or color
  resource Id
* [DrawableStateImage]: Create a Drawable as a state image using the given Drawable or Drawable
  resource Id
* [ErrorStateImage]: Specially used for error status, different status images will be selected
  according to the error type
* [IconStateImage]: Create a state image using the given icon Drawable and background Drawable,
  which ensures that regardless of the View How big is the size? The icon is always centered and the
  size remains unchanged. It is more suitable for use in waterfall layouts.
* [MemoryCacheStateImage]: Using the given memory cache key, try to get the bitmap from the memory
  cache As a status picture, it can be used with crossfade to achieve a perfect transition from
  small pictures to large pictures.
* [ThumbnailMemoryCacheStateImage]: Use the given or current request uri to match the aspect ratio
  in the memory cache to be consistent with the original image, and is useless Transformation
  modified thumbnail as status image

### Configure

Both [ImageRequest] and [ImageOptions] provide placeholder(), uriEmpty(), error() methods, as
follows:

```kotlin

import java.awt.Color

imageView.displayImage("https://example.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    placeholder(resources.getDrawable(R.drawable.placeholder))
    placeholder(ColorStateImage(IntColor(Color.RED)))
    placeholder(DrawableStateImage(R.drawable.placeholder))
    placeholder(IconStateImage(R.drawable.placeholder_icon, IntColor(Color.GRAY)))

    uriEmpty(R.drawable.placeholder)
    uriEmpty(resources.getDrawable(R.drawable.placeholder))
    uriEmpty(ColorStateImage(IntColor(Color.RED)))
    uriEmpty(DrawableStateImage(R.drawable.placeholder))
    uriEmpty(IconStateImage(R.drawable.placeholder_icon, IntColor(Color.GRAY)))

    // error is internally implemented using ErrorStateImage, so there is an additional lambda function that can configure specific error conditions.
    // And the error() method that can be used by the placeholder() method can also be used.
    error(R.drawable.error) {
        uriEmptyError(DrawableStateImage(R.drawable.uri_empty))
    }
}
```

### Customize

You can refer to the existing implementation of [StateImage]

### Extends ErrorStateImage

[ErrorStateImage] supports returning different status images according to different error types

By default, Sketch only provides uriEmptyError type, you can implement [CombinedStateImage]
.Condition interface to extend the new type, and then pass [ErrorStateImage].Builder.addState() uses
a custom type, as follows:

```kotlin

import java.io.IOException

object MyCondition : CombinedStateImage.Condition {

    override fun accept(
        request: ImageRequest,
        throwable: Throwable?
    ): Boolean = throwable is IOException
}

imageView.displayImage("https://example.com/image.jpg")
{
    error(R.drawable.error) {
        addState(MyCondition to DrawableStateImage(R.drawable.uri_empty))
    }
}
```

### IconStateImage

In the waterfall flow layout, since the size of each item may be different, when all items use the
same placeholder, ImageView The scaling causes the placeholder to appear larger or smaller on the
page.

For this situation, using [IconStateImage] can perfectly solve the problem. [IconStateImage]
consists of an icon and a background, and has no fixed size. No matter how big the bounds are, the
icon will remain a fixed size, so that all placeholders on the page look like same size

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
find thumbnails in the memory. However, if the list page and the details page use different uris,
you need to actively specify the uri of the list page, as follows:

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
    placeholder(ThumbnailMemoryCacheStateImage("https://www.sample.com/image.jpg?widht=300"))
    crossfade(fadeStart = false)
}
```

> The standard for thumbnails is images with the same aspect ratio and without any Transformation
> modification.

[StateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/stateimage/StateImage.kt

[ColorStateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/stateimage/ColorStateImage.kt

[DrawableStateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/stateimage/DrawableStateImage.kt

[ErrorStateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/stateimage/ErrorStateImage.kt

[CombinedStateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/stateimage/internal/CombinedStateImage.kt

[IconStateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/stateimage/IconStateImage.kt

[MemoryCacheStateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/stateimage/MemoryCacheStateImage.kt

[ThumbnailMemoryCacheStateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/stateimage/ThumbnailMemoryCacheStateImage.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.kt

[CurrentStateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/stateimage/CurrentStateImage.kt