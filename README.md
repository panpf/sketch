# ![logo_image] Sketch Image Loader

![Platform][platform_image]
[![API][min_api_image]][min_api_link]
[![License][license_image]][license_link]
[![version_icon]][version_link]
![QQ Group][qq_group_image]

Sketch 是 Android 上的一个强大且全面的图片加载库，除了基础功能外，还支持 GIF、SVG，手势缩放、分块显示超大图片、ExifInterface、视频缩略图、Jetpack
Compose 等功能

## 关于 3.0 版本

* 3.0 版本全部用 kotlin 重写，并且 maven groupId 和包名已经变更所以与 2.0 版本完全不冲突，两者可以共存
* 3.0 版本参考 [coil][coil] 2.0.0-alpha05 版本并结合 sketch 原有功能实现，相较于 [coil][coil] sketch 最低支持到 API
  16，而 [coil][coil] 是 21

## 简介

* 支持 http、asset、content、drawable 等多种 URI
* 支持播放 gif、webp、heif 等动图
* 支持手势缩放及分块显示超大图片
* 支持下载、转换结果、内存三级缓存
* 支持通过 Exif 纠正图片方向
* 支持 Base64、视频帧、SVG 图片
* 支持 Jetpack Compose
* 支持根据 view 大小自动调整图片尺寸
* 支持仅加载图片到内存或仅下载图片到磁盘
* 支持节省蜂窝流量等各种实用功能
* 支持对 URI、缓存、解码、转换、显示、占位图等各个环节的扩展
* 基于 Kotlin 及 Kotlin 协程编写

## 导入

`已发布到 mavenCentral`

```kotlin
dependencies {
    implementation("io.github.panpf.sketch3:sketch:${LAST_VERSION}")
}
```

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

还有一些可选的模块用来扩展 sketch 的功能：

```kotlin
dependencies {
    // 支持 Jetpack Compose
    implementation("io.github.panpf.sketch3:sketch-compose:${LAST_VERSION}")

    // 支持下载进度蒙层、列表滑动中暂停加载、节省蜂窝流量、图片类型角标、加载 apk 文件和已安装 app 图标等实用功能
    implementation("io.github.panpf.sketch3:sketch-extensions:${LAST_VERSION}")

    // 通过 koral--/android-gif-drawable 库的 GifDrawable 实现 gif 播放
    implementation("io.github.panpf.sketch3:sketch-gif-koral:${LAST_VERSION}")

    // 通过 Android 内置的 Movie 类实现 gif 播放
    implementation("io.github.panpf.sketch3:sketch-gif-movie:${LAST_VERSION}")

    // 支持 OkHttp
    implementation("io.github.panpf.sketch3:sketch-okhttp:${LAST_VERSION}")

    // 支持 SVG 图片
    implementation("io.github.panpf.sketch-svg:${LAST_VERSION}")

    // 通过 Android 内置的 MediaMetadataRetriever 类实现读取视频帧 
    implementation("io.github.panpf.sketch-video:${LAST_VERSION}")

    // 通过 wseemann 的 FFmpegMediaMetadataRetriever 库实现读取视频帧
    implementation("io.github.panpf.sketch-video-ffmpeg:${LAST_VERSION}")

    // 支持手势缩放显示图片以及分块显示超大图片
    implementation("io.github.panpf.sketch3:sketch-zoom:${LAST_VERSION}")
}
```

#### R8 / Proguard

sketch 自己不需要配置任何混淆规则，但你可能需要为间接依赖的 [Kotlin Coroutines], [OkHttp], [Okio] 配置一些规则

## 快速上手

#### ImageView

```kotlin
// url
imageView.displayImage("https://www.sample.com/image.jpg")

// File
imageView.displayImage("/sdcard/download/image.jpeg")

// asset
imageView.displayImage("asset://image.jpg")

// There is a lot more...
```

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

需要先导入 `sketch-compose` 模块

```kotlin
AsyncImage(
    imageUri = "https://www.sample.com/image.jpg",
    modifier = Modifier.size(300.dp, 200.dp),
    contentScale = ContentScale.Crop,
    contentDescription = ""
) {
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
    transformations(CircleCropTransformation())
    crossfade()
    // There is a lot more...
}
```

## 文档

基础功能：

* [入门][getting_started]
* [播放 GIF、WEBP、HEIF 动图][animated_image]
* [Resize：修改图片尺寸][resize]
* [Transformation：转换图片][transformation]
* [Transition：用炫酷的过渡方式显示图片][transition]
* [StateImage：占位图和错误图][state_image]
* [Listener：监听请求状态和下载进度][listener]
* [Cache：了解下载、结果、内存缓存][cache]
* [Fetcher：了解 Fetcher 及扩展新的 URI 类型][fetcher]
* [Decoder：了解 Decoder 及扩展新的图片类型][decoder]
* [Target：将加载结果应用到目标上][target]
* [HttpStack：了解 http 部分及使用 okhttp][http_stack]
* [SVG：解码 SVG 静态图片][svg]
* [Video：解码视频帧][video_frame]
* [Exif：纠正图片方向][exif]
* [ImageOptions：统一管理图片配置][image_options]
* [RequestInterceptor：拦截 ImageRequest][request_interceptor]
* [DecodeInterceptor：拦截 Bitmap 或 Drawable 解码][decode_interceptor]
* [BitmapPool：复用 Bitmap，减少 GC][bitmap_pool]
* [DownloadRequest：下载图片到磁盘][download_request]
* [LoadRequest：加载图片获取 Bitmap][load_request]
* [预加载图片到内存][preloading]

特色功能：

* [SketchZoomImageView：手势缩放及分块显示超大图][zoom]
* [提高长图在网格列表中的清晰度][long_image_grid_thumbnails_]
* [显示下载进度][show_download_progress_]
* [显示图片类型角标][show_image_type_]
* [蜂窝数据网络下暂停下载图片节省流量][save_cellular_traffic_]
* [列表滑动时暂停加载图片，提升列表滑动流畅度][pause_load_when_scrolling_]
* [显示 apk 文件或已安装 app 的图标][apk_app_icon_]
* [查看日志][log_]

[comment]: <> (## 示例 APP)

[comment]: <> (![sample_app_download_qrcode])

[comment]: <> (扫描二维码下载或[点我下载][sample_app_download_link])

## 更新日志

请查看 [CHANGELOG.md] 文件

## 特别感谢

* [coil-kt]/[coil]: framework、compose
* [bumptech]/[glide]: BitmapPool
* [chrisbanes]/[PhotoView]: Zoom
* [koral--]/[android-gif-drawable]: gif-koral
* [wseemann]/[FFmpegMediaMetadataRetriever]: video-ffmpeg
* [BigBadaboom]/[androidsvg]: svg

## License

    Copyright (C) 2019 panpf <panpfpanpf@outlook.com>

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

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch/

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

[zoom]: docs/wiki/zoom.md

[save_cellular_traffic]: docs/wiki/save_cellular_traffic.md

[pause_load_when_scrolling]: docs/wiki/pause_load_when_scrolling.md

[apk_app_icon]: docs/wiki/apk_app_icon.md

[log]: docs/wiki/log.md


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

[sample_app_download_qrcode]: docs/sketch-sample.png

[sample_app_download_link]: https://github.com/panpf/sketch/raw/master/docs/sketch-sample.apk