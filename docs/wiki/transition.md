# Transition

[Transition] 用来配置图片显示时与旧图片的过渡方式，默认提供了 [CrossfadeTransition]

### 配置

[ImageRequest] 和 [ImageOptions] 都提供了 crossfade() 方法和 transition() 方法用于配置 [Transition]，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    crossfade()
    // 或
    transition(MyTransition.Factory())
}
```

### 自定义

参考 [CrossfadeTransition] 的实现即可

[Transition]: ../../sketch/src/main/java/com/github/panpf/sketch/transition/Transition.kt

[CrossfadeTransition]: ../../sketch/src/main/java/com/github/panpf/sketch/transition/CrossfadeTransition.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageOptions.kt