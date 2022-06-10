# Transformation

通过 [Transformation] 可以对解码得到 Bitmap 进行转换，Sketch 内置了以下几种 [Transformation]

* [CircleCropTransformation]：圆形裁剪转换
* [RotateTransformation]：旋转转换
* [RoundedCornersTransformation]：圆角转换

> 注意：[Transformation] 不支持动图，动图请使用 animatedTransformation() 函数

### 配置

[ImageRequest] 和 [ImageOptions] 都提供了 transformations 方法用于配置 [Transformation]

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    transformations(CircleCropTransformation(), RoundedCornersTransformation(20f))
}
```

### 自定义

自定义 [Transformation] 时需要注意 key 属性的实现

因为 key 属性用于在构建 [Transformation] 列表时去除重复的 [Transformation]，所以要保证 key 属性的唯一性和确定性：

* 同一个 [Transformation] 输入相同的参数输出相同的 key
* 同一个 [Transformation] 输入不同的参数输出不同的 key

[Transformation]: ../../sketch/src/main/java/com/github/panpf/sketch/transform/Transformation.kt

[CircleCropTransformation]: ../../sketch/src/main/java/com/github/panpf/sketch/transform/CircleCropTransformation.kt

[RotateTransformation]: ../../sketch/src/main/java/com/github/panpf/sketch/transform/RotateTransformation.kt

[RoundedCornersTransformation]: ../../sketch/src/main/java/com/github/panpf/sketch/transform/RoundedCornersTransformation.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageOptions.kt