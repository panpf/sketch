# ![logo_image] Sketch Image Loader

![Platform][platform_image]
[![License][license_image]][license_link]
[![version_icon]][version_link]
![QQ Group][qq_group_image]

翻译：[English](README.md)

Sketch 是专为 Compose Multiplatform 和 Android View 设计的图片加载库，它有以下特点：

* `多加载源`：支持从 http、file、compose resource、android asset/content/resource 等多种来源加载图片
* `功能强大`：支持三级缓存、自动取消请求、自动调整图片尺寸、自动根据 Exif Orientation 旋转图片等
* `功能丰富`：支持动图、SVG 图片、Base64 图片、视频帧
* `易于扩展`：支持对缓存、解码、转换、过渡、占位图等各个环节的扩展
* `扩展功能`：提供蜂窝流量时暂停下载、列表滚动中暂停加载、图片类型徽章、下载进度指示器等实用扩展
* `现代化`：完全基于 Kotlin 和 Kotlin 协程设计

## 安装

`已发布到 mavenCentral`

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

### Compose Multiplatform:

Import the required Compose and network modules:

```kotlin
// 提供了 Sketch 的核心功能以及单例和依赖单例实现的扩展函数
implementation("io.github.panpf.sketch4:sketch-compose:${LAST_VERSION}")

// 提供了加载网络图片的能力
implementation("io.github.panpf.sketch4:sketch-http:${LAST_VERSION}")
```

> [!IMPORTANT]
> 为提升 compose 的性能请拷贝 `sketch-core` 模块下的 [compose_compiler_config.conf]
> 文件到您的项目中，然后按照  [Compose Stability Configuration][stability_configuration] 文档配置它

### Android View:

```kotlin
// 提供了 Sketch 的核心功能以及单例和依赖单例实现的扩展函数
implementation("io.github.panpf.sketch4:sketch-view:${LAST_VERSION}")

// 提供了加载网络图片的能力
implementation("io.github.panpf.sketch4:sketch-http:${LAST_VERSION}")
```

### 可选模块

```kotlin
// 使用 Android 或 Skia 内置的解码器解码 gif 动图并播放
implementation("io.github.panpf.sketch4:sketch-animated-gif:${LAST_VERSION}")

// [仅 Android] 使用 android-gif-drawable 库的 GifDrawable 解码 gif 动图并播放
implementation("io.github.panpf.sketch4:sketch-animated-gif-koral:${LAST_VERSION}")

// [仅 Android] Android 或 Skia 内置的解码器解码 heif 动图并播放
implementation("io.github.panpf.sketch4:sketch-animated-heif:${LAST_VERSION}")

// 使用 Android 或 Skia 内置的解码器解码 webp 动图并播放
implementation("io.github.panpf.sketch4:sketch-animated-webp:${LAST_VERSION}")

// 支持通过 uri 或 placeholder、fallback、error 访问 compose resources 资源
implementation("io.github.panpf.sketch4:sketch-compose-resources:${LAST_VERSION}")
implementation("io.github.panpf.sketch4:sketch-extensions-compose-resources:${LAST_VERSION}")

// 提供下载进度、图片类型角标、列表滚动中暂停加载、节省蜂窝流量等实用功能
implementation("io.github.panpf.sketch4:sketch-extensions-compose:${LAST_VERSION}")
implementation("io.github.panpf.sketch4:sketch-extensions-view:${LAST_VERSION}")

// [仅 Android] 支持通过文件路径加载 apk 文件的图标 
implementation("io.github.panpf.sketch4:sketch-extensions-apkicon:${LAST_VERSION}")

// [仅 Android] 支持通过包名和版本号加载已安装 app 的图标
implementation("io.github.panpf.sketch4:sketch-extensions-appicon:${LAST_VERSION}")

// [仅 JVM] 支持使用 HttpURLConnection 访问网络图片
implementation("io.github.panpf.sketch4:sketch-http-hurl:${LAST_VERSION}")

// [仅 JVM] 支持使用 OkHttp 访问网络图片
implementation("io.github.panpf.sketch4:sketch-http-okhttp:${LAST_VERSION}")

// 支持使用 2.x 版本的 ktor 访问网络图片
implementation("io.github.panpf.sketch4:sketch-http-ktor2:${LAST_VERSION}")

// 支持使用 3.x 版本的 ktor 访问网络图片
implementation("io.github.panpf.sketch4:sketch-http-ktor3:${LAST_VERSION}")

// 支持 SVG 图片
implementation("io.github.panpf.sketch4:sketch-svg:${LAST_VERSION}")

// [仅 Android] 使用 Android 内置的 MediaMetadataRetriever 类实现解码视频帧
implementation("io.github.panpf.sketch4:sketch-video:${LAST_VERSION}")

// [仅 Android] 使用 wseemann 的 FFmpegMediaMetadataRetriever 库实现解码视频帧
implementation("io.github.panpf.sketch4:sketch-video-ffmpeg:${LAST_VERSION}")
```

