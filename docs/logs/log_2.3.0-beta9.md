Sketch:

>* `MemoryCache` Sketch类新增onLowMemory()和onTrimMemory(int)，需要在readme中说明
>* `stateImageMemoryCache` 去掉stateImageMemoryCache，共用一个内存缓存器
>* `minSdkVersion` 最低支持版本升到9
>* `bitmap pool` 增加bitmap pool，减少内存分配，减少GC回收造成的卡顿
>* `ExceptionMonitor` ExceptionMonitor改名为SketchMonitor并挪到顶级目录
>* :bug: `GIF` onDetachedFromWindow时主动回收GifDrawable
>* :sparkles: `bitmapPoolDisabled` LoadOptions、LoadHelper、DisplayOptions、DisplayHelper增加disableBitmapPool属性
>* disableCacheInDisk和disableCacheInMemory属性改名为cacheInDiskDisabled和cacheInMemoryDisabled
>* `Lock` 磁盘缓存编辑锁挪到了LockPool中，移除内存缓存编辑锁
>* :bug: 修复同一内存缓存ID可以出现多个请求同时执行的BUG
>* :bug: 修复由于同一内存缓存ID可以出现多个请求，导致会出现同一个内存缓存ID会连续存入多个缓存对象，
那么新存入的缓存对象就会挤掉旧的缓存对象，如果旧的缓存对象只有缓存引用，那么旧的缓存对象会被直接回收

Sample APP

>* 本地相册调整成每行四个，并去掉圆角

待办：
>* 完善bitmap pool的文档