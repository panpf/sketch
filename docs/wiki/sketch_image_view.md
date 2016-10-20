#### 简介

SketchImageView用来代替ImageView，你必须使用SketchImageView才能保证图片会被正常回收

特点：
>* 使用display***Image()系列方法即可方便的显示各种图片
>* 支持显示下载进度
>* 支持显示按下状态，长按的时候还会显示类似Android 5.0的涟漪效果
>* 支持显示图片来源，能方便的看出当前图片来自内存还是本地缓存还是刚从网络下载的
>* 支持显示gif图标，当显示的是gif图的时候会在右下角显示一个图标，用于提醒用户这是一张gif图片
>* 支持显示失败的时候点击重新显示图片
>* 支持暂停下载的时候点击强制显示图片
>* onDetachedFromWindow的时候主动释放图片以及取消请求

### 使用SketchImageView
首先在布局中定义，如下：
res/layout/item_user.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<me.xiaopan.sketch.SketchImageView
    android:id="@+id/image_head"
    android:layout_width="130dp"
    android:layout_height="130dp"/>
```

然后在代码中调用其display***Image()系列方法显示图片，如下：
```java
SketchImageView sketchImageView = (SketchImageView) findVieById(R.id.image_head);

// display from image network
sketchImageView.displayImage("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg");

// display image from SDCard
sketchImageView.displayImage("/sdcard/sample.png");
sketchImageView.displayImage("file:///sdcard/sample.png");

// display apk icon from SDCard
sketchImageView.displayImage("/sdcard/google_play.apk");

// display installed app icon
sketchImageView.displayInstalledAppIcon("com.tencent.qq", 50001);

// display resource drawable
sketchImageView.displayResourceImage(R.drawable.sample);

// display image from asset
sketchImageView.displayAssetImage("sample.jpg");

// display image from URI
Uri uri = ...;
sketchImageView.displayURIImage(uri);
```

### 其它功能（可选）

#### 1.设置各种参数
```java
sketchImageView.getOptions()
    .set***()
    .set***();
```
或通过setOptions(DisplayOptions)方法批量设置
```java
DisplayOptions displayOptions = new DisplayOptions();
displayOptions.setLoadingImage(R.drawable.image_loading);
displayOptions.setErrorImage(R.drawable.image_error);
....

sketchImageView.setOptions(displayOptions)
```
具体的配置项请参考[配置各种属性.md](options.md)

#### 2.使用手势缩放和分块显示超大图功能

[点我了解手势缩放功能](zoom.md)

[点我了解分块显示超大图功能](large_image.md)

#### 3.监听显示过程和下载进度
```java
// 监听显示过程
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
    public void onError(FailCause failCause) {
        Log.i("displayListener", "失败");
    }

    @Override
    public void onCanceled(CancelCause cancelCause) {
        Log.i("displayListener", "取消");
    }
});

// 监听下载进度
sketchImageView.setDownloadProgressListener(new DownloadProgressListener() {
    @Override
    public void onUpdateDownloadProgress(int totalLength, int completedLength) {
        Log.i("progressListener", "文件总大小："+totalLength+"; 已完成："+comletedLength);
    }
});
```
``注意：setDownloadProgressListener()方法一定要在displayImage()之前调用，否则不起作用``

#### 4.在SketchImageView上显示下载进度
SketchImageView提供了一个简易版的显示进度的功能，你只需调用如下代码开启即可，这样你就无需在ImageView上面放一个ProgressBar来实现这种效果了。
```java
sketchImageView.setShowDownloadProgress(true);
```
``同样一定要在displayImage()之前调用，否则不起作用``
``如果图片是圆角或者圆形的那么还需要通过ImageShape来改变进度蒙层的形状``[查看如何使用ImageShape](#ImageShape)

#### 5.在SketchImageView上显示按下状态
SketchImageView支持点击的时候在图片上面显示一层黑色半透明层，表示按下状态，长按的时候还会有类似Android5.0的涟漪效果，这样你就无需在ImageView上面放一个黑色半透明的View来实现这种效果了。
```java
// 你需要先设置点击Listener
sketchImageView.setOnClickListener(...);

