# ![logo_image] Sketch Image Loader

![Platform][platform_image]
[![Android Arsenal][android_arsenal_image]][android_arsenal_link]
[![License][license_image]][license_link]
[![API][min_api_image]][min_api_link]
![QQ Group][qq_group_image]

[English version of the README.md](README_EN.md)

Sketch 是 Android 上一款强大且全面的图片加载器，除了图片加载的必备功能外，还支持 GIF，手势缩放、分块显示超大图片、自动纠正图片方向、显示视频缩略图等功能

## 示例 APP

![sample_app_download_qrcode]

扫描二维码下载或[点我下载][sample_app_download_link]

## 特性

### 支持的特性

* `多种 URI 支持`. 支持 `http://`、`https://`、`asset://`、`content://`、`file:///sdcard/sample.jpg`、 `/sdcard/sample.jpg`、`drawable://`、`data:image/`、`data:img/`、`android.resource://`、`apk.icon://`、`app.icon://` 等 URI，通过 [UriModel] 还可以扩展支持的 URI
* `支持 gif 图片`. 集成了 [android-gif-drawable] 1.2.19 可以方便的显示 gif 图片
* `支持手势缩放`. 支持手势缩放功能，在 [PhotoView] 的基础上进行了优化，增加了滚动条，定位等功能
* `支持分块显示超大图`. 支持分块显示超大图功能，从此再大的图片也不怕了
* `支持三级缓存`. 通过 LruMemoryCache、LruDiskCache 复用图片，加快显示时间；通过 LruBitmapPool 复用 Bitmap，减少因 GC 而造成的卡顿
* `支持纠正图片方向`. 可纠正方向不正的图片，并且分块显示超大图功能也支持，仅限 JPEG 格式的图片
* `支持显示 APK 或 APP 图标`. 支持显示本地 APK 文件的图标或根据包名和版本号显示已安装APP的图标
* `支持 Base64 图片`. 支持解析 Base64 格式的图片
* `支持各种列表`. 在各种列表（ListView、RecyclerView）中循环使用不错位，并且不占用 setTag() 方法
* `自动防止加载过大 Bitmap` 可通过 maxSize 来控制加载到内存的图片的尺寸，默认为 ImageView的 layout_width 和 layout_height 或屏幕的宽高
* `独家 TransitionDrawable 支持`. 独家支持任意尺寸的两张图片使用 TransitionDrawable 过渡显示，保证不变形
* `只加载或只下载`. 除了 display() 方法可以显示图片之外，你还可以通过 load() 方法只加载图片到内存中或通过 download() 方法只下载图片到本地
* `移动数据下暂停下载`. 内置了移动数据下暂停下载图片的功能，你只需开启即可
* `自动选择合适的 Bitmap.Config`. 根据图片的 MimeType 自动选择合适的 Bitmap.Config，减少内存浪费，例如对于 JPEG 格式的图片就会使用 Bitmap.Config.RGB_565 解码
* `强大且灵活的自定义`. 可自定义 URI 支持、HTTP、下载、缓存、解码、处理、显示、占位图等各个环节

### 支持的 URI

|Type|Scheme|
|:---|:---|
|File in network|http://, https:// |
|File in SDCard|/, file:// |
|Content Resolver|content://|
|Asset Resource|asset:// |
|Drawable Resource|drawable:// |
|Base64|data:image/, data:/img/ |
|APK Icon|apk.icon:// |
|APP Icon|app.icon:// |
|Android Resource|android.resource:// |

详情请参考 [URI 类型及使用指南][uri]

### 支持的图片类型

* jpeg
* png
* gif
* bmp
* webp

## 开始使用

### 导入 Sketch

在 app 的 build.gradle 文件的 dependencies 节点中加入依赖

```groovy
implementation 'me.panpf:sketch:$sketch_version'
```

请自行替换 `$sketch_version` 为最新的版本 [![sketch_version_image]][sketch_version_link]

如果需要播放 GIF 就添加 sketch-gif 的依赖

```groovy
implementation 'me.panpf:sketch-gif:$sketch_gif_version'
```

请自行替换 `$sketch_gif_version` 为最新的版本 [![sketch_gif_version_image]][sketch_gif_version_link]

如果需要手势缩放功能就添加 sketch-zoom 的依赖

```groovy
implementation 'me.panpf:sketch-zoom:$sketch_zoom_version'
```

请自行替换 `$sketch_zoom_version` 为最新的版本 [![sketch_zoom_version_image]][sketch_zoom_version_link]

`Android Studio 会自动合并 AAR 中所包含的权限和混淆配置`

### 使用 SketchImageView 显示图片

```java
SketchImageView sketchImageView = (SketchImageView) findViewById(R.id.image_main);
sketchImageView.displayImage("http://t.cn/RShdS1f");
```

