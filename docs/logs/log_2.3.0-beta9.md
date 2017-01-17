缓存：
>* :sparkles: Sketch类中新增onLowMemory()和onTrimMemory(int)方法，用于在内存较低时释放缓存，需要在Application中回调，具体请查看README或参考demo app
>* :fire: 去掉stateImageMemoryCache，共用一个内存缓存器
>* :sparkles: `bitmap pool` 增加bitmap pool，减少内存分配，降低因GC回收造成的卡顿

GIF：
>* :bug: onDetachedFromWindow时主动回收GifDrawable

请求：
>* :art: `Attribute` disableCacheInDisk和disableCacheInMemory属性改名为cacheInDiskDisabled和cacheInMemoryDisabled
>* :sparkles: `Attribute` LoadOptions、LoadHelper、DisplayOptions、DisplayHelper增加disableBitmapPool属性
>* :fire: `Lock` 移除内存缓存编辑锁
>* :bug: `Filter repeat` 修复同一内存缓存ID可以出现多个请求同时执行的BUG
>* :bug: `Filter repeat` 修复由于同一内存缓存ID可以出现多个请求，导致会出现同一个内存缓存ID会连续存入多个缓存对象，那么新存入的缓存对象就会挤掉旧的缓存对象，如果旧的缓存对象只有缓存引用，那么旧的缓存对象会被直接回收
>* :zap: `FreeRide` 新增FreeRide机制避免重复下载和加载
>* :bug: `TransitonImageDisplayer` 修复从ImageView上取当前图片作为过渡图片时没有过滤LayerDrawable，导致可能会越套越深的BUG

ImageShaper：
>* :zap: `RoundRectImageShaper` 优化描边的绘制方式，不再出现圆角除盖不住的情况

其它：
>* :arrow_up: `minSdkVersion` 最低支持版本升到9
>* :art: `ExceptionMonitor` ExceptionMonitor改名为SketchMonitor并挪到顶级目录
>* :zap: `Log` 日志分不同的类型分别提供开关控制，详见[SLogType.java](../../sketch/src/main/java/me/xiaopan/sketch/SLogType.java)
