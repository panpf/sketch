# Decoder

[//]: # (TODO)

翻译：[English](decode.md)

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
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(context).apply {
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




Sketch 的解码过程支持拦截器，你可以通过拦截器来改变解码前后的输入和输出

首先，实现 [DecodeInterceptor] 接口实现你的 [DecodeInterceptor]，如下：

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
```

> 1. MyDecodeInterceptor 演示了一个将所有请求的 Bitmap.Config 改为 ARGB_4444 的案例
> 2. 如果你想修改返回结果，就拦截 proceed 方法返回的结果，返回一个新的 [DecodeResult] 即可
> 3. 如果想不再执行请求只需不执行 proceed 方法即可

然后，通过 addDecodeInterceptor() 和 addDecodeInterceptor() 方法注册你的
DecodeInterceptor，如下：

```kotlin
/* 为所有 ImageRequest 注册 */
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(context).apply {
            components {
                addDecodeInterceptor(MyDecodeInterceptor())
            }
        }.build()
    }
}

/* 为单个 ImageRequest 注册 */
imageView.displayImage("file:///sdcard/sample.mp4") {
    components {
        addDecodeInterceptor(MyDecodeInterceptor())
    }
}
```


[comment]: <> (classs)

[Decoder]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[Image]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Image.kt

[FetchResult]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/FetchResult.kt

[BitmapFactoryDecoder]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/internal/BitmapFactoryDecoder.kt

[DrawableDecoder]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/internal/DrawableDecoder.kt

[FFmpegVideoFrameDecoder]: ../../sketch-video-ffmpeg/src/main/kotlin/com/github/panpf/sketch/decode/FFmpegVideoFrameDecoder.kt

[ApkIconDecoder]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/decode/ApkIconDecoder.kt

[VideoFrameDecoder]: ../../sketch-video/src/main/kotlin/com/github/panpf/sketch/decode/VideoFrameDecoder.kt

[SvgDecoder]: ../../sketch-svg/src/main/kotlin/com/github/panpf/sketch/decode/SvgDecoder.kt

[DrawableDecoder]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/internal/DrawableDecoder.kt

[GifAnimatedDecoder]: ../../sketch-animated/src/main/kotlin/com/github/panpf/sketch/decode/GifAnimatedDecoder.kt

[HeifAnimatedDecoder]: ../../sketch-animated/src/main/kotlin/com/github/panpf/sketch/decode/HeifAnimatedDecoder.kt

[WebpAnimatedDecoder]: ../../sketch-animated/src/main/kotlin/com/github/panpf/sketch/decode/WebpAnimatedDecoder.kt

[GifDrawableDecoder]: ../../sketch-animated-koralgif/src/main/kotlin/com/github/panpf/sketch/decode/GifDrawableDecoder.kt

[GifMovieDecoder]: ../../sketch-animated/src/main/kotlin/com/github/panpf/sketch/decode/GifMovieDecoder.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

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

[DecodeInterceptor]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/DecodeInterceptor.kt

[DecodeResult]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/DecodeResult.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt