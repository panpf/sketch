# SketchImageView 使用指南

[SketchImageView] 用来代替 ImageView，是 Sketch 的核心类，你必须使用 [SketchImageView] 才能保证图片会被正常且及时的回收

相比 ImageView 增加如下功能：
* 使用 displayImage() 系列方法即可方便的显示各种图片
* 支持显示下载进度
* 支持显示按下状态，按下后会在图片上显示一个黑色半透明的蒙层
* 支持显示图片来源，能方便的看出当前图片来自内存还是本地缓存还是刚从网络下载的
* 支持显示 gif 图标，当显示的是 gif 图的时候会在右下角显示一个图标，用于提示用户这是一张 gif 图片
* 支持显示失败的时候点击重新显示图片
* 支持暂停下载的时候点击强制显示图片
* onDetachedFromWindow 或图片替换时主动释放图片以及取消请求

### 使用

首先在布局中定义，如下：

res/layout/item_user.xml

```xml
<com.github.panpf.sketch.SketchImageView
    android:id="@+id/image_main"
    android:layout_width="130dp"
    android:layout_height="130dp"/>
```

然后在代码中通过 displayImage() 系列方法显示图片，如下：

```java
SketchImageView sketchImageView = (SketchImageView) findViewById(R.id.image_main);
sketchImageView.displayImage("http://t.cn/RShdS1f");
```

更多类型 URI 的使用请参考 [URI 类型及使用指南][uri]

### 设置显示选项

```java
sketchImageView.getOptions()
    .set***()
    .set***();
```

通过 setOptions(DisplayOptions) 方法批量设置

```java
DisplayOptions displayOptions = new DisplayOptions();
displayOptions.setLoadingImage(R.drawable.image_loading);
displayOptions.setErrorImage(R.drawable.image_error);
....

sketchImageView.setOptions(displayOptions)
```

更多配置项请参考 [使用 Options 配置图片][options]

### 手势缩放和分块显示超大图

参考 [手势缩放、旋转图片][zoom]

参考 [分块显示超大图片][block_display]

### 监听显示过程和下载进度

参考 [监听开始、成功、失败以及下载进度事件][listener]

### 显示下载进度

[SketchImageView] 提供了一个简易的显示进度功能，会在图片上面显示一个黑色半透明层，随着进度的更新从上往下消失，你只需调用如下代码开启即可：

```java
sketchImageView.setShowDownloadProgressEnabled(true);
```

`一定要在 displayImage() 之前调用，否则不起作用`

默认会使用 DisplayOptions 里的 ImageShaper 修改蒙层的形状，你也可以自定义蒙层的形状，如下：

```java
sketchImageView.setShowDownloadProgressEnabled(true, new RoundRectImageShaper());
```

你还可以修改蒙层的颜色，如下：

```java
sketchImageView.setShowDownloadProgressEnabled(true, Color.parse("#ff0000"));
```

### 显示按下状态

[SketchImageView] 支持点击的时候在图片上面显示一层黑色半透明蒙层，表示按下状态

```java
// 你需要先设置点击Listener
sketchImageView.setOnClickListener(...);

// 然后开启按下状态
sketchImageView.setShowPressedStatusEnabled(true);
```

默认会使用 DisplayOptions 里的 ImageShaper 修改蒙层的形状，你也可以自定义蒙层的形状，如下：

```java
sketchImageView.setShowPressedStatusEnabled(true, new RoundRectImageShaper());
```

你还可以修改蒙层的颜色，如下：

```java
sketchImageView.setShowPressedStatusEnabled(true, Color.parse("#ff0000"));
```

### 显示图片来源

[SketchImageView] 支持显示图片的来源，效果如下：

![sample](../res/sample_debug_mode.jpeg)

这个功能在调试时还是比较有用的，你只需开启即可：

```java
sketchImageView.setShowImageFromEnabled(true);
```

图片加载完成后会在 [SketchImageView] 的左上角显示一个纯色的三角形，根据三角形的颜色你就可以知道图片是从哪里来的
* 紫色表示是从内存中加载的，例如 base64 图片
* 绿色表示是从内存缓存中加载的
* 蓝色表示是本地图片
* 黄色表示是从本地缓存加载的
* 红色表示是刚刚从网络下载的

### 失败时点击重新显示

```java
sketchImageView.setClickRetryOnDisplayErrorEnabled(true);
```

### 暂停下载时点击强制显示

参考 [移动数据或有流量限制的 WIFI 下暂停下载图片，节省流量][pause_download]

### 显示 GIF 图标识

参考 [播放 GIF 图片][play_gif]

[pause_download]: save_cellular_traffic.md
[play_gif]: play_gif.md
[zoom]: zoom.md
[block_display]: block_display.md
[uri]: uri.md
[options]: options.md
[listener]: listener.md
[SketchImageView]: ../../sketch/src/main/java/com/github/panpf/sketch/SketchImageView.java
