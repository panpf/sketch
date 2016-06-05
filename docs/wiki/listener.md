Sketch支持对``开始``、``完成``、``失败``、``取消``以及``下载进度``的监听

SketchImageView
```java
SketchImageView sketchImageView = ...;
sketchImageView.setDisplayListener(new DisplayListener() {
    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(ImageFrom imageFrom, String mimeType) {

    }

    @Override
    public void onFailed(FailCause failCause) {

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
}).downloadProgressListener(new DownloadProgressListener() {
    @Override
    public void onUpdateDownloadProgress(int totalLength, int completedLength) {

    }
}).maxSize(100, 100).commit();
```

download()
```java
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
}).downloadProgressListener(new DownloadProgressListener() {
    @Override
    public void onUpdateDownloadProgress(int totalLength, int completedLength) {

    }
}).commit();
```

listener默认都是异步回调的，并且都会在主线程回调，但是当load()和download()开启了同步后其listener就在当前线程回调