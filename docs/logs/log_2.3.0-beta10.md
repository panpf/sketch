ImageProcessor：
>* :sparkles: 增加MaskImageProcessor可以给任意形状的PNG图片加上一层遮罩颜色
>* :sparkles: 增加WrappedImageProcessor可以将任意不同的ImageProcessor组合在一起使用
>* :bug: 修复GaussianBlurImageProcessor生成的key无法区分不同的darkColor的Bug
>* :art: 删除所有ImageDisplayer的setAlwaysUse(boolean)方法，改为在构造函数中设置
>* :art: GaussianBlurImageProcessor的所有构造函数改为私有的，只能通过静态方法创建

Rename：
>* :art: SketchImageView.getOptionsId()改名为getOptionsKey()
>* :art: SketchUtils.makeRequestId(String, DownloadOptions)改名为make请求Key(String, DownloadOptions)
>* :art: SketchUtils.makeRequestId(String, String)改名为make请求Key(String, String)
>* :art: SketchUtils.makeStateImageRequestId(String, DownloadOptions)改名为makeStateImageMemoryCacheKey(String, DownloadOptions)

Other：
>* :zap: 优化inSampleSize计算逻辑，防止超过OpenGL所允许的最大尺寸
