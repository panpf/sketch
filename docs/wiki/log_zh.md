# 日志

翻译：[English](log.md)

[Sketch] 的日志由 [Logger] 组件提供服务，Tag 统一为 `Sketch`

### 修改 Level

和大多数日志框架一样，[Logger] 也通过 [Logger].Level 控制输出日志的级别，默认为 `Info`

你可以任何时候修改 level，如下：

```kotlin
// 在初始哈 Sketch 时
Sketch.Builder(this).apply {
    logger(level = Logger.Level.Debug)
}.build()

// 在其它任何时候
context.sketch.logger.level = Logger.Level.Debug
```

> [!TIP]
> 过多的日志会影响 UI 流畅度，正式发布版本请将 level 设置为 Info 及以上级别

### 修改输出

[Logger] 通过 [Logger].Pipeline 接口输出日志，在 Android 平台上 [Logger].Pipeline
的实现是 [AndroidLogPipeline]，非 Android 平台上是 [PrintLogPipeline]

你可以实现 [Logger].Pipeline 接口自定义新的输出，然后在初始化 [Sketch] 时使用它，如下：

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

Sketch.Builder(this).apply {
    logger(pipeline = MyPipeline())
}.build()
```

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.kt

[Logger]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/util/Logger.kt

[AndroidLogPipeline]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/util/Logger.android.kt

[PrintLogPipeline]: ../../sketch-core/src/nonAndroidMain/kotlin/com/github/panpf/sketch/util/Logger.nonAndroid.kt