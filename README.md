# ![logo_image] Sketch Image Loader

![Platform][platform_image]
[![License][license_image]][license_link]
[![version_icon]][version_link]
![QQ Group][qq_group_image]

Translations: [简体中文](README_zh.md)

Sketch is an image loading library specially designed for Compose Multiplatform and Android View. It
has the following features:

* `Multiple loading sources`: Supports loading images from multiple sources such as http, file,
  compose.resource, android asset/content/resource, etc.
* `Powerful functions`: Supports three-level caching, automatically cancels requests, automatically
  adjusts image size, automatically rotates images according to Exif Orientation, etc.
* `Rich functions`: Supports Animated image, SVG images, Base64 images, and video frames
* `Easy to Expand`: Supports expansion of various aspects such as caching, decoding, transformation,
  transition, placeholder, etc.
* `Special functions`: Practical extensions such as pausing downloads when cellular data is
  provided, pausing loading during list scrolling, image type badges, download progress indicators,
  etc.
* `Modern`: Completely based on Kotlin and Kotlin coroutine design

## Download

`Published to mavenCentral`

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (Not included 'v')

Compose Multiplatform:

```kotlin
// Provides the core functions of Sketch as well as singletons and extension 
// functions that rely on singleton implementations
implementation("io.github.panpf.sketch4:sketch-compose:${LAST_VERSION}")
```

> [!IMPORTANT]
> To improve the performance of compose, please copy [compose_compiler_config.conf] under
> the `sketch-core` module file to your project and configure it according to
> the [Compose Stability Configuration][stability_configuration] documentation

Android View:

```kotlin
// Provides the core functions of Sketch as well as singletons and extension 
// functions that rely on singleton implementations
implementation("io.github.panpf.sketch4:sketch-view:${LAST_VERSION}")
```

There are also some optional modules:

```kotlin
// Use Android or Skia's built-in decoder to decode gif, webp, heif and other animated images and play them
implementation("io.github.panpf.sketch4:sketch-animated:${LAST_VERSION}")

// [Android only] Use GifDrawable of the android-gif-drawable library to decode gif and play it
implementation("io.github.panpf.sketch4:sketch-animated-koralgif:${LAST_VERSION}")

// Provides practical functions such as download progress, pausing loading during list scrolling, 
// saving cellular data, image type badge, loading apk icons and installed app icons, etc.
implementation("io.github.panpf.sketch4:sketch-extensions-compose:${LAST_VERSION}")
implementation("io.github.panpf.sketch4:sketch-extensions-view:${LAST_VERSION}")

// [JVM only] Supports using OkHttp to download images
implementation("io.github.panpf.sketch4:sketch-http-okhttp:${LAST_VERSION}")

// [JVM only] Supports using ktor to download images
implementation("io.github.panpf.sketch4:sketch-http-ktor:${LAST_VERSION}")

// Support SVG images
implementation("io.github.panpf.sketch4:sketch-svg:${LAST_VERSION}")

// [Android only] Use Android's built-in MediaMetadataRetriever class to decode video frames
implementation("io.github.panpf.sketch4:sketch-video:${LAST_VERSION}")

// [Android only] Decoding video frames using wseemann's FFmpegMediaMetadataRetriever library
implementation("io.github.panpf.sketch4:sketch-video-ffmpeg:${LAST_VERSION}")
```

> [!TIP]
> * `sketch-compose`, `sketch-view`, `sketch-extensions-compose`, `sketch-extensions-view`
    Modules all depend on the singleton provided by the `sketch-singleton` module. If you don’t need
    the singleton, you can directly rely on their `*-core` version.
> * On Android `sketch-compose` and `sketch-view` can be used together

#### R8 / Proguard

Sketch itself does not need to configure any obfuscation rules, but you may need to configure it for
the indirectly dependent [Kotlin Coroutines], [OkHttp], [Okio] Add obfuscation configuration

## Quickly Started

