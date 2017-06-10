此版本主要新增了纠正图片方向功能、对 Base64 格式图片的支持以及增加对延迟配置 Sketch的 支持，另外对示例 app 进行了大的升级改造

### 升级
:fire::fire:如果你使用了以下功能就不能无痛升级:fire::fire:
* SketchMonitor
* ImageViewInterface
* 根据枚举存储和获取 Options
* SketchImageView 的setSupportZoom(boolean)、setSupportLargeImage(boolean) 等方法
* 自定义 ImageDecoder
* 自定义 ImagePreprocessor

### :sparkles: 自动纠正图片方向
* 新增纠正图片方向功能 [了解更多](https://github.com/xiaopansky/sketch/blob/master/docs/wiki/correct_image_orientation.md)
* 功能默认开启，可通过 `correctImageOrientationDisabled` 属性关闭
* 一般的图片支持自动纠正方向，分块显示超大图也支持
* 可通过 SketchDrawable.getExifOrientation() 方法获得图片的 exif 方向

### :sparkles: Base64格式图片支持
* 新增对Base64格式图片的支持，并且支持 `data:image/` 和 `data:img/` 两种写法
* 对于 Base64 格式的图片会首先会缓存到磁盘上再读取
* 支持 Sketch 所有功能，ImageProcessor、MaxSize、Resize等

### :sparkles: Initializer
* 新增 Initializer 可以在 AndroidManifest.xml 中指定配置类，这样就不用再在 Application 中配置了，可减轻 Application 的负担，也可百分之百保证第一时间完成对 Sketch 的配置，详情请参考[initializer.md](https://github.com/xiaopansky/sketch/blob/master/docs/wiki/initializer.md)

### ImageDisplayer
* :art: 现在TransitionDisplayer遇到两张一模一样的图片时不再执行过渡动画

### Download
* :bug: 修复下载进度不回调的bug
* :art: 下载进度回调间隔由1000毫秒减少到100毫秒，这样进度的变化更明显

### ImageProcessor
* :hammer: WrapableImageProcessor 重命名为 WrappedImageProcessor

### Drawable：
* :hammer: LoadingDrawable重命名为SketchLoadingDrawable
* :hammer: RefBitmapDrawable重命名为SketchRefBitmapDrawable
* :hammer: ShapeBitmapDrawable重命名为SketchShapeBitmapDrawable
* :hammer: RefBitmap重命名为SketchRefBitmap
* :hammer: RefDrawable重命名为SketchRefDrawable
* :sparkles: 新增 SketchTransitionDrawable，TransitionImageDisplayer会使用，现在你可以直接转成SketchDrawable然后获取图片的相关信息，例如`((SketchDrawable)sketchImageView.getDrawable()).getOriginWidth()`

### ImageDecoder：
* :hammer: 移除 DefaultImageDecoder，现在 ImageDecoder 是一个 class 可以直接使用，如果你有自定义实现 ImageDecoder 的，现在需要直接继承 ImageDecoder

### ImagePreprocessor
* :hammer: isSpecific(LoadRequest) 方法改为 match(Context, UriInfo)，只是方法名和入参变了，作用没变
* :hammer: process(LoadRequest) 方法改为 process(Context, UriInfo)，只是入参变了，作用没变
* :hammer: 内部实现重构，改为一个个小的 Preprocessor，同样也有 match(Context, UriInfo) 和 process(Context, UriInfo) 方法，你可以通过ImagePreprocessor.addPreprocessor() 方法添加一个自定义的子预处理器进来

`由于重构了 ImagePreprocessor 的实现，因此有重写需求的需要重新适配，可参考 sample app`

### ImageZoomer
* :art: 现在如果 ImageZoomer 已经消费了触摸事件就不再往下传递了
* :art: 现在 ImageZoomer 可以回调 ImageView 的 OnClickListener和 OnLongClickListener，但 OnViewTapListener 和 OnViewLongPressListener 的优先级更高

### Request：
* :hammer: DisplayListener.onCompleted(ImageFrom, String) 参数改为 DisplayListener.onCompleted(Drawable, ImageFrom, ImageAttrs)
* :hammer: DisplayHelper.options()、LoadHelper.options()、DownloadHelper.options() 内部处理由合并改为完全覆盖。由此带来的影响，如下示例：
    ```java
    DisplayOptions options = new DisplayOptions();

    Sketch.with(context).display("http://...", imageView)
        .decodeGifImage()
        .options(options)
        .commit();
    ```
    这段代码，之前的效果 decodeGifImage 属性的值最终是 true，因为合并时 true 优先。现在改为完全覆盖后最终的值就是 false，因为 options里 decodeGifImage 属性是 false

### SketchImageView
* :hammer: getDisplayParams() 方法改名为 getDisplayCache()
* :fire: 修复在显示错误时点击重试的时候会意外的跳过移动网络暂停下载功能的bug
* :sparkles: 新增 redisplay(RedisplayListener) 方法可按照上次的配置重新显示
* :sparkles: 新增 setClickPlayGifEnabled(Drawable) 方法可开启点击播放gif功能
* :hammer: displayURIImage(Uri) 重命名为 displayContentImage(Uri)
* :hammer: setClickRetryOnError(boolean) 重命名为 setClickRetryOnDisplayErrorEnabled(boolean)
* :hammer: setClickRetryOnPauseDownload(boolean) 重命名为 setClickRetryOnPauseDownloadEnabled(boolean)
* :hammer: isShowDownloadProgress() 重命名为 isShowDownloadProgressEnabled()
* :hammer: setShowDownloadProgress(boolean) 重命名为 setShowDownloadProgressEnabled(boolean)
* :hammer: isShowPressedStatus() 重命名为 isShowPressedStatusEnabled()
* :hammer: setShowPressedStatus(boolean) 重命名为 setShowPressedStatusEnabled(boolean)
* :hammer: isShowImageFrom() 重命名为 isShowImageFromEnabled()
* :hammer: setShowImageFrom(boolean) 重命名为 setShowImageFromEnabled(boolean)
* :hammer: isShowGifFlag() 重命名为 isShowGifFlagEnabled()
* :hammer: setShowGifFlag(Drawable) 重命名为 setShowGifFlagEnabled(Drawable)
* :hammer: setShowGifFlag(int) 重命名为 setShowGifFlagEnabled(int)
* :hammer: isSupportZoom() 重命名为 isZoomEnabled()
* :hammer: setSupportZoom(boolean) 重命名为 setZoomEnabled(boolean)
* :hammer: isSupportLargeImage() 重命名为 isBlockDisplayLargeImageEnabled()
* :hammer: setSupportLargeImage(boolean) 重命名为 setBlockDisplayLargeImageEnabled(boolean)

### SketchMonitor：
* :hammer: 改名为 ErrorTracker
* :hammer: onInBitmapException(String, int, int, int, Bitmap) 方法改为 onInBitmapDecodeError(String, int, int, String, Throwable, int, Bitmap)
* :fire: 删除 onInBitmapExceptionForRegionDecoder(String, int, int, Rect, int, Bitmap) 方法
* :sparkles: 新增 onDecodeRegionError(String, int, int, String, Throwable, Rect, int) 方法，用于通报使用 BitmapRegionDecoder 解码图片时候发生的错误
* :hammer: 新增 onNotFoundGifSoError(Throwable) 方法，用于准确通报找不到 gif so 文件错误，onDecodeGifImageError(Throwable, LoadRequest, int, int, String) 方法将不会再收到找不到 gif so 文件错误

### Options
:fire: 整个移除使用枚举存储和获取Options的功能。为何要移除？经实际使用发现，即使在Application中第一时间存储Options，也会出现取不到本应该存在的Options的情况，因此推荐改用懒加载的方式管理Options，详情可参考Demo里的 [ImageOptions.java](https://github.com/xiaopansky/sketch/blob/master/sample/src/main/java/me/xiaopan/sketchsample/ImageOptions.java) 或 [如何管理多个Options.md](https://github.com/xiaopansky/sketch/blob/master/docs/wiki/options_manage.md)。涉及以下方法
  * DisplayHelper.optionsByName(Enum<?>)
  * LoadHelper.optionsByName(Enum<?>)
  * DownloadHelper.optionsByName(Enum<?>)
  * SketchImageView.setOptionsByName(Enum<?>)
  * Sketch.putOptions(Enum<?>, DownloadOptions)
  * Sketch.putOptions(Enum<?>, LoadOptions)
  * Sketch.putOptions(Enum<?>, DisplayOptions)
  * Sketch.DownloadOptions getDownloadOptions(Enum<?>)
  * Sketch.LoadOptions getLoadOptions(Enum<?>)
  * Sketch.DisplayOptions getDisplayOptions(Enum<?>)

### Sketch
* :hammer: Sketch.displayFromURI(Uri) 重命名为 displayFromContent(Uri)

### 其它
* :art: 优化由 inBitmap 导致的解码失败的情况的判断
* :fire: 删除 WRITE_EXTERNAL_STORAGE 权限
* :hammer: ImageFormat 重命名为 ImageType
* :arrow_up: 最低API提升至10
* :hammer: ImageViewInterface 改名为 SketchView 并移到 me.xiaopan.sketch 目录下，`所有与此相关的自定义都需要修改`
* :fire: 删除一些过时的方法
    * ImageSizeCalculator.calculateImageResize(SketchView)
    * ImageSizeCalculator.compareMaxSize(MaxSize, MaxSize)
* :hammer: 调整目录结构
    * SketchImageView 的各种 Function、LargeImageViewer 以及 ImageZoomer 由 me.xiaopan.sketch.feature 移到 me.xiaopan.sketch.viewfun
    * ImagePreprocessor 相关类由 me.xiaopan.sketch.feature移到 me.xiaopan.sketch.preprocess
    * ImageSizeCalculator 和 ResizeCalculator由 me.xiaopan.sketch.feature 移到 me.xiaopan.sketch.decode

### Sample APP：
* :hammer: 图片详情页右下角设置按钮改为长按
* :hammer: 图片详情页点击显示底部四个按钮挪到了长按--更多功能里
* :sparkles: 图片详情页点击关闭页面
* :sparkles: 增加自动纠正图片方向测试页面
* :sparkles: 增加base64图片测试页面
* :art: 优化侧滑选项的命名
* :sparkles: drawable、asset、content 来源的图片可以使用分享、保存和设置壁纸功能了
* :sparkles: 可以在任意位置长按图片查看图片信息
* :sparkles: 增加列表中点击播放gif选项
* :sparkles: 新增 Unsplash 页面，可浏览来自 Unsplash 的高质量图片
* :fire: 去掉了明星图片页面
* :sparkles: 增加我的视频列表，展示如何显示视频缩略图
* :zap: 提升扫描本地 apk 的速度
* :lipstick: 我的相册页面改为每行三个
