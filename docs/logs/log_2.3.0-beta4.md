新功能：
>* `ImageShaper` 新增ImageShaper，用来在绘制时修改图片的形状，[点击查看使用介绍](../wiki/image_shaper.md)
>* `ShapeSize` 新增ShapeSize，用来在绘制时在修改图片的尺寸，[点击查看使用介绍](../wiki/shape_size.md)

优化：
>* `resizeByFixedSize` 设置了resizeByFixedSize但是ImageView的宽高没有固定的话就抛出异常
>* `MakerDrawableModeImage` MakerDrawableModeImage不再接受设置resize, lowQualityImage, forceUseResize, imageProcessor，现在自动从DisplayOptions中取
>* `resize`调用setResize()时不再默认设置resizeByFixedSize为false
>* `resizeByFixedSize`调用setResizeByFixedSize()时不再默认设置resize为null

其它：
>* `Rename` RoundedCornerImageProcessor重命名为RoundRectImageProcessor
>* `Rename` ModeImage重命名为StateImage
>* `Rename` DrawableModeImage重命名为DrawableStateImage
>* `Rename` MemoryCacheModeImage重命名为MemoryCacheStateImage
>* `Rename` MakerDrawableModeImage重命名为MakerStateImage