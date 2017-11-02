这是一个重大重构版本，重点重构了 [ImageZoomer]、HugeImageViewer 以及将包名改为 me.panpf.sketch  

:bug: 不能无痛升级 :bug:，请参考页尾的升级指南

### Sketch

修复 bug：
* :bug: 修复开启解码 gif 后内存缓存失效的 bug
* :bug: 修复在生成 [WrappedImageProcessor] 的 key 时崩溃的错误

包名重构：
* :hammer: sketch library 的包名改为 me.panpf.sketch
* :hammer: sketch-gif library 的包名改为 me.panpf.sketch.gif

ImageZoomer 重构：
* :hammer: 重构 [ImageZoomer] 的代码结构，现在逻辑更清晰易懂
* :hammer: [SketchImageView].getImageZoomer() 重命名为 getZoomer()
* :hammer: [ImageZoomer] 从 me.xiaopan.sketch.viewfun.zoom 移动到 me.xiaopan.sketch.zoom

HugeImageViewer 重构：
* :hammer: hugeImageEnabled 合并到 zoomEnabled，现在只有 zoomEnabled 一个开关
* :hammer: HugeImageViewer 从 me.xiaopan.sketch.viewfun.huge 移动到 me.xiaopan.sketch.zoom
* :hammer: HugeImageViewer 重命名为 [BlockDisplayer]
* :hammer: 移除 [SketchImageView].getHugeImageViewer() 方法，请用 [SketchImageView].getZoomer().getBlockDisplayer() 代替
* :hammer: Tile 重命名为 [Block]，并且所有 tile 相关的名字 全部改成了 block
* :hammer: [ErrorTracker].onTileSortError() 重命名为 onBlockSortError()

其它重构：
* :hammer: UriModelRegistry 重命名为 [UriModelManager]
* :hammer: OptionsFilterRegistry 重命名为 [OptionsFilterManager]
* :hammer: [HttpStack].ImageHttpResponse 重命名为 [HttpStack].Response
* :hammer: [HttpStack].getHttpResponse(String). 重命名为 [HttpStack].getResponse(String)
* :hammer: [HttpStack].ImageHttpResponse.getResponseCode() 重命名为 [HttpStack].Response.getCode()
* :hammer: [HttpStack].ImageHttpResponse.getResponseMessage() 重命名为 [HttpStack].Response.getMessage()
* :hammer: [HttpStack].ImageHttpResponse.getResponseHeadersString() 重命名为 [HttpStack].Response.getHeadersString()
* :hammer: [HttpStack].ImageHttpResponse.getResponseHeader() 重命名为 [HttpStack].Response.getHeader()
* :hammer: [ImageAttrs] 从 drawable 中移动到 decode 包中
* :hammer: 重构 [WrappedImageProcessor] 的 getKey() 和 toString() 方法，所有子类都需要重新适配

改进：
* :art: 优化 key 的格式，现在 key 的格式更加短小，并且在控制台点击跳转到浏览器后不用对 uri 做任何修改即可显示图片，但旧的已处理缓存 key 和其他的需要根据 key 缓存的磁盘缓存将全部失效

### Sample App

修复 bug:
* :bug: 修复 UNSPLASH 页面没有加载完数据就切换到别的页面时崩溃的 BUG

重构：
* :hammer: 重构包名为 me.panpf.sketch.sample


### 升级指南

* 全局搜索 `me.xiaopan.sketch.viewfun.huge.HugeImageViewer` 并替换为 `me.xiaopan.sketch.zoom.BlockDisplayer`
* 全局区分大小写搜索 `HugeImageViewer` 并替换为 `BlockDisplayer`
* 全局搜索 `me.xiaopan.sketch.viewfun.zoom.ImageZoomer` 并替换为 `me.xiaopan.sketch.zoom.ImageZoomer`
* 全局搜索 `me.xiaopan.sketch` 替换为 `me.panpf.sketch`

[ImageZoomer]: ../../sketch/src/main/java/me/panpf/sketch/zoom/ImageZoomer.java
[BlockDisplayer]: ../../sketch/src/main/java/me/panpf/sketch/zoom/BlockDisplayer.java
[Block]: ../../sketch/src/main/java/me/panpf/sketch/zoom/block/Block.java
[SketchImageView]: ../../sketch/src/main/java/me/panpf/sketch/SketchImageView.java
[WrappedImageProcessor]: ../../sketch/sketch/src/main/java/me/panpf/sketch/process/WrappedImageProcessor.java
[ErrorTracker]: ../../sketch/sketch/src/main/java/me/panpf/sketch/ErrorTracker.java
[UriModelManager]: ../../sketch/src/main/java/me/panpf/sketch/uri/UriModelManager.java
[OptionsFilterManager]: ../../sketch/src/main/java/me/panpf/sketch/optionsfilter/OptionsFilterManager.java
[HttpStack]: ../../sketch/src/main/java/me/panpf/sketch/http/HttpStack.java
[ImageAttrs]: ../../sketch/src/main/java/me/panpf/sketch/decode/ImageAttrs.java