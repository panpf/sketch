#![Logo](https://github.com/xiaopansky/Sketch/raw/master/docs/logo.png) Sketch

Sketch是用于Android上的一个图片加载器，目的是为了帮助开发者从本地或网络读取图片，然后处理并显示在页面上

Sketch is for Android on a picture of the loader, the purpose is to help the developers to read the image from a local or network, then processed and displayed on the page

![sample](https://github.com/xiaopansky/Sketch/raw/master/docs/sample.jpg)

###特点（Features）
>* ``支持GIF图片``. 集成了[android-gif-drawable 1.1.7](https://github.com/koral--/android-gif-drawable)可以方便的显示GIF图片，感谢koral--
>* ``多种URI支持``. 支持``http://``、``https://``、``asset://``、``content://``、``/sdcard/sample.jpg``、``drawable://``等6种URI。
>* ``异步加载``. 采用线程池来处理每一个请求，并且网络加载和本地加载会放在不同的线程池中执行，保证不会因为网络加载而堵塞本地加载。
>* ``二级缓存支持``. 采用Lru算法在本地和内存中缓存图片，本地缓存默认最大容量为100M，内存缓存默认最大容量为最大可用内存的八分之一。
>* ``支持ViewHolder``. 即使你在ListView中使用了ViewHolder也依然可以使用Sketch来加载图片，在不占用setTag()方法的同时保证图片显示绝对不会混乱。
>* ``SketchImageView``. 提供功能更强大的SketchImageView，只需调用display***Image()系列方法即可显示各种图片，并且支持显示下载进度，按下效果，显示失败时点击重新显示以及显示GIF标识等功能。
>* ``重复下载过滤``. 如果两个请求的图片地址一样的话，第二个就会等待第一个下载完成之后直接使用第一个下载的图片。
>* ``即时取消无用请求``. SketchImageView在onDetachedFromWindow()或被重复利用的时候会及时取消之前的请求。
>* ``自动防止加载过大Bitmap`` 提供maxSize参数来控制加载到内存的图片的尺寸，默认为屏幕宽高的1.5倍，在使用display()方法显示图片的时候还会自动根据ImageView的layout size来调整maxSize。
>* ``TransitionDrawable支持``. 自定义了一个FixedSizeBitmapDrawable，用于支持任意尺寸的两张图片使用TransitionDrawable过渡显示，保证不变形。
>* ``兼容RecyclerView``. RecyclerView增加了一些新的特性，导致在onDetachedFromWindow()中直接回收图片或设置drawable为null会导致一些显示异常和崩溃，现已完美兼容。
>* ``多种方式玩转图片``. 除了display()方法可用来显示图片之外，你还可以通过load()方法加载图片或通过download()方法下载图片。
>* ``强大的自定义功能``. 可自定义参数组织、请求分发与执行、图片缓存、图片解码、图片处理、图片显示、默认或失败占位图、计算inSampleSize以及整个流程等。
>* ``支持读取APK图标``. 支持直接读取本地APK文件的图标，只需想指定本地图片的路径那样指定APK文件的路径即可。
>* ``提供RequestOptions``. 通过RequestOptions你可以提前定义好一系列的属性，然后在显示图片的时候一次性设置，另外你还可以通过Sketch.putOptions(Enum<?>, RequestOptions)存储RequestOptions。然后在使用的时候指定名称即可。
>* ``提供移动网络下暂停下载功能``. 内置了移动网络下暂停下载图片的功能，你只需调用Sketch.with(context).getConfiguration().setMobileNetworkPauseDownload(true)开启即可。
>* ``占位图支持内存缓存``. 对经过ImageProcessor处理的占位图支持内存缓存
>* ``自动选择合适的Bimtap.Config``. 根据图片的MimeType自动选择合适的Bitmap.Config，减少内存浪费，最明显的例子就是对于JPEG类型的图片使用Bitmap.Config.RGB_565解码。

###示例APP（Sample app）
![SampleApp](https://github.com/xiaopansky/Sketch/raw/master/docs/sketch-sample.png)

扫描二维码下载示例APP，也可[点击直接下载（Click download APK）](https://github.com/xiaopansky/Sketch/raw/master/docs/sketch-sample.apk)

###简介（Introduction）

####支持的URI以及使用的方法（Support URI and the use of the method）：

|Type|Scheme|Fetch method used in SketchImageView|
|:--|:--|:--|
|File in network|http://, https:// |displayImage(String)|
|File in SDCard|/|displayImage(String)|
|Content Provider|content://|displayURIImage(Uri)|
|Asset in app|asset://|displayAssetImage(String)|
|Resource in app|resource://|displayResourceImage(int)|

####支持的图片类型（Support picture type）

|Type|Scheme|jpeg|png|webp|gif|apk icon|
|:--|:--|:--|:--|:--|:--|:--|:--|
|File in network|http://, http:// |YES|YES|YES（Android4.0 above）|YES|NO|
|File in SDCard|/|YES|YES|YES（Android4.0 above）|YES|YES|
|Content Provider|content://|YES|YES|YES（Android4.0 above）|YES|NO|
|Asset in app|asset://|YES|YES|YES（Android4.0 above）|YES|NO|
|Resource in app|resource://|YES|YES|YES（Android4.0 above）|YES|NO|

示例（Sample）：
```java
SketchImageView sketchImageView = ...;

// display from image network
sketchImageView.displayImage("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg");

// display image from SDCard
sketchImageView.displayImage("/sdcard/sample.png");

// display apk icon from SDCard
sketchImageView.displayImage("/sdcard/google_play.apk");

// display resource drawable
sketchImageView.displayResourceImage(R.drawable.sample);

// display image from asset
sketchImageView.displayAssetImage("sample.jpg");

// display image from URI
Uri uri = ...;
sketchImageView.displayURIImage(uri);
```

####download()、load()、display()
Sketch共有display()、load()、download()三个方法可供使用，你可以根据你的需求选择合适的方法
>* download()方法会下载图片到本地，并实现本地缓存；
>* load()方法在download()方法的基础上，加载图片到内存中，并对图片进行处理；
>* display()方法在load()方法的基础上，将图片缓存在内存中并显示在ImageView上。

下面是三种方法所属支持的属性表（'-'代表不支持，非'-'代表支持并且默认值是什么）

|属性|download()|load()|display()|
|:--|:--|:--|:--|
|name|uri|uri|memoryCacheId|
|requestLevel|NET|NET|NET|
|listener|null|null|null|
|progressListener|null|null|null|
|diskCache|true|true|true|
|maxSize|-|屏幕的1.5倍|默认会先尝试用SketchImageView的layout size作为maxSize，否则会用当前屏幕宽高的1.5倍作为maxSize|
|resize|-|null|null|
|resizeByFixedSize|-|false|false|
|forceUseResize|-|false|false|
|processor|-|null|null|
|decodeGifImage|-|null|null|
|lowQualityImage|-|false|false|
|memoryCache|-|-|true|
|memoryCacheId|-|-|null|
|displayer|-|-|DefaultImageDisplayer|
|loadingImage|-|-|null|
|failureImage|-|-|null|
|pauseDownloadImage|-|-|null|

###使用指南（Usage guide）

####1. 导入Sketch（Import Sketch）
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

####2. 在XML中使用SketchImageView
res/layout/item_user.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<me.xiaopan.sketch.SketchImageView
    android:id="@+id/image_main_head"
    android:layout_width="130dp"
    android:layout_height="130dp"
  />
```

####3. 在代码中设置URI显示图片
```java
SketchImageView headImageView = ...;
headImageView.displayImage("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg");
```
[点击查看SketchImageView详细使用说明](https://github.com/xiaopansky/Sketch/wiki/SketchImageView)

####4. 你可能还感兴趣的功能：
增强用户体验：
>* [使用SketchImageView代替ImageView显示图片](https://github.com/xiaopansky/Sketch/wiki/SketchImageView)
>* [使用ImageProcessor将图片变成圆形的、圆角的或者高斯模糊的](https://github.com/xiaopansky/Sketch/wiki/ImageProcessor)
>* [以渐变、缩放或更加炫酷的方式显示图片](https://github.com/xiaopansky/Sketch/wiki/ImageDisplayer)
>* [显示GIF图片](https://github.com/xiaopansky/Sketch/wiki/display-gif-image)
>* [使用maxSize防止加载过大的图片以节省内存](https://github.com/xiaopansky/Sketch/wiki/maxSize)
>* [使用resize裁剪图片](https://github.com/xiaopansky/Sketch/wiki/resize)
>* [移动网络下暂停下载图片，节省流量](https://github.com/xiaopansky/Sketch/wiki/pauseDownload)
>* [实现列表滑动时暂停加载图片，在较旧的设备上进一步提升滑动流畅度](https://github.com/xiaopansky/Sketch/wiki/pauseLoad)

改变Sketch的配置：
>* [了解和自定义inSampleSize计算规则](https://github.com/xiaopansky/Spear/wiki/inSampleSize)
>* [了解和配置内存缓存](https://github.com/xiaopansky/Sketch/wiki/MemoryCache)
>* [了解和配置本地缓存](https://github.com/xiaopansky/Sketch/wiki/DiskCache)
>* [了解和配置下载器](https://github.com/xiaopansky/Sketch/wiki/ImageDownloader)
>* [了解和配置任务执行器](https://github.com/xiaopansky/Sketch/wiki/RequestExecutor)

其它功能：
>* [监听加载开始、成功、失败以及进度](https://github.com/xiaopansky/Sketch/wiki/listener)
>* [使用RequestOptions定义属性模板来简化属性设置](https://github.com/xiaopansky/Sketch/wiki/RequestOptions)
>* [显示APK的图标](https://github.com/xiaopansky/Sketch/wiki/display-apk-icon)
>* [了解何时取消请求以及如何主动取消请求](https://github.com/xiaopansky/Sketch/wiki/CancelRequest)
>* [配置混淆（Proguard）](https://github.com/xiaopansky/Sketch/wiki/proguard-configuration)

###Thanks
[koral](https://github.com/koral--) - [android-gif-drawable](https://github.com/koral--/android-gif-drawable)

###License
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