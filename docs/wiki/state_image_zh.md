# StateImage

翻译：[English](state_image.md)

[StateImage] 用来为加载中状态和错误状态提供图片，有以下几种实现：

For View:

* [DrawableStateImage]：使用 Drawable 作为状态图片
* [ColorDrawableStateImage]：使用颜色创建 ColorDrawable 作为状态图片
* [IconDrawableStateImage]：使用 [IconDrawable] 作为状态图片。可以确保图标大小始终不变，不受组件的缩放影响，适合用在瀑布流布局中
* [IconAnimatableDrawableStateImage]：使用 [IconAnimatableDrawable]
  作为状态图片。可以确保图标大小始终不变，不受组件的缩放影响，适合用在瀑布流布局中

For Compose:

* [PainterStateImage]：使用 Painter 作为状态图片
* [ColorPainterStateImage]：使用颜色创建 ColorPainter 作为状态图片
* [IconPainterStateImage]：使用 [IconPainter] 作为状态图片。可以确保图标大小始终不变，不受组件的缩放影响，适合用在瀑布流布局中
* [IconAnimatablePainterStateImage]：使用 [IconAnimatablePainter]
  作为状态图片。可以确保图标大小始终不变，不受组件的缩放影响，适合用在瀑布流布局中

Generic:

* [CurrentStateImage]：使用组件当前的 Image 作为状态图片
* [MemoryCacheStateImage]：使用给定的内存缓存 key 从内存缓存中获取 Image 作为状态图片，搭配 crossfade
  可以实现从小图到大图的完美过渡
* [ThumbnailMemoryCacheStateImage]：[MemoryCacheStateImage] 的简化版，使用给定的或当前请求的 uri
  匹配内存缓存中的宽高比和原图一致，并且没有用 Transformation 修改的缩略图作为状态图片。同样搭配
  crossfade 可以实现从小图到大图的完美过渡
* [ErrorStateImage]：专门用于错误状态，可以根据错误类型选择不同的状态图片

## 配置

[StateImage] 用在 [ImageRequest] 和 [ImageOptions] 的 placeholder(), fallback(), error() 方法，如下：

```kotlin
// View
ImageRequest(context, "https://example.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    placeholder(context.getEqualityDrawable(R.drawable.placeholder))
    placeholder(IntColorDrawableStateImage(Color.Gray))
    placeholder(DrawableStateImage(R.drawable.placeholder))
    placeholder(IconDrawableStateImage(R.drawable.placeholder, IntColor(Color.GRAY)))

    fallback(R.drawable.fallback)
    fallback(context.getEqualityDrawable(R.drawable.fallback))
    fallback(IntColorDrawableStateImage(Color.RED))
    fallback(DrawableStateImage(R.drawable.fallback))
    fallback(IconDrawableStateImage(R.drawable.fallback, IntColor(Color.RED)))

    error(R.drawable.error)
    error(R.drawable.error) {
        addState(MyCondition, R.drawable.mystate)
    }
    error(ErrorStateImage(R.drawable.error)) {
        addState(MyCondition, R.drawable.mystate)
    })
}

// Compose
ComposableImageRequest("https://example.com/image.jpg") {
    placeholder(Res.drawable.placeholder)
    placeholder(rememberPainterStateImage(Res.drawable.placeholder))
    placeholder(rememberColorPainterStateImage(Color.Gray))
    placeholder(rememberIconPainterStateImage(Res.drawable.placeholder, background = Color.Gray))

    fallback(Res.drawable.fallback)
    fallback(rememberPainterStateImage(Res.drawable.fallback))
    fallback(rememberColorPainterStateImage(Color.Red))
    fallback(rememberIconPainterStateImage(Res.drawable.fallback, background = Color.Red))
  
    error(Res.drawable.error)
    error(Res.drawable.error) {
        addState(MyCondition, Res.drawable.mystate)
    }
    error(ErrorStateImage(R.drawable.error)) {
        addState(MyCondition, Res.drawable.mystate)
    })
}
```

> [!TIP]
> 需要导入 `sketch-compose-resources` 模块 placeholder、fallback、error 才能支持 compose resources 的
> DrawableResource

### 自定义

可参考现有 [StateImage] 的实现

### ErrorStateImage

[ErrorStateImage] 支持根据不同的错误类型返回不同的状态图片，你可以实现 [ErrorStateImage].Condition
接口来扩展新的类型，然后通过 [ErrorStateImage].Builder.addState() 使用自定义的类型，如下：

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
        addState(MyCondition, DrawableStateImage(R.drawable.mystate))
    }
}
```

### Icon*StateImage

在瀑布流布局中由于每个 item 的大小可能不一样，所有 item 使用同一个 placeholder 时由于组件的缩放会导致在页面上看起来
placeholder 会有大有小

针对这样的情况使用 Icon\*StateImage 可以完美解决问题，Icon\*StateImage
由一个图标和一个背景组成，图标不受组件缩放影响图标始终保持固定大小不变，这样页面上看起来所有
placeholder 都是一样的大小

### ThumbnailMemoryCacheStateImage

从图片列表页面跳到图片详情页时我们希望能用列表页面已加载的缩略图片作为详情页加载大图时的占位图

这样在配合上 `crossfade(fadeStart = false)` 在大图加载完成时页面上看起来会从较模糊的图片逐渐变为一张清晰的图片，这样的效果会比较好

[ThumbnailMemoryCacheStateImage] 就可以帮助我们非常方便的从内存缓存中寻找的缩略图，如下：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    placeholder(ThumbnailMemoryCacheStateImage())
    crossfade(fadeStart = false)
}
```

[ThumbnailMemoryCacheStateImage] 默认会用当前 [ImageRequest] 的 uri 去内存中寻找缩略图，但如果列表页面和详情页面用的是不同的
uri 就需要主动指定列表页面的 uri，如下：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    placeholder(ThumbnailMemoryCacheStateImage("https://www.sample.com/image.jpg?widht=300"))
    crossfade(fadeStart = false)
}
```

> [!TIP]
> 缩略图的标准为宽高比一致并且没有用任何 Transformation 修改的图片

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

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt

[CurrentStateImage]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/state/CurrentStateImage.kt

[PainterStateImage]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/state/PainterStateImage.kt

[IconPainter]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/painter/IconPainter.common.kt

[IconAnimatablePainter]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/painter/IconAnimatablePainter.common.kt

[IconAnimatableDrawable]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/drawable/IconAnimatableDrawable.kt

[IconDrawable]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/drawable/IconDrawable.kt