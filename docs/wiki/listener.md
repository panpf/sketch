Sketch支持对``开始加载``、``完成``、``失败``、``取消``以及``下载进度``进行监听

SketchImageView
```java
SketchImageView sketchImageView = ...;
sketchImageView.setDisplayListener(new DisplayListener() {
    @Override
    public void onStartLoad() {
        // 只有在需要进入异步线程加载数据时才会回调 onStartLoad() 方法
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

// setDownloadProgressListener()一定要在displayImage之前执行
sketchImageView.setDownloadProgressListener(new DownloadProgressListener() {
    @Override
    public void onUpdateDownloadProgress(int totalLength, int completedLength) {

    }
});

sketchImageView.displayImage("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg");
```

``display()不支持设置listener和downloadProgressListener``

load()
```java
Sketch.with(context).load("http://t.cn/RShdS1f", new LoadListener() {
    @Override
    public void onStartLoad() {
        // 只有在需要进入异步线程加载数据时才会回调 onStartLoad() 方法
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

download()
```java
Sketch.with(context).download("http://t.cn/RShdS1f", new DownloadListener() {
    @Override
    public void onStartLoad() {
        // 只有在需要进入异步线程加载数据时才会回调 onStartLoad() 方法
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

listener 默认都是异步回调的，并且都会在主线程回调，但是当 load() 和 download() 开启了同步后其 listener 就在当前线程回调
