# Decoder

Translations: [简体中文](decoder.zh.md)

[Decoder] is used to read data from [DataSource] and decode images, and each image type supported by
Sketch is supported by a corresponding [Decoder], as shown in the following table:

| Decoder                           | Format                                             | Dependent modules         | Android | iOS | Desktop | Web |
|-----------------------------------|:---------------------------------------------------|---------------------------|---------|:----|:--------|:----|
| [SkiaDecoder]                     | jpeg, png, webp, bmp                               | -                         | ❌       | ✅   | ✅       | ✅   |
| [BitmapFactoryDecoder]            | jpeg, png, webp, bmp, heif (API 28), avif (API 31) | -                         | ✅       | ❌   | ❌       | ❌   |
| [PhotosAssetDecoder]              | jpeg, png, webp, bmp, heif                         | -                         | ❌       | ✅   | ❌       | ❌   |
| [SkiaGifDecoder]                  | gif (Not Support resize)                           | sketch-animated-gif       | ❌       | ✅   | ✅       | ✅   |
| [MovieGifDecoder]                 | gif (Not Support resize)                           | sketch-animated-gif       | ✅       | ❌   | ❌       | ❌   |
| [ImageDecoderGifDecoder]          | gif (API 28)                                       | sketch-animated-gif       | ✅       | ❌   | ❌       | ❌   |
| [KoralGifDecoder]                 | gif                                                | sketch-animated-gif-koral | ✅       | ❌   | ❌       | ❌   |
| [ImageDecoderAnimatedWebpDecoder] | Animated webp (API 28)                             | sketch-animated-webp      | ✅       | ❌   | ❌       | ❌   |
| [SkiaAnimatedWebpDecoder]         | Animated webp (Not Support resize)                 | sketch-animated-webp      | ❌       | ✅   | ✅       | ✅   |
| [ImageDecoderAnimatedHeifDecoder] | Animated heif (API 30)                             | sketch-animated-heif      | ✅       | ❌   | ❌       | ❌   |
| [SvgDecoder]                      | svg (CSS is not supported on non-Android)          | sketch-svg                | ✅       | ✅   | ✅       | ✅   |
| [VideoFrameDecoder]               | Video frame                                        | sketch-video              | ✅       | ❌   | ❌       | ❌   |
| [FFmpegVideoFrameDecoder]         | Video frame                                        | sketch-video-ffmpeg       | ✅       | ❌   | ❌       | ❌   |
| [PhotosAssetVideoFrameDecoder]    | Video frame                                        | sketch-video              | ❌       | ✅   | ❌       | ❌   |
| [FileVideoFrameDecoder]           | Video frame                                        | sketch-video              | ❌       | ✅   | ❌       | ❌   |
| [BlurHashDecoder]                 | BlurHash                                           | sketch-blurhash           | ✅       | ✅   | ✅       | ✅   |
| [ApkIconDecoder]                  | Apk Icon                                           | sketch-extensions-core    | ✅       | ❌   | ❌       | ❌   |
| [DrawableDecoder]                 | Andoid res drawable                                | -                         | ✅       | ❌   | ❌       | ❌   |

The uses of each [Decoder] are as follows:

* [SkiaDecoder]: Use Skia's built-in Image Decoder on non-Android platforms; it's the final decoder
  for non-Android platforms.
* [BitmapFactoryDecoder]: On the Android platform, images are decoded using Android's built-in
  BitmapFactory, which is the final decoder on the Android platform.
* [PhotosAssetDecoder]: Decode images from the Photos Library on iOS platform
* [SkiaGifDecoder] Use Skia's built-in Codec to decode gif animations on non-Android
  platforms ([Learn more](animated_image.md))
* [MovieGifDecoder] Use Android's built-in [Movie] to decode gif animations on the Android
  platform ([Learn more](animated_image.md))
* [ImageDecoderGifDecoder] Use Android's built-in [ImageDecoder] to decode gif animations on the
  Android platform ([Learn more](animated_image.md))
