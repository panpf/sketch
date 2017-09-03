Sketch 共有三个方法可供使用，你可以根据你的需求选择合适的方法
>* download()：下载图片到本地，并实现本地缓存
>* load()：在download()方法的基础上，加载图片到内存中，并对图片进行处理
>* display()：在load()方法的基础上，将图片缓存在内存中并显示在ImageView上

SketchImageView就是使用的 Sketch.display() 方法

每一种方法都有专用的Options、Listener、Helper

* Options：用来定制请求，[点击了解更多](options_and_helper.md)
* Listener：用来监控请求的状态，[点击了解更多](listener.md)
* Helper：用来组织请求并提交请求

|Method|Options|Listener|Helper|
|:---|:---|:---|:---|
|download()|DownLoadOptions|DownloadListener|DownloadHelper|
|load()|LoadOptions|LoadListener|LoadHelper|
|display()|DisplayOptions|DisplayListener|DisplayHelper|

#### 示例：

显示一张图片到 SketchImageView 上并设置加载中占位图

```java
Sketch.with(context).display("http://t.cn/RShdS1f", sketchImageView)
    .loadingImage(R.drawable.image_loading)
    .commit();
```

显示图片时我们可以直接使用 SketchImageView 的 displayImage() 方法，其内部也是调的这个方法，[点我了解更多 SketchImageView 的用法](sketch_image_view.md)

加载图片到内存中，并限制图片尺寸不能超过100x100

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

下载图片到本地
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

#### 同步执行

load() 和 download() 方法还支持同步执行，这在非主线程中加载图片时非常有用，只需调用sync()方法开启即可：

```java
Sketch.with(context).load("http://t.cn/RShdS1f", new LoadListener() {
  @Override
  public void onCompleted(LoadResult loadResult) {
      Bitmap bitmap = loadResult.getBitmap();
      ...
  }

  ...
}).maxSize(100, 100).sync().commit();


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
>* 不能在主线程开启同步，否则会运行时抛异常
>* 不仅下载、加载的主体过程会在当前调用线程执行，listener 回调也会在当前调用线程执行
