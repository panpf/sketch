# 只加载或下载图片

Sketch 共有三个方法可供使用，你可以根据你的需求选择合适的方法
* download()：下载图片到本地，并实现本地缓存
* load()：在 download() 方法的基础上，加载图片到内存中，并对图片进行处理
* display()：在 load() 方法的基础上，将图片缓存在内存中并显示在 ImageView 上，[SketchImageView] 的 displayImage() 方法就是对 Sketch.display() 方法的一层封装

### Options & Listener & Helper

每一种方法都有专用的 Options、Listener、Helper

* Options：用来定制请求，更详细的介绍请参考 [使用 Options 配置图片][options]
* Listener：用来监控请求的结果，更详细的介绍请参考 [监听准备加载、成功、失败以及下载进度事件][listener.md]
* Helper：用来组织请求并提交

|Method|Options|Listener|Helper|
|:---|:---|:---|:---|
|download()|DownLoadOptions|DownloadListener|DownloadHelper|
|load()|LoadOptions|LoadListener|LoadHelper|
|display()|DisplayOptions|DisplayListener|DisplayHelper|

### 示例：

1.显示一张图片到 SketchImageView 上并设置加载中占位图

```java
Sketch.with(context).display("http://t.cn/RShdS1f", sketchImageView)
    .loadingImage(R.drawable.image_loading)
    .commit();
```

显示图片时我们可以直接使用 SketchImageView 的 displayImage() 方法，其内部也是调的这个方法，更多内容请参考[SketchImageView 使用指南][sketch_image_view.md]

2.加载图片到内存中，并限制图片尺寸不能超过 100x100

```java
Sketch.with(context).load("http://t.cn/RShdS1f", new LoadListener() {
    @Override
    public void onCompleted(LoadResult loadResult) {
        Bitmap bitmap = loadResult.getBitmap();
        ...
    }

    ...
}).maxSize(100, 100).commit();
```

3.下载图片到本地

```java
Sketch.with(context).download("http://t.cn/RShdS1f", new DownloadListener() {
    @Override
    public void onCompleted(DownloadResult downloadResult) {
        DiskCacheEntry cache = downloadResult.getDiskCacheEntry();
        File imageFile = cache.getFile();
        ...
    }

    ...
}).commit();
```

### 同步执行

load() 和 download() 方法还支持同步执行，这在非主线程中加载图片时非常有用，只需调用 sync() 方法开启即可：

```java
// load
Sketch.with(context).load("http://t.cn/RShdS1f", new LoadListener() {
  @Override
  public void onCompleted(LoadResult loadResult) {
      Bitmap bitmap = loadResult.getBitmap();
      ...
  }

  ...
}).maxSize(100, 100).sync().commit();

// download
Sketch.with(context).download("http://t.cn/RShdS1f", new DownloadListener() {
    @Override
    public void onCompleted(DownloadResult downloadResult) {
        DiskCacheEntry cache = downloadResult.getDiskCacheEntry();
        File imageFile = cache.getFile();
        ...
    }

    ...
}).sync().commit();
```

注意：
* 不能在主线程开启同步，否则会运行时抛异常
* 不仅下载、加载的主体过程会在当前调用线程执行，listener 回调也会在当前调用线程执行，更多listener内容请参考 [监听准备加载、成功、失败以及下载进度事件][listener.md]

[SketchImageView]: ../../sketch/src/main/java/me/xiaopan/sketch/SketchImageView.java
[options]: options.md
[listener]: listener.md
[sketch_image_view]: sketch_image_view.md
