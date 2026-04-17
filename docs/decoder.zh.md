# Decoder

翻译：[English](decoder.md)

[Decoder] 用于从 [DataSource] 读取数据并解码图像，Sketch 支持的每一种图片类型都有对应的 [Decoder]
为提供支持，如下表所示：

| Decoder                           | Format                                             | Dependent modules         | Android | iOS | Desktop | Web |
|-----------------------------------|:---------------------------------------------------|---------------------------|---------|:----|:--------|:----|
| [SkiaDecoder]                     | jpeg, png, webp, bmp                               | -                         | ❌       | ✅   | ✅       | ✅   |
| [BitmapFactoryDecoder]            | jpeg, png, webp, bmp, heif (API 28), avif (API 31) | -                         | ✅       | ❌   | ❌       | ❌   |
| [PhotosAssetDecoder]              | jpeg, png, webp, bmp, heif                         | -                         | ❌       | ✅   | ❌       | ❌   |
| [SkiaGifDecoder]                  | gif (不支持 resize)                                   | sketch-animated-gif       | ❌       | ✅   | ✅       | ✅   |
| [MovieGifDecoder]                 | gif (不支持 resize)                                   | sketch-animated-gif       | ✅       | ❌   | ❌       | ❌   |
| [ImageDecoderGifDecoder]          | gif (API 28)                                       | sketch-animated-gif       | ✅       | ❌   | ❌       | ❌   |
| [KoralGifDecoder]                 | gif                                                | sketch-animated-gif-koral | ✅       | ❌   | ❌       | ❌   |
| [ImageDecoderAnimatedWebpDecoder] | webp 动图 (API 28)                                   | sketch-animated-webp      | ✅       | ❌   | ❌       | ❌   |
| [SkiaAnimatedWebpDecoder]         | webp 动图 (不支持 resize)                               | sketch-animated-webp      | ❌       | ✅   | ✅       | ✅   |
| [ImageDecoderAnimatedHeifDecoder] | heif 动图 (API 30)                                   | sketch-animated-heif      | ✅       | ❌   | ❌       | ❌   |
| [SvgDecoder]                      | svg (非 Android 不支持 CSS)                            | sketch-svg                | ✅       | ✅   | ✅       | ✅   |
| [VideoFrameDecoder]               | 视频帧                                                | sketch-video              | ✅       | ❌   | ❌       | ❌   |
| [FFmpegVideoFrameDecoder]         | 视频帧                                                | sketch-video-ffmpeg       | ✅       | ❌   | ❌       | ❌   |
| [PhotosAssetVideoFrameDecoder]    | 视频帧                                                | sketch-video              | ❌       | ✅   | ❌       | ❌   |
| [FileVideoFrameDecoder]           | 视频帧                                                | sketch-video              | ❌       | ✅   | ❌       | ❌   |
| [BlurHashDecoder]                 | BlurHash                                           | sketch-blurhash           | ✅       | ✅   | ✅       | ✅   |
| [ApkIconDecoder]                  | Apk Icon                                           | sketch-extensions-core    | ✅       | ❌   | ❌       | ❌   |
| [DrawableDecoder]                 | Andoid res drawable                                | -                         | ✅       | ❌   | ❌       | ❌   |

每种 [Decoder] 的用途如下：

* [SkiaDecoder]：在非 Android 平台上使用 Skia 内置的 Image 解码图片，它是非 Android 平台最后的解码器
* [BitmapFactoryDecoder]：在 Android 平台上使用 Android 内置的 [BitmapFactory] 解码图片，它是 Android
  平台最后的解码器
* [PhotosAssetDecoder]：在 iOS 平台上解码来自 Photos Library 的图片
* [SkiaGifDecoder]：在非 Android 平台上使用 Skia 内置的 Codec 解码 gif
  动图（[了解更多](animated_image.zh.md)）
* [MovieGifDecoder]：在 Android 平台上使用 Android 内置的 [Movie] 解码 gif
  动图（[了解更多](animated_image.zh.md)）
* [ImageDecoderGifDecoder]：在 Android 平台上使用 Android 内置的 [ImageDecoder] 解码 gif
  动图（[了解更多](animated_image.zh.md)）
* [KoralGifDecoder]：在 Android 平台上使用 koral-- 的 [android-gif-drawable][android-gif-drawable]
  库解码 gif 动图（[了解更多](animated_image.zh.md)）
* [ImageDecoderAnimatedWebpDecoder]：在 Android 平台上使用 Android 内置的 [ImageDecoder] 解码 webp
  动图（[了解更多](animated_image.zh.md)）
* [SkiaAnimatedWebpDecoder]：在非 Android 平台上使用 Skia 内置的 Codec 解码 webp
  动图（[了解更多](animated_image.zh.md)）
* [ImageDecoderAnimatedHeifDecoder]：使用 Android 内置的 [ImageDecoder] 解码 heif
  动图（[了解更多](animated_image.zh.md)）
