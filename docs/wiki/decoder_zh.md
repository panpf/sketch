# Decoder

翻译：[English](decoder.md)

[Decoder] 用于解码图片文件，支持的每一种图片类型都有对应的 [Decoder] 实现，如下表所示：

| Format   | Decoder                                    | Dependent modules         | Android    | iOS             | Desktop         | Web             |
|:---------|--------------------------------------------|---------------------------|------------|:----------------|:----------------|:----------------|
| jpeg     | [BitmapFactoryDecoder]                     | -                         | ✅          | ❌               | ❌               | ❌               |
| jpeg     | [SkiaDecoder]                              | -                         | ❌          | ✅               | ✅               | ✅               |
| png      | [BitmapFactoryDecoder]                     | -                         | ✅          | ❌               | ❌               | ❌               |
| png      | [SkiaDecoder]                              | -                         | ❌          | ✅               | ✅               | ✅               |
| webp     | [BitmapFactoryDecoder]                     | -                         | ✅          | ❌               | ❌               | ❌               |
| webp     | [SkiaDecoder]                              | -                         | ❌          | ✅               | ✅               | ✅               |
| bmp      | [BitmapFactoryDecoder]                     | -                         | ✅          | ❌               | ❌               | ❌               |
| bmp      | [SkiaDecoder]                              | -                         | ❌          | ✅               | ✅               | ✅               |
| heif     | [BitmapFactoryDecoder]                     | -                         | ✅ (API 28) | ❌               | ❌               | ❌               |
| avif     | [BitmapFactoryDecoder]                     | -                         | ✅ (API 31) | ❌               | ❌               | ❌               |
| gif      | [ImageDecoderGifDecoder]                   | sketch-animated-gif       | ✅ (API 28) | ❌               | ❌               | ❌               |
| gif      | [MovieGifDecoder]<br/>(不支持 resize)         | sketch-animated-gif       | ✅          | ❌               | ❌               | ❌               |
| gif      | [SkiaGifDecoder]<br/>(不支持 resize)          | sketch-animated-gif       | ❌          | ✅               | ✅               | ✅               |
| gif      | [KoralGifDecoder]                          | sketch-animated-gif-koral | ✅          | ❌               | ❌               | ❌               |
| webp 动图  | [ImageDecoderAnimatedWebpDecoder]          | sketch-animated-webp      | ✅ (API 28) | ❌               | ❌               | ❌               |
| webp 动图  | [SkiaAnimatedWebpDecoder]<br/>(不支持 resize) | sketch-animated-webp      | ❌          | ✅               | ✅               | ✅               |
| heif 动图  | [ImageDecoderAnimatedHeifDecoder]          | sketch-animated-heif      | ✅ (API 30) | ❌               | ❌               | ❌               |
| svg      | [SvgDecoder]                               | sketch-svg                | ✅          | ✅<br/>(不支持 CSS) | ✅<br/>(不支持 CSS) | ✅<br/>(不支持 CSS) |
| 视频帧      | [VideoFrameDecoder]                        | sketch-video              | ✅          | ❌               | ❌               | ❌               |
| 视频帧      | [FFmpegVideoFrameDecoder]                  | sketch-video-ffmpeg       | ✅          | ❌               | ❌               | ❌               |
| Apk Icon | [ApkIconDecoder]                           | sketch-extensions-core    | ✅          | ❌               | ❌               | ❌               |

