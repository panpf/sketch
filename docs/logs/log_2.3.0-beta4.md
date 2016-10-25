新功能：
>* `ImageShaper` 新增ImageShaper，用来在绘制时修改图片的形状
>* `ShapeSize` 新增ShapeSize，用来在绘制时在修改图片的尺寸

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

wiki待办：
>* TransiDisplayer文档修改
>* 新增文档讲解如何在详情页先显示不太清晰的图
>* 新增了几个属性imageShaper，shapeSize，shapeSizeByFixedSize;
>* 新增文档如何在绘制时改变图片的形状