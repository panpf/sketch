# v...

* :arrow_up: Upgrade ti Jetpack
* :bug: Fix ShapeSize to ShapeSize.byViewFixedSize(), errorImage is not null, but error image can't always be displayed when uri is empty

# v2.6.2 Beta1

* :arrow_up: Target sdk version upgrade to 28
* :arrow_up: Support library upgrade to 28.0.0
* :arrow_up: Min sdk version upgrade to 16
* :sparkles: [#81] ImageZoomer supports custom scaling and examples are added to the demo, please see the zoom section of the document [zoom]
* :sparkles: ImageZoomer adds getBlockByDrawablePoint, getBlockByImagePoint, touchPointToDrawablePoint method
* :bug: [#83],[#73] Fix bug with black line in the large graph block display function
* :bug: Fix bug where MaskImageProcessor expires in version 21 or lower

[zoom]: docs/wiki/zoom.md
[#73]: https://github.com/panpf/sketch/issues/73
[#81]: https://github.com/panpf/sketch/issues/81
[#83]: https://github.com/panpf/sketch/issues/83

# v2.6.0 P1

这是一个重大重构版本，重点重构了 [ImageZoomer]、HugeImageViewer 以及将包名改为 me.panpf.sketch

:fire: 不能无痛升级 :fire:，请参考页尾的升级指南

### Sketch

修复 bug：
* :bug: 修复开启解码 gif 后内存缓存失效的 bug

包名重构：
* :hammer: sketch library 的包名改为 me.panpf.sketch
* :hammer: sketch-gif library 的包名改为 me.panpf.sketch.gif

ImageZoomer 重构：
* :hammer: 重构 [ImageZoomer] 的代码结构，现在逻辑更清晰易懂
* :hammer: [SketchImageView].getImageZoomer() 重命名为 getZoomer()
* :hammer: [ImageZoomer] 从 me.xiaopan.sketch.viewfun.zoom 移动到 me.xiaopan.sketch.zoom

HugeImageViewer 重构：
* :hammer: hugeImageEnabled 合并到 zoomEnabled，现在只有 zoomEnabled 一个开关
* :hammer: HugeImageViewer 从 me.xiaopan.sketch.viewfun.huge 移动到 me.xiaopan.sketch.zoom
* :hammer: HugeImageViewer 重命名为 [BlockDisplayer]
* :hammer: 移除 [SketchImageView].getHugeImageViewer() 方法，请用 [SketchImageView].getZoomer().getBlockDisplayer() 代替
* :hammer: Tile 重命名为 [Block]，并且所有 tile 相关的名字 全部改成了 block
* :hammer: [ErrorTracker].onTileSortError() 重命名为 onBlockSortError()

其它重构：
* :hammer: UriModelRegistry 重命名为 [UriModelManager]
* :hammer: OptionsFilterRegistry 重命名为 [OptionsFilterManager]
* :hammer: [HttpStack].ImageHttpResponse 重命名为 [HttpStack].Response
* :hammer: [HttpStack].getHttpResponse(String). 重命名为 [HttpStack].getResponse(String)
* :hammer: [HttpStack].ImageHttpResponse.getResponseCode() 重命名为 [HttpStack].Response.getCode()
* :hammer: [HttpStack].ImageHttpResponse.getResponseMessage() 重命名为 [HttpStack].Response.getMessage()
* :hammer: [HttpStack].ImageHttpResponse.getResponseHeadersString() 重命名为 [HttpStack].Response.getHeadersString()
* :hammer: [HttpStack].ImageHttpResponse.getResponseHeader() 重命名为 [HttpStack].Response.getHeader()
* :hammer: [ImageAttrs] 从 drawable 中移动到 decode 包中
* :hammer: 重构 [WrappedImageProcessor] 的 getKey() 和 toString() 方法，所有子类都需要重新适配

改进：
* :art: 优化 key 的格式，现在 key 的格式更加短小，并且在控制台点击跳转到浏览器后不用对 uri 做任何修改即可显示图片，但旧的已处理缓存 key 和其他的需要根据 key 缓存的磁盘缓存将全部失效

### Sample App

修复 bug:
* :bug: 修复 UNSPLASH 页面没有加载完数据就切换到别的页面时崩溃的 BUG

重构：
* :hammer: 重构包名为 me.panpf.sketch.sample


### 升级指南

* 全局搜索 `me.xiaopan.sketch.viewfun.huge.HugeImageViewer` 并替换为 `me.xiaopan.sketch.zoom.BlockDisplayer`
* 全局区分大小写搜索 `HugeImageViewer` 并替换为 `BlockDisplayer`
* 全局搜索 `me.xiaopan.sketch.viewfun.zoom.ImageZoomer` 并替换为 `me.xiaopan.sketch.zoom.ImageZoomer`
* 全局搜索 `me.xiaopan.sketch.drawable.ImageAttrs` 并替换为 `me.xiaopan.sketch.decode.ImageAttrs`
* 全局搜索 `me.xiaopan.sketch` 替换为 `me.panpf.sketch`

[ImageZoomer]: ../../sketch/src/main/java/me/panpf/sketch/zoom/ImageZoomer.java
[BlockDisplayer]: ../../sketch/src/main/java/me/panpf/sketch/zoom/BlockDisplayer.java
[Block]: ../../sketch/src/main/java/me/panpf/sketch/zoom/block/Block.java
[SketchImageView]: ../../sketch/src/main/java/me/panpf/sketch/SketchImageView.java
[WrappedImageProcessor]: ../../sketch/sketch/src/main/java/me/panpf/sketch/process/WrappedImageProcessor.java
[ErrorTracker]: ../../sketch/sketch/src/main/java/me/panpf/sketch/ErrorTracker.java
[UriModelManager]: ../../sketch/src/main/java/me/panpf/sketch/uri/UriModelManager.java
[OptionsFilterManager]: ../../sketch/src/main/java/me/panpf/sketch/optionsfilter/OptionsFilterManager.java
[HttpStack]: ../../sketch/src/main/java/me/panpf/sketch/http/HttpStack.java
[ImageAttrs]: ../../sketch/src/main/java/me/panpf/sketch/decode/ImageAttrs.java


# v2.5.0 Beta2

bugs：
* :bug: 修复由于混淆了 Sketch.onTrimMemory 和 Sketch.onLowMemory 方法导致其内部调用过滤失效的 bug

优化：
* :sparkles: 支持处理 301 302 重定向，但仅支持重定向一次

重构：
* :hammer: key 中不再包含 decodeGifImage ，受此影响已经通过 cacheProcessedImageInDisk 生成的磁盘缓存将全部失效

sample app：
* :bug: 修复在 MyPhotos 页面如果数据量超大的话就会崩溃的 bug
* :lipstick: 重构图片详情页的长按菜单
* :lipstick: 兼容 MIX 2 的全屏显示


# v2.5.0 Beta1

这是一个大的重构版本，重点重构了日志和 uri 部分

:fire: 如果你用到了下述重命名、移除、重构、重命名以及 ErrorTracker 部分的话就不能无痛升级 :fire:

bugs：
* :bug: 修复在某些 ROM 上查找 Initializer 时碰到 meta 值不是 String 时崩溃的 BUG [#43]
* :bug: 修复 [Sketch] 的单例可能失效的 BUG
* :bug: 修复在多线程环境下可能拿到一个尚未执行 [Initializer] 的 [Sketch] 的 bug
* :bug: 修复 [ImageZoomer] 的双击事件和单击事件可能会有冲突的 BUG
* :bug: 修复通过 [SketchUtils].generatorTempFileName(DataSource, String) 方法生成的文件名中含有换行符导致创建文件失败的BUG，直接影响了 DataSource.getFile(File, String) 方法
* :bug: 修复当同一个文件被用于多种 uri 支持时，其磁盘缓存可能错乱的 BUG

移除：
* :fire: 移除 SLogType，其功能整合到了 [SLog] 中，详情请参考 [日志][log]
* :fire: 移除 SLogTracker，新增 [SLog].Proxy 代替之，详情请参考 [日志][log]
* :fire: 移除 UriScheme，UriScheme.valueOfUri(String) 用 [UriModel].match(Context, String) 代替，UriScheme.xxx.createUri(String) 被 xxxUriModel.makeUri() 代替，[UriModel] 用法请参考  [UriModel 详解及扩展 URI][uri_model]
* :fire: 移除 ImagePreprocessor 用 [UriModel] 代替之，相关类 Preprocessor、PreProcessResult 也一并移除，[UriModel] 用法请参考  [UriModel 详解及扩展 URI][uri_model]
* :fire: 移除 [Sketch].createInstalledAppIconUri(String, int) 方法，[AppIconUriModel].makeUri(String, int) 方法替代之
* :fire: 移除 [DownloadHelper].listener(DownloadListener) 方法
* :fire: 移除 [LoadHelper].listener(LoadListener) 方法
* :fire: 移除 [SketchImageView].displayInstalledAppIcon(String, int) 方法，新的使用方法请参考 [显示 APK 或已安装 APP 的图标][display_apk_or_app_icon]
* :fire: 移除 [Sketch].displayInstalledAppIcon(String, int, SketchView) 方法，新的使用方法请参考 [显示 APK 或已安装 APP 的图标][display_apk_or_app_icon]
* :fire: 移除 [Sketch].loadInstalledAppIcon(String, int, LoadListener) 方法，新的使用方法请参考 [显示 APK 或已安装 APP 的图标][display_apk_or_app_icon]
* :fire: 删除 RequestLevelFrom ，因此取消原因中不再区分 REQUEST_LEVEL_IS_LOCAL 和 REQUEST_LEVEL_IS_MEMORY
* :fire: 删除 [CancelCause].REQUEST_LEVEL_IS_LOCAL 和 REQUEST_LEVEL_IS_MEMORY
* :fire: 删除 [SketchUtils].makeRequestKey(String, [UriModel], DownloadOptions) 方法，makeRequestKey(String, [UriModel], String) 方法代替之
* :fire: 删除 [SketchUtils].makeRequestKey(String, String) 方法，makeRequestKey(String, [UriModel], String) 方法代替之
* :fire: 删除 [SketchUtils].makeStateImageMemoryCacheKey(String, DownloadOptions) 方法，makeRequestKey(String, [UriModel], String) 方法代替之
* :fire: 删除 [SketchImageView].ImageShape
* :fire: 删除 [SketchImageView].getImageShape() 方法
* :fire: 删除 [SketchImageView].setImageShape([SketchImageView].ImageShape) 方法
* :fire: 删除 [SketchImageView].getImageShapeCornerRadius() 方法
* :fire: 删除 [SketchImageView].setImageShapeCornerRadius(float[]) 方法
* :fire: 删除 [SketchImageView].setImageShapeCornerRadius(float) 方法
* :fire: 删除 [SketchImageView].setImageShapeCornerRadius(float, float, float, float) 方法
* :fire: 移除 [SketchImageView] 按下状态的涟漪效果

重构：
* :hammer: file:// 格式的 uri 已产生的磁盘缓存将全部作废，因为其磁盘缓存 key 去掉了 file://
* :hammer: 现在你要显示 apk icon 就必须使用 apk.icon:// 协议，详情请参考 [显示 APK 或已安装 APP 的图标][display_apk_or_app_icon]
* :hammer: [DownloadListener]/[LoadListener]/[DisplayListener].onStarted() 方法现在只有需要进入异步线程加载或下载图片才会回调
* :hammer: [LoadHelper] 不再接受空的 [LoadListener]
* :hammer: [Sketch].displayFromContent(Uri, SketchView) 方法签名现在改为 [Sketch].displayFromContent(String, SketchView)
* :hammer: [Sketch].loadFromURI(Uri, SketchView) 方法重构为 [Sketch].loadFromContent(String, SketchView)
* :hammer: SketchImageView.displayContentImage(Uri) 方法签名现在改为 SketchImageView.displayContentImage(String)
* :hammer: [Initializer].onInitialize(Context, [Sketch], Configuration) 方法签名现在改为 onInitialize(Context, Configuration)
* :hammer: [ImageProcessor].process([Sketch], Bitmap, Resize, boolean, boolean) 方法签名现在改为 process([Sketch], Bitmap, [Resize], boolean)
* :hammer: [WrappedImageProcessor].onProcess([Sketch], Bitmap, Resize, boolean, boolean) 方法签名现在改为 onProcess([Sketch], Bitmap, Resize, boolean
* :hammer: 移除 resizeByFixedSize 属性，用 [Resize].byViewFixedSize() 代替，详情请参考 [使用 Resize 精确修改图片的尺寸][resize]
* :hammer: 移除 shapeSizeByFixedSize 属性，用 [ShapeSize].byViewFixedSize() 代替，详情请参考 [通过 ShapeSize 在绘制时改变图片的尺寸][shape_size]
* :hammer: 移除 forceUseResize 属性，用 [Resize].Mode 代替，详情请参考 [使用 Resize 精确修改图片的尺寸][resize]
* :hammer: 重构 ErrorCause 枚举属性
* :hammer: [ImageShaper] 接口新增 getPath(Rect bounds) 方法，获取形状的 Path
* :hammer: 重构 [SketchImageView] 的下载进度和按下状态功能的蒙层形状配置方式，详情请参考 [SketchImageView 使用指南][sketch_image_view]

重命名：
* :hammer: LargeImageViewer 重命名为 HugeImageViewer
* :hammer: [SketchImageView].isBlockDisplayLargeImageEnabled() 重命名为 isHugeImageEnabled()
* :hammer: [SketchImageView].setBlockDisplayLargeImageEnabled(boolean) 重命名为 setHugeImageEnabled(boolean)
* :hammer: [SketchImageView].getLargeImageViewer() 重命名为 getHugeImageViewer()
* :hammer: [Configuration].isGlobalPauseDownload() 重命名为 isPauseDownloadEnabled()
* :hammer: [Configuration].setGlobalPauseDownload(boolean) 重命名为 setPauseDownloadEnabled(boolean)
* :hammer: [Configuration].isGlobalPauseLoad() 重命名为 isPauseLoadEnabled()
* :hammer: [Configuration].setGlobalPauseLoad(boolean) 重命名为 setPauseLoadEnabled(boolean)
* :hammer: [Configuration].isGlobalLowQualityImage() 重命名为 isLowQualityImageEnabled()
* :hammer: [Configuration].setGlobalLowQualityImage(boolean) 重命名为 setLowQualityImageEnabled(boolean)
* :hammer: [Configuration].isGlobalInPreferQualityOverSpeed() 重命名为 isInPreferQualityOverSpeedEnabled()
* :hammer: [Configuration].setGlobalInPreferQualityOverSpeed(boolean) 重命名为 setInPreferQualityOverSpeedEnabled(boolean)
* :hammer: [Configuration].isGlobalMobileNetworkGlobalPauseDownload() 重命名为 isMobileDataPauseDownloadEnabled()
* :hammer: [Configuration].setGlobalMobileNetworkPauseDownload(boolean) 重命名为 setMobileDataPauseDownloadEnabled(boolean)
* :hammer: [Configuration].setImageSizeCalculator(ImageSizeCalculator) 重命名为 setSizeCalculator(ImageSizeCalculator)
* :hammer: [Configuration].getImageSizeCalculator() 方法改名为 getSizeCalculator
* :hammer: [LoadOptions].getImageProcessor() 重命名为 getProcessor()
* :hammer: [LoadOptions].setImageProcessor(ImageProcessor) 重命名为 setProcessor(ImageProcessor)
* :hammer: [DisplayOptions].getImageDisplayer() 重命名为 getDisplayer()
* :hammer: [DisplayOptions].setImageDisplayer(ImageDisplayer) 重命名为 setDisplayer(ImageDisplayer)
* :hammer: [DisplayOptions].getImageShaper() 重命名为 getShaper()
* :hammer: [DisplayOptions].setImageShaper(ImageShaper) 重命名为 setShaper(ImageShaper)

新增：
* :sparkles: 新增 [UriModel] 替代 UriScheme 和 ImagePreprocessor 实现支持不同的 uri 协议，你还可以通过 [UriModel] 轻松自定义 uri，详情请参考 [UriModel 详解及扩展 URI][uri_model]
* :sparkles: 新增 apk.icon:///sdcard/file.apk 协议来显示 apk icon，代替 ApkIconPreprocessor，详情请参考 [显示 APK 或已安装 APP 的图标][display_apk_or_app_icon]
* :sparkles: 新增 app.icon://me.xiaopan.sketchsample/240 协议来显示 app icon，代替 InstallAppIconPreprocessor，详情请参考 [显示 APK 或已安装 APP 的图标][display_apk_or_app_icon]
* :sparkles: 新增 android.resource://me.xiaopan.sketchsample/drawable/ic_launcher 协议，可显示别的 APP 的资源图片，详情请参考 [URI 类型及使用指南][uri]
* :sparkles: 新增 [OptionsFilter] 可统一过滤修改 Options，详情请参考 [统一修改 Options][options_filter]

优化：
* :zap: 移动数据暂停下载功能支持识别流量共享 WIFI 热点，更多内容请参考 [移动数据下暂停下载图片，节省流量][pause_download]
* :zap: 现在 sketch 里大部分对外的接口都加上了 @NonNull 或 @Nullable 注解，因此现在需要明确依赖 support-annotations

sample app：
* 修复了 gif 图无法播放的问题
* MyPhotos 列表的图标可以变成圆角矩形的，由 "Show Round Rect In Photo List" 开关控制
* 默认关闭 长按显示图片详情的功能，交由 "Long Clock Show Image Info" 开关控制
* 现在视频缩略图模块 从 sample app 中抽离出去成为一个单独的 module [sample-video-thumbnail]

[Sketch]: ../../sketch/src/main/java/me/panpf/sketch/Sketch.java
[SketchImageView]: ../../sketch/src/main/java/me/panpf/sketch/SketchImageView.java
[SketchUtils]: ../../sketch/src/main/java/me/panpf/sketch/util/SketchUtils.java
[ImageShaper]: ../../sketch/src/main/java/me/panpf/sketch/shaper/ImageShaper.java
[CancelCause]: ../../sketch/src/main/java/me/panpf/sketch/request/CancelCause.java
[DownloadListener]: ../../sketch/src/main/java/me/panpf/sketch/request/DownloadListener.java
[LoadListener]: ../../sketch/src/main/java/me/panpf/sketch/request/LoadListener.java
[DisplayListener]: ../../sketch/src/main/java/me/panpf/sketch/request/DisplayListener.java
[Resize]: ../../sketch/src/main/java/me/panpf/sketch/request/Resize.java
[LoadOptions]: ../../sketch/src/main/java/me/panpf/sketch/request/LoadOptions.java
[DisplayOptions]: ../../sketch/src/main/java/me/panpf/sketch/request/DisplayOptions.java
[DownloadHelper]: ../../sketch/src/main/java/me/panpf/sketch/request/DownloadHelper.java
[LoadHelper]: ../../sketch/src/main/java/me/panpf/sketch/request/LoadHelper.java
[LoadHelper]: ../../sketch/src/main/java/me/panpf/sketch/request/LoadHelper.java
[OptionsFilter]: ../../sketch/src/main/java/me/panpf/sketch/optionsfilter/OptionsFilter.java
[Configuration]: ../../sketch/src/main/java/me/panpf/sketch/Configuration.java
[ImageZoomer]: ../../sketch/src/main/java/me/panpf/sketch/zoom/ImageZoomer.java
[Initializer]: ../../sketch/src/main/java/me/panpf/sketch/Initializer.java
[UriModel]: ../../sketch/src/main/java/me/panpf/sketch/uri/UriModel.java
[AppIconUriModel]: ../../sketch/src/main/java/me/panpf/sketch/uri/AppIconUriModel.java
[SLog]: ../../sketch/src/main/java/me/panpf/sketch/SLog.java
[ImageProcessor]: ../../sketch/src/main/java/me/panpf/sketch/process/ImageProcessor.java
[WrappedImageProcessor]: ../../sketch/src/main/java/me/panpf/sketch/process/WrappedImageProcessor.java

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


# v2.4.0 Beta4

:fire::fire::fire: 如果使用了根据枚举存储和获取Options的功能或已删除的一些过时方法就不能无痛升级 :fire::fire::fire:

### Options
:fire: 整个移除使用枚举存储和获取Options的功能，涉及以下方法
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

为何要移除？经实际使用发现，即使在Application中第一时间存储Options，也会出现取不到本应该存在的Options的情况，因此推荐改用懒加载的方式管理Options，详情可参考Demo里的 [ImageOptions.java](https://github.com/panpf/sketch/blob/master/sample/src/main/java/me/panpf/sketchsample/ImageOptions.java) 或 [如何管理多个Options.md](https://github.com/panpf/sketch/blob/master/docs/wiki/options_manage.md)

### Initializer
:sparkles: 新增Initializer可以在AndroidManifest.xml中配置初始化类，这样就不用在Application中初始化了，可减轻Application的负担，也可百分之百保证第一时间完成Sketch的初始化，详情请参考[initializer.md](https://github.com/panpf/sketch/blob/master/docs/wiki/initializer.md)

### ImageZoomer
* :art: 现在如果ImageZoomer已经消费了触摸事件就不再往下传递了
* :art: 现在ImageZoomer可以回调ImageView的OnClickListener和OnLongClickListener，但OnViewTapListener和OnViewLongPressListener的优先级更高

### Other
* :fire: 删除所有过时的方法
    * SketchImageView.displayURIImage(Uri)
    * SketchImageView.setClickRetryOnError(boolean)
    * SketchImageView.setClickRetryOnPauseDownload(boolean)
    * SketchImageView.isShowDownloadProgress()
    * SketchImageView.setShowDownloadProgress(boolean)
    * SketchImageView.isShowPressedStatus()
    * SketchImageView.setShowPressedStatus(boolean)
    * SketchImageView.isShowImageFrom()
    * SketchImageView.setShowImageFrom(boolean)
    * SketchImageView.isShowGifFlag()
    * SketchImageView.setShowGifFlag(Drawable)
    * SketchImageView.setShowGifFlag(int)
    * SketchImageView.isSupportZoom()
    * SketchImageView.setSupportZoom(boolean)
    * SketchImageView.isSupportLargeImage()
    * SketchImageView.setSupportLargeImage(boolean)
    * Sketch.displayFromURI(Uri)
    * ImageSizeCalculator.calculateImageResize(SketchView)
    * ImageSizeCalculator.compareMaxSize(MaxSize, MaxSize)

### Sample App
* :sparkles: 增加我的视频列表，展示如何显示视频缩略图
* :zap: 提升扫描本地apk的速度
* :lipstick: 我的相册页面改为每行三个


# v2.4.0 Beta3

此版本主要是修复了几个bug然后升级了示例App

:fire::fire::fire: 如果你有与ImageViewInterface相关的自定义就不能无痛升级 :fire::fire::fire:

### ImageDisplayer
* :art: 现在TransitionDisplayer遇到两张一模一样的图片时不再执行过渡动画

### Download
* :bug: 修复下载进度不回调的bug
* :art: 下载进度回调间隔由1000毫秒减少到100毫秒，这样进度的变化更明显

### Other:
* :hammer: ImageViewInterface改名为SketchView并移到me.xiaopan.sketch目录下，`所有与此相关的自定义都需要修改`

### ImagePreprocessor
* :bug: 修复所有内置Preprocessor在获取锁后发现有缓存的情况下没有解锁的bug
* :bug: 修复ApkPreprocessor没有使用正确的磁盘缓存key的bug

### Sample App：
* :sparkles: 新增Unsplash页面，可浏览来自Unsplash的高质量图片
* :fire: 去掉了明星图片页面


# v2.4.0 Beta2

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
* :fire: 修复在显示错误时点击重试的时候会意外的跳过移动数据暂停下载功能
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


# v2.4.0 Beta1

### :sparkles: 纠正图片方向功能
* 新增纠正图片方向功能 [了解更多](https://github.com/panpf/sketch/blob/master/docs/wiki/correct_image_orientation.md)
* 默认关闭，通过correctImageOrientation属性开启
* 一般的图片支持自动纠正方向，分块显示超大图也支持
* 可通过SketchDrawable.getOrientation()方法获得图片的方向

### ImageProcessor：
* WrapableImageProcessor重命名为WrappedImageProcessor

### 其它
* ImageFormat重命名为ImageType
* 最低API提升至10

### Sample APP：
* 图片详情页右下角设置按钮改为长按
* 图片详情页点击显示底部四个按钮挪到了长按--更多功能里
* 图片详情页点击关闭页面


# v2.3.0 Beta10

ImageProcessor：
>* :sparkles: 增加MaskImageProcessor可以给任意形状的PNG图片加上一层遮罩颜色
>* :sparkles: 增加WrappedImageProcessor可以将任意不同的ImageProcessor组合在一起使用
>* :bug: 修复GaussianBlurImageProcessor生成的key无法区分不同的darkColor的Bug
>* :art: 删除所有ImageDisplayer的setAlwaysUse(boolean)方法，改为在构造函数中设置
>* :art: GaussianBlurImageProcessor的所有构造函数改为私有的，只能通过静态方法创建

Rename：
>* :art: SketchImageView.getOptionsId()改名为getOptionsKey()
>* :art: SketchUtils.makeRequestId(String, DownloadOptions)改名为make请求Key(String, DownloadOptions)
>* :art: SketchUtils.makeRequestId(String, String)改名为make请求Key(String, String)
>* :art: SketchUtils.makeStateImageRequestId(String, DownloadOptions)改名为makeStateImageMemoryCacheKey(String, DownloadOptions)

Other：
>* :zap: 优化inSampleSize计算逻辑，防止超过OpenGL所允许的最大尺寸


# v2.3.0 Beta9

缓存：
>* :sparkles: Sketch类中新增onLowMemory()和onTrimMemory(int)方法，用于在内存较低时释放缓存，需要在Application中回调，具体请查看README或参考demo app
>* :fire: 去掉stateImageMemoryCache，共用一个内存缓存器
>* :sparkles: `bitmap pool` 增加bitmap pool，减少内存分配，降低因GC回收造成的卡顿

GIF：
>* :bug: onDetachedFromWindow时主动回收GifDrawable

请求：
>* :art: `Attribute` disableCacheInDisk和disableCacheInMemory属性改名为cacheInDiskDisabled和cacheInMemoryDisabled
>* :sparkles: `Attribute` LoadOptions、LoadHelper、DisplayOptions、DisplayHelper增加disableBitmapPool属性
>* :fire: `Lock` 移除内存缓存编辑锁
>* :bug: `Filter repeat` 修复同一内存缓存ID可以出现多个请求同时执行的BUG
>* :bug: `Filter repeat` 修复由于同一内存缓存ID可以出现多个请求，导致会出现同一个内存缓存ID会连续存入多个缓存对象，那么新存入的缓存对象就会挤掉旧的缓存对象，如果旧的缓存对象只有缓存引用，那么旧的缓存对象会被直接回收
>* :zap: `FreeRide` 新增FreeRide机制避免重复下载和加载
>* :bug: `TransitonImageDisplayer` 修复从ImageView上取当前图片作为过渡图片时没有过滤LayerDrawable，导致可能会越套越深的BUG

ImageShaper：
>* :zap: `RoundRectImageShaper` 优化描边的绘制方式，不再出现圆角除盖不住的情况

其它：
>* :arrow_up: `minSdkVersion` 最低支持版本升到9
>* :art: `ExceptionMonitor` ExceptionMonitor改名为SketchMonitor并挪到顶级目录
>* :zap: `Log` 日志分不同的类型分别提供开关控制，详见[SLogType.java](../../sketch/src/main/java/me/panpf/sketch/SLogType.java)


# v2.3.0 Beta8

GIF：
>* :bug: `GIF` 修复当反复切换LoadOptions.decodeGifImage的值后再刷新页面，需要播放GIF时却依然显示静态的GIF第一帧的BUG
>* :bug: `Gif Flag` 修复SketchImageView.setShowGifFlag()反复调用时无效的bug

SketchImageView
>* :bug: `Zoom` 修复设置关闭手势缩放功能时没有恢复Matrix和ScaleType的BUG
>* :bug: `ImageFrom` 修复反复调用setShowImageFrom(boolean)时无效的BUG

请求：
>* :bug: 修复显示时遇到已回收的Bitmap崩溃的BUG
>* :bug: 修复读取缓存的已处理图片时类型以及原始尺寸丢失的BUG
>* :bug: 修复读取APK icon时drawable宽高小于等于0崩溃的BUG
>* :sparkles: ExceptionMonitor新增onBitmapRecycledOnDisplay(DisplayRequest, RefDrawable)方法用来监控即将显示时发现Bitmap被回收的问题

缓存：
>* :bug: RefBitmap各个方法全部加了同步锁，视图解决在显示时Bitmap却已回收的BUG


# v2.3.0 Beta7

优化：
>* `DiskLruCache` 捕获commit时可能出现的java.lang.IllegalStateException: edit didn't create file 0异常


# v2.3.0 Beta6

优化：
>* `id` 计算ID时，forceUseResize和thumbnailMode依赖resize
>* `RequestAttrs` 重构RequestAttrs，如果你自定义的一些功能要用到的话就需要改一下

新增：
>* `cacheProcessedImageInDisk` 为了加快速度，将经过ImageProcessor、resize或thumbnailMode处理过读取时inSampleSize大于等于8的图片保存到磁盘缓存中，下次就直接读取

删除：
>* `memoryCacheId` 删除DisplayHelper的memoryCacheId(String)方法


# v2.3.0 Beta5

修复BUG：
>* 修复TransitionImageDisplayer在碰到ImageView当前没有图片时崩溃的BUG

新增：
>* `StateImage` 新增OldStateImage，可以使用ImageView当前正在显示的图片作为loadingImage
>* `ImageDisplayer` ImageDisplayer新增setAlwaysUse(boolean)方法，可设置只要涉及到显示图片就得使用ImageDisplayer（显示从内存里取出的缓存图片时也不例外）


# v2.3.0 Beta4

新功能：
>* `ImageShaper` 新增ImageShaper，用来在绘制时修改图片的形状，[点击查看使用介绍](../wiki/image_shaper.md)
>* `ShapeSize` 新增ShapeSize，用来在绘制时在修改图片的尺寸，[点击查看使用介绍](../wiki/shape_size.md)

优化：
>* `resizeByFixedSize` 设置了resizeByFixedSize但是ImageView的宽高没有固定的话就抛出异常
>* `MakerDrawableModeImage` MakerDrawableModeImage不再接受设置resize, lowQualityImage, forceUseResize, imageProcessor，现在自动从DisplayOptions中取
>* `resize`调用setResize()时不再默认设置resizeByFixedSize为false
>* `resizeByFixedSize`调用setResizeByFixedSize()时不再默认设置resize为null

其它：
>* `Rename` RoundedCornerImageProcessor重命名为RoundRectImageProcessor
>* `Rename` ModeImage重命名为StateImage
>* `Rename` DrawableModeImage重命名为DrawableStateImage
>* `Rename` MemoryCacheModeImage重命名为MemoryCacheStateImage
>* `Rename` MakerDrawableModeImage重命名为MakerStateImage


# v2.3.0 Beta3

优化：
>* 优化了分块显示超大图功能的碎片排序比较器，还对可能在Java7上出现的排序崩溃进行了捕获并可通过ExceptionMonitor监控，另外崩溃时会临时切换成Java6的排序方式重新排序


# v2.3.0 Beta2

修复BUG：
>* [#16](https://github.com/panpf/sketch/issues/16) 修复由于在ImageView onAttachedToWindow的时候重新创建了ImageZoomer导致先前往ImageZoomer中设置的各种监听失效的BUG，此BUG的直接表现就是在Activity中无法使用分块显示超大图功能（在Fragment中并不会触发onAttachedToWindow所以在Fragment中看起来一切正常）

优化：
>* 优化了LargeImageView的暂停功能，并且pause()和resume()方法合为setPause(boolean)一个了


# v2.3.0 Beta1

修复BUG：
>* [#4](https://github.com/panpf/sketch/issues/4) 修复由于在内存中缓存了Drawable，导致同一个缓存Drawable在两个不同的地方使用时bounds被改变从而图片大小显示异常，常见的表现为点击图片进入图片详情页后再回来发现图片变小了
>* [#11](https://github.com/panpf/sketch/issues/11) 修复最后一条磁盘缓存无效的BUG，这是DiskLruCache的BUG，因为在commit的时候没有持久化操作记录导致的
>* [#13](https://github.com/panpf/sketch/issues/13) 修复SketchBitmapDrawable由于没有设置TargetDensity而始终以160的默认像素密度来缩小图片最终导致通过getIntrinsicWidth()得到的尺寸始终比Bitmap实际尺寸小的BUG
>* [#14](https://github.com/panpf/sketch/issues/14) ImageHolder直接缓存了Drawable导致同一个Drawable在多个FIX_XY的ImageView上显示时大小异常的BUG

新功能：
>* ``Decode``. [缩略图模式](../wiki/thumbnail_mode.md)，通过缩略图模式你可以在列表中更加清晰的显示那些宽高相差特别大的图片
>* ``Gesture Zoom``. [手势缩放功能](../wiki/zoom.md)，参照PhotoVie，SketchImageView内置了手势缩放功能，比PhotoView功能更强大，体验更好，新增了定位、阅读模式等特色功能
>* ``Super Large Image``. [分块显示超大图功能](../wiki/block_display.md)，SketchImageVie内置了分块显示超大图功能，长微博、高清妹子图什么的不再是问题
>* ``ModeImage``. 新增ModeImage替代ImageHolder，现在可以用任意类型的drawable或者内存缓存中的图片来作为loading占位图了

优化：
>* ``inSampleSize``. 优化inSampleSize计算规则，先根据像素数过滤，然后再根据OpenGL的MAX_TEXTURE_SIZE过滤，最后如果是为大图功能加载预览图的话，当缩小2倍的时为了节省内存考虑还不如缩小4倍（缩小1倍时不会启用大图功能，因此无需处理）
>* ``maxSize``. 默认maxSize改为屏幕的宽高，不再乘以0.75
>* ``Drawable``. 重构Drawable系统，现在可以用任意类型的drawable作为loading占位图了
>* ``TransitonImageDisplayer``. 对TransitionImageDisplayer的安全验证条件中不再验证CENTER_CROP

其它：
>* ``Listener``. DownloadListener的onCompleted(File cacheFile, boolean isFromNetwork)和onCompleted(byte[] data)合并成一个onCompleted(DownloadResult downloadResult)
>* ``Listener``. LoadListener的onCompleted(Bitmap bitmap, ImageFrom imageFrom, String mimeType)和onCompleted(GifDrawable gifDrawable, ImageFrom imageFrom, String mimeType)合并成一个onCompleted(LoadResult loadResult)
>* ``Rename``. 所有跟Failed相关的名字全改成了Error
>* ``FadeInImageDisplayer``. 新增渐入图片显示器FadeInImageDisplayer


