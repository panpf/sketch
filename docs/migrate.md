# Migrate

Translations: [简体中文](migrate.zh.md)

The [sketch3compat] directory provides some utility functions, utility classes, and alias classes
that are compatible with the sketch3 API, which can help you adapt to sketch4 more easily. You can
copy them directly into your project.

* DownloadRequest: You can use `Sketch.executeDownload(ImageRequest)` or
  `Sketch.enqueueDownload(ImageRequest)` instead
* LoadRequest: Just don’t set `target`

Other migration details are being gradually improved.

[sketch3compat]: ../samples/androidApp/src/main/kotlin/com/github/panpf/sketch/sample/util/sketch3compat