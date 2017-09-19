bug 修复版

bugs：
* :bug: 修复 2.5.0-p1版本改出来的 无法显示 content chunked 的图片
* :bug: 修复 Resize.byViewFixedSize(Resize.Mode) 方法设置 mode 无效的 bug

变更：
* :hammer: Listener.onStartLoad() 方法名字改为 onReadyLoad()

删除：
* :fire: 删除 RequestLevelFrom ，因此取消原因中不再区分 REQUEST_LEVEL_IS_LOCAL 和 REQUEST_LEVEL_IS_MEMORY
* :fire: 删除 CancelCause.REQUEST_LEVEL_IS_LOCAL 和 REQUEST_LEVEL_IS_MEMORY
* :fire: 删除 SketchUtils.makeRequestKey(String, UriModel, DownloadOptions) 方法，makeRequestKey(String, UriModel, String) 方法代替之
* :fire: 删除 SketchUtils.makeRequestKey(String, String) 方法，makeRequestKey(String, UriModel, String) 方法代替之
* :fire: 删除 SketchUtils.makeStateImageMemoryCacheKey(String, DownloadOptions) 方法，makeRequestKey(String, UriModel, String) 方法代替之

优化：
* :zap: 移动网络暂停下载功能支持识别 流量共享 WIFI 热点，更多内容请参考 [移动网络下暂停下载图片，节省流量][pause_download]

新功能：
* :sparkles: 新增 OptionsFilter 可统一过滤修改 Options

sample app：
* 修复了来自百度的 gif 图无法播放的问题

待办：
* 写文档介绍 OptionsFilter

[pause_download]: ../wiki/pause_download.md