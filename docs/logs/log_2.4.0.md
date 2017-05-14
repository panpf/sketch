重构：
* ImageInfo重命名为ImageAttrs，并将当中的key和uri移出，现在其定位为图片的属性
* ImageFormat重命名为ImageType，LargeImageViewer.getImageFormat()方法重命名为getImageType()
* ImageAttrs中新增orientationDegrees属性，存储图片旋转角度
* WrapableImageProcessor重命名为WrappedImageProcessor
* 移除DefaultImageDecoder，现在ImageDecoder是一个class可以直接使用

新功能：
* load和display的Options以及Helper新增correctImageOrientation属性可让被旋转了的图片以正常方向显示，[点击了解更多](../wiki/correct_image_orientation.md)

SketchMonitor：
* onInBitmapException(String, int, int, int, Bitmap)方法改为onInBitmapDecodeError(String, int, int, String, Throwable, int, Bitmap)
* 删除onInBitmapExceptionForRegionDecoder(String, int, int, Rect, int, Bitmap)方法
* 新增onDecodeRegionError(String, int, int, String, Throwable, Rect, int)方法
* 改名为ErrorTracker

* 最低API提升至10

其它：
* 优化由inBitmap导致的解码失败的情况的判断

Sample：
* 图片详情页右下角设置按钮改为长按
* 图片详情页点击显示底部四个按钮挪到了长按--更多功能里
* 图片详情页点击关闭页面
