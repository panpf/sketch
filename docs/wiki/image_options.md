# ImageOptions

[ImageOptions] 用来批量定义图片配置，支持 [ImageRequest] 全部图片相关属性

目前可以在两个地方使用 [ImageOptions]：

* View
    * [SketchImageView].displayImageOptions
    * [SketchZoomImageView].displayImageOptions
* [ImageRequest].Builder.merge()/default()
* [Sketch].globalImageOptions

最终在构建 [ImageRequest] 时将以 [ImageRequest].Builder > View > [ImageRequest]
.Builder.defaultOptions > [Sketch].globalImageOptions 的顺序构建

### 示例

Global：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
          globalImageOptions(ImageOptions {
            placeholer(R.drawable.placeholder)
            error(R.drawable.error)
            // more ...
          })
        }.build()
    }
}
```

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

ImageRequest：

```kotlin
DisplayRequest(context, "http://sample.com/sample.jpeg") {
    merge(ImageOptions {
        // ...
    })
    default(ImageOptions {
        // ...
    })
}
```

[Sketch]: ../../sketch/src/main/java/com/github/panpf/sketch/Sketch.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageOptions.kt

[SketchImageView]: ../../sketch-extensions/src/main/java/com/github/panpf/sketch/SketchImageView.kt

[SketchZoomImageView]: ../../sketch-zoom/src/main/java/com/github/panpf/sketch/zoom/SketchZoomImageView.kt