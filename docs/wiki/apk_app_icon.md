# 显示 APK 文件或已安装 APP 的图标

`需要导入 sketch-extensions 模块`

### 显示 APK 文件的图标

首先在初始化 [Sketch] 时注册 [ApkIconBitmapDecoder]，这样所有的 [ImageRequest] 都可以使用，如下：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                supportApkIcon()
            }
        }.build()
    }
}
```

然后显示图片时传入 apk 文件的路径，如下：

```kotlin
imageView.displayImage("/sdcard/sample.apk")
```

或者在显示图片时只给当前 [ImageRequest] 注册，这样就只有当前 [ImageRequest] 可以使用，如下：

```kotlin
imageView.displayImage("/sdcard/sample.apk") {
    components {
        supportApkIcon()
    }
}
```

### 显示已安装 APP 的图标

首先在初始化 [Sketch] 时注册 [AppIconUriFetcher] 和 [AppIconBitmapDecoder]，这样所有的 [ImageRequest] 都可以使用，如下：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                supportAppIcon()
            }
        }.build()
    }
}
```

然后使用 `newAppIconUri()` 函数创建专用 uri 并执行显示，如下：

```kotlin
imageView.displayImage(newAppIconUri("com.github.panpf.sketch.sample", versionCode = 1))
```

或者在显示图片时只给当前 [ImageRequest] 注册，这样就只有当前 [ImageRequest] 可以使用，如下：

```kotlin
imageView.displayImage(newAppIconUri("com.github.panpf.sketch.sample", versionCode = 1)) {
    components {
        supportAppIcon()
    }
}
```

* versionCode：App 的版本号，必须传入正确的版本号。因为对图标进行修改时就会将修改后的图标缓存在磁盘上，如果只用 packageName 作为缓存 key 那么 App
  版本更新后图标即使改变了缓存也不会刷新

[Sketch]: ../../sketch-core/src/main/java/com/github/panpf/sketch/Sketch.kt

[AppIconBitmapDecoder]: ../../sketch-extensions-core/src/main/java/com/github/panpf/sketch/decode/AppIconBitmapDecoder.kt

[ApkIconBitmapDecoder]: ../../sketch-extensions-core/src/main/java/com/github/panpf/sketch/decode/ApkIconBitmapDecoder.kt

[AppIconUriFetcher]: ../../sketch-extensions-core/src/main/java/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt

[ImageRequest]: ../../sketch-core/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt