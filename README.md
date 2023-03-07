# ![logo_image] Sketch Image Loader

![Platform][platform_image]
[![API][min_api_image]][min_api_link]
[![License][license_image]][license_link]
[![version_icon]][version_link]
![QQ Group][qq_group_image]

Sketch is a powerful and comprehensive image load library on Android, in addition to the basic
functions, it also supports Jetpack Compose, GIF, SVG, video thumbnails, gesture zoom, huge images
sampling, ExifInterface and other functions.
<br>
Sketch 是 Android 上的一个强大且全面的图片加载库，除了基础功能外，还支持 Jetpack
Compose、GIF、SVG、视频缩略图、手势缩放、超大图采样、ExifInterface 等功能。

## Features/简介

* Support http, asset, content, android.resource and other URIs
* Support playing GIFs, WebP, HEIF and other animated image
* Support gesture zoom and large image sampling
* Supports download, conversion results, and memory L3 cache
* Support for correcting image orientation via Exif
* Supports Base64, video frames, SVG images
* Support for Jetpack Compose
* Supports automatic resizing of images according to the size of the view
* Supports loading only pictures to memory or downloading only pictures to disk
* Supports various useful features such as saving cellular data
* Support the extension of URI, cache, decoding, conversion, display, placeholder and other links
* Based on Kotlin and Kotlin coroutines

<div>-----------------------</div>

* 支持 http、asset、content、android.resource 等多种 URI
* 支持播放 gif、webp、heif 等动图
* 支持手势缩放及超大图采样
* 支持下载、转换结果、内存三级缓存
* 支持通过 Exif 纠正图片方向
* 支持 Base64、视频帧、SVG 图片
* 支持 Jetpack Compose
* 支持根据 view 大小自动调整图片尺寸
* 支持仅加载图片到内存或仅下载图片到磁盘
* 支持节省蜂窝流量等各种实用功能
* 支持对 URI、缓存、解码、转换、显示、占位图等各个环节的扩展
* 基于 Kotlin 及 Kotlin 协程编写

## Import/导入

`Published to mavenCentral · 已发布到 mavenCentral`

```kotlin
dependencies {
    implementation("io.github.panpf.sketch3:sketch:${LAST_VERSION}")
}
```

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (Not included 'v' · 不包含 'v')

There are also optional modules to extend the functionality of sketch:
<br>
还有一些可选的模块用来扩展 sketch 的功能：

```kotlin
dependencies {
    // Support for Jetpack Compose
    // 支持 Jetpack Compose
    implementation("io.github.panpf.sketch3:sketch-compose:${LAST_VERSION}")

    // Supports useful features such as download progress mask, pause loading during list swipe, save cellular traffic, image type corner marker, load apk file and installed app icon
    // 支持下载进度蒙层、列表滑动中暂停加载、节省蜂窝流量、图片类型角标、加载 apk 文件和已安装 app 图标等实用功能
    implementation("io.github.panpf.sketch3:sketch-extensions:${LAST_VERSION}")

    // GifDrawable through Koral's android-gif-drawable library
    // 通过 koral 的 android-gif-drawable 库的 GifDrawable 实现 gif 播放
    implementation("io.github.panpf.sketch3:sketch-gif-koral:${LAST_VERSION}")

    // GIF playback is achieved through Android's built-in Movie class
    // 通过 Android 内置的 Movie 类实现 gif 播放
    implementation("io.github.panpf.sketch3:sketch-gif-movie:${LAST_VERSION}")

    // Support for OkHttp
    // 支持 OkHttp
    implementation("io.github.panpf.sketch3:sketch-okhttp:${LAST_VERSION}")

    // SVG images are supported
    // 支持 SVG 图片
    implementation("io.github.panpf.sketch3:sketch-svg:${LAST_VERSION}")

    // Video frames are read through Android's built-in MediaMetadataRetriever class
    // 通过 Android 内置的 MediaMetadataRetriever 类实现读取视频帧
    implementation("io.github.panpf.sketch3:sketch-video:${LAST_VERSION}")

    // Video frames are read through wseemann's FFmpegMediaMetadataRetriever library
    // 通过 wseemann 的 FFmpegMediaMetadataRetriever 库实现读取视频帧
    implementation("io.github.panpf.sketch3:sketch-video-ffmpeg:${LAST_VERSION}")

    // Supports gesture zoom and jumbo sampling
    // 支持手势缩放以及超大图采样
    implementation("io.github.panpf.sketch3:sketch-zoom:${LAST_VERSION}")
}
```