Compose Multiplatform：

```kotlin
// val imageUri = "/Users/my/Downloads/image.jpg"
// val imageUri = "compose.resource://files/sample.png"
val imageUri = "https://www.sample.com/image.jpg"

AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)

// config params
AsyncImage(
    rqeuest = ImageRequest(imageUri) {
        placeholder(Res.drawable.placeholder)
        error(Res.drawable.error)
        crossfade()
        // There is a lot more...
    },
    contentDescription = "photo"
)

Image(
    painter = rememberAsyncImagePainter(
        request = ImageRequest(imageUri) {
            placeholder(Res.drawable.placeholder)
            error(Res.drawable.error)
            crossfade()
            // There is a lot more...
        }
    ),
    contentDescription = "photo"
)
```

Android View：

```kotlin
// val imageUri = "/sdcard/download/image.jpg"
// val imageUri = "asset://image.jpg"
// val imageUri = "content://media/external/images/media/88484"
val imageUri = "https://www.sample.com/image.jpg"

imageView.displayImage(imageUri)

// config params
imageView.displayImage(imageUri) {
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
    crossfade()
    // There is a lot more...
}

val request = ImageRequest(context, imageUri) {
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
    crossfade()
    target(imageView)
    // There is a lot more...
}
context.sketch.enqueue(request)
```

For more information about Uri, image types, platform differences, Sketch customization,
ImageRequest, etc., please view the [《Getting Started》][getting_started] document

## Documents

Basic functions：

* [Get Started][getting_started]
* [Compose][compose]
* [AnimatedImage：GIF、WEBP、HEIF][animated_image]
* [Resize：Modify the image size][resize]
* [Transformation：Transformation image][transformation]
* [Transition：Display images in cool transitions][transition]
* [StateImage：Placeholder and error images][state_image]
* [Listener：Listen for request status and download progress][listener]
* [Cache：Learn about downloads, results, memory caching][cache]
* [Fetcher：Learn about Fetcher and extend new URI types][fetcher]
* [Decode：Understand the decoding process of Sketch][decode]
* [Target：Apply the load results to the target][target]
* [HttpStack：Learn about the HTTP section and using okhttp][http_stack]
* [SVG：Decode SVG still images][svg]
* [VideoFrames：Decode video frames][video_frame]
* [ExifOrientation：Correct the image orientation][exif_orientation]
* [ImageOptions：Manage image configurations in a unified manner][image_options]
* [RequestInterceptor：Intercept ImageRequest][request_interceptor]
* [Preload][preload]
* [Lifecycle][lifecycle]
* [Log][log]

Featured functions：

* [SketchImageView：Configure the request through XML attributes][sketch_image_view]
* [Improve the clarity of long images in grid lists][long_image_grid_thumbnails]
* [Displays the download progress][progress_indicator]
* [Displays the image type corner][mime_type_logo]
* [Pause image downloads on cellular data to save data][save_cellular_traffic]
* [The list slides to pause the loading of images][pause_load_when_scrolling]
* [Displays an icon for an apk file or installed app][apk_app_icon]

## Change log

Please review the [CHANGELOG.md] file

## About version 4.0

* The maven groupId is upgraded to `io.github.panpf.sketch4`, so versions 2.\* and 3.\* will not
  prompt for upgrade
* Version 4.0 is specially built for Compose Multiplatform, so there are many breaking changes in
  the API, please upgrade with caution
* Version 4.0 has made a lot of simplifications and is much simpler than version 3.0, such as
  DisplayRequest, LoadRequest, DownloadRequest
  Merged into one ImageRequest, removed BitmapPool, etc.
* Android minimum API raised to API 21

## Special thanks

* [coil-kt/coil][coil]: Sketch uses some code from Coil, including framework, compose and
  sketch-animated movie part
