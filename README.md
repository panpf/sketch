# ![logo_image] Sketch Image Loader

![Platform][platform_image]
[![API][min_api_image]][min_api_link]
[![License][license_image]][license_link]
[![version_icon]][version_link]
![QQ Group][qq_group_image]

Translations: [简体中文](README_zh.md)

Sketch is a powerful and comprehensive image load library on Android, in addition to the basic
functions, it also supports Jetpack Compose, GIF, SVG, video thumbnails, huge images
sampling, ExifInterface and other functions.

## Features

* Support http, asset, content, android.resource and other URIs
* Support playing GIFs, WebP, HEIF and other animated image
* Supports download, conversion results, and memory L3 cache
* Support for correcting image orientation via Exif
* Supports Base64, video frames, SVG images
* Support for Jetpack Compose
* Supports automatic resizing of images according to the size of the view
* Supports loading only pictures to memory or downloading only pictures to disk
* Supports various useful features such as saving cellular data
* Support the extension of URI, cache, decoding, conversion, display, placeholder and other links
* Based on Kotlin and Kotlin coroutines

## Import

`Published to mavenCentral`

```kotlin
dependencies {
    // The core functionality of Sketch is provided as well as a singleton and some 
    // handy extension functions that depend on this singleton implementation, 
    // and if you don't need a singleton, you can use the sketch-core module
    implementation("io.github.panpf.sketch4:sketch:${LAST_VERSION}")
}
```

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (Not included 'v')

There are also optional modules to extend the functionality of sketch:

```kotlin
dependencies {
    // Support for Jetpack Compose.
    // It relies on the singletons provided by the sketch module, 
    // and you can use the sketch-compose-core module if you don't need the singleton pattern
    implementation("io.github.panpf.sketch4:sketch-compose:${LAST_VERSION}")

    // Provides View with practical functions such as download progress, 
    // pausing loading during list sliding, saving cellular data, 
    // image type corner icons, loading apk files and installed app icons, etc.
    // It relies on the singleton provided by the sketch module. 
    // If you do not need the singleton mode, you can use the sketch-view-core module.
    implementation("io.github.panpf.sketch4:sketch-extensions-view:${LAST_VERSION}")

    // Provide Compose with practical functions such as download progress, 
    // pausing loading during list sliding, saving cellular data, 
    // image type corner icons, loading apk files and installed app icons, etc.
    implementation("io.github.panpf.sketch4:sketch-extensions-compose:${LAST_VERSION}")

    // GIF playback is achieved through Android's built-in ImageDecoder and Movie class
    implementation("io.github.panpf.sketch4:sketch-animated:${LAST_VERSION}")

    // GifDrawable through Koral's android-gif-drawable library
    implementation("io.github.panpf.sketch4:sketch-animated-koralgif:${LAST_VERSION}")

    // Support for OkHttp
    implementation("io.github.panpf.sketch4:sketch-okhttp:${LAST_VERSION}")

    // SVG images are supported
    implementation("io.github.panpf.sketch4:sketch-svg:${LAST_VERSION}")

    // Video frames are read through Android's built-in MediaMetadataRetriever class
    implementation("io.github.panpf.sketch4:sketch-video:${LAST_VERSION}")

    // Video frames are read through wseemann's FFmpegMediaMetadataRetriever library
    implementation("io.github.panpf.sketch4:sketch-video-ffmpeg:${LAST_VERSION}")
}
```

#### R8 / Proguard

Sketch doesn't need to configure any obfuscation rules itself, but you may need to add obfuscation
configurations for indirectly dependent [Kotlin Coroutines], [OkHttp], [Okio].

## Quickly Started

#### ImageView

Sketch provides a series of extended functions called displayImage for ImageView, which can easily
display images

```kotlin
// http
imageView.displayImage("https://www.sample.com/image.jpg")

// File
imageView.displayImage("/sdcard/download/image.jpg")

// asset
imageView.displayImage("asset://image.jpg")

// There is a lot more...
```

