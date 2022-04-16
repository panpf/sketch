# StateImage

StateImage 是一种状态图片，用来设置加载状态占位图和错误状态图，有以下几种实现：

* [ColorStateImage]：给定一个颜色值或颜色资源 Id 作为状态图片
* [DrawableStateImage]：给定一个 Drawable 或 Drawable 资源 Id 作为状态图片
* [ErrorStateImage]：专门用于错误状态，会根据错误类型选择不同的状态图
* [IconStateImage]：给定一个小的图标 Drawable 或 Drawable 资源 ID 和背景色作为状态图，无论目标 View 的大小多大，图标始终居中且大小不变
* [MemoryCacheStateImage]：给定一个 bitmap 内存缓存的 key，将尝试使用此 key 从内存缓存中获取 bitmap 作为状态图片

### 配置

[ImageRequest] 和 [ImageOptions] 都提供了 placeholder() 和 error() 方法用于配置 [StateImage]，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    placeholder(resources.getDrawable(R.drawable.placeholder))
    placeholder(ColorStateImage(IntColor(Color.RED)))
    placeholder(DrawableStateImage(R.drawable.placeholder))
    placeholder(IconStateImage(R.drawable.placeholder_icon, IntColor(Color.GRAY)))

    // error 内部用 ErrorStateImage 实现，因此多了一个可以配置具体错误情况的 lambda 函数
    // 并且 placeholder() 方法能用的 error() 也都能用故不再赘述
    error(R.drawable.error) {
        uriEmptyError(DrawableStateImage(R.drawable.uri_empty))
    }
}
```

### 自定义

可参考现有 [StateImage] 的实现

[StateImage]: ../../sketch/src/main/java/com/github/panpf/sketch/stateimage/StateImage.kt

[ColorStateImage]: ../../sketch/src/main/java/com/github/panpf/sketch/stateimage/ColorStateImage.kt

[DrawableStateImage]: ../../sketch/src/main/java/com/github/panpf/sketch/stateimage/DrawableStateImage.kt

[ErrorStateImage]: ../../sketch/src/main/java/com/github/panpf/sketch/stateimage/ErrorStateImage.kt

[IconStateImage]: ../../sketch/src/main/java/com/github/panpf/sketch/stateimage/IconStateImage.kt

[MemoryCacheStateImage]: ../../sketch/src/main/java/com/github/panpf/sketch/stateimage/MemoryCacheStateImage.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageOptions.kt