// 然后开启按下状态
sketchImageView.setShowPressedStatus(true);
```
``如果图片是圆角或者圆形的那么还需要通过ImageShape来改变按下蒙层的形状``[查看如何使用ImageShape](#ImageShape)

<h4 id="ImageShape">6.设置ImageShape改变蒙层的形状</h4>
下载进度和按下状态蒙层默认是矩形的，如果你使用了CircleImageProcessor将图片处理成了圆形的，那么这时候

没按下时是这样的：

![image_shape1](../res/image_shape1.png)

按下时是这样的：

![image_shape2](../res/image_shape2.png)

这样当然不行了，ImageShape就是来解决这个问题的，你可以执行如下代码设置ImageShape为圆形的
```java
sketchImageView.setImageShape(SketchImageView.ImageShape.CIRCLE);
```
这时候按下后效果是这样的：

![image_shape3](../res/image_shape3.png)

可能有同学会说为什么不用ClipPath实现这个效果呢？这样也不用裁剪图片了，省事。经实际测试后发现ClipPath会有明显的锯齿，效果不好，并且部分机型硬件加速还不支持ClipPath。

当图片是圆角的时候，需要如下设置：
```java
sketchImageView.setImageShape(SketchImageView.ImageShape.ROUNDED_RECT);
sketchImaegView.setImageShapeCornerRadius(20);
```
圆角还有一个比较麻烦的地方是当一个列表内图片的尺寸不一致并且ImageView的ScaleType又是CNTER_CROP或FIT_CENTER的时候会放大或缩小图片，那么图片的圆角也会随之放大或缩小，最终的效果就是不同尺寸的图片的圆角大小不一样，效果比较难看。要解决这个问题就需要先设置ImageView宽高为固定值，然后使用设置resizeByFixedSize并且设置forceUseResize让最终返回的图片的尺寸都一样，如下：
```java
new DisplayOptions()
	.setResizeByFixedSize(true)
	.setForceUseResize(true);
```
resizeByFixedSize意思就是使用ImageView的layout_width和layout_height作为resize，然后forceUseResize的作用是让最终返回的图片的尺寸一定是resize

#### 7.显示gif图标识
Sketch支持解码gif图，因此SketchImageView在发现显示的是gif图的时候可以在SketchImageView的右下角显示一个图标，以告诉用户这是一张gif图，如下：

```java
sketchImageView.setGifFlagDrawable(R.drawable.ic_gif);
```

Sketch通过mimeType来识别gif图，所以即使你没有调用decodeGifImage()也会认为这是一张gif图，这个功能主要用来在列表中告诉用户这是一张gif图，然后点击图片到大图详情页再播放gif

效果如下：

![gif](../res/gif_flag_drawable.png)

#### 8.显示图片来源
SketchImageView还支持显示图片来源，如下：
```
sketchImageView.setShowFromFlag(true);
```

开启此功能后会在SketchImageView的左上角显示一个纯色的三角形，根据三角形的颜色你就可以知道图片是从哪里来的
>* 绿色表示是从内存缓存中加载的；
>* 蓝色表示是本地图片；
>* 黄色表示是从本地缓存加载的；
>* 红色表示是刚刚从网络下载的。

效果如下：

![sample](../res/sample_debug_mode.jpeg)

#### 9.失败时点击重新显示
一句话开启即可
```java
sketchImageView.setClickRetryOnError(true);
```

#### 10.暂停下载时点击强制显示
一句话开启即可
```java
sketchImageView.setClickDisplayOnPauseDownload(true);
```
暂停下载时点击即可强制（不受暂停下载功能控制）显示当前图片，此功能`只作用于当前图片`，并且`只生效一次`