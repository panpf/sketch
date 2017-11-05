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
[ImageZoomer]: ../../sketch/src/main/java/me/panpf/sketch/viewfun/zoom/ImageZoomer.java
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
