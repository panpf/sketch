# 通过 ErrorTracker 监控 Sketch 的异常

[Sketch] 在运行的过程中会有各种各样的异常，你可以通过 [ErrorTracker] 收到这些异常，然后将这些异常记录下来帮助解决问题

### 使用

[ErrorTracker] 默认实现只是将收到的异常打印在 log cat 中，因此首先你要继承 [ErrorTracker] 并重写你要记录的异常对应的方法，如下：

```java
public class MyErrorTracker extends ErrorTracker {

    public MyErrorTracker(Context context) {
        super(context);
    }

    @Override
    public void onBlockSortError(IllegalArgumentException e, List<Block> blockList, boolean useLegacyMergeSort) {
        super.onBlockSortError(e, blockList, useLegacyMergeSort);
        String message = (useLegacyMergeSort ? "useLegacyMergeSort. " : "") + SketchUtils.blockListToString(blockList);
        CrashReport.postCatchedException(new Exception(message, e));
    }
}
```

然后通过 [Configuration] 使用即可，如下：
```java
Sketch.with(context).getConfiguration().setErrorTracker(new MyErrorTracker());
```

[Sketch]: ../../sketch/src/main/java/me/xiaopan/sketch/Sketch.java
[Configuration]: ../../sketch/src/main/java/me/xiaopan/sketch/Configuration.java
[ErrorTracker]: ../../sketch/src/main/java/me/xiaopan/sketch/ErrorTracker.java