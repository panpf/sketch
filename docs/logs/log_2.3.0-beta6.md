优化：
>* `id` 计算ID时，forceUseResize和thumbnailMode依赖resize

新增：
>* `cacheProcessedImageInDisk` 为了加快速度，将经过ImageProcessor、resize或thumbnailMode处理过读取时inSampleSize大于等于8的图片保存到磁盘缓存中，下次就直接读取