

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

为何要移除？经实际使用发现，即使在Application中第一时间存储Options，也会出现取不到本应该存在的Options的情况，因此推荐改用懒加载的方式管理Options，详情可参考Demo里的 [ImageOptions.java](https://github.com/xiaopansky/sketch/blob/master/sample/src/main/java/me/xiaopan/sketchsample/ImageOptions.java) 或 [如何管理多个Options.md](https://github.com/xiaopansky/sketch/blob/master/docs/wiki/options_manage.md)

### Initializer
:sparkles: 新增Initializer可以在AndroidManifest.xml中配置初始化类，这样就不用在Application中初始化了，可减轻Application的负担，也可百分之百保证第一时间完成Sketch的初始化，详情请参考[initializer.md](https://github.com/xiaopansky/sketch/blob/master/docs/wiki/initializer.md)

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
