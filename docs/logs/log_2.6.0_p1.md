* :hammer: hugeImageEnabled 合并到 zoomEnabled，现在只有 zoomEnabled 一个开关
* :art: 改进 ImageZoomer 的代码
* :hammer: SketchImageView.getImageZoomer() 重命名为 getZoomer()
* :hammer: HugeImageViewer 重命名为 BlockDisplayer
* :fire: SketchImageView.getHugeImageViewer() 移除，请用 getZoomer().getBlockDisplayer() 代替

sample app:
* :bug: 修复 UNSPLASH 页面没有加载完数据就切换到别的页面时崩溃的 BUG