* [SvgDecoder]：在 Android 平台上使用 BigBadaboom 的 [androidsvg] 库，在非 Android 平台上使用 Skia 内置的
  SVGDOM 解码静态 svg 文件（[了解更多](svg.zh.md)）
* [VideoFrameDecoder]：在 Android 平台上使用 Android 内置的 [MediaMetadataRetriever]
  类解码视频文件的帧（[了解更多](video_frame.zh.md)）
* [FFmpegVideoFrameDecoder]：在 Android 平台上使用 wseemann 的 [FFmpegMediaMetadataRetriever]
  库解码视频帧（[了解更多](video_frame.zh.md)）
* [PhotosAssetVideoFrameDecoder]：在 iOS 平台上解码来自 Photos Library 视频文件的帧
* [FileVideoFrameDecoder]：在 iOS 平台上解码来自本地视频文件的帧
* [BlurHashDecoder]：从 BlurHash 字符串解码模糊图像 （[了解更多](blurhash.zh.md)）
* [ApkIconDecoder]：在 Android 平台上解码 Apk
  文件的图标（[了解更多](apk_app_icon.zh.md#加载-apk-图标)）
* [DrawableDecoder]：在 Android 平台上解码 vector、shape 等 Android 支持的 xml drawable 图片

> [!IMPORTANT]
> * 内置的不依赖额外模块的 Decoder 都已经注册了
> * 依赖额外模块的 Decoder 也都支持自动注册，你只需要配置好依赖即可
> * 如果你需要手动注册，请阅读文档：[《注册组件》](register_component.zh.md)

### 解码静态图片

#### Android 平台

在 Android 平台上主要使用 [BitmapFactoryDecoder] 解码静态图片，[BitmapFactoryDecoder] 使用 Android
内置的 BitmapFactory 解码图片，支持的图片格式和最低版本请参考上表。

#### 桌面和 Web 平台

在桌面和 Web 平台上主要使用 [SkiaDecoder] 解码静态图片，[SkiaDecoder] 使用 Skia 内置的 Image
解码图片，支持的图片格式请参考上表。

#### iOS 平台

ios 平台上同样主要使用 [SkiaDecoder] 解码静态图片，但对于来自 Photos Library
的图片会优先使用 [PhotosAssetDecoder] 解码，[PhotosAssetDecoder] 使用 ios 原生的 PHImageManager
解码图片，这样能够利用系统自身的能力支持更多的图片格式。

如果你想使用 [SkiaDecoder] 解码来自 Photos Library 的图片可以通过 ` useSkiaForImagePhotosAsset()`
函数实现，如下：

```kotlin
val imageUri = newPhotosAssetUri("DB16113B-984A-4D12-B4D0-50FC46066781/L0/001")
val request = ImageRequest(context, imageUri) {
    useSkiaForImagePhotosAsset(true)
}
AsyncImage(
    request = request,
    contentDescription = "photo"
)
```

[UseSkiaInterceptor] 在检测到 useSkiaForImagePhotosAsset 为 true 后就会在 fetch 之后将图片原始数据全部读到内存中再包装成
ByteArrayDataSource 供 [SkiaDecoder] 解码

如果你还想将来自 Photos Library 的图片原始数据缓存到下载缓存中再包装成 FileDataSource
供 [SkiaDecoder] 解码，可以通过 `preferredFileCacheForImagePhotosAsset()` 函数实现，如下：

```kotlin
val imageUri = newPhotosAssetUri("DB16113B-984A-4D12-B4D0-50FC46066781/L0/001")
val request = ImageRequest(context, imageUri) {
    useSkiaForImagePhotosAsset(true)
    preferredFileCacheForImagePhotosAsset(true)
}
AsyncImage(
    request = request,
    contentDescription = "photo"
)
```

### 解码动图

解码动图请参考文档：[《动图》](animated_image.zh.md)

### 解码 SVG

解码 SVG 请参考文档：[《SVG》](svg.zh.md)

### 解码视频帧

解码视频帧请参考文档：[《视频帧》](video_frame.zh.md)

### 解码 BlurHash

解码 BlurHash 请参考文档：[《BlurHash》](blurhash.zh.md)

### 解码 APK 图标

解码 APK 图标请参考文档：[《加载 Apk 图标》](apk_app_icon.zh.md#加载-Apk-图标)

### 解码 Android Drawable

在 Android 平台主要使用 [DrawableDecoder] 解码 vector、shape 等 Android 支持的 xml drawable
图片，它的原理就是将 Drawable 绘制到 Bitmap 上，因此要求 Drawable 必须有固有尺寸。

### 扩展 Decoder

先实现 [Decoder] 接口定义你的 Decoder 和它的 Factory

然后参考文档 [《注册组件》](register_component.zh.md) 注册你的 Decoder 即可

> [!CAUTION]
> 1. 自定义 [Decoder] 需要应用 ImageRequest 中的很多与图片质量和尺寸相关的属性，例如
     size、colorType、colorSpace 等，可参考其它 [Decoder] 实现
> 2. 如果你的 [Decoder] 是解码动图的话一定要判断 [ImageRequest].disallowAnimatedImage 参数

## 解码属性

### BitmapColorType

BitmapColorType 用于设置位图的颜色类型，可选值有：

* FixedColorType：始终使用指定的颜色类型
* LowQualityColorType：优先使用低质量的颜色类型
  * Android 平台上 jpeg 图片使用 RGB_565，其它使用默认值
  * 非 Android 平台上 jpeg 和 webp 图片使用 RGB_565，其它使用 ARGB_4444
* HighQualityColorType：优先使用高质量的颜色类型
  * Android 平台上 API 26 以上使用 RGBA_F16，其它使用默认值
  * 非 Android 平台上始终使用 RGBA_F16

示例：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    // 在 Android 平台上使用指定的颜色类型
    colorType(Bitmap.Config.RGB_565)

    // 在非 Android 平台上使用指定的颜色类型
    colorType(ColorType.RGBA_F16)

    // 优先使用低质量的颜色类型
    colorType(LowQualityColorType)

    // 优先使用高质量的颜色类型
    colorType(HighQualityColorType)
}
```

### BitmapColorSpace

BitmapColorSpace 用于设置位图的颜色空间，可选值有：

* FixedColorSpace：始终使用指定的颜色空间

示例：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    // 在 Android 平台上使用指定的颜色空间
    colorSpace(ColorSpace.Named.DISPLAY_P3)

    // 在非 Android 平台上使用指定的颜色空间
    colorSpace(ColorSpace.displayP3)
}
```

### preferQualityOverSpeed

preferQualityOverSpeed 用于设置质量优先解码时质量优先，只能在 Android 平台使用。

示例：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    preferQualityOverSpeed(true)
}
```

[comment]: <> (classs)

[Decoder]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[Image]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Image.kt

[FetchResult]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/FetchResult.kt

[BitmapFactoryDecoder]: ../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/decode/internal/BitmapFactoryDecoder.kt

[FFmpegVideoFrameDecoder]: ../sketch-video-ffmpeg/src/main/kotlin/com/github/panpf/sketch/decode/FFmpegVideoFrameDecoder.kt

[ApkIconDecoder]: ../sketch-extensions-apkicon/src/main/kotlin/com/github/panpf/sketch/decode/ApkIconDecoder.kt

[VideoFrameDecoder]: ../sketch-video/src/androidMain/kotlin/com/github/panpf/sketch/decode/VideoFrameDecoder.kt

[SvgDecoder]: ../sketch-svg/src/commonMain/kotlin/com/github/panpf/sketch/decode/SvgDecoder.kt

[DrawableDecoder]: ../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/decode/internal/DrawableDecoder.kt

[ImageDecoderGifDecoder]: ../sketch-animated-gif/src/androidMain/kotlin/com/github/panpf/sketch/decode/ImageDecoderGifDecoder.kt

[ImageDecoderAnimatedHeifDecoder]: ../sketch-animated-heif/src/main/kotlin/com/github/panpf/sketch/decode/ImageDecoderAnimatedHeifDecoder.kt

[ImageDecoderAnimatedWebpDecoder]: ../sketch-animated-webp/src/androidMain/kotlin/com/github/panpf/sketch/decode/ImageDecoderAnimatedWebpDecoder.kt

[KoralGifDecoder]: ../sketch-animated-gif-koral/src/main/kotlin/com/github/panpf/sketch/decode/KoralGifDecoder.kt

[MovieGifDecoder]: ../sketch-animated-gif/src/androidMain/kotlin/com/github/panpf/sketch/decode/MovieGifDecoder.kt

[ImageRequest]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever

[androidsvg]: https://github.com/BigBadaboom/androidsvg

[android-gif-drawable]: https://github.com/koral--/android-gif-drawable

[Movie]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/kotlin/android/graphics/Movie.java

[ImageDecoder]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/kotlin/android/graphics/ImageDecoder.java

[BitmapFactory]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/kotlin/android/graphics/BitmapFactory.java

[MediaMetadataRetriever]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/media/kotlin/android/media/MediaMetadataRetriever.java

[ImageRequest]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[SkiaDecoder]: ../sketch-core/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/SkiaDecoder.kt

[SkiaGifDecoder]: ../sketch-animated-gif/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/SkiaGifDecoder.kt

[SkiaAnimatedWebpDecoder]: ../sketch-animated-webp/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/SkiaAnimatedWebpDecoder.kt

[BlurHashDecoder]: ../sketch-blurhash/src/commonMain/kotlin/com/github/panpf/sketch/decode/BlurHashDecoder.kt

[DataSource]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/source/DataSource.kt

[PhotosAssetFetcher]: ../sketch-core/src/iosMain/kotlin/com/github/panpf/sketch/fetch/PhotosAssetFetcher.kt

[UseSkiaInterceptor]: ../sketch-core/src/iosMain/kotlin/com/github/panpf/sketch/decode/internal/UseSkiaInterceptor.kt