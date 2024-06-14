# Log

Translations: [简体中文](log_zh.md)

The log of [Sketch] is provided by the [Logger] component, and the tag is unified as `Sketch`

### Modify Level

Like most logging frameworks, [Logger] also controls the level of output logs through [Logger]
.Level, which defaults to `Info`

You can modify the level at any time, as follows:

```kotlin
// When initializing Sketch
Sketch.Builder(context).apply {
    logger(level = Logger.Level.Debug)
}.build()

// At any other time
context.sketch.logger.level = Logger.Level.Debug
```

> [!TIP]
> Excessive logs will affect UI fluency. For the official release version, please set the level to
> Info and above.

### 修改输出

[Logger] Output logs through the [Logger].Pipeline interface, on the Android platform [Logger]
.Pipeline
The implementation is [AndroidLogPipeline], on non-Android platforms it is [PrintLogPipeline]

You can implement the [Logger].Pipeline interface to customize new output, and then use it when
initializing [Sketch], as follows:

```kotlin
class MyPipeline : Logger.Pipeline {

    override fun log(level: Logger.Level, tag: String, msg: String, tr: Throwable?) {
        if (tr != null) {
            val trString = tr.stackTraceToString()
            println("$level. $tag. $msg. \n$trString")
        } else {
            println("$level. $tag. $msg")
        }
    }

    override fun flush() {

    }

    override fun toString(): String = "MyPipeline"
}

Sketch.Builder(context).apply {
    logger(pipeline = MyPipeline())
}.build()
```

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[Logger]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/util/Logger.common.kt

[AndroidLogPipeline]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/util/Logger.android.kt

[PrintLogPipeline]: ../../sketch-core/src/nonAndroidMain/kotlin/com/github/panpf/sketch/util/Logger.nonAndroid.kt