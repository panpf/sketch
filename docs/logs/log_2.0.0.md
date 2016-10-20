本次更新日志：
**download**
>* ``优化`` 默认连接超时时间改为20秒

**process**
>* ``修复`` 新增ResizeCalculator来计算resize以及srcRect、destRect
>* ``新增`` 新增GaussianBlurImageProcessor，能够对图片进行高斯模糊并变暗处理并可自定义模糊半径

**display**
>* ``优化`` TransitionImageDisplayer会忽略RecycleGifDrawable
>* ``优化`` TransitionImageDisplayer在display的时候，如果ImageView没有旧的drawable就用一张透明的图片代替
>* ``新增`` TransitionImageDisplayer、ColorTransitionImageDisplayer、ZoomInImageDisplayer、ZommOutImageDisplayer等增加了一些get、set方法

**cache**
>* ``优化`` 磁盘缓存最大容量默认为100M
>* ``新增`` MemoryCache增加getSize()、getMaxSize()方法
>* ``新增`` DiskCache增加getSize()、getMaxSize()、getReserveSize()方法
>* ``修改`` DiskCache.setDiskCacheDir()方法重命名为DiskCache.setCacheDir()
>* ``修改`` DiskCache删除getCacheFileByUri()方法
>* ``修改`` DiskCache.getCacheFile(Download)方法修改为getCacheFile(String)，并且没有缓存文件的话就返回null
>* ``优化`` 获取缓存文件的时候考虑其它的目录，这样就可以解决一些问题，比如在未插入SD卡之前缓存文件是放在系统空间里的，插入sd卡之后缓存目录就变成了sd卡，那么查找缓存文件的时候也要到系统缓存去看看

**decode**
>* ``新增`` 支持读取本地APK文件的图标，已安装APP的图标也可以通过其本地APK文件的方式读取其图标。另外读取apk图标时会先将apk图标存到磁盘缓存区然后从本地读
>* ``新增`` 支持解码GIF图片，因此ImageDecoder.decode(LoadRequest)方法的返回值类型从bitmap变成了Object

**SketchImageView**
>* ``新增`` setDebugMode(true)方法改名为setShowFromFlag(true)
>* ``优化`` FromFlag新增DISK_CACHE，现在就有四种了，分别是MEMORY_CACHE（绿色）、DISK_CACHE（黄色）、LOCAL（蓝色）、NETWORK（红色）
>* ``修改`` setImageFrom()系列方法改名为displayImage()
>* ``新增`` 支持在暂停下载的时候通过手动点击下载当前图片，只需执行setClickDisplayOnPauseDownload(true)开启此功能即可
>* ``新增`` 支持失败的时候手动点击重新显示图片，setClickRetryOnError(true)开启此功能即可
>* ``修复`` 修复之前为了解决兼容RecyclerView，而取消了在onDetachedFromWindow中设置drawable为null的操作，从而导致在setImageDrawable的时候如果当前要设置的Drawable跟旧的是同一张，就会立马引用计数归零，最终导致图片被回收而引发崩溃的BUG
>* ``优化`` 采用新的方式完美兼容RecyclerView
>* ``新增`` 增加单独的DisplayOptions，支持getDisplayOptions()后单独修改属性，进一步增强了灵活性
>* ``新增`` 新增ImageShape属性，用来指定图片的类型，以便在绘制按下状态和下载进度的时候和图片的形状保持一致，达到最佳的显示效果
>* ``新增`` 集成GifImageView的功能
>* ``新增`` 新增Gif角标功能。通过setGifFlagDrawable(Drawable)设置角标图片即可，然后当发现是GIF图片就会在右下角显示指定的角标图片
>* ``新增`` 新增SketchImageViewInterface接口，将Sketch中用到的方法抽离出来，这样你也可以实现此接口来自定义一个SketchImageView
>* ``新增`` 新增onDisplay()方法，现在你可以继承SketchImageView重写onDisplay()方法，在这里设置一些在getView()方法中需要反复设置的通用属性，具体可以参考示例APP中的MyImageView

