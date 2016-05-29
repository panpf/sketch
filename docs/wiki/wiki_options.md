ReuqestOptions是属性集，用来定义属性模板，这样在使用的时候可以批量设置属性。

RequestOptions分为以下三种
>* DisplayOptions：用于DisplayHelper和SketchImageView
>* LoadOptions：用于LoadHelper
>* DownloadOptions：用于DownloadHelper

继承关系如下：
DisplayOptions `extends` LoadOptions `extends` DownloadOptions

支持属性如下（'-'代表不支持，非'-'代表支持并且默认值是什么）：

|属性|DownloadOptions|LoadOptions|DisplayOptions|
|:--|:--|:--|:--|
|requestLevel|NET|NET|NET|
|diskCache|true|true|true|
|maxSize|-|屏幕的1.5倍|默认会先尝试用SketchImageView的layout size作为maxSize，否则会用当前屏幕宽高的1.5倍作为maxSize|
|resize|-|null|null|
|resizeByFixedSize|-|false|false|
|forceUseResize|-|false|false|
|processor|-|null|null|
|decodeGifImage|-|null|null|
|lowQualityImage|-|false|false|
|memoryCache|-|-|true|
|displayer|-|-|DefaultImageDisplayer|
|loadingImage|-|-|null|
|failureImage|-|-|null|
|pauseDownloadImage|-|-|null|

接下来我们就以DisplayOptions为例（LoadHelper和DownloadHelper只需照葫芦画瓢即可）

####配置DisplayOptions
```java
DisplayOptions displayOptions = new DisplayOptions();
// 禁用磁盘缓存
displayOptions.setCacheInDisk(false);
// 设置最大尺寸，用来解码Bitmap时计算inSampleSize，防止加载过大的图片到内存中，默认会先尝试用SketchImageView的layout size作为maxSize，否则会用当前屏幕宽高的1.5倍作为maxSize
displayOptions.setMaxSize(1000, 1000);
// 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resi，也有可能小于resize
displayOptions.setResize(300, 300);
// 使用SketchImageView的layout size作为resize，和setResize只能二选一
displayOptions.setResizeByFixedSize(true);
// 强制使经过resize返回的图片同resize的尺寸一致
displayOptions.setForceUseResize(true);
// 设置使用BitmapFactory解码GIF图，通常在列表中不需要显示GIF图，只需要显示第一帧即可，使用BitmapFactory即可满足这样的需求
displayOptions.setDecodeGifImage(false);
// 尝试返回低质量的图片
displayOptions.setLowQualityImage(true);
// 设置图片处理器
displayOptions.setImageProcessor(new CircleImageProcessor());
// 禁用内存缓存
displayOptions.setCacheInMemory(false);
// 设置正在加载的时候显示的图片
displayOptions.setLoadingImage(R.drawable.image_loading);
// 设置当加载失败的时候显示的图片
displayOptions.setFailureImage(R.drawable.image_load_fail);
// 设置当暂停下载的时候显示的图片
displayOptions.setPauseDownloadImage(R.drawable.image_load_fail);
// 设置图片显示器，在最后一步会使用ImageDisplayer来显示图片。如果你使用了TransitionImageDisplayer并且SketchImageView的layout size是固定的并且ScaleType是CENTER_CROP的话，就会自动使用FixedSizeBitmapDrawable的FixesSize功能，让占位图和实际图片的比例保持一致，这样可以保证最终显示效果不变形
displayOptions.setImageDisplayer(new TransitionImageDisplayer());
```

#####默认配置

DownloadOptions的默认配置是：
>* 开启硬盘缓存

LoadOptions的默认配置是：
>* 开启硬盘缓存
>* 解码GIF图片
>* maxSize为当前设备屏幕的1.5倍

DisplayOptions除继承自LoadOptions之外的默认配置是：
>* 开启硬盘缓存
>* 解码GIF图片
>* maxSize为当前设备屏幕的1.5倍
>* 开启内存缓存
>* ImageDisplayer为DefaultImageDisplayer（无任何动画效果）

####使用DisplayOptions：
```java
SketchImageView sketchImageView = ...;
sketchImageView.setDisplayOptions(displayOptions);
sketchImageView.displayImage("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg");
```

