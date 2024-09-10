# Decoder

翻译：[English](decode.md)

[Decoder] 用于解码图片文件，支持的每一种图片类型都有对应的 [Decoder] 实现，如下表所示：

| Format   | Decoder                                    | Dependent modules        | Android    | iOS             | Desktop         | Web             |
|:---------|--------------------------------------------|--------------------------|------------|:----------------|:----------------|:----------------|
| jpeg     | [BitmapFactoryDecoder]                     | -                        | ✅          | ❌               | ❌               | ❌               |
| jpeg     | [SkiaDecoder]                              | -                        | ❌          | ✅               | ✅               | ✅               |
| png      | [BitmapFactoryDecoder]                     | -                        | ✅          | ❌               | ❌               | ❌               |
| png      | [SkiaDecoder]                              | -                        | ❌          | ✅               | ✅               | ✅               |
| webp     | [BitmapFactoryDecoder]                     | -                        | ✅          | ❌               | ❌               | ❌               |
| webp     | [SkiaDecoder]                              | -                        | ❌          | ✅               | ✅               | ✅               |
| bmp      | [BitmapFactoryDecoder]                     | -                        | ✅          | ❌               | ❌               | ❌               |
| bmp      | [SkiaDecoder]                              | -                        | ❌          | ✅               | ✅               | ✅               |
| heif     | [BitmapFactoryDecoder]                     | -                        | ✅ (API 28) | ❌               | ❌               | ❌               |
| gif      | [GifAnimatedDecoder]                       | sketch-animated          | ✅ (API 28) | ❌               | ❌               | ❌               |
| gif      | [GifDrawableDecoder]                       | sketch-animated-koralgif | ✅          | ❌               | ❌               | ❌               |
| gif      | [GifMovieDecoder]<br/>(不支持 resize)         | sketch-animated          | ✅          | ❌               | ❌               | ❌               |
| gif      | [GifSkiaAnimatedDecoder]<br/>(不支持 resize)  | sketch-animated          | ❌          | ✅               | ✅               | ✅               |
| webp 动图  | [WebpAnimatedDecoder]                      | sketch-animated          | ✅ (API 28) | ❌               | ❌               | ❌               |
| webp 动图  | [WebpSkiaAnimatedDecoder]<br/>(不支持 resize) | sketch-animated          | ❌          | ✅               | ✅               | ✅               |
| heif 动图  | [HeifAnimatedDecoder]                      | sketch-animated          | ✅ (API 30) | ❌               | ❌               | ❌               |
| svg      | [SvgDecoder]                               | sketch-svg               | ✅          | ✅<br/>(不支持 CSS) | ✅<br/>(不支持 CSS) | ✅<br/>(不支持 CSS) |
| 视频帧      | [VideoFrameDecoder]                        | sketch-video             | ✅          | ❌               | ❌               | ❌               |
| 视频帧      | [FFmpegVideoFrameDecoder]                  | sketch-video-ffmpeg      | ✅          | ❌               | ❌               | ❌               |
| Apk Icon | [ApkIconDecoder]                           | sketch-extensions-core   | ✅          | ❌               | ❌               | ❌               |

