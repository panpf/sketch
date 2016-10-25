Sketch在运行的过程中会有各种各样的异常，你可以通过ExceptionMonitor收到这些异常，然后将这些异常记录下来帮助解决问题

包含以下方法：
>* `onInstallDiskCacheError(Exception, File)`：安装DiskLruCache异常，但Sketch还是可以正常运行的。一旦失败了这个方法就会被频繁回调，这是因为每一次需要用到DiskCache的时候都会尝试恢复，再次失败就会再次回调
，因此你在记录日志的时候不必每次失败都记，可以每隔一个小时记一次
>* `onDecodeGifImageError(Throwable, LoadRequest, int, int, String)`：解码gif图片异常，你需要过滤一下Throwable。如果是UnsatisfiedLinkError或ExceptionInInitializerError就是找不到libpl_droidsonroids_gif.so，这个时候你就要记录一下当前设备的abi类型，Sketch中包含的libpl_droidsonroids_gif.so已经覆盖了全平台了，如果还找不到就说明有其他的问题了
>* `onDecodeNormalImageError(Throwable, LoadRequest, int, int, String)`：解码普通图片异常
>* `onProcessImageError(Throwable, String, ImageProcessor)`：处理图片异常
>* `onDownloadError(DownloadRequest, Throwable)`：下载异常
>* `onTileSortError(IllegalArgumentException, List<Tile>, boolean)`：分块显示超大图功能碎片排序异常

#### 使用

ExceptionMonitor默认实现只是将收到的异常信息打印在log cat中，你要记录异常信息的话首先你需要继承ExceptionMonitor修改
```java
public class MyExceptionMonitor extends ExceptionMonitor {

    public MyExceptionMonitor(Context context) {
        super(context);
    }

    @Override
    public void onTileSortError(IllegalArgumentException e, List<Tile> tileList, boolean useLegacyMergeSort) {
        super.onTileSortError(e, tileList, useLegacyMergeSort);
        String message = (useLegacyMergeSort ? "useLegacyMergeSort. " : "") + SketchUtils.tileListToString(tileList);
        CrashReport.postCatchedException(new Exception(message, e));
    }
}
```

然后通过Configuration的setExceptionMonitor(ExceptionMonitor)方法应用即可

```java
Sketch.with(context).getConfiguration().setExceptionMonitor(new MyExceptionMonitor());
```
