# 日志

Translations: [简体中文](log_zh.md)

[Sketch] logs are provided by the [Logger] component. By default, android.util.Log is used for
output, and the Tag is unified to `Sketch`

### Modify Level

Like android.util.Log, [Logger] also supports Level, the default is `INFO`

You can modify it when initializing [Sketch], as follows:

```kotlin
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            logger(Logger(Logger.Level.DEBUG))
        }.build()
    }
}
```

You can also provide options in the App settings to modify them at any time, as follows:

```kotlin
context.sketch.logger.level = Logger.Level.DEBUG
```

> Note: Excessive Log logs will affect UI fluency. Please set the level to INFO and above for the
> official release version.

### Modify output

[Logger] outputs to android.util.Log by default. You can implement the [Logger].Proxy interface to
customize new output, and then modify it when initializing [Sketch], as follows:

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

class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            logger(Logger(Logger.Level.DEBUG, MyProxy()))
        }.build()
    }
}
```

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.kt

[Logger]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/util/Logger.kt