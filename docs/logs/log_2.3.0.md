Fixed bugs:
>* [#4](https://github.com/xiaopansky/Sketch/issues/4) 修复由于在内存中缓存了Drawable，导致同一个缓存Drawable在两个不同的地方使用时bounds被改变从而图片大小异常显示的BUG，常见的BUG表现为，点击图片进入图片详情页后再回来发现图片变小了
>* [#11](https://github.com/xiaopansky/Sketch/issues/11) 修复最后一条磁盘缓存无效的BUG，这是DiskLruCache的BUG，因为在commit的时候没有持久化操作记录导致的
>* [#13](https://github.com/xiaopansky/Sketch/issues/13) 修复SketchBitmapDrawable由于没有设置TargetDensity而始终以160的默认像素密度来缩小图片的BUG，最终导致通过getIntrinsicWidth()得到的尺寸始终比Bitmap实际尺寸小

New features：
>* ``Decode``. [缩略图模式](../wiki/thumbnail_mode.md)，通过缩略图模式你可以在列表中更加清晰的显示那些宽高相差特别大的图片
>* ``Zoom``. 缩放功能
>* ``SuperLargeImage``. 大图功能

Other：
>* ``inSampleSize``. 优化inSampleSize计算规则，先根据像素数过滤，然后再根据优化OpenGL的MAX_TEXTURE_SIZE过滤，最后如果是为大图功能加载预览图的话，当缩小2倍的时为了节省内存考虑还不如缩小4倍（缩小1倍时不会启用大图功能，因此无需处理）
>* ``maxSize``. 默认maxSize改为屏幕的宽高，不再乘以0.75
>* ``Listener``. DownloadListener的onCompleted(File cacheFile, boolean isFromNetwork)和onCompleted(byte[] data)合并成一个onCompleted(DownloadResult downloadResult)
>* ``Listener``. LoadListener的onCompleted(Bitmap bitmap, ImageFrom imageFrom, String mimeType)和onCompleted(GifDrawable gifDrawable, ImageFrom imageFrom, String mimeType)合并成一个onCompleted(LoadResult loadResult)

>* ``删除``. RecycleGifDrawable改名为SketchGifDrawable，并去掉了其中的recycler
>* ``修改``. RecycleBitmapDrawable改名为SketchBitmapDrawable
>* ``修改``. FixedRecycleBitmapDrawable改名为FixedBitmapDrawable
>* ``修改``. BindFixedRecycleBitmapDrawable改名为BindFixedBitmapDrawable

WIKI：
>* readme中感谢[chrisbanes](https://github.com/chrisbanes)/[PhotoView](https://github.com/chrisbanes/PhotoView)
>* readme中感谢[davemorrissey](https://github.com/davemorrissey)/[subsampling-scale-image-view](https://github.com/davemorrissey/subsampling-scale-image-view)
>* 特性中介绍独家支持缩放ImageView和显示大图功能
>* SketchImageView中讲解如何使用缩放和显示大图功能
>* 增强用户体验中加入使用缩放和显示大图功能
>* readme的demo中介绍缩放和显示大图功能
>* 新增缩放功能介绍页
>* 新增大图功能介绍页