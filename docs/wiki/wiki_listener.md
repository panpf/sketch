Sketch支持对``开始``、``完成``、``失败``、``取消``以及``进度``的监听，例如：
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

sketchImageView.setProgressListener(new ProgressListener() {
    @Override
    public void onUpdateProgress(int totalLength, int completedLength) {

    }
});

sketchImageView.displayImage("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg");
```

有以下几点需要注意：
>* 由于大多数情况下我们不需要监听进度并且Display时监听回调需要在主线程中执行，所以为了避免浪费效率，Sketch将进度监听独立了出来，这样一来只有你设置了ProgressListener，Sketch才会处理进度回调；
>* 由于display时进度回调方法要通过Handler在主线程中执行，如果频繁回调的话对性能会有所影响，因此ImageDownloader默认限制在整个下载过程中只执行10次进度回调，也就是在10%、20%、30%...的时候执行回调方法，另外你也可以通过ImageDownloader.setProgressCallbackNumber(int)方法修改回调次数