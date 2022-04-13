# v3.0.0-alpha01

全新版本，新的开始

* change: maven groupId 改为 `io.github.panpf.sketch3`，因此 2.\* 版本不会提示升级
* change: 包名改为 `com.github.panpf.sketch` 因此与 2.\* 版本不会冲突
* change: 基于 kotlin 协程重写，API、功能实现全部改变，当一个新的库用就行
* improve: 不再要求必须使用 SketchImageView，任何 ImageView 及其子类都可以，甚至结合自定义 Target 可以支持任意 View
* improve: Zoom 模块的分块显示超大图功能重构并且支持多线程，因此速度更快更高效
* new: 新增支持 SVG
* new: 新增支持 Jetpack Compose
* new: 支持拦截请求和图片解码

> 参考 [coil] 2.0.0-alpha05 版本并结合 sketch 原有功能实现，[coil] 最低支持 API 21，而 sketch 最低支持 API 16


[coil]: https://github.com/coil-kt/coil
