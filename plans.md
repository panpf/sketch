待办池：
* 考虑支持不同的应用场景，例如background，drawableTop等
* 支持OkHttp
* 解码缓存文件失败的时候要再次下载
* 2.3以上不再主动回收图片了
* 支持vector
* 4.0以下下webp转png再使用，可以解决很多问题（超大图功能对WebP的限制需要改一下）
* 支持一次设置两张图地址 分大图小图 WiFi下可直接大图
* 再次考虑Drawable.setVisible
* 4.3模拟器上模糊失效
* 长图只读取部分区域，参考百思不得姐
* 参考高并发对象池，优化sketch里的同步锁与对象池
* 加上面部识别功能，居中显示部分时可以以脸部为中心
* demo上下滑动关闭功能
* 将所有日志全部抽离出去搞成一个类
* 加一些常用的xml属性
* 集成Oblique，支持倾斜图片显示
* 重新设计ImageShaper，感觉现在的很垃圾，限制很大

其它：
* 参考Glide、Fresco和Picasso
* 写一个系列博客介绍Sketch，也算是做一个总结
* 用一下其它的几款图片加载器，例如UIL、fresco、glide、picasso对比一下，特别是对RecyclerView的兼容性，然后写博客
* 搞一个悬浮窗，用来显示所有加载任务，这样是不是很酷炫呢，每个任务还可以显示走到哪一步了

