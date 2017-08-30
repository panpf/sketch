:fire: 如果你用到了下述重命名和移除部分功能以及 ErrorTracker 的话就不能无痛升级 :fire: 

bugs：
* :bug: 修复在某些 ROM 上查找 Initializer 时碰到 meta 值不是 String 时崩溃的 BUG
* :bug: 修复 Sketch 的单例可能失效的 BUG
* :bug: 修复在多线程环境下可能拿到一个尚未执行 Initializer 的 Sketch 的 bug
* :bug: 修复 ImageZoomer 的双击事件和单击事件可能会有冲突的BUG

重命名：
* :hammer: Configuration.setImageSizeCalculator(ImageSizeCalculator) 方法改名为 setSizeCalculator
* :hammer: Configuration.getImageSizeCalculator() 方法改名为 getSizeCalculator
* :hammer: Configuration.setMobileNetworkGlobalPauseDownload(boolean) 方法改名为 setGlobalMobileNetworkPauseDownload
* :hammer: Configuration.istMobileNetworkGlobalPauseDownload() 方法改名为 isGlobalMobileNetworkPauseDownload
* :hammer: LargeImageViewer 重命名为 HugeImageViewer 
* :hammer: SketchImageView.isBlockDisplayLargeImageEnabled() 重命名为 SketchImageView.isHugeImageEnabled() 
* :hammer: SketchImageView.setBlockDisplayLargeImageEnabled(boolean) 重命名为 SketchImageView.setHugeImageEnabled(boolean) 
* :hammer: SketchImageView.getLargeImageViewer() 重命名为 SketchImageView.getHugeImageViewer() 

移除：
* :fire: 移除 SLogType 类，其功能整合到了 SLog 中 ([了解 Sketch 日志])
* :fire: 移除 SLogTracker 类，新增 SLog.Proxy 代替之 ([了解 Sketch 日志])
* :fire: 移除了 UriScheme

变更：
* :hammer: file:// 格式的 uri 已产生的磁盘缓存将全部作废，因为其磁盘缓存 key 去掉了 file://

新增：
* :sparkles: 新增了 UriModel

wiki待办：
* UriModel wiki 新增
* UriScheme wiki 删除


[了解 Sketch 日志]: ../wiki/log.md


