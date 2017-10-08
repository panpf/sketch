bugs：
* :bug: 修复由于混淆了 Sketch.onTrimMemory 和 Sketch.onLowMemory 方法导致其内部调用过滤失效的 bug

重构：
* :hammer: key 中不再包含 decodeGifImage ，受此影响已经通过 cacheProcessedImageInDisk 生成的磁盘缓存将全部失效

sample app：
* :bug: 修复在 MyPhotos 页面如果数据量超大的话就会崩溃的 bug
* :lipstick: 重构图片详情页的长按菜单
* :lipstick: 兼容 MIX 2 的全屏显示
