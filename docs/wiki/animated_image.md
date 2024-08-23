# Animated Image

Translations: [简体中文](animated_image_zh.md)

> [!IMPORTANT]
> Required import `sketch-animated` or `sketch-animated-koralgif` module

[Sketch] supports playing GIF, WEBP, and HEIF animations. Each animation has a
corresponding [Decoder] to provide support, the platforms they support and their differences are as
follows:

| Format        | Decoder                   | Android   | iOS | Desktop | Web | resize | Dependent modules        |
|:--------------|:--------------------------|:----------|:----|:--------|:----|--------|:-------------------------|
| GIF           | [GifAnimatedDecoder]      | ✅(API 28) | ❌   | ❌       | ❌   | ✅      | sketch-animated          |
| GIF           | [GifMovieDecoder]         | ✅         | ❌   | ❌       | ❌   | ❌      | sketch-animated          |
| GIF           | [GifDrawableDecoder]      | ✅         | ❌   | ❌       | ❌   | ✅      | sketch-animated-koralgif |
| GIF           | [GifSkiaAnimatedDecoder]  | ❌         | ✅   | ✅       | ✅   | ❌      | sketch-animated          |
| WEBP Animated | [WebpAnimatedDecoder]     | ✅(API 28) | ❌   | ❌       | ❌   | ✅      | sketch-animated          |
| WEBP Animated | [WebpSkiaAnimatedDecoder] | ❌         | ✅   | ✅       | ✅   | ❌      | sketch-animated          |
| HEIF Animated | [HeifAnimatedDecoder]     | ✅(API 30) | ❌   | ❌       | ❌   | ✅      | sketch-animated          |

> [!TIP]
> There are three [Decoder] options for GIF on Android. You can choose the appropriate [Decoder]
> based on the minimum version supported by the app.

## Register Decoder

[Sketch] does not register any animated [Decoder] by default. You need to actively
register [Decoder] to [Sketch] to play animated images, as follows:

```kotlin
// Register for all ImageRequests when customizing Sketch
Sketch.Builder(context).apply {
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

// Register for a single ImageRequest when loading an image
ImageRequest(context, "https://www.example.com/image.gif") {
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

## Configuration

Both [ImageRequest] and [ImageOptions] provide related methods for animation-related configuration,
as follows:

```kotlin
ImageRequest(context, "https://www.example.com/image.gif") {
    // Disable animation and only decode the first frame of the animation
    disallowAnimatedImage()

    // Configure the animation to be played repeatedly once and then stop. The default is to play in an infinite loop.
    repeatCount(1)

    // Monitor the animation to start and stop playing
    onAnimationStart {
        // ...
    }
    onAnimationEnd {
        // ...
    }

    // [Only Android] Modify each frame of the animation as it is drawn 
    animatedTransformation { canvas: Canvas ->
        // ...
    }
}
```

## Control Play

[Decoder] related to animations uniformly returns [AnimatableDrawable] or [AnimatablePainter]. You
can control playback through their start() and stop() methods, and determine the playback status
through the isRunning() method.

#### Initial State

The initial state of the animation is controlled by [GenericViewTarget] and [GenericComposeTarget].
After the animation is load into Target, the status of [ImageRequest].lifecycle will be
checked. If the status of lifecycle is greater than start, it will start playing. Otherwise, it will
wait until lifecycle. Play again when the status changes to start

#### Automatic Control

[GenericViewTarget] and [GenericComposeTarget] will listen to the start and stop of [ImageRequest]
.lifecycle Status automatic control playback

#### Cache Decoding Timeout Frame

Sketch uses skiko's Codec to decode animations on non-Android platforms, but Codec decodes slower
frames closer to the end of the animation.

When the decoding time exceeds the duration of the previous frame, the user will feel that the
playback is stuck. Therefore, in order to improve the smoothness of playback, Sketch supports the
function of caching decoding timeout frames.

However, this feature will significantly increase memory consumption, so it is turned off by
default. You can enable it through the cacheDecodeTimeoutFrame() function, as follows:

```kotlin
ImageRequest(context, "https://www.example.com/image.gif") {
    cacheDecodeTimeoutFrame(true)
}
```

[comment]: <> (classs)


[AnimatableDrawable]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/drawable/AnimatableDrawable.kt

[AnimatablePainter]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/painter/AnimatablePainter.kt

[Decoder]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[GenericComposeTarget]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/target/GenericComposeTarget.kt

[GenericViewTarget]: ../../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/target/GenericViewTarget.kt

[GifAnimatedDecoder]: ../../sketch-animated/src/androidMain/kotlin/com/github/panpf/sketch/decode/GifAnimatedDecoder.kt

[GifDrawableDecoder]: ../../sketch-animated-koralgif/src/main/kotlin/com/github/panpf/sketch/decode/GifDrawableDecoder.kt

[GifMovieDecoder]: ../../sketch-animated/src/androidMain/kotlin/com/github/panpf/sketch/decode/GifMovieDecoder.kt

[GifSkiaAnimatedDecoder]: ../../sketch-animated/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/GifSkiaAnimatedDecoder.kt

[HeifAnimatedDecoder]: ../../sketch-animated/src/androidMain/kotlin/com/github/panpf/sketch/decode/HeifAnimatedDecoder.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt

[Movie]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/java/android/graphics/Movie.java

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[WebpAnimatedDecoder]: ../../sketch-animated/src/androidMain/kotlin/com/github/panpf/sketch/decode/WebpAnimatedDecoder.kt

[WebpSkiaAnimatedDecoder]: ../../sketch-animated/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/WebpSkiaAnimatedDecoder.kt


[comment]: <> (wiki)

[getting_started_platform_different]: getting_started.md#platform-differences