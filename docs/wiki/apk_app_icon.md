# 显示 APK 文件或已安装 APP 的图标

`需要导入 sketch-extensions 模块`

### 显示 APK 文件的图标

首先在初始化 [Sketch] 时注册 [ApkIconBitmapDecoder]，如下：

```kotlin
class MyApplication : Application(), SketchConfigurator {

    override fun createSketchConfig(): Builder.() -> Unit = {
        components {
            addBitmapDecoder(ApkIconBitmapDecoder.Factory())
        }
    }
}
```

然后显示图片时传入 apk 文件的路径，如下：

```kotlin
imageView.displayImage("/sdcard/sample.apk")
```

### 显示已安装 APP 的图标

首先在初始化 [Sketch] 时注册 [AppIconUriFetcher] 和 [AppIconBitmapDecoder]，如下：

```kotlin
class MyApplication : Application(), SketchConfigurator {

    override fun createSketchConfig(): Builder.() -> Unit = {
        addFetcher(AppIconUriFetcher.Factory())
        components {
            addBitmapDecoder(AppIconBitmapDecoder.Factory())
        }
    }
}
```

然后使用 `newAppIconUri()` 函数创建专用 uri 显示，如下：

```kotlin
imageView.displayImage(newAppIconUri("com.github.panpf.sketch.sample", 1))
```

[Sketch]: ../../sketch/src/main/java/com/github/panpf/sketch/Sketch.kt

[AppIconBitmapDecoder]: ../../sketch-extensions/src/main/java/com/github/panpf/sketch/decode/AppIconBitmapDecoder.kt

[ApkIconBitmapDecoder]: ../../sketch-extensions/src/main/java/com/github/panpf/sketch/decode/ApkIconBitmapDecoder.kt

[AppIconUriFetcher]: ../../sketch-extensions/src/main/java/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt