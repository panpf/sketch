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
>* ````. 

其它：
优化inSampleSize计算规则，修复根据像素数过滤较大图片时应该除以4，却写成了除以2的BUG
优化inSampleSize计算规则，防止图片超过OpenGL MAX_TEXTURE_SIZE的限制

WIKI：
>* readme中感谢[chrisbanes](https://github.com/chrisbanes)/[PhotoView](https://github.com/chrisbanes/PhotoView)
>* readme中感谢[davemorrissey](https://github.com/davemorrissey)/[subsampling-scale-image-view](https://github.com/davemorrissey/subsampling-scale-image-view)
>* 特性中介绍独家支持缩放ImageView和显示超大图功能
>* SketchImageView中讲解如何使用缩放和显示超大图功能
>* 增强用户体验中加入使用缩放和显示超大图功能
>* readme的demo中介绍缩放和显示超大图功能


在PhotoView基础上优化了以下功能：
>* 根据图片的尺寸调整最小、中间、最大缩放比例，保证最小比例时能够看到完整的图片，中间比例时能够让图片的一边充满屏幕，然后只需在一个方向上滑动即可查看完整图片，最大比例是中间比例的两倍
>* 手动持续缩放时如果超过了最小比例或最大比例依然可以缩放，但有种拉橡皮筋的感觉，松手后自动回滚到最小或最大比例

待办：
删除RecyclerDrawable，放到显示的时候new一个gifDrawable