>* 更多类型 URI 的使用请参考 [URI 类型及使用指南][uri]
>* 更多 SketchImageView 使用方法请参考 [SketchImageView 使用指南][sketch_image_view]

### 文档

基础功能：
* [URI 类型及使用指南][uri]
* [SketchImageView 使用指南][sketch_image_view]
* [使用 Options 配置图片][options]
* [播放 GIF 图片][play_gif_image]
* [手势缩放、旋转图片][zoom]
* [分块显示超大图片][block_display]
* [使用 ShapeSize 在绘制时改变图片的尺寸][shape_size]
* [使用 ImageShaper 在绘制时改变图片的形状][image_shaper]
* [使用 ImageProcessor 在解码后改变图片][image_processor]
* [使用 ImageDisplayer 以动画的方式显示图片][image_displayer]
* [使用 MaxSize 读取合适尺寸的缩略图，节省内存][max_size]
* [使用 Resize 精确修改图片的尺寸][resize]
* [使用 StateImage 设置占位图片和状态图片][state_image]
* [监听开始、成功、失败以及下载进度][listener]

提升用户体验：
* [使用 TransitionImageDisplayer 以自然过渡渐的变方式显示图片][transition_image_displayer]
* [使用 thumbnailMode 属性显示更清晰的缩略图][thumbnail_mode]
* [使用 cacheProcessedImageInDisk 属性缓存需要复杂处理的图片，提升显示速度][cache_processed_image_in_disk]
* [使用 MemoryCacheStateImage 先显示已缓存的较模糊的图片，然后再显示清晰的图片][memory_cache_state_image]
* [移动数据或有流量限制的 WIFI 下暂停下载图片，节省流量][pause_download]
* [列表滑动时暂停加载图片，提升列表滑动流畅度][pause_load]

更多：
* [UriModel 详解及扩展 URI][uri_model]
* [统一修改 Options][options_filter]
* [显示视频缩略图][display_video_thumbnail]
* [管理多个 Options][options_manage]
* [只加载或下载图片][load_and_download]
* [显示 APK 或已安装 APP 的图标][display_apk_or_app_icon]
* [自动纠正图片方向][correct_image_orientation]
* [复用 Bitmap 降低 GC 频率，减少卡顿][bitmap_pool]
* [在内存中缓存 Bitmap 提升显示速度][memory_cache]
* [在磁盘上缓存图片原文件，避免重复下载][disk_cache]
* [发送 HTTP 请求][http_stack]
* [取消请求][cancel_request]
* [处理 Sketch 的异常][callback]
* [日志][log]
* [延迟并统一配置 Sketch][initializer]
* [配置混淆（Proguard）][proguard_config]

## 更新日志

Please view the [CHANGELOG.md] file

## 特别感谢

* [koral--] - [android-gif-drawable]
* [chrisbanes] - [PhotoView]
* [bumptech] - [glide]（BitmapPool）

## 交流群

* ![QQ Group][qq_group_image]

## License
    Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>

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
[android_arsenal_image]: https://img.shields.io/badge/Android%20Arsenal-Sketch-orange.svg?style=true
[android_arsenal_link]: https://android-arsenal.com/details/1/4165
[license_image]: https://img.shields.io/badge/License-Apache%202-blue.svg
[license_link]: https://www.apache.org/licenses/LICENSE-2.0
[sketch_version_image]: https://api.bintray.com/packages/panpf/maven/sketch/images/download.svg
[sketch_version_link]: https://bintray.com/panpf/maven/sketch/_latestVersion#files
[sketch_gif_version_image]: https://api.bintray.com/packages/panpf/maven/sketch-gif/images/download.svg
[sketch_gif_version_link]: https://bintray.com/panpf/maven/sketch-gif/_latestVersion#files
[sketch_zoom_version_image]: https://api.bintray.com/packages/panpf/maven/sketch-zoom/images/download.svg
[sketch_zoom_version_link]: https://bintray.com/panpf/maven/sketch-zoom/_latestVersion#files
[min_api_image]: https://img.shields.io/badge/API-16%2B-orange.svg
[min_api_link]: https://android-arsenal.com/api?level=16
[qq_group_image]: https://img.shields.io/badge/QQ%E4%BA%A4%E6%B5%81%E7%BE%A4-529630740-red.svg

[CHANGELOG.md]: CHANGELOG.md

[sample_app_download_qrcode]: docs/sketch-sample.png
[sample_app_download_link]: https://github.com/panpf/sketch/raw/master/docs/sketch-sample.apk
[UriModel]: sketch/src/main/java/me/panpf/sketch/uri/UriModel.java

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
[callback]: docs/wiki/callback.md
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