You can also configure parameters through a trailing lambda function:

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
    transformations(CircleCropTransformation())
    crossfade()
    // There is a lot more...
}
```

#### Jetpack Compose

> [!IMPORTANT]
> Required import `sketch-compose` module

```kotlin
AsyncImage(
    uri = "https://www.sample.com/image.jpg",
    contentScale = ContentScale.Crop,
    contentDescription = ""
)

// config params
AsyncImage(
    rqeuest = ImageRequest(LocalContext.current, "https://www.sample.com/image.jpg") {
        placeholder(R.drawable.placeholder)
        error(R.drawable.error)
        transformations(BlurTransformation())
        crossfade()
        // There is a lot more...
    },
    contentScale = ContentScale.Crop,
    contentDescription = ""
)
```

## Document

Basic functions：

* [Get Started][getting_started]
* [AnimatedImage：GIF、WEBP、HEIF][animated_image]
* [Resize：Modify the image size][resize]
* [Transformation：Transformation image][transformation]
* [Transition：Display images in cool transitions][transition]
* [StateImage：Placeholder and error images][state_image]
* [Listener：Listen for request status and download progress][listener]
* [Cache：Learn about downloads, results, memory caching][cache]
* [Fetcher：Learn about Fetcher and extend new URI types][fetcher]
* [Decoder：Learn about Decoder and expand into new image types][decoder]
* [Target：Apply the load results to the target][target]
* [HttpStack：Learn about the HTTP section and using okhttp][http_stack]
* [SVG：Decode SVG still images][svg]
* [VideoFrames：Decode video frames][video_frame]
* [Exif：Correct the image orientation][exif]
* [ImageOptions：Manage image configurations in a unified manner][image_options]
* [RequestInterceptor：Intercept ImageRequest][request_interceptor]
* [DecodeInterceptor：Intercept Bitmap or Drawable decoding][decode_interceptor]
* [DownloadRequest：Download the image to disk][download_request]
* [LoadRequest：Load the image to get the Bitmap][load_request]
* [Preload images into memory][preloading]
* [Lifecycle][lifecycle]
* [Jetpack Compose][jetpack_compose]
* [Log][log]

Featured functions：

* [SketchImageView：Configure the request through XML attributes][sketch_image_view]
* [Improve the clarity of long images in grid lists][long_image_grid_thumbnails]
* [Displays the download progress][download_progress_indicator]
* [Displays the image type corner][mime_type_logo]
* [Pause image downloads on cellular data to save data][save_cellular_traffic]
* [The list slides to pause the loading of images][pause_load_when_scrolling]
* [Displays an icon for an apk file or installed app][apk_app_icon]

## Changelog

Please review the [CHANGELOG.md] file

### About version 3.0

* The maven groupId was changed to 'io.github.panpf.sketch4', so version 2.\* will not prompt for an
  upgrade
* The package name was changed to 'com.github.panpf.sketch' so it does not conflict with version
  2.\*
* Based on the kotlin coroutine rewrite, APIs and functions are all refactored as a new library
* There is no longer a requirement to use a SketchImageView, any ImageView and its subclasses will
  do, and any View can be supported in combination with a custom Target
* The Zoom function is split into independent modules that can be relied on separately, and the
  large image sampling function is refactored and supports multi-threaded decoding, which is faster
* The gif module now directly depends on the [android-gif-drawable] library, no longer modified
  twice, and can be upgraded by itself
* Support for Jetpack Compose
* Support for request and decode interceptors
* Referring to [coil] and combining with the original functionality of sketch, there are the
  following differences compared to [coil]:
    * sketch supports a minimum of API 16, while [coil] supports only API 21
    * Sketch supports bitmap reuse, while [coil] does not
    * Sketch supports more granular resizing of images
    * sketch clearly distinguishes between display, load, and download requests

## Special thanks

* [coil-kt]/[coil]: Sketch uses some code from Coil, including framework, compose,
  sketch-animated-movie
  parts
* [chrisbanes]/[PhotoView]: Zoom
* [koral--]/[android-gif-drawable]: gif-koral
* [wseemann]/[FFmpegMediaMetadataRetriever]: video-ffmpeg
* [BigBadaboom]/[androidsvg]: svg

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

[logo_image]: docs/res/logo.png

[platform_image]: https://img.shields.io/badge/Platform-Android-brightgreen.svg

[license_image]: https://img.shields.io/badge/License-Apache%202-blue.svg

[license_link]: https://www.apache.org/licenses/LICENSE-2.0

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/

[min_api_image]: https://img.shields.io/badge/API-16%2B-orange.svg

[min_api_link]: https://android-arsenal.com/api?level=16

[qq_group_image]: https://img.shields.io/badge/QQ%E4%BA%A4%E6%B5%81%E7%BE%A4-529630740-red.svg


[comment]: <> (wiki)

[getting_started]: docs/wiki/getting_started.md

[fetcher]: docs/wiki/fetcher.md

[decoder]: docs/wiki/decoder.md

[animated_image]: docs/wiki/animated_image.md

[resize]: docs/wiki/resize.md

[transformation]: docs/wiki/transformation.md

[transition]: docs/wiki/transition.md

[state_image]: docs/wiki/state_image.md

[listener]: docs/wiki/listener.md

[cache]: docs/wiki/cache.md

[target]: docs/wiki/target.md

[http_stack]: docs/wiki/http_stack.md

[svg]: docs/wiki/svg.md

[video_frame]: docs/wiki/video_frame.md

[exif]: docs/wiki/exif.md

[image_options]: docs/wiki/image_options.md

[request_interceptor]: docs/wiki/request_interceptor.md

[decode_interceptor]: docs/wiki/decode_interceptor.md

[bitmap_pool]: docs/wiki/bitmap_pool.md

[preloading]: docs/wiki/preloading.md

[download_request]: docs/wiki/download_request.md

[load_request]: docs/wiki/load_request.md

[long_image_grid_thumbnails]: docs/wiki/long_image_grid_thumbnails.md

[mime_type_logo]: docs/wiki/mime_type_logo.md

[download_progress_indicator]: docs/wiki/download_progress_indicator.md

[sketch_image_view]: docs/wiki/sketch_image_view.md

[save_cellular_traffic]: docs/wiki/save_cellular_traffic.md

[pause_load_when_scrolling]: docs/wiki/pause_load_when_scrolling.md

[apk_app_icon]: docs/wiki/apk_app_icon.md

[log]: docs/wiki/log.md

[lifecycle]: docs/wiki/lifecycle.md

[jetpack_compose]: docs/wiki/jetpack_compose.md


[comment]: <> (links)

[koral--]: https://github.com/koral--

[android-gif-drawable]: https://github.com/koral--/android-gif-drawable

[chrisbanes]: https://github.com/chrisbanes

[PhotoView]: https://github.com/chrisbanes/PhotoView

[bumptech]: https://github.com/bumptech

[glide]: https://github.com/bumptech/glide

[coil-kt]: https://github.com/coil-kt

[coil]: https://github.com/coil-kt/coil

[wseemann]: https://github.com/wseemann

[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever

[BigBadaboom]: https://github.com/BigBadaboom

[androidsvg]: https://github.com/BigBadaboom/androidsvg

[Kotlin Coroutines]: https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/resources/META-INF/proguard/coroutines.pro

[OkHttp]: https://github.com/square/okhttp/blob/master/okhttp/src/jvmMain/resources/META-INF/proguard/okhttp3.pro

[Okio]: https://github.com/square/okio/blob/master/okio/src/jvmMain/resources/META-INF/proguard/okio.pro


[comment]: <> (footer)

[CHANGELOG.md]: CHANGELOG.md