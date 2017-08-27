
bugs:
* :bug: 修复在某些 ROM 上查找 Initializer 时碰到 meta 值不是 String 时崩溃的 BUG
* :bug: 修复 Sketch 的单例可能失效的 BUG
* :bug: 修复在多线程环境下可能拿到一个尚未执行 Initializer 的 Sketch 的 bug

变更：
* :hammer: 重构 SLog，去掉 SLogTracker，新增 SLog.Proxy，请查看文档 [了解 Sketch 日志](../wiki/log.md)
* :hammer: Configuration.setImageSizeCalculator(ImageSizeCalculator) 方法改名为 setSizeCalculator
* :hammer: Configuration.getImageSizeCalculator() 方法改名为 getSizeCalculator
* :hammer: Configuration.setMobileNetworkGlobalPauseDownload(boolean) 方法改名为 setGlobalMobileNetworkPauseDownload
* :hammer: Configuration.istMobileNetworkGlobalPauseDownload() 方法改名为 isGlobalMobileNetworkPauseDownload
