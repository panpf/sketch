# v2.7.1 beta2

* fix: Fixed a bug where the Resize(Resize) constructor did not copy the mode field
* feature: Improve @NonNull and @Nullable annotations
* fix: Content-Type is no longer required to be 'image/*' because not all servers adhere to this standard
* behavior: Now the SketchImageView.displayImage(Uri) method supports passing in null to clear the ImageView image, which is equivalent to setImageDrawable(null)
* bug: Fix SketchImageView setting setImageDrawable(null) will still display the old image when scrolling in the list

# v2.7.1 beta1

#### Sketch

Fix the following bug:
* Fix: ProcessedCacheDecodeHelper is not used when processedImageDiskCacheKey is the same as uri

The following behavior has changed:
* Behavior: Support for downloading images without Content-Length and Transfer-Encoding not chunked
* Behavior: When the decodeGifImage is true, the 'gif' identifier is no longer added to the memoryCacheKey

New Feature:
* Feature: Add a new checkpoint when downloading, Content-Type must be image/*
* Feature: TransitionImageDisplayer support setup disable crossFade 
* Feature: ImageZoomer add canScrollVertically,canScrollHorizontally,getVerScrollEdge,getHorScrollEdge method

#### Sample App

* Fix: Fix FragmentLifecycleCallbacks.onFragmentViewDestroyed method does not execute a bug that caused memory overflow
* Test: The oversized image for testing is built into the app


#### Build: 
* :arrow_up: Upgrade android build plugin 3.4.0, kotlin 1.3.31, gardle 5.1.1


# v2.7.0

* :arrow_up: Upgrade to Jetpack
* :bug: Fix ShapeSize to ShapeSize.byViewFixedSize(), errorImage is not null, but error image can't always be displayed when uri is empty

# v2.7.0 Beta1

* :arrow_up: Upgrade to Jetpack
* :bug: Fix ShapeSize to ShapeSize.byViewFixedSize(), errorImage is not null, but error image can't always be displayed when uri is empty

# v2.6.2

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


# v2.6.1

bug：
* :bug: Fix StatFs Invalid path exception
* :bug: 修复当一个已经乘坐上了顺风车的请求取消后，依然还会运行的 bug

其它：
* :arrow_up: 最低支持 API 升至 14
* :arrow_up: 升级 android-gif-drawable 到 1.2.10 版本

Sample App：
* :lipstick: 搜索 GIF 页面改用 FlexboxLayoutManager 展示


# v2.6.0

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


# v2.5.0

这是一个大的重构版本，重点重构了日志和 uri 部分

:fire: 如果你用到了下述重命名、移除、重构、重命名以及 ErrorTracker 部分的话就不能无痛升级 :fire:

bugs：
* :bug: 修复在某些 ROM 上查找 Initializer 时碰到 meta 值不是 String 时崩溃的 BUG [#43]
* :bug: 修复 [Sketch] 的单例可能失效的 BUG
* :bug: 修复在多线程环境下可能拿到一个尚未执行 [Initializer] 的 [Sketch] 的 bug
* :bug: 修复 [ImageZoomer] 的双击事件和单击事件可能会有冲突的 BUG
* :bug: 修复通过 [SketchUtils].generatorTempFileName(DataSource, String) 方法生成的文件名中含有换行符导致创建文件失败的BUG，直接影响了 DataSource.getFile(File, String) 方法
* :bug: 修复当同一个文件被用于多种 uri 支持时，其磁盘缓存可能错乱的 BUG
* :bug: 修复由于混淆了 Sketch.onTrimMemory 和 Sketch.onLowMemory 方法导致其内部调用过滤失效的 bug

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
* :hammer: key 中不再包含 decodeGifImage ，受此影响已经通过 cacheProcessedImageInDisk 生成的磁盘缓存将全部失效

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
* :zap: 支持处理 301 302 重定向，但仅支持重定向一次

sample app：
* :bug: 修复了 gif 图无法播放的问题
* :sparkles: MyPhotos 列表的图标可以变成圆角矩形的，由 "Show Round Rect In Photo List" 开关控制
* :hammer: 默认关闭 长按显示图片详情的功能，交由 "Long Clock Show Image Info" 开关控制
* :hammer: 现在视频缩略图模块 从 sample app 中抽离出去成为一个单独的 module [sample-video-thumbnail]
* :bug: 修复在 MyPhotos 页面如果数据量超大的话就会崩溃的 bug
* :lipstick: 重构图片详情页的长按菜单
* :lipstick: 兼容 MIX 2 的全屏显示

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


# v2.4.1

本版本主要修复了demo中的bug，然后顺便将sketch中同样的场景都加了预防措施

:green_heart: 可平滑升级 :green_heart:

其它：
* :ambulance: 所有使用磁盘缓存编辑锁的地方都加了try finally，防止异常导致无法释放锁的情况发生

Sample APP
* :bug: 修复release版本混淆后找不到FFmpegMediaMetadataRetriever类的bug
* :bug: 修复由于VideoThumbnailPreprocessor在遇到异常后没有释放缓存编辑锁，导致阻塞了所有的本地解码线程的bug

如果你使用了demo中的 [VideoThumbnailPreprocessor.java](../../sample/src/main/java/me/panpf/sketchsample/util/VideoThumbnailPreprocessor.java)  那么强烈建议你更新一下 VideoThumbnailPreprocessor 的源码


# v2.4.0 Stable

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
* 新增纠正图片方向功能 [了解更多](https://github.com/panpf/sketch/blob/master/docs/wiki/correct_image_orientation.md)
* 功能默认开启，可通过 `correctImageOrientationDisabled` 属性关闭
* 一般的图片支持自动纠正方向，分块显示超大图也支持
* 可通过 SketchDrawable.getExifOrientation() 方法获得图片的 exif 方向

### :sparkles: Base64格式图片支持
* 新增对Base64格式图片的支持，并且支持 `data:image/` 和 `data:img/` 两种写法
* 对于 Base64 格式的图片会首先会缓存到磁盘上再读取
* 支持 Sketch 所有功能，ImageProcessor、MaxSize、Resize等

### :sparkles: Initializer
* 新增 Initializer 可以在 AndroidManifest.xml 中指定配置类，这样就不用再在 Application 中配置了，可减轻 Application 的负担，也可百分之百保证第一时间完成对 Sketch 的配置，详情请参考[initializer.md](https://github.com/panpf/sketch/blob/master/docs/wiki/initializer.md)

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
* :fire: 修复在显示错误时点击重试的时候会意外的跳过移动数据暂停下载功能的bug
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
:fire: 整个移除使用枚举存储和获取Options的功能。为何要移除？经实际使用发现，即使在Application中第一时间存储Options，也会出现取不到本应该存在的Options的情况，因此推荐改用懒加载的方式管理Options，详情可参考Demo里的 [ImageOptions.java](https://github.com/panpf/sketch/blob/master/sample/src/main/java/me/panpf/sketchsample/ImageOptions.java) 或 [如何管理多个Options.md](https://github.com/panpf/sketch/blob/master/docs/wiki/options_manage.md)。涉及以下方法
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
    * ImageSizeCalculator 和 ResizeCalculator  由 me.xiaopan.sketch.feature 移到 me.xiaopan.sketch.decode

### Sample APP：
* :hammer: 图片详情页右下角设置按钮改为长按
* :hammer: 图片详情页点击显示底部四个按钮挪到了长按--更多功能里
* :sparkles: 图片详情页点击关闭页面
* :sparkles: 增加自动纠正图片方向测试页面
* :sparkles: 增加 base64 图片测试页面
* :art: 优化侧滑选项的命名
* :sparkles: drawable、asset、content 来源的图片可以使用分享、保存和设置壁纸功能了
* :sparkles: 可以在任意位置长按图片查看图片信息
* :sparkles: 增加列表中点击播放gif选项
* :sparkles: 新增 Unsplash 页面，可浏览来自 Unsplash 的高质量图片
* :fire: 去掉了明星图片页面
* :sparkles: 增加我的视频列表，展示如何显示视频缩略图
* :zap: 提升扫描本地 apk 的速度
* :lipstick: 我的相册页面改为每行三个


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


# v2.3.3

这是一个bug修复版本，如果你使用了lowQualityImage属性，那么强烈建议升级

bugs：
* 修复在kitkat以上版本使用lowQualityImage属性时对bitmap执行reconfigure会抛出"IllegalArgumentException: Bitmap not large enough to support new configuration"异常导致图片显示失败的bug

其它：
* 在kitkat以上版本Options的bitmapConfig属性和ImageFormat的lowQualityConfig属性无法使用ARGB_4444


# v2.3.2

#### GIF
* :arrow_up: 升级android-gif-drawable版本到1.2.6
* :hammer: 拆分gif模块，现在作为一个单独的library对外提供，依赖方式请参考[README](../../README.md)
* :sparkles: SketchGifDrawable新增followPageVisible(boolean, boolean)方法用于方便的实现页面不可见时停止播放gif功能，[点击查看具体用法](../wiki/play_gif_image.md)

#### Other
* :bug: 修复使用在DiskLruCache中SLog.w(String, String, Object ...)方法打印日志时，如果日内容中包含"3A"这样的字符就崩溃的BUG

#### DiskCache
* :bug: 修复由于磁盘缓存文件名称过长导致缓存文件不可用的bug，因为之前磁盘缓存文件的名称是用uri或内存缓存key经过URLEncoder处理后得到的，现在是经过MD5
处理

#### 注意：
* 由于升级了磁盘缓存文件名称的加密方式所以旧的磁盘缓存文件将全部删除，默认的删除方式是升级LruDIskCache的appVersionCode参数，如果你有自定义创建LruDIskCache，那么你也需要升级appVersionCode参数


# v2.3.1

ScaleType:
>* :bug: 修复resize和shapeSize里的ScaleType不能自动从ImageView身上获取的BUG


# v2.3.0

2.3.0是一个大版本，除了有大量的BUG修复外，还重构了占位图系统，增加了手势缩放、分块显示超大图、ImageShaper、BitmapPool、缩略图模式等新功能

`推荐度：强烈建议升级`

`升级：不能平滑升级，因为有些许重命名和重构`

##### 下载
>* :bug: 支持下载那些服务端无法确定文件大小导致响应头里只有Transfer-Encoding: chunked没有Content-Length的图片

##### 缓存
>* :bug: [#4](https://github.com/panpf/sketch/issues/4) 修复由于在内存中缓存了Drawable，导致同一个缓存Drawable在两个不同的地方使用时bounds被改变从而图片大小显示异常，常见的表现为点击图片进入图片详情页后再回来发现图片变小了
>* :bug: [#11](https://github.com/panpf/sketch/issues/11) 修复最后一条磁盘缓存无效的BUG，这是DiskLruCache的BUG，因为在commit的时候没有持久化操作记录导致的
>* :bug: [#14](https://github.com/panpf/sketch/issues/14) 修复ImageHolder直接缓存了Drawable导致同一个Drawable在多个FIX_XY的ImageView上显示时大小异常的BUG
>* :bug: `DiskLruCache` 捕获commit时可能出现的java.lang.IllegalStateException: edit didn't create file 0异常
>* :fire: 去掉stateImageMemoryCache，共用一个内存缓存器
>* :sparkles: Sketch类中新增onLowMemory()和onTrimMemory(int)方法，用于在内存较低时释放缓存，需要在Application中回调，具体请查看README或参考demo app
>* :sparkles: 新增[BitmapPool](../wiki/bitmap_pool.md)，可减少内存分配，降低因GC回收造成的卡顿

##### Drawable
>* :bug: [#13](https://github.com/panpf/sketch/issues/13) 修复SketchBitmapDrawable由于没有设置TargetDensity而始终以160的默认像素密度来缩小图片最终导致通过getIntrinsicWidth()得到的尺寸始终比Bitmap实际尺寸小的BUG

##### 占位图（[StateImage](../wiki/state_image.md)）：
>* :hammer: 重构占位图系统，更名为StateImage，现在可以用任意类型的drawable作为loading占位图了

##### ImageDisplayer
>* :bug: `TransitionImageDisplayer`. 修复TransitionImageDisplayer在碰到ImageView当前没有图片时崩溃的BUG
>* :bug: `TransitionImageDisplayer` 修复从ImageView上取当前图片作为过渡图片时没有过滤LayerDrawable，导致可能会越套越深的BUG
>* :zap: `TransitionImageDisplayer`. 对TransitionImageDisplayer的安全验证条件中不再验证CENTER_CROP
>* :sparkles: `FadeInImageDisplayer`. 新增渐入图片显示器FadeInImageDisplayer
>* :sparkles: `ImageDisplayer` ImageDisplayer新增setAlwaysUse(boolean)方法，可设置只要涉及到显示图片就得使用ImageDisplayer（显示从内存里取出的缓存图片时也不例外）

#### ImageProcessor
>* :hammer: `RoundedCornerImageProcessor` RoundedCornerImageProcessor重命名为RoundRectImageProcessor
>* :art: GaussianBlurImageProcessor的所有构造函数改为私有的，只能通过静态方法创建
>* :sparkles: 增加MaskImageProcessor可以给任意形状的PNG图片加上一层遮罩颜色
>* :sparkles: 增加WrappedImageProcessor可以将任意不同的ImageProcessor组合在一起使用

##### ImageSize
>* :zap: `inSampleSize`. 优化inSampleSize计算规则，先根据像素数过滤，然后再根据OpenGL的MAX_TEXTURE_SIZE过滤，最后如果是为大图功能加载预览图的话，当缩小2倍的时为了节省内存考虑还不如缩小4倍（缩小1倍时不会启用大图功能，因此无需处理）
>* :zap: `maxSize`. 默认maxSize改为屏幕的宽高，不再乘以0.75
>* :zap: `resizeByFixedSize` 设置了resizeByFixedSize但是ImageView的宽高没有固定的话就抛出异常
>* :zap: `resize`调用setResize()时不再默认设置resizeByFixedSize为false
>* :zap: `resizeByFixedSize`调用setResizeByFixedSize()时不再默认设置resize为null

##### Request
>* :bug: `Filter repeat` 修复同一内存缓存ID可以出现多个请求同时执行的BUG
>* :bug: `Filter repeat` 修复由于同一内存缓存ID可以出现多个请求，导致会出现同一个内存缓存ID会连续存入多个缓存对象，那么新存入的缓存对象就会挤掉旧的缓存对象，如果旧的缓存对象只有缓存引用，那么旧的缓存对象会被直接回收
>* :bug: 修复显示时遇到已回收的Bitmap崩溃的BUG
>* :bug: 修复读取APK icon时drawable宽高小于等于0崩溃的BUG
>* :hammer: `Listener`. DownloadListener的onCompleted(File cacheFile, boolean isFromNetwork)和onCompleted(byte[] data)合并成一个onCompleted(DownloadResult downloadResult)
>* :hammer: `Listener`. LoadListener的onCompleted(Bitmap bitmap, ImageFrom imageFrom, String mimeType)和onCompleted(GifDrawable gifDrawable, ImageFrom imageFrom, String mimeType)合并成一个onCompleted(LoadResult loadResult)
>* :hammer: `RequestAttrs` 重构RequestAttrs
>* :fire: `memoryCacheId` 删除DisplayHelper的memoryCacheId(String)方法
>* :fire: `Lock` 移除内存缓存编辑锁
>* :art: `Attribute` disableCacheInDisk和disableCacheInMemory属性改名为cacheInDiskDisabled和cacheInMemoryDisabled
>* :zap: `id` 计算ID时，forceUseResize和thumbnailMode依赖resize
>* :zap: 新增FreeRide机制避免重复下载和加载
>* :sparkles: `Attribute` LoadOptions、LoadHelper、DisplayOptions、DisplayHelper增加disableBitmapPool属性
>* :sparkles: 新增[缩略图模式](../wiki/thumbnail_mode.md)，通过缩略图模式你可以在列表中更加清晰的显示那些宽高相差特别大的图片
>* :sparkles: 新增[cacheProcessedImageInDisk](../wiki/cache_processed_image_in_disk.md)属性，为了加快速度，将经过ImageProcessor、resize或thumbnailMode处理过读取时inSampleSize大于等于8的图片保存到磁盘缓存中，下次就直接读取
>* :sparkles: 新增[shapeSize](../wiki/shape_size.md)属性，借助ImageShaper可以在绘制时在修改图片的尺寸

##### GIF
>* :bug: `GIF` 修复当反复切换LoadOptions.decodeGifImage的值后再刷新页面，需要播放GIF时却依然显示静态的GIF第一帧的BUG
>* :bug: onDetachedFromWindow时主动回收GifDrawable

##### SketchImageView
>* :bug: `Gif Flag` 修复SketchImageView.setShowGifFlag()反复调用时无效的bug
>* :bug: `Zoom` 修复设置关闭手势缩放功能时没有恢复Matrix和ScaleType的BUG
>* :bug: `ImageFrom` 修复反复调用setShowImageFrom(boolean)时无效的BUG

##### ExceptionMonitor
>* :hammer: ExceptionMonitor重命名为SketchMonitor并并挪到顶级目录
>* :sparkles: 新增onBitmapRecycledOnDisplay(DisplayRequest, RefDrawable)方法用来监控即将显示时发现Bitmap被回收的问题
>* :sparkles: 新增`onBitmapRecycledOnDisplay(DisplayRequest, RefDrawable)`方法用来监控即将显示时发现Bitmap被回收的问题
>* :sparkles: 新增`onTileSortError(IllegalArgumentException, List<Tile>, boolean)`用来监控分块显示超大图功能碎片排序异常
>* :sparkles: 新增`onBitmapRecycledOnDisplay(DisplayRequest, RefDrawable)`用来监控在即将显示图片之前发现图片被回收了的异常
>* :sparkles: 新增`onInBitmapExceptionForRegionDecoder(String, int, int, Rect, int, Bitmap)`用来监控在BitmapRegionDecoder中使用inBitmap时发生的异常
>* :sparkles: 新增`onInBitmapException(String, int, int imageHeight, int, Bitmap)`用来监控在BitmapFactory中使用inBitmap时发生的异常

##### Log
>* :hammer: `Log` 日志分不同的类型分别提供开关控制，详见[SLogType.java](../../sketch/src/main/java/me/panpf/sketch/SLogType.java)
>* :sparkles: `SLogTracker` 新增SLogTracker用于收集Sketch的日志，详情可参看示例APP中的[SampleLogTracker.java](../../sample/src/main/java/me/panpf/sketchsample/SampleLogTracker.java)

##### 其它
>* :arrow_up: `minSdkVersion` 最低支持版本升到9
>* :hammer: `Rename`. 所有跟Failed相关的名字全改成了Error

#### 全新功能
>* :sparkles: `Gesture Zoom`. 新增[手势缩放](../wiki/zoom.md)功能，参照PhotoVie，SketchImageView内置了手势缩放功能，比PhotoView功能更强大，体验更好，新增了定位、阅读模式等特色功能
>* :sparkles: `Super Large Image`. 新增[分块显示超大图](../wiki/block_display.md)功能，SketchImageVie内置了分块显示超大图功能，长微博、高清妹子图什么的不再是问题
>* :sparkles: `ImageShaper` 新增[ImageShaper](../wiki/image_shaper.md)，可以在绘制时修改图片的形状，避免同一张图片有不同的形状需求时通过ImageProcessor实现会产生多张图片，从而浪费内存缓存的情况

#### 相对于2.3.0-beta10：
>* :bug: 修复反复开启、关闭超大图功能会导致手势缩放比例异常的BGU
>* :bug: 修复GaussianBlurImageProcessor无法对config为null的bitmap执行模糊的BUG
>* :bug: 修复在Android4.3上取不到webp图片的outMimeType时，无法显示图片的BUG
>* :bug: 修复2.3上RotateImageProcessor崩溃的BUG
>* :lipstick: 优化Sample App


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


# v2.2.3

ErrorCallback:
>* ``优化``. onInstallDiskCacheFailed异常细分出UnableCreateDirException，标明无法创建缓存目录
>* ``新增``. onInstallDiskCacheFailed异常细分出UnableCreateFileException，标明无法缓存目录中创建文件
>* ``新增``. 新增onProcessImageFailed(OutOfMemoryError, String, ImageProcessor)方法
>* ``新增``. 新增构造函数ErrorCallback(Context)
>* ``修改``. ErrorCallback重命名为ExceptionMonitor
>* ``新增``. 新增onDownloadFailed(DownloadRequest, Throwable)，可监控下载失败异常

DiskCache：
>* ``优化``. DiskCache在执行close的时候，顺便把缓存编辑锁清了
>* ``新增``. DiskCache新增isClosed()方法
>* ``优化``. LruDiskCache内部各个方法增加了closed判断，增强稳定性
>* ``修改``. 现在DiskCache.getEditLock(String)可能返回null，因此要判断一下
>* ``优化``. LruDiskCache安装磁盘缓存的时候增加目录可用性检查和文件创建测试
>* ``优化``. 现在LruDiskCache创建缓存目录的时候会优先考虑所有SD卡，然后考虑内部存储
>* ``BUG``. 修复cache is closed异常

MemoryCache：
>* ``优化``. MemoryCache在执行close的时候，顺便把缓存编辑锁清了
>* ``新增``. MemoryCache新增isClosed()方法
>* ``优化``. LruMemoryCache内部各个方法增加了closed判断，增强稳定性
>* ``修改``. 现在MemoryCache.getEditLock(String)可能返回null，因此要判断一下


# v2.2.2

2.2.2还是一个BUG修复和优化版，如果你现在使用的是2.2.0或2.2.1，那么强烈建议你更新到2.2.2

下载：
>* ``BUG``. 修复拿到磁盘缓存编辑锁之后发现取消了，但没有结束得等到下一个检查点才会结束的BUG ，这个不会影响到解锁
>* ``优化``. 默认超时时间改为7秒，并且不再重试，这么做的目的是为了减少因为个别图片超时而导致过长的等待

display：
>* ``BUG``. 修复拿到内存缓存编辑锁之后发现取消了，但没有结束得等到下一个检查点才会结束的BUG ，这个不会影响到解锁

ImagePreprocessor：
>* prePrecess()方法名字改为了process()，你需要修改你的实现

日志：
>* ``优化``. 在日志中新增了ImageView的hashCode、当前线程名称等信息方便追踪问题


# v2.2.1

2.2.1是一个BUG修复版，如果你现在使用的是2.2.0，那么强烈建议你更新到2.2.1

``建议升级到2.2.2``

下载：
>* ``BUG``. 修复使用一段时间后有较大几率会出现下载线程全部被锁，导致无法下载新图片的BUG。
    这是因为下载的过程中在获取到磁盘缓存编辑锁之后如果发现已经取消了那就直接结束了，但是却没有解锁，这时候这个下载线程就永远被锁死了
>* ``BUG``. 禁用磁盘缓存的时候不使用磁盘缓存编辑锁
>* ``修改``. 下载过程中不再根据url加锁，目前下载过程中就只有一个根据磁盘缓存KEY的锁，但同样能防止重复下载

display：
>* ``修改``. 加载过程的锁改为了内存缓存编辑锁，并且放到的DisplayRequest中实现
>* ``BUG``. 禁用内存缓存的时候不使用内存缓存编辑锁

现在Sketch中一共有两个锁：
>* 磁盘缓存编辑锁：根据磁盘缓存KEY加锁，防止同一图片重复下载和同一磁盘缓存内容被重复编辑
>* 内存缓存编辑锁：根据内存缓存ID加锁，防止同样的图片重复加载和同一内存缓存内容被重复添加

ImagePreprocessor：
>* getDiskCacheEntry()方法名字改为了prePrecess()，你需要修改你的实现


# v2.2.0

2.2.0版本是一次大的升级，重构了大部分的实现，修复了一些BUG，也改善了一些API的设计。

``但有几个比较大的BUG，建议升级到2.2.1``

``由于改了一些API所以此次无法无缝升级，需要你该些代码``

磁盘缓存:
>* ``优化`` 升级LruDiskCache，内部采用DiskLruCache实现
>* ``修改`` DiskCache接口增加了edit(String)方法，去掉了generateCacheFile(String)、
applyForSpace(long)、setCacheDir(File)、setReserveSize(int)、getReserveSize()、
setMaxSize(int)、saveBitmap(Bitmap, String)方法
>* ``优化`` 旧的缓存文件会自动删除
>* ``优化`` `LruDiskCache兼容多进程，多进程下会采用不同的disk缓存目录，防止多进程持有同一目录造成目录被锁不能使用的问题`
>* ``优化`` 磁盘缓存编辑加同步锁

下载：
>* ``优化`` 下载进度回调方式改为每秒钟一次（之前是每10%一次）
>* ``优化`` 重构ImageDownloader，新增可设置User-Agent、readTimeout和批量添加header
>* ``修改`` ``cacheInDisk改成了disableCacheInDisk``

加载：
>* ``优化`` 默认maxSize由屏幕宽高的1.5倍改为0.75倍，这样可以大幅减少大图的内存占用，但对图片的清晰度影响却较小
>* ``优化`` 默认inSampleSize计算规则优化，targetSize默认放大1.25倍，目的是让原始尺寸跟targetSize较为接近的图片不被缩小直接显示，这样显示效果会比较好
>* ``新增`` 新增ImagePreprocessor可以处理一些特殊的本地文件，然后提取出它们的当中包含的图片，这样Sketch就可以直接显示这些特殊文件中包含的图片了
>* ``新增`` LoadOptions支持设置BitmapConfig，你可以单独定制某张图片的配置
>* ``新增`` LoadOptions支持设置inPreferQualityOverSpeed（也可在Configuration中统一配置），你可以在解码速度和图片质量上自由选择

显示：
>* ``修改`` ``cacheInMemory改成了disableCacheInMemory``

请求：
>* ``新增`` 支持file:///****.jpg
>* ``新增`` Download也支持设置requestLevel
>* ``优化`` 去掉了DisplayHelper上的listener和progressListener设置，你只能通过SketchImageView来设置listener和progressListener了
>* ``优化`` 重构\***Request的实现，简化并统一逻辑处理
>* ``修改`` \***Helper.options(Enum)改为optionsByName(Enum)
>* ``优化`` 调低分发线程的优先级，这样能减少display在主线程的耗时，提高页面的流畅度
>* ``新增`` 支持在debug模式下输出display在主线程部分的耗时
>* ``优化`` 本地任务支持多线程，加快处理速度
>* ``BUG`` 修复在Display的commit阶段显示失败时如果没有配置相应的图片就不设置Drawable而导致页面上显示的还是上一个图片的BUG
>* ``新增`` 支持load()和download()支持同步执行
>* ``修改`` 方法名progressListener()改为了downloadProgressListener()

处理：
>* ``新增`` 新增旋转图片处理器RotateImageProcessor
>* ``新增`` RoundedCornerImageProcessor扩展构造函数，支持定义每个角的大小

解码：
>* ``新增`` 支持在debug模式下输出解码耗时
>* ``新增`` 解码支持设置Options.inPreferQualityOverSpeed（通过LoadOptions配置，也可在Configuration中统一配置），你可以在解码速度和图片质量上自由选择

GIF：
>* ``修改`` ``由于显示GIF的场景较少，所以默认不再解码GIF图，在需要解码的地方你可以主动调用 ***Options.setDecodeGifImage(true)或***Helper.decodeGifImage()开启``
>* ``修改`` 删除\***Helper.disableDecodeGif()方法替换为\***Helper.decodeGifImage()
>* ``修改`` 禁止gif图禁止使用内存缓存，因为GifDrawable需要依赖Callback才能播放，如果缓存的话就会出现一个GifDrawable被显示在多个ImageView上的情况，这时候就只有最后一个能正常播放

SketchImageView：
>* ``修改`` SketchImageView.setDisplayOptions(Enum)改名为setOptionsByName(Enum)
>* ``BUG`` 修复使用setImageResource等方法设置图片后在列表中一滑动图片就没了的BUG
>* ``优化`` SketchImageView的ImageShape的圆角角度支持设置每个角的角度
>* ``修改`` SketchImageView的setImageShapeRoundedRadius方法改名为setImageShapeCornerRadius，getImageShapeRoundedRadius方法改名为getImageShapeCornerRadius

其它：
>* ``修改`` 去掉了一些多余的接口设计，例如HelperFactory、ImageSizeCalculator、RequestFactory、
ResizeCalculator、ImageSize、Request、DisplayHelper、LoadHelper、DownloadHelper
>* ``修改`` 所有failure改名为failed
>* ``优化`` apk文件图标的磁盘缓存KEY加上了文件的最后修改时间，这么做是为了避免包路径一样，但是内容已经发生了变化的时候不能及时刷新缓存图标
>* ``新增`` 原生支持通过包名和版本号显示已安装APP的图标，SketchImageView增加displayInstalledAppIcon(String, int)方法、
Sketch增加displayInstalledAppIcon(String, int, ImageViewInterface)和loadInstalledAppIcon(String, int, LoadListener)方法
>* ``优化`` 源码兼容jdk1.6
>* ``优化`` 减少占位图缓存最大容量，调整为最大可用内存的32分之一，但又不能少于2M
>* ``修改`` Sketch.putOptions(RequestOptions)拆分成了Sketch.putDisplayOptions(DisplayOptions)、
Sketch.putLoadOptions(LoadOptions)、Sketch.putDownloadOptions(LoadOptions)
>* ``新增`` 新增ErrorCallback，开发者为通过ErrorCallback接收到解码失败，磁盘缓存安装失败等异常
>* ``修改`` ``读取APK图标时不再过滤默认图标``
>* ``修改`` Configuration中相关参数重命名：
    >* pauseLoad --> globalPauseLoad
    >* pauseDownload --> globalPauseDownload
    >* lowQualityImage --> globalLowQualityImage
    >* cacheInDisk --> globalDisableCacheInDisk
    >* cacheInMemory --> globalDisableCacheInMemory

``注意：本次更新cacheInMemory改成了disableCacheInMemory，cacheInDisk改成了disableCacheInDisk，decodeGifImage的默认值改成了false，所以对这三个属性的调用和设置已定义要重点关注``


# v2.1.1

请求：
>* ``BUG`` 修复在RecyclerView中使用SketchImageView的时候会由于没有重新读取ScaleType导致抛出IllegalArgumentException异常的BUG


# v2.1.0

解码：
>* ``优化`` 解码部分捕获所有异常和错误，避免崩溃

下载：
>* ``优化`` API8以及以下改为使用HttpClient

请求：
>* ``优化`` 增加对不符合使用TransitionImageDisplayer情况的过滤，处理方式是直接抛出异常

缓存:
>* ``BUG`` 修复直接缓存了带有FixedSize导致显示错误的BUG

其它：
>* ``优化`` 持有Context的时候获取ApplicationContext避免误持有Activity
>* ``BUG`` 修复计算InSampleSize时遇到一边未知一边固定的情况会计算出超大的inSampleSize
>* ``BUG`` 修复设置FixedSize时按照src设置bounds导致图片尺寸不一致的BUG


# v2.0.0

本次更新日志：
**download**
>* ``优化`` 默认连接超时时间改为20秒

**process**
>* ``修复`` 新增ResizeCalculator来计算resize以及srcRect、destRect
>* ``新增`` 新增GaussianBlurImageProcessor，能够对图片进行高斯模糊并变暗处理并可自定义模糊半径

**display**
>* ``优化`` TransitionImageDisplayer会忽略RecycleGifDrawable
>* ``优化`` TransitionImageDisplayer在display的时候，如果ImageView没有旧的drawable就用一张透明的图片代替
>* ``新增`` TransitionImageDisplayer、ColorTransitionImageDisplayer、ZoomInImageDisplayer、ZommOutImageDisplayer等增加了一些get、set方法

**cache**
>* ``优化`` 磁盘缓存最大容量默认为100M
>* ``新增`` MemoryCache增加getSize()、getMaxSize()方法
>* ``新增`` DiskCache增加getSize()、getMaxSize()、getReserveSize()方法
>* ``修改`` DiskCache.setDiskCacheDir()方法重命名为DiskCache.setCacheDir()
>* ``修改`` DiskCache删除getCacheFileByUri()方法
>* ``修改`` DiskCache.getCacheFile(Download)方法修改为getCacheFile(String)，并且没有缓存文件的话就返回null
>* ``优化`` 获取缓存文件的时候考虑其它的目录，这样就可以解决一些问题，比如在未插入SD卡之前缓存文件是放在系统空间里的，插入sd卡之后缓存目录就变成了sd卡，那么查找缓存文件的时候也要到系统缓存去看看

**decode**
>* ``新增`` 支持读取本地APK文件的图标，已安装APP的图标也可以通过其本地APK文件的方式读取其图标。另外读取apk图标时会先将apk图标存到磁盘缓存区然后从本地读
>* ``新增`` 支持解码GIF图片，因此ImageDecoder.decode(LoadRequest)方法的返回值类型从bitmap变成了Object

**SketchImageView**
>* ``新增`` setDebugMode(true)方法改名为setShowFromFlag(true)
>* ``优化`` FromFlag新增DISK_CACHE，现在就有四种了，分别是MEMORY_CACHE（绿色）、DISK_CACHE（黄色）、LOCAL（蓝色）、NETWORK（红色）
>* ``修改`` setImageFrom()系列方法改名为displayImage()
>* ``新增`` 支持在暂停下载的时候通过手动点击下载当前图片，只需执行setClickDisplayOnPauseDownload(true)开启此功能即可
>* ``新增`` 支持失败的时候手动点击重新显示图片，setClickRetryOnError(true)开启此功能即可
>* ``修复`` 修复之前为了解决兼容RecyclerView，而取消了在onDetachedFromWindow中设置drawable为null的操作，从而导致在setImageDrawable的时候如果当前要设置的Drawable跟旧的是同一张，就会立马引用计数归零，最终导致图片被回收而引发崩溃的BUG
>* ``优化`` 采用新的方式完美兼容RecyclerView
>* ``新增`` 增加单独的DisplayOptions，支持getDisplayOptions()后单独修改属性，进一步增强了灵活性
>* ``新增`` 新增ImageShape属性，用来指定图片的类型，以便在绘制按下状态和下载进度的时候和图片的形状保持一致，达到最佳的显示效果
>* ``新增`` 集成GifImageView的功能
>* ``新增`` 新增Gif角标功能。通过setGifFlagDrawable(Drawable)设置角标图片即可，然后当发现是GIF图片就会在右下角显示指定的角标图片
>* ``新增`` 新增SketchImageViewInterface接口，将Sketch中用到的方法抽离出来，这样你也可以实现此接口来自定义一个SketchImageView
>* ``新增`` 新增onDisplay()方法，现在你可以继承SketchImageView重写onDisplay()方法，在这里设置一些在getView()方法中需要反复设置的通用属性，具体可以参考示例APP中的MyImageView

**Sketch**
>* ``修改`` 改名为Sketch
>* ``修改`` 修改包名为me.xiaopan.sketch
>* ``修改`` "assets://"协议改为"asset://"
>* ``修改`` "file:///test.png"类型的uri改为"/test.png"，去掉了前面的"file://"
>* ``新增`` 支持读取APK文件的ICON，只需APK文件的路径即可，例如/sdcard/test.apk，如果需要显示已安装APP的图标的话就通过PackageManager拿到其APK文件路径即可
>* ``修改`` 去掉RequestFuture，commit()方法直接返回Request，这样就少创建一个对象了
>* ``优化`` 优化处理流程，当请求取消的时候及时的回收bitmap
>* ``修改`` FailureCause重命名为FailedCause
>* ``新增`` 所有请求新增RequestLevel属性，可决定请求的处理深度，MEMORY表示只从内存加载；LOCAL：表示只从内存和本地加载；NETWORK：表示最终还会从网络加载
>* ``新增`` 结合RequestLevel新增暂停下载图片功能，通过Sketch.getConfiguration().setPauseDownload(boolean)设置即可
>* ``新增`` 在暂停下载功能的基础上提供移动数据下暂停下载图片功能，通过Sketch.getConfiguration().setMobileNetworkPauseDownload(boolean)开启即可
>* ``修改`` Sketch.pause()和Sketch.resume()修改为Sketch.getConfiguration().setPauseLoad(boolean)
>* ``优化`` Helper和Request完全接口化，现在你可以通过自定义Helper和Request来修改整个流程
>* ``优化`` display请求在加载之前会先从内存缓存中查找一下是否有相同ID的图片
>* ``修改`` LoadRequest和DownloadRequest的listener也在主线程执行
>* ``新增`` 集成了android-gif-drawable，支持gif图
>* ``新增`` Configuration新增setDecodeGifImage(true)属性，可全局控制是否解码GIF图片
>* ``新增`` LoadHelper和DisplayHelper新增disableDecodeGifImage()方法可对单次请求控制不解码GIF图片
>* ``新增`` LoadOptions和DisplayOptions新增setDecodeGifImage(boolean)方法可控制是否解码GIF图片
>* ``修改`` DownloadOptions、LoadO以及RequestOptions所有设置相关的方法的名字都改为了以set开头
>* ``新增`` RecycleDrawableInterface新增getMimeType()方法可查看图片类型
>* ``修改`` LoadListener和DisplayListener的onCompleted()方法新增String mimeType参数，返回图片类型
>* ``修改`` LoadListener.onCompleted()方法的第一个参数由Bitmap改为Drawable，因为可能会返回RecycleGifDrawable
>* ``修改`` 新增了一个自定义的FixedSizeBitmapDrawable来解决比例不一致的两张图片使用TransitionDrawable时变形的问题
>* ``优化`` 改善框架实现，便于使用者自定义整个流程中的每个环节
>* ``优化`` 取消Request之间的继承关系
>* ``新增`` DisplayHelper支持设置缓存ID
>* ``修改`` DrawableHolder升级为ImageHolder
>* ``修改`` 不再从ImageView上解析resize，只有你调用了resizeByFixedSize()才会从ImageView上解析resize
>* ``修改`` 当你使用了TransitionImageDisplay并且ImageView的布局尺寸是固定的以及ScaleType是CENTER_CROP的时候会自动开启FixedSizeBitmapDrawable的fixedSize功能，保证在使用TransitionDrawable显示图片的时候不会变形
>* ``修改`` MaxSize调整为在commit的时候检查，如果为null就设置为默认的MaxSize，默认的是屏幕宽高的1.5倍
>* ``新增`` 根据图片MimeType自动选择最合适的Bitmap.Config
>* ``新增`` 支持设置取低质量的图片
>* ``新增`` 对经过ImageProcessor处理的loadingImage、failureImage、pauseDownloadImage提供内存缓存支持，默认缓存容量为可用最大内存的十六分之一，现在你可以放心的对占位图进行各种处理了
>* ``新增`` 增加forceUseResize选项

sample app：
>* ``新增`` 在设置中增加多种开关，包括“显示下载进度”、“移动数据下不下载新图片”、“列表滑动时不加载新图片”等
>* ``新增`` 搜索页和明星个人主页使用瀑布流展示
>* ``新增`` 增加一个页面，展示读取已安装APP或本地APK文件的图标的功能。页面分两部分，分别显示已安装APP列表和扫描到的本地APK包列表
>* ``新增`` 搞一个高斯模糊的图片作为背景
>* ``新增`` 搜索的默认关键词改为gif
>* ``新增`` 本地相册页面支持显示的本地的GIF图
>* ``修改`` 本地相册页面图片改成圆角的

WIKI更新：
>* 将所有assets:// 改为asset://
>* wiki中所有涉及到SketchImageView.setImageFor的方法名都要吸怪为display***
>* Helper和Request完全接口化，现在你可以通过自定义Helper和Request来修改整个流程
>* 新增暂停下载新图片功能，并提供一句话实现移动数据下暂停下载图片，并结合SketchImageView实现暂停时点击下载指定图片
>* SketchImageView的setDebugMode更新了
>* 更新所有关于LoadRequest和DownloadRequest的listener执行位置的说明
>* 要着重介绍SketchImageView可以getDisplayOptions直接修改某项属性
>* SketchImageView增加了ImageShape，可以对按下效果和进度加以限制和图片的形状更加吻合
>* 增加说明最低兼容API-V7
>* 支持RecyclerView，由于RecyclerView的特性，往回滚的时候会检测已回收的ItemView和数据是否一致，一致的话说明无需重新getView，直接使用即可，但是Sketch的处理流程为在getView的设置新的图片，在onDewindow的时候回收Bitmap。这就与RecyclerView的特性冲突了，因此SketchImageView配合RecyclerView增加了恢复的特性
>* 更新示例APP，以及截图


# v1.3.0

**SpearImageView**
>* ``修复``. 兼容RecyclerView，因为在RecyclerView中View的生命周期略有变化，导致图片显示异常，现已修复
>* ``修复``. 取消了在setImageByUri()方法中的过滤请求功能，因为这里只能根据URI过滤。例如：同一个URI在同一个SpearImageView上调用setImageByUri()方法显示了两次，但是这两次显示的时候SpearImageView的宽高是不一样的，结果就是第一次的显示请求继续执行，第二次的显示请求被拒绝了。现在去掉过滤功能后统一都交给了Spear处理，结果会是第一次的显示请求被取消，第二次的显示请求继续执行。
>* ``新增``. 新增在图片表面显示进度的功能，你只需调用setEnableShowProgress(boolean)方法开启即可
>* ``优化``. debug开关不再由Spear.isDebug()控制，而是在SpearImageView中新增了一个debugMode参数来控制
>* ``新增``. 新增类似MaterialDesign的点击涟漪效果。你只需注册点击事件或调用setClickable(true)，然后调用setEnableClickRipple(true)即可
>* ``修复``. 修复了使用SpearImageView时设置了DisplayOptions、DisplayListener等参数，但最终没有通过setImageBy***()方法显示图片而是通过Spear.with(context).display(imageUrl, spearImageView)显示图片最终导致DisplayOptions、DisplayListener等参数不起作用的BUG
>* ``修改``. setImageBy***()系列方法，改名为setImageFrom***()

**Download**
>* ``优化``. 优化HttpClientImageDownloader，读取数据的时候出现异常或取消的时候主动关闭输入流，避免堵塞连接池，造成ConnectionPoolTimeoutException异常
>* ``修改``. 默认下载器改为HttpUrlConnectionImageDownloader.java，而HttpClientImageDownloader则作为备选
>* ``修改``. ImageDownloader.setTimeout()改名为setConnectTimeout()
>* ``优化``. 优化下载的实现，使其更稳定

**Cache**
>* ``删除``. 删除SoftReferenceMemoryCache.java
>* ``移动``. 移动DiskCache.java、LruDiskCache.java、LruMemoryCache.java、MemoryCache.java到cache目录下
>* ``优化``. 调整LruDiskCache的默认保留空间为100M
>* ``新增``. LruDiskCache增加maxsize功能
>* ``修复``. 修复在2.3及以下缓存RecyclingBitmapDrawable的时候忘记添加计数导致Bitmap被提前回收而引发崩溃的BUG

**Decode**
>* ``优化``. 优化了默认的inSampleSize的计算方法，增加了限制图片像素数超过目标尺寸像素的两倍，这样可以有效防止那些一边特小一边特大的图片，以特大的姿态被加载到内存中
>* ``优化``. 将计算默认maxsize的代码封装成一个方法并放到了ImageSizeCalculator.java中
>* ``修复``. 计算maxsize的时候不再考虑ImageView的getWidth()和getHeight()，这是因为当ImageView的宽高是固定的，在循环重复利用的时候从第二次循环利用开始，最终计算出来的size都将是上一次的size，显然这是个很严重的BUG。当所有的ImageView的宽高都是一样的时候看不出来这个问题，都不一样的时候问题就出来了。
>* ``优化``. 默认解码器在遇到1x1的图片时按照失败处理

**Display**
>* ``优化``. 优化了默认ImageDisplayer的实现方式
>* ``修改``. 修改ColorFadeInImageDisplayer的名字为ColorTransitionImageDisplayer；OriginalFadeInImageDisplayer的名字为TransitionImageDisplayer
>* ``修改``. 当你使用TransitionImageDisplayer作为displayer的时候会默认开启resizeByImageViewLayoutSize功能，因为不开启resizeByImageViewLayoutSize的话图片最终就会显示变形

**Execute**
>* ``优化``. 默认任务执行器的任务队列的长度由20调整为200，这是由于如果你一次性要显示大量的图片，队列长度比较小的话，后面的将会出现异常
>* ``优化``. 默认任务执行器的线程池的keepAliveTime时间由1秒改为60秒

**Process**
>* ``修复``. 计算resize的时候不再考虑ImageView的getWidth()和getHeight()，这是因为当ImageView的宽高是固定的，在循环重复利用的时候从第二次循环利用开始，最终计算出来的size都将是上一次的size，显然这是个很严重的BUG。当所有的ImageView的宽高都是一样的时候看不出来这个问题，都不一样的时候问题就出来了。
>* ``优化``. 优化了默认ImageProcessor的实现方式
>* ``优化``. 优化自带的几种图片处理器，对ScaleType支持更完善，更准确

**Request**
>* ``修改``. DisplayListener.From.LOCAL改名为DisplayListener.From.DISK

**Spear**
>* ``优化``. 将一些配置移到了Configuration.java中，debugMode的设置直接改成了静态的
>* ``新增``. 增加pause功能，你可以在列表滚动时调用pause()方法暂停加载新图片，在列表停止滚动后调用resume()方法恢复并刷新列表，通过这样的手段来提高列表滑动流畅度
>* ``修改``. image uri不再支持“file:///mnt/sdcard/image.png”，直接支持“/mnt/sdcard/image.png”
>* ``修复``. 修复了由于DisplayHelper、LoadHelper、DownloadHelper的options()方法参数为null时返回了一个null对象的BUG，这会导致使用SpearImageView时由于没有设置DisplayOptions而引起崩溃
>* ``修改``. 修改DisplayHelper中loadFailedDrawable()方法的名称为loadFailDrawable()
>* ``修复``. 修复DisplayHelper、LoadHelper、DownloadHelper中调用options()方法设置参数的时候会直接覆盖Helper中的参数的BUG，修改后的规则是如果helper中为null，且Options中的参数被设置过才会覆盖
>* ``优化``. 默认图片和失败图片使用ImageProcessor处理时支持使用DisplayHelper中的resize和scaleType
>* ``优化``. 调用display()方法显示图片时，当uri为null或空时显示loadingDrawable
>* ``优化``. display的fire方法去掉了异步线程过滤，由于display基本都是在主线程执行的过滤异步线程没有意义
>* ``修改``. 不再默认根据ImageView的Layout Size设置resize，新增resizeByImageViewLayoutSize()方法开启此功能。另外当你使用TransitionImageDisplayer作为displayer的时候会默认开启resizeByImageViewLayoutSize功能，因为不开启resizeByImageViewLayoutSize的话图片最终就会显示变形


# v1.2.0

>* ``优化``. display的fire方法去掉了异步线程过滤，由于display基本都是在主线程执行的过滤异步线程没有意义
>* ``优化``. 改善了需要通过Handler在主线程执行回调以及显示的方式，以前是使用Runnable，现在时通过Message，这样就避免了创建Runnable，由于display是非常频繁的操作，因此这将会是有意义的改善
>* ``优化``. 优化了DisplayHelper的使用，以前是为每一次display都创建一个DisplayHelper，现在是只要你是按照display().fire()这样连续的使用，那么所有的display将共用一个DisplayHelper，这将会避免创建大量的DisplayHelper
>* ``优化``. ProgressListener.onUpdateProgress(long, long)改为ProgressListener.onUpdateProgress(int, int)，因为int足够用了


# v1.1.3

>* ``修改``. 修改ProgressCallback的名字为ProgressListener并且各个Request.Helper中的progressCallback()方法页改名为progressListener
>* ``优化``. DisplayRequest.Helper.fire()方法不再限制只能在主线程中执行
>* ``修改``. 修改SpearImageView.setImageByDrawable()方法的名称为setImageByResource()


# v1.1.2

>* ``修改``. 修改DisplayRequest.Builder、LoadRequest.Builder、DownloadRequest.Builder的名字为DisplayRequest.Helper、LoadRequest.Helper、DownloadRequest.Helper，这是因为DisplayRequest.Builder原本应有的build()方法被fire()代替了，而功能也是大不一样，所以觉得叫Builder不太合适


# v1.1.1

>* ``新增``. RequestFuture增加了getName()方法用于获取请求名称
>* ``优化``. 优化了SpearImageView中onDetachedFromWindow()取消时的日志
>* ``新增``. SpearImageView的setImageUriBy***系列方法新增了返回值，返回对应的RequestFuture，方便查看请求的状态
>* ``修改``. SpearImageView的setImageByUri(Uri)方法改名为setImageByContent(Uri)


# v1.1.0

>* ``新增``. ImageDownloader新增setProgressCallbackNumber(int)方法可用来控制进度回调次数
>* ``新增``. DownloadListener、LoadLinstener、DisplayListener的onCompleted()方法新增From参数，用来表示数据来自哪里
>* ``新增``.  SpearImageView新增类似Picasso的Debug功能，只需调用Spear.setDebugMode(true)开启调试模式即可开启此功能
>* ``优化``. 优化内置的几种图片处理器的resize处理规则。当原图尺寸小于resize时，之前是担心会创建一张更大的图，浪费内存，于是做法是尺寸不变，现在的做法是依然处理但是resize要根据原图尺寸重新计算，原则就是保证新的resize小于原图尺寸并且宽高比同旧的resize一样。例如原图宽高是300x225，resize宽高是400x400，那么之前的结果就是resize还是400x400，最终图片是300x225，而现在的结果是调整resize为255x255，最终图片是225x225
>* ``新增``. 支持仅根据宽或高限制图片大小，例如：maxsize为500x-1，意思就是宽最大为500，高随之缩放
>* ``优化``. 调整了DefaultRequestExecitor的创建方式，网络下载线程池最大容量由10修改为5
>* ``优化``. 调整了DisplayRequest.Helper的options()方法里应用DisplayOptions.resize的规则


# v1.0.0

Spear 脱胎换骨，全新出发

