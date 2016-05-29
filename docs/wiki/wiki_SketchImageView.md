###简介
SketchImageView用来代替ImageView，你必须使用SketchImageView才能保证图片会被正常回收

SketchImageView有以下特点：
>* 使用display***Image()系列方法即可方便的显示各种图片
>* 支持显示下载进度
>* 支持显示按下状态，长按的时候还会显示类似Android 5.0的涟漪效果
>* 支持显示图片来源，能方便的看出当前图片来自内存还是本地缓存还是刚从网络下载的
>* 支持显示GIF图标，当显示的是GIF图的时候会在右下角显示一个图标，用于提醒用户这是一张GIF图片
>* 支持当显示失败的时候点击重新显示图片
>* 支持当暂停下载的时候点击显示图片
>* onDetachedFromWindow的时候主动释放图片以及取消请求
>* 兼容RecyclerView

###开始使用SketchImageView：
#### 1.在XML中使用SketchImageView
res/layout/item_user.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<me.xiaopan.sketch.SketchImageView
    android:id="@+id/image_head"
    android:layout_width="130dp"
    android:layout_height="130dp"
  />
```

#### 2.在Activity、Fragment或Adapter中拿到SketchImageView
```java
SketchImageView sketchImageView = (SketchImageView) findVieById(R.id.image_head);
```

#### 3. 设置显示参数（可选）
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

SketchImageView sketchImageView = ...;
sketchImageView.setDisplayOptions(displayOptions);
```

