StateImage用来为loadingImage，errorImage，pauseDownloadImage提供图片

#### 现支持以下几种：
>* DrawableStateImage：给什么图片显示什么图片，支持ShapeSize和ImageShaper
>* OldStateImage：使用当前ImageView正在显示的图片作为状态图片
>* MemoryCacheStateImage：从内存缓存中获取图片作为状态图片，支持ShapeSize和ImageShaper，[点击查看更详细的介绍](memory_cache_state_image.md)
>* MakerStateImage：可以利用Options中配置的ImageProcessor和resize修改原图片，同样支持ShapeSize和ImageShaper

#### 自定义：
直接实现StateImage接口实现即可，但要注意的是你要保证实现ShapeSize和ImageShaper功能，详情可参考DrawableStateImage

自定义完成后通过\*\*\*Options.setLoadingImage(StateImage)方法或\*\*\*Helper.loadingImage(StateImage)方法使用即可