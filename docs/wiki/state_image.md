# 使用 StateImage 设置占位图片和状态图片

Sketch 支持不同的状态显示不同的占位图片：

* loadingImage：正在加载时显示的占位图
* errorImage：加载失败时显示的占位图
* pauseDownloadImage：暂停下载时现实的占位图

这三种属性都可以在 [DisplayOptions] 和 [DisplayHelper] 中配置

通过 [StateImage] 你可以灵活的使用各种来源的图片为上述三种状态提供占位图

### StateImage

目前内置了一下几种 [StateImage]

* [DrawableStateImage]：给什么图片显示什么图片，支持 [ShapeSize] 和 [ImageShaper]
* [MakerStateImage]：可以利用 [DisplayOptions] 中配置的 [ImageProcessor] 和 [Resize] 修改指定的图片然后再作为状态图片，同样支持 [ShapeSize] 和 [ImageShaper]
* [OldStateImage]：使用当前正在显示的图片作为状态图片
* [MemoryCacheStateImage]：从内存缓存中获取图片作为状态图片，支持 [ShapeSize] 和 [ImageShaper]，更详细的使用方法请参考 [使用 MemoryCacheStateImage 先显示已缓存的较模糊的图片，然后再显示清晰的图片][memory_cache_state_image]

### 自定义

实现 [StateImage] 接口，最起码你要支持 [ShapeSize] 和 [ImageShaper]，具体可参考 [DrawableStateImage] 或 [MakerStateImage]

通过 [DisplayOptions].setLoadingImage(StateImage) 方法 或 [DisplayHelper].loadingImage(StateImage) 方法使用即可

[StateImage]: ../../sketch/src/main/java/me/xiaopan/sketch/state/StateImage.java
[DisplayOptions]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DisplayOptions.java
[DisplayHelper]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DisplayHelper.java
[ShapeSize]: ../../sketch/src/main/java/me/xiaopan/sketch/request/ShapeSize.java
[ImageShaper]: ../../sketch/src/main/java/me/xiaopan/sketch/shaper/ImageShaper.java
[memory_cache_state_image]: memory_cache_state_image.md
[ImageProcessor]: image_processor.md
[Resize]: resize.md
[DrawableStateImage]: ../../sketch/src/main/java/me/xiaopan/sketch/state/DrawableStateImage.java
[MakerStateImage]: ../../sketch/src/main/java/me/xiaopan/sketch/state/MakerStateImage.java
[OldStateImage]: ../../sketch/src/main/java/me/xiaopan/sketch/state/OldStateImage.java
[MemoryCacheStateImage]: ../../sketch/src/main/java/me/xiaopan/sketch/state/MemoryCacheStateImage.java
