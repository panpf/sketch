这是一个大的重构版本，重点重构了日志和 uri 部分

:fire: 如果你用到了下述重命名、移除和重构以及 ErrorTracker 的话就不能无痛升级 :fire:

bugs：
* :bug: 修复在某些 ROM 上查找 Initializer 时碰到 meta 值不是 String 时崩溃的 BUG [#43]
* :bug: 修复 Sketch 的单例可能失效的 BUG
* :bug: 修复在多线程环境下可能拿到一个尚未执行 Initializer 的 Sketch 的 bug
* :bug: 修复 ImageZoomer 的双击事件和单击事件可能会有冲突的 BUG
* :bug: 修复通过 SketchUtils.generatorTempFileName(DataSource, String) 方法生成的文件名中含有换行符导致创建文件失败的BUG，直接影响了 DataSource.getFile(File, String) 方法
* :bug: 修复当同一个文件被用于多种 uri 支持时，其磁盘缓存可能错乱的 BUG

重命名：
* :fire: Configuration.setImageSizeCalculator(ImageSizeCalculator) 重命名为 setSizeCalculator(ImageSizeCalculator)
* :fire: Configuration.getImageSizeCalculator() 方法改名为 getSizeCalculator
* :fire: Configuration.setMobileNetworkGlobalPauseDownload(boolean) 重命名为 setGlobalMobileNetworkPauseDownload(boolean)
* :fire: Configuration.istMobileNetworkGlobalPauseDownload() 重命名为 isGlobalMobileNetworkPauseDownload()
* :fire: LargeImageViewer 重命名为 HugeImageViewer
* :fire: SketchImageView.isBlockDisplayLargeImageEnabled() 重命名为 isHugeImageEnabled()
* :fire: SketchImageView.setBlockDisplayLargeImageEnabled(boolean) 重命名为 setHugeImageEnabled(boolean)
* :fire: SketchImageView.getLargeImageViewer() 重命名为 getHugeImageViewer()

移除：
* :fire: 移除 SLogType，其功能整合到了 SLog 中，详情亲参考[日志][log]
* :fire: 移除 SLogTracker，新增 SLog.Proxy 代替之，详情亲参考[日志][log]
* :fire: 移除 UriScheme 用 UriModel 代替之，UriScheme.valueOfUri(String) 用 UriModel.match(Context, String) 代替，UriScheme.xxx.createUri(String) 被 xxxUriModel.makeUri() 代替
* :fire: 移除 ImagePreprocessor 用 UriModel 代替之，相关类 Preprocessor、PreProcessResult 也一并移除
* :fire: 移除 Sketch.createInstalledAppIconUri(String, int) 方法，AppIconUriModel.makeUri(String, int) 方法替代之
* :fire: 移除 DownloadHelper.listener(DownloadListener) 方法
* :fire: 移除 LoadHelper.listener(LoadListener) 方法
* :fire: 移除 DownloadListener、LoadListener、DisplayListener 的 onStarted() 方法 用
* :fire: 移除 SketchImageView.displayInstalledAppIcon(String, int) 方法，新的使用方法请参考[显示 APK 或已安装 APP 的图标][display_apk_or_app_icon]
* :fire: 移除 Sketch.displayInstalledAppIcon(String, int, SketchView) 方法，新的使用方法请参考[显示 APK 或已安装 APP 的图标][display_apk_or_app_icon]
* :fire: 移除 Sketch.loadInstalledAppIcon(String, int, LoadListener) 方法，新的使用方法请参考[显示 APK 或已安装 APP 的图标][display_apk_or_app_icon]

重构：
* :hammer: file:// 格式的 uri 已产生的磁盘缓存将全部作废，因为其磁盘缓存 key 去掉了 file://
* :hammer: 现在你要显示 apk icon 就必须使用 apk.icon:// 协议，详情请参考[显示 APK 或已安装 APP 的图标][display_apk_or_app_icon]
* :hammer: DownloadListener、LoadListener、DisplayListener 新增 onStartLoad() 方法代替之前的 onStarted() ，现在只有在需要进入异步线程加载数据时才会回调 onStartLoad() 方法
* :hammer: 现在 LoadHelper 不接受空的 LoadListener
* :hammer: Sketch.displayFromContent(Uri, SketchView) 方法签名现在改为 Sketch.displayFromContent(String, SketchView)
* :hammer: Sketch.loadFromURI(Uri, SketchView) 方法重构为 Sketch.loadFromContent(String, SketchView)
* :hammer: SketchImageView.displayContentImage(Uri) 方法签名现在改为 SketchImageView.displayContentImage(String)
* :hammer: Initializer.onInitialize(Context, Sketch, Configuration) 方法签名现在改为 onInitialize(Context, Configuration)
* :hammer: ImageProcessor.process(Sketch, Bitmap, Resize, boolean, boolean) 方法签名现在改为 process(Sketch, Bitmap, Resize, boolean)
* :hammer: WrappedImageProcessor.onProcess(Sketch, Bitmap, Resize, boolean, boolean) 方法签名现在改为 onProcess(Sketch, Bitmap, Resize, boolean
* :hammer: 移除 resizeByFixedSize 属性，用 Resize.byViewFixedSize() 代替，详情参考 [使用 Resize 精确修改图片的尺寸][resize]
* :hammer: 移除 shapeSizeByFixedSize 属性，用 ShapeSize.byViewFixedSize() 代替，详情参考 [通过 ShapeSize 在绘制时改变图片的尺寸][shape_size]
* :hammer: 移除 forceUseResize 属性，用 Resize.Mode 代替，详情参考 [使用 Resize 精确修改图片的尺寸][resize]
* :hammer: 移除 ErrorCause 枚举重构

新增：
* :sparkles: 新增 UriModel，来实现支持不同的 uri 协议
* :sparkles: 新增 apk.icon:///sdcard/file.apk 协议来显示 apk icon，代替 ApkIconPreprocessor，详情请参考[显示 APK 或已安装 APP 的图标][display_apk_or_app_icon]
* :sparkles: 新增 app.icon://me.xiaopan.sketchsample/240 协议来显示 app icon，代替 InstallAppIconPreprocessor，详情请参考[显示 APK 或已安装 APP 的图标][display_apk_or_app_icon]
* :sparkles: 新增 android.resource://me.xiaopan.sketchsample/drawable/ic_launcher 协议
* :sparkles: 现在 sketch 里大部分对外的接口都加上了 @NonNull 或 @Nullable 注解，因此现在需要明确依赖 support-annotations

[log]: ../wiki/log.md
[resize]: ../wiki/resize.md
[shape_size]: ../wiki/shape_size.md
[display_apk_or_app_icon]: ../wiki/display_apk_or_app_icon.md
[#43]: https://github.com/panpf/sketch/issues/43