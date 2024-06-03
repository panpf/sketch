# Transformation

翻译：[English](transformation.md)

通过 [Transformation] 可以对解码得到的 Bitmap 进行转换，Sketch 内置了以下几种 [Transformation]

* [CircleCropTransformation]：将图片裁剪成圆形的
* [RotateTransformation]：将图片旋转指定角度
* [RoundedCornersTransformation]：将图片裁剪成圆角矩形的
* [MaskTransformation]：将图片盖上一层颜色遮罩，常用于将图片作为窗口或模块的背景时将图片变暗
* [BlurTransformation]：将图片进行模糊处理

> 注意：
> 1. [Transformation] 不支持动图，动图请使用 animatedTransformation() 函数
> 2. 在使用 RoundedCornersTransformation 时请搭配 'resizePrecision(Precision.EXACTLY)' 使用，因为如果原图尺寸和
     resize 尺寸不一致时，最终圆角在显示时会被缩放，导致圆角大小和期待的不一致

### 配置

[ImageRequest] 和 [ImageOptions] 都提供了 transformations 方法用于配置 [Transformation]

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
    transformations(CircleCropTransformation(), RoundedCornersTransformation(20f))
}
```

### 自定义

自定义 [Transformation] 时需要注意 key 属性的实现

因为 key 属性用于在构建 [Transformation] 列表时去除重复的 [Transformation]，所以要保证 key
属性的唯一性和确定性：

* 同一个 [Transformation] 输入相同的参数输出相同的 key
* 同一个 [Transformation] 输入不同的参数输出不同的 key

> 注意：自定义的 Transformation 不要对 transform 方法的 input Bitmap 执行 recycle()，这会造成不可预知的错误

[Transformation]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transform/Transformation.kt

[CircleCropTransformation]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transform/CircleCropTransformation.kt

[RotateTransformation]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transform/RotateTransformation.kt

[RoundedCornersTransformation]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transform/RoundedCornersTransformation.kt

[MaskTransformation]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transform/MaskTransformation.kt

[BlurTransformation]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transform/BlurTransformation.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.kt