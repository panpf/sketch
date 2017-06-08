# ![Logo](docs/res/logo.png) Sketch

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Sketch-green.svg?style=true)](https://android-arsenal.com/details/1/4165)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Logs](https://img.shields.io/github/release/xiaopansky/sketch.svg?label=Logs)](https://github.com/xiaopansky/sketch/releases)
[![Version](https://img.shields.io/github/release/xiaopansky/sketch.svg?label=JCenter&colorB=green)](https://bintray.com/xiaopansky/maven/sketch/_latestVersion#files)
[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)
![QQ Group](https://img.shields.io/badge/QQ%20Gruop-529630740-red.svg)

[Chinese version of the README.md](README.md)

`by Google Translate`

Sketch is a powerful and comprehensive image loader on Android, with support for GIF, gesture zooming, block display super large image

![sample](docs/res/sample.jpg)

### Features
>* `Multiple URL support`. Support for `http:// or https://`、`asset://`、`content://`、`file:///sdcard/sample.jpg or /sdcard/sample.jpg`、`drawable://`、`data:image/ or data:img/`6 kinds of URI
>* `Support gif`. Integrated [android-gif-drawable 1.2.6](https://github.com/koral--/android-gif-drawable) can be easily displayed gif pictures, thanks koral--
>* `Support gesture zoom`. Support gesture zoom function, optimized on [PhotoView] (https://github.com/chrisbanes/PhotoView), added scroll bar, positioning and other functions
>* `Support block display large picture`. Support block display large picture function, from then the big picture is not afraid
>* `Support level 3 cache`. Through the LruMemoryCache, LruDiskCache multiplexing pictures to speed up the display time; through the LruBitmapPool reuse Bitmap, to reduce the Caton caused by GC
>* `Support correcting picture orientation`. Can correct the direction of the image is not correct, and block display large map function also supports only jpeg format pictures
>* `Support reading APK icon`. Support to directly read the local APK file icon or according to the package name and version number to read the icon has been installed APP
>* `Support Base64 image`. Support parse of Base64 format image
>* `Support various list`. Can be used in a variety of lists (ListView, RecyclerView), and does not occupy the setTag () method
>* `Automatically prevents excessive loading Bitmap` Can be controlled by maxSize to load the size of the image memory, the default for the ImageView layout_width and layout_height or screen size
>* `Exclusive TransitionDrawable support`. Exclusive support for any size of the two images using TransitionDrawable transition display, to ensure that no deformation
>* `Only to load or only to download`. In addition to display () method can display pictures, you can also load () method to load the picture only to memory or by download () method to download the picture to the local
>* `Paused download on mobile network`. Built-in mobile network to download pictures under the suspended function, you can simply open
>* `Automatically select the appropriate Bitmap.Config`. According to the picture MimeType automatically select the appropriate Bitmap.Config, reduce memory waste, for example, for JPEG format images will use Bitmap.Config.RGB_565 decoding
>* `Special file preprocessing`. Through the ImagePreprocessor can be special files (such as multimedia files) for pretreatment, extract the images it contains, read the APK file icon is achieved through this function
>* `Powerful and flexible customization`. Can be customized to download, cache, decoding, processing, display, placeholder and other links

### Example APP
![SampleApp](docs/sketch-sample.png)

Scan a two-dimensional code to download a sample APP, or [click to download APK](docs/sketch-sample.apk)

### Guide

#### Import

1.Add dependencies to the dependencies node of the app's build.gradle file

```groovy
compile 'me.xiaopan:sketch:$sketch_version'
```

Replace `$sketch_version` with the latest version [![Version](https://img.shields.io/github/release/xiaopansky/sketch.svg?label=JCenter&colorB=green)](https://bintray.com/xiaopansky/maven/sketch/_latestVersion#files) `(Do not "v")`

If you need to play GIF add sketch-gif dependencies

```groovy
compile 'me.xiaopan:sketch-gif:$sketch_gif_version'
```

Replace `$sketch_gif_version` with the latest version [![Version](https://img.shields.io/github/release/xiaopansky/sketch.svg?label=JCenter&colorB=green)](https://bintray.com/xiaopansky/maven/sketch-gif/_latestVersion#files) `(Do not "v")`

`Android Studio automatically merges the permissions and proguard in the AAR`

2.If your APP wants to be compatible with API 13 (Android 3.2) and below, then you need to call in your Application to release the cache method (Android 4.0 above can be directly through the Context registration and callback)
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

#### Display Image
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
sketchImageView.displayImage("data:image/jpeg;base,/9j/4QaO...U7T/in//Z");

// display apk icon from SDCard
sketchImageView.displayImage("/sdcard/google_play.apk");

// display installed app icon
sketchImageView.displayInstalledAppIcon("com.tencent.qq", 210);
```

[SketchImageView Detailed Instructions.md](docs/wiki/sketch_image_view.md)

#### Supported URIs
|Type|Scheme|Method In SketchImageView|
|:---|:---|:---|
|File in network|http://, https:// |displayImage(String)|
|File in SDCard|/, file:// |displayImage(String)|
|Content Provider|content:// |displayContentImage(Uri)|
|Asset in app|asset:// |displayAssetImage(String)|
|Resource in app|resource:// |displayResourceImage(int)|
|Base64|data:image/, data:/img/ |displayImage(String)|

#### Supported image types
* jpeg：[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)
* png：[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)
* gif：[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)
* bmp：[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)
* webp：[![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=14)

#### download()、load()、display()
Sketch total display (), load (), download () three methods available, you can choose according to your needs appropriate method
>* Download () method will download the image to the local, and to achieve local cache
>* Load () method in the download () method on the basis of loading pictures into memory, and image processing
>* The display () method, based on the load () method, caches the image in memory and displays it on the ImageView

Examples:
```
// Display
Sketch.with(context).display("http://t.cn/RShdS1f", sketchImageView)
    .loadingImage(R.drawable.image_loading)
    .commit();

// Load
Sketch.with(context).load("http://t.cn/RShdS1f", new LoadListener() {
    @Override
    public void onCompleted(Bitmap bitmap, ImageFrom imageFrom, String mimeType) {

    }

    @Override
    public void onCompleted(GifDrawable gifDrawable, ImageFrom imageFrom, String mimeType) {

    }
	...
}).maxSize(100, 100).commit();

// Download
Sketch.with(context).download("http://t.cn/RShdS1f", new DownloadListener() {
    @Override
    public void onCompleted(File cacheFile, boolean isFromNetwork) {

    }
	...
}).commit();
```

Load () and download () also support synchronous execution, please refer to [Synchronize load and download.md](docs/wiki/sync.md)

#### You may also be interested in the features:

Basic functions:
>* [SketchImageView Detailed Instructions](docs/wiki/sketch_image_view.md)
>* [Play gif image](docs/wiki/display_gif_image.md)
>* [Gesture zoom, rotate the picture](docs/wiki/zoom.md)
>* [Blocked display of large images](docs/wiki/large_image.md)
>* [Config Options](docs/wiki/options_config.md)
>* [Manage Options](docs/wiki/options_manage.md)
>* [Use ShapeSize to change the size of the image when drawing](docs/wiki/shape_size.md)
>* [Use ImageShaper to draw pictures in circles, rounded corners, and so on](docs/wiki/image_shaper.md)
>* [ImageProcessor through the image into a Gaussian fuzzy, reflection](docs/wiki/process_image.md)
>* [Through the ImageDisplay to transition, fade, etc. to display pictures](docs/wiki/displayer.md)
>* [Control the picture size by MaxSize](docs/wiki/max_size.md)
>* [Resize the image size by Resize](docs/wiki/resize.md)
>* [Flexible use of various images as loading image by StateImage](docs/wiki/state_image.md)
>* [Learn to automatically correct image orientation](docs/wiki/correct_image_orientation.md)
>* [Display video thumbnail](docs/wiki/display_video_thumbnail.md)

To further enhance the user experience:
>* [So that any size of the two pictures can use TransitionDrawable transition display](docs/wiki/transition_displayer.md)
>* [thumbnailMode property to show clearer thumbnails](docs/wiki/thumbnail_mode.md)
>* [cacheProcessedImageInDisk property cache through the need for complex processing of pictures, to enhance the display speed](docs/wiki/cache_processed_image_in_disk.md)
>* [Mobile network to suspend downloading pictures, save traffic](docs/wiki/pause_download.md)
>* [Pause loading of images while sliding the list to improve fluency](docs/wiki/pause_load.md)
>* [Display APK or installed APP icon](docs/wiki/display_apk_or_app_icon.md)
>* [Displaying images in special files with Image Preprocessor (eg video, MP3)](docs/wiki/pre_process_image.md)
>* [Through the MemoryCache StateImage first show more vague picture, and then display a clear picture](docs/wiki/memory_cache_state_image.md)

More:
>* [Synchronize load and download](docs/wiki/sync.md)
>* [Learn about inSampleSize Calculation Rule](docs/wiki/in_sample_size.md)
>* [Understanding and Configuring Bitmap Pool](docs/wiki/bitmap_pool.md)
>* [Understanding and Configuring Memory Cache](docs/wiki/memory_cache.md)
>* [Understanding and Configuring Local Cache](docs/wiki/disk_cache.md)
>* [Learn and configure HttpStack](docs/wiki/http_stack.md)
>* [Listen for loading start, success, failure, and download progress](docs/wiki/listener.md)
>* [Learn when to cancel a request and how to cancel the request](docs/wiki/cancel_request.md)
>* [The Sketch exception is monitored by ErrorTracker](docs/wiki/error_tracker.md)
>* [Synchronize the output of the Sketch run log to the SD card](docs/wiki/sync_out_log_to_disk.md)
>* [Delay config Sketch](docs/wiki/initializer.md)
>* [Configuration confusing (Proguard)](docs/wiki/proguard_config.md)

### Thanks

[koral](https://github.com/koral--) - [android-gif-drawable](https://github.com/koral--/android-gif-drawable)

[chrisbanes](https://github.com/chrisbanes) - [PhotoView](https://github.com/chrisbanes/PhotoView)

[bumptech](https://github.com/bumptech) - [glide](https://github.com/bumptech/glide) （BitmapPool）

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
