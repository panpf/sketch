### :sparkles: 纠正图片方向功能
* 新增纠正图片方向功能 [了解更多](https://github.com/xiaopansky/sketch/blob/master/docs/wiki/correct_image_orientation.md)
* 默认关闭，通过correctImageOrientation属性开启
* 一般的图片支持自动纠正方向，分块显示超大图也支持
* 可通过SketchDrawable.getOrientation()方法获得图片的方向

### ImageProcessor：
* WrapableImageProcessor重命名为WrappedImageProcessor

### 其它
* ImageFormat重命名为ImageType
* 最低API提升至10

### Sample APP：
* 图片详情页右下角设置按钮改为长按
* 图片详情页点击显示底部四个按钮挪到了长按--更多功能里
* 图片详情页点击关闭页面
