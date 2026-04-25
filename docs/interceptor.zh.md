# Interceptor

翻译：[English](interceptor.md)

Sketch 通过 [Interceptor] 来处理 [ImageRequest] 的执行过程，你可以通过 [Interceptor]
改变执行过程中任一环节的输入和输出

Sketch 核心模块提供的 [Interceptor]：

* [MemoryCacheInterceptor]：提供内存缓存功能，避免重复解析、变换图片，提高性能
* [PlaceholderInterceptor]：提供占位图功能，在图片加载过程中显示占位图
* [ResultCacheInterceptor]：提供结果缓存功能，避免重复变换图片，提高性能
* [ThumbnailInterceptor]：提供缩略图功能，在加载原图的同时加载缩略图，缩略图先显示，原图加载完成后替换缩略图显示，提升用户体验
* [TransformationInterceptor]：提供变换功能，图片加载后对图片进行变换处理，如圆形、圆角、模糊等
* [FetcherInterceptor]：提供从 Uri 加载图片原始数据功能，从网络、文件、资源等来源加载图片原始数据供
  Decoder 解码
* [UseSkiaInterceptor]：将 iOS 平台来自 Photos Library 的 PhotosAssetDataSource 转换为可供 Skia 解码的
  ByteArrayDataSource
* [DecoderInterceptor]：提供解码功能，将图片原始数据解码为 Bitmap

sketch-extensions-core 模块提供的 [Interceptor]：

* [PauseLoadWhenScrollingInterceptor]：提供列表滑动时暂停加载图片，停止滑动时恢复加载图片的功能
* [SaveCellularTrafficInterceptor]：提供在非 Wi-Fi 网络环境下禁止从网络加载图片的功能

## 自定义 Interceptor

首先实现 [Interceptor] 接口定义你的 Interceptor，如下：

```kotlin
class MyInterceptor : Interceptor {

    // 如果当前 Interceptor 会修改返回的结果并且仅用于部分请求，那么请给一个不重复的 key 用于构建缓存 key，否则给 null 即可
    override val key: String? = null

    // 用于排序，值越大在列表中越靠后。取值范围是 0 ~ 100。通常是零。只有 DecoderInterceptor 可以是 100
    override val sortWeight: Int = 0

    override suspend fun intercept(chain: Chain): Result<ImageData> {
        // 所有请求禁止使用内存缓存
        val newRequest = chain.request.newRequest {
            memoryCachePolicy(CachePolicy.DISABLED)
        }
        return chain.proceed(newRequest)
    }
}
```

> [!TIP]
> 1. MyInterceptor 演示了一个禁止所有请求使用内存缓存的案例
> 2. 如果你想修改返回结果，就拦截 proceed 方法返回的结果，返回一个新的 [ImageData] 即可
> 3. 如果想不再执行请求只需不执行 proceed 方法即可

然后注册你的 Interceptor，如下：

```kotlin
// 在自定义 Sketch 时为所有 ImageRequest 注册
Sketch.Builder(context).apply {
    components {
        addInterceptor(MyInterceptor())
    }
}.build()

// 加载图片时为单个 ImageRequest 注册
ImageRequest(context, "https://example.com/image.jpg") {
    components {
        addInterceptor(MyInterceptor())
    }
}
```

[Interceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Interceptor.kt

[ImageRequest]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageResult]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageResult.kt

[ImageData]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageData.kt

[MemoryCacheInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/MemoryCacheInterceptor.kt

[PlaceholderInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/internal/PlaceholderInterceptor.kt

[ResultCacheInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/ResultCacheInterceptor.kt

[ThumbnailInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/internal/ThumbnailInterceptor.kt

[TransformationInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transform/internal/TransformationInterceptor.kt

[FetcherInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/internal/FetcherInterceptor.kt

[UseSkiaInterceptor]: ../sketch-core/src/iosMain/kotlin/com/github/panpf/sketch/decode/internal/UseSkiaInterceptor.kt

[DecoderInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/internal/DecoderInterceptor.kt

[PauseLoadWhenScrollingInterceptor]: ../sketch-extensions-core/src/commonMain/kotlin/com/github/panpf/sketch/request/PauseLoadWhenScrollingInterceptor.kt

[SaveCellularTrafficInterceptor]: ../sketch-extensions-core/src/commonMain/kotlin/com/github/panpf/sketch/request/SaveCellularTrafficInterceptor.kt