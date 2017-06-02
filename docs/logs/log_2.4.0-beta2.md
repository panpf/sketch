此版本主要是优化了纠正图片方向功能以及一些内在功能的实现，还要就是增加了对Base64图片的支持

:fire::fire::fire: 不能无痛升级 :fire::fire::fire:

### :sparkles: Base64格式图片支持
* 新增支持Base64格式的图片，支持data:image/和data:img/两种写法
* 对于Base64格式的图片会首先会缓存到磁盘上再读取
* 支持Sketch所有功能

### 纠正图片方向功能
* :hammer: 默认由关闭改为开启，相关控制属性也改为了correctImageOrientationDisabled
* :hammer: SketchDrawable.getOrientation()方法改为getExifOrientation()方法，并且返回的是原始exif方向
* :bug: 修复缩略图功能没有正确旋转的bug
* :bug: 修复读取已处理缓存图片时读取原图尺寸后没有按照图片方向旋转的bug
* :bug: 修复纠正图片方向时只处理了方向，没有处理翻转的bug

### Drawable：
* :hammer: LoadingDrawable重命名为SketchLoadingDrawable
* :hammer: RefBitmapDrawable重命名为SketchRefBitmapDrawable
* :hammer: ShapeBitmapDrawable重命名为SketchShapeBitmapDrawable
* :hammer: RefBitmap重命名为SketchRefBitmap
* :hammer: RefDrawable重命名为SketchRefDrawable
* :sparkles: 新增SketchTransitionDrawable，TransitionImageDisplayer会使用，现在你可以直接转成SketchDrawable然后获取图片的相关信息，例如`((SketchDrawable)sketchImageView.getDrawable()).getOriginWidth()`

### ImageDecoder：
* :hammer: 移除DefaultImageDecoder，现在ImageDecoder是一个class可以直接使用，如果你有自定义实现ImageDecoder的，现在需要直接继承ImageDecoder

### ImagePreprocessor
* :hammer: isSpecific(LoadRequest)方法改为match(Context, UriInfo)，只是方法名和入参变了，作用没变
* :hammer: process(LoadRequest)方法改为process(Context, UriInfo)，只是入参变了，作用没变
* :hammer: 内部实现重构，改为一个个小的Preprocessor，同样也有match(Context, UriInfo)和process(Context, UriInfo)方法，你可以通过ImagePreprocessor.addPreprocessor()方法添加一个自定义的子预处理器进来

`由于重构了ImagePreprocessor的实现，因此有重写需求的需要重新适配，可参考sample app`

### Request：
* :hammer: DisplayListener.onCompleted(ImageFrom, String)参数改为DisplayListener.onCompleted(Drawable, ImageFrom, ImageAttrs)
* :hammer: DisplayHelper.options()、LoadHelper.options()、DownloadHelper.options()内部处理由合并改为完全覆盖。由此带来的影响，如下示例：
    ```java
    DisplayOptions options = new DisplayOptions();

    Sketch.with(context).display("http://...", imageView)
        .decodeGifImage()
        .options(options)
        .commit();
    ```
    这段代码，之前的效果decodeGifImage属性的值最终是true，因为合并时true优先。现在改为完全覆盖后最终的值就是false，因为options里decodeGifImage属性是false

### SketchImageView
* :hammer: getDisplayParams() 方法改名为 getDisplayCache()
* :fire: 修复在显示错误时点击重试的时候会意外的跳过移动网络暂停下载功能
* :sparkles: 新增 redisplay(RedisplayListener) 方法可按照上次的配置重新显示
* :sparkles: 新增 displayContentImage(Uri) 方法代替 displayURIImage(Uri) 方法
* :sparkles: 新增 setClickRetryOnDisplayErrorEnabled(boolean) 方法代替 setClickRetryOnError(boolean) 方法
* :sparkles: 新增 setClickRetryOnPauseDownloadEnabled(boolean) 方法代替 setClickRetryOnPauseDownload(boolean) 方法
* :sparkles: 新增 setClickPlayGifEnabled(Drawable) 方法可开启点击播放gif功能
* :sparkles: 新增 isShowDownloadProgressEnabled() 方法代替 isShowDownloadProgress() 方法
* :sparkles: 新增 setShowDownloadProgressEnabled(boolean) 方法代替 setShowDownloadProgress(boolean) 方法
* :sparkles: 新增 isShowPressedStatusEnabled() 方法代替 isShowPressedStatus() 方法
* :sparkles: 新增 setShowPressedStatusEnabled(boolean) 方法代替 setShowPressedStatus(boolean) 方法
* :sparkles: 新增 isShowImageFromEnabled() 方法代替 isShowImageFrom() 方法
* :sparkles: 新增 setShowImageFromEnabled(boolean) 方法代替 setShowImageFrom(boolean) 方法
* :sparkles: 新增 isShowGifFlagEnabled() 方法代替 isShowGifFlag() 方法
* :sparkles: 新增 setShowGifFlagEnabled(Drawable) 方法代替 setShowGifFlag(Drawable) 方法
* :sparkles: 新增 setShowGifFlagEnabled(int) 方法代替 setShowGifFlag(int) 方法
* :sparkles: 新增 isZoomEnabled() 方法代替 isSupportZoom() 方法
* :sparkles: 新增 setZoomEnabled(boolean) 方法代替 setSupportZoom(boolean) 方法
* :sparkles: 新增 isBlockDisplayLargeImageEnabled() 方法代替 isSupportLargeImage() 方法
* :sparkles: 新增 setBlockDisplayLargeImageEnabled(boolean) 方法代替 setSupportLargeImage(boolean) 方法

### SketchMonitor：
* :hammer: 改名为ErrorTracker
* :hammer: onInBitmapException(String, int, int, int, Bitmap)方法改为onInBitmapDecodeError(String, int, int, String, Throwable, int, Bitmap)
* :fire: 删除onInBitmapExceptionForRegionDecoder(String, int, int, Rect, int, Bitmap)方法
* :sparkles: 新增onDecodeRegionError(String, int, int, String, Throwable, Rect, int)方法，用于通报使用BitmapRegionDecoder解码图片时候发生的错误
* :hammer: 新增onNotFoundGifSoError(Throwable)方法，用于准确通报找不到gif so文件错误，onDecodeGifImageError(Throwable, LoadRequest, int, int, String)方法将不会再收到找不到gif so文件错误

### 其它：
* :art: 优化由inBitmap导致的解码失败的情况的判断
* :sparkles: Sketch新增displayFromContent(Uri)方法用来代替displayFromURI(Uri)
* :fire: 删除WRITE_EXTERNAL_STORAGE权限

### 调整目录结构：
* :hammer: 调整目录结构，SketchImageView的各种Function、LargeImageViewer以及ImageZoomer由me.xiaopan.sketch.feature移到me.xiaopan.sketch.viewfun
* :hammer: 调整目录结构，ImagePreprocessor相关类由me.xiaopan.sketch.feature移到me.xiaopan.sketch.preprocess
* :hammer: ImageSizeCalculator和ResizeCalculator由me.xiaopan.sketch.feature移到me.xiaopan.sketch.decode

### Sample App：
* :sparkles: 增加自动纠正图片方向测试页面
* :sparkles: 增加base64图片测试页面
* :art: 优化侧滑选项的命名
* :sparkles: drawable、asset、content来源的图片可以使用分享、保存和设置壁纸功能了
* :sparkles: 可以在任意位置长按图片查看图片信息
* :sparkles: 增加列表中点击播放gif选项
