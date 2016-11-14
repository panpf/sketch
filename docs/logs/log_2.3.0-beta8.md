BUG：
>* `GIF` 修复当反复切换LoadOptions.decodeGifImage的值后再刷新页面，需要播放GIF时却依然显示静态的GIF第一帧的BUG
>* `Gif Flag` 修复SketchImageView.setShowGifFlag()反复调用时无效的bug
>* `Zoom` 修复设置关闭手势缩放功能时没有恢复Matrix和ScaleType的BUG
>* `ImageFrom` 修复反复调用setShowImageFrom(boolean)时无效的BUG