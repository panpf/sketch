# 日志

[Sketch] 的日志由 [Logger] 组件提供服务，默认使用 android.util.Log 输出，Tag 统一为 `Sketch`

### 修改 Level

和 android.util.Log 一样，[Logger] 也支持 Level，默认 为 INFO，你可以在初始化 [Sketch] 时修改，如下：

```kotlin
class MyApplication : Application(), SketchConfigurator {

    override fun createSketchConfig(): Builder.() -> Unit = {
        logger(Logger(Logger.Level.DEBUG))
    }
}
```

### 修改输出

[Logger] 默认输出到 android.util.Log，你可以实现 [Logger].Proxy 接口自定义新的输出，然后在初始化 [Sketch] 时修改，如下：

```kotlin
class MyProxy : Proxy {
    override fun v(tag: String, msg: String, tr: Throwable?) {
        Log.v(tag, msg, tr)
    }

    override fun d(tag: String, msg: String, tr: Throwable?) {
        Log.d(tag, msg, tr)
    }

    override fun i(tag: String, msg: String, tr: Throwable?) {
        Log.i(tag, msg, tr)
    }

    override fun w(tag: String, msg: String, tr: Throwable?) {
        Log.w(tag, msg, tr)
    }

    override fun e(tag: String, msg: String, tr: Throwable?) {
        Log.e(tag, msg, tr)
    }

    override fun flush() {

    }

    override fun toString(): String = "MyProxy"
}

class MyApplication : Application(), SketchConfigurator {

    override fun createSketchConfig(): Builder.() -> Unit = {
        logger(Logger(Logger.Level.DEBUG, MyProxy()))
    }
}
```

[Sketch]: ../../sketch/src/main/java/com/github/panpf/sketch/Sketch.kt

[Logger]: ../../sketch/src/main/java/com/github/panpf/sketch/util/Logger.kt