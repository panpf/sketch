# 日志

翻译：[English](log.md)

[Sketch] 的日志由 [Logger] 组件提供服务，默认使用 android.util.Log 输出，Tag 统一为 `Sketch`

### 修改 Level

和 android.util.Log 一样，[Logger] 也支持 Level，默认为 `INFO`

你可以在初始化 [Sketch] 时修改，如下：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            logger(Logger(Logger.Level.DEBUG))
        }.build()
    }
}
```

也可以在App 的设置中提供选项随时修改，如下：

```kotlin
context.sketch.logger.level = Logger.Level.DEBUG
```

> 注意：过多的 Log 日志会影响 UI 流畅度，正式发布版本请将 level 设置为 INFO 及以上级别

### 修改输出

[Logger] 默认输出到 android.util.Log，你可以实现 [Logger].Proxy
接口自定义新的输出，然后在初始化 [Sketch] 时修改，如下：

```kotlin
class MyProxy : Logger.Proxy {
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

class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            logger(Logger(Logger.Level.DEBUG, MyProxy()))
        }.build()
    }
}
```

[Sketch]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/Sketch.kt

[Logger]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/util/Logger.kt