重命名：
* ImageInfo重命名为ImageAttrs，并将当中的key和uri移出，现在其定位为图片的属性
* ImageFormat重命名为ImageType，LargeImageViewer.getImageFormat()方法重命名为getImageType()
* WrapableImageProcessor重命名为WrappedImageProcessor
* LoadingDrawable重命名为SketchLoadingDrawable
* RefBitmapDrawable重命名为SketchRefBitmapDrawable
* ShapeBitmapDrawable重命名为SketchShapeBitmapDrawable
* RefBitmap重命名为SketchRefBitmap
* RefDrawable重命名为SketchRefDrawable

重构：
* 移除DefaultImageDecoder，现在ImageDecoder是一个class可以直接使用，如果你有自定义实现ImageDecoder的现在需要直接继承ImageDecoder
* DisplayListener.onCompleted(ImageFrom, String)参数改为DisplayListener.onCompleted(Drawable, ImageFrom, ImageAttrs)
* ImagePreprocessor重构

变化：
* DisplayHelper.options()、LoadHelper.options()、DownloadHelper.options()内部处理由合并改为完全覆盖

新功能：
* 新增自动纠正图片方向功能 [点击了解更多](../wiki/correct_image_orientation.md)
* ImageAttrs中新增exifOrientation属性，存储图片方向
* SketchImageView增加redisplay()方法可在需要的时候重走显示流程

SketchMonitor：
* onInBitmapException(String, int, int, int, Bitmap)方法改为onInBitmapDecodeError(String, int, int, String, Throwable, int, Bitmap)
* 删除onInBitmapExceptionForRegionDecoder(String, int, int, Rect, int, Bitmap)方法
* 新增onDecodeRegionError(String, int, int, String, Throwable, Rect, int)方法
* 改名为ErrorTracker

最低API提升至10

其它：
* 优化由inBitmap导致的解码失败的情况的判断

Sample：
* 图片详情页右下角设置按钮改为长按
* 图片详情页点击显示底部四个按钮挪到了长按--更多功能里
* 图片详情页点击关闭页面
* 增纠正图片方向功能示例和开关控制


待办：
* 增加了base64图片支持，log，文档都得加
* 增加自动纠正图片方向的测试页面
* 重构了ImagePreprocessor的实现，有重写需求的需要从新适配