完成：
* 支持读取本地APK文件的图标（已安装APP的图标也可以通过其本地APK文件的方式读取其图标）
* 示例APP增加一个页面，展示读取已安装APP或本地APK文件的图标的功能。页面分两部分，分别显示已安装APP列表和扫描到的本地APK包列表
* 修复首页竖的默认图貌似裁剪跑偏的问题
* 去掉RequestFuture，fire()方法直接返回Request，这样就少创建一个对象了
* 新增加载级别，并将加载级别参数应用到每个请求中，MEMORY表示只从内存加载；LOCAL：表示只从没存和本地加载；NETWORK：表示最终还会从网络加载，这样就能实现移动网络下不从网络加载图片的功能，另外看这样能否替代暂停功能的实现
* 增加在移动网络下不加载网络图片的功能
* SketchImageView支持在移动网络下不加载图片的时候支持手动点击加载功能（是否要新增加一个可以提示“点击加载”的默认图片，同loadingDrawable是同级的）
* 改善框架实现，便于使用者自定义整个流程中的每个环节
* 去掉各种toStatus方法，去掉DEFAULT_ENABLE_DISK_CACHE
* 检查下拆分后的各方法
* 组织下拆分后的字段和方法
* SketchImageView支持点击加载已经搞的差不多了，还差在displayRequest和displayOptions中增加一个clickLoadDrawable并在适当的时候显示这张图片
* 将MobileNetworkPauseDownloadManager纳入Sketch
* 将所有PauseDownloadNewImage改为PauseDownload PauseLoadNewImage也一样
* 回收利用request
* 立马着手搞定重复利用request的功能，因为失败重试以及暂停下载和onAttachedToWindow()里的恢复图片都要用
* 获取缓存文件的时候要考虑其它的目录，这样就可以解决一些问题，比如在未插入SD卡之前缓存文件是放在系统空间里的，插入sd卡之后缓存目录就变成了sd卡，那么查找缓存文件的时候也要到系统缓存去看看
* getCacheFile()如果没有的话就直接返回null
* display请求在加载之前要先从内存缓存中查找一下
* display、load和download的listener都在主线程执行
* 支持设置缓存ID
* 修改包名为me.xiaopan.sketch，其它项目也要照办
* 取消Request之间的继承关系
* 示例APP增加暂停下载时点击重新加载图片和失败时点击重新加载图片选项
* 还要将waitDisplayRefCount改为int，因为执行加载前要从内存中判断下是否已经有了，有了的话就要设置等待显示，这时候如果多个共用的话就得用int计数了
* display请求执行加载之前先去判断内存中是否已经存在了符合要求的图片，有的话取出来计数加一
* 返回图片mimeType，帮助判断是什么类型的图片
* SketchImageView支持在右下角根据mimeType显示gif角标
* load和download也要支持requestLevel
* 等待显示计数不能放在setIsDisplayed方法中执行了，因为如果没有使用SketchImageView就不会调用setIsDisplayed方法，这样的话就惨了
* RecycleGifDrawable不能使用TransitionImageDisplayer
* 考虑项目中没有集成android-gif-drawable的时候 碰到Gif图该怎么处理，原则上是初始化的时候通过反射看能否拿到GifDrawable拿不到就说明没有，然后就不处理
* SketchImageView的getImageShapeClipPath方法圆角矩形还没有处理呢
* SketchImageView增加单独的DisplayOptions，这样也方便在应用模板后拿到getDisplayOptions直接修改，这样进一步增强了灵活性
* 现在直接集成了gif-drawable，但还要判断没有lib pl_droidsonroids_gif.so的时候改怎么处理
* 解码apk时，先将apk图片存到本地缓存区然后从本地读
* DiskCache默认设为100m
* 自定义一个BitmapDrawable，增加srcRect功能，这样就可以随意实现过渡效果了
* 为了解决recycleView不遵守约定在onAttachedWindow之前执行getView方法，就在onDetachedWindow的时候设置个标记意思是尚未调用display方法，然后在display方法里将这个标记设为一设置，然后在onAttachedWindow里检查这个标记如果尚未设置就企图使用旧的请求加载，这样貌似就可以在onAttachedWindow的时候设置drawable为null了
* 当请求取消的时候那些滞留在内存中的bitmap该怎么办，目前load过程中的都已在取消时回收
* 还要更加精细化request的运行状态，比如在取消时检查当前的状态，如果合适的话就回收bitmap
* 搞一个高斯模糊的图片作为背景
* 搜索的默认关键词改为gif色图
* gif drawable 不能使用过渡显示器
* 直接集成GIFDrawable到sketch中
* 当bitmap创建之后遇到的各种取消，并且没有放到内存缓存之前应该主动释放Bitmap（在解码完后发现取消了就回收Bitmap）
* 我擦imageHolder处理完了之后原图不能回收啊
* 增加一个接口，将sketchImageView的特别功能抽离出来，方便别人自定义，最重要的是在display之前加一个初始化的方法，方便初始化一些在getView中频繁设置的属性
* （已经搞成瀑布流了）将搜索列表改为RecyclerView
* 去掉android.support.annonations
* lruCache换成string，drawable这样就能放各种drawable了
* 再次思考是否能用drawable的bounds解决transition变形问题
* 明星个人主页要用瀑布流（上百度搜索下瀑布流怎么搞，有没有好的实现方式）StaggeredGridLayoutManager.setGapStrategy(GAP_HANDLING_NONE)貌似就不会换位置了
* 修改所有日志总字符串的写法，写一个公共的专门连接字符串的方法
* 要不要名字改为Sketch
* SketchImageView的点击涟漪效果，快速点按的时候应该快速消失，并且在2.3版本上涟漪效果失效
* 是否不再默认从ImageView上解析resize并且对图片进行裁剪，以前这样做的目的是为了解决图片变形问题，现在呢？貌似不需要了
* 支持设置bitmap.config
* 将bitmapConfig，改成低质量的图片
* 示例APP增加修改Bitmap.Config，界面改了一点儿，其它都没动
* 有些明确需要透明效果的处理器不能跟着bitmapConfig走
* 不再提供bitmapConfig设置，改由自动根据mimeType来选择合适的config，这样更加智能
* 低版本支持WEBP
* （低版本不再支持WEBP了，因为WEBP库不支持inSampleSize）已经实现了低版本支持了WEBP，但是目前DefaultImageDecoder代码逻辑还比较混乱，需要再优化以下
* 优化ImageFragment中图片显示失败时的提示方式，现在显示失败图片太二了
* SrcBitmapDrawable改名为SketchBitmapDrawable
* 把SketchImageView中跟ImageView相关的方法都在interface中也定义一份，这样Sketch中就不需要明确引用ImageView，也更方便别人自定义
* 找个地方给图片给改成圆角的，目的是为了展示圆角的效果
* BindBitmapDrawable的setFixedSize不应该转交给recycleDrawable处理，因为每一个都士不一样的，而RecycleDrawable却是一样的，因此比较纠结
* 对loadingDrawable提供内存缓存支持，默认缓存容量为可用最大内存的十六分之一，现在你可以放心的对占位图进行各种处理了
* CommentUtils重命名为SketchUtils
* sample中关于页面信息需要更新
* readme.me增加最低版本说明
* 初始化Sketch的加入输出版本信息，例如Sketch，2.0.0， 200， release
* 是否要对ImageHolder加入同步，貌似还是很有必要的，例如失败图片是在异步线程或主线程都会同时调用的，加同步还是必须的，实现方案是，ImageHolder搞成接口，例如换成PlaceholderImage接口，然后搞三种继承LoadingPlaceholderImage，FailureImage，PauseDownloadImage，然后由于后两者会在异步线程中调用，因此只给后两者加同步
* 用百度云测试一下
* readme增加感谢android-gif-drawable说明
* 增加forceUseResize参数
* 低质量图片参数改名 翻转过来
* HintView里的失败图标和按钮以及下拉帅新的箭头都是灰色的或者样式不符合要求
* enableMemoryCache改名为cacheInMemory，还有enableDiskCache
* 根据版本决定使用httpURL还是httpclient
* 解码部分捕获oom异常，避免直接崩溃
* 计算inSampleSize有bug，在一桶水中已修复
* injector有新版本，需要更新
* 貌似不应该直接缓存带有fixedSize的对象，用fixed draw able包装Re cycle draw able
* 在onAttachRoWindow恢复的时候没有重新读取ScaleType，导致使用了旧任务的ScaleType导致崩溃
* （2016-04-05）改用diskLruCache
* （2016-04-05）进度更新改为每秒钟一次
* （2016-04-06）还要在解决一下apk icon 被缩小的问题
* （2016-04-06，采用apk路径加最后修改时间即可满足需求）突然想到由于目前apk icon有本地缓存，那么重新安装app的时候如果图标变化了是否能识别到并及时刷新缓存
* （2016-04-06）是RecycleDrawable在onDetachedFromWindow的时候才需要setDrawable为null
* （2016-04-24）缓存文件的删除方式有问题，搞一个DiskCacheItem，里面包含文件以及key，删除的时候要用key
* （2016-04-24）Sketch会自动从SketchImageVie身上去listener，那要是listener被重新设置了呢？（DisplayHelper上的listener去掉了）
* （2016-04-25）定义一个统一的Identifier接口
* （2016-04-25）所有参数配置封装成一个类，这样helper和request就可以共用一个参数类了，还能减少代码量。这样参数类得提供一个计算出KEY的方法，KEY加上Uri就是图片的内存缓存ID，完美
* （2016-04-26）将基础属性封装成一个单独的类，将运行中状态封装成一个单独的类，将下载结果、加载结果封装成一个单独的类，将display特有的属性封装成一个单独的类
* （2016-04-26）线程间跳转的是不是可以封装一下（已封装在了SketchRequest中）
* （2016-04-26）还有在主线程中调用的是不是也可以封装一下（已封装在了SketchRequest中）
* （2016-04-27）试着让DisplayRequest继承自LoadRequest，这样加载部分的代码就简单多了
* （2016-04-27）Sketch.putOptions(RequestOptions)改成了Sketch.putDisplayOptions(DisplayOptions)...
* （2016-04-27）兼容file://（直接改成了每种类型可以有两种协议类型，这样还顺便把http://和https://融合成了一种）
* （2016-04-27）SketchImageView.setDisplayOptions(Enum)改为setOptionsByName(Enum) ...Helper.options(Enum)改为optionsByName(Enum)
* （2016-04-28）当使用setResource设置了新的图片的时候，那么displayParams是不是该清除了，确保在onAttch的时候不被恢复
* （2016-04-28）TransitionImageDisplayer异常的时候说明是哪个没设置对
* （2016-04-28）修复DiskLruCache的removeBUG，当要删除的缓存文件已经不存在了就会remove失败，解决办法就是先验证文件是否存在
* （2016-05-02）拆分SketchImageView
* （2016-05-03）重组目录结构
* （2016-05-04）新增旋转图片处理器RotateImageProcessor
* （2016-05-05）默认不再解码GIF图
* （2016-05-07）当暂停下载时尽可能在commit的时候就判断如果没有磁盘缓存就结束，这样体验会更好
* （2016-05-08）LruDiskCache添加一个判断是否存在的方法，这样DisplayHelper用的时候能少创建几个对象
* （2016-05-08）统一使用getDiskCacheKey访问磁盘缓存
* （2016-05-08）listener回调在commit也要检查是不是在主线程不再的话就放到主线程执行
* （2016-05-08）本地图片就不再显示进度蒙层了
* （2016-05-09）diskLruCache abort和commit的时候会抛出运行时异常，捕获一下
* （2016-05-09）提高DiskLruCache的遇错自我恢复能力
* （2016-05-09）LruDiskCache兼容多进程
* （2016-05-09）优化分发效率以及支持输出display在主线程的耗时
* （2016-05-12）减少占位图缓存最大容量，调整为最大可用内存的32分之一，但又不能少于2M
* （2016-05-11）各种异常情况如果没有配置相应的占位image就设置drawable为空，这是之前的BUG
* （2016-05-11）缓存文件的名称不能再用uri.encode来加密了，要用MD5（不必了之前有这样的考虑是因为可能会因为后缀可能为.apk而导致被误识别，但是DiskLruCache会在最后加上序号就没这问题了）
* （2016-05-16）本地任务支持多线程，加快处理速度
* （2016-05-24）重构ImageDownloader,新增可设置User-Agent、readTimeout和批量添加header
* （2016-05-24）采用httpStack模式 改进imageDownloader
* （2016-05-26）不再用DisplayHelper中的options覆盖ImageView的options,这是之前重构DisplayHelper时候改出来的BUG,现在依然要放到DisplayParams中去
* （2016-05-29）支持设置inPreferQualityOverSpeed
* （2016-05-30）LocalImagePreprocessor rename to ImagePreprocessor
* （2016-05-31）Sketch增加displayInstalledAppIcon(String, int, ImageViewInterface)和loadInstalledAppIcon(String, int, LoadListener)方法
* （2016-05-31）DefaultImageProcessor rename to ResizeImageProcessor
* （2016-05-31）SketchImageView的ImageShape的圆角角度支持更详细的定义，并且setImageShapeRoundedRadius方法改名为setImageShapeCornerRadius，getImageShapeRoundedRadius方法改名为getImageShapeCornerRadius
* （2016-06-01）新增ErrorCallback可让使用者接收解码失败、安装磁盘缓存失败等异常
* （2016-06-01）禁止GifDrawable使用内存缓存，因为GifDrawable需要依赖Callback才能播放， 如果缓存的话就会出现一个GifDrawable被显示在多个ImageView上的情况，这时候就只有最后一个能正常播放
* （2016-06-01）磁盘缓存编辑加同步锁
* （2016-06-02）读取APK图标时不再过滤默认图标
* （2016-06-03）cacheInDisk to disableCacheInDisk, cacheInMemory to disableCacheInMemory
* （2016-06-04）Configuration中lowQualityImage rename to globalLowQualityImage
* （2016-06-04）支持同步加载和下载
* （2016-06-04）progressListener改名为downloadProgressListener
* （2016-08-9）试图解决硬件加速时对图片尺寸的限制，OpenGLRenderer: Bitmap too large to be uploaded into a texture (440x4540, max=4096x4096)
* （2016-08-16）onDisplay的执行时机提前
* （2016-10-15）图片异常变小似乎是由于缓存的drawable在其它地方被使用了，然后bounds被改变了导致的，想办法改变缓存的bitmap，使其仅仅具备引用信息，初步设想是搞一个引用bitmap包装bitmap记录引用信息，缓存的时候也是缓存引用bitmap，然后recycledrawable支持引用bitmap
* （2016-10-18）旋转处理器如果旋转角度为0或360就不往ID上加标识了
* （2016-10-18）另外碰见要显示100x100 但图片是1000x2000的，如果硬是缩小然后直接显示则可能很模糊，支持处理一下，只读取中间部分，这个功能就是是否允许生成缩略图
* （2016-10-18）支持显示超大图，候选者有largeImage，subSamplingScaleImageView，搞一个LargeImageDrawable
* （2016-10-18）参考v7包里的DrawableWrapper改造BindDrawable，这样loadingImage就可以使用各种图片了
* （2016-10-18，已经支持了）用纯色定义的图片作为默认图的时候会发生什么情况呢？
* （2016-10-18，已经支持了）loading支持bitmap
* （2016-10-21）考虑如何支持用已缓存的小缩略图作为默认图片（比如支持从内存缓存中加载默认图）
* （2016-10-21）如果同时有三张大图同时工作的话，可能会比较耗内存，因此提供一些回收和恢复的方法，方便在Fragment显示或隐藏的时候主动控制
* （2016-10-25）利用BitmapShader实现各种形状的图片
* （2016-10-28）支持缓存处理过的图片，这样下次读取就很快了
* （2016-10-30）加一些具体的使用场景的文档，比如如何让不规则的图片也能使用TransitionDrawable，如何清晰的显示超大图片的缩略图，如何在图片详情页先显示较小的图片，在显示清晰的图片
* （2017-03-21）demo侧滑菜单背景异常
* （2017-03-21）demo详情页不可见时暂停播放失效了
* （2017-03-23）缓存文件的名称改用MD5编码，因为已经出现了文件名字过长导致文件无法访问的问题
* （2017-05-10）DecoderHelper改造一下，只返回InputStream，具体的交由Decoder解决
* （2017-05-10）解码结果改成一个接口，然后有普通bitmap和gif drawable两种实现，支不支持ImageProcessor、创建Drawable或后续该怎么处理交由接口控制
* （2017-05-10）改造ImageDecoder，改成责任链模式，例如先准备好一些基础属性，如宽、高、类型、旋转角度、然后将gif、缩略图、普通的等等改成一个个的责任链
* （2017-05-10）改善架构，特别是Request里，强化DataSource的功能，数据来源、处理结果，这些概念要分清楚
* （2017-05-10）demo长按出选项，点击关闭
* （2017-05-10）将一个功能的所有处理代码都放到一个类中，例如缓存已处理图片的功能
* （2017-05-10）通过ExifInterface读取图片方向信息，实现自动旋转方向不正的图片，新的ExifInterface支持库 http://developers.googleblog.cn/2017/01/exifinterface.html compile "com.android.support:exifinterface:25.1.0"
* （2017-05-24）支持Base64格式的图片
* （2017-05-27）gif点击播放
* （2017-05-30）集成unsplash的图片资源

