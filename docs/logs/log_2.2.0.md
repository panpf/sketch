2.2.0版本是一次大的升级，重构了大部分的实现，修复了一些BUG，也改善了一些API的设计。

``但有几个比较大的BUG，建议升级到2.2.1``

``由于改了一些API所以此次无法无缝升级，需要你该些代码``

磁盘缓存:
>* ``优化`` 升级LruDiskCache，内部采用DiskLruCache实现
>* ``修改`` DiskCache接口增加了edit(String)方法，去掉了generateCacheFile(String)、
applyForSpace(long)、setCacheDir(File)、setReserveSize(int)、getReserveSize()、
setMaxSize(int)、saveBitmap(Bitmap, String)方法
>* ``优化`` 旧的缓存文件会自动删除
>* ``优化`` `LruDiskCache兼容多进程，多进程下会采用不同的disk缓存目录，防止多进程持有同一目录造成目录被锁不能使用的问题`
>* ``优化`` 磁盘缓存编辑加同步锁

下载：
>* ``优化`` 下载进度回调方式改为每秒钟一次（之前是每10%一次）
>* ``优化`` 重构ImageDownloader，新增可设置User-Agent、readTimeout和批量添加header
>* ``修改`` ``cacheInDisk改成了disableCacheInDisk``

加载：
>* ``优化`` 默认maxSize由屏幕宽高的1.5倍改为0.75倍，这样可以大幅减少大图的内存占用，但对图片的清晰度影响却较小
>* ``优化`` 默认inSampleSize计算规则优化，targetSize默认放大1.25倍，目的是让原始尺寸跟targetSize较为接近的图片不被缩小直接显示，这样显示效果会比较好
>* ``新增`` 新增ImagePreprocessor可以处理一些特殊的本地文件，然后提取出它们的当中包含的图片，这样Sketch就可以直接显示这些特殊文件中包含的图片了
>* ``新增`` LoadOptions支持设置BitmapConfig，你可以单独定制某张图片的配置
>* ``新增`` LoadOptions支持设置inPreferQualityOverSpeed（也可在Configuration中统一配置），你可以在解码速度和图片质量上自由选择

显示：
>* ``修改`` ``cacheInMemory改成了disableCacheInMemory``

请求：
>* ``新增`` 支持file:///****.jpg
>* ``新增`` Download也支持设置requestLevel
>* ``优化`` 去掉了DisplayHelper上的listener和progressListener设置，你只能通过SketchImageView来设置listener和progressListener了
>* ``优化`` 重构\***Request的实现，简化并统一逻辑处理
>* ``修改`` \***Helper.options(Enum)改为optionsByName(Enum)
>* ``优化`` 调低分发线程的优先级，这样能减少display在主线程的耗时，提高页面的流畅度
>* ``新增`` 支持在debug模式下输出display在主线程部分的耗时
>* ``优化`` 本地任务支持多线程，加快处理速度
>* ``BUG`` 修复在Display的commit阶段显示失败时如果没有配置相应的图片就不设置Drawable而导致页面上显示的还是上一个图片的BUG
>* ``新增`` 支持load()和download()支持同步执行 
>* ``修改`` 方法名progressListener()改为了downloadProgressListener() 

处理：
>* ``新增`` 新增旋转图片处理器RotateImageProcessor
>* ``新增`` RoundedCornerImageProcessor扩展构造函数，支持定义每个角的大小

解码：
>* ``新增`` 支持在debug模式下输出解码耗时
>* ``新增`` 解码支持设置Options.inPreferQualityOverSpeed（通过LoadOptions配置，也可在Configuration中统一配置），你可以在解码速度和图片质量上自由选择

GIF：
>* ``修改`` ``由于显示GIF的场景较少，所以默认不再解码GIF图，在需要解码的地方你可以主动调用 ***Options.setDecodeGifImage(true)或***Helper.decodeGifImage()开启``
>* ``修改`` 删除\***Helper.disableDecodeGif()方法替换为\***Helper.decodeGifImage()
>* ``修改`` 禁止gif图禁止使用内存缓存，因为GifDrawable需要依赖Callback才能播放，如果缓存的话就会出现一个GifDrawable被显示在多个ImageView上的情况，这时候就只有最后一个能正常播放

SketchImageView：
>* ``修改`` SketchImageView.setDisplayOptions(Enum)改名为setOptionsByName(Enum)
>* ``BUG`` 修复使用setImageResource等方法设置图片后在列表中一滑动图片就没了的BUG
>* ``优化`` SketchImageView的ImageShape的圆角角度支持设置每个角的角度
>* ``修改`` SketchImageView的setImageShapeRoundedRadius方法改名为setImageShapeCornerRadius，getImageShapeRoundedRadius方法改名为getImageShapeCornerRadius

其它：
>* ``修改`` 去掉了一些多余的接口设计，例如HelperFactory、ImageSizeCalculator、RequestFactory、
ResizeCalculator、ImageSize、Request、DisplayHelper、LoadHelper、DownloadHelper
>* ``修改`` 所有failure改名为failed
>* ``优化`` apk文件图标的磁盘缓存KEY加上了文件的最后修改时间，这么做是为了避免包路径一样，但是内容已经发生了变化的时候不能及时刷新缓存图标
>* ``新增`` 原生支持通过包名和版本号显示已安装APP的图标，SketchImageView增加displayInstalledAppIcon(String, int)方法、
Sketch增加displayInstalledAppIcon(String, int, ImageViewInterface)和loadInstalledAppIcon(String, int, LoadListener)方法
>* ``优化`` 源码兼容jdk1.6
>* ``优化`` 减少占位图缓存最大容量，调整为最大可用内存的32分之一，但又不能少于2M
>* ``修改`` Sketch.putOptions(RequestOptions)拆分成了Sketch.putDisplayOptions(DisplayOptions)、
Sketch.putLoadOptions(LoadOptions)、Sketch.putDownloadOptions(LoadOptions)
>* ``新增`` 新增ErrorCallback，开发者为通过ErrorCallback接收到解码失败，磁盘缓存安装失败等异常
>* ``修改`` ``读取APK图标时不再过滤默认图标``
>* ``修改`` Configuration中相关参数重命名：
    >* pauseLoad --> globalPauseLoad
    >* pauseDownload --> globalPauseDownload
    >* lowQualityImage --> globalLowQualityImage
    >* cacheInDisk --> globalDisableCacheInDisk
    >* cacheInMemory --> globalDisableCacheInMemory

``注意：本次更新cacheInMemory改成了disableCacheInMemory，cacheInDisk改成了disableCacheInDisk，decodeGifImage的默认值改成了false，所以对这三个属性的调用和设置已定义要重点关注``