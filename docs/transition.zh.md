# Transition

翻译：[English](transition.md)

[Transition] 用来配置新图片显示时与旧图片的过渡方式，默认提供了 [CrossfadeTransition] 支持淡入淡出的效果

### 配置

[ImageRequest] 和 [ImageOptions] 都提供了 crossfade() 方法和 transitionFactory() 方法用于配置 [Transition]
，如下：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    crossfade()
    // 或
    transitionFactory(CrossfadeTransition.Factory())
}
```

### 自定义

请参考 [CrossfadeTransition] 的实现

### 完美过渡

[CrossfadeTransition] 以 placeholder 图片和 result 图片的最大宽高作为新图片的宽高，然后对 placeholder
图片和 result 图片进行缩放

如果 result 图片和 placeholder 图片的尺寸不一致，例如 result 比 placeholder 大，placeholder 图片在过渡开始时就会被放大，虽然这个过程很快，但还是容易看出来的

解决这个问题的最好办法就是让 placeholder 图片和 result 图片的尺寸始终保持一致，借助 [ImageRequest]
和 [ImageOptions] 的 resizeOnDraw 属性就可以轻松实现这个效果

resizeOnDraw 属性将会用 [ResizePainter] 或 [ResizeDrawable] 包装 placeholder 、error、result 图片，用[ImageRequest].size 作为新的尺寸，再用 [ImageRequest].scale 属性对图片进行缩放。[了解 resizeOnDraw][resizeOnDraw]

因此通常建议 [CrossfadeTransition] 和 resizeOnDraw 搭配使用，如下：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    placeholder(R.drawable.im_placeholder)
    crossfade()
    resizeOnDraw()
}
```

[Transition]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transition/Transition.kt

[CrossfadeTransition]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transition/CrossfadeTransition.kt

[ImageRequest]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt

[ResizePainter]: ../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/painter/ResizePainter.kt

[ResizeDrawable]: ../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/drawable/ResizeDrawable.kt

[resizeOnDraw]: resize.zh.md#resizeOnDraw