不必了：
* （没有必要，因为是BitmapDrawable的话返回的就是其自己，所以没有必要重新绘制）调用drawableToBitmap后drawable是否有必要发现是bitmapDrawable立即释放）
* （不必，验证HashCode后发现每次都是不一样的）如果从本地apk文件中读取的icon是否缓存（例如多次读取到的是同一张图片），那么就有必要重新绘制一张，然后回收旧的，因为我要保证每一张都是独立的不一样的
* （胡搞）现在需要在onDetachedFromWindow的时候主动取消加载，在notifyDrawable方法中加入对BindDrawable的处理，在BingDrawable中取消请求，这样就不必考弱引用来维护关系了
* （不这么搞了）在Sketch中加一个参数，意思就是是否开启暂停加载的功能，然后所有的PauseLoadFor类都判断次参数
* （不靠谱）改AsyncDrawable为RecycleDrawable形式
* （经测试会自定停止但尚不知是怎么做到的）GifDrawable是否有在隐藏的时候停止播放的功能，有的话是否需要ImageView配合
* （已经采取了新的方式实现，不再需要处理图片了）对loading drawable处理的时候加上判断尺寸比例是否一样，一样的话就不处理了
* （不需要已采用新方案实现）考虑是否要去除对loadingDrawable的处理
* （ClipPath有明显的锯齿，并且无法解决，因此不能使用）SketchImageView支持ClipPath
* （ClipPath有明显的锯齿，并且无法解决，因此不能使用）SketchBitmapDrawable支持ClipPath
* （不必了，之前加这个参数主要为了解决圆角图片的问题，现在在圆角处理器中加入了这个功能，解决了这个问题）resize加一个参数，一定要拿到的图跟resize尺寸一样
* （不用考虑了）考虑如何处理下载的垃圾文件，申请空间的时候清理文件要处理那些下载垃圾
* （已通过一个scheme可以有多个值的方式解决了问题）改善scheme的设计模式，试图支持自定义scheme以及处理方式
* （不搞了）displayHelper根据内存缓存ID过滤一下相同的请求
* （本就是主线程）考虑是否把Bitmap往缓存里放的时机放到主线程来搞
* （这样只会越来越复杂）考虑对宽高固定的ImageView和普通图片使用像GlideDrawable一样的方式实现，这样可能会更顺畅一点
* （没必要，这样的话会产生很多临时文件，扰乱磁盘缓存系统）支持断点续传