> [!TIP]
> * `sketch-compose`、`sketch-view` 模块都依赖 `sketch-singleton`
    > 模块提供的单例，如果你不需要单例则可以直接依赖他们的 `*-core` 版本
> * `sketch-http` 模块在 jvm 平台上依赖 `sketch-http-hurl`，在非 jvm 平台上依赖 `sketch-http-ktor3`

### 注册组件

Sketch 支持自动发现并注册 Fetcher 和 Decoder 组件，在 jvm 平台通过 ServiceLoader 实现，在非 jvm 平台通过
@EagerInitialization 注解实现。

自带模块全部支持自动注册，如果你想禁用自动注册，采用手动注册的方式请参考文档：[《注册组件》][register_component]

### R8 / Proguard

Sketch 自己不需要配置任何混淆规则，但你可能需要为间接依赖的 [Kotlin Coroutines], [OkHttp], [Okio]
添加混淆配置

## 快速上手

### Compose Multiplatform：

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
> `placeholder(Res.drawable.placeholder)` 需要导入 `sketch-compose-resources` 模块

### Android View：

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

## 文档

基础功能：

* [开始使用][getting_started]
* [注册组件][register_component]
* [Compose][compose]
* [Http：加载网络图片][http]
* [AnimatedImage：GIF、WEBP、HEIF][animated_image]
* [Resize：修改图片尺寸][resize]
* [Transformation：转换图片][transformation]
* [Transition：用炫酷的过渡方式显示图片][transition]
* [StateImage：占位图和错误图][state_image]
* [Listener：监听请求状态和下载进度][listener]
* [DownloadCache：了解下载缓存，避免重复下载][download_cache]
* [ResultCache：了解结果缓存，避免重复转换][result_cache]
* [MemoryCache：了解内存缓存，避免重复加载][memory_cache]
* [Fetcher：了解 Fetcher 及扩展新的 URI 类型][fetcher]
* [Decoder：了解 Sketch 的解码过程][decoder]
* [Target：将加载结果应用到目标上][target]
* [SVG：解码 SVG 静态图片][svg]
* [VideoFrames：解码视频帧][video_frame]
* [ExifOrientation：纠正图片方向][exif_orientation]
* [ImageOptions：统一管理图片配置][image_options]
* [RequestInterceptor：拦截 ImageRequest][request_interceptor]
* [DecodeInterceptor：拦截解码过程][decode_interceptor]
* [预加载图片到内存中][preload]
* [下载图片][download]
* [Lifecycle][lifecycle]
* [日志][log]

特色功能：

* [SketchImageView：通过 XML 属性配置请求][sketch_image_view]
* [提高长图在网格列表中的清晰度][long_image_grid_thumbnails]
* [显示下载进度][progress_indicator]
* [显示图片类型角标][mime_type_logo]
* [蜂窝数据网络下暂停下载图片节省流量][save_cellular_traffic]
* [列表滑动中暂停加载图片][pause_load_when_scrolling]
* [显示 APK 文件或已安装 APP 的图标][apk_app_icon]

## 更新日志

请查看 [CHANGELOG.md] 文件

## 测试平台

* Android：macOS Arm64 Emulator；API 21-34
* Desktop：macOS 14.6.1；JDK 17
* iOS：iphone 16 simulator；iOS 18.1
* Web：None

## 运行示例 App

准备环境：

1. Android Studio: Koala+ (2024.1.1+)
2. JDK: 17+
3. 使用 [kdoctor] 检查运行环境，并按照提示安装需要的软件
4. Android Studio 安装 `Kotlin Multiplatform` 和 `Compose Multiplatform IDE Support` 插件

运行示例 App：

1. 克隆项目并使用 Android Studio 打开
2. `.run` 目录下已经添加了各个平台的运行配置，同步完成后直接在 Android Studio
   顶部运行配置下拉框中选择对应平台的运行配置然后点击运行即可
3. ios 平台的运行配置需要你根据模版手动创建，如下：
    1. 拷贝 `.run/iosSample.run.template.xml` 文件，并去掉 `.template` 后缀，`.ignore` 文件中已经配置了忽略
       `iosSample.run.xml`
    2. 在顶部运行配置下拉框点击 `Edit Configurations` 选择 `iosSample` 然后配置 `Execute target` 即可

