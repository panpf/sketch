# ![Logo](docs/res/logo.png) Sketch

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Sketch-green.svg?style=true)](https://android-arsenal.com/details/1/4165)
[![Release Version](https://img.shields.io/github/release/xiaopansky/Sketch.svg)](https://github.com/xiaopansky/Sketch/releases)

[Chinese version of the README.md](README_EN.md)

`by Google Translate`

Sketch is an image loader on Android that allows developers to read images from a local or network and display them on a page after processing

![sample](docs/res/sample.jpg)

### Features
>* ``Support gif``. Integrated [android-gif-drawable 1.1.7](https://github.com/koral--/android-gif-drawable) can be easily displayed gif pictures, thanks koral--
>* ``Multiple URL support``. Support for ``http://``、``https://``、``asset://``、``content://``、``file:///sdcard/sample.png``、``/sdcard/sample.jpg``、``drawable://``7 kinds of URI
>* ``Secondary cache``. Using Lru algorithm in the local and memory cache pictures, local cache default maximum capacity of 100M, memory cache default maximum capacity is one-eighth of the maximum available memory
>* ``Various list support``. Can be used in a variety of lists (ListView, RecyclerView), and does not occupy the setTag () method
>* ``SketchImageView``. Use Sketch ImageView to display a variety of images by simply calling the display *** Image () method, and to display frequently used functions such as download progress, click effects, click retries, etc.
>* ``Repeat the download filter``. Repeat the same picture to download, the latter will wait for the first download completion after directly the of the use of disk cache
>* ``Immediate cancellation of useless requests``. SketchImageView onDetachedFromWindow () or reused when the initiative will cancel the previous request
>* ``Automatically prevents excessive loading Bitmap`` Can be controlled by maxSize to load the size of the image memory, the default is 0.75 times the width of the screen, but also automatically according to the ImageView layout_width and layout_height to adjust maxSize
>* ``Exclusive TransitionDrawable support``. Exclusive support for any size of the two images using TransitionDrawable transition display, to ensure that no deformation
>* ``Only to load or only to download``. In addition to display () method can display pictures, you can also load () method to load the picture only to memory or by download () method to download the picture to the local
>* ``Support reading APK icon``. Support to directly read the local APK file icon or according to the package name and version number to read the icon has been installed APP
>* ``Paused download on mobile network``. Built-in mobile network to download pictures under the suspended function, you can simply open
>* ``Automatically select the appropriate Bitmap.Config``. According to the picture MimeType automatically select the appropriate Bitmap.Config, reduce memory waste, for example, for JPEG format images will use Bitmap.Config.RGB_565 decoding
>* ``Special file preprocessing``. Provide Image Preprocessor, can be on the local special files (such as multimedia files) for pretreatment, Sketch can directly display its cover, read the APK file icon is achieved through this function
>* ``Powerful and flexible customization``. Can be customized to download, cache, decoding, processing, display, placeholder and other links

### Example APP
![SampleApp](docs/sketch-sample.png)

Scan a two-dimensional code to download a sample APP, or [click to download APK](docs/sketch-sample.apk)

### Examples
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

[SketchImageView Detailed Instructions.md](docs/wiki/sketch_image_view.md)

### User's guidance

#### Import from JCenter

```groovy
dependencies{
	compile 'me.xiaopan:sketch:lastVersionName'
}
```
`lastVersionName`：[![Release Version](https://img.shields.io/github/release/xiaopansky/Sketch.svg)](https://github.com/xiaopansky/Sketch/releases)`（not With v）`

Add the following permissions
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

Minimum support for `Android2.2 API 7`

#### Supported URIs

|Type|Scheme|Method|
|:---|:---|:---|
|File in network|http://, https:// |displayImage(String)|
|File in SDCard|/, file:// |displayImage(String)|
|Content Provider|content:// |displayURIImage(Uri)|
|Asset in app|asset:// |displayAssetImage(String)|
|Resource in app|resource:// |displayResourceImage(int)|

#### Supported image types

|Type|Scheme|jpeg|png|webp|gif|apk icon|
|:---|:---|:---|:--|:---|:--|:---|:---|
|File in network|http://, http:// |YES|YES|YES（Android4.0 above）|YES|NO|
|File in SDCard|/, file:// |YES|YES|YES（Android4.0 above）|YES|YES|
|Content Provider|content:// |YES|YES|YES（Android4.0 above）|YES|NO|
|Asset in app|asset:// |YES|YES|YES（Android4.0 above）|YES|NO|
|Resource in app|resource:// |YES|YES|YES（Android4.0 above）|YES|NO|

#### download()、load()、display()
Sketch total display (), load (), download () three methods available, you can choose according to your needs appropriate method
>* Download () method will download the image to the local, and to achieve local cache
>* Load () method in the download () method on the basis of loading pictures into memory, and image processing
>* The display () method, based on the load () method, caches the image in memory and displays it on the ImageView

示例：
```
// Display
Sketch.with(context).display("http://biying.png", sketchImageView)
    .loadingImage(R.drawable.image_loading)
    .commit();

// Load
Sketch.with(context).load("http://biying.png", new LoadListener() {
    @Override
    public void onCompleted(Bitmap bitmap, ImageFrom imageFrom, String mimeType) {

    }

    @Override
    public void onCompleted(GifDrawable gifDrawable, ImageFrom imageFrom, String mimeType) {

    }
	...
}).maxSize(100, 100).commit();

// Download
Sketch.with(context).download("http://biying.png", new DownloadListener() {
    @Override
    public void onCompleted(File cacheFile, boolean isFromNetwork) {

    }
	...
}).commit();
```

Load () and download () also support synchronous execution, please refer to [Synchronize load and download.md](docs/wiki/sync.md)

The following are supported by the three methods (`-` is not supported)

|属性|download()|load()|display()|
|:---|:---|:---|:---|
|sync|false|false|-|
|requestLevel|NET|NET|NET|
|listener|null|null|null|
|downloadProgressListener|null|null|null|
|disableCacheInDisk|false|false|false|
|maxSize|-|0.75 times the screen|prioritizing ImageView's layout_width and layout_height|
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

For a detailed description of each attribute, see[Configuring various properties.md](docs/wiki/options.md)

#### You may also be interested in the features:
Enhance the user experience:
>* [SketchImageView Detailed Instructions.md](docs/wiki/sketch_image_view.md)
>* [Configure various properties.md](docs/wiki/options.md)
>* [Display gif image.md](docs/wiki/display_gif_image.md)
>* [Use ImageProcessor to turn a picture into a round, rounded, or Gaussian blur.md](docs/wiki/process_image.md)
>* [Use ImageDisplayer to display pictures in a cool way (transitions, zoom, etc.).md](docs/wiki/displayer.md)
>* [Use ImagePreprocessor to display thumbnails or icons of special files .md](docs/wiki/pre_process_image.md)
>* [Use maxSize to control image size.md](docs/wiki/max_size.md)
>* [Use resize to trim image size.md](docs/wiki/resize.md)
>* [Download paused images in mobile network, saving traffic.md](docs/wiki/pause_download.md)
>* [Pause loading of images while sliding the list to improve fluency.md](docs/wiki/pause_load.md)

Other：
>* [Synchronize load and download.md](docs/wiki/sync.md)
>* [Learn about inSampleSize Calculation Rule.md](docs/wiki/in_sample_size.md)
>* [Understanding and Configuring Memory Cache.md](docs/wiki/memory_cache.md)
>* [Understanding and Configuring Local Cache.md](docs/wiki/disk_cache.md)
>* [Learn and configure HttpStack.md](docs/wiki/http_stack.md)
>* [Listen for loading start, success, failure, and download progress.md](docs/wiki/listener.md)
>* [Display APK or installed APP icon.md](docs/wiki/display_apk_or_app_icon.md)
>* [Learn when to cancel a request and how to cancel the request.md](docs/wiki/cancel_request.md)
>* [Use ExceptionMonitor to monitor the Sketch exception.md](docs/wiki/error_callback.md)
>* [Configuration confusing (Proguard).md](docs/wiki/proguard_config.md)

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
