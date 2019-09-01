# 通过 SketchCallback 处理 Sketch 的异常

[Sketch] 在运行的过程中会有各种各样的异常，你可以通过 [SketchCallback] 收到这些异常，然后将这些异常记录下来帮助解决问题

### 使用

实现 [SketchCallback] 接口定义你自己的 SketchCallback 如下：

```java
public class MySketchCallback extends SketchCallback {

    @Override
    public void onError(@NonNull SketchException e) {
        if(e instanceof DownloadException){
            DownloadException downloadException = (DownloadException) e;
            CrashReport.postCatchedException(new Exception("download error. uri=" + downloadException.getUri(), e));
        }
    }
}
```

然后通过 [Configuration] 使用即可，如下：

```java
Sketch.with(context).getConfiguration().setCllback(new MySketchCallback());
```

[Sketch]: ../../sketch/src/main/java/me/panpf/sketch/Sketch.java
[Configuration]: ../../sketch/src/main/java/me/panpf/sketch/Configuration.java
[SketchCallback]: ../../sketch/src/main/java/me/panpf/sketch/SketchCallback.java