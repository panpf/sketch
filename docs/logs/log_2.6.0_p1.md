* :hammer: hugeImageEnabled 合并到 zoomEnabled，现在只有 zoomEnabled 一个开关
* :art: 改进 ImageZoomer 的代码
* :hammer: SketchImageView.getImageZoomer() 重命名为 getZoomer()
* :hammer: HugeImageViewer 重命名为 BlockDisplayer
* :fire: SketchImageView.getHugeImageViewer() 移除，请用 getZoomer().getBlockDisplayer() 代替
* :hammer: Tile 重命名为 Block
* :hammer: ErrorTracker.onTileSortError() 重命名为 onBlockSortError
* :hammer: 所有 tile 相关的名字 全部改成了 block
* :hammer: UriModelRegistry 重命名为 UriModelManager
* :hammer: OptionsFilterRegistry 重命名为 OptionsFilterManager
* :hammer: HttpStack.ImageHttpResponse 重命名为 HttpStack.Response
* :hammer: HttpStack.getHttpResponse(String). 重命名为 HttpStack.getResponse(String)
* :hammer: HttpStack.ImageHttpResponse.getResponseCode() 重命名为 HttpStack.Response.getCode()
* :hammer: HttpStack.ImageHttpResponse.getResponseMessage() 重命名为 HttpStack.Response.getMessage()
* :hammer: HttpStack.ImageHttpResponse.getResponseHeadersString() 重命名为 HttpStack.Response.getHeadersString()
* :hammer: HttpStack.ImageHttpResponse.getResponseHeader() 重命名为 HttpStack.Response.getHeader()

sample app:
* :bug: 修复 UNSPLASH 页面没有加载完数据就切换到别的页面时崩溃的 BUG

待办：
* 重写 block_display.md ，然后在 zoom.md 中引用
