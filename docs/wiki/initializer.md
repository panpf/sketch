Initializer 用来延迟配置 Sketch，具体会延迟到你第一次使用Sketch的时候

#### 设计的初衷

通常我们在开发一款app的时候会依赖很多的第三方 sdk 或 library ，大部分的 sdk 或 library 都需要你在 Application 的 onCreate() 方法中初始化它，这样一来就会增加 onCreate() 方法的耗时，进而影响app的启动速度，如下：

```java
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MockAgent.init(this);
        Glide.init(this);
    }
}
```

那么最理想的初始化 sdk 或 library 的时机就应该是用到某个 sdk 或 library 的时候

虽然 Sketch 本身不需要任何初始化操作就可以使用，但仍免不了用户会有些自定义的配置需要第一时间设置生效，因此 Sketch 就提供了 Initializer 用来实现延迟配置

#### 使用方式

1.定义你自己的初始化类，并实现Initializer接口，如下：

```java
public class MyInitializer implements Initializer {

    @Override
    public void onInitialize(Context context, Sketch sketch, Configuration configuration) {
        configuration.getImagePreprocessor().addPreprocessor(new VideoIconPreprocessor());
        configuration.setErrorTracker(new MyErrorTracker(context));
    }
}
```

2.在AndroidManifest.xml中定义meta-data，如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest>
    ...
    <application>
        ...
        <meta-data
            android:name="me.xiaopan.sketchsample.MyInitializer"
            android:value="SKETCH_INITIALIZER" />
    </application>
</manifest>
```

注意：
* 类名要定义在 name 属性上
* value 属性值固定是 SKETCH_INITIALIZER
* sketch 的 aar 中已经包含了对所有 Initializer 子类的混淆忽略配置
