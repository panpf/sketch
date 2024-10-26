# Decoder

Translations: [简体中文](decoder_zh.md)

[Decoder] is used to decode image files. Each supported image type has a corresponding [Decoder]
implementation, as shown in the following table:

| Format        | Decoder                                            | Dependent modules         | Android    | iOS                     | Desktop                 | Web                     |
|:--------------|----------------------------------------------------|---------------------------|------------|:------------------------|:------------------------|:------------------------|
| jpeg          | [BitmapFactoryDecoder]                             | -                         | ✅          | ❌                       | ❌                       | ❌                       |
| jpeg          | [SkiaDecoder]                                      | -                         | ❌          | ✅                       | ✅                       | ✅                       |
| png           | [BitmapFactoryDecoder]                             | -                         | ✅          | ❌                       | ❌                       | ❌                       |
| png           | [SkiaDecoder]                                      | -                         | ❌          | ✅                       | ✅                       | ✅                       |
| webp          | [BitmapFactoryDecoder]                             | -                         | ✅          | ❌                       | ❌                       | ❌                       |
| webp          | [SkiaDecoder]                                      | -                         | ❌          | ✅                       | ✅                       | ✅                       |
| bmp           | [BitmapFactoryDecoder]                             | -                         | ✅          | ❌                       | ❌                       | ❌                       |
| bmp           | [SkiaDecoder]                                      | -                         | ❌          | ✅                       | ✅                       | ✅                       |
| heif          | [BitmapFactoryDecoder]                             | -                         | ✅ (API 28) | ❌                       | ❌                       | ❌                       |
| avif          | [BitmapFactoryDecoder]                             | -                         | ✅ (API 31) | ❌                       | ❌                       | ❌                       |
| gif           | [ImageDecoderGifDecoder]                           | sketch-animated-gif       | ✅ (API 28) | ❌                       | ❌                       | ❌                       |
| gif           | [MovieGifDecoder]<br/>(Not Support resize)         | sketch-animated-gif       | ✅          | ❌                       | ❌                       | ❌                       |
| gif           | [SkiaGifDecoder]<br/>(Not Support resize)          | sketch-animated-gif       | ✅          | ❌                       | ❌                       | ❌                       |
| gif           | [KoralGifDecoder]                                  | sketch-animated-gif-koral | ❌          | ✅                       | ✅                       | ✅                       |
| Animated webp | [ImageDecoderAnimatedWebpDecoder]                  | sketch-animated-webp      | ✅ (API 28) | ❌                       | ❌                       | ❌                       |
| Animated webp | [SkiaAnimatedWebpDecoder]<br/>(Not Support resize) | sketch-animated-webp      | ❌          | ✅                       | ✅                       | ✅                       |
| Animated heif | [ImageDecoderAnimatedHeifDecoder]                  | sketch-animated-heif      | ✅ (API 30) | ❌                       | ❌                       | ❌                       |
| svg           | [SvgDecoder]                                       | sketch-svg                | ✅          | ✅<br/>(Not Support CSS) | ✅<br/>(Not Support CSS) | ✅<br/>(Not Support CSS) |
| Video frames  | [VideoFrameDecoder]                                | sketch-video              | ✅          | ❌                       | ❌                       | ❌                       |
| Video frames  | [FFmpegVideoFrameDecoder]                          | sketch-video-ffmpeg       | ✅          | ❌                       | ❌                       | ❌                       |
| Apk Icon      | [ApkIconDecoder]                                   | sketch-extensions-core    | ✅          | ❌                       | ❌                       | ❌                       |