* [KoralGifDecoder] Use koral--'s [android-gif-drawable][android-gif-drawable] library to decode
  animated gifs on the Android platform ([Learn more](animated_image.md))
* [ImageDecoderAnimatedWebpDecoder] Use Android's built-in [ImageDecoder] to decode webp animations
  on the Android platform ([Learn more](animated_image.md))
* [SkiaAnimatedWebpDecoder] Use Skia's built-in Codec to decode webp animations on non-Android
  platforms ([Learn more](animated_image.md))
* [ImageDecoderAnimatedHeifDecoder] Use Android's built-in [ImageDecoder] to decode heif
  animations ([Learn more](animated_image.md))
* [SvgDecoder] Use BigBadaboom's [androidsvg] library on Android platforms, and use
  Skia's built-in SVGDOM to decode static svg files on non-Android platforms ( [Learn more](svg.md))
* [VideoFrameDecoder] Decode frames of video files using Android's built-in [MediaMetadataRetriever]
  class on the Android platform ([Learn more](video_frame.md))
* [FFmpegVideoFrameDecoder] Decoding video frames using wseemann's [FFmpegMediaMetadataRetriever]
  library on Android ([Learn more](video_frame.md))
* [PhotosAssetVideoFrameDecoder]: Decoding frames from video files in the Photos Library on iOS
  platform
* [FileVideoFrameDecoder]: Decoding frames from local video files on the iOS platform
* [BlurHashDecoder] Decode blurred images from BlurHash string （[Learn more](blurhash.md)）
* [ApkIconDecoder] Decoding the icon of an Apk file on
  Android ([Learn more](apk_app_icon.md#load-apk-icon))
* [DrawableDecoder] Decode vector, shape and other xml drawable images supported by Android on the
  Android platform

> [!IMPORTANT]
> * The built-in Fetchers that do not rely on additional modules have been registered.
> * Fetchers that rely on additional modules also support automatic registration. You only need to
    configure the dependencies.
> * If you need to register manually, please read the
    documentation: [《Register component》](register_component.md)

### Decode static images

#### Android platform

On the Android platform, [BitmapFactoryDecoder] is mainly used to decode static images,
and [BitmapFactoryDecoder] is used to decode images using Android's built-in BitmapFactory.

#### Desktop and Web platforms

On desktop and web platforms, [SkiaDecoder] is mainly used to decode static images,
and [SkiaDecoder] uses Skia's built-in image to decode images.

#### iOS platform

The ios platform also mainly uses [SkiaDecoder] to decode static images, but for images from the
Photos Library, [PhotosAssetDecoder] will be used to decode the images, and [PhotosAssetDecoder]
will use the iOS native PHImageManager to decode the images, so that the system can use its own
capabilities to support more image formats.

If you want to use [SkiaDecoder] to decode images from the Photos Library, you can do so with the
`useSkiaForImagePhotosAsset()` function, as follows:

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

[UseSkiaInterceptor] will read all the original image data into memory after fetch after detecting
that useSkiaForImagePhotosAsset is true, and then wrap it into a ByteArrayDataSource
for [SkiaDecoder] to decode

If you also want to cache the raw data of the images from the Photos Library into the download cache
and wrap them into a FileDataSource for [SkiaDecoder] to decode, you can do so using the
`preferredFileCacheForImagePhotosAsset()` function, as follows:

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

### Decode animated images

Decode animated images, please refer to the documentation: [《Animated Image》](animated_image.md)

### Decode SVG

Decode SVG, refer to the documentation: [《SVG》](svg.md)

### Decode video frame

Decode video frame, refer to the documentation: [《Video Frame》](video_frame.md)

### Decode BlurHash

Decode BlurHash, refer to the documentation: [《BlurHash》](blurhash.md)

### Decode APK icon

Decode the APK icon, please refer to the
documentation: [《Load Apk Icon》](apk_app_icon.md#load-apk-icon)

### Decode Android Drawable

On the Android platform, [DrawableDecoder] is mainly used to decode xml drawable images supported by
Android, such as vector and shape, and its principle is to draw the Drawable onto the Bitmap, so the
Drawable must have an intrinsic dimension.

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