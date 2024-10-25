# Animated Image

Translations: [简体中文](animated_image_zh.md)

Sketch provides the `sketch-animated-*` series of modules to support animated graphics. The
supported platforms and differences are as follows:

| Module                    | DecoderProvider                           | Decoder                                                                                                                          | Android   | iOS | Desktop | Web |
|:--------------------------|:------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------|:----------|:----|:--------|:----|
| sketch-animated-gif       | [GifDecoderProvider]                      | android api 28+: [ImageDecoderGifDecoder]</br>android api 27-: [MovieGifDecoder]</br>non android: [SkiaGifDecoder]               | ✅         | ✅   | ✅       | ✅   |
| sketch-animated-gif-koral | [KoralGifDecoderProvider]                 | [KoralGifDecoder]                                                                                                                | ✅         | ❌   | ❌       | ❌   |
| sketch-animated-webp      | [AnimatedWebpDecoderProvider]             | android api 28+: [ImageDecoderAnimatedWebpDecoder]</br>android api 27-: Not supported</br>non android: [SkiaAnimatedWebpDecoder] | ✅(API 28) | ✅   | ✅       | ✅   |
| sketch-animated-heif      | [ImageDecoderAnimatedHeifDecoderProvider] | [ImageDecoderAnimatedHeifDecoder]                                                                                                | ✅(API 30) | ❌   | ❌       | ❌   |

## Download

Before loading animations, you need to select one of the above components and configure
dependencies. Take `sketch-animated-gif` as an example:

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (Not included 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-animated-gif:${LAST_VERSION}")
```

The above components all support automatic registration. You only need to import them without
additional configuration. If you need to register manually, please read the
documentation: [《Register component》](register_component.md)

## Load animated image

Simply specify the uri to load the image, as follows:

```kotlin
val imageUri = "https://www.sample.com/image.gif"

// compose
AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)

// view
imageView.loadImage(imageUri)
```

## Configuration

The `sketch-animated-core` module has some extension methods for [ImageRequest] and [ImageOptions]
for animation-related configuration, as follows:

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
    animatedTransformation { canvas: Any ->
        if (canvas is androidx.compose.ui.graphics.Canvas) {
            // ...
        } else if (canvas is android.graphics.Canvas) {
            // ...
        }
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

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/

[AnimatableDrawable]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/drawable/AnimatableDrawable.kt

[AnimatablePainter]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/painter/AnimatablePainter.kt

[Decoder]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[GenericComposeTarget]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/target/GenericComposeTarget.kt

[GenericViewTarget]: ../../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/target/GenericViewTarget.kt

[ImageDecoderGifDecoder]: ../../sketch-animated-gif/src/androidMain/kotlin/com/github/panpf/sketch/decode/ImageDecoderGifDecoder.kt

[KoralGifDecoder]: ../../sketch-animated-gif-koral/src/main/kotlin/com/github/panpf/sketch/decode/KoralGifDecoder.kt

[MovieGifDecoder]: ../../sketch-animated-gif/src/androidMain/kotlin/com/github/panpf/sketch/decode/MovieGifDecoder.kt

[SkiaGifDecoder]: ../../sketch-animated-gif/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/SkiaGifDecoder.kt

[ImageDecoderAnimatedHeifDecoder]: ../../sketch-animated-heif/src/main/kotlin/com/github/panpf/sketch/decode/ImageDecoderAnimatedHeifDecoder.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt

[Movie]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/java/android/graphics/Movie.java

[ImageDecoderAnimatedWebpDecoder]: ../../sketch-animated-webp/src/androidMain/kotlin/com/github/panpf/sketch/decode/ImageDecoderAnimatedWebpDecoder.kt

[SkiaAnimatedWebpDecoder]: ../../sketch-animated-webp/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/SkiaAnimatedWebpDecoder.kt

[GifDecoderProvider]: ../../sketch-animated-gif/src/commonMain/kotlin/com/github/panpf/sketch/decode/internal/GifDecoderProvider.common.kt

[KoralGifDecoderProvider]: ../../sketch-animated-gif-koral/src/main/kotlin/com/github/panpf/sketch/decode/internal/KoralGifDecoderProvider.kt

[AnimatedWebpDecoderProvider]: ../../sketch-animated-webp/src/commonMain/kotlin/com/github/panpf/sketch/decode/internal/AnimatedWebpDecoderProvider.common.kt

[ImageDecoderAnimatedHeifDecoderProvider]: ../../sketch-animated-heif/src/main/kotlin/com/github/panpf/sketch/decode/internal/ImageDecoderAnimatedHeifDecoderProvider.kt

[comment]: <> (wiki)

[getting_started_platform_different]: getting_started.md#platform-differences