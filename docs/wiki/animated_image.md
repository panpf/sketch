# Play Animated Image

Translations: [简体中文](animated_image_zh.md)

Sketch supports playing GIF, WEBP, HEIF animated images, and each animated image is supported by a
corresponding [DrawableDecoder], as follows:

| Type          | Decoder                       | APi Limit    | Additional Module |
|:--------------|:------------------------------|:-------------|:------------------|
| GIF           | [GifAnimatedDrawableDecoder]  | Android 9+   | _                 |
| GIF           | [GifMovieDrawableDecoder]     | Android 4.4+ | sketch-gif-movie  |
| GIF           | [GifDrawableDrawableDecoder]  | Android 4.1+ | sketch-gif-koral  |
| WEBP Animated | [WebPAnimatedDrawableDecoder] | Android 9+   | _                 |
| HEIF Animated | [HeifAnimatedDrawableDecoder] | Android 11+  | _                 |

> Caution:
> 1. [GifMovieDrawableDecoder] and [GifDrawableDrawableDecoder] need to rely on additional modules
> 2. There are three types of GIFs, [DrawableDecoder] that can be selected according to the minimum
     version supported by the app
> 3. The `sketch-gif-movie` module uses Android's built-in [Movie] class to play GIFs without adding
     extra package size
> 4. The `sketch-gif-koral` module uses the [gif--]/[android-gif-drawable] library for playback
     gif, the library size is about 250 KB

## Register Decoder

By default, Sketch does not register any [DrawableDecoder] for animated image, so you need to
actively register [DrawableDecoder] with Sketch to play the animated image, as follows:

```kotlin
/* Register for all ImageRequests */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addDrawableDecoder(
                    when {
                        VERSION.SDK_INT >= VERSION_CODES.P -> GifAnimatedDrawableDecoder.Factory()
                        VERSION.SDK_INT >= VERSION_CODES.KITKAT -> GifMovieDrawableDecoder.Factory()
                        else -> GifDrawableDrawableDecoder.Factory()
                    }
                )
                if (VERSION.SDK_INT >= VERSION_CODES.P) {
                    addDrawableDecoder(WebpAnimatedDrawableDecoder.Factory())
                }
                if (VERSION.SDK_INT >= VERSION_CODES.R) {
                    addDrawableDecoder(HeifAnimatedDrawableDecoder.Factory())
                }
            }
        }.build()
    }
}

/* Register for a single ImageRequest */
imageView.displayImage("https://www.example.com/image.gif") {
     components {
          addDrawableDecoder(
               when {
                    VERSION.SDK_INT >= VERSION_CODES.P -> GifAnimatedDrawableDecoder.Factory()
                    VERSION.SDK_INT >= VERSION_CODES.KITKAT -> GifMovieDrawableDecoder.Factory()
                    else -> GifDrawableDrawableDecoder.Factory()
               }
          )
          if (VERSION.SDK_INT >= VERSION_CODES.P) {
               addDrawableDecoder(WebpAnimatedDrawableDecoder.Factory())
          }
          if (VERSION.SDK_INT >= VERSION_CODES.R) {
               addDrawableDecoder(HeifAnimatedDrawableDecoder.Factory())
          }
     }
}
```

## Configure

Both [ImageRequest] and [ImageOptions] provide relevant methods for animated image configuration, as
follows:

```kotlin
imageView.displayImage("https://www.example.com/image.gif") {
    // Disabling animated image will decode only the first frame of animated image
    disallowAnimatedImage()

    // Configure animated image playback to stop after 1 time, and play it in an infinite loop by default
    repeatCount(1)

    // Listen for the animated image to start and stop playback
    onAnimationStart {
        // ...
    }
    onAnimationEnd {
        // ...
    }

    // Modify each frame of the animated image as you draw 
    animatedTransformation { canvas ->
        // ...
    }
}
```

## Control playback

The [DrawableDecoder] related to the animated image returns [SketchAnimatableDrawable] uniformly,
and [SketchAnimatableDrawable] is implemented
Animatable2Compat interface

You can manually control the start and stop playback via the start() and stop() methods of the
Animatable2Compat interface

#### Initial state

[GenericViewDisplayTarget] checks after [SketchAnimatableDrawable] is displayed on the ImageView
ImageRequest.lifecycle, if the state of lifecycle is greater than start, it will start playing

#### Automatic control

[GenericViewDisplayTarget] listens to the start and stop states of ImageRequest.lifecycle to
automatically control playback


[koral--]: https://github.com/koral--

[android-gif-drawable]: https://github.com/koral--/android-gif-drawable

[GifDrawable]: https://github.com/koral--/android-gif-drawable/blob/dev/android-gif-drawable/src/main/kotlin/pl/droidsonroids/gif/GifDrawable.java

[DrawableDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/DrawableDecoder.kt

[GifAnimatedDrawableDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/GifAnimatedDrawableDecoder.kt

[HeifAnimatedDrawableDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/HeifAnimatedDrawableDecoder.kt

[WebpAnimatedDrawableDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/WebpAnimatedDrawableDecoder.kt

[GifDrawableDrawableDecoder]: ../../sketch-gif-koral/src/main/kotlin/com/github/panpf/sketch/decode/GifDrawableDrawableDecoder.kt

[GifMovieDrawableDecoder]: ../../sketch-gif-movie/src/main/kotlin/com/github/panpf/sketch/decode/GifMovieDrawableDecoder.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[SketchFactory]: ../../sketch/src/main/kotlin/com/github/panpf/sketch/SketchFactory.kt

[SketchAnimatableDrawable]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/drawable/SketchAnimatableDrawable.kt

[Movie]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/kotlin/android/graphics/Movie.java

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageOptions.kt

[GenericViewDisplayTarget]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/target/GenericViewDisplayTarget.kt