* [ApkIconDecoder] 在 Android 平台上解码 Apk
  文件的图标（[了解更多](apk_app_icon_zh.md#加载-apk-图标)）
* [BitmapFactoryDecoder] 在 Android 平台上使用 Android 内置的 [BitmapFactory] 解码图片，它是最后的解码器
* [DrawableDecoder] 在 Android 平台上解码 vector、shape 等 Android 支持的 xml drawable 图片
* [ImageDecoderGifDecoder] 在 Android 平台上使用 Android 内置的 [ImageDecoder] 解码 gif
  动图（[了解更多](animated_image_zh.md)）
* [KoralGifDecoder] 在 Android 平台上使用 koral-- 的 [android-gif-drawable][android-gif-drawable]
  库解码 gif 动图（[了解更多](animated_image_zh.md)）
* [MovieGifDecoder] 在 Android 平台上使用 Android 内置的 [Movie] 解码 gif
  动图（[了解更多](animated_image_zh.md)）
* [SkiaGifDecoder] 在非 Android 平台上使用 Skia 内置的 Codec 解码 gif
  动图（[了解更多](animated_image_zh.md)）
* [ImageDecoderAnimatedHeifDecoder] 使用 Android 内置的 [ImageDecoder] 解码 heif
  动图（[了解更多](animated_image_zh.md)）
* [SkiaDecoder] 在非 Android 平台上使用 Skia 内置的 Image 解码图片，它是最后的解码器
* [SvgDecoder] 在 Android 平台上使用 BigBadaboom 的 [androidsvg] 库，在非 Android 平台上使用 Skia 内置的
  SVGDOM 解码静态 svg 文件（[了解更多](svg_zh.md)）
* [ImageDecoderAnimatedWebpDecoder] 在 Android 平台上使用 Android 内置的 [ImageDecoder] 解码 webp
  动图（[了解更多](animated_image_zh.md)）
* [SkiaAnimatedWebpDecoder] 在非 Android 平台上使用 Skia 内置的 Codec 解码 webp
  动图（[了解更多](animated_image_zh.md)）
* [VideoFrameDecoder] 在 Android 平台上使用 Android 内置的 [MediaMetadataRetriever]
  类解码视频文件的帧（[了解更多](video_frame_zh.md)）
* [FFmpegVideoFrameDecoder] 在 Android 平台上使用 wseemann 的 [FFmpegMediaMetadataRetriever]
  库解码视频帧（[了解更多](video_frame_zh.md)）

> [!IMPORTANT]
> 上述组件都支持自动注册，你只需要导入即可，无需额外配置，如果你需要手动注册，
> 请阅读文档：[《注册组件》](register_component_zh.md)

### 扩展 Decoder

先实现 [Decoder] 接口定义你的 Decoder 和它的 Factory

然后参考文档 [《注册组件》](register_component_zh.md) 注册你的 Decoder 即可

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

[Decoder]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[Image]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Image.kt

[FetchResult]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/FetchResult.kt

[BitmapFactoryDecoder]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/decode/internal/BitmapFactoryDecoder.kt

[FFmpegVideoFrameDecoder]: ../../sketch-video-ffmpeg/src/main/kotlin/com/github/panpf/sketch/decode/FFmpegVideoFrameDecoder.kt

[ApkIconDecoder]: ../../sketch-extensions-apkicon/src/main/kotlin/com/github/panpf/sketch/decode/ApkIconDecoder.kt

[VideoFrameDecoder]: ../../sketch-video/src/main/kotlin/com/github/panpf/sketch/decode/VideoFrameDecoder.kt

[SvgDecoder]: ../../sketch-svg/src/commonMain/kotlin/com/github/panpf/sketch/decode/SvgDecoder.kt

[DrawableDecoder]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/decode/internal/DrawableDecoder.kt

[ImageDecoderGifDecoder]: ../../sketch-animated-gif/src/androidMain/kotlin/com/github/panpf/sketch/decode/ImageDecoderGifDecoder.kt

[ImageDecoderAnimatedHeifDecoder]: ../../sketch-animated-heif/src/main/kotlin/com/github/panpf/sketch/decode/ImageDecoderAnimatedHeifDecoder.kt

[ImageDecoderAnimatedWebpDecoder]: ../../sketch-animated-webp/src/androidMain/kotlin/com/github/panpf/sketch/decode/ImageDecoderAnimatedWebpDecoder.kt

[KoralGifDecoder]: ../../sketch-animated-gif-koral/src/main/kotlin/com/github/panpf/sketch/decode/KoralGifDecoder.kt

[MovieGifDecoder]: ../../sketch-animated-gif/src/androidMain/kotlin/com/github/panpf/sketch/decode/MovieGifDecoder.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever

[androidsvg]: https://github.com/BigBadaboom/androidsvg

[android-gif-drawable]: https://github.com/koral--/android-gif-drawable

[Movie]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/kotlin/android/graphics/Movie.java

[ImageDecoder]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/kotlin/android/graphics/ImageDecoder.java

[BitmapFactory]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/kotlin/android/graphics/BitmapFactory.java

[MediaMetadataRetriever]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/media/kotlin/android/media/MediaMetadataRetriever.java

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[SkiaDecoder]: ../../sketch-core/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/SkiaDecoder.kt

[SkiaGifDecoder]: ../../sketch-animated-gif/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/SkiaGifDecoder.kt

[SkiaAnimatedWebpDecoder]: ../../sketch-animated-webp/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/SkiaAnimatedWebpDecoder.kt