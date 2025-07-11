# ![logo_image] Sketch Image Loader

![Platform][platform_image]
[![License][license_image]][license_link]
[![version_icon]][version_link]
![QQ Group][qq_group_image]

Translations: [简体中文](README.zh.md)

Sketch is an image loading library specially designed for Compose Multiplatform and Android View. It
has the following features:

* `Multiple sources`: Supports loading images from multiple sources such as http, file,
  compose resource, android asset/content/resource, etc.
* `Powerful functions`: Supports three-level caching, automatically cancels requests, automatically
  adjusts image size, automatically rotates images according to Exif Orientation, etc.
* `Rich functions`: Supports Animated image, SVG images, Base64 images, and video frames
* `Easy to expand`: Supports expansion of various aspects such as caching, decoding, transformation,
  transition, placeholder, etc.
* `Extended functions`: Practical extensions such as pausing downloads when cellular data is
  provided, pausing loading during list scrolling, image type badges, download progress indicators,
  etc.
* `Modern`: Completely based on Kotlin and Kotlin coroutine design

## Sample App

* For Android, iOS, desktop version, and web deployable packages, please go to
  the [Releases](https://github.com/panpf/sketch/releases) page to download.
* Web example：https://panpf.github.io/sketch/app

## Install

`Published to mavenCentral`

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (Not included 'v')

### Compose Multiplatform:

Import the required Compose and network modules:

```kotlin
// Provides the core functions of Sketch as well as singletons and extension 
// functions that rely on singleton implementations
implementation("io.github.panpf.sketch4:sketch-compose:${LAST_VERSION}")

// Provides the ability to load network images
implementation("io.github.panpf.sketch4:sketch-http:${LAST_VERSION}")
```

> [!IMPORTANT]
> To improve the performance of compose, please copy [compose_compiler_config.conf] under
> the `sketch-core` module file to your project and configure it according to
> the [Compose Stability Configuration][stability_configuration] documentation

### Android View:

Import the required View and network modules:

```kotlin
// Provides the core functions of Sketch as well as singletons and extension 
// functions that rely on singleton implementations
implementation("io.github.panpf.sketch4:sketch-view:${LAST_VERSION}")

// Provides the ability to load network images
implementation("io.github.panpf.sketch4:sketch-http:${LAST_VERSION}")
```

### Optional modules:

```kotlin
// Use Android or Skia's built-in decoder to decode gif animations and play them
implementation("io.github.panpf.sketch4:sketch-animated-gif:${LAST_VERSION}")

// [Android only] Use the GifDrawable of the android-gif-drawable library to decode and play gif animations
implementation("io.github.panpf.sketch4:sketch-animated-gif-koral:${LAST_VERSION}")

// [Android only] Android or Skia's built-in decoder decodes heif animations and plays them
implementation("io.github.panpf.sketch4:sketch-animated-heif:${LAST_VERSION}")

// Use Android or Skia's built-in decoder to decode webp animations and play them
implementation("io.github.panpf.sketch4:sketch-animated-webp:${LAST_VERSION}")

// Support accessing compose resources through uri or placeholder, fallback, error, etc.
implementation("io.github.panpf.sketch4:sketch-compose-resources:${LAST_VERSION}")
implementation("io.github.panpf.sketch4:sketch-extensions-compose-resources:${LAST_VERSION}")

// Provides practical functions such as download progress, image type icons, 
//  pausing loading during list scrolling, and saving cellular traffic.
implementation("io.github.panpf.sketch4:sketch-extensions-compose:${LAST_VERSION}")
implementation("io.github.panpf.sketch4:sketch-extensions-view:${LAST_VERSION}")

// [Android only] Support icon loading of apk files via file path 
implementation("io.github.panpf.sketch4:sketch-extensions-apkicon:${LAST_VERSION}")

// [Android only] Support loading icons of installed apps by package name and version code
implementation("io.github.panpf.sketch4:sketch-extensions-appicon:${LAST_VERSION}")

// [JVM only] Support using HttpURLConnection to access network images
implementation("io.github.panpf.sketch4:sketch-http-hurl:${LAST_VERSION}")

// [JVM only] Support using OkHttp to access network images
implementation("io.github.panpf.sketch4:sketch-http-okhttp:${LAST_VERSION}")

// Supports using ktor version 2.x to access network images
implementation("io.github.panpf.sketch4:sketch-http-ktor2:${LAST_VERSION}")

// Supports using ktor version 3.x to access network images
implementation("io.github.panpf.sketch4:sketch-http-ktor3:${LAST_VERSION}")

// Provides koin integration support, instead of sketch-compose and sketch-view, 
// and obtains Sketch instances from koin by default
implementation("io.github.panpf.sketch4:sketch-compose-koin:${LAST_VERSION}")
implementation("io.github.panpf.sketch4:sketch-view-koin:${LAST_VERSION}")

// Support SVG images
implementation("io.github.panpf.sketch4:sketch-svg:${LAST_VERSION}")

// [Android only] Use Android's built-in MediaMetadataRetriever class to decode video frames
implementation("io.github.panpf.sketch4:sketch-video:${LAST_VERSION}")

// [Android only] Decoding video frames using wseemann's FFmpegMediaMetadataRetriever library
implementation("io.github.panpf.sketch4:sketch-video-ffmpeg:${LAST_VERSION}")
```

> [!TIP]
> * `sketch-compose`, `sketch-view` Modules all depend on the singleton provided by
    the `sketch-singleton` module. If you don’t need the singleton, you can directly rely on
    their `*-core` version.
> * The `sketch-http` module depends on `sketch-http-hurl` on jvm platforms and `sketch-http-ktor3`
    on non-jvm platforms.

### Register component

Sketch supports automatic discovery and registration of Fetcher and Decoder components, which are
implemented through ServiceLoader on the JVM platform and through the @EagerInitialization
annotation on non-JVM platforms.

All built-in modules support automatic registration. If you want to disable automatic registration,
please refer to the documentation for manual
registration: [《Register component》][register_component]

### R8 / Proguard

1. Android, iOS, Web and other platforms do not need to configure any obfuscation rules
2. The following obfuscation rules are required for desktop platforms:
    ```proguard
    # -------------------------- Sketch Privider ---------------------------- #
    -keep class * implements com.github.panpf.sketch.util.DecoderProvider { *; }
    -keep class * implements com.github.panpf.sketch.util.FetcherProvider { *; }
    ```
3. It may also be necessary to add obfuscation configurations to the indirect dependencies of
   three-party libraries such as [Kotlin Coroutines], [OkHttp], [Okio]

## Quickly Started

### Compose Multiplatform:

```kotlin
// val imageUri = "/Users/my/Downloads/image.jpg"
// val imageUri = file:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/sample.png
val imageUri = "https://www.sample.com/image.jpg"

AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)

AsyncImage(
    uri = imageUri,
    state = rememberAsyncImageState(ComposableImageOptions {
        placeholder(Res.drawable.placeholder)
        error(Res.drawable.error)
        crossfade()
        // There is a lot more...
    }),
    contentDescription = "photo"
)

AsyncImage(
    rqeuest = ComposableImageRequest(imageUri) {
        placeholder(Res.drawable.placeholder)
        error(Res.drawable.error)
        crossfade()
        // There is a lot more...
    },
    contentDescription = "photo"
)

Image(
    painter = rememberAsyncImagePainter(
        request = ComposableImageRequest(imageUri) {
            placeholder(Res.drawable.placeholder)
            error(Res.drawable.error)
            crossfade()
            // There is a lot more...
        }
    ),
    contentDescription = "photo"
)
```

> [!TIP]
> `placeholder(Res.drawable.placeholder)` needs to import the `sketch-compose-resources` module

### Android View:

```kotlin
// val imageUri = "/sdcard/download/image.jpg"
// val imageUri = "file:///android_asset/image.jpg"
// val imageUri = "content://media/external/images/media/88484"
val imageUri = "https://www.sample.com/image.jpg"

imageView.loadImage(imageUri)

imageView.loadImage(imageUri) {
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

## Documents

Basic functions:

* [Get Started][getting_started]
* [Register component][register_component]
* [Compose][compose]
* [Http: Load network images][http]
* [AnimatedImage: GIF、WEBP、HEIF][animated_image]
* [Resize: Modify the image size][resize]
* [Transformation: Transformation image][transformation]
* [Transition: Display images in cool transitions][transition]
* [StateImage: Placeholder and error images][state_image]
* [Listener: Listen for request status and download progress][listener]
* [DownloadCache: Understand download caching to avoid repeated downloads][download_cache]
* [ResultCache: Understand result caching to avoid duplicate conversions][result_cache]
* [MemoryCache: Understand memory caching to avoid repeated loading][memory_cache]
* [Fetcher: Learn about Fetcher and extend new URI types][fetcher]
* [Decoder: Understand the decoding process of Sketch][decoder]
* [Target: Apply the load results to the target][target]
* [SVG: Decode SVG still images][svg]
* [VideoFrames: Decode video frames][video_frame]
* [ExifOrientation: Correct the image orientation][exif_orientation]
* [ImageOptions: Manage image configurations in a unified manner][image_options]
* [RequestInterceptor: Intercept ImageRequest][request_interceptor]
* [DecodeInterceptor: Intercept the decoding process][decode_interceptor]
* [Preload images into memory][preload]
* [Download images][download]
* [Lifecycle][lifecycle]
* [Log][log]
* [Migrate][migrate]

Featured functions:

* [SketchImageView: Configure the request through XML attributes][sketch_image_view]
* [Improve the clarity of long images in grid lists][long_image_grid_thumbnails]
* [Displays the download progress][progress_indicator]
* [Displays the image type corner][mime_type_logo]
* [Pause image downloads on cellular data to save data][save_cellular_traffic]
* [The list slides to pause the loading of images][pause_load_when_scrolling]
* [Displays an icon for an apk file or installed app][apk_app_icon]

## Change log

Please review the [CHANGELOG.md] file

## Test Platform

* Android: Emulator; Arm64; API 21-34
* Desktop: macOS; 14.6.1; JDK 17
* iOS: iphone 16 simulator; iOS 18.1
* Web: Chrome; 130

## Run Sample App

Prepare the environment:

1. Android Studio: Norwhal+ (2025.1.1+)
2. JDK: 17+
3. Use [kdoctor] to check the running environment and follow the prompts to install the required
   software
4. Android Studio installs the `Kotlin Multiplatform` plugin

Run the sample app:

1. Clone the project and open it using Android Studio
2. After synchronization is completed, the `Kotlin Multiplatform` plug-in will automatically create
   a running configuration for each platform.
3. Select the corresponding platform's running configuration, and then click Run

## About version 4.0

* The maven groupId is upgraded to `io.github.panpf.sketch4`, so versions 2.\* and 3.\* will not
  prompt for upgrade
* Version 4.0 is specially built for Compose Multiplatform, so there are many breaking changes in
  the API, please upgrade with caution
* Version 4.0 has made a lot of simplifications and is much simpler than version 3.0, please check
  the update log for details
* Android minimum API raised to API 21
* Kotlin version upgraded to 2.0.0

## Special thanks

* [coil-kt/coil][coil]: Sketch uses some code from Coil, including framework, compose and
  sketch-animated movie part
* [koral--/android-gif-drawable][android-gif-drawable]: animated-koralgif
* [wseemann/FFmpegMediaMetadataRetriever][FFmpegMediaMetadataRetriever]: video-ffmpeg
* [BigBadaboom/androidsvg][androidsvg]: svg

## My Projects

The following are my other open source projects. If you are interested, you can learn about them:

* [zoomimage](https://github.com/panpf/zoomimage): Library for zoom images, supported Android View,
  Compose and Compose Multiplatform; supported double-click zoom, One or two fingers gesture zoom,
  single-finger drag, inertial sliding, positioning, rotation, super-large image subsampling and
  other functions.
* [assembly-adapter](https://github.com/panpf/assembly-adapter): A library on Android that provides
  multi-type Item implementations for various adapters. Incidentally, it also provides the most
  powerful divider for RecyclerView.
* [sticky-item-decoration](https://github.com/panpf/stickyitemdecoration): RecyclerView sticky item
  implementation

## License

Apache 2.0. See the [LICENSE](LICENSE.txt) file for details.

[comment]: <> (header)

[license_image]: https://img.shields.io/badge/License-Apache%202-blue.svg

[logo_image]: docs/images/logo.png

[license_link]: https://www.apache.org/licenses/LICENSE-2.0

[platform_image]: https://img.shields.io/badge/Platform-ComposeMultiplatform-brightgreen.svg

[qq_group_image]: https://img.shields.io/badge/QQ%E4%BA%A4%E6%B5%81%E7%BE%A4-529630740-red.svg

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/


[comment]: <> (wiki)

[animated_image]: docs/animated_image.md

[apk_app_icon]: docs/apk_app_icon.md

[compose]: docs/compose.md

[decoder]: docs/decoder.md

[download_cache]: docs/download_cache.md

[exif_orientation]: docs/exif_orientation.md

[fetcher]: docs/fetcher.md

[getting_started]: docs/getting_started.md

[register_component]: docs/register_component.md

[http]: docs/http.md

[image_options]: docs/image_options.md

[lifecycle]: docs/lifecycle.md

[listener]: docs/listener.md

[log]: docs/log.md

[long_image_grid_thumbnails]: docs/long_image_grid_thumbnails.md

[memory_cache]: docs/memory_cache.md

[mime_type_logo]: docs/mime_type_logo.md

[pause_load_when_scrolling]: docs/pause_load_when_scrolling.md

[preload]: docs/preload.md

[download]: docs/download_image.md

[progress_indicator]: docs/progress_indicator.md

[request_interceptor]: docs/request_interceptor.md

[decode_interceptor]: docs/decode_interceptor.md

[resize]: docs/resize.md

[result_cache]: docs/result_cache.md

[save_cellular_traffic]: docs/save_cellular_traffic.md

[sketch_image_view]: docs/sketch_image_view.md

[state_image]: docs/state_image.md

[svg]: docs/svg.md

[target]: docs/target.md

[transformation]: docs/transformation.md

[transition]: docs/transition.md

[video_frame]: docs/video_frame.md

[migrate]: docs/migrate.md


[comment]: <> (links)


[androidsvg]: https://github.com/BigBadaboom/androidsvg

[android-gif-drawable]: https://github.com/koral--/android-gif-drawable

[coil]: https://github.com/coil-kt/coil

[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever


[Kotlin Coroutines]: https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/resources/META-INF/proguard/coroutines.pro

[OkHttp]: https://square.github.io/okhttp/features/r8_proguard/

[Okio]: https://square.github.io/okio/


[compose_compiler_config.conf]: sketch-core/compose_compiler_config.conf

[stability_configuration]: https://developer.android.com/develop/ui/compose/performance/stability/fix#configuration-file


[comment]: <> (footer)

[CHANGELOG.md]: CHANGELOG.md

[kdoctor]: https://github.com/Kotlin/kdoctor
