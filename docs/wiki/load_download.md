Sketch共有display()、load()、download()三个方法可供使用，你可以根据你的需求选择合适的方法

|方法|作用|Options|Helper|
|:--|:--|:--|:--|
|download()|下载图片到本地，并实现本地缓存|DownloadOptions|DownloadHelper|
|load()|在download()方法的基础上，加载图片到内存中，并对图片进行处理|LoadOptions|LoadHelper|
|display()|在load()方法的基础上，将图片缓存在内存中并显示在ImageView上|DisplayOptions|DisplayHelper|

示例：
```
// 显示
Sketch.with(context).display("http://biying.png", sketchImageView)
    .loadingImage(R.drawable.image_loading)
    .commit();

// 加载
Sketch.with(context).load("http://biying.png", new LoadListener() {
    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(Bitmap bitmap, ImageFrom imageFrom, String mimeType) {

    }

    @Override
    public void onCompleted(GifDrawable gifDrawable, ImageFrom imageFrom, String mimeType) {

    }

    @Override
    public void onFailed(FailedCause failedCause) {

    }

    @Override
    public void onCanceled(CancelCause cancelCause) {

    }
}).maxSize(100, 100).commit();

// 下载
Sketch.with(context).download("http://biying.png", new DownloadListener() {
    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(File cacheFile, boolean isFromNetwork) {
        
    }

    @Override
    public void onCompleted(byte[] data) {

    }

    @Override
    public void onFailed(FailedCause failedCause) {

    }

    @Override
    public void onCanceled(CancelCause cancelCause) {

    }
}).commit();
```

跟过属性配置请参考[配置各种属性](options.md)

#### 同步执行 sync
load()和download()还支持同步执行，只需调用sync()方法开启即可，如：
```java
Sketch.with(context).load("http://biying.png", new LoadListener() {
    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(Bitmap bitmap, ImageFrom imageFrom, String mimeType) {

    }

    @Override
    public void onCompleted(GifDrawable gifDrawable, ImageFrom imageFrom, String mimeType) {

    }

    @Override
    public void onFailed(FailedCause failedCause) {

    }

    @Override
    public void onCanceled(CancelCause cancelCause) {

    }
}).maxSize(100, 100).sync().commit();
```

需要注意：
>* 不能在主线程开启同步，否则会运行时抛异常
>* 不仅下载、加载会在当前线程执行，listener也会在当前线程回调