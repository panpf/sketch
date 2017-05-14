# ![Logo](docs/res/logo.png) Sketch

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Sketch-green.svg?style=true)](https://android-arsenal.com/details/1/4165)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Logs](https://img.shields.io/github/release/xiaopansky/sketch.svg?label=Logs)](https://github.com/xiaopansky/sketch/releases)
[![Version](https://img.shields.io/github/release/xiaopansky/sketch.svg?label=JCenter&colorB=green)](https://bintray.com/xiaopansky/maven/sketch/_latestVersion#files)
[![API](https://img.shields.io/badge/API-10%2B-red.svg)](https://android-arsenal.com/api?level=9)
![QQ Group](https://img.shields.io/badge/QQ%E4%BA%A4%E6%B5%81%E7%BE%A4-529630740-orange.svg)

[English version of the README.md](README_EN.md)

Sketch是Android上一个强大且全面的图片加载器，支持GIF，手势缩放以及分块显示超大图片

![sample](docs/res/sample.jpg)

### 特点
>* ``多种URI支持``. 支持``http://``、``https://``、``asset://``、``content://``、``file:///sdcard/sample.png``、``/sdcard/sample.jpg``、``drawable://``等7种URI
>* ``支持gif图``. 集成了[android-gif-drawable 1.2.6](https://github.com/koral--/android-gif-drawable)可以方便的显示gif图片，感谢koral--
>* ``支持手势缩放``. 支持手势缩放功能，在[PhotoView](https://github.com/chrisbanes/PhotoView)的基础上进行了优化，增加了滚动条，定位等功能
>* ``支持分块显示超大图``. 支持分块显示超大图功能，从此再大的图片也不怕了
>* ``支持三级缓存``. 通过LruMemoryCache、LruDiskCache复用图片，加快显示时间；通过LruBitmapPool复用Bitmap，减少因GC而造成的卡顿
>* ``支持各种列表``. 在各种列表（ListView、RecyclerView）中循环使用不错位，并且不占用setTag()方法
>* ``自动防止加载过大Bitmap`` 可通过maxSize来控制加载到内存的图片的尺寸，默认为ImageView的layout_width和layout_height或屏幕的宽高
>* ``独家TransitionDrawable支持``. 独家支持任意尺寸的两张图片使用TransitionDrawable过渡显示，保证不变形
>* ``只加载或只下载``. 除了display()方法可以显示图片之外，你还可以通过load()方法只加载图片到内存中或通过download()方法只下载图片到本地
>* ``支持读取APK图标``. 支持直接读取本地APK文件的图标或根据包名和版本号读取已安装APP的图标
>* ``移动网络下暂停下载``. 内置了移动网络下暂停下载图片的功能，你只需开启即可
>* ``自动选择合适的Bitmap.Config``. 根据图片的MimeType自动选择合适的Bitmap.Config，减少内存浪费，例如对于JPEG格式的图片就会使用Bitmap.Config.RGB_565解码
>* ``特殊文件预处理``. 通过ImagePreprocessor可对特殊文件（例如多媒体文件）进行预处理，提取出其包含的图片，读取APK文件的图标就是通过这个功能实现的
>* ``支持纠正图片方向``. 可纠正方向不正的图片，并且分块显示超大图功能也支持，仅限jpeg格式的图片
>* ``强大且灵活的自定义``. 可自定义下载、缓存、解码、处理、显示、占位图等各个环节

### 示例APP
![SampleApp](docs/sketch-sample.png)

扫描二维码下载示例APP，也可[点击直接下载（Click download APK）](docs/sketch-sample.apk)

### 示例
```java
SketchImageView sketchImageView = findViewByID(R.id.image_main);

// display image from network
sketchImageView.displayImage("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg");

// display image from SDCard
sketchImageView.displayImage("/sdcard/sample.png");
sketchImageView.displayImage("file:///sdcard/sample.png");

// display apk icon from SDCard
sketchImageView.displayImage("/sdcard/google_play.apk");

// display installed app icon
sketchImageView.displayInstalledAppIcon("com.tencent.qq", 50001);

// display resource drawable
sketchImageView.displayResourceImage(R.drawable.sample);

// display image from asset
sketchImageView.displayAssetImage("sample.jpg");

// display image from URI
Uri uri = ...;
sketchImageView.displayURIImage(uri);
```

[SketchImageView详细使用说明.md](docs/wiki/sketch_image_view.md)

### 使用指南

#### 导入

1.在app的build.gradle文件的dependencies节点中加入依赖

```groovy
dependencies {
	compile 'me.xiaopan:sketch:<SKETCH_LAST_VERSION_NAME>'
}
```

请自行替换 `<SKETCH_LAST_VERSION_NAME>` 为最新的版本 [![Version](https://img.shields.io/github/release/xiaopansky/sketch.svg?label=JCenter&colorB=green)](https://bintray.com/xiaopansky/maven/sketch/_latestVersion#files) `(不要"v")`

如果需要播放GIF就添加sketch-gif的依赖

```groovy
dependencies {
	compile 'me.xiaopan:sketch-gif:<SKETCH_GIF_LAST_VERSION_NAME>'
}
```

请自行替换`<SKETCH_GIF_LAST_VERSION_NAME>` 为最新的版本 [![Version](https://img.shields.io/github/release/xiaopansky/sketch.svg?label=JCenter&colorB=green)](https://bintray.com/xiaopansky/maven/sketch-gif/_latestVersion#files) `(不要"v")`

[点击了解混淆配置](docs/wiki/proguard_config.md)

2.添加以下权限
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

3.Android 4.0以下需要在Application中调用释放缓存的方法（Android 4.0以上能直接通过Context注册并回调）
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

最低支持`Android2.3 API 9`

#### 支持的URI
|Type|Scheme|Method|
|:---|:---|:---|
|File in network|http://, https:// |displayImage(String)|
|File in SDCard|/, file:// |displayImage(String)|
|Content Provider|content:// |displayURIImage(Uri)|
|Asset in app|asset:// |displayAssetImage(String)|
|Resource in app|resource:// |displayResourceImage(int)|

#### 支持的图片类型

|Type|Scheme|jpeg|png|webp|gif|apk icon|
|:---|:---|:---|:---|:---|:---|:---|
|File in network|http://, https:// |YES|YES|YES（Android4.0 above）|YES|NO|
|File in SDCard|/, file:// |YES|YES|YES（Android4.0 above）|YES|YES|
|Content Provider|content:// |YES|YES|YES（Android4.0 above）|YES|NO|
|Asset in app|asset:// |YES|YES|YES（Android4.0 above）|YES|NO|
|Resource in app|resource:// |YES|YES|YES（Android4.0 above）|YES|NO|

#### download()、load()、display()
Sketch共有display()、load()、download()三个方法可供使用，你可以根据你的需求选择合适的方法
>* download()方法会下载图片到本地，并实现本地缓存
>* load()方法在download()方法的基础上，加载图片到内存中，并对图片进行处理
>* display()方法在load()方法的基础上，将图片缓存在内存中并显示在ImageView上

示例：
```
// 显示
Sketch.with(context).display("http://biying.png", sketchImageView)
    .loadingImage(R.drawable.image_loading)
    .commit();

// 加载
Sketch.with(context).load("http://biying.png", new LoadListener() {
    @Override
    public void onCompleted(LoadResult loadResult) {

    }

    ...
}).maxSize(100, 100).commit();

// 下载
Sketch.with(context).download("http://biying.png", new DownloadListener() {
    @Override
    public void onCompleted(DownloadResult downloadResult) {

    }

    ...
}).commit();
```

load()和download()还支持同步执行，详情请参考[同步执行load和download.md](docs/wiki/sync.md)

#### 你可能还感兴趣的功能：

基础功能：
>* [SketchImageView详细使用说明.md](docs/wiki/sketch_image_view.md)
>* [播放GIF图片.md](docs/wiki/display_gif_image.md)
>* [手势缩放、旋转图片.md](docs/wiki/zoom.md)
>* [分块显示超大图片.md](docs/wiki/large_image.md)
>* [配置各种属性和属性管理.md](docs/wiki/options.md)
>* [通过ShapeSize在绘制时改变图片的尺寸.md](docs/wiki/shape_size.md)
>* [通过ImageShaper在绘制时以圆形、圆角等形状显示图片.md](docs/wiki/image_shaper.md)
>* [通过ImageProcessor将图片变成高斯模糊的、倒影的.md](docs/wiki/process_image.md)
>* [通过ImageDisplayer以过渡、渐入等方式显示图片.md](docs/wiki/displayer.md)
>* [通过MaxSize控制图片大小.md](docs/wiki/max_size.md)
>* [通过Resize修剪图片尺寸.md](docs/wiki/resize.md)
>* [通过StateImage灵活的使用各种图片作为loadingImage.md](docs/wiki/state_image.md)
>* [了解自动纠正图片方向功能.md](docs/wiki/correct_image_orientation.md)

进一步提升用户体验：
>* [让任意尺寸的两张图片都能使用TransitionDrawable过渡显示.md](docs/wiki/transition_displayer.md)
>* [通过thumbnailMode属性显示更清晰的缩略图.md](docs/wiki/thumbnail_mode.md)
>* [通过cacheProcessedImageInDisk属性缓存需要复杂处理的图片，提升显示速度.md](docs/wiki/cache_processed_image_in_disk.md)
>* [移动网络下暂停下载图片，节省流量.md](docs/wiki/pause_download.md)
>* [列表滑动时暂停加载图片，提升流畅度.md](docs/wiki/pause_load.md)
>* [显示APK或已安装APP的图标.md](docs/wiki/display_apk_or_app_icon.md)
>* [通过ImagePreprocessor显示特殊文件中的图片（例如视频、MP3）.md](docs/wiki/pre_process_image.md)
>* [通过MemoryCacheStateImage先显示较模糊的图片，然后再显示清晰的图片.md](docs/wiki/memory_cache_state_image.md)

更多：
>* [同步执行load和download.md](docs/wiki/sync.md)
>* [了解inSampleSize计算规则.md](docs/wiki/in_sample_size.md)
>* [了解并配置BitmapPool.md](docs/wiki/bitmap_pool.md)
>* [了解并配置内存缓存.md](docs/wiki/memory_cache.md)
>* [了解并配置本地缓存.md](docs/wiki/disk_cache.md)
>* [了解并配置HttpStack.md](docs/wiki/http_stack.md)
>* [监听加载开始、成功、失败以及下载进度.md](docs/wiki/listener.md)
>* [了解何时取消请求以及如何主动取消请求.md](docs/wiki/cancel_request.md)
>* [通过ErrorTracker监控Sketch的异常.md](docs/wiki/error_tracker.md)
>* [同步输出Sketch运行日志到SD卡.md](docs/wiki/sync_out_log_to_disk.md)
>* [配置混淆（Proguard）.md](docs/wiki/proguard_config.md)

### 特别感谢

[koral](https://github.com/koral--) - [android-gif-drawable](https://github.com/koral--/android-gif-drawable)

[chrisbanes](https://github.com/chrisbanes) - [PhotoView](https://github.com/chrisbanes/PhotoView)

[bumptech](https://github.com/bumptech/glide) - [glide](https://github.com/bumptech/glide) （BitmapPool）

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
