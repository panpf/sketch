:fire: 如果你用到了下述重命名和移除部分功能以及 ErrorTracker 的话就不能无痛升级 :fire: 

bugs：
* :bug: 修复在某些 ROM 上查找 Initializer 时碰到 meta 值不是 String 时崩溃的 BUG
* :bug: 修复 Sketch 的单例可能失效的 BUG
* :bug: 修复在多线程环境下可能拿到一个尚未执行 Initializer 的 Sketch 的 bug
* :bug: 修复 ImageZoomer 的双击事件和单击事件可能会有冲突的BUG

重命名：
* :fire: Configuration.setImageSizeCalculator(ImageSizeCalculator) 方法改名为 setSizeCalculator
* :fire: Configuration.getImageSizeCalculator() 方法改名为 getSizeCalculator
* :fire: Configuration.setMobileNetworkGlobalPauseDownload(boolean) 方法改名为 setGlobalMobileNetworkPauseDownload
* :fire: Configuration.istMobileNetworkGlobalPauseDownload() 方法改名为 isGlobalMobileNetworkPauseDownload
* :fire: LargeImageViewer 重命名为 HugeImageViewer 
* :fire: SketchImageView.isBlockDisplayLargeImageEnabled() 重命名为 SketchImageView.isHugeImageEnabled() 
* :fire: SketchImageView.setBlockDisplayLargeImageEnabled(boolean) 重命名为 SketchImageView.setHugeImageEnabled(boolean) 
* :fire: SketchImageView.getLargeImageViewer() 重命名为 SketchImageView.getHugeImageViewer() 
* :fire: SketchImageView.displayInstalledAppIcon(String, int) 重命名为 displayAppIcon(String, int)
* :fire: Sketch.displayInstalledAppIcon(String, int, SketchView) 重命名为 displayAppIcon(String, int, SketchView)
* :fire: Sketch.loadInstalledAppIcon(String, int, LoadListener) 重命名为 loadAppIcon(String, int, LoadListener)
* :fire: Sketch.loadFromURI(Uri, LoadListener) 重命名为 loadFromContent(Uri, LoadListener)

移除：
* :fire: 移除 SLogType 类，其功能整合到了 SLog 中 ([了解 Sketch 日志])
* :fire: 移除 SLogTracker 类，新增 SLog.Proxy 代替之 ([了解 Sketch 日志])
* :fire: 移除了 UriScheme
* :fire: 移除 ImagePreprocessor 机制，UriModel 代替之，相关类 Preprocessor、PreProcessResult 也一并移除
* :fire: 移除 Sketch.createInstalledAppIconUri(String, int) 方法，AppIconUriModel.makeUri(String, int) 方法替代之

变更：
* :hammer: file:// 格式的 uri 已产生的磁盘缓存将全部作废，因为其磁盘缓存 key 去掉了 file://
* :hammer: 现在你要现实 apk icon 就必须使用 apk.icon://协议 

新增：
* :sparkles: 新增了 UriModel
* :sparkles: 新增 apk.icon:///sdcard/file.apk 协议
* :sparkles: 新增 app.icon://me.xiaopan.sketchsample/240 协议
* :sparkles: SketchImageView 新增 displayApkIcon(String) 方法，用于显示 APK 的图标
* :sparkles: Sketch 新增 displayApkIcon(String, SketchView) 方法，用于显示 APK 的图标
* :sparkles: Sketch 新增 loadApkIcon(String, LoadListener) 方法，用于加载 APK 的图标

wiki待办：
* UriModel wiki 新增
* UriScheme wiki 删除
* 现在有专门的 apk.icon:// 协议支持，那么现实 apk icon 的方式就得变化了


[了解 Sketch 日志]: ../wiki/log.md


