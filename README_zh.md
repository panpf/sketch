# ![logo_image] Sketch Image Loader

![Platform][platform_image]
[![License][license_image]][license_link]
[![version_icon]][version_link]
![QQ Group][qq_group_image]

翻译：[English](README.md)

Sketch 是一个专为 Compose Multiplatform 和 Android View 设计的图片加载库，它有以下特点：

* `多加载源`：支持从 http、file、compose.resource、android asset/content/resource 等多种来源加载图片
* `功能强大`：支持三级缓存、自动取消请求、自动调整图片尺寸等功能、自动根据 Exif Orientation 旋转图片
* `功能丰富`：支持动图、SVG 图片、Base64 图片、视频帧
* `易于扩展`：支持对缓存、解码、转换、过渡、占位图等各个环节的扩展
* `扩展功能`：提供蜂窝流量时暂停下载、列表滚动中暂停加载、图片类型徽章、下载进度指示器等各种实用扩展
* `现代化`：完全基于 Kotlin 和 Kotlin 协程设计

## 多平台支持

| 功能/平台            | Android | iOS | Desktop | Web |
|:-----------------|---------|:----|:--------|:----|
| 内存缓存             | ✅       | ✅   | ✅       | ✅   |
| 结果缓存             | ✅       | ✅   | ✅       | ✅   |
| 磁盘缓存             | ✅       | ✅   | ✅       | ❌   |
| 动图               | ✅       | ✅   | ✅       | ✅   |
| SVG              | ✅       | ✅   | ✅       | ✅   |
| 视频帧              | ✅       | ❌   | ❌       | ❌   |
| Exif Orientation | ✅       | ✅   | ✅       | ✅   |
| 最低 API           | API 21  | -   | JDK 1.8 | -   |

> 最低 API 是 '-' 表示和 Compose Multiplatform 同步

## 下载

`已发布到 mavenCentral`

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

### Compose Multiplatform:

```kotlin
// 提供了 Sketch 的核心功能以及单例和依赖单例实现的扩展函数
implementation("io.github.panpf.sketch4:sketch-compose:${LAST_VERSION}")
```

> [!IMPORTANT]
> 为提升 compose 的性能请拷贝 `sketch-core` 模块下的 [compose_compiler_config.conf]
> 文件到您的项目中，然后按照  [Compose Stability Configuration][stability_configuration] 文档配置它

### Android View:

```kotlin
// 提供了 Sketch 的核心功能以及单例和依赖单例实现的扩展函数
implementation("io.github.panpf.sketch4:sketch-view:${LAST_VERSION}")
```

还有一些可选的模块用来扩展 sketch 的功能：

```kotlin
dependencies {
    // 通过 Android 或 Skia 内置的解码器实现解码 gif、webp、heif 等动图并播放
    implementation("io.github.panpf.sketch4:sketch-animated:${LAST_VERSION}")

    // [仅 Android] 通过 android-gif-drawable 库的 GifDrawable 实现解码 gif 并播放
    implementation("io.github.panpf.sketch4:sketch-animated-koralgif:${LAST_VERSION}")
    
    // 提供下载进度、列表滚动中暂停加载、节省蜂窝流量、图片类型角标、加载 apk icon 和已安装 app icon 等实用功能
    implementation("io.github.panpf.sketch4:sketch-extensions-compose:${LAST_VERSION}")
    implementation("io.github.panpf.sketch4:sketch-extensions-view:${LAST_VERSION}")

    // [仅 JVM] 支持通过 OkHttp 来下载图片
    implementation("io.github.panpf.sketch4:sketch-http-okhttp:${LAST_VERSION}")

    // [仅 JVM] 支持通过 ktor 来下载图片
    implementation("io.github.panpf.sketch4:sketch-http-ktor:${LAST_VERSION}")

    // 支持 SVG 图片
    implementation("io.github.panpf.sketch4:sketch-svg:${LAST_VERSION}")

    // [仅 Android] 通过 Android 内置的 MediaMetadataRetriever 类实现解码视频帧
    implementation("io.github.panpf.sketch4:sketch-video:${LAST_VERSION}")

    // [仅 Android] 通过 wseemann 的 FFmpegMediaMetadataRetriever 库实现解码视频帧
    implementation("io.github.panpf.sketch4:sketch-video-ffmpeg:${LAST_VERSION}")
}
```

