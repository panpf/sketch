# ![Logo](docs/res/logo.png) Sketch

Sketch是Android上的一个图片加载器，能够帮助开发者从本地或网络读取图片，处理后显示在页面上

![sample](docs/res/sample.jpg)

### 特点（Features）
>* ``支持GIF图``. 集成了[android-gif-drawable 1.1.7](https://github.com/koral--/android-gif-drawable)可以方便的显示GIF图片，感谢koral--
>* ``多种URI支持``. 支持``http://``、``https://``、``asset://``、``content://``、``file:///sdcard/sample.png``、``/sdcard/sample.jpg``、``drawable://``等7种URI
>* ``异步加载``. 异步处理每一个请求，并且网络任务和本地任务会放在不同的线程池中执行，这样不会因为网络任务而堵塞本地任务
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

### 示例APP（Sample app）
![SampleApp](docs/sketch-sample.png)

扫描二维码下载示例APP，也可[点击直接下载（Click download APK）](docs/sketch-sample.apk)

### 简介（Introduction）

#### 支持的URI以及使用的方法（Support URI and the use of the method）：

|Type|Scheme|Method|
|:---|:---|:---|
|File in network|http://, https:// |displayImage(String)|
|File in SDCard|/, file:// |displayImage(String)|
|Content Provider|content:// |displayURIImage(Uri)|
|Asset in app|asset:// |displayAssetImage(String)|
|Resource in app|resource:// |displayResourceImage(int)|

#### 支持的图片类型（Support picture type）

|Type|Scheme|jpeg|png|webp|gif|apk icon|
|:---|:---|:---|:--|:---|:--|:---|:---|
|File in network|http://, http:// |YES|YES|YES（Android4.0 above）|YES|NO|
|File in SDCard|/, file:// |YES|YES|YES（Android4.0 above）|YES|YES|
|Content Provider|content:// |YES|YES|YES（Android4.0 above）|YES|NO|
|Asset in app|asset:// |YES|YES|YES（Android4.0 above）|YES|NO|
|Resource in app|resource:// |YES|YES|YES（Android4.0 above）|YES|NO|

示例（Sample）：
```java
SketchImageView sketchImageView = ...;

// display from image network
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

#### download()、load()、display()
Sketch共有display()、load()、download()三个方法可供使用，你可以根据你的需求选择合适的方法
>* download()方法会下载图片到本地，并实现本地缓存；
>* load()方法在download()方法的基础上，加载图片到内存中，并对图片进行处理；
>* display()方法在load()方法的基础上，将图片缓存在内存中并显示在ImageView上。

下面是三种方法所属支持的属性表（'-'代表不支持，非'-'代表支持并且默认值是什么）

|属性|download()|load()|display()|
|:--|:--|:--|:--|
|requestLevel|NET|NET|NET|
|listener|null|null|null|
|progressListener|null|null|null|
|disableCacheInDisk|false|false|false|
|maxSize|-|屏幕的0.75倍|优先考虑ImageView的layout_width和layout_height|
|resize|-|null|null|
|resizeByFixedSize|-|false|false|
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

各属性的作用请参考[配置显示、加载、下载选项.md](docs/wiki/download_load_display_options.md)

### 使用指南（Usage guide）

#### 1. 导入Sketch（Import Sketch）
add gradle dependency
```groovy
dependencies{
	compile compile 'me.xiaopan:sketch:lastVersionName'
}
```
`lastVersionName`是最新版本名称的意思，你可以在[release](https://github.com/xiaopansky/Sketch/releases)页面看到最新的版本名称

最低支持`Android2.2`

然后在AndroidManifest.xml文件中添加以下权限
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

#### 2. 在XML中使用SketchImageView
res/layout/item_user.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<me.xiaopan.sketch.SketchImageView
    android:id="@+id/image_main_head"
    android:layout_width="130dp"
    android:layout_height="130dp"/>
```

#### 3. 在代码中设置URI显示图片
```java
SketchImageView headImageView = ...;
headImageView.displayImage("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg");
```

#### 4. 你可能还感兴趣的功能：
增强用户体验：
>* [SketchImageView详细使用说明](docs/wiki/sketch_image_view.md)
>* [配置显示、加载、下载选项](docs/wiki/download_load_display_options.md)
>* [显示GIF图片](docs/wiki/display-gif-image.md)
>* [使用ImageProcessor将图片变成圆形的、圆角的或者高斯模糊的](docs/wiki/process_image.md)
>* [使用ImageDisplayer以更炫酷的方式显示图片（过渡、缩放等）](docs/wiki/displayer.md)
>* [使用ImagePreprocessor显示特殊文件的缩略图或图标](docs/wiki/pre_process_image.md)
>* [使用maxSize控制图片大小](docs/wiki/maxsize_resize_in_sample_size.md)
>* [使用resize修剪图片尺寸](docs/wiki/maxsize_resize_in_sample_size.md)
>* [移动网络下暂停下载图片，节省流量](docs/wiki/mobile_network_pause_download.md)
>* [列表滑动时暂停加载图片，提升流畅度](docs/wiki/sliding_pause_load.md)

其它：
>* [非主线程不要初始化Sketch](docs/wiki/filtering_non_main_process.md)
>* [了解和自定义inSampleSize计算规则](docs/wiki/maxsize_resize_in_sample_size.md)
>* [了解和配置内存缓存](docs/wiki/memory_cache.md)
>* [了解和配置本地缓存](docs/wiki/disk_cache.md)
>* [自定义Http](docs/wiki/http_stack.md)
>* [监听加载开始、成功、失败以及进度](docs/wiki/listener.md)
>* [显示APK或已安装APP的图标](docs/wiki/display_apk_or_app_icon.md)
>* [了解何时取消请求以及如何主动取消请求](docs/wiki/cancel_request.md)
>* [使用ErrorCallback监控Sketch的异常](docs/wiki/error_callback.md)
>* [配置混淆（Proguard）](docs/wiki/proguard_config.md)

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