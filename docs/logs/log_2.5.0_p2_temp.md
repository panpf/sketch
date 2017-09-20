bug 修复版

bugs：
* :bug: 修复 2.5.0-p1版本改出来的 无法显示 content chunked 的图片
* :bug: 修复 [Resize].byViewFixedSize(Resize.Mode) 方法设置 mode 无效的 bug

变更：
* :hammer: [Listener].onStartLoad() 方法名字改为 onReadyLoad()
* :hammer: [ImageShaper] 接口新增 getPath(Rect bounds) 方法，获取形状的 Path
* :hammer: 重构 [SketchImageView] 的下载进度和按下状态功能的蒙层形状配置方式，详情请参考 [SketchImageView 使用指南][sketch_image_view]
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
* :hammer: [LoadOptions].getImageProcessor() 重命名为 getProcessor()
* :hammer: [LoadOptions].setImageProcessor(ImageProcessor) 重命名为 setProcessor(ImageProcessor)
* :hammer: [DisplayOptions].getImageDisplayer() 重命名为 getDisplayer()
* :hammer: [DisplayOptions].setImageDisplayer(ImageDisplayer) 重命名为 setDisplayer(ImageDisplayer)
* :hammer: [DisplayOptions].getImageShaper() 重命名为 getShaper()
* :hammer: [DisplayOptions].setImageShaper(ImageShaper) 重命名为 setShaper(ImageShaper)

删除：
* :fire: 删除 RequestLevelFrom ，因此取消原因中不再区分 REQUEST_LEVEL_IS_LOCAL 和 REQUEST_LEVEL_IS_MEMORY
* :fire: 删除 [CancelCause].REQUEST_LEVEL_IS_LOCAL 和 REQUEST_LEVEL_IS_MEMORY
* :fire: 删除 [SketchUtils].makeRequestKey(String, UriModel, DownloadOptions) 方法，makeRequestKey(String, UriModel, String) 方法代替之
* :fire: 删除 [SketchUtils].makeRequestKey(String, String) 方法，makeRequestKey(String, UriModel, String) 方法代替之
* :fire: 删除 [SketchUtils].makeStateImageMemoryCacheKey(String, DownloadOptions) 方法，makeRequestKey(String, UriModel, String) 方法代替之
* :fire: 删除 [SketchImageView].ImageShape 
* :fire: 删除 [SketchImageView].getImageShape() 方法
* :fire: 删除 [SketchImageView].setImageShape([SketchImageView].ImageShape) 方法
* :fire: 删除 [SketchImageView].getImageShapeCornerRadius() 方法
* :fire: 删除 [SketchImageView].setImageShapeCornerRadius(float[]) 方法
* :fire: 删除 [SketchImageView].setImageShapeCornerRadius(float) 方法
* :fire: 删除 [SketchImageView].setImageShapeCornerRadius(float, float, float, float) 方法
* :fire: 移除 [SketchImageView] 按下状态的涟漪效果

优化：
* :zap: 移动数据暂停下载功能支持识别 流量共享 WIFI 热点，更多内容请参考 [移动数据下暂停下载图片，节省流量][pause_download]

新功能：
* :sparkles: 新增 [OptionsFilter] 可统一过滤修改 Options

sample app：
* 修复了 gif 图无法播放的问题

[pause_download]: ../wiki/pause_download.md
[sketch_image_view]: ../wiki/sketch_image_view.md
[SketchImageView]: ../../sketch/src/main/java/me/xiaopan/sketch/SketchImageView.java
[SketchUtils]: ../../sketch/src/main/java/me/xiaopan/sketch/util/SketchUtils.java
[ImageShaper]: ../../sketch/src/main/java/me/xiaopan/sketch/shaper/ImageShaper.java
[CancelCause]: ../../sketch/src/main/java/me/xiaopan/sketch/request/CancelCause.java
[Listener]: ../../sketch/src/main/java/me/xiaopan/sketch/request/Listener.java
[Resize]: ../../sketch/src/main/java/me/xiaopan/sketch/request/Resize.java
[LoadOptions]: ../../sketch/src/main/java/me/xiaopan/sketch/request/LoadOptions.java
[DisplayOptions]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DisplayOptions.java
[OptionsFilter]: ../../sketch/src/main/java/me/xiaopan/sketch/optionsfilter/OptionsFilter.java
[Configuration]: ../../sketch/src/main/java/me/xiaopan/sketch/Configuration.java