# ![logo_image] Sketch Image Loader

![Platform][platform_image]
[![API][min_api_image]][min_api_link]
[![License][license_image]][license_link]
[![version_icon]][version_link]
![QQ Group][qq_group_image]

翻译：[English](README.md)

Sketch 是 Android 上的一个强大且全面的图片加载库，除了基础功能外，还支持 Jetpack
Compose、GIF、SVG、视频缩略图、超大图采样、ExifInterface 等功能。

## 特点

* 支持 http、asset、content、android.resource 等多种 URI
* 支持播放 gif、webp、heif 等动图
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
    // 提供了 Sketch 的核心功能以及单例和依赖此单例实现的一些便捷的扩展函数，
    // 如果不需要单例可以使用 sketch-core 模块
    implementation("io.github.panpf.sketch3:sketch:${LAST_VERSION}")
}
```

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

还有一些可选的模块用来扩展 sketch 的功能：

```kotlin
dependencies {
    // 支持 Jetpack Compose
    // 它依赖 sketch 模块提供的单例，如果不需要单例模式可以使用 sketch-compose-core 模块
    implementation("io.github.panpf.sketch3:sketch-compose:${LAST_VERSION}")

    // 为 View 提供下载进度、列表滑动中暂停加载、节省蜂窝流量、图片类型角标、加载 apk 文件和已安装 app 图标等实用功能
    // 它依赖 sketch 模块提供的单例，如果不需要单例模式可以使用 sketch-view-core 模块
    implementation("io.github.panpf.sketch3:sketch-extensions-view:${LAST_VERSION}")

    // 为 Compose 提供下载进度、列表滑动中暂停加载、节省蜂窝流量、图片类型角标、加载 apk 文件和已安装 app 图标等实用功能
    implementation("io.github.panpf.sketch3:sketch-extensions-compose:${LAST_VERSION}")

    // 通过 Android 内置的 ImageDecoder 和 Movie 类实现 gif 播放
    implementation("io.github.panpf.sketch3:sketch-gif:${LAST_VERSION}")

    // 通过 koral 的 android-gif-drawable 库的 GifDrawable 实现 gif 播放
    implementation("io.github.panpf.sketch3:sketch-gif-koral:${LAST_VERSION}")

    // 支持 OkHttp
    implementation("io.github.panpf.sketch3:sketch-okhttp:${LAST_VERSION}")

    // 支持 SVG 图片
    implementation("io.github.panpf.sketch3:sketch-svg:${LAST_VERSION}")

    // 通过 Android 内置的 MediaMetadataRetriever 类实现读取视频帧
    implementation("io.github.panpf.sketch3:sketch-video:${LAST_VERSION}")

    // 通过 wseemann 的 FFmpegMediaMetadataRetriever 库实现读取视频帧
    implementation("io.github.panpf.sketch3:sketch-video-ffmpeg:${LAST_VERSION}")
}
```

#### R8 / Proguard

Sketch 自己不需要配置任何混淆规则，但你可能需要为间接依赖的 [Kotlin Coroutines], [OkHttp], [Okio]
添加混淆配置

## 快速上手

#### ImageView

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

> [!IMPORTANT]
> 必须导入 `sketch-compose` 模块

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
* [Exif：纠正图片方向][exif]
* [ImageOptions：统一管理图片配置][image_options]
* [RequestInterceptor：拦截 ImageRequest][request_interceptor]
* [DecodeInterceptor：拦截 Bitmap 或 Drawable 解码][decode_interceptor]
* [BitmapPool：复用 Bitmap，减少 GC][bitmap_pool]
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

### 关于 3.0 版本

* maven groupId 改为 `io.github.panpf.sketch3`，因此 2.\* 版本不会提示升级
* 包名改为 `com.github.panpf.sketch` 因此与 2.\* 版本不会冲突
* 基于 kotlin 协程重写，API、功能实现全部重构，当一个新的库用
* 不再要求必须使用 SketchImageView，任何 ImageView 及其子类都可以，结合自定义 Target 可以支持任意 View
* Zoom 功能拆分成独立的可单独依赖的模块并且超大图采样功能重构且支持多线程解码速度更快
* gif 模块现在直接依赖 [android-gif-drawable] 库不再二次修改，可自行升级
* 支持 Jetpack Compose
* 支持请求和解码拦截器
* 参考 [coil] 并结合 sketch 原有功能实现，对比 [coil] 有以下区别：
    * sketch 最低支持 API 16，而 [coil] 最低仅支持 API 21
    * sketch 支持 bitmap 复用，而 [coil] 不支持
    * sketch 支持更加精细化的调整图片大小
    * sketch 明确区分显示、加载、下载请求

## 特别感谢

* [coil-kt]/[coil]: Sketch 使用了来自 Coil 的部分代码，包括 framework、compose、sketch-gif-movie 部分
* [bumptech]/[glide]: BitmapPool
* [chrisbanes]/[PhotoView]: Zoom
* [koral--]/[android-gif-drawable]: gif-koral
* [wseemann]/[FFmpegMediaMetadataRetriever]: video-ffmpeg
* [BigBadaboom]/[androidsvg]: svg

## 我的项目

以下是我的其它开源项目，感兴趣的可以了解一下：

* [zoomimage](https://github.com/panpf/zoomimage)：用于缩放图像的库，支持 Android View、Compose 以及
  Compose Multiplatform；支持双击缩放、单指或双指手势缩放、单指拖动、惯性滑动、定位、旋转、超大图子采样等功能。
* [assembly-adapter](https://github.com/panpf/assembly-adapter)：Android 上的一个为各种 Adapter 提供多类型
  Item 实现的库。还顺带为 RecyclerView 提供了最强大的 divider。
* [sticky-item-decoration](https://github.com/panpf/stickyitemdecoration)：RecyclerView 黏性 item 实现

## License

Apache 2.0. 有关详细信息，请参阅 [LICENSE](LICENSE.txt) 文件.

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

[bitmap_pool]: docs/wiki/bitmap_pool_zh.md

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


[comment]: <> (footer)

[CHANGELOG.md]: CHANGELOG_zh.md