## 关于 4.0 版本

* maven groupId 升级为 `io.github.panpf.sketch4`，因此 2.\*、3.\* 版本不会提示升级
* 4.0 版本专为 Compose Multiplatform 打造，所以 API 有很多破坏性改动，请谨慎升级
* 4.0 版本做了大量的简化，比 3.0 版本简单很多，详情请查看更新日志
* Android 最低 API 升到了 API 21
* Kotlin 版本升级到了 2.0.0

## 特别感谢

* [coil-kt/coil][coil]: Sketch 使用了来自 Coil 的部分代码，包括 framework、compose 以及
  sketch-animated 的 movie 部分
* [koral--/android-gif-drawable][android-gif-drawable]: sketch-animated-koralgif
* [wseemann/FFmpegMediaMetadataRetriever][FFmpegMediaMetadataRetriever]: sketch-video-ffmpeg
* [BigBadaboom/androidsvg][androidsvg]: sketch-svg

## 我的项目

以下是我的其它开源项目，感兴趣的可以了解一下：

* [zoomimage](https://github.com/panpf/zoomimage)：用于缩放图片的库，支持 Compose Multiplatform 和
  Android
  View；支持双击缩放、单指或双指手势缩放、单指拖动、惯性滑动、定位、旋转、超大图子采样等功能。
* [assembly-adapter](https://github.com/panpf/assembly-adapter)：Android 上的一个为各种 Adapter 提供多类型
  Item 实现的库。还顺带为 RecyclerView 提供了最强大的 divider。
* [sticky-item-decoration](https://github.com/panpf/stickyitemdecoration)：RecyclerView 黏性 item 实现

## License

Apache 2.0. 有关详细信息，请参阅 [LICENSE](LICENSE.txt) 文件.

[comment]: <> (header)

[license_image]: https://img.shields.io/badge/License-Apache%202-blue.svg

[logo_image]: docs/res/logo.png

[license_link]: https://www.apache.org/licenses/LICENSE-2.0

[platform_image]: https://img.shields.io/badge/Platform-ComposeMultiplatform-brightgreen.svg

[qq_group_image]: https://img.shields.io/badge/QQ%E4%BA%A4%E6%B5%81%E7%BE%A4-529630740-red.svg

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/


[comment]: <> (wiki)

[animated_image]: docs/wiki/animated_image_zh.md

[apk_app_icon]: docs/wiki/apk_app_icon_zh.md

[compose]: docs/wiki/compose_zh.md

[decoder]: docs/wiki/decoder_zh.md

[download_cache]: docs/wiki/download_cache_zh.md

[exif_orientation]: docs/wiki/exif_orientation_zh.md

[fetcher]: docs/wiki/fetcher_zh.md

[getting_started]: docs/wiki/getting_started_zh.md

[register_component]: docs/wiki/register_component_zh.md

[http]: docs/wiki/http_zh.md

[image_options]: docs/wiki/image_options_zh.md

[lifecycle]: docs/wiki/lifecycle_zh.md

[listener]: docs/wiki/listener_zh.md

[log]: docs/wiki/log_zh.md

[long_image_grid_thumbnails]: docs/wiki/long_image_grid_thumbnails_zh.md

[memory_cache]: docs/wiki/memory_cache_zh.md

[mime_type_logo]: docs/wiki/mime_type_logo_zh.md

[pause_load_when_scrolling]: docs/wiki/pause_load_when_scrolling_zh.md

[preload]: docs/wiki/preload_zh.md

[download]: docs/wiki/download_image_zh.md

[progress_indicator]: docs/wiki/progress_indicator_zh.md

[request_interceptor]: docs/wiki/request_interceptor_zh.md

[decode_interceptor]: docs/wiki/decode_interceptor_zh.md

[resize]: docs/wiki/resize_zh.md

[result_cache]: docs/wiki/result_cache_zh.md

[save_cellular_traffic]: docs/wiki/save_cellular_traffic_zh.md

[sketch_image_view]: docs/wiki/sketch_image_view_zh.md

[state_image]: docs/wiki/state_image_zh.md

[svg]: docs/wiki/svg_zh.md

[target]: docs/wiki/target_zh.md

[transformation]: docs/wiki/transformation_zh.md

[transition]: docs/wiki/transition_zh.md

[video_frame]: docs/wiki/video_frame_zh.md


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

[CHANGELOG.md]: CHANGELOG_zh.md

[kdoctor]: https://github.com/Kotlin/kdoctor
