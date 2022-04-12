# Target

[Target] 用来接收 [ImageRequest] 的结果 [ImageResult]，并将结果应用到目标上

从前面的 [入门][getting_started] 文档可以知道 [ImageRequest] 分为 [DisplayRequest]、[LoadRequest]、[DownloadRequest]
三种，他们又都有不同的 [ImageResult] 实现，因此 [Target] 也有对应的三种实现：

* [DisplayTarget]：接收 Drawable 类型的结果，[DisplayRequest] 专用
* [LoadTarget]：接收 Bitmap 类型的结果，[LoadRequest] 专用
* [DownloadTarget]：接收 [DownloadData] 类型的结果，[DownloadRequest] 专用

下面演示创建自定义 [DisplayTarget]:

```kotlin
val request = DisplayRequest(context, "https://www.example.com/image.jpg") {
    target(
        onStart = { placeholder: Drawable? ->
            // Handle the placeholder drawable. 
        },
        onSuccess = { result: Drawable ->
            // Handle the successful result Drawable. 
        },
        onError = { error: Drawable? ->
            // Handle the error drawable. 
        }
    )
}
sketch.enqueue(request)
```

> LoadTarget 和 DownloadTarget 同 DisplayTarget 使用方式大同小异

[DisplayTarget] 通常用来将 Drawable 应用到 View，因此 Sketch 提供了 [ViewTarget] 和 [ImageViewTarget] 来简化使用

[DisplayRequest] 还提供了 target(ImageView) 方法来简化绑定到 ImageView，如下：

```kotlin
val request = DisplayRequest(context, "https://www.example.com/image.jpg") {
    target(imageView)
}
sketch.enqueue(request)
```

[getting_started]: getting_started.md

[Target]: ../../sketch/src/main/java/com/github/panpf/sketch/target/Target.kt

[DisplayTarget]: ../../sketch/src/main/java/com/github/panpf/sketch/target/DisplayTarget.kt

[ViewTarget]: ../../sketch/src/main/java/com/github/panpf/sketch/target/ViewTarget.kt

[ImageViewTarget]: ../../sketch/src/main/java/com/github/panpf/sketch/target/ImageViewTarget.kt

[LoadTarget]: ../../sketch/src/main/java/com/github/panpf/sketch/target/LoadTarget.kt

[DownloadTarget]: ../../sketch/src/main/java/com/github/panpf/sketch/target/DownloadTarget.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageResult]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageResult.kt

[DisplayRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/DisplayRequest.kt

[LoadRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/LoadRequest.kt

[DownloadRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/DownloadRequest.kt

[DownloadData]: ../../sketch/src/main/java/com/github/panpf/sketch/request/DownloadData.kt