* [ApkIconDecoder] Decoding the icon of an Apk file on
  Android ([Learn more](apk_app_icon.md#load-apk-icon))
* [BitmapFactoryDecoder] Decode images on the Android platform using Android's
  built-in [BitmapFactory], which is the last resort decoder
* [DrawableDecoder] Decode vector, shape and other xml drawable images supported by Android on the
  Android platform
* [ImageDecoderGifDecoder] Use Android's built-in [ImageDecoder] to decode gif animations on the
  Android
  platform ([Learn more](animated_image.md))
* [KoralGifDecoder] Use koral--'s [android-gif-drawable][android-gif-drawable] library to decode
  animated gifs on the Android platform ([Learn more](animated_image.md))
* [MovieGifDecoder] Use Android's built-in [Movie] to decode gif animations on the Android
  platform ([Learn more](animated_image.md))
* [SkiaGifDecoder] Use Skia's built-in Codec to decode gif animations on non-Android
  platforms ([Learn more](animated_image.md))
* [ImageDecoderAnimatedHeifDecoder] Use Android's built-in [ImageDecoder] to decode heif
  animations ([Learn more](animated_image.md))
* [SkiaDecoder] Use Skia's built-in Image to decode images on non-Android platforms, which is the
  last decoder* [SvgDecoder] Use BigBadaboom's [androidsvg] library on Android platforms, and use
  Skia's built-in SVGDOM to decode static svg files on non-Android platforms ( [Learn more](svg.md))
* [ImageDecoderAnimatedWebpDecoder] Use Android's built-in [ImageDecoder] to decode webp animations
  on the
  Android platform ([Learn more](animated_image.md))
* [SkiaAnimatedWebpDecoder] Use Skia's built-in Codec to decode webp animations on non-Android
  platforms ([Learn more](animated_image.md))
* [VideoFrameDecoder] Decode frames of video files using Android's built-in [MediaMetadataRetriever]
  class on the Android platform ([Learn more](video_frame.md))
* [FFmpegVideoFrameDecoder] Decoding video frames using wseemann's [FFmpegMediaMetadataRetriever]
  library on Android ([Learn more](video_frame.md))

> [!IMPORTANT]
> The above components all support automatic registration. You only need to import them without
> additional configuration. If you need to register manually, please read the
> documentation: [《Register component》](register_component.md)

### Extend Decoder

First implement the [Decoder] interface to define your Decoder and its Factory

Then refer to the document [《Register component》](register_component.md) to register your Decoder.

> [!CAUTION]
> 1. Customizing [Decoder] requires applying many properties related to image quality and size in
     ImageRequest, such as size, colorType, colorSpace, etc. You can refer to other [Decoder]
     implementations
> 2. If your [Decoder] is decoding animated images, you must determine the [ImageRequest]
     .disallowAnimatedImage parameter.

## Decoding Properties

### BitmapColorType

BitmapColorType is used to set the color type of the bitmap. The optional values are:

* FixedColorType: always use the specified color type
* LowQualityColorType: Prioritize low-quality color types
    * jpeg images on the Android platform use RGB_565, and other values use the default value.
    * jpeg and webp images on non-Android platforms use RGB_565, others use ARGB_4444
* HighQualityColorType: Give priority to high-quality color types
    * On the Android platform, API 26 and above use RGBA_F16, and others use the default value.
    * Always use RGBA_F16 on non-Android platforms

Example:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    // Use specified color type on Android platform
    colorType(Bitmap.Config.RGB_565)

    // Use specified color type on non-Android platforms
    colorType(ColorType.RGBA_F16)

    // Prioritize lower quality color types
    colorType(LowQualityColorType)

    // Prioritize high-quality color types
    colorType(HighQualityColorType)
}
```

### BitmapColorSpace

BitmapColorSpace is used to set the color space of the bitmap. The optional values are:

* FixedColorSpace: Always use the specified color space

Example:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    // Use specified color space on Android platform
    colorSpace(ColorSpace.Named.DISPLAY_P3)

    // Use specified color space on non-Android platforms
    colorSpace(ColorSpace.displayP3)
}
```

### preferQualityOverSpeed

preferQualityOverSpeed is used to set quality priority when decoding. It can only be used on the
Android platform.

Example:

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