> [!TIP]
> * `sketch-compose`、`sketch-view`、`sketch-extensions-compose`、`sketch-extensions-view`
    模块都依赖 `sketch-singleton` 模块提供的单例，如果你不需要单例则可以直接依赖他们的 `*-core` 版本
> * 在 Android 上 `sketch-compose` 和 `sketch-view` 可以一起使用

#### R8 / Proguard

Sketch 自己不需要配置任何混淆规则，但你可能需要为间接依赖的 [Kotlin Coroutines], [OkHttp], [Okio]
添加混淆配置

## 快速上手

#### Compose Multiplatform

```kotlin
// val imageUri = "/sdcard/download/image.jpg"
// val imageUri = "compose.resource://drawable/sample.png"
val imageUri = "https://www.sample.com/image.jpg"
AsyncImage(
    uri = imageUri,
    modifier = Modifier.size(300.dp, 200.dp),
    contentScale = ContentScale.Crop,
    contentDescription = ""
)

// config params
AsyncImage(
    rqeuest = ImageRequest("https://www.sample.com/image.jpg") {
        placeholder(Res.drawable.placeholder)
        error(Res.drawable.error)
        transformations(BlurTransformation())
        crossfade()
        // There is a lot more...
    },
    modifier = Modifier.size(300.dp, 200.dp),
    contentScale = ContentScale.Crop,
    contentDescription = ""
)
```

#### Android View

```kotlin
// val imageUri = "/sdcard/download/image.jpg"
// val imageUri = "asset://image.jpg"
val imageUri = "https://www.sample.com/image.jpg"
imageView.displayImage(imageUri)

// config params
imageView.displayImage(imageUri) {
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
    transformations(CircleCropTransformation())
    crossfade()
    // There is a lot more...
}
```

## 文档

基础功能：

* [开始使用][getting_started]
* [AnimatedImage：GIF、WEBP、HEIF][animated_image]
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
* [VideoFrames：解码视频帧][video_frame]
* [ExifOrientation：纠正图片方向][exif]
* [ImageOptions：统一管理图片配置][image_options]
* [RequestInterceptor：拦截 ImageRequest][request_interceptor]
* [DecodeInterceptor：拦截 Bitmap 或 Drawable 解码][decode_interceptor]
* [DownloadRequest：下载图片到磁盘][download_request]
* [LoadRequest：加载图片获取 Bitmap][load_request]
* [预加载图片到内存][preloading]
* [Lifecycle][lifecycle]
* [Jetpack Compose][jetpack_compose]
* [日志][log]

特色功能：

* [SketchImageView：通过 XML 属性配置请求][sketch_image_view]
* [提高长图在网格列表中的清晰度][long_image_grid_thumbnails]
* [显示下载进度][download_progress_indicator]
* [显示图片类型角标][mime_type_logo]
* [蜂窝数据网络下暂停下载图片节省流量][save_cellular_traffic]
* [列表滑动中暂停加载图片][pause_load_when_scrolling]
* [显示 APK 文件或已安装 APP 的图标][apk_app_icon]

## 更新日志

请查看 [CHANGELOG.md] 文件

## 关于 4.0 版本

* maven groupId 升级为 `io.github.panpf.sketch4`，因此 2.\*、3.\* 版本不会提示升级
* 4.0 版本专为 Compose Multiplatform 打造所以 API 有很多破坏性改动，请谨慎升级
* 4.0 版本做了大量的简化，比 3.0 版本简单很多，例如 DisplayRequest、LoadRequest、DownloadRequest
  合并为一个 ImageRequest，移除了 BitmapPool 等
* Android 最低 API 升到了 API 21

