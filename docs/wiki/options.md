# 使用 Options 配置图片

Options 用来批量设置如何下载、加载、显示图片，有如下三种：

* [DisplayOptions]：Sketch.display() 方法和 SketchImageView 专用
* [LoadOptions]：Sketch.load() 方法专用
* [DownloadOptions]：Sketch.download() 方法专用

三者的继承关系如下：
[DisplayOptions] `extends` [LoadOptions] `extends` [DownloadOptions]

### Options 的属性

`（'-'代表不支持，非'-'代表支持并且默认值是什么）`

|Attr|DownloadOptions|LoadOptions|DisplayOptions|
|:---|:---|:---|:---|
|requestLevel|NET|NET|NET|
|cacheInDiskDisabled|false|false|false|
|maxSize|-|屏幕的宽高|优先考虑 ImageView 的 layout_width 和 layout_height|
|resize|-|null|null|
|processor|-|null|null|
|decodeGifImage|-|false|false|
|lowQualityImage|-|false|false|
|bitmapConfig|-|null|null|
|inPreferQualityOverSpeed|-|false|false|
|thumbnailMode|-|false|false|
|cacheProcessedImageInDisk|-|false|false|
|bitmapPoolDisabled|-|false|false|
|correctImageOrientationDisabled|-|false|false|
|cacheInMemoryDisabled|-|-|false|
|displayer|-|-|DefaultImageDisplayer|
|loadingImage|-|-|null|
|errorImage|-|-|null|
|pauseDownloadImage|-|-|null|
|imageShaper|-|-|null|
|shapeSize|-|-|null|

详解：

* requestLevel: 指定请求的处理深度，专门用来实现暂停加载和暂停下载功能，更多内容请参考 [RequestLevel]、[列表滑动时暂停加载图片，提升列表滑动流畅度][pause_load]、[移动数据或有流量限制的 WIFI 下暂停下载图片，节省流量][pause_download]
* cacheInDiskDisabled: 禁用磁盘缓存，更多内容请参考 [在磁盘上缓存图片原文件，避免重复下载][disk_cache]
* maxSize: 缩小图片尺寸，读取缩略图，更多内容请参考 [使用 MaxSize 读取合适尺寸的缩略图，节省内存][max_size]
* resize: 修改图片的尺寸，更多内容请参考 [使用 Resize 精确修改图片的尺寸][resize]
* processor: 处理图片，更多内容请参考 [使用 ImageProcessor 在解码后改变图片][image_processor]
* decodeGifImage: 解码 GIF 图片，开启后可播放 GIF 图片，更多内容请参考 [播放 GIF 图片][play_gif_image]
* lowQualityImage: 解码图片时优先使用低质量的 Bitmap.Config，例如 JPEG 图片将使用 ARGB_565 解码，更多内容请参考 [ImageType] 类
* bitmapConfig: 指定解码图片时使用的 Bitmap.Config，KITKAT 以上版本不能使用 ARGB_4444
* inPreferQualityOverSpeed: 设置解码时质量优先，可提高图片质量，但会降低解码速度，当你要频繁的对一张图片进行读取然后写出的时候一定要开启此选项
* thumbnailMode: 开启缩略图模式，能够显示更清晰的缩略图，更多内容请参考 [使用 thumbnailMode 属性显示更清晰的缩略图][thumbnail_mode]
* cacheProcessedImageInDisk: 为了加快速度，将经过复杂处理的图片保存到磁盘缓存中，下次读取后直接使用，更多内容请参考 [使用 cacheProcessedImageInDisk 属性缓存需要复杂处理的图片，提升显示速度][cache_processed_image_in_disk]
* bitmapPoolDisabled: 禁用 BitmapPool，更多内容请参考 [复用 Bitmap 降低 GC 频率，减少卡顿][bitmap_pool]
* correctImageOrientationDisabled: 禁止纠正图片方向，更多内容请参考 [自动纠正图片方向][correct_image_orientation]
* cacheInMemoryDisabled: 禁用内存缓存，更多内容请参考 [在内存中缓存 Bitmap 提升显示速度][memory_cache]
* displayer: 使用专门的显示效果来显示图片，更多内容请参考 [使用 ImageDisplayer 以动画的方式显示图片][image_displayer]
* loadingImage: 设置正在加载时显示的图片
* errorImage: 设置加载失败时显示的图片
* pauseDownloadImage: 设置暂停下载时显示的图片
* imageShaper: 设置图片以什么形状显示，更多内容请参考 [通过 ImageShaper 在绘制时改变图片的形状][imag_shaper]
* shapeSize: 设置图片以多大尺寸显示，更多内容请参考 [通过 ShapeSize 在绘制时改变图片的尺寸][shape_size]

`Options 支持的属性 Helper 中都有对应的方法，只是方法名不一样（没有set）`

### 使用 Options：

Sketch.display()、Sketch.load()、Sketch.download()都会返回其专属的Helper，Helper中也都会有专门的方法配置这些属性，例如：

```java
// 显示
Sketch.with(context).display("http://t.cn/RShdS1f", sketchImageView)
	.loadingImage(R.drawable.image_loading)
	.displayer(new TransitionImageDisplayer())
	...
	.commit();

// 加载
Sketch.with(context).load("http://t.cn/RShdS1f", new LoadListener() {...})
	.maxSize(300, 400)
	.bitmapConfig()
	...
	.commit();

// 下载
Sketch.with(context).download("http://t.cn/RShdS1f", new DownloadListener(){...})
	.disableCacheInDisk()
	...
	.commit();
```

Helper 和 SketchImageView 还都支持批量设置Options

```java
// 批量配置显示属性
DisplayOptions displayOptions = new DisplayOptions();
displayOptions.set***;

Sketch.with(context).display("http://t.cn/RShdS1f", sketchImageView)
	.options(displayOptions)
	.commit();

SketchImageView sketchImageView = ...;
sketchImageView.setOptions(displayOptions);

// 批量配置加载属性
LoadOptions loadOptions = new LoadOptions();
loadOptions.set***;

Sketch.with(context).load("http://t.cn/RShdS1f", new LoadListener() {...})
	.options(loadOptions)
	.commit();

// 批量配置下载属性
DownloadOptions downloadOptions = new DownloadOptions();
downloadOptions.set***;

Sketch.with(context).download("http://t.cn/RShdS1f", new DownloadListener(){...})
	.options(downloadOptions)
	.commit();
```

``使用 options() 方法和使用属性专用方法设置同一个属性时并不会冲突，就看谁最后执行``

[RequestLevel]: ../../sketch/src/main/java/com/xiaopan/sketch/request/RequestLevel.java
[load_android_download]: load_android_download.md
[disk_cache]: disk_cache.md
[memory_cache]: memory_cache.md
[thumbnail_mode]: thumbnail_mode.md
[ImageType]: ../../sketch/src/main/java/com/xiaopan/sketch/decode/ImageType.java
[max_size]: max_size.md
[resize]: resize.md
[image_processor]: image_processor.md
[play_gif_image]: play_gif_image.md
[cache_processed_image_in_disk]: cache_processed_image_in_disk.md.md
[bitmap_pool]: bitmap_pool.md
[correct_image_orientation]: correct_image_orientation.md.md
[image_displayer]: image_displayer.md
[imag_shaper]: imag_shaper.md
[shape_size]: shape_size.md
[pause_load]: pause_load.md
[pause_download]: pause_download.md
[DownloadOptions]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DownloadOptions.java
[LoadOptions]: ../../sketch/src/main/java/me/xiaopan/sketch/request/LoadOptions.java
[DisplayOptions]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DisplayOptions.java
