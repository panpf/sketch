# ![Logo](docs/res/logo.png) Sketch

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Sketch-green.svg?style=true)](https://android-arsenal.com/details/1/4165)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Released Version](https://img.shields.io/github/release/xiaopansky/Sketch.svg)](https://github.com/xiaopansky/Sketch/releases)
[![Download](https://api.bintray.com/packages/xiaopansky/maven/Sketch/images/download.svg)](https://bintray.com/xiaopansky/maven/Sketch/_latestVersion#files)
[![API](https://img.shields.io/badge/API-9%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=9)
![QQ Group](https://img.shields.io/badge/QQ%E4%BA%A4%E6%B5%81%E7%BE%A4-529630740-red.svg)

[Chinese version of the README.md](README.md)

`by Google Translate`

Sketch is a powerful and comprehensive image loader on Android, with support for GIF, gesture zooming, block display super large image

![sample](docs/res/sample.jpg)

### Features
>* ``Multiple URL support``. Support for ``http://``、``https://``、``asset://``、``content://``、``file:///sdcard/sample.png``、``/sdcard/sample.jpg``、``drawable://``7 kinds of URI
>* ``Support gif``. Integrated [android-gif-drawable 1.2.4](https://github.com/koral--/android-gif-drawable) can be easily displayed gif pictures, thanks koral--
>* ``Exclusive gesture zoom and super large image support``. Exclusive built-in gesture zoom and block display super large image features, and the combination of better
>* ``Level 3 cache support``. Through the LruMemoryCache, LruDiskCache multiplexing pictures to speed up the display time; through the LruBitmapPool reuse Bitmap, to reduce the Caton caused by GC
>* ``Various list support``. Can be used in a variety of lists (ListView, RecyclerView), and does not occupy the setTag () method
>* ``Automatically prevents excessive loading Bitmap`` Can be controlled by maxSize to load the size of the image memory, the default for the ImageView layout_width and layout_height or screen size
>* ``Exclusive TransitionDrawable support``. Exclusive support for any size of the two images using TransitionDrawable transition display, to ensure that no deformation
>* ``Only to load or only to download``. In addition to display () method can display pictures, you can also load () method to load the picture only to memory or by download () method to download the picture to the local
>* ``Support reading APK icon``. Support to directly read the local APK file icon or according to the package name and version number to read the icon has been installed APP
>* ``Paused download on mobile network``. Built-in mobile network to download pictures under the suspended function, you can simply open
>* ``Automatically select the appropriate Bitmap.Config``. According to the picture MimeType automatically select the appropriate Bitmap.Config, reduce memory waste, for example, for JPEG format images will use Bitmap.Config.RGB_565 decoding
>* ``Special file preprocessing``. Through the ImagePreprocessor can be special files (such as multimedia files) for pretreatment, extract the images it contains, read the APK file icon is achieved through this function
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

### Guide

#### Import

1.Add dependencies to the dependencies node of the app's build.gradle file

```groovy
dependencies {
	compile 'me.xiaopan:sketch:SKETCH_LAST_VERSION_NAME'
}
```

Replace `SKETCH_LAST_VERSION_NAME` with the latest version [![Download](https://api.bintray.com/packages/xiaopansky/maven/Sketch/images/download.svg)](https://bintray.com/xiaopansky/maven/Sketch/_latestVersion#files) `(Ignore “Download”)`

If you need to play GIF add sketch-gif dependencies

```groovy
dependencies{
	compile 'me.xiaopan:sketch-gif:SKETCH_GIF_LAST_VERSION_NAME'
}
```

Replace `SKETCH_GIF_LAST_VERSION_NAME` with the latest version [![Download](https://api.bintray.com/packages/xiaopansky/maven/Sketch-GIF/images/download.svg)](https://bintray.com/xiaopansky/maven/Sketch-GIF/_latestVersion#files) `(Ignore “Download”)`

2.Add the following permissions
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

3.Android 4.0 the following need release of the cache in the Application (Android 4.0 above can be registered directly through the Context and callback)
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

Minimum support for `Android2.3 API 9`

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
|:---|:---|:---|:---|:---|:---|:---|
|File in network|http://, https:// |YES|YES|YES（Android4.0 above）|YES|NO|
|File in SDCard|/, file:// |YES|YES|YES（Android4.0 above）|YES|YES|
|Content Provider|content:// |YES|YES|YES（Android4.0 above）|YES|NO|
|Asset in app|asset:// |YES|YES|YES（Android4.0 above）|YES|NO|
|Resource in app|resource:// |YES|YES|YES（Android4.0 above）|YES|NO|

#### download()、load()、display()
Sketch total display (), load (), download () three methods available, you can choose according to your needs appropriate method
>* Download () method will download the image to the local, and to achieve local cache
>* Load () method in the download () method on the basis of loading pictures into memory, and image processing
>* The display () method, based on the load () method, caches the image in memory and displays it on the ImageView

Examples:
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

#### You may also be interested in the features:

Basic functions:
>* [SketchImageView Detailed Instructions.md](docs/wiki/sketch_image_view.md)
>* [Play gif image.md](docs/wiki/display_gif_image.md)
>* [Gesture zoom, rotate the picture.md](docs/wiki/zoom.md)
>* [Blocked display of large images.md](docs/wiki/large_image.md)
>* [Configure various option and option management.md](docs/wiki/options.md)
>* [Use ShapeSize to change the size of the image when drawing.md](docs/wiki/shape_size.md)
>* [Use ImageShaper to draw pictures in circles, rounded corners, and so on.md](docs/wiki/image_shaper.md)
>* [ImageProcessor through the image into a Gaussian fuzzy, reflection.md](docs/wiki/process_image.md)
>* [Through the ImageDisplay to transition, fade, etc. to display pictures.md](docs/wiki/displayer.md)
>* [Control the picture size by MaxSize.md](docs/wiki/max_size.md)
>* [Resize the image size by Resize.md](docs/wiki/resize.md)
>* [Flexible use of various images as loading image by StateImage.md](docs/wiki/state_image.md)

To further enhance the user experience:
>* [So that any size of the two pictures can use TransitionDrawable transition display.md](docs/wiki/transition_displayer.md)
>* [thumbnailMode property to show clearer thumbnails.md](docs/wiki/thumbnail_mode.md)
>* [cacheProcessedImageInDisk property cache through the need for complex processing of pictures, to enhance the display speed.md](docs/wiki/cache_processed_image_in_disk.md)
>* [Mobile network to suspend downloading pictures, save traffic.md](docs/wiki/pause_download.md)
>* [Pause loading of images while sliding the list to improve fluency.md](docs/wiki/pause_load.md)
>* [Display APK or installed APP icon.md](docs/wiki/display_apk_or_app_icon.md)
>* [Displaying images in special files with Image Preprocessor (eg video, MP3).md](docs/wiki/pre_process_image.md)
>* [Through the MemoryCache StateImage first show more vague picture, and then display a clear picture.md](docs/wiki/memory_cache_state_image.md)

More:
>* [Synchronize load and download.md](docs/wiki/sync.md)
>* [Learn about inSampleSize Calculation Rule.md](docs/wiki/in_sample_size.md)
>* [Understanding and Configuring Bitmap Pool.md](docs/wiki/bitmap_pool.md)
>* [Understanding and Configuring Memory Cache.md](docs/wiki/memory_cache.md)
>* [Understanding and Configuring Local Cache.md](docs/wiki/disk_cache.md)
>* [Learn and configure HttpStack.md](docs/wiki/http_stack.md)
>* [Listen for loading start, success, failure, and download progress.md](docs/wiki/listener.md)
>* [Learn when to cancel a request and how to cancel the request.md](docs/wiki/cancel_request.md)
>* [The Sketch exception is monitored by SketchMonitor.md](docs/wiki/monitor.md)
>* [Synchronize the output of the Sketch run log to the SD card.md](docs/wiki/sync_out_log_to_disk.md)
>* [Configuration confusing (Proguard).md](docs/wiki/proguard_config.md)

### Thanks

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
