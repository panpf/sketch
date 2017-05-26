load()和download()还支持同步执行，只需调用sync()方法开启即可：
```java
Sketch.with(context).load("http://t.cn/RShdS1f", new LoadListener() {
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
    public void onError(ErrorCause errorCause) {

    }

    @Override
    public void onCanceled(CancelCause cancelCause) {

    }
}).maxSize(100, 100).sync().commit();
```

需要注意：
>* 不能在主线程开启同步，否则会运行时抛异常
>* 不仅下载、加载会在当前线程执行，listener也会在当前线程回调
