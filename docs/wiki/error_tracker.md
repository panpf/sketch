Sketch在运行的过程中会有各种各样的异常，你可以通过ErrorTracker收到这些异常，然后将这些异常记录下来帮助解决问题

#### 使用

ErrorTracker默认实现只是将收到的异常信息打印在log cat中，你要记录异常信息的话首先你需要继承ErrorTracker修改
```java
public class MyErrorTracker extends ErrorTracker {

    public MyErrorTracker(Context context) {
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

然后通过Configuration设置即可，如下：
```java
Sketch.with(context).getConfiguration().setErrorTracker(new MyErrorTracker());
```
