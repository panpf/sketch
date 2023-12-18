# 显示 APK 文件或已安装 APP 的图标

翻译：[English](apk_app_icon.md)

> [!IMPORTANT]
> 必须导入 `sketch-extensions-view` 或 `sketch-extensions-compose` 模块

### 显示 APK 文件的图标

首先，注册 [ApkIconBitmapDecoder]，如下：

```kotlin
/* 为所有 ImageRequest 注册 */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                supportApkIcon()
            }
        }.build()
    }
}

/* 为单个 ImageRequest 注册 */
imageView.displayImage("/sdcard/sample.apk") {
  components {
    supportApkIcon()
  }
}
```

然后，显示图片时传入 apk 文件的路径，如下：

```kotlin
imageView.displayImage("/sdcard/sample.apk")
```

### 显示已安装 APP 的图标

首先，注册 [AppIconUriFetcher] 和 [AppIconBitmapDecoder]，如下：

```kotlin
/* 为所有 ImageRequest 注册 */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                supportAppIcon()
            }
        }.build()
    }
}

/* 为单个 ImageRequest 注册 */
imageView.displayImage(newAppIconUri("com.github.panpf.sketch.sample", versionCode = 1)) {
  components {
    supportAppIcon()
  }
}
```

然后使用 `newAppIconUri()` 函数创建专用 uri 并执行显示，如下：

```kotlin
imageView.displayImage(newAppIconUri("com.github.panpf.sketch.sample", versionCode = 1))
```

* versionCode：App 的版本号，必须传入正确的版本号。因为对图标进行修改时就会将修改后的图标缓存在磁盘上，如果只用 packageName 作为缓存 key 那么 App
  版本更新后图标即使改变了缓存也不会刷新

[Sketch]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/Sketch.kt

[AppIconBitmapDecoder]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/decode/AppIconBitmapDecoder.kt

[ApkIconBitmapDecoder]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/decode/ApkIconBitmapDecoder.kt

[AppIconUriFetcher]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt