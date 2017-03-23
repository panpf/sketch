2.3.0是一个大版本，除了有大量的BUG修复外，还重构了占位图系统，增加了手势缩放、分块显示超大图、ImageShaper、BitmapPool、缩略图模式等新功能

`推荐度：强烈建议升级`

`升级：不能平滑升级，因为有些许重命名和重构`

##### 下载
>* :bug: 支持下载那些服务端无法确定文件大小导致响应头里只有Transfer-Encoding: chunked没有Content-Length的图片

##### 缓存
>* :bug: [#4](https://github.com/xiaopansky/sketch/issues/4) 修复由于在内存中缓存了Drawable，导致同一个缓存Drawable在两个不同的地方使用时bounds被改变从而图片大小显示异常，常见的表现为点击图片进入图片详情页后再回来发现图片变小了
>* :bug: [#11](https://github.com/xiaopansky/sketch/issues/11) 修复最后一条磁盘缓存无效的BUG，这是DiskLruCache的BUG，因为在commit的时候没有持久化操作记录导致的
>* :bug: [#14](https://github.com/xiaopansky/sketch/issues/14) 修复ImageHolder直接缓存了Drawable导致同一个Drawable在多个FIX_XY的ImageView上显示时大小异常的BUG
>* :bug: `DiskLruCache` 捕获commit时可能出现的java.lang.IllegalStateException: edit didn't create file 0异常
>* :fire: 去掉stateImageMemoryCache，共用一个内存缓存器
>* :sparkles: Sketch类中新增onLowMemory()和onTrimMemory(int)方法，用于在内存较低时释放缓存，需要在Application中回调，具体请查看README或参考demo app
>* :sparkles: 新增[BitmapPool](../wiki/bitmap_pool.md)，可减少内存分配，降低因GC回收造成的卡顿

##### Drawable
>* :bug: [#13](https://github.com/xiaopansky/sketch/issues/13) 修复SketchBitmapDrawable由于没有设置TargetDensity而始终以160的默认像素密度来缩小图片最终导致通过getIntrinsicWidth()得到的尺寸始终比Bitmap实际尺寸小的BUG

##### 占位图（[StateImage](../wiki/state_image.md)）：
>* :hammer: 重构占位图系统，更名为StateImage，现在可以用任意类型的drawable作为loading占位图了

##### ImageDisplayer
>* :bug: `TransitionImageDisplayer`. 修复TransitionImageDisplayer在碰到ImageView当前没有图片时崩溃的BUG
>* :bug: `TransitionImageDisplayer` 修复从ImageView上取当前图片作为过渡图片时没有过滤LayerDrawable，导致可能会越套越深的BUG
>* :zap: `TransitionImageDisplayer`. 对TransitionImageDisplayer的安全验证条件中不再验证CENTER_CROP
>* :sparkles: `FadeInImageDisplayer`. 新增渐入图片显示器FadeInImageDisplayer
>* :sparkles: `ImageDisplayer` ImageDisplayer新增setAlwaysUse(boolean)方法，可设置只要涉及到显示图片就得使用ImageDisplayer（显示从内存里取出的缓存图片时也不例外）

#### ImageProcessor
>* :hammer: `RoundedCornerImageProcessor` RoundedCornerImageProcessor重命名为RoundRectImageProcessor
>* :art: GaussianBlurImageProcessor的所有构造函数改为私有的，只能通过静态方法创建
>* :sparkles: 增加MaskImageProcessor可以给任意形状的PNG图片加上一层遮罩颜色
>* :sparkles: 增加WrappedImageProcessor可以将任意不同的ImageProcessor组合在一起使用

##### ImageSize
>* :zap: `inSampleSize`. 优化inSampleSize计算规则，先根据像素数过滤，然后再根据OpenGL的MAX_TEXTURE_SIZE过滤，最后如果是为大图功能加载预览图的话，当缩小2倍的时为了节省内存考虑还不如缩小4倍（缩小1倍时不会启用大图功能，因此无需处理）
>* :zap: `maxSize`. 默认maxSize改为屏幕的宽高，不再乘以0.75
>* :zap: `resizeByFixedSize` 设置了resizeByFixedSize但是ImageView的宽高没有固定的话就抛出异常
>* :zap: `resize`调用setResize()时不再默认设置resizeByFixedSize为false
>* :zap: `resizeByFixedSize`调用setResizeByFixedSize()时不再默认设置resize为null

##### Request
>* :bug: `Filter repeat` 修复同一内存缓存ID可以出现多个请求同时执行的BUG
>* :bug: `Filter repeat` 修复由于同一内存缓存ID可以出现多个请求，导致会出现同一个内存缓存ID会连续存入多个缓存对象，那么新存入的缓存对象就会挤掉旧的缓存对象，如果旧的缓存对象只有缓存引用，那么旧的缓存对象会被直接回收
>* :bug: 修复显示时遇到已回收的Bitmap崩溃的BUG
>* :bug: 修复读取APK icon时drawable宽高小于等于0崩溃的BUG
>* :hammer: `Listener`. DownloadListener的onCompleted(File cacheFile, boolean isFromNetwork)和onCompleted(byte[] data)合并成一个onCompleted(DownloadResult downloadResult)
>* :hammer: `Listener`. LoadListener的onCompleted(Bitmap bitmap, ImageFrom imageFrom, String mimeType)和onCompleted(GifDrawable gifDrawable, ImageFrom imageFrom, String mimeType)合并成一个onCompleted(LoadResult loadResult)
>* :hammer: `RequestAttrs` 重构RequestAttrs
>* :fire: `memoryCacheId` 删除DisplayHelper的memoryCacheId(String)方法
>* :fire: `Lock` 移除内存缓存编辑锁
>* :art: `Attribute` disableCacheInDisk和disableCacheInMemory属性改名为cacheInDiskDisabled和cacheInMemoryDisabled
>* :zap: `id` 计算ID时，forceUseResize和thumbnailMode依赖resize
>* :zap: 新增FreeRide机制避免重复下载和加载
>* :sparkles: `Attribute` LoadOptions、LoadHelper、DisplayOptions、DisplayHelper增加disableBitmapPool属性
>* :sparkles: 新增[缩略图模式](../wiki/thumbnail_mode.md)，通过缩略图模式你可以在列表中更加清晰的显示那些宽高相差特别大的图片
>* :sparkles: 新增[cacheProcessedImageInDisk](../wiki/cache_processed_image_in_disk.md)属性，为了加快速度，将经过ImageProcessor、resize或thumbnailMode处理过读取时inSampleSize大于等于8的图片保存到磁盘缓存中，下次就直接读取
>* :sparkles: 新增[shapeSize](../wiki/shape_size.md)属性，借助ImageShaper可以在绘制时在修改图片的尺寸

##### GIF
>* :bug: `GIF` 修复当反复切换LoadOptions.decodeGifImage的值后再刷新页面，需要播放GIF时却依然显示静态的GIF第一帧的BUG
>* :bug: onDetachedFromWindow时主动回收GifDrawable

##### SketchImageView
>* :bug: `Gif Flag` 修复SketchImageView.setShowGifFlag()反复调用时无效的bug
>* :bug: `Zoom` 修复设置关闭手势缩放功能时没有恢复Matrix和ScaleType的BUG
>* :bug: `ImageFrom` 修复反复调用setShowImageFrom(boolean)时无效的BUG

##### ExceptionMonitor
>* :hammer: ExceptionMonitor重命名为SketchMonitor并并挪到顶级目录
>* :sparkles: 新增onBitmapRecycledOnDisplay(DisplayRequest, RefDrawable)方法用来监控即将显示时发现Bitmap被回收的问题
>* :sparkles: 新增`onBitmapRecycledOnDisplay(DisplayRequest, RefDrawable)`方法用来监控即将显示时发现Bitmap被回收的问题
>* :sparkles: 新增`onTileSortError(IllegalArgumentException, List<Tile>, boolean)`用来监控分块显示超大图功能碎片排序异常
>* :sparkles: 新增`onBitmapRecycledOnDisplay(DisplayRequest, RefDrawable)`用来监控在即将显示图片之前发现图片被回收了的异常
>* :sparkles: 新增`onInBitmapExceptionForRegionDecoder(String, int, int, Rect, int, Bitmap)`用来监控在BitmapRegionDecoder中使用inBitmap时发生的异常
>* :sparkles: 新增`onInBitmapException(String, int, int imageHeight, int, Bitmap)`用来监控在BitmapFactory中使用inBitmap时发生的异常

##### Log
>* :hammer: `Log` 日志分不同的类型分别提供开关控制，详见[SLogType.java](../../sketch/src/main/java/me/xiaopan/sketch/SLogType.java)
>* :sparkles: `SLogTracker` 新增SLogTracker用于收集Sketch的日志，详情可参看示例APP中的[SampleLogTracker.java](../../sample/src/main/java/me/xiaopan/sketchsample/SampleLogTracker.java)

##### 其它
>* :arrow_up: `minSdkVersion` 最低支持版本升到9
>* :hammer: `Rename`. 所有跟Failed相关的名字全改成了Error

#### 全新功能
>* :sparkles: `Gesture Zoom`. 新增[手势缩放](../wiki/zoom.md)功能，参照PhotoVie，SketchImageView内置了手势缩放功能，比PhotoView功能更强大，体验更好，新增了定位、阅读模式等特色功能
>* :sparkles: `Super Large Image`. 新增[分块显示超大图](../wiki/large_image.md)功能，SketchImageVie内置了分块显示超大图功能，长微博、高清妹子图什么的不再是问题
>* :sparkles: `ImageShaper` 新增[ImageShaper](../wiki/image_shaper.md)，可以在绘制时修改图片的形状，避免同一张图片有不同的形状需求时通过ImageProcessor实现会产生多张图片，从而浪费内存缓存的情况

#### 相对于2.3.0-beta10：
>* :bug: 修复反复开启、关闭超大图功能会导致手势缩放比例异常的BGU
>* :bug: 修复GaussianBlurImageProcessor无法对config为null的bitmap执行模糊的BUG
>* :bug: 修复在Android4.3上取不到webp图片的outMimeType时，无法显示图片的BUG
>* :bug: 修复2.3上RotateImageProcessor崩溃的BUG
>* :lipstick: 优化Sample App
