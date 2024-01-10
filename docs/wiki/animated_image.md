# Play Animated Image

Translations: [简体中文](animated_image_zh.md)

Sketch supports playing GIF, WEBP, HEIF animated images, and each animated image is supported by a
corresponding [Decoder], as follows:

| Type          | Decoder               | APi Limit    | Additional Module |
|:--------------|:----------------------|:-------------|:------------------|
| GIF           | [GifAnimatedDecoder]  | Android 9+   | sketch-gif        |
| GIF           | [GifMovieDecoder]     | Android 4.4+ | sketch-gif        |
| GIF           | [GifDrawableDecoder]  | Android 4.1+ | sketch-gif-koral  |
| WEBP Animated | [WebPAnimatedDecoder] | Android 9+   | sketch-gif        |
| HEIF Animated | [HeifAnimatedDecoder] | Android 11+  | sketch-gif        |

> Caution:
> 1. There are three types of GIFs, [Decoder] that can be selected according to the minimum
     version supported by the app
> 2. The `sketch-gif` module uses Android's built-in [ImageDecoder] and [Movie] classes to implement
     GIF, WEBP, and HEIF playback without additional increase in package size.
> 3. The `sketch-gif-koral` module uses the [gif--]/[android-gif-drawable] library for playback
     gif, the library size is about 250 KB

## Register Decoder

By default, Sketch does not register any [Decoder] for animated image, so you need to
actively register [Decoder] with Sketch to play the animated image, as follows:

```kotlin
/* Register for all ImageRequests */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addDecoder(
                    when {
                        VERSION.SDK_INT >= VERSION_CODES.P -> GifAnimatedDecoder.Factory()
                        VERSION.SDK_INT >= VERSION_CODES.KITKAT -> GifMovieDecoder.Factory()
                        else -> GifDrawableDecoder.Factory()
                    }
                )
                if (VERSION.SDK_INT >= VERSION_CODES.P) {
                    addDecoder(WebpAnimatedDecoder.Factory())
                }
                if (VERSION.SDK_INT >= VERSION_CODES.R) {
                    addDecoder(HeifAnimatedDecoder.Factory())
                }
            }
        }.build()
    }
}

/* Register for a single ImageRequest */
imageView.displayImage("https://www.example.com/image.gif") {
    components {
        addDecoder(
            when {
                VERSION.SDK_INT >= VERSION_CODES.P -> GifAnimatedDecoder.Factory()
                VERSION.SDK_INT >= VERSION_CODES.KITKAT -> GifMovieDecoder.Factory()
                else -> GifDrawableDecoder.Factory()
            }
        )
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            addDecoder(WebpAnimatedDecoder.Factory())
        }
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            addDecoder(HeifAnimatedDecoder.Factory())
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

The [Decoder] related to the animated image returns [SketchAnimatableDrawable] uniformly,
and [SketchAnimatableDrawable] is implemented
Animatable2Compat interface

You can manually control the start and stop playback via the start() and stop() methods of the
Animatable2Compat interface

#### Initial state

[GenericViewTarget] checks after [SketchAnimatableDrawable] is displayed on the ImageView
ImageRequest.lifecycle, if the state of lifecycle is greater than start, it will start playing

#### Automatic control

[GenericViewTarget] listens to the start and stop states of ImageRequest.lifecycle to
automatically control playback


[koral--]: https://github.com/koral--

[android-gif-drawable]: https://github.com/koral--/android-gif-drawable

[GifDrawable]: https://github.com/koral--/android-gif-drawable/blob/dev/android-gif-drawable/src/main/kotlin/pl/droidsonroids/gif/GifDrawable.java

[Decoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[GifAnimatedDecoder]: ../../sketch-gif/src/main/kotlin/com/github/panpf/sketch/decode/GifAnimatedDecoder.kt

[HeifAnimatedDecoder]: ../../sketch-gif/src/main/kotlin/com/github/panpf/sketch/decode/HeifAnimatedDecoder.kt

[WebpAnimatedDecoder]: ../../sketch-gif/src/main/kotlin/com/github/panpf/sketch/decode/WebpAnimatedDecoder.kt

[GifDrawableDecoder]: ../../sketch-gif-koral/src/main/kotlin/com/github/panpf/sketch/decode/GifDrawableDecoder.kt

[GifMovieDecoder]: ../../sketch-gif/src/main/kotlin/com/github/panpf/sketch/decode/GifMovieDecoder.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[SketchFactory]: ../../sketch/src/main/kotlin/com/github/panpf/sketch/SketchFactory.kt

[SketchAnimatableDrawable]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/drawable/SketchAnimatableDrawable.kt

[Movie]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/java/android/graphics/Movie.java

[ImageDecoder]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/java/android/graphics/ImageDecoder.java

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageOptions.kt

[GenericViewTarget]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/target/GenericViewTarget.kt