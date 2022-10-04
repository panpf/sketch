# Decoder

Decoder 用于解码图片文件得到一个 Bitmap 或 Drawable，因此 Sketch 有两种 Decoder：

* [BitmapDecoder]：用于解码图片文件并将其转成 Bitmap
    * [ApkIconBitmapDecoder][ApkIconBitmapDecoder]：解码 Apk
      文件的图标，[点我了解如何使用](apk_app_icon.md#显示-APK-文件的图标)
    * [AppIconBitmapDecoder][AppIconBitmapDecoder]：解码已安装 App
      的图标，[点我了解如何使用](apk_app_icon.md#显示已安装-APP-的图标)
    * [DefaultBitmapDecoder][DefaultBitmapDecoder]：最后的 Bitmap 解码器，采用 Android 内置的 [BitmapFactory]
      解码图片
    * [FFmpegVideoFrameBitmapDecoder][FFmpegVideoFrameBitmapDecoder]：使用 [wseemann]
      /[FFmpegMediaMetadataRetriever-project] 库的 [FFmpegMediaMetadataRetriever]
      类解码视频文件的帧，[点我了解如何使用](video_frame.md)
    * [SvgBitmapDecoder][SvgBitmapDecoder]：使用 [BigBadaboom]/[androidsvg] 库解码静态 svg
      文件，[点我了解如何使用](svg.md)
    * [VideoFrameBitmapDecoder][VideoFrameBitmapDecoder]：使用 Android 内置的 [MediaMetadataRetriever]
      类解码视频文件的帧，[点我了解如何使用](video_frame.md)
    * [XmlDrawableBitmapDecoder][XmlDrawableBitmapDecoder]：解码 vector、shape 等 Android 支持的 xml
      drawable 图片
* [DrawableDecoder]： 用于解码图片文件并将其转成 Drawable
    * [DefaultDrawableDecoder][DefaultDrawableDecoder]：最后的 Drawable 解码器，调用 BitmapDecoder 得到 Bitmap
      再封装成 BitmapDrawable
    * [GifAnimatedDrawableDecoder][GifAnimatedDrawableDecoder]：使用 Android 内置的 [ImageDecoder] 类解码 gif
      ，[点我了解如何使用](animated_image.md)
    * [GifDrawableDrawableDecoder][GifDrawableDrawableDecoder]：使用 [koral--]/[android-gif-drawable]
      库的 [GifDrawable] 类解码 gif 图片，[点我了解如何使用](animated_image.md)
    * [GifMovieDrawableDecoder][GifMovieDrawableDecoder]：使用 Android 内置的 [Movie] 类解码 gif
      图片，[点我了解如何使用](animated_image.md)
    * [HeifAnimatedDrawableDecoder][HeifAnimatedDrawableDecoder]：使用 Android 内置的 [ImageDecoder] 类解码
      heif 动图，[点我了解如何使用](animated_image.md)
    * [WebpAnimatedDrawableDecoder][WebpAnimatedDrawableDecoder]：使用 Android 内置的 [ImageDecoder] 类解码
      webp 动图，[点我了解如何使用](animated_image.md)

[BitmapDecoder] 和 [DrawableDecoder] 各有一个 Decoder 列表，需要解码时 Sketch 会根据 [ImageRequest] 的类型依次遍历对应的
Decoder 列表，直到找到一个能解码当前类型图片的 Decoder，然后调用其 decode 方法得到解码结果

### 扩展新的 Decoder

1.首先需要实现 [BitmapDecoder] 或 [DrawableDecoder] 接口定义你的 Decoder 和它的 Factory，下面以 [BitmapDecoder] 为例，如下：

```kotlin
class MyBitmapDecoder : BitmapDecoder {

    override suspend fun decode(): BitmapDecodeResult {
        // 在这里解析图片
    }

    companion object {
        const val MY_MIME_TYPE = "image/mypng"
    }

    class Factory : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): BitmapDecoder? {
            val mimeType = fetchResult.mimeType
            val dataSource = fetchResult.dataSource
            // 在这通过 mimeType 或 dataSource 判断当前图片是否是
            // MyBitmapDecoder 的目标类型，是的话返回一个新的 MyBitmapDecoder
            return if (fetchResult.mimeType == MY_MIME_TYPE) {
                MyBitmapDecoder()
            } else {
                null
            }
        }
    }
}
```

2.然后在配置 Sketch 时通过 components 方法将其 Factory 注册到 Sketch，这样所有的 [ImageRequest] 都可以使用，如下：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addBitmapDecoder(MyBitmapDecoder.Factory())
            }
        }.build()
    }
}
```

或者在显示图片时只给当前 [ImageRequest] 注册，这样就只有当前 [ImageRequest] 可以使用，如下：

```kotlin
imageView.displayImage("mypng://my.png") {
    components {
        addBitmapDecoder(MyBitmapDecoder.Factory())
    }
}
```

> 注意：自定义 Decoder 需要应用 ImageRequest 中的很多与图片质量和尺寸相关的属性，例如 bitmapConfig、resize、colorSpace 等，可参考其它 Decoder 实现

3.自定义 [DrawableDecoder] 和 [BitmapDecoder] 流程一样，唯一区别在于注册到 Sketch 时要调用 addDrawableDecoder() 方法
> 注意：如果你自定义的 [DrawableDecoder] 是解码动图的话一定要判断 [ImageRequest].disallowAnimatedImage 参数


[comment]: <> (class)

[BitmapDecoder]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/BitmapDecoder.kt

[DefaultBitmapDecoder]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/internal/DefaultBitmapDecoder.kt

[XmlDrawableBitmapDecoder]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/internal/XmlDrawableBitmapDecoder.kt

[FFmpegVideoFrameBitmapDecoder]: ../../sketch-video-ffmpeg/src/main/java/com/github/panpf/sketch/decode/FFmpegVideoFrameBitmapDecoder.kt

[ApkIconBitmapDecoder]: ../../sketch-extensions/src/main/java/com/github/panpf/sketch/decode/ApkIconBitmapDecoder.kt

[AppIconBitmapDecoder]: ../../sketch-extensions/src/main/java/com/github/panpf/sketch/decode/AppIconBitmapDecoder.kt

[VideoFrameBitmapDecoder]: ../../sketch-video/src/main/java/com/github/panpf/sketch/decode/VideoFrameBitmapDecoder.kt

[SvgBitmapDecoder]: ../../sketch-svg/src/main/java/com/github/panpf/sketch/decode/SvgBitmapDecoder.kt

[DrawableDecoder]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/DrawableDecoder.kt

[DefaultDrawableDecoder]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/internal/DefaultDrawableDecoder.kt

[GifAnimatedDrawableDecoder]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/GifAnimatedDrawableDecoder.kt

[HeifAnimatedDrawableDecoder]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/HeifAnimatedDrawableDecoder.kt

[WebpAnimatedDrawableDecoder]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/WebpAnimatedDrawableDecoder.kt

[GifDrawableDrawableDecoder]: ../../sketch-gif-koral/src/main/java/com/github/panpf/sketch/decode/GifDrawableDrawableDecoder.kt

[GifMovieDrawableDecoder]: ../../sketch-gif-movie/src/main/java/com/github/panpf/sketch/decode/GifMovieDrawableDecoder.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[wseemann]: https://github.com/wseemann

[FFmpegMediaMetadataRetriever-project]: https://github.com/wseemann/FFmpegMediaMetadataRetriever

[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever/blob/master/core/src/main/java/wseemann/media/FFmpegMediaMetadataRetriever.java

[BigBadaboom]: https://github.com/BigBadaboom

[androidsvg]: https://github.com/BigBadaboom/androidsvg

[koral--]: https://github.com/koral--

[android-gif-drawable]: https://github.com/koral--/android-gif-drawable

[GifDrawable]: https://github.com/koral--/android-gif-drawable/blob/dev/android-gif-drawable/src/main/java/pl/droidsonroids/gif/GifDrawable.java

[Movie]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/java/android/graphics/Movie.java

[ImageDecoder]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/java/android/graphics/ImageDecoder.java

[BitmapFactory]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/java/android/graphics/BitmapFactory.java

[MediaMetadataRetriever]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/media/java/android/media/MediaMetadataRetriever.java