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

## 示例 App

* Android、iOS、桌面版、Web 可部署包请到 [Releases](https://github.com/panpf/sketch/releases) 页面下载最新版本
* Web 示例：https://panpf.github.io/sketch/app

## 安装

`已发布到 mavenCentral`

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

### Compose Multiplatform:

导入必需的 Compose 和网络模块：

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

导入必需的 View 和网络模块：

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

// 支持 BlurHash 格式图片
implementation("io.github.panpf.sketch4:sketch-blurhash:${LAST_VERSION}")

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

// 提供 koin 集成支持，代替 sketch-compose 和 sketch-view，默认从 koin 获取 Sketch 实例
implementation("io.github.panpf.sketch4:sketch-compose-koin:${LAST_VERSION}")
implementation("io.github.panpf.sketch4:sketch-view-koin:${LAST_VERSION}")

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

1. Android、iOS、Web 等平台不需要配置任何混淆规则
2. 桌面平台需要配置以下混淆规则：
    ```proguard
    # -------------------------- Sketch Privider ---------------------------- #
    -keep class * implements com.github.panpf.sketch.util.DecoderProvider { *; }
    -keep class * implements com.github.panpf.sketch.util.FetcherProvider { *; }
    ```
3. 可能还需要为间接依赖的 [Kotlin Coroutines], [OkHttp], [Okio] 等三方库添加混淆配置

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
* [BlurHash][blurhash]
* [ExifOrientation：纠正图片方向][exif_orientation]
* [ImageOptions：统一管理图片配置][image_options]
* [Interceptor：拦截 ImageRequest][interceptor]
* [预加载图片到内存中][preload]
* [下载图片][download]
* [Lifecycle][lifecycle]
* [日志][log]
* [迁移][migrate]

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

* Android: Emulator; Arm64; API 21-34
* Desktop: macOS; 14.6.1; JDK 17
* iOS: iphone 16 simulator; iOS 18.1
* Web: Chrome; 130

## 运行示例 App

准备环境：

1. Android Studio: Norwhal+ (2025.1.1+)
2. JDK: 17+
3. 使用 [kdoctor] 检查运行环境，并按照提示安装需要的软件
4. Android Studio 安装 `Kotlin Multiplatform` 插件

运行示例 App：

1. 克隆项目并使用 Android Studio 打开
2. 同步完成后 `Kotlin Multiplatform` 插件会自动为各个平台创建运行配置
3. 选择对应平台的运行配置，然后点击运行即可

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

[logo_image]: docs/images/logo.png

[license_link]: https://www.apache.org/licenses/LICENSE-2.0

[platform_image]: https://img.shields.io/badge/Platform-ComposeMultiplatform-brightgreen.svg

[qq_group_image]: https://img.shields.io/badge/QQ%E4%BA%A4%E6%B5%81%E7%BE%A4-529630740-red.svg

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/


[comment]: <> (wiki)

[animated_image]: docs/animated_image.zh.md

[apk_app_icon]: docs/apk_app_icon.zh.md

[compose]: docs/compose.zh.md

[decoder]: docs/decoder.zh.md

[download_cache]: docs/download_cache.zh.md

[exif_orientation]: docs/exif_orientation.zh.md

[fetcher]: docs/fetcher.zh.md

[getting_started]: docs/getting_started.zh.md

[register_component]: docs/register_component.zh.md

[http]: docs/http.zh.md

[image_options]: docs/image_options.zh.md

[lifecycle]: docs/lifecycle.zh.md

[listener]: docs/listener.zh.md

[log]: docs/log.zh.md

[long_image_grid_thumbnails]: docs/long_image_grid_thumbnails.zh.md

[memory_cache]: docs/memory_cache.zh.md

[mime_type_logo]: docs/mime_type_logo.zh.md

[pause_load_when_scrolling]: docs/pause_load_when_scrolling.zh.md

[preload]: docs/preload.zh.md

[download]: docs/download_image.zh.md

[progress_indicator]: docs/progress_indicator.zh.md

[interceptor]: docs/interceptor.zh.md

[resize]: docs/resize.zh.md

[result_cache]: docs/result_cache.zh.md

[save_cellular_traffic]: docs/save_cellular_traffic.zh.md

[sketch_image_view]: docs/sketch_image_view.zh.md

[state_image]: docs/state_image.zh.md

[svg]: docs/svg.zh.md

[target]: docs/target.zh.md

[transformation]: docs/transformation.zh.md

[transition]: docs/transition.zh.md

[video_frame]: docs/video_frame.zh.md

[migrate]: docs/migrate.zh.md

[blurhash]: docs/blurhash.zh.md


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

[CHANGELOG.md]: CHANGELOG.zh.md

[kdoctor]: https://github.com/Kotlin/kdoctor
