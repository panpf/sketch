Sketch共有DisplayOptions `extends` LoadOptions `extends` DownloadOptions三种选项，分别对应display()、load()、download()三个方法

支持属性如下（'-'代表不支持，非'-'代表支持并且默认值是什么）：

|属性|DownloadOptions|LoadOptions|DisplayOptions|
|:---|:---|:---|:---|
|sync|false|false|-|
|requestLevel|NET|NET|NET|
|listener|null|null|null|
|downloadProgressListener|null|null|null|
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
|resizeByFixedSize|-|-|false|
|imageShaper|-|-|null|
|shapeSize|-|-|null|
|shapeSizeByFixedSize|-|-|false|

#### 属性详解

```java
DisplayOptions displayOptions = new DisplayOptions();

// 只能从本地加载图片，即使本地没有缓存也不下载了
displayOptions.setRequestLevel(RequestLevel.LOCAL);

// 禁用磁盘缓存
displayOptions.setCacheInDiskDisabled(true);

// 设置最大尺寸，用来解码Bitmap时计算inSampleSize，防止加载过大的图片到内存中。默认会先尝试用SketchImageView的layout_width和layout_height作为maxSize，否则会用当前屏幕的宽高作为maxSize
displayOptions.setMaxSize(1000, 1000);

// 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resize，也有可能小于resize
displayOptions.setResize(300, 300);

// 强制使经过最终返回的图片同resize的尺寸一致
displayOptions.setForceUseResize(true);

// 解码gif图返回GifDrawable
displayOptions.setDecodeGifImage(true);

// 尝试返回低质量的图片，例如PNG图片将使用ARGB_4444解析，具体的请查看ImageType类
displayOptions.setLowQualityImage(true);

// 强制使用RGB_565解码图片，KITKAT以上版本不能使用ARGB_4444
displayOptions.setBitmapConfig(Bitmap.Config.RGB_565);

// 解码图片的时候优先考虑质量（默认是优先考虑速度，当你要频繁的对一张图片进行读取然后写出的时候一定要设置优先考虑质量）
displayOptions.setInPreferQualityOverSpeed(true);

// 开启缩略图模式（需要resize配合）
displayOptions.setThumbnailMode(true);

// 将图片改成圆形的
displayOptions.setImageProcessor(new CircleImageProcessor());

// 为了加快速度，将经过ImageProcessor、resize或thumbnailMode处理过或者读取时inSampleSize大于等于8的图片保存到磁盘缓存中，下次就直接读取
displayOptions.setCacheProcessedImageInDisk(true);

// 禁用BitmapPool
displayOptions.setBitmapPoolDisabled(true);

// 禁用纠正图片方向功能
displayOptions.setCorrectImageOrientationDisabled(true);

// 禁用内存缓存
displayOptions.setCacheInMemoryDisabled(true);

// 设置正在加载时显示的图片
displayOptions.setLoadingImage(R.drawable.image_loading);

// 用resize和ImageProcessor修改R.drawable.image_loading然后作为正在加载时显示的图片
displayOptions.setLoadingImage(new MakerStateImage(R.drawable.image_loading);

// 设置加载失败时显示的图片
displayOptions.setErrorImage(R.drawable.image_load_error);

// 设置暂停下载时显示的图片
displayOptions.setPauseDownloadImage(R.drawable.image_load_pause_download);

// 使用过度效果来显示图片。如果你使用了TransitionImageDisplayer并且SketchImageView的layout_width和layout_height是固定的并且ScaleType是CENTER_CROP的话，就会自动使用FixedSizeBitmapDrawable的FixedSize功能，让占位图和实际图片的比例保持一致，这样可以保证最终显示不变形
displayOptions.setImageDisplayer(new TransitionImageDisplayer());

// 使用ImageView的layout_width和layout_height作为resize，优先级较高
displayOptions.setResizeByFixedSize(true);

// 以圆角矩形的形状绘制图片，包括loadingImage、errorImage、pauseDownloadImage以及要加载的图片
displayOptions.setImageShaper(new ReoundRectImageShaper(20)));

// 以500x500的尺寸绘制图片，包括loadingImage、errorImage、pauseDownloadImage以及要加载的图片
displayOptions.setShapeSize(500, 500);

// 使用ImageView的layout_width和layout_height作为shape size，优先级较高
// displayOptions.setShapeSizeByFixedSize(true);
```

Options支持的属性Helper中都有对应的方法，只是方法名不一样（没有set）

#### 使用Options：
Sketch.display()、Sketch.load()、Sketch.download()都会返回其专属的Helper，Helper中也都会有专门的方法配置这些属性，例如：
```java
// 显示
Sketch.with(context).display("http://t.cn/RShdS1f", sketchImageView)
	.loadingImage(R.drawable.image_loading)
	.displayer(new TransitionImageDisplayer())
	.shapeSizeByFixedSize()
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