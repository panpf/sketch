# 加载 Apk 或已安装 App 的图标

翻译：[English](apk_app_icon.md)

## 加载 Apk 图标

先配置 `sketch-extensions-apkicon` 模块的依赖

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-extensions-apkicon:${LAST_VERSION}")
```

然后加载图片时直接传入 apk 文件的路径即可：

```kotlin
sketch.enqueue(ImageRequest(context, uri = "/sdcard/sample.apk"))
```

> [!IMPORTANT]
> 1. `sketch-extensions-apkicon`
     模块支持自动注册组件，有关组件注册的详细内容请查看文档：[《注册组件》](register_component_zh.md)
> 2. 仅支持 Android 平台

## 加载已安装 App 的图标

先配置 `sketch-extensions-appicon` 模块的依赖

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-extensions-appicon:${LAST_VERSION}")
```

然后加载图片时使用 `newAppIconUri()` 函数创建专用 uri 即可

```kotlin
// app.icon://com.github.panpf.sketch.sample/1
val appIconUri = newAppIconUri(packageName = "com.github.panpf.sketch.sample", versionCode = 1)
sketch.enqueue(ImageRequest(context, uri = appIconUri))
```

> [!IMPORTANT]
> 1. versionCode：App 的版本号。必须传入正确的版本号，因为对图标进行修改时就会将修改后的图标缓存在磁盘上，如果只用
     packageName 作为缓存 key 那么 App 版本更新后图标即使改变了缓存也不会刷新
> 2. `sketch-extensions-appicon`
     模块支持自动注册组件，有关组件注册的详细内容请查看文档：[《注册组件》](register_component_zh.md)
> 3. 仅支持 Android 平台

[comment]: <> (classs)

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/

[ApkIconDecoder]: ../../sketch-extensions-apkicon/src/main/kotlin/com/github/panpf/sketch/decode/ApkIconDecoder.kt

[AppIconUriFetcher]: ../../sketch-extensions-appicon/src/main/kotlin/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt
