Sketch:

>* `MemoryCache` Sketch类新增onLowMemory()和onTrimMemory(int)，需要在readme中说明
>* `stateImageMemoryCache` 去掉stateImageMemoryCache，共用一个内存缓存器
>* `minSdkVersion` 最低支持版本升到9
>* `bitmap pool` 增加bitmap pool，减少内存分配，减少GC回收造成的卡顿
>* `ExceptionMonitor` ExceptionMonitor改名为SketchMonitor并挪到顶级目录
>* :bug: `GIF` onDetachedFromWindow时主动回收GifDrawable
>* :sparkles: `disableBitmapPool` LoadOptions、LoadHelper、DisplayOptions、DisplayHelper增加disableBitmapPool属性

Sample APP

>* 本地相册调整成每行四个，并去掉圆角

待办：
>* 完善bitmap pool的文档