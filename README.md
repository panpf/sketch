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
* 3.0 版本参考 [coil][coil] 并结合 sketch 原有功能实现，相较于 [coil][coil] sketch 最低支持到 API 16，而 [coil][coil] 是 21

## 简介

* 支持 http、asset、content、drawable 等多种 URI
* 支持播放 gif、webp、heif 等动图
* 支持手势缩放及分块显示超大图片
* 支持下载、转换结果、内存三级缓存
* 支持通过 ExifInterface 纠正图片方向
* 支持 Base64、视频帧、SVG 图片
* 支持 Jetpack Compose
* 支持根据 view 大小自动调整图片尺寸
* 支持仅加载图片到内存或仅下载图片到磁盘
* 支持节省蜂窝流量等各种实用功能
* 支持对 URI、缓存、解码、转换、显示、占位图等各个环节的扩展
* 基于 Kotlin 及 Kotlin 协程编写

## 导入

`该库已发布到 mavenCentral`

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
imageView.dislplayImage("https://www.sample.com/image.jpg")

// File
imageView.dislplayImage("/sdcard/download/image.jpeg")

// asset
imageView.dislplayImage("asset://image.jpg")

// There is a lot more...
```

还可以通过尾随的 lambda 函数配置请求：

```kotlin
imageView.dislplayImage("https://www.sample.com/image.jpg") {
    placeholderImage(R.drawable.placeholder)
    errorImage(R.drawable.error)
    transformations(CircleCropTransformation())
    crossfadeTransition()
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
    placeholderImage(R.drawable.placeholder)
    errorImage(R.drawable.error)
    transformations(CircleCropTransformation())
    crossfadeTransition()
    // There is a lot more...
}
```

## 文档

* 如何配置 Sketch
* 如何配置图片请求
* 支持的 URI 类型及自定义
* 支持的图片类型及自定义
* 播放 GIF、webp、heif 等动图
* 使用手势缩放及分块显示超大图功能
* 使用 resize 修改图片尺寸
* 使用 Transformation 转换图片
* 使用 Transition 用不同的效果显示图片
* 设置占位图和错误图片
* 监听请求状态和下载进度
* 配置下载、转换结果、内存三级缓存
* 显示 svg 图片和视频帧
* 将 http 网络部分替换成 okhttp

特色小功能
* 使用 SketchImageView 显示下载进度、图片类型角标
* 使用手势缩放功能的阅读模式提升体验
* 使用 resize 的长图裁剪功能提升超大图片在列表中的清晰度
* 蜂窝数据网络下暂停下载图片节省流量
* 列表滑动时暂停加载图片，提升列表滑动流畅度
* 仅加载图片到内存或仅下载图片到磁盘
* 显示 apk 文件或已安装 app 的图标

[comment]: <> (基础功能：)

[comment]: <> (* [URI 类型及使用指南][uri])

[comment]: <> (* [SketchImageView 使用指南][sketch_image_view])

[comment]: <> (* [使用 Options 配置图片][options])

[comment]: <> (* [播放 GIF 图片][play_gif_image])

[comment]: <> (* [手势缩放、旋转图片][zoom])

[comment]: <> (* [分块显示超大图片][block_display])

[comment]: <> (* [使用 ShapeSize 在绘制时改变图片的尺寸][shape_size])

[comment]: <> (* [使用 ImageShaper 在绘制时改变图片的形状][image_shaper])

[comment]: <> (* [使用 ImageProcessor 在解码后改变图片][image_processor])

[comment]: <> (* [使用 ImageDisplayer 以动画的方式显示图片][image_displayer])

[comment]: <> (* [使用 MaxSize 读取合适尺寸的缩略图，节省内存][max_size])

[comment]: <> (* [使用 Resize 精确修改图片的尺寸][resize])

[comment]: <> (* [使用 StateImage 设置占位图片和状态图片][state_image])

[comment]: <> (* [监听开始、成功、失败以及下载进度][listener])

[comment]: <> (提升用户体验：)

[comment]: <> (* [使用 TransitionImageDisplayer 以自然过渡渐的变方式显示图片][transition_image_displayer])

[comment]: <> (* [使用 thumbnailMode 属性显示更清晰的缩略图][thumbnail_mode])

[comment]: <> (* [使用 cacheProcessedImageInDisk 属性缓存需要复杂处理的图片，提升显示速度][cache_processed_image_in_disk])

[comment]: <> (* [使用 MemoryCacheStateImage 先显示已缓存的较模糊的图片，然后再显示清晰的图片][memory_cache_state_image])

[comment]: <> (* [移动数据或有流量限制的 WIFI 下暂停下载图片，节省流量][pause_download])

[comment]: <> (* [列表滑动时暂停加载图片，提升列表滑动流畅度][pause_load])

[comment]: <> (更多：)

[comment]: <> (* [UriModel 详解及扩展 URI][uri_model])

[comment]: <> (* [统一修改 Options][options_filter])

[comment]: <> (* [显示视频缩略图][display_video_thumbnail])

[comment]: <> (* [管理多个 Options][options_manage])

[comment]: <> (* [只加载或下载图片][load_and_download])

[comment]: <> (* [显示 APK 或已安装 APP 的图标][display_apk_or_app_icon])

[comment]: <> (* [自动纠正图片方向][correct_image_orientation])

[comment]: <> (* [复用 Bitmap 降低 GC 频率，减少卡顿][bitmap_pool])

[comment]: <> (* [在内存中缓存 Bitmap 提升显示速度][memory_cache])

[comment]: <> (* [在磁盘上缓存图片原文件，避免重复下载][disk_cache])

[comment]: <> (* [发送 HTTP 请求][http_stack])

[comment]: <> (* [取消请求][cancel_request])

[comment]: <> (* [监控 Sketch 的异常][error_tracker])

[comment]: <> (* [日志][log])

[comment]: <> (* [延迟并统一配置 Sketch][initializer])

[comment]: <> (* [配置混淆（Proguard）][proguard_config])

## 示例 APP

![sample_app_download_qrcode]

扫描二维码下载或[点我下载][sample_app_download_link]

## 更新日志

请查看 [CHANGELOG.md] 文件

## 特别感谢

* [coil-kt]/[coil]: framework、compose
* [bumptech]/[glide]: BitmapPool
* [chrisbanes]/[PhotoView]: Zoom
* [koral--]/[android-gif-drawable]: gif-koral
* [wseemann]/[FFmpegMediaMetadataRetriever]: video-ffmpeg

## 交流群

![QQ Group][qq_group_image]

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

[logo_image]: docs/res/logo.png

[platform_image]: https://img.shields.io/badge/Platform-Android-brightgreen.svg

[license_image]: https://img.shields.io/badge/License-Apache%202-blue.svg

[license_link]: https://www.apache.org/licenses/LICENSE-2.0

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch3/sketch

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch/

[min_api_image]: https://img.shields.io/badge/API-16%2B-orange.svg

[min_api_link]: https://android-arsenal.com/api?level=16

[qq_group_image]: https://img.shields.io/badge/QQ%E4%BA%A4%E6%B5%81%E7%BE%A4-529630740-red.svg

[CHANGELOG.md]: CHANGELOG.md

[sample_app_download_qrcode]: docs/sketch-sample.png

[sample_app_download_link]: https://github.com/panpf/sketch/raw/master/docs/sketch-sample.apk

[UriModel]: sketch/src/main/java/com/github/panpf/sketch/uri/UriModel.java

[uri]: docs/wiki/uri.md

[sketch_image_view]: docs/wiki/sketch_image_view.md

[options]: docs/wiki/options.md

[options_manage]: docs/wiki/options_manage.md

[load_and_download]: docs/wiki/load_and_download.md

[play_gif_image]: docs/wiki/play_gif_image.md

[zoom]: docs/wiki/zoom.md

[block_display]: docs/wiki/block_display.md

[shape_size]: docs/wiki/shape_size.md

[image_shaper]: docs/wiki/image_shaper.md

[image_processor]: docs/wiki/image_processor.md

[image_displayer]: docs/wiki/image_displayer.md

[max_size]: docs/wiki/max_size.md

[resize]: docs/wiki/resize.md

[state_image]: docs/wiki/state_image.md

[transition_image_displayer]: docs/wiki/transition_image_displayer.md

[thumbnail_mode]: docs/wiki/thumbnail_mode.md

[cache_processed_image_in_disk]: docs/wiki/cache_processed_image_in_disk.md

[pause_download]: docs/wiki/pause_download.md

[pause_load]: docs/wiki/pause_load.md

[display_apk_or_app_icon]: docs/wiki/display_apk_or_app_icon.md

[memory_cache_state_image]: docs/wiki/memory_cache_state_image.md

[uri_model]: docs/wiki/uri_model.md

[display_video_thumbnail]: docs/wiki/display_video_thumbnail.md

[correct_image_orientation]: docs/wiki/correct_image_orientation.md

[bitmap_pool]: docs/wiki/bitmap_pool.md

[memory_cache]: docs/wiki/memory_cache.md

[disk_cache]: docs/wiki/disk_cache.md

[http_stack]: docs/wiki/http_stack.md

[listener]: docs/wiki/listener.md

[cancel_request]: docs/wiki/cancel_request.md

[error_tracker]: docs/wiki/error_tracker.md

[log]: docs/wiki/log.md

[initializer]: docs/wiki/initializer.md

[proguard_config]: docs/wiki/proguard_config.md

[options_filter]: docs/wiki/options_filter.md

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

[Kotlin Coroutines]: https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/resources/META-INF/proguard/coroutines.pro

[OkHttp]: https://github.com/square/okhttp/blob/master/okhttp/src/jvmMain/resources/META-INF/proguard/okhttp3.pro

[Okio]: https://github.com/square/okio/blob/master/okio/src/jvmMain/resources/META-INF/proguard/okio.pro