# 更新日志

翻译：[English](CHANGELOG.md)

# 4.0.0-alpha01

> [!CAUTION]
> 1. 4.x 版本为兼容 Compose Multiplatform 而进行了大量破坏性重构和简化，不兼容 3.x 版本
> 2. maven groupId 升级为 `io.github.panpf.sketch4`，因此 2.\*、3.\* 版本不会提示升级

### sketch-core

* 不再区分 Display、Load、Download，现在只有一个 ImageRequest、ImageResult、ImageListener
* 移除 BitmapPool 以及和它相关的 disallowReuseBitmap 属性、CountBitmap、SketchCountBitmapDrawable 类
* ImageResult 的 requestKey 属性被移除、requestCacheKey 属性重命名为 cacheKey
* SketchSingleton 重构为 SingletonSketch
* SketchDrawable 的 imageUri、requestKey、requestCacheKey、imageInfo、dataFrom、transformedList、extras 等属性被移除，现在请从 ImageResult 中获取它们
* 现在 Target、ImageResult、DecodeResult 都使用 Image

### sketch-compose


### other

* Android 最低 API 升到了 API 21
