# StateImage

StateImage 用来为加载中状态和错误状态提供图片，有以下几种实现：

* [CurrentStateImage]：使用 ImageView 当前的 drawable 作为状态图片
* [ColorStateImage]：使用给定的颜色值或颜色资源 Id 创建一个 ColorDrawable 作为状态图片
* [DrawableStateImage]：使用给定的 Drawable 或 Drawable 资源 Id 创建一个 Drawable 作为状态图片
* [ErrorStateImage]：专门用于错误状态，会根据错误类型选择不同的状态图片
* [IconStateImage]：使用给定的图标 Drawable 和背景 Drawable 创建一个状态图片，无论目标 Bounds 多大，图标始终居中且大小不变
* [MemoryCacheStateImage]：使用给定的内存缓存 key，尝试从内存缓存中获取 bitmap 作为状态图片
* [InexactlyMemoryCacheStateImage]：使用给定的或当前请求的 uri 匹配内存缓存中宽高比和原图一致，并且没有被 Transformation 修改的 bitmap
  作为状态图片

### 配置

[ImageRequest] 和 [ImageOptions] 都提供了 placeholder() 和 error() 方法，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    placeholder(resources.getDrawable(R.drawable.placeholder))
    placeholder(ColorStateImage(IntColor(Color.RED)))
    placeholder(DrawableStateImage(R.drawable.placeholder))
    placeholder(IconStateImage(R.drawable.placeholder_icon, IntColor(Color.GRAY)))

    // error 内部用 ErrorStateImage 实现，因此多了一个可以配置具体错误情况的 lambda 函数
    // 并且 placeholder() 方法能用的 error() 也都能用
    error(R.drawable.error) {
        uriEmptyError(DrawableStateImage(R.drawable.uri_empty))
    }
}
```

### 自定义

可参考现有 [StateImage] 的实现

### 扩展 ErrorStateImage

[ErrorStateImage] 支持根据不同的错误类型返回不同的状态图片

默认 Sketch 仅提供了 uriEmptyError 一种类型，您可以实现 [ErrorStateImage].Matcher 接口来扩展新的类型，然后通过
[ErrorStateImage].Builder.addMatcher() 使用自定义的类型，如下：

```kotlin
class MyMatcher(val stateImage: StateImage) : Matcher {

    override fun match(
        sketch: Sketch,
        request: ImageRequest,
        exception: SketchException?
    ): Boolean {
        // 根据 exception 判断错误类型
    }

    override fun getDrawable(
        sketch: Sketch,
        request: ImageRequest,
        exception: SketchException?
    ): Drawable = stateImage.getDrawable(sketch, request, exception)
}

imageView.displayImage("https://www.sample.com/image.jpg") {
    error(R.drawable.error) {
        addMatcher(MyMatcher(DrawableStateImage(R.drawable.uri_empty)))
    }
}
```

### 使用 IconStateImage 在瀑布流布局中实现完美占位图

在瀑布流布局中由于每个 item 的大小可能不一样，所有 item 使用同一个 placeholder 时由于 ImageView 的缩放导致在页面上看起来 placeholder 会有大有小

针对这样的情况使用 [IconStateImage] 可以完美解决问题，[IconStateImage] 由一个图标和一个背景组成，且没有固定的大小，不论 bounds
多大图标都会保持固定大小不变，这样页面上看起来所有 placeholder 都是一样的大小

### 使用 InexactlyMemoryCacheStateImage 在图片详情页面寻找内存缓存中的采样图作为占位图

从图片列表页面跳到图片详情页时我们希望能用列表页面已加载的采样图片作为详情页加载大图时的占位图

这样在配合上 `crossfade(fadeStart = false)` 在大图加载完成时页面上看起来会从较模糊的图片逐渐变为一张清晰的图片，这样的效果会比较好

[InexactlyMemoryCacheStateImage] 就可以帮助我们非常方便的从内存缓存中寻找的采样图，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    placeholder(InexactlyMemoryCacheStateImage())
    crossfade(fadeStart = false)
}
```

[InexactlyMemoryCacheStateImage] 默认会用当前 [ImageRequest] 的 uri 去内存中寻找采样图，但如果列表页面和详情页面用的是不同的 uri
就需要主动指定列表页面的 uri，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    placeholder(InexactlyMemoryCacheStateImage("https://www.sample.com/image.jpg?widht=300"))
    crossfade(fadeStart = false)
}
```

> 采样图的标准为宽高比一致并且没有被任何 Transformation 修改的图片

[StateImage]: ../../sketch/src/main/java/com/github/panpf/sketch/stateimage/StateImage.kt

[ColorStateImage]: ../../sketch/src/main/java/com/github/panpf/sketch/stateimage/ColorStateImage.kt

[DrawableStateImage]: ../../sketch/src/main/java/com/github/panpf/sketch/stateimage/DrawableStateImage.kt

[ErrorStateImage]: ../../sketch/src/main/java/com/github/panpf/sketch/stateimage/ErrorStateImage.kt

[IconStateImage]: ../../sketch/src/main/java/com/github/panpf/sketch/stateimage/IconStateImage.kt

[MemoryCacheStateImage]: ../../sketch/src/main/java/com/github/panpf/sketch/stateimage/MemoryCacheStateImage.kt

[InexactlyMemoryCacheStateImage]: ../../sketch/src/main/java/com/github/panpf/sketch/stateimage/InexactlyMemoryCacheStateImage.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageOptions.kt

[CurrentStateImage]: ../../sketch/src/main/java/com/github/panpf/sketch/stateimage/CurrentStateImage.kt