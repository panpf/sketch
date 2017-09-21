# 监听开始、成功、失败以及下载进度事件

Sketch 支持对 `开始`、`完成`、`失败`、`取消` 以及 `下载进度` 进行监听

注意：
* listener 默认在主线程回调，但是当 Sketch.load() 和 Sketch.download() 开启了同步后其 listener 就在运行线程回调，可能是主线程，也可能是非主线程
* onStarted() 方法只有在需要进入非主线程加载或下载图片时才会被回调，因此有可能不回调 onStarted() 方法而直接回调其它方法

#### SketchImageView

```java
SketchImageView sketchImageView = ...;

// setDisplayListener() 一定要在 displayImage() 之前
sketchImageView.setDisplayListener(new DisplayListener() {
    @Override
    public void onStarted() {
        // 只有在需要进入非主线程加载图片时才会回调 onStarted() 方法
    }

    @Override
    public void onCompleted(Drawable drawable, ImageFrom imageFrom, ImageAttrs imageAttrs) {

    }

    @Override
    public void onError(ErrorCause errorCause) {

    }

    @Override
    public void onCanceled(CancelCause cancelCause) {

    }
});

// setDownloadProgressListener() 一定要在 displayImage() 之前
sketchImageView.setDownloadProgressListener(new DownloadProgressListener() {
    @Override
    public void onUpdateDownloadProgress(int totalLength, int completedLength) {

    }
});

sketchImageView.displayImage("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg");
```

``Sketch.display() 不支持设置 listener 和 downloadProgressListener``

#### Sketch.load()

```java
Sketch.with(context).load("http://t.cn/RShdS1f", new LoadListener() {
    @Override
    public void onStarted() {
        // 只有在需要进入非主线程加载图片时才会回调 onStarted() 方法
    }

    @Override
    public void onCompleted(LoadResult loadResult) {

    }

    @Override
    public void onError(ErrorCause errorCause) {

    }

    @Override
    public void onCanceled(CancelCause cancelCause) {

    }
}).downloadProgressListener(new DownloadProgressListener() {
    @Override
    public void onUpdateDownloadProgress(int totalLength, int completedLength) {

    }
}).maxSize(100, 100).commit();
```

#### Sketch.download()

```java
Sketch.with(context).download("http://t.cn/RShdS1f", new DownloadListener() {
    @Override
    public void onStarted() {
        // 只有在需要进入非主线程下载图片时才会回调 onStarted() 方法
    }

    @Override
    public void onCompleted(DownloadResult downloadResult) {

    }

    @Override
    public void onError(ErrorCause errorCause) {

    }

    @Override
    public void onCanceled(CancelCause cancelCause) {

    }
}).downloadProgressListener(new DownloadProgressListener() {
    @Override
    public void onUpdateDownloadProgress(int totalLength, int completedLength) {

    }
}).commit();
```
