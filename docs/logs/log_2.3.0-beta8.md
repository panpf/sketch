GIF：
>* :bug: `GIF` 修复当反复切换LoadOptions.decodeGifImage的值后再刷新页面，需要播放GIF时却依然显示静态的GIF第一帧的BUG
>* :bug: `Gif Flag` 修复SketchImageView.setShowGifFlag()反复调用时无效的bug

SketchImageView
>* :bug: `Zoom` 修复设置关闭手势缩放功能时没有恢复Matrix和ScaleType的BUG
>* :bug: `ImageFrom` 修复反复调用setShowImageFrom(boolean)时无效的BUG

请求：
>* :bug: 修复显示时遇到已回收的Bitmap崩溃的BUG
>* :bug: 修复读取缓存的已处理图片时类型以及原始尺寸丢失的BUG
>* :bug: 修复读取APK icon时drawable宽高小于等于0崩溃的BUG
>* :sparkles: ExceptionMonitor新增onBitmapRecycledOnDisplay(DisplayRequest, RefDrawable)方法用来监控即将显示时发现Bitmap被回收的问题

缓存：
>* :bug: RefBitmap各个方法全部加了同步锁，视图解决在显示时Bitmap却已回收的BUG
