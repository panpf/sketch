# Transition

[Transition] 用来配置图片显示时与旧图片的过渡方式，默认提供了 [CrossfadeTransition]：淡入淡出

### 配置

[ImageRequest] 和 [ImageOptions] 都提供了 crossfade() 方法和 transition() 方法用于配置 [Transition]，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    crossfade()
    // 或
    transition(CrossfadeTransition.Factory())
}
```

### 自定义

请参考 [CrossfadeTransition] 的实现

### 实现完美过渡

[CrossfadeTransition] 使用 [CrossfadeDrawable] 来实现过渡，[CrossfadeDrawable] 以 placeholder 图片和 result
图片的最大尺寸作为新 Drawable 的尺寸，然后对 placeholder 图片和 result 图片进行缩放

如果 result 图片的尺寸比 placeholder 图片大，placeholder 图片在作为过渡的 start 时就会被改变尺寸，在页面上的显示效果就是在过渡的开始时 placeholder
图片会快速的放大，虽然这个过程很快，但还是容易被看出来

解决这个问题的最好办法就是让 placeholder 图片和 result 图片的尺寸始终保持一致，借助 [ImageRequest] 和 [ImageOptions] 的
resizeApplyToDrawable 属性就可以轻松实现这一点，resizeApplyToDrawable 属性会用 Resize 作为 placeholder 、error、result
drawable 的尺寸。[查看更多 resizeApplyToDrawable 介绍][resize]

因此通常建议 [CrossfadeTransition] 和 resizeApplyToDrawable 搭配使用，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    placeholder(R.drawable.im_placeholder)
    crossfade()
    resizeApplyToDrawable()
}
```

> Android 自带的 TransitionDrawable 也是以 start 和 end 图片的最大尺寸作为新 Drawable 的尺寸

[Transition]: ../../sketch/src/main/java/com/github/panpf/sketch/transition/Transition.kt

[CrossfadeTransition]: ../../sketch/src/main/java/com/github/panpf/sketch/transition/CrossfadeTransition.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageOptions.kt

[CrossfadeDrawable]: ../../sketch/src/main/java/com/github/panpf/sketch/drawable/internal/CrossfadeDrawable.kt

[resize]: resize.md