Sketch提供了对多个DisplayOptions的存储和管理，你可以定义一个共用的DisplayOptions，通过Sketch.put(Enum<?>, RequestOptions) 存储起来，然后在需要用的时候调用sketchImageView.setDisplayOptions<Enum<?>>即可方便设置，详细请[点击查看](https://github.com/xiaopansky/Spear/wiki/RequestOptions)

另外在使用共用DisplayOptions的时候如果你还需要修改个别参数，但又不想影响共用的DisplayOptions的话，你可以在setDisplayOptions(Enum<?>)后直接调用getDisplayOptions()方法获取DisplayOptions然后直接设置相关属性即可，如下：
```java
sketchImageView.setDisplayOptions(OptionsType.HEAD);
sketchImageView.getDisplayOptions().setDecodeGifImage(true);
```
这是因为每个SketchImageView都有一个私有的DisplayOptions，你调用setDisplayOptions()方法设置一个DisplayOptions的时候，实际上是拷贝属性到私有的DisplayOptions中，调用getDisplayOptions()方法返回的是私有的DisplayOptions，这时候你怎么改都无所谓

####  4.设置监听显示过程（可选）
```java
sketchImageView.setDisplayListener(new DisplayListener() {
    @Override
    public void onStarted() {
        Log.i("displayListener", "开始");
    }

    @Override
    public void onCompleted(ImageFrom imageFrom, String mimeType) {
        Log.i("displayListener", "完成");
    }

    @Override
    public void onFailed(FailCause failCause) {
        Log.i("displayListener", "失败");
    }

    @Override
    public void onCanceled(CancelCause cancelCause) {
        Log.i("displayListener", "取消");
    }
});
```

####4. 设置监听下载进度（可选）
```java
sketchImageView.setProgressListener(new ProgressListener() {
    @Override
    public void onUpdateProgress(int totalLength, int completedLength) {
        progressBar.setProgress(completedLength);
        Log.i("progressListener", "文件总大小："+totalLength+"; 已完成："+comletedLength);
    }
});
```

####5.设置显示下载进度（可选）
SketchImageView提供了一个简易版的显示进度的功能，你只需调用如下代码开启即可
```java
sketchImageView.setShowDownloadProgress(true);
```
使用此功能的好处是你无需在SketchImageView的上面再放一个ProgressBar，可减少View对象的数量，特别是对于有多张图片的时候，效果十分明显。

####6. 设置显示按下状态（可选）
SketchImageView支持点击的时候在图片上面显示一层黑色半透明层，表示按下状态，长按的时候还会有类似Android5.0的涟漪效果
```java
// 你需要先开启点击事件
sketchImageView.setOnClickListener(...);

// 然后开启按下状态
sketchImageView.setShowPressedStatus(true);
```
开启按下状态后你就不需要再在SketchImageView的上方放一个View来模拟这种效果了，这样就减少的View的数量，特别是对于有多张图片的时候，效果十分明显。

####7.设置ImageShape（可选）
下载进度和按下状态默认是矩形的，如果你使用了CircleImageProcessor将图片处理成了圆形的，那么这时候

没按下时是这样的：

![image_shape1](https://github.com/xiaopansky/Sketch/raw/master/docs/image_shape1.png)

按下时是这样的：

![image_shape2](https://github.com/xiaopansky/Sketch/raw/master/docs/image_shape2.png)

这样当然不行了，ImageShape就是来解决这个问题的，你可以执行如下代码设置ImageShape为圆形的
```java
sketchImageView.setImageShape(SketchImageView.ImageShape.CIRCLE);
```
这时候按下后效果是这样的：

![image_shape3](https://github.com/xiaopansky/Sketch/raw/master/docs/image_shape3.png)

可能有同学会说为什么不用ClipPath实现这个效果呢？这样也不用裁剪图片了，省事。经实际测试后发现ClipPath会有明显的锯齿，效果很不好，并且部分机型硬件加速还不支持ClipPath。

另外当你需要将图片处理成圆角的时候会比较麻烦一些。这是因为当一个列表内图片的尺寸不一致并且ImageView的ScaleType又是CNTER_CROP或FIT_CENTER的时候会放大或缩小图片，那么图片的圆角也会随之放大或缩小，最终的效果就是不同尺寸的图片的圆角大小不一样，效果比较难看。Sketch的解决办法就是先设置ImageView宽高为固定值，然后使用resizeByFixedSize属性加forceUseResize属性让最终返回的图片的尺寸都一样，如下：
```java
new DisplayOptions()
	...
	.setResizeByFixedSize(true)
	.setForceUseResize(true);
```
resizeByFixedSize意思就是使用ImageView的layout size作为resize，然后加上forceUseResize属性让最终返回的图片的尺寸一定是resize

#### 8.设置GIF图标识（可选）
Sketch支持解码GIF图，因此SketchImageView在发现显示的是GIF图的时候可以在SketchImageView的右下角显示一个图标，以告诉用户这是一张GIF图，如下：

![gif](https://github.com/xiaopansky/Sketch/raw/master/docs/ic_gif.png)

```java
sketchImageView.setGifFlagDrawable(R.drawable.ic_gif);
```

然后效果是这样的

![gif](https://github.com/xiaopansky/Sketch/raw/master/docs/gif_flag_drawable.png)

####9. 设置显示图片来源（可选）
SketchImageView还支持显示图片来源，如下：
```
sketchImageView.setShowFromFlag(true);
```
开启此功能后会在SketchImageView的左上角显示一个纯色的三角形，根据三角形的颜色你就可以知道图片是从哪里来的。
>* 绿色表示是从内存缓存中加载的；
>* 蓝色表示是本地图片；
>* 黄色表示是从本地缓存加载的；
>* 红色表示是刚刚从网络下载的。

![sample](https://github.com/xiaopansky/Sketch/raw/master/docs/sampe_debug_mode.jpeg)

#### 10. 开启点击重新显示功能
SketchImageView还支持在显示失败时点击重新显示和暂停下载时候点击直接显示功能，如下：
```java
// 开启显示失败时点击重新显示功能
sketchImageView.setClickRedisplayOnFailed(true);

// 开启暂停下载时候点击直接显示功能
sketchImageView.setClickDisplayOnPauseDownload(true);
```

#### 11. 最后调用display***Image()系列方法显示图片
```java
// display from image network
sketchImageView.displayImage("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg");

// display image from SDCard
sketchImageView.displayImage("/sdcard/sample.png");

// display apk icon from SDCard
sketchImageView.displayImage("/sdcard/google_play.apk");

// display resource drawable
sketchImageView.displayResourceImage(R.drawable.sample);

// display image from asset
sketchImageView.displayAssetImage("sample.jpg");

// display image from URI
Uri uri = ...;
sketchImageView.displayURIImage(uri);
```