**Sketch**
>* ``修改`` 改名为Sketch
>* ``修改`` 修改包名为me.xiaopan.sketch
>* ``修改`` "assets://"协议改为"asset://"
>* ``修改`` "file:///test.png"类型的uri改为"/test.png"，去掉了前面的"file://"
>* ``新增`` 支持读取APK文件的ICON，只需APK文件的路径即可，例如/sdcard/test.apk，如果需要显示已安装APP的图标的话就通过PackageManager拿到其APK文件路径即可
>* ``修改`` 去掉RequestFuture，commit()方法直接返回Request，这样就少创建一个对象了
>* ``优化`` 优化处理流程，当请求取消的时候及时的回收bitmap
>* ``修改`` FailureCause重命名为FailedCause
>* ``新增`` 所有请求新增RequestLevel属性，可决定请求的处理深度，MEMORY表示只从内存加载；LOCAL：表示只从内存和本地加载；NETWORK：表示最终还会从网络加载
>* ``新增`` 结合RequestLevel新增暂停下载图片功能，通过Sketch.getConfiguration().setPauseDownload(boolean)设置即可
>* ``新增`` 在暂停下载功能的基础上提供移动网络下暂停下载图片功能，通过Sketch.getConfiguration().setMobileNetworkPauseDownload(boolean)开启即可
>* ``修改`` Sketch.pause()和Sketch.resume()修改为Sketch.getConfiguration().setPauseLoad(boolean)
>* ``优化`` Helper和Request完全接口化，现在你可以通过自定义Helper和Request来修改整个流程
>* ``优化`` display请求在加载之前会先从内存缓存中查找一下是否有相同ID的图片
>* ``修改`` LoadRequest和DownloadRequest的listener也在主线程执行
>* ``新增`` 集成了android-gif-drawable，支持gif图
>* ``新增`` Configuration新增setDecodeGifImage(true)属性，可全局控制是否解码GIF图片
>* ``新增`` LoadHelper和DisplayHelper新增disableDecodeGifImage()方法可对单次请求控制不解码GIF图片
>* ``新增`` LoadOptions和DisplayOptions新增setDecodeGifImage(boolean)方法可控制是否解码GIF图片
>* ``修改`` DownloadOptions、LoadO以及RequestOptions所有设置相关的方法的名字都改为了以set开头
>* ``新增`` RecycleDrawableInterface新增getMimeType()方法可查看图片类型
>* ``修改`` LoadListener和DisplayListener的onCompleted()方法新增String mimeType参数，返回图片类型
>* ``修改`` LoadListener.onCompleted()方法的第一个参数由Bitmap改为Drawable，因为可能会返回RecycleGifDrawable
>* ``修改`` 新增了一个自定义的FixedSizeBitmapDrawable来解决比例不一致的两张图片使用TransitionDrawable时变形的问题
>* ``优化`` 改善框架实现，便于使用者自定义整个流程中的每个环节
>* ``优化`` 取消Request之间的继承关系
>* ``新增`` DisplayHelper支持设置缓存ID
>* ``修改`` DrawableHolder升级为ImageHolder
>* ``修改`` 不再从ImageView上解析resize，只有你调用了resizeByFixedSize()才会从ImageView上解析resize
>* ``修改`` 当你使用了TransitionImageDisplay并且ImageView的布局尺寸是固定的以及ScaleType是CENTER_CROP的时候会自动开启FixedSizeBitmapDrawable的fixedSize功能，保证在使用TransitionDrawable显示图片的时候不会变形
>* ``修改`` MaxSize调整为在commit的时候检查，如果为null就设置为默认的MaxSize，默认的是屏幕宽高的1.5倍
>* ``新增`` 根据图片MimeType自动选择最合适的Bitmap.Config
>* ``新增`` 支持设置取低质量的图片
>* ``新增`` 对经过ImageProcessor处理的loadingImage、failureImage、pauseDownloadImage提供内存缓存支持，默认缓存容量为可用最大内存的十六分之一，现在你可以放心的对占位图进行各种处理了
>* ``新增`` 增加forceUseResize选项

sample app：
>* ``新增`` 在设置中增加多种开关，包括“显示下载进度”、“移动网络下不下载新图片”、“列表滑动时不加载新图片”等
>* ``新增`` 搜索页和明星个人主页使用瀑布流展示
>* ``新增`` 增加一个页面，展示读取已安装APP或本地APK文件的图标的功能。页面分两部分，分别显示已安装APP列表和扫描到的本地APK包列表
>* ``新增`` 搞一个高斯模糊的图片作为背景
>* ``新增`` 搜索的默认关键词改为gif
>* ``新增`` 本地相册页面支持显示的本地的GIF图
>* ``修改`` 本地相册页面图片改成圆角的

WIKI更新：
>* 将所有assets:// 改为asset://
>* wiki中所有涉及到SketchImageView.setImageFor的方法名都要吸怪为display***
>* Helper和Request完全接口化，现在你可以通过自定义Helper和Request来修改整个流程
>* 新增暂停下载新图片功能，并提供一句话实现移动网络下暂停下载图片，并结合SketchImageView实现暂停时点击下载指定图片
>* SketchImageView的setDebugMode更新了
>* 更新所有关于LoadRequest和DownloadRequest的listener执行位置的说明
>* 要着重介绍SketchImageView可以getDisplayOptions直接修改某项属性
>* SketchImageView增加了ImageShape，可以对按下效果和进度加以限制和图片的形状更加吻合
>* 增加说明最低兼容API-V7
>* 支持RecyclerView，由于RecyclerView的特性，往回滚的时候会检测已回收的ItemView和数据是否一致，一致的话说明无需重新getView，直接使用即可，但是Sketch的处理流程为在getView的设置新的图片，在onDewindow的时候回收Bitmap。这就与RecyclerView的特性冲突了，因此SketchImageView配合RecyclerView增加了恢复的特性
>* 更新示例APP，以及截图