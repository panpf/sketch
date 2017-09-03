# ![Logo](docs/res/logo.png) Sketch Image Loader

![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Sketch-orange.svg?style=true)](https://android-arsenal.com/details/1/4165)
[![License](https://img.shields.io/badge/License-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Logs](https://img.shields.io/github/release/panpf/sketch.svg?label=Logs&colorB=4AC41C)](https://github.com/panpf/sketch/releases)
[![Version](https://img.shields.io/github/release/panpf/sketch.svg?label=JCenter&colorB=4AC41C)](https://bintray.com/panpf/maven/sketch/_latestVersion#files)
[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)
![QQ Group](https://img.shields.io/badge/QQ%20Gruop-529630740-red.svg)

[中文版本 README.md](README.md)

Sketch is a powerful and comprehensive picture loader on Android, in addition to the necessary features to load the picture, but also supports GIF, gesture zoom, block display huge image, automatically correct the direction of the picture, display video thumbnails and other functions

### Sample APP

![SampleApp](docs/sketch-sample.png)

Scan a two-dimensional code to download a sample APP, or [click to download APK](docs/sketch-sample.apk)

### Supported Features

* `Multiple URL support`. Support for `http://` or `https://`、`asset://`、`content://`、`file:///sdcard/sample.jpg` or `/sdcard/sample.jpg`、`drawable://`、`data:image/` or  `data:img/`6 kinds of URI
* `Support gif`. Integrated [android-gif-drawable] 1.2.6 can be easily displayed gif pictures, thanks [koral--]
* `Support gesture zoom`. Support gesture zoom function, optimized on [PhotoView] (https://github.com/chrisbanes/PhotoView), added scroll bar, positioning and other functions
* `Support block display huge image`. Support block display huge image function, from then the huge image is not afraid
* `Support level 3 cache`. Through the LruMemoryCache, LruDiskCache multiplexing pictures to speed up the display time; through the LruBitmapPool reuse Bitmap, to reduce the Caton caused by GC
* `Support correcting picture orientation`. Can correct the direction of the image is not correct, and block display huge image function also supports only jpeg format pictures
* `Support reading APK icon`. Support to directly read the local APK file icon or according to the package name and version number to read the icon has been installed APP
* `Support Base64 image`. Support parse of Base64 format image
* `Support various list`. Can be used in a variety of lists (ListView, RecyclerView), and does not occupy the setTag () method
* `Automatically prevents excessive loading Bitmap` Can be controlled by maxSize to load the size of the image memory, the default for the ImageView layout_width and layout_height or screen size
* `Exclusive TransitionDrawable support`. Exclusive support for any size of the two images using TransitionDrawable transition display, to ensure that no deformation
* `Only to load or only to download`. In addition to display () method can display pictures, you can also load () method to load the picture only to memory or by download () method to download the picture to the local
* `Paused download on mobile network`. Built-in mobile network to download pictures under the suspended function, you can simply open
* `Automatically select the appropriate Bitmap.Config`. According to the picture MimeType automatically select the appropriate Bitmap.Config, reduce memory waste, for example, for JPEG format images will use Bitmap.Config.RGB_565 decoding
* `Special file preprocessing`. Through the ImagePreprocessor can be special files (such as multimedia files) for pretreatment, extract the images it contains, read the APK file icon is achieved through this function
* `Powerful and flexible customization`. Can be customized to download, cache, decoding, processing, display, placeholder and other links

### Supported URI

|Type|Scheme|Method In SketchImageView|
|:---|:---|:---|
|File in network|http://, https:// |displayImage(String)|
|File in SDCard|/, file:// |displayImage(String)|
|Content Provider|content:// |displayContentImage(Uri)|
|Asset in app|asset:// |displayAssetImage(String)|
|Resource in app|resource:// |displayResourceImage(int)|
|Base64|data:image/, data:/img/ |displayImage(String)|

### Supported Image Format

|Image Type|Supported Version|
|:---|:---|
|jpeg|[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)|
|png|[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)|
|gif|[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)|
|bmp|[![API](https://img.shields.io/badge/API-10%2B-orange.svg)](https://android-arsenal.com/api?level=10)|
|webp|[![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=14)|

### Getting Started

#### Import Sketch

1.Add dependencies to the dependencies node of the app's build.gradle file

```groovy
compile 'me.xiaopan:sketch:$sketch_version'
```

Replace `$sketch_version` with the latest version [![Version](https://img.shields.io/github/release/panpf/sketch.svg?label=JCenter&colorB=4AC41C)](https://bintray.com/panpf/maven/sketch/_latestVersion#files) `(Do not "v")`

If you need to play GIF add sketch-gif dependencies

```groovy
compile 'me.xiaopan:sketch-gif:$sketch_gif_version'
```

Replace `$sketch_gif_version` with the latest version [![Version](https://img.shields.io/github/release/panpf/sketch.svg?label=JCenter&colorB=4AC41C)](https://bintray.com/panpf/maven/sketch-gif/_latestVersion#files) `(Do not "v")`

`Android Studio automatically merges the permissions and proguard in the AAR`

2.If need compatible with API 13 (Android 3.2) and below, then need to call release cache method in Application (Android 4.0 above can be directly through the Context registration and callback)

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

#### Use SketchImageView Display Image

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

#### More Features:

Basic functions:
* [SketchImageView Detailed Instructions](docs/wiki/sketch_image_view.md)
* [Options & Helper](docs/wiki/options_and_helper.md)
* [Manage Multiple Options](docs/wiki/options_manage.md)
* [Just load the picture into memory or just download the picture to the local](docs/wiki/load_and_download.md)
* [Play gif image](docs/wiki/display_gif_image.md)
* [Gesture zoom, rotate the picture](docs/wiki/zoom.md)
* [Blocked display of huge image](docs/wiki/huge_image.md)
* [Use ShapeSize to change the size of the image when drawing](docs/wiki/shape_size.md)
* [Use ImageShaper to draw pictures in circles, rounded corners, and so on](docs/wiki/image_shaper.md)
* [ImageProcessor through the image into a Gaussian fuzzy, reflection](docs/wiki/process_image.md)
* [Through the ImageDisplay to transition, fade, etc. to display pictures](docs/wiki/displayer.md)
* [Control the picture size by MaxSize](docs/wiki/max_size.md)
* [Resize the image size by Resize](docs/wiki/resize.md)
* [Flexible use of various images as loading image by StateImage](docs/wiki/state_image.md)
* [Learn to automatically correct image orientation](docs/wiki/correct_image_orientation.md)
* [Display video thumbnail](docs/wiki/display_video_thumbnail.md)

To further enhance the user experience:
* [So that any size of the two pictures can use TransitionDrawable transition display](docs/wiki/transition_displayer.md)
* [thumbnailMode property to show clearer thumbnails](docs/wiki/thumbnail_mode.md)
* [cacheProcessedImageInDisk property cache through the need for complex processing of pictures, to enhance the display speed](docs/wiki/cache_processed_image_in_disk.md)
* [Mobile network to suspend downloading pictures, save traffic](docs/wiki/pause_download.md)
* [Pause loading of images while sliding the list to improve fluency](docs/wiki/pause_load.md)
* [Display APK or installed APP icon](docs/wiki/display_apk_or_app_icon.md)
* [Through the MemoryCache StateImage first show more vague picture, and then display a clear picture](docs/wiki/memory_cache_state_image.md)

More:
* [Learn about inSampleSize Calculation Rule](docs/wiki/in_sample_size.md)
* [Understanding and Configuring Bitmap Pool](docs/wiki/bitmap_pool.md)
* [Understanding and Configuring Memory Cache](docs/wiki/memory_cache.md)
* [Understanding and Configuring Local Cache](docs/wiki/disk_cache.md)
* [Learn and configure HttpStack](docs/wiki/http_stack.md)
* [Listen for loading start, success, failure, and download progress](docs/wiki/listener.md)
* [Learn when to cancel a request and how to cancel the request](docs/wiki/cancel_request.md)
* [The Sketch exception is monitored by ErrorTracker](docs/wiki/error_tracker.md)
* [Learn about Sketch logs](docs/wiki/log.md)
* [Delay config Sketch](docs/wiki/initializer.md)
* [Configuration confusing (Proguard)](docs/wiki/proguard_config.md)

### Thanks

* [koral--] - [android-gif-drawable]
* [chrisbanes] - [PhotoView]
* [bumptech] - [glide]（BitmapPool）

### Contact Me

* ![Email](https://img.shields.io/badge/Email-sky@xiaopan.me-red.svg)
* ![QQ Group](https://img.shields.io/badge/QQ%20Group-529630740-red.svg)

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
