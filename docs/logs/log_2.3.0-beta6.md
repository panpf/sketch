优化：
>* `forceUseResize` 计算内存缓存ID时，如果没有resize不再考虑forceUseResize了

新增：
>* `cacheProcessedImageInDisk` 为了加快速度，将经过ImageProcessor、resize或thumbnailMode处理过的图片保存到磁盘缓存中，下次就直接读取