#### R8 / Proguard

Sketch doesn't need to configure any obfuscation rules itself, but you may need to add obfuscation configurations for indirectly dependent [Kotlin Coroutines], [OkHttp], [Okio].
<br>
Sketch 自己不需要配置任何混淆规则，但你可能需要为间接依赖的 [Kotlin Coroutines], [OkHttp], [Okio]
添加混淆配置

## Get started/快速上手

#### ImageView

Sketch provides a series of extended functions called displayImage for ImageView, which can easily display images
<br>
Sketch 为 ImageView 提供了一系列的名为 displayImage 的扩展函数，可以方便的显示图片

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
<br>
还可以通过尾随的 lambda 函数配置参数：

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

`Need import sketch-compose module · 需要导入 sketch-compose 模块`

```kotlin
AsyncImage(
  imageUri = "https://www.sample.com/image.jpg",
  modifier = Modifier.size(300.dp, 200.dp),
  contentScale = ContentScale.Crop,
  contentDescription = ""
)

// config params
AsyncImage(
  rqeuest = DisplayRequest(LocalContext.current, "https://www.sample.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
    transformations(BlurTransformation())
    crossfade()
    // There is a lot more...
  },
  modifier = Modifier.size(300.dp, 200.dp),
  contentScale = ContentScale.Crop,
  contentDescription = ""
)
```

## Document/文档

Basic features/基础功能：

* [Started/快速上手][getting_started]
* [AnimatedImage：GIF、WEBP、HEIF][animated_image]
* [Resize：Modify the image size/修改图片尺寸][resize]
* [Transformation：Transformation image/转换图片][transformation]
* [Transition：Display images in cool transitions/用炫酷的过渡方式显示图片][transition]
* [StateImage：Placeholder and error images/占位图和错误图][state_image]
* [Listener：Listen for request status and download progress/监听请求状态和下载进度][listener]
* [Cache：Learn about downloads, results, memory caching/了解下载、结果、内存缓存][cache]
* [Fetcher：Learn about Fetcher and extend new URI types/了解 Fetcher 及扩展新的 URI 类型][fetcher]
* [Decoder：Learn about Decoder and expand into new image types/了解 Decoder 及扩展新的图片类型][decoder]
* [Target：Apply the load results to the target/将加载结果应用到目标上][target]
* [HttpStack：Learn about the HTTP section and using okhttp/了解 http 部分及使用 okhttp][http_stack]
* [SVG：Decode SVG still images/解码 SVG 静态图片][svg]
* [VideoFrames：Decode video frames/解码视频帧][video_frame]
* [Exif：Correct the image orientation/纠正图片方向][exif]
* [ImageOptions：Manage image configurations in a unified manner/统一管理图片配置][image_options]
* [RequestInterceptor：Intercept ImageRequest/拦截 ImageRequest][request_interceptor]
* [DecodeInterceptor：Intercept Bitmap or Drawable decoding/拦截 Bitmap 或 Drawable 解码][decode_interceptor]
* [BitmapPool：Reuse Bitmap to reduce GC/复用 Bitmap，减少 GC][bitmap_pool]
* [DownloadRequest：Download the image to disk/下载图片到磁盘][download_request]
* [LoadRequest：Load the image to get the Bitmap/加载图片获取 Bitmap][load_request]
* [Preload images into memory/预加载图片到内存][preloading]
* [Lifecycle][lifecycle]
* [Jetpack Compose][jetpack_compose]

Featured features/特色功能：

* [SketchImageView：Configure the request through XML attributes/通过 XML 属性配置请求][sketch_image_view]
* [SketchZoomImageView：Gesture zoom and large image sampling/手势缩放及超大图采样][zoom]
* [Improve the clarity of long images in grid lists/提高长图在网格列表中的清晰度][long_image_grid_thumbnails]
* [Displays the download progress/显示下载进度][show_download_progress]
* [Displays the image type corner/显示图片类型角标][show_image_type]
* [Pause image downloads on cellular data to save data/蜂窝数据网络下暂停下载图片节省流量][save_cellular_traffic]
* [The list slides to pause the loading of images/列表滑动中暂停加载图片][pause_load_when_scrolling]
* [Displays an icon for an apk file or installed app/显示 APK 文件或已安装 APP 的图标][apk_app_icon]
* [Log/日志][log]

