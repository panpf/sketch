修复BUG：
>* [#4](https://github.com/xiaopansky/sketch/issues/4) 修复由于在内存中缓存了Drawable，导致同一个缓存Drawable在两个不同的地方使用时bounds被改变从而图片大小显示异常，常见的表现为点击图片进入图片详情页后再回来发现图片变小了
>* [#11](https://github.com/xiaopansky/sketch/issues/11) 修复最后一条磁盘缓存无效的BUG，这是DiskLruCache的BUG，因为在commit的时候没有持久化操作记录导致的
>* [#13](https://github.com/xiaopansky/sketch/issues/13) 修复SketchBitmapDrawable由于没有设置TargetDensity而始终以160的默认像素密度来缩小图片最终导致通过getIntrinsicWidth()得到的尺寸始终比Bitmap实际尺寸小的BUG
>* [#14](https://github.com/xiaopansky/sketch/issues/14) ImageHolder直接缓存了Drawable导致同一个Drawable在多个FIX_XY的ImageView上显示时大小异常的BUG

新功能：
>* ``Decode``. [缩略图模式](../wiki/thumbnail_mode.md)，通过缩略图模式你可以在列表中更加清晰的显示那些宽高相差特别大的图片
>* ``Gesture Zoom``. [手势缩放功能](../wiki/zoom.md)，参照PhotoVie，SketchImageView内置了手势缩放功能，比PhotoView功能更强大，体验更好，新增了定位、阅读模式等特色功能
>* ``Super Large Image``. [分块显示超大图功能](../wiki/large_image.md)，SketchImageVie内置了分块显示超大图功能，长微博、高清妹子图什么的不再是问题
>* ``ModeImage``. 新增ModeImage替代ImageHolder，现在可以用任意类型的drawable或者内存缓存中的图片来作为loading占位图了

优化：
>* ``inSampleSize``. 优化inSampleSize计算规则，先根据像素数过滤，然后再根据OpenGL的MAX_TEXTURE_SIZE过滤，最后如果是为大图功能加载预览图的话，当缩小2倍的时为了节省内存考虑还不如缩小4倍（缩小1倍时不会启用大图功能，因此无需处理）
>* ``maxSize``. 默认maxSize改为屏幕的宽高，不再乘以0.75
>* ``Drawable``. 重构Drawable系统，现在可以用任意类型的drawable作为loading占位图了
>* ``TransitonImageDisplayer``. 对TransitionImageDisplayer的安全验证条件中不再验证CENTER_CROP

其它：
>* ``Listener``. DownloadListener的onCompleted(File cacheFile, boolean isFromNetwork)和onCompleted(byte[] data)合并成一个onCompleted(DownloadResult downloadResult)
>* ``Listener``. LoadListener的onCompleted(Bitmap bitmap, ImageFrom imageFrom, String mimeType)和onCompleted(GifDrawable gifDrawable, ImageFrom imageFrom, String mimeType)合并成一个onCompleted(LoadResult loadResult)
>* ``Rename``. 所有跟Failed相关的名字全改成了Error
>* ``FadeInImageDisplayer``. 新增渐入图片显示器FadeInImageDisplayer