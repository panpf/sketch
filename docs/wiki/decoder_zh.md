# Decoder

翻译：[English](decoder.md)

## 支持的图片类型

| 类型            | API 限制      | 额外依赖模块                                      |
|:--------------|:------------|:--------------------------------------------|
| jpeg          | _           | _                                           |
| png           | _           | _                                           |
| bmp           | _           | _                                           |
| webp          | _           | _                                           |
| svg           | _           | sketch-svg                                  |
| heif          | Android 9+  | _                                           |
| gif           | _           | sketch-animated<br>sketch-animated-koralgif |
| webp Animated | Android 9+  | _                                           |
| heif Animated | Android 11+ | _                                           |
| video frames  | _           | sketch-video<br>sketch-video-ffmpeg         |

每一种图片类型都有对应的 Decoder
对其提供支持，[查看更多 Decoder 介绍以及如何扩展新的图片类型][decoder]

[Decoder] 用于解码图像文件，它有以下实现：

* [BitmapFactoryDecoder][BitmapFactoryDecoder]：使用 Android 内置的 [BitmapFactory] 解码图像，它是最后的解码器
* [DrawableDecoder][DrawableDecoder]：解码 vector、shape 等 Android 支持的 xml drawable 图像
* [SvgDecoder][SvgDecoder]：使用 [BigBadaboom]/[androidsvg] 库解码静态 svg
  文件（[了解更多](svg_zh.md)）
* [ApkIconDecoder][ApkIconDecoder]：解码 Apk
  文件的图标（[了解更多](apk_app_icon_zh.md#显示-APK-文件的图标)）
* [GifAnimatedDecoder][GifAnimatedDecoder]：使用 Android 内置的 [ImageDecoder] 解码
  gif（[了解更多](animated_image_zh.md)）
* [GifDrawableDecoder][GifDrawableDecoder]：使用 [koral--]/[android-gif-drawable] 库的 [GifDrawable]
  解码 gif 图像（[了解更多](animated_image_zh.md)）
* [GifMovieDecoder][GifMovieDecoder]：使用 Android 内置的 [Movie] 解码 gif
  图像（[了解更多](animated_image_zh.md)）
* [HeifAnimatedDecoder][HeifAnimatedDecoder]：使用 Android 内置的 [ImageDecoder] 解码 heif
  动图（[了解更多](animated_image_zh.md)）
* [WebpAnimatedDecoder][WebpAnimatedDecoder]：使用 Android 内置的 [ImageDecoder] 解码 webp
  动图（[了解更多](animated_image_zh.md)）
* [VideoFrameDecoder][VideoFrameDecoder]：使用 Android 内置的 [MediaMetadataRetriever]
  类解码视频文件的帧（[了解更多](video_frame_zh.md)）
* [FFmpegVideoFrameDecoder][FFmpegVideoFrameDecoder]
  ：使用 [wseemann] /[FFmpegMediaMetadataRetriever-project] 库的 [FFmpegMediaMetadataRetriever]
  类解码视频文件的帧（[了解更多](video_frame_zh.md)）

需要解码时 Sketch 会遍历 [Decoder] 列表找到一个能解码当前图像的 [Decoder]，然后执行其 decode 方法得到解码结果

### 注册 Decoder

默认情况下 Sketch 只注册了 [DrawableDecoder] 和 [BitmapFactoryDecoder]，其它的 [Decoder]
则需要你根据需求手动注册，如下：

```kotlin
/* 为所有 ImageRequest 注册 */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addDecoder(MyDecoder.Factory())
            }
        }.build()
    }
}

/* 为单个 ImageRequest 注册 */
imageView.displayImage("asset://sample.mypng") {
    components {
        addDecoder(MyDecoder.Factory())
    }
}
```

### 扩展新的 Decoder

1.首先需要实现 [Decoder] 接口实现你的 [Decoder] 和它的 Factory，如下：

```kotlin
class MyDecoder : Decoder {

    override suspend fun decode(): Result<BitmapDecodeResult> {
        // 在这里解码图像
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
            // 在这通过 mimeType 或 dataSource 判断当前图像是否是
            // MyDecoder 的目标类型，是的话返回一个新的 MyDecoder
            return if (fetchResult.mimeType == MY_MIME_TYPE) {
                MyDecoder()
            } else {
                null
            }
        }
    }
}
```

2.然后参考 [注册 Decoder](#注册-decoder) 注册它

> Caution:
> 1. 自定义 [Decoder] 需要应用 ImageRequest 中的很多与图像质量和尺寸相关的属性，例如
     bitmapConfig、resize、colorSpace 等，可参考其它 [Decoder] 实现
> 2. 如果你的 [Decoder] 是解码动图的话一定要判断 [ImageRequest].disallowAnimatedImage 参数


[comment]: <> (class)

[Decoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[Image]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/Image.kt

[FetchResult]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/fetch/FetchResult.kt

[BitmapFactoryDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/internal/BitmapFactoryDecoder.kt

[DrawableDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/internal/DrawableDecoder.kt

[FFmpegVideoFrameDecoder]: ../../sketch-video-ffmpeg/src/main/kotlin/com/github/panpf/sketch/decode/FFmpegVideoFrameDecoder.kt

[ApkIconDecoder]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/decode/ApkIconDecoder.kt

[VideoFrameDecoder]: ../../sketch-video/src/main/kotlin/com/github/panpf/sketch/decode/VideoFrameDecoder.kt

[SvgDecoder]: ../../sketch-svg/src/main/kotlin/com/github/panpf/sketch/decode/SvgDecoder.kt

[DrawableDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/internal/DrawableDecoder.kt

[GifAnimatedDecoder]: ../../sketch-animated/src/main/kotlin/com/github/panpf/sketch/decode/GifAnimatedDecoder.kt

[HeifAnimatedDecoder]: ../../sketch-animated/src/main/kotlin/com/github/panpf/sketch/decode/HeifAnimatedDecoder.kt

[WebpAnimatedDecoder]: ../../sketch-animated/src/main/kotlin/com/github/panpf/sketch/decode/WebpAnimatedDecoder.kt

[GifDrawableDecoder]: ../../sketch-animated-koralgif/src/main/kotlin/com/github/panpf/sketch/decode/GifDrawableDecoder.kt

[GifMovieDecoder]: ../../sketch-animated/src/main/kotlin/com/github/panpf/sketch/decode/GifMovieDecoder.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[wseemann]: https://github.com/wseemann

[FFmpegMediaMetadataRetriever-project]: https://github.com/wseemann/FFmpegMediaMetadataRetriever

[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever/blob/master/core/src/main/kotlin/wseemann/media/FFmpegMediaMetadataRetriever.java

[BigBadaboom]: https://github.com/BigBadaboom

[androidsvg]: https://github.com/BigBadaboom/androidsvg

[koral--]: https://github.com/koral--

[android-gif-drawable]: https://github.com/koral--/android-gif-drawable

[GifDrawable]: https://github.com/koral--/android-gif-drawable/blob/dev/android-gif-drawable/src/main/kotlin/pl/droidsonroids/gif/GifDrawable.java

[Movie]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/kotlin/android/graphics/Movie.java

[ImageDecoder]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/kotlin/android/graphics/ImageDecoder.java

[BitmapFactory]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/kotlin/android/graphics/BitmapFactory.java

[MediaMetadataRetriever]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/media/kotlin/android/media/MediaMetadataRetriever.java