####管理多个RequestOptions
当你有多个RequestOptions的时候你要怎么去管理并方便的使用呢？

最简单的办法就是你用一个静态的全局可访问的Map来保存，然后在用的时候从Map中。

Sketch考虑到有这种需求于是就提供了一个这样的实现，稍微有些特别的是Key不是String而是Enum

**下面来看一个示例**
首先你需要定义一个枚举类来作为ReuqestOptions的Key，例如：
```java
public enum OptionsType {
    /**
     * 通用矩形
      */
    NORMAL_RECT,

    /**
     * APP图标
     */
    APP_ICON,

    /**
     * 通用圆形
     */
    NORMAL_CIRCULAR,

    /**
     * 详情
     */
    DETAIL,

    /**
     * 窗口背景
     */
    WINDOW_BACKGROUND,
}
```

然后在Application中配置并通过Spear.putOptions(Enum<?>, Options)方法保存，例如：
```java
public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

        TransitionImageDisplayer transitionImageDisplayer = new TransitionImageDisplayer();
        Sketch.putOptions(OptionsType.NORMAL_RECT, new DisplayOptions()
                        .setLoadingImage(R.drawable.image_loading)
                        .setFailureImage(R.drawable.image_failure)
                        .setPauseDownloadImage(R.drawable.image_pause_download)
                        .setDecodeGifImage(false)
                        .setImageDisplayer(transitionImageDisplayer)
        );

        RoundedCornerImageProcessor roundedCornerImageProcessor = new RoundedCornerImageProcessor(DeviceUtils.dp2px(context, 10));
        Resize appIconSize = new Resize(DeviceUtils.dp2px(context, 60), DeviceUtils.dp2px(context, 60), ImageView.ScaleType.CENTER_CROP);
        Sketch.putOptions(OptionsType.APP_ICON, new DisplayOptions()
                        .setLoadingImage(new LoadingImageHolder(R.drawable.image_loading).setImageProcessor(roundedCornerImageProcessor).setResize(appIconSize).setForceUseResize(true))
                        .setFailureImage(new FailureImageHolder(R.drawable.image_failure).setImageProcessor(roundedCornerImageProcessor).setResize(appIconSize).setForceUseResize(true))
                        .setPauseDownloadImage(new PauseDownloadImageHolder(R.drawable.image_pause_download).setImageProcessor(roundedCornerImageProcessor).setResize(appIconSize).setForceUseResize(true))
                        .setDecodeGifImage(false)
                        .setResizeByFixedSize(true)
                        .setForceUseResize(true)
                        .setImageDisplayer(transitionImageDisplayer)
                        .setImageProcessor(roundedCornerImageProcessor)
        );

        Sketch.putOptions(OptionsType.DETAIL, new DisplayOptions()
                        .setImageDisplayer(transitionImageDisplayer)
        );

        Sketch.putOptions(OptionsType.NORMAL_CIRCULAR, new DisplayOptions()
                        .setLoadingImage(new LoadingImageHolder(R.drawable.image_loading).setImageProcessor(CircleImageProcessor.getInstance()))
                        .setFailureImage(new FailureImageHolder(R.drawable.image_failure).setImageProcessor(CircleImageProcessor.getInstance()))
                        .setPauseDownloadImage(new PauseDownloadImageHolder(R.drawable.image_pause_download).setImageProcessor(CircleImageProcessor.getInstance()))
                        .setDecodeGifImage(false)
                        .setImageDisplayer(transitionImageDisplayer)
                        .setImageProcessor(CircleImageProcessor.getInstance())
        );

        Sketch.putOptions(OptionsType.WINDOW_BACKGROUND, new LoadOptions()
                        .setImageProcessor(new GaussianBlurImageProcessor(true))
                        .setDecodeGifImage(false)
        );
    }
}
```

最后在使用的时候就可以直接设置OptionsType即可，所有的options()方法都提供了options(Enum)重载函数，例如：
```java
SketchImageView sketchImageView = ...;
sketchImageView.setDisplayOptions(OptionsType.NORMAL_RECT);
sketchImageView.displayImage("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg");

SketchImageView sketchImageView2 = ...;
sketchImageView2.setDisplayOptions(OptionsType.APP_ICON);
sketchImageView2.displayImage("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg");
```