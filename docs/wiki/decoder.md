# Decoder

Translations: [简体中文](decoder_zh.md)

Decoder is used to decode an image file to get a Bitmap or Drawable, so Sketch has two kinds of
decoder:

* [BitmapDecoder]: Used to decode image files and convert them into Bitmap
    * [ApkIconBitmapDecoder][ApkIconBitmapDecoder]: Decode the Apk file
      icon, [click here to learn how to use it](apk_app_icon.md#displays-an-icon-for-the-apk-file)
    * [AppIconBitmapDecoder][AppIconBitmapDecoder]: Decode installed apps
      icon, [click me to learn how to use](apk_app_icon.md#displays-an-icon-for-the-installed-app)
    * [DefaultBitmapDecoder][DefaultBitmapDecoder]: The last Bitmap decoder with Android's
      built-in [BitmapFactory] Decode the image
    * [FFmpegVideoFrameBitmapDecoder][FFmpegVideoFrameBitmapDecoder]:
      Using [wseemann] /[FFmpegMediaMetadataRetriever-project] library
      for [FFmpegMediaMetadataRetriever] Class decode the frames of a video
      file, [click here to learn how to use](video_frame.md)
    * [SvgBitmapDecoder][SvgBitmapDecoder]: Decode static svg using the [BigBadaboom]/[androidsvg]
      library file, [click here to learn how to use](svg.md)
    * [VideoFrameBitmapDecoder][VideoFrameBitmapDecoder]: Use Android's
      built-in [MediaMetadataRetriever] Class decode the frames of a video
      file, [click here to learn how to use](video_frame.md)
    * [DrawableBitmapDecoder][DrawableBitmapDecoder]: Decode vector, shape, and other
      Android-supported xml drawable image
* [DrawableDecoder]:  Used to decode image files and convert them to Drawable
    * [DefaultDrawableDecoder][DefaultDrawableDecoder]: The final Drawable decoder calls
      BitmapDecoder to get the Bitmap It is then encapsulated as BitmapDrawable
    * [GifAnimatedDrawableDecoder][GifAnimatedDrawableDecoder]: Use Android's
      built-in [ImageDecoder] class to decode
      gifs , [Click here to learn how to use](animated_image.md)
    * [GifDrawableDrawableDecoder][GifDrawableDrawableDecoder]: Use [koral--]/[android-gif-drawable]
      Library's [GifDrawable] class to decode
      gifs, [click here to learn how to use it](animated_image.md)
    * [GifMovieDrawableDecoder][GifMovieDrawableDecoder]: Use Android's built-in [Movie] class to
      decode gifs Picture, [Click here to learn how to use it](animated_image.md)
    * [HeifAnimatedDrawableDecoder][HeifAnimatedDrawableDecoder]: Decode using Android's
      built-in [ImageDecoder] class HEIF GIF, [Click here to learn how to use it](animated_image.md)
    * [WebpAnimatedDrawableDecoder][WebpAnimatedDrawableDecoder]: Decode using Android's
      built-in [ImageDecoder] class webp GIFs, [click here to learn how to use](animated_image.md)

[BitmapDecoder] and [DrawableDecoder] each have a list of decoders, and when they need to be
decoded, Sketch will iterate through them according to the type of [ImageRequest].
Decoder list until you find a Decoder that can decode the image of the current type, and then call
its decode method to get the decoding result

### Extend Decoder

1.The first thing you need to do is implement the [BitmapDecoder] or [DrawableDecoder] interface to
define your Decoder and its
Factory, let's take [BitmapDecoder] as an example, as follows:

```kotlin
class MyBitmapDecoder : BitmapDecoder {

    override suspend fun decode(): Result<BitmapDecodeResult> {
        // Decode the image here
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
            // Here, use mimeType or dataSource to determine whether the current image is the 
            // target type of MyBitmapDecoder, and if so, return a new MyBitmapDecoder
            return if (fetchResult.mimeType == MY_MIME_TYPE) {
                MyBitmapDecoder()
            } else {
                null
            }
        }
    }
}
```

2.Then register via addBitmapDecoder as follows:

```kotlin
/* Register for all ImageRequests */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addBitmapDecoder(MyBitmapDecoder.Factory())
            }
        }.build()
    }
}

/* Register for a single ImageRequest */
imageView.displayImage("mypng://my.png") {
    components {
        addBitmapDecoder(MyBitmapDecoder.Factory())
    }
}
```

> Note: Customizing a Decoder requires the application of many properties related to image quality
> and size in the ImageRequest, such as bitmapConfig, resize, colorSpace, etc., which can be
> implemented by referring to other Decoder implementations

3.The custom [DrawableDecoder] flow is the same as the [BitmapDecoder] flow, with the only
difference being that it is called when it is registered to Sketch
addDrawableDecoder() method
> Note: If your custom [DrawableDecoder] is a decoded animated image, be sure to check the [ImageRequest]
> .disallowAnimatedImage parameter


[comment]: <> (class)

[BitmapDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/BitmapDecoder.kt

[DefaultBitmapDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/internal/DefaultBitmapDecoder.kt

[DrawableBitmapDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/internal/DrawableBitmapDecoder.kt

[FFmpegVideoFrameBitmapDecoder]: ../../sketch-video-ffmpeg/src/main/kotlin/com/github/panpf/sketch/decode/FFmpegVideoFrameBitmapDecoder.kt

[ApkIconBitmapDecoder]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/decode/ApkIconBitmapDecoder.kt

[AppIconBitmapDecoder]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/decode/AppIconBitmapDecoder.kt

[VideoFrameBitmapDecoder]: ../../sketch-video/src/main/kotlin/com/github/panpf/sketch/decode/VideoFrameBitmapDecoder.kt

[SvgBitmapDecoder]: ../../sketch-svg/src/main/kotlin/com/github/panpf/sketch/decode/SvgBitmapDecoder.kt

[DrawableDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/DrawableDecoder.kt

[DefaultDrawableDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/internal/DefaultDrawableDecoder.kt

[GifAnimatedDrawableDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/GifAnimatedDrawableDecoder.kt

[HeifAnimatedDrawableDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/HeifAnimatedDrawableDecoder.kt

[WebpAnimatedDrawableDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/WebpAnimatedDrawableDecoder.kt

[GifDrawableDrawableDecoder]: ../../sketch-gif-koral/src/main/kotlin/com/github/panpf/sketch/decode/GifDrawableDrawableDecoder.kt

[GifMovieDrawableDecoder]: ../../sketch-gif-movie/src/main/kotlin/com/github/panpf/sketch/decode/GifMovieDrawableDecoder.kt

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