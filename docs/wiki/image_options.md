# ImageOptions

[ImageOptions] 用来批量定义图片配置，支持 [ImageRequest] 全部图片相关属性

目前可以在两个地方使用 [ImageOptions]：

* View
    * [SketchImageView].displayImageOptions
    * [SketchZoomImageView].displayImageOptions
* [Sketch].globalImageOptions

最终在构建 [ImageRequest] 时将以 [ImageRequest] 优先、来自 View 的 [ImageOptions] 次之、globalImageOptions
最后的顺序构建属性配置

### 示例

View：

```kotlin
sketchImageView.displayImageOptions = ImageOptions {
    placeholer(R.drawable.placeholder)
    // more ...
}

// 在现有 ImageOptions 基础上更新
sketchImageView.updateDisplayImageOptions {
    error(R.drawable.error)
}
```

Global：

```kotlin
class MyApplication : Application(), SketchConfigurator {

    override fun createSketchConfig(): Builder.() -> Unit = {
        globalImageOptions(ImageOptions {
            placeholer(R.drawable.placeholder)
            error(R.drawable.error)
            // more ...
        })
    }
}
```

[Sketch]: ../../sketch/src/main/java/com/github/panpf/sketch/Sketch.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageOptions.kt

[SketchImageView]: ../../sketch-extensions/src/main/java/com/github/panpf/sketch/SketchImageView.kt

[SketchZoomImageView]: ../../sketch-zoom/src/main/java/com/github/panpf/sketch/zoom/SketchZoomImageView.kt