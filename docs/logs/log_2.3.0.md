SketchImageView:
>* ``新增``. 集成PhotoView支持缩放ImageView，根据图片大小调整缩放倍数
>* ``新增``. 通过BitmapRegionDecoder支持显示超级大图

>* ````. 


Request:
>* ``修改``. DownloadListener的onCompleted(File cacheFile, boolean isFromNetwork)和onCompleted(byte[] data)合并成一个onCompleted(DownloadResult downloadResult)
>* ``修改``. LoadListener的onCompleted(Bitmap bitmap, ImageFrom imageFrom, String mimeType)和onCompleted(GifDrawable gifDrawable, ImageFrom imageFrom, String mimeType)合并成一个onCompleted(LoadResult loadResult)

Drawable：
>* ``删除``. RecycleGifDrawable改名为SketchGifDrawable，并去掉了其中的recycler
>* ``修改``. RecycleBitmapDrawable改名为SketchBitmapDrawable
>* ``修改``. FixedRecycleBitmapDrawable改名为FixedBitmapDrawable
>* ``修改``. BindFixedRecycleBitmapDrawable改名为BindFixedBitmapDrawable
>* ``BUG``. 修复SketchBitmapDrawable由于没有设置TargetDensity导致始终以160的默认像素密度来缩小图片的BUG

其它：
优化inSampleSize计算规则，先先根据像素数过滤，然后再根据优化OpenGL的MAX_TEXTURE_SIZE过滤，最后如果是为超大图功能加载预览图的话，当缩小2倍的时为了节省内存考虑还不如缩小4倍（缩小1倍时不会启用超大图功能，因此无需处理）
默认maxSize改为屏幕的宽高，不再乘以0.75

WIKI：
>* readme中感谢[chrisbanes](https://github.com/chrisbanes)/[PhotoView](https://github.com/chrisbanes/PhotoView)
>* readme中感谢[davemorrissey](https://github.com/davemorrissey)/[subsampling-scale-image-view](https://github.com/davemorrissey/subsampling-scale-image-view)
>* 特性中介绍独家支持缩放ImageView和显示超大图功能
>* SketchImageView中讲解如何使用缩放和显示超大图功能
>* 增强用户体验中加入使用缩放和显示超大图功能
>* readme的demo中介绍缩放和显示超大图功能


在PhotoView基础上优化了以下功能：
>* 双击缩放比例比例闲现在有最小和最大两个 
>* 根据图片的尺寸和ImageView的宽高动态计算合适的双击缩放比例
>* 手动持续缩放时如果超过了最小比例或最大比例依然可以缩放，并且超过后会有种拉橡皮筋的感觉，松手后自动回滚到最小或最大缩放比例