## Changelog/更新日志

Please review the [CHANGELOG.md] file
<br>
请查看 [CHANGELOG.md] 文件

### About version 3.0/关于 3.0 版本

* The maven groupId was changed to 'io.github.panpf.sketch3', so version 2.\* will not prompt for an upgrade
* The package name was changed to 'com.github.panpf.sketch' so it does not conflict with version 2.\*
* Based on the kotlin coroutine rewrite, APIs and functions are all refactored as a new library
* There is no longer a requirement to use a SketchImageView, any ImageView and its subclasses will do, and any View can be supported in combination with a custom Target
* The Zoom function is split into independent modules that can be relied on separately, and the large image sampling function is refactored and supports multi-threaded decoding, which is faster
* The gif module now directly depends on the [android-gif-drawable] library, no longer modified twice, and can be upgraded by itself
* Support for Jetpack Compose
* Support for request and decode interceptors
* Referring to [coil] v2.2.0 and combining with the original functionality of sketch, there are the following differences compared to [coil]:
  * sketch supports a minimum of API 16, while [coil] supports only API 21
  * Sketch supports bitmap reuse, while [coil] does not
  * Sketch supports more granular resizing of images
  * sketch clearly distinguishes between display, load, and download requests
  * sketch provides image scaling and display components and supports large image sampling

<div>-----------------------</div>

* maven groupId 改为 `io.github.panpf.sketch3`，因此 2.\* 版本不会提示升级
* 包名改为 `com.github.panpf.sketch` 因此与 2.\* 版本不会冲突
* 基于 kotlin 协程重写，API、功能实现全部重构，当一个新的库用
* 不再要求必须使用 SketchImageView，任何 ImageView 及其子类都可以，结合自定义 Target 可以支持任意 View
* Zoom 功能拆分成独立的可单独依赖的模块并且超大图采样功能重构且支持多线程解码速度更快
* gif 模块现在直接依赖 [android-gif-drawable] 库不再二次修改，可自行升级
* 支持 Jetpack Compose
* 支持请求和解码拦截器
* 参考 [coil] v2.2.0 版本并结合 sketch 原有功能实现，对比 [coil] 有以下区别：
  * sketch 最低支持 API 16，而 [coil] 最低仅支持 API 21
  * sketch 支持 bitmap 复用，而 [coil] 不支持
  * sketch 支持更加精细化的调整图片大小
  * sketch 明确区分显示、加载、下载请求
  * sketch 提供了图片缩放显示组件并且支持超大图采样

## Special thanks/特别感谢

* [coil-kt]/[coil]: framework、compose
* [bumptech]/[glide]: BitmapPool
* [chrisbanes]/[PhotoView]: Zoom
* [koral--]/[android-gif-drawable]: gif-koral
* [wseemann]/[FFmpegMediaMetadataRetriever]: video-ffmpeg
* [BigBadaboom]/[androidsvg]: svg

## License

    Copyright (C) 2022 panpf <panpfpanpf@outlook.com>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[comment]: <> (header)

[logo_image]: docs/res/logo.png

[platform_image]: https://img.shields.io/badge/Platform-Android-brightgreen.svg

[license_image]: https://img.shields.io/badge/License-Apache%202-blue.svg

[license_link]: https://www.apache.org/licenses/LICENSE-2.0

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch3/sketch

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch3/

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

[show_image_type]: docs/wiki/show_image_type.md

[show_download_progress]: docs/wiki/show_download_progress.md

[sketch_image_view]: docs/wiki/sketch_image_view.md

[zoom]: docs/wiki/zoom.md

[save_cellular_traffic]: docs/wiki/save_cellular_traffic.md

[pause_load_when_scrolling]: docs/wiki/pause_load_when_scrolling.md

[apk_app_icon]: docs/wiki/apk_app_icon.md

[log]: docs/wiki/log.md

[Lifecycle]: docs/wiki/lifecycle.md

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