* [koral--/android-gif-drawable][android-gif-drawable]: animated-koralgif
* [wseemann/FFmpegMediaMetadataRetriever][FFmpegMediaMetadataRetriever]: video-ffmpeg
* [BigBadaboom/androidsvg][androidsvg]: svg

## My Projects

The following are my other open source projects. If you are interested, you can learn about them:

* [zoomimage](https://github.com/panpf/zoomimage)：Library for zoom images, supported Android View,
  Compose and Compose Multiplatform; supported double-click zoom, One or two fingers gesture zoom,
  single-finger drag, inertial sliding, positioning, rotation, super-large image subsampling and
  other functions.
* [assembly-adapter](https://github.com/panpf/assembly-adapter)：A library on Android that provides
  multi-type Item implementations for various adapters. Incidentally, it also provides the most
  powerful divider for RecyclerView.
* [sticky-item-decoration](https://github.com/panpf/stickyitemdecoration)：RecyclerView sticky item
  implementation

## License

Apache 2.0. See the [LICENSE](LICENSE.txt) file for details.

[comment]: <> (header)

[license_image]: https://img.shields.io/badge/License-Apache%202-blue.svg

[logo_image]: docs/res/logo.png

[license_link]: https://www.apache.org/licenses/LICENSE-2.0

[platform_image]: https://img.shields.io/badge/Platform-ComposeMultiplatform-brightgreen.svg

[qq_group_image]: https://img.shields.io/badge/QQ%E4%BA%A4%E6%B5%81%E7%BE%A4-529630740-red.svg

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/


[comment]: <> (wiki)

[animated_image]: docs/wiki/animated_image.md

[apk_app_icon]: docs/wiki/apk_app_icon.md

[cache]: docs/wiki/cache.md

[compose]: docs/wiki/compose.md

[decode]: docs/wiki/decode.md

[exif_orientation]: docs/wiki/exif_orientation.md

[fetcher]: docs/wiki/fetcher.md

[getting_started]: docs/wiki/getting_started.md

[http_stack]: docs/wiki/http_stack.md

[image_options]: docs/wiki/image_options.md

[lifecycle]: docs/wiki/lifecycle.md

[listener]: docs/wiki/listener.md

[log]: docs/wiki/log.md

[long_image_grid_thumbnails]: docs/wiki/long_image_grid_thumbnails.md

[mime_type_logo]: docs/wiki/mime_type_logo.md

[pause_load_when_scrolling]: docs/wiki/pause_load_when_scrolling.md

[preload]: docs/wiki/preload.md

[progress_indicator]: docs/wiki/progress_indicator.md

[request_interceptor]: docs/wiki/request_interceptor.md

[resize]: docs/wiki/resize.md

[save_cellular_traffic]: docs/wiki/save_cellular_traffic.md

[sketch_image_view]: docs/wiki/sketch_image_view.md

[state_image]: docs/wiki/state_image.md

[svg]: docs/wiki/svg.md

[target]: docs/wiki/target.md

[transformation]: docs/wiki/transformation.md

[transition]: docs/wiki/transition.md

[video_frame]: docs/wiki/video_frame.md


[comment]: <> (links)


[androidsvg]: https://github.com/BigBadaboom/androidsvg

[android-gif-drawable]: https://github.com/koral--/android-gif-drawable

[coil]: https://github.com/coil-kt/coil

[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever


[Kotlin Coroutines]: https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/resources/META-INF/proguard/coroutines.pro

[OkHttp]: https://github.com/square/okhttp/blob/master/okhttp/src/jvmMain/resources/META-INF/proguard/okhttp3.pro

[Okio]: https://github.com/square/okio/blob/master/okio/src/jvmMain/resources/META-INF/proguard/okio.pro


[compose_compiler_config.conf]: sketch-core/compose_compiler_config.conf

[stability_configuration]: https://developer.android.com/develop/ui/compose/performance/stability/fix#configuration-file


[comment]: <> (footer)

[CHANGELOG.md]: CHANGELOG.md