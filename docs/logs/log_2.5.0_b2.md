
bugs:
* :bug: 修复由于混淆了 Sketch.onTrimMemory 和 Sketch.onLowMemory 方法导致其内部调用过滤失效的 bug

sample app：
* :bug: 修复在 MyPhotos 页面如果数据量超大的话就会崩溃的 bug

[Sketch]: ../../sketch/src/main/java/me/xiaopan/sketch/Sketch.java
[SketchImageView]: ../../sketch/src/main/java/me/xiaopan/sketch/SketchImageView.java
[SketchUtils]: ../../sketch/src/main/java/me/xiaopan/sketch/util/SketchUtils.java
[ImageShaper]: ../../sketch/src/main/java/me/xiaopan/sketch/shaper/ImageShaper.java
[CancelCause]: ../../sketch/src/main/java/me/xiaopan/sketch/request/CancelCause.java
[DownloadListener]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DownloadListener.java
[LoadListener]: ../../sketch/src/main/java/me/xiaopan/sketch/request/LoadListener.java
[DisplayListener]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DisplayListener.java
[Resize]: ../../sketch/src/main/java/me/xiaopan/sketch/request/Resize.java
[LoadOptions]: ../../sketch/src/main/java/me/xiaopan/sketch/request/LoadOptions.java
[DisplayOptions]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DisplayOptions.java
[DownloadHelper]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DownloadHelper.java
[LoadHelper]: ../../sketch/src/main/java/me/xiaopan/sketch/request/LoadHelper.java
[LoadHelper]: ../../sketch/src/main/java/me/xiaopan/sketch/request/LoadHelper.java
[OptionsFilter]: ../../sketch/src/main/java/me/xiaopan/sketch/optionsfilter/OptionsFilter.java
[Configuration]: ../../sketch/src/main/java/me/xiaopan/sketch/Configuration.java
[ImageZoomer]: ../../sketch/src/main/java/me/xiaopan/sketch/viewfun/zoom/ImageZoomer.java
[Initializer]: ../../sketch/src/main/java/me/xiaopan/sketch/Initializer.java
[UriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/UriModel.java
[AppIconUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/AppIconUriModel.java
[SLog]: ../../sketch/src/main/java/me/xiaopan/sketch/SLog.java
[ImageProcessor]: ../../sketch/src/main/java/me/xiaopan/sketch/process/ImageProcessor.java
[WrappedImageProcessor]: ../../sketch/src/main/java/me/xiaopan/sketch/process/WrappedImageProcessor.java

[log]: ../wiki/log.md
[resize]: ../wiki/resize.md
[shape_size]: ../wiki/shape_size.md
[pause_download]: ../wiki/pause_download.md
[sketch_image_view]: ../wiki/sketch_image_view.md
[display_apk_or_app_icon]: ../wiki/display_apk_or_app_icon.md
[uri_model]: ../wiki/uri_model.md
[uri]: ../wiki/uri.md
[options_filter]: ../wiki/options_filter.md

[#43]: https://github.com/panpf/sketch/issues/43

[sample-video-thumbnail]: ../../sample-video-thumbnail/
