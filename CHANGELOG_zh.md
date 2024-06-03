# 更新日志

翻译：[English](CHANGELOG.md)

# New 4.0

* SketchSingleton 重构为 SingletonSketch
* DisplayRequest、LoadRequest、DownloadRequest 合并为 ImageRequest
* DisplayResult、LoadResult、DownloadResult 合并为 ImageResult
* DisplayResult、LoadResult、DownloadResult 合并为 ImageResult
* ImageResult 的 requestKey 属性被移除、requestCacheKey 属性重命名为 cacheKey
* 移除 BitmapPool 以及和它相关的 disallowReuseBitmap 属性、CountBitmap、SketchCountBitmapDrawable 类