* [ApkIconDecoder] 在 Android 平台上解码 Apk
  文件的图标（[了解更多](apk_app_icon_zh.md#加载-apk-的图标)）
* [BitmapFactoryDecoder] 在 Android 平台上使用 Android 内置的 [BitmapFactory] 解码图片，它是最后的解码器
* [DrawableDecoder] 在 Android 平台上解码 vector、shape 等 Android 支持的 xml drawable 图片
* [GifAnimatedDecoder] 在 Android 平台上使用 Android 内置的 [ImageDecoder] 解码 gif
  动图（[了解更多](animated_image_zh.md)）
* [GifDrawableDecoder] 在 Android 平台上使用 koral-- 的 [android-gif-drawable][android-gif-drawable]
  库解码 gif 动图（[了解更多](animated_image_zh.md)）
* [GifMovieDecoder] 在 Android 平台上使用 Android 内置的 [Movie] 解码 gif
  动图（[了解更多](animated_image_zh.md)）
* [GifSkiaAnimatedDecoder] 在非 Android 平台上使用 Skia 内置的 Codec 解码 gif
  动图（[了解更多](animated_image_zh.md)）
* [HeifAnimatedDecoder] 使用 Android 内置的 [ImageDecoder] 解码 heif
  动图（[了解更多](animated_image_zh.md)）
* [SkiaDecoder] 在非 Android 平台上使用 Skia 内置的 Image 解码图片，它是最后的解码器
* [SvgDecoder] 在 Android 平台上使用 BigBadaboom 的 [androidsvg] 库，在非 Android 平台上使用 Skia 内置的
  SVGDOM 解码静态 svg 文件（[了解更多](svg_zh.md)）
* [WebpAnimatedDecoder] 在 Android 平台上使用 Android 内置的 [ImageDecoder] 解码 webp
  动图（[了解更多](animated_image_zh.md)）
* [WebpSkiaAnimatedDecoder] 在非 Android 平台上使用 Skia 内置的 Codec 解码 webp
  动图（[了解更多](animated_image_zh.md)）
* [VideoFrameDecoder] 在 Android 平台上使用 Android 内置的 [MediaMetadataRetriever]
  类解码视频文件的帧（[了解更多](video_frame_zh.md)）
* [FFmpegVideoFrameDecoder] 在 Android 平台上使用 wseemann 的 [FFmpegMediaMetadataRetriever]
  库解码视频帧（[了解更多](video_frame_zh.md)）

### 注册 Decoder

需要依赖单独模块的 [Decoder]（例如 [SvgDecoder]），需要在初始化 Sketch 时注册，如下：

```kotlin
// 在自定义 Sketch 时为所有 ImageRequest 注册
Sketch.Builder(context).apply {
    components {
        addDecoder(SvgDecoder.Factory())
    }
}.build()

// 加载图片时为单个 ImageRequest 注册
ImageRequest(context, "file:///android_asset/sample.mypng") {
    components {
        addDecoder(SvgDecoder.Factory())
    }
}
```

### 扩展 Decoder

先实现 [Decoder] 接口定义你的 Decoder 和它的 Factory，然后通过 addDecoder() 方法注册即可，如下：

```kotlin
class MyDecoder : Decoder {

    override suspend fun decode(): Result<BitmapDecodeResult> {
        // 在这里解码图片
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
            // 在这通过 mimeType 或 dataSource 判断当前图片是否是
            // MyDecoder 的目标类型，是的话返回一个新的 MyDecoder
            return if (fetchResult.mimeType == MY_MIME_TYPE) {
                MyDecoder()
            } else {
                null
            }
        }
    }
}

// 在自定义 Sketch 时为所有 ImageRequest 注册
Sketch.Builder(context).apply {
    components {
        addDecoder(MyDecoder.Factory())
    }
}.build()

// 加载图片时为单个 ImageRequest 注册
ImageRequest(context, "file:///android_asset/sample.mypng") {
    components {
        addDecoder(MyDecoder.Factory())
    }
}
```

> [!CAUTION]
> 1. 自定义 [Decoder] 需要应用 ImageRequest 中的很多与图片质量和尺寸相关的属性，例如
     bitmapConfig、size、colorSpace 等，可参考其它 [Decoder] 实现
> 2. 如果你的 [Decoder] 是解码动图的话一定要判断 [ImageRequest].disallowAnimatedImage 参数

## 解码拦截器

Sketch 的解码过程支持通过拦截器来改变解码前后的输入和输出

先实现 [DecodeInterceptor] 接口实现你的 DecodeInterceptor，然后通过 addDecodeInterceptor() 方法注册即可，如下：

```kotlin
class MyDecodeInterceptor : DecodeInterceptor {

    // 如果当前 DecodeInterceptor 会修改返回的结果并且仅用于部分请求，那么请给一个不重复的 key 用于构建缓存 key，否则给 null 即可
    override val key: String = "MyDecodeInterceptor"

    // 用于排序，值越大在列表中越靠后。取值范围是 0 ~ 100。通常是零。只有 EngineDecodeInterceptor 可以是 100
    override val sortWeight: Int = 0

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain,
    ): Result<DecodeResult> {
        val newRequest = chain.request.newRequest {
            bitmapConfig(Bitmap.Config.ARGB_4444)
        }
        return chain.proceed(newRequest)
    }
}

// 在自定义 Sketch 时为所有 ImageRequest 注册
Sketch.Builder(context).apply {
    components {
        addDecodeInterceptor(MyDecodeInterceptor())
    }
}.build()

// 加载图片时为单个 ImageRequest 注册
ImageRequest(context, "file:///sdcard/sample.mp4") {
    components {
        addDecodeInterceptor(MyDecodeInterceptor())
    }
}
```

> [!TIP]
> 1. MyDecodeInterceptor 演示了一个将所有请求的 Bitmap.Config 改为 ARGB_4444 的案例
> 2. 如果你想修改返回结果，就拦截 proceed 方法返回的结果，返回一个新的 [DecodeResult] 即可
> 3. 如果想不再执行请求只需不执行 proceed 方法即可

## 解码相关属性

* ImageRequest.bitmapConfig(BitmapConfig): 设置位图的颜色质量。全平台可用
* ImageRequest.colorSpace(ColorSpace): 设置位图的色彩空间。仅 Android 平台可用
* ImageRequest.preferQualityOverSpeed(Boolean): 设置质量优先解码模式。仅 Android 平台可用

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