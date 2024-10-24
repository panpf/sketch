# Decoder

Translations: [简体中文](decoder_zh.md)

[Decoder] is used to decode image files. Each supported image type has a corresponding [Decoder]
implementation, as shown in the following table:

| Format        | Decoder                                            | Dependent modules        | Android    | iOS                     | Desktop                 | Web                     |
|:--------------|----------------------------------------------------|--------------------------|------------|:------------------------|:------------------------|:------------------------|
| jpeg          | [BitmapFactoryDecoder]                             | -                        | ✅          | ❌                       | ❌                       | ❌                       |
| jpeg          | [SkiaDecoder]                                      | -                        | ❌          | ✅                       | ✅                       | ✅                       |
| png           | [BitmapFactoryDecoder]                             | -                        | ✅          | ❌                       | ❌                       | ❌                       |
| png           | [SkiaDecoder]                                      | -                        | ❌          | ✅                       | ✅                       | ✅                       |
| webp          | [BitmapFactoryDecoder]                             | -                        | ✅          | ❌                       | ❌                       | ❌                       |
| webp          | [SkiaDecoder]                                      | -                        | ❌          | ✅                       | ✅                       | ✅                       |
| bmp           | [BitmapFactoryDecoder]                             | -                        | ✅          | ❌                       | ❌                       | ❌                       |
| bmp           | [SkiaDecoder]                                      | -                        | ❌          | ✅                       | ✅                       | ✅                       |
| heif          | [BitmapFactoryDecoder]                             | -                        | ✅ (API 28) | ❌                       | ❌                       | ❌                       |
| avif          | [BitmapFactoryDecoder]                             | -                        | ✅ (API 31) | ❌                       | ❌                       | ❌                       |
| gif           | [GifAnimatedDecoder]                               | sketch-animated          | ✅ (API 28) | ❌                       | ❌                       | ❌                       |
| gif           | [GifDrawableDecoder]                               | sketch-animated-koralgif | ✅          | ❌                       | ❌                       | ❌                       |
| gif           | [GifMovieDecoder]<br/>(Not Support resize)         | sketch-animated          | ✅          | ❌                       | ❌                       | ❌                       |
| gif           | [GifSkiaAnimatedDecoder]<br/>(Not Support resize)  | sketch-animated          | ❌          | ✅                       | ✅                       | ✅                       |
| Animated webp | [WebpAnimatedDecoder]                              | sketch-animated          | ✅ (API 28) | ❌                       | ❌                       | ❌                       |
| Animated webp | [WebpSkiaAnimatedDecoder]<br/>(Not Support resize) | sketch-animated          | ❌          | ✅                       | ✅                       | ✅                       |
| Animated heif | [HeifAnimatedDecoder]                              | sketch-animated          | ✅ (API 30) | ❌                       | ❌                       | ❌                       |
| svg           | [SvgDecoder]                                       | sketch-svg               | ✅          | ✅<br/>(Not Support CSS) | ✅<br/>(Not Support CSS) | ✅<br/>(Not Support CSS) |
| Video frames  | [VideoFrameDecoder]                                | sketch-video             | ✅          | ❌                       | ❌                       | ❌                       |
| Video frames  | [FFmpegVideoFrameDecoder]                          | sketch-video-ffmpeg      | ✅          | ❌                       | ❌                       | ❌                       |
| Apk Icon      | [ApkIconDecoder]                                   | sketch-extensions-core   | ✅          | ❌                       | ❌                       | ❌                       |

