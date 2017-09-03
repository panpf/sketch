# Options & Helper

* Options：用于批量设置请求属性
* Helper： 用于设置请求属性并提交请求

Sketch 的 display()、load()、download() 方法（[参考 load_android_download.md]）都有对应的 Options 和 Helper，如下：

|Type|Options|Helper|
|:---|:---|:---|
|Sketch.display()|DisplayOptions|DisplayHelper|
|Sketch.load()|LoadOptions|LoadHelper|
|Sketch.download()|DownloadOptions|DownloadHelper|

### Options 支持属性：

`（'-'代表不支持，非'-'代表支持并且默认值是什么）`

|Attr|DownloadOptions|LoadOptions|DisplayOptions|
|:---|:---|:---|:---|
|requestLevel|NET|NET|NET|
|cacheInDiskDisabled|false|false|false|
|maxSize|-|屏幕的宽高|优先考虑ImageView的layout_width和layout_height|
|resize|-|null|null|
|forceUseResize|-|false|false|
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

* requestLevel: 指定请求的处理深度-[参考 RequestLevel]
* cacheInDiskDisabled: 禁用磁盘缓存-[参考 disk_cache.md]
* maxSize: 限制读取到内存的图片的尺寸-[参考 max_size.md]
* resize: 修改图片的尺寸-[参考 resize.md]
* forceUseResize: 强制使用 resize-[参考 resize.md]
* processor: 处理图片-[参考 process_image.md]
* decodeGifImage: 解码 GIF 图片，开启后可播放 GIF 图片-[参考 display_gif_image.md]
* lowQualityImage: 解码图片时优先使用低质量的 Bitmap.Config，例如 JPEG 图片将使用 ARGB_565 解码，具体请查看 [ImageType] 类
* bitmapConfig: 强制指定解码图片时使用的 Bitmap.Config，KITKAT 以上版本不能使用 ARGB_4444
* inPreferQualityOverSpeed: 解码时质量优先，可提高图片质量，但会降低解码速度，当你要频繁的对一张图片进行读取然后写出的时候一定要设置优先考虑质量
* thumbnailMode: 开启缩略图模式-[参考 thumbnail_mode.md]
* cacheProcessedImageInDisk: 为了加快速度，将经过复杂出路的图片保存到磁盘缓存中，下次就直接读取-[参考 cache_processed_image_in_disk.md.md]
* bitmapPoolDisabled: 禁用BitmapPool-[参考 bitmap_pool.md]
* correctImageOrientationDisabled: 禁用纠正图片方向功能-[参考 correct_image_orientation.md.md]
* cacheInMemoryDisabled: 禁用内存缓存
* displayer: 使用专门的显示效果来显示图片-[参考 displayer.md]
* loadingImage: 设置正在加载时显示的图片
* errorImage: 设置加载失败时显示的图片
* pauseDownloadImage: 设置暂停下载时显示的图片
* imageShaper: 设置图片以什么形状显示-[参考 imag_shaper.md]
* shapeSize: 设置图片以多大尺寸显示-[参考 shape_size.md]

Options 支持的属性 Helper 中都有对应的方法，只是方法名不一样（没有set）

#### 使用Options：

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

各大Helper和SketchImageView还都支持批量设置Options

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

``使用options方法和使用专用方法设置同一个属性时并不会冲突，就看谁最后执行``

[参考 RequestLevel]: ../../sketch/src/main/java/com/xiaopan/sketch/request/RequestLevel.java
[参考 load_android_download.md]: load_android_download.md
[参考 disk_cache.md]: disk_cache.md
[参考 thumbnail_mode.md]: thumbnail_mode.md
[ImageType]: ../../sketch/src/main/java/com/xiaopan/sketch/decode/ImageType.java
[参考 max_size.md]: max_size.md
[参考 resize.md]: resize.md
[参考 process_image.md]: process_image.md
[参考 display_gif_image.md]: display_gif_image.md
[参考 cache_processed_image_in_disk.md.md]: cache_processed_image_in_disk.md.md
[参考 bitmap_pool.md]: bitmap_pool.md
[参考 correct_image_orientation.md.md]: correct_image_orientation.md.md
[参考 displayer.md]: displayer.md
[参考 imag_shaper.md]: imag_shaper.md
[参考 shape_size.md]: shape_size.md
