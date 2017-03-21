#### GIF
* :arrow_up: 升级android-gif-drawable版本到1.2.6
* :hammer: 拆分gif模块，现在作为一个单独的library对外提供，依赖方式请参考[README](../../README.md)
* :sparkles: SketchGifDrawable新增followPageVisible(boolean, boolean)方法用于方便的实现页面不可见时停止播放gif功能，[点击查看具体用法](../wiki//display_gif_image.md)

##### Other
* :bug: 修复使用在DiskLruCache中SLog.w(String, String, Object ...)方法打印日志时，如果日内容中包含"3A"这样的字符就崩溃的BUG