* [ApkIconDecoder] Decoding the icon of an Apk file on
  Android ([Learn more](apk_app_icon.md#load-apk-icon))
* [BitmapFactoryDecoder] Decode images on the Android platform using Android's
  built-in [BitmapFactory], which is the last resort decoder
* [DrawableDecoder] Decode vector, shape and other xml drawable images supported by Android on the
  Android platform
* [GifAnimatedDecoder] Use Android's built-in [ImageDecoder] to decode gif animations on the Android
  platform ([Learn more](animated_image.md))
* [GifDrawableDecoder] Use koral--'s [android-gif-drawable][android-gif-drawable] library to decode
  animated gifs on the Android platform ([Learn more](animated_image.md))
* [GifMovieDecoder] Use Android's built-in [Movie] to decode gif animations on the Android
  platform ([Learn more](animated_image.md))
* [GifSkiaAnimatedDecoder] Use Skia's built-in Codec to decode gif animations on non-Android
  platforms ([Learn more](animated_image.md))
* [HeifAnimatedDecoder] Use Android's built-in [ImageDecoder] to decode heif
  animations ([Learn more](animated_image.md))
* [SkiaDecoder] Use Skia's built-in Image to decode images on non-Android platforms, which is the
  last decoder* [SvgDecoder] Use BigBadaboom's [androidsvg] library on Android platforms, and use
  Skia's built-in SVGDOM to decode static svg files on non-Android platforms ( [Learn more](svg.md))
* [WebpAnimatedDecoder] Use Android's built-in [ImageDecoder] to decode webp animations on the
  Android platform ([Learn more](animated_image.md))
* [WebpSkiaAnimatedDecoder] Use Skia's built-in Codec to decode webp animations on non-Android
  platforms ([Learn more](animated_image.md))
* [VideoFrameDecoder] Decode frames of video files using Android's built-in [MediaMetadataRetriever]
  class on the Android platform ([Learn more](video_frame.md))
* [FFmpegVideoFrameDecoder] Decoding video frames using wseemann's [FFmpegMediaMetadataRetriever]
  library on Android ([Learn more](video_frame.md))

### Register Decoder

[Decoder] that needs to rely on a separate module (such as [SvgDecoder]) needs to be registered when
initializing Sketch, as follows:

```kotlin
// Register for all ImageRequests when customizing Sketch
Sketch.Builder(context).apply {
    components {
        addDecoder(SvgDecoder.Factory())
    }
}.build()

// Register for a single ImageRequest when loading an image
ImageRequest(context, "file:///android_asset/sample.mypng") {
    components {
        addDecoder(SvgDecoder.Factory())
    }
}
```

### Extend Decoder

First implement the [Decoder] interface to define your Decoder and its Factory, and then register it
through the addDecoder() method, as follows:

```kotlin
class MyDecoder : Decoder {

    override suspend fun decode(): Result<BitmapDecodeResult> {
        // Decode image here
    }

    companion object {
        const val MY_MIME_TYPE = "image/mypng"
    }

    class Factory : Decoder.Factory {

        override fun create(
            sketch: Sketch,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): Decoder? {
            val mimeType = fetchResult.mimeType
            val dataSource = fetchResult.dataSource
            // Here, judge whether the current image is the target type of MyDecoder 
            // through mimeType or dataSource. If so, return a new MyDecoder.
            return if (fetchResult.mimeType == MY_MIME_TYPE) {
                MyDecoder()
            } else {
                null
            }
        }
    }
}

// Register for all ImageRequests when customizing Sketch
Sketch.Builder(context).apply {
    components {
        addDecoder(MyDecoder.Factory())
    }
}.build()

// Register for a single ImageRequest when loading an image
ImageRequest(context, "file:///android_asset/sample.mypng") {
    components {
        addDecoder(MyDecoder.Factory())
    }
}
```

> [!CAUTION]
> 1. Customizing [Decoder] requires applying many properties related to image quality and size in
     ImageRequest, such as size, colorType, colorSpace, etc. You can refer to other [Decoder]
     implementations
> 2. If your [Decoder] is decoding animated images, you must determine the [ImageRequest]
     .disallowAnimatedImage parameter.

## Decode Interceptor

The decoding process of Sketch supports changing the input and output before and after decoding
through interceptors.

First implement the [DecodeInterceptor] interface to implement your DecodeInterceptor, and then
register it through the addDecodeInterceptor() method, as follows:

```kotlin
class MyDecodeInterceptor : DecodeInterceptor {

    // If the current DecodeInterceptor will modify the returned results and is only used for some requests, then please give a unique key to build the cache key, otherwise give null
    override val key: String = "MyDecodeInterceptor"

    // Used for sorting, the larger the value, the further back in the list. The value range is 0 ~ 100. Usually zero. Only EngineDecodeInterceptor can be 100
    override val sortWeight: Int = 0

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain,
    ): Result<DecodeResult> {
        val newRequest = chain.request.newRequest {
            colorType(Bitmap.Config.RGB_565)
        }
        return chain.proceed(newRequest)
    }
}

// Register for all ImageRequests when customizing Sketch
Sketch.Builder(context).apply {
    components {
        addDecodeInterceptor(MyDecodeInterceptor())
    }
}.build()

// Register for a single ImageRequest when loading an image
ImageRequest(context, "https://example.com/image.jpg") {
    components {
        addDecodeInterceptor(MyDecodeInterceptor())
    }
}
```

> [!TIP]
> 1. MyDecodeInterceptor demonstrates a case of changing the Bitmap.Config of all requests to
     RGB_565
> 2. If you want to modify the return result, just intercept the result returned by the proceed
     method and return a new [DecodeResult]
> 3. If you don’t want to execute the request anymore, just don’t execute the proceed method.

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

[ApkIconDecoder]: ../../sketch-extensions-core/src/androidMain/kotlin/com/github/panpf/sketch/decode/ApkIconDecoder.kt

[VideoFrameDecoder]: ../../sketch-video/src/main/kotlin/com/github/panpf/sketch/decode/VideoFrameDecoder.kt

[SvgDecoder]: ../../sketch-svg/src/commonMain/kotlin/com/github/panpf/sketch/decode/SvgDecoder.kt

[DrawableDecoder]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/decode/internal/DrawableDecoder.kt

[GifAnimatedDecoder]: ../../sketch-animated/src/androidMain/kotlin/com/github/panpf/sketch/decode/GifAnimatedDecoder.kt

[HeifAnimatedDecoder]: ../../sketch-animated/src/androidMain/kotlin/com/github/panpf/sketch/decode/HeifAnimatedDecoder.kt

[WebpAnimatedDecoder]: ../../sketch-animated/src/androidMain/kotlin/com/github/panpf/sketch/decode/WebpAnimatedDecoder.kt

[GifDrawableDecoder]: ../../sketch-animated-koralgif/src/main/kotlin/com/github/panpf/sketch/decode/GifDrawableDecoder.kt

[GifMovieDecoder]: ../../sketch-animated/src/androidMain/kotlin/com/github/panpf/sketch/decode/GifMovieDecoder.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever

[androidsvg]: https://github.com/BigBadaboom/androidsvg

[android-gif-drawable]: https://github.com/koral--/android-gif-drawable

[Movie]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/kotlin/android/graphics/Movie.java

[ImageDecoder]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/kotlin/android/graphics/ImageDecoder.java

[BitmapFactory]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/kotlin/android/graphics/BitmapFactory.java

[MediaMetadataRetriever]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/media/kotlin/android/media/MediaMetadataRetriever.java

[DecodeInterceptor]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/DecodeInterceptor.kt

[DecodeResult]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/DecodeResult.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[SkiaDecoder]: ../../sketch-core/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/SkiaDecoder.kt

[GifSkiaAnimatedDecoder]: ../../sketch-animated/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/GifSkiaAnimatedDecoder.kt

[WebpSkiaAnimatedDecoder]: ../../sketch-animated/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/WebpSkiaAnimatedDecoder.kt