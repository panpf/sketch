# ![Logo](docs/res/logo.png) Sketch Image Loader

![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Sketch-orange.svg?style=true)](https://android-arsenal.com/details/1/4165)
[![License](https://img.shields.io/badge/License-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Logs](https://img.shields.io/github/release/panpf/sketch.svg?label=Logs&colorB=4AC41C)](https://github.com/panpf/sketch/releases)
[![Version](https://img.shields.io/github/release/panpf/sketch.svg?label=JCenter&colorB=4AC41C)](https://bintray.com/panpf/maven/sketch/_latestVersion#files)
[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)
![QQ Group](https://img.shields.io/badge/QQ%E4%BA%A4%E6%B5%81%E7%BE%A4-529630740-red.svg)

[English version of the README.md](README_EN.md)

Sketch 是 Android 上一款强大且全面的图片加载器，除了图片加载的必备功能外，还支持 GIF，手势缩放、分块显示超大图片、自动纠正图片方向、显示视频缩略图等功能

### 示例 APP

![SampleApp](docs/sketch-sample.png)

扫描二维码下载示例 APP，也可[点击直接下载（Click download APK）](docs/sketch-sample.apk)

### 支持的特性

* `多种 URI 支持`. 支持 `http://` 或 `https://`、`asset://`、`content://`、`file:///sdcard/sample.jpg` 或 `/sdcard/sample.jpg`、`drawable://`、`data:image/或data:img/` 等 6 种 URI
* `支持 gif 图片`. 集成了 [android-gif-drawable] 1.2.6 可以方便的显示 gif 图片，感谢 [koral--]
* `支持手势缩放`. 支持手势缩放功能，在 [PhotoView] 的基础上进行了优化，增加了滚动条，定位等功能
* `支持分块显示超大图`. 支持分块显示超大图功能，从此再大的图片也不怕了
* `支持三级缓存`. 通过 LruMemoryCache、LruDiskCache 复用图片，加快显示时间；通过 LruBitmapPool 复用 Bitmap，减少因 GC 而造成的卡顿
* `支持纠正图片方向`. 可纠正方向不正的图片，并且分块显示超大图功能也支持，仅限 JPEG 格式的图片
* `支持读取APK图标`. 支持直接读取本地 APK 文件的图标或根据包名和版本号读取已安装APP的图标
* `支持 Base64 图片`. 支持解析 Base64 格式的图片
* `支持各种列表`. 在各种列表（ListView、RecyclerView）中循环使用不错位，并且不占用 setTag() 方法
* `自动防止加载过大 Bitmap` 可通过 maxSize 来控制加载到内存的图片的尺寸，默认为 ImageView的 layout_width 和 layout_height 或屏幕的宽高
* `独家 TransitionDrawable 支持`. 独家支持任意尺寸的两张图片使用 TransitionDrawable 过渡显示，保证不变形
* `只加载或只下载`. 除了 display() 方法可以显示图片之外，你还可以通过 load() 方法只加载图片到内存中或通过 download() 方法只下载图片到本地
* `移动网络下暂停下载`. 内置了移动网络下暂停下载图片的功能，你只需开启即可
* `自动选择合适的 Bitmap.Config`. 根据图片的 MimeType 自动选择合适的 Bitmap.Config，减少内存浪费，例如对于 JPEG 格式的图片就会使用 Bitmap.Config.RGB_565 解码
* `特殊文件预处理`. 通过 ImagePreprocessor 可对特殊文件（例如多媒体文件）进行预处理，提取出其包含的图片，读取 APK 文件的图标就是通过这个功能实现的
* `强大且灵活的自定义`. 可自定义下载、缓存、解码、处理、显示、占位图等各个环节

### 支持的 URI

|Type|Scheme|Method In SketchImageView|
|:---|:---|:---|
|File in network|http://, https:// |displayImage(String)|
|File in SDCard|/, file:// |displayImage(String)|
|Content Provider|content:// |displayContentImage(Uri)|
|Asset in app|asset:// |displayAssetImage(String)|
|Resource in app|resource:// |displayResourceImage(int)|
|Base64|data:image/, data:/img/ |displayImage(String)|

### 支持的图片类型

|Image Type|Supported Version|
|:---|:---|
|jpeg|[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)|
|png|[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)|
|gif|[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)|
|bmp|[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)|
|webp|[![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=14)|

### 开始使用

#### 导入 Sketch

1.在 app 的 build.gradle 文件的 dependencies 节点中加入依赖

```groovy
compile 'me.xiaopan:sketch:$sketch_version'
```

请自行替换 `$sketch_version` 为最新的版本 [![Version](https://img.shields.io/github/release/panpf/sketch.svg?label=JCenter&colorB=4AC41C)](https://bintray.com/panpf/maven/sketch/_latestVersion#files) `(不要"v")`

如果需要播放 GIF 就添加 sketch-gif 的依赖

```groovy
compile 'me.xiaopan:sketch-gif:$sketch_gif_version'
```

请自行替换 `$sketch_gif_version` 为最新的版本 [![Version](https://img.shields.io/github/release/panpf/sketch.svg?label=JCenter&colorB=4AC41C)](https://bintray.com/panpf/maven/sketch-gif/_latestVersion#files) `(不要"v")`

`Android Studio 会自动合并 AAR 中所包含的权限和混淆配置`

2.如果需要兼容 API 13 (Android 3.2) 及以下的版本，那么需要在 Application 中调用释放缓存的方法（Android 4.0 以上能直接通过 Context 注册并回调）

```java
public class MyApplication extends Application {    

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Sketch.with(getBaseContext()).onTrimMemory(level);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Sketch.with(getBaseContext()).onLowMemory();
        }
    }
}
```

#### 使用 SketchImageView 显示图片

```java
SketchImageView sketchImageView = (SketchImageView) findViewById(R.id.image_main);

// display image from net
sketchImageView.displayImage("http://t.cn/RShdS1f");

// display image from SDCard
sketchImageView.displayImage("/sdcard/sample.jpg");
sketchImageView.displayImage("file:///sdcard/sample.jpg");

// display resource drawable
sketchImageView.displayResourceImage(R.drawable.sample);

// display image from asset
sketchImageView.displayAssetImage("sample.jpg");

// display image from content provider
sketchImageView.displayContentImage(Uri.parse("content://com.android.gallery/last"));

// display base64 image
sketchImageView.displayImage("data:image/jpeg;base64,/9j/4QaO...U7T/in//Z");

// display apk/app icon
sketchImageView.displayImage("/sdcard/google_play.apk");
sketchImageView.displayInstalledAppIcon("com.tencent.qq", 210);
```

#### 更多功能

基础功能：
* [SketchImageView 详细使用说明](docs/wiki/sketch_image_view.md)
* [Options & Helper](docs/wiki/options_and_helper.md)
* [管理多个 Options](docs/wiki/options_manage.md)
* [只加载图片到内存或只下载图片到本地](docs/wiki/load_and_download.md)
* [播放 GIF 图片](docs/wiki/display_gif_image.md)
* [手势缩放、旋转图片](docs/wiki/zoom.md)
* [分块显示超大图片](docs/wiki/huge_image.md.md)
* [通过 ShapeSize 在绘制时改变图片的尺寸](docs/wiki/shape_size.md)
* [通过 ImageShaper 在绘制时以圆形、圆角等形状显示图片](docs/wiki/image_shaper.md)
* [通过 ImageProcessor 将图片变成高斯模糊的、倒影的](docs/wiki/process_image.md)
* [通过 ImageDisplayer 以过渡、渐入等方式显示图片](docs/wiki/displayer.md)
* [通过 MaxSize 控制图片大小](docs/wiki/max_size.md)
* [通过 Resize 修剪图片尺寸](docs/wiki/resize.md)
* [通过 StateImage 灵活的使用各种图片作为 loadingImage](docs/wiki/state_image.md)
* [了解自动纠正图片方向功能](docs/wiki/correct_image_orientation.md)
* [显示视频缩略图](docs/wiki/display_video_thumbnail.md)

进一步提升用户体验：
* [让任意尺寸的两张图片都能使用 TransitionDrawable 过渡显示](docs/wiki/transition_displayer.md)
* [通过 thumbnailMode 属性显示更清晰的缩略图](docs/wiki/thumbnail_mode.md)
* [通过 cacheProcessedImageInDisk 属性缓存需要复杂处理的图片，提升显示速度](docs/wiki/cache_processed_image_in_disk.md)
* [移动网络下暂停下载图片，节省流量](docs/wiki/pause_download.md)
* [列表滑动时暂停加载图片，提升流畅度](docs/wiki/pause_load.md)
* [显示 APK 或已安装 APP 的图标](docs/wiki/display_apk_or_app_icon.md)
* [通过 MemoryCacheStateImage 先显示较模糊的图片，然后再显示清晰的图片](docs/wiki/memory_cache_state_image.md)

更多：
* [了解 inSampleSize 计算规则](docs/wiki/in_sample_size.md)
* [了解并配置 BitmapPool](docs/wiki/bitmap_pool.md)
* [了解并配置内存缓存](docs/wiki/memory_cache.md)
* [了解并配置本地缓存](docs/wiki/disk_cache.md)
* [了解并配置 HttpStack](docs/wiki/http_stack.md)
* [监听加载开始、成功、失败以及下载进度](docs/wiki/listener.md)
* [了解何时取消请求以及如何主动取消请求](docs/wiki/cancel_request.md)
* [通过 ErrorTracker 监控 Sketch 的异常](docs/wiki/error_tracker.md)
* [了解 Sketch 日志](docs/wiki/log.md)
* [延迟配置 Sketch](docs/wiki/initializer.md)
* [配置混淆（Proguard）](docs/wiki/proguard_config.md)

### 特别感谢

* [koral--] - [android-gif-drawable]
* [chrisbanes] - [PhotoView]
* [bumptech] - [glide]（BitmapPool）

### 联系我

* ![Email](https://img.shields.io/badge/Email-sky@xiaopan.me-red.svg)
* ![QQ Group](https://img.shields.io/badge/QQ%E4%BA%A4%E6%B5%81%E7%BE%A4-529630740-red.svg)

### License
    Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[koral--]: https://github.com/koral--
[android-gif-drawable]: https://github.com/koral--/android-gif-drawable
[chrisbanes]: https://github.com/chrisbanes
[PhotoView]: https://github.com/chrisbanes/PhotoView
[bumptech]: https://github.com/bumptech
[glide]: https://github.com/bumptech/glide
