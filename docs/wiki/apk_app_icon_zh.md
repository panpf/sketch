# 加载 Apk 或已安装 App 的图标

翻译：[English](apk_app_icon.md)

> [!IMPORTANT]
> 1. 必须导入 `sketch-extensions-view` 或 `sketch-extensions-compose` 模块
> 2. 仅支持 Android 平台

### 加载 Apk 的图标

```kotlin
// 在自定义 Sketch 时为所有 ImageRequest 注册
val sketch = Sketch.Builder(context).apply {
    components {
        supportApkIcon()
    }
}.build()
// 然后加载图片时传入 apk 文件的路径即可
sketch.enqueue(ImageRequest(context, uri = "/sdcard/sample.apk"))

// 或者加载图片时为单个 ImageRequest 注册
ImageRequest(context, uri = "/sdcard/sample.apk") {
    components {
        supportApkIcon()
    }
}
```

### 加载已安装 App 的图标

```kotlin
// 在自定义 Sketch 时为所有 ImageRequest 注册
val sketch = Sketch.Builder(context).apply {
    components {
        supportAppIcon()
    }
}.build()
// 然后加载图片时使用 `newAppIconUri()` 函数创建专用 uri 即可
sketch.enqueue(ImageRequest(context, uri = newAppIconUri("com.github.panpf.sketch.sample", versionCode = 1)))

// 或者加载图片时为单个 ImageRequest 注册
ImageRequest(context, uri = newAppIconUri("com.github.panpf.sketch.sample", versionCode = 1)) {
    components {
        supportAppIcon()
    }
}
```

* versionCode：App 的版本号。必须传入正确的版本号，因为对图标进行修改时就会将修改后的图标缓存在磁盘上，如果只用
  packageName 作为缓存 key 那么 App 版本更新后图标即使改变了缓存也不会刷新

[comment]: <> (classs)


[ApkIconDecoder]: ../../sketch-extensions-core/src/androidMain/kotlin/com/github/panpf/sketch/decode/ApkIconDecoder.kt

[AppIconUriFetcher]: ../../sketch-extensions-core/src/androidMain/kotlin/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt
