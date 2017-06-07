:fire::fire::fire: 如果使用了根据枚举存储和获取Options的功能就不能无痛升级 :fire::fire::fire:

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
