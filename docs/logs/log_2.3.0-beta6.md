优化：
>* `id` 计算ID时，forceUseResize和thumbnailMode依赖resize
>* `RequestAttrs` 重构RequestAttrs，如果你自定义的一些功能要用到的话就需要改一下

新增：
>* `cacheProcessedImageInDisk` 为了加快速度，将经过ImageProcessor、resize或thumbnailMode处理过读取时inSampleSize大于等于8的图片保存到磁盘缓存中，下次就直接读取

删除：
>* `memoryCacheId` 删除DisplayHelper的memoryCacheId(String)方法