## 特别感谢

* [coil-kt]/[coil]: Sketch 使用了来自 Coil 的部分代码，包括 framework、compose、sketch-animated-movie
  部分
* [koral--]/[android-gif-drawable]: animated-koralgif
* [wseemann]/[FFmpegMediaMetadataRetriever]: video-ffmpeg
* [BigBadaboom]/[androidsvg]: svg

## 我的项目

以下是我的其它开源项目，感兴趣的可以了解一下：

* [zoomimage](https://github.com/panpf/zoomimage)：用于缩放图像的库，支持 Android View 以及 Compose
  Multiplatform；支持双击缩放、单指或双指手势缩放、单指拖动、惯性滑动、定位、旋转、超大图子采样等功能。
* [assembly-adapter](https://github.com/panpf/assembly-adapter)：Android 上的一个为各种 Adapter 提供多类型
  Item 实现的库。还顺带为 RecyclerView 提供了最强大的 divider。
* [sticky-item-decoration](https://github.com/panpf/stickyitemdecoration)：RecyclerView 黏性 item 实现

## License

Apache 2.0. 有关详细信息，请参阅 [LICENSE](LICENSE.txt) 文件.

[comment]: <> (header)

[logo_image]: docs/res/logo.png

[platform_image]: https://img.shields.io/badge/Platform-ComposeMultiplatform-brightgreen.svg

[license_image]: https://img.shields.io/badge/License-Apache%202-blue.svg

[license_link]: https://www.apache.org/licenses/LICENSE-2.0

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/


[qq_group_image]: https://img.shields.io/badge/QQ%E4%BA%A4%E6%B5%81%E7%BE%A4-529630740-red.svg


[comment]: <> (wiki)

[getting_started]: docs/wiki/getting_started_zh.md

[fetcher]: docs/wiki/fetcher_zh.md

[decoder]: docs/wiki/decoder_zh.md

[animated_image]: docs/wiki/animated_image_zh.md

[resize]: docs/wiki/resize_zh.md

[transformation]: docs/wiki/transformation_zh.md

[transition]: docs/wiki/transition_zh.md

[state_image]: docs/wiki/state_image_zh.md

[listener]: docs/wiki/listener_zh.md

[cache]: docs/wiki/cache_zh.md

[target]: docs/wiki/target_zh.md

[http_stack]: docs/wiki/http_stack_zh.md

[svg]: docs/wiki/svg_zh.md

[video_frame]: docs/wiki/video_frame_zh.md

[exif]: docs/wiki/exif_zh.md

[image_options]: docs/wiki/image_options_zh.md

[request_interceptor]: docs/wiki/request_interceptor_zh.md

[decode_interceptor]: docs/wiki/decode_interceptor_zh.md

[preloading]: docs/wiki/preloading_zh.md

[download_request]: docs/wiki/download_request_zh.md

[load_request]: docs/wiki/load_request_zh.md

[long_image_grid_thumbnails]: docs/wiki/long_image_grid_thumbnails_zh.md

[mime_type_logo]: docs/wiki/mime_type_logo_zh.md

[download_progress_indicator]: docs/wiki/download_progress_indicator_zh.md

[sketch_image_view]: docs/wiki/sketch_image_view_zh.md

[save_cellular_traffic]: docs/wiki/save_cellular_traffic_zh.md

[pause_load_when_scrolling]: docs/wiki/pause_load_when_scrolling_zh.md

[apk_app_icon]: docs/wiki/apk_app_icon_zh.md

[log]: docs/wiki/log_zh.md

[lifecycle]: docs/wiki/lifecycle_zh.md

[jetpack_compose]: docs/wiki/jetpack_compose_zh.md


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

[compose_compiler_config.conf]: sketch-core/compose_compiler_config.conf

[stability_configuration]: https://developer.android.google.cn/develop/ui/compose/performance/stability/fix#configuration-file


[comment]: <> (footer)

[CHANGELOG.md]: CHANGELOG_zh.md