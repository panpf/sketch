# ![Logo][logo_image] Sketch Image Loader

![Platform][platform_image]
[![Android Arsenal][android_arsenal_image]][android_arsenal_link]
[![License][license_image]][license_link]
[![Logs][logs_image]][logs_link]
[![Version][sketch_version_image]][sketch_version_link]
[![API][api_10_image]][api_10_link]
![QQ Group][qq_group_image]

[中文版本 README.md](README.md)

Sketch is a powerful and comprehensive picture loader on Android, in addition to the necessary features to load the picture, but also supports GIF, gesture zoom, block display huge image, automatically correct the direction of the picture, display video thumbnails and other functions

### Sample APP

![sample_app_download_qrcode]

Scan qrcode to download, or [Click me download][sample_app_download_link]

### Supported Features

* `Multiple URL support`. Support `http://`、`https://`、`asset://`、`content://`、`file:///sdcard/sample.jpg`、 `/sdcard/sample.jpg`、`drawable://`、`data:image/`、`data:img/`、`android.resource://`、`apk.icon://`、`app.icon://` URI, The supported URIs can also be extended via [UriModel]
* `Support gif`. Include [android-gif-drawable] 1.2.6 can be easily displayed gif pictures
* `Support gesture zoom`. Support gesture zoom function, optimized on [PhotoView] (https://github.com/chrisbanes/PhotoView), added scroll bar, positioning and other functions
* `Support block display huge image`. Support block display huge image function, from then the huge image is not afraid
* `Support level 3 cache`. Through the LruMemoryCache, LruDiskCache multiplexing pictures to speed up the display time; through the LruBitmapPool reuse Bitmap, to reduce the Caton caused by GC
* `Support correcting picture orientation`. Can correct the direction of the image is not correct, and block display huge image function also supports only jpeg format pictures
* `Support display APK or APP icon`. Support to display local APK file icon or according to the package name and version number to display installed APP icon
* `Support Base64 image`. Support parse of Base64 format image
* `Support various list`. Can be used in a variety of lists (ListView, RecyclerView), and does not occupy the setTag () method
* `Automatically prevents excessive loading Bitmap` Can be controlled by maxSize to load the size of the image memory, the default for the ImageView layout_width and layout_height or screen size
* `Exclusive TransitionDrawable support`. Exclusive support for any size of the two images using TransitionDrawable transition display, to ensure that no deformation
* `Only to load or only to download`. In addition to display () method can display pictures, you can also load () method to load the picture only to memory or by download () method to download the picture to the local
* `Paused download on mobile network`. Built-in mobile network to download pictures under the suspended function, you can simply open
* `Automatically select the appropriate Bitmap.Config`. According to the picture MimeType automatically select the appropriate Bitmap.Config, reduce memory waste, for example, for JPEG format images will use Bitmap.Config.RGB_565 decoding
* `Powerful and flexible customization`. Can be customized to URI Support, HTTP, download, cache, decoding, processing, display, placeholder and other links

### Supported URI

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

Please refer to the details [URI type and usage guide][uri]

### Supported Image Format

|Image Format|Supported Version|
|:---|:---|
|jpeg|[![api_10_image]][api_10_link]|
|png|[![api_10_image]][api_10_link]|
|gif|[![api_10_image]][api_10_link]|
|bmp|[![api_10_image]][api_10_link]|
|webp|[![api_14_image]][api_14_link]|

### Getting Started

#### Import Sketch

1.Add dependencies to the dependencies node of the app's build.gradle file

```groovy
compile 'me.xiaopan:sketch:$sketch_version'
```

Replace `$sketch_version` with the latest version [![sketch_version_image]][sketch_version_link] `(Do not "v")`

If you need to play GIF add sketch-gif dependencies

```groovy
compile 'me.xiaopan:sketch-gif:$sketch_gif_version'
```

Replace `$sketch_gif_version` with the latest version [![sketch_gif_version_image]][sketch_gif_version_link] `(Do not "v")`

`Android Studio automatically merges the permissions and proguard in the AAR`

2.If need compatible with Android 3.2 and below, then need to call release cache method in Application (Android 4.0 above can be directly through the Context registration and callback)

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
sketchImageView.displayImage("http://t.cn/RShdS1f");
```

>* To display pictures of other types of URIs, please refer to the [URI type and usage guide][uri]
>* For more information on how to use SketchImageView [SketchImageView usage guide][uri]

#### Documents:

Basic functions:
* [URI type and usage guide][uri]
* [SketchImageView usage guide][sketch_image_view]
* [Use Options to configure the image][options]
* [Play GIF image][play_gif_image]
* [Gesture zoom, rotate the picture][zoom]
* [Blocked display of huge image][huge_image]
* [Use ShapeSize to change the size of the image when drawing][shape_size]
* [Use ImageShaper to change the shape of a picture when drawing][image_shaper]
* [Use ImageProcessor to change the picture after decoding][image_processor]
* [Use ImageDisplayer to display images in an animated manner][image_displayer]
* [Use MaxSize to read thumbnails of the right size to save memory][max_size]
* [Use Resize to precisely modify the size of the image][resize]
* [Use StateImage to set the placeholder picture and status picture][state_image]
* [Listen to ready load, success, failure, and download progress events][listener]

To further enhance the user experience:
* [Use the TransitionImageDisplayer to display the image in a natural transition gradient][transition_image_displayer]
* [Use the thumbnailMode property to display clearer thumbnails][thumbnail_mode]
* [Use the cacheProcessedImageInDisk property to cache images that require complex processing to increase the display speed][cache_processed_image_in_disk]
* [Use the MemoryCacheStateImage to display a cached, vague image before displaying a clear image][memory_cache_state_image]
* [Mobile network or traffic restrictions under the WIFI pause download pictures, saving traffic][pause_download]
* [When the list is slipping, the image is paused to increase the sliding fluence of the list][pause_load]

More:
* [Customize UriModel support new Uri types][uri_model]
* [Unified Change Options][options_filter]
* [Show video thumbnails][display_video_thumbnail]
* [Manage Multiple Options][options_manage]
* [Only load or download images][load_and_download]
* [Display icon from APK or  installed app][display_apk_or_app_icon]
* [Automatically correct image orientation][correct_image_orientation]
* [Multiplexing Bitmap reduces GC frequency and reduces Caton][bitmap_pool]
* [Cache Bitmap in memory to increase the display speed][memory_cache]
* [Cache the original file on the disk, to avoid duplication of the download][disk_cache]
* [Send HTTP request][http_stack]
* [Cancel request][cancel_request]
* [Monitor Sketch's exception][error_tracker]
* [Log][log]
* [Delay and configure Sketch][initializer]
* [Config proguard][proguard_config]

### Thanks

* [koral--] - [android-gif-drawable]
* [chrisbanes] - [PhotoView]
* [bumptech] - [glide]（BitmapPool）

### Contact Me

* ![Email][email_image]
* ![QQ Group][qq_group_image]

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


[qq_group_image]: https://img.shields.io/badge/QQ%20Gruop-529630740-red.svg

[logo_image]: docs/res/logo.png
[platform_image]: https://img.shields.io/badge/Platform-Android-brightgreen.svg
[android_arsenal_image]: https://img.shields.io/badge/Android%20Arsenal-Sketch-orange.svg?style=true
[android_arsenal_link]: https://android-arsenal.com/details/1/4165
[license_image]: https://img.shields.io/badge/License-Apache%202-blue.svg
[license_link]: https://www.apache.org/licenses/LICENSE-2.0
[logs_image]: https://img.shields.io/github/release/panpf/sketch.svg?label=Logs&colorB=4AC41C
[logs_link]: https://github.com/panpf/sketch/releases
[sketch_version_image]: https://img.shields.io/github/release/panpf/sketch.svg?label=JCenter&colorB=4AC41C
[sketch_version_link]: https://bintray.com/xiaopansky/maven/sketch/_latestVersion#files
[sketch_gif_version_image]: https://img.shields.io/github/release/panpf/sketch.svg?label=JCenter&colorB=4AC41C
[sketch_gif_version_link]: https://bintray.com/xiaopansky/maven/sketch-gif/_latestVersion#files
[api_10_image]: https://img.shields.io/badge/API-10%2B-orange.svg
[api_10_link]: https://android-arsenal.com/api?level=10
[api_14_image]: https://img.shields.io/badge/API-14%2B-blue.svg
[api_14_link]: https://android-arsenal.com/api?level=14
[email_image]: https://img.shields.io/badge/Email-sky@panpf.me-red.svg

[sample_app_download_qrcode]: docs/sketch-sample.png
[sample_app_download_link]: docs/sketch-sample.apk
[UriModel]: sketch/src/main/java/me/xiaopan/sketch/uri/UriModel.java

[uri]: docs/wiki/uri.md
[sketch_image_view]: docs/wiki/sketch_image_view.md
[options]: docs/wiki/options.md
[options_manage]: docs/wiki/options_manage.md
[load_and_download]: docs/wiki/load_and_download.md
[play_gif_image]: docs/wiki/play_gif_image.md
[zoom]: docs/wiki/zoom.md
[huge_image]: docs/wiki/huge_image.md
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
[error_tracker]: docs/wiki/error_tracker.md
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
