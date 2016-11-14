BUG：
>* `GIF` 修复当反复切换LoadOptions.decodeGifImage的值后再刷新页面，导致需要播放GIF时却依然显示静态的GIF第一帧的BUG
>* `Gif Flag` 修复SketchImageView.setShowGifFlag()反复调用时无效的bug