# 迁移

翻译：[English](migrate.md)

[sketch3compat] 目录下提供了一些兼容 sketch3 API 的工具函数、工具类、以及别名类，可以帮助你更容易的适配
sketch4，你可以直接拷贝他们到你的项目中。

* DownloadRequest：可以使用 `Sketch.executeDownload(ImageRequest)` 或
  `Sketch.enqueueDownload(ImageRequest)` 替代
* LoadRequest：不设置 `target` 即可

其它迁移细节正在陆续完善中

[sketch3compat]: ../../sample/src/androidMain/kotlin/com/github/panpf/sketch/sample/util/sketch3compat