# ![Logo](docs/res/logo.png) Sketch

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Sketch-green.svg?style=true)](https://android-arsenal.com/details/1/4165)
[![version](https://img.shields.io/bintray/v/xiaopansky/maven/Sketch.svg)](https://bintray.com/xiaopansky/maven/Sketch)

Sketch是Android上的一个图片加载器，能够帮助开发者从本地或网络读取图片，处理后显示在页面上

![sample](docs/res/sample.jpg)

### 特点
>* ``支持gif图``. 集成了[android-gif-drawable 1.1.7](https://github.com/koral--/android-gif-drawable)可以方便的显示gif图片，感谢koral--
>* ``多种URI支持``. 支持``http://``、``https://``、``asset://``、``content://``、``file:///sdcard/sample.png``、``/sdcard/sample.jpg``、``drawable://``等7种URI
>* ``二级缓存``. 采用Lru算法在本地和内存中缓存图片，本地缓存默认最大容量为100M，内存缓存默认最大容量为最大可用内存的八分之一
>* ``各种列表支持``. 在各种列表（ListView、RecyclerView）中循环使用不错位，并且不占用setTag()方法
>* ``SketchImageView``. 提供功能SketchImageView，只需调用display***Image()系列方法即可显示各种图片，并且支持显示下载进度，显示按下效果，点击重试等常用功能
>* ``重复下载过滤``. 如果两个请求的图片地址一样的话，第二个就会等待第一个下载完成之后直接使用第一个下载的图片
>* ``即时取消无用请求``. SketchImageView在onDetachedFromWindow()或被重复利用的时候会主动取消之前的请求
>* ``自动防止加载过大Bitmap`` 可通过maxSize来控制加载到内存的图片的尺寸，默认为屏幕宽高的0.75倍，还会自动根据ImageView的layout_width和layout_height来调整maxSize
>* ``独家TransitionDrawable支持``. 独家支持任意尺寸的两张图片使用TransitionDrawable过渡显示，保证不变形
>* ``只加载或只下载``. 除了display()方法可以显示图片之外，你还可以通过load()方法只加载图片到内存中或通过download()方法只下载图片到本地
>* ``支持读取APK图标``. 支持直接读取本地APK文件的图标或根据包名和版本号读取已安装APP的图标
>* ``移动网络下暂停下载``. 内置了移动网络下暂停下载图片的功能，你只需开启即可
>* ``自动选择合适的Bimtap.Config``. 根据图片的MimeType自动选择合适的Bitmap.Config，减少内存浪费，例如对于JPEG格式的图片就会使用Bitmap.Config.RGB_565解码
>* ``特殊文件预处理``. 提供ImagePreprocessor，可对本地的特殊文件（例如多媒体文件）进行预处理，Sketch便可直接显示其封面，读取APK文件的图标就是通过这个功能实现的
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
从JCenter导入 

```groovy
dependencies{
	compile compile 'me.xiaopan:sketch:lastVersionName'
}
```
`lastVersionName`：[![version](https://img.shields.io/bintray/v/xiaopansky/maven/Sketch.svg)](https://bintray.com/xiaopansky/maven/Sketch) （不带v）

添加以下权限
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

最低支持`Android2.2 API 7`

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
|:---|:---|:---|:--|:---|:--|:---|:---|
|File in network|http://, http:// |YES|YES|YES（Android4.0 above）|YES|NO|
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
    public void onCompleted(Bitmap bitmap, ImageFrom imageFrom, String mimeType) {

    }

    @Override
    public void onCompleted(GifDrawable gifDrawable, ImageFrom imageFrom, String mimeType) {

    }
	...
}).maxSize(100, 100).commit();

// 下载
Sketch.with(context).download("http://biying.png", new DownloadListener() {
    @Override
    public void onCompleted(File cacheFile, boolean isFromNetwork) {
        
    }
	...
}).commit();
```

load()和download()还支持同步执行，详情请参考[同步执行load和download.md](docs/wiki/sync.md)

下面是三种方法所支持的属性（'-'代表不支持）

|属性|download()|load()|display()|
|:---|:---|:---|:---|
|sync|false|false|-|
|requestLevel|NET|NET|NET|
|listener|null|null|null|
|downloadProgressListener|null|null|null|
|disableCacheInDisk|false|false|false|
|maxSize|-|屏幕的0.75倍|优先考虑ImageView的layout_width和layout_height|
|resize|-|null|null|
|forceUseResize|-|false|false|
|processor|-|null|null|
|decodeGifImage|-|false|false|
|lowQualityImage|-|false|false|
|bitmapConfig|-|null|null|
|inPreferQualityOverSpeed|-|false|false|
|disableCacheInMemory|-|-|false|
|displayer|-|-|DefaultImageDisplayer|
|loadingImage|-|-|null|
|failedImage|-|-|null|
|pauseDownloadImage|-|-|null|
|resizeByFixedSize|-|-|false|

各属性的详细说明请参考[配置各种属性.md](docs/wiki/options.md)

#### 你可能还感兴趣的功能：
增强用户体验：
>* [SketchImageView详细使用说明.md](docs/wiki/sketch_image_view.md)
>* [配置各种属性.md](docs/wiki/options.md)
>* [显示gif图片.md](docs/wiki/display_gif_image.md)
>* [使用ImageProcessor将图片变成圆形的、圆角的或者高斯模糊的.md](docs/wiki/process_image.md)
>* [使用ImageDisplayer以更炫酷的方式显示图片（过渡、缩放等）.md](docs/wiki/displayer.md)
>* [使用ImagePreprocessor显示特殊文件的缩略图或图标.md](docs/wiki/pre_process_image.md)
>* [使用maxSize控制图片大小.md](docs/wiki/max_size.md)
>* [使用resize修剪图片尺寸.md](docs/wiki/resize.md)
>* [移动网络下暂停下载图片，节省流量.md](docs/wiki/pause_download.md)
>* [列表滑动时暂停加载图片，提升流畅度.md](docs/wiki/pause_load.md)

其它：
>* [同步执行load和download.md](docs/wiki/sync.md)
>* [了解inSampleSize计算规则.md](docs/wiki/in_sample_size.md)
>* [了解并配置内存缓存.md](docs/wiki/memory_cache.md)
>* [了解并配置本地缓存.md](docs/wiki/disk_cache.md)
>* [了解并配置HttpStack.md](docs/wiki/http_stack.md)
>* [监听加载开始、成功、失败以及下载进度.md](docs/wiki/listener.md)
>* [显示APK或已安装APP的图标.md](docs/wiki/display_apk_or_app_icon.md)
>* [了解何时取消请求以及如何主动取消请求.md](docs/wiki/cancel_request.md)
>* [使用ErrorCallback监控Sketch的异常.md](docs/wiki/error_callback.md)
>* [配置混淆（Proguard）.md](docs/wiki/proguard_config.md)

### Thanks
[koral](https://github.com/koral--) - [android-gif-drawable](https://github.com/koral--/android-gif-drawable)

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
