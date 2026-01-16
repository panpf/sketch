# Thumbnail

翻译：[English](thumbnail.md)

Sketch 通过 [ThumbnailInterceptor] 支持同时加载较低分辨率的缩略图和原图，如果缩略图先加载成功就显示缩略图，等原图加载成功后再切换为原图，从而提升用户体验，如下：

```kotlin
ImageRequest(context, "https://www.example.com/image.jpg") {
    thumbnail("https://www.example.com/image_thumbnail.jpg")
}
```

如果你想自定义配置缩略图请求还可以创建一个单独的缩略图 ImageRequest 传入：

```kotlin
ImageRequest(context, "https://www.example.com/image.jpg") {
    thumbnail(ImageRequest(context, "https://www.example.com/image_thumbnail.jpg") {
        // Configure thumbnail request here
    })
}
```

> [!IMPORTANT]
> * 缩略图请求不需要设置 target，会自动与主请求共享同一个 target，即使设置了也会被替换掉
> * 缩略图请求不会触发 placeholder、fallback、error
> * 缩略图请求会自动屏蔽掉来自 Target 的 cacheKey 以及 Listener 和 ProgressListener
> * 使用 uri 方式时会以主请求为基础创建缩略图请求，但会屏蔽掉来自主请求的 cacheKey 以及 Listener 和
    ProgressListener


[ThumbnailInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/internal/ThumbnailInterceptor.kt