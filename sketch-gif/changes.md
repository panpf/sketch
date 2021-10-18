集成 android-gif-drawable 后对其源码的修改

版本：1.2.19

GifDrawable：
* Bitmap mBuffer 字段的访问范围由包级别改为 protected
* GifDrawable(GifInfoHandle, final GifDrawable, ScheduledThreadPoolExecutor, boolean) 构造函数中创建 Bitmap 的部分抽离出一个单独的 makeBitmap 方法，可供子类继承修改
* recycle() 方法中回收 mBuffer 部分抽离出一个单独的 recycleBitmap 方法，可供子类继承修改

GifTextureView：
* R 的包名改为 com.github.panpf.sketch.gif.R

GifViewUtils：
* R 的包名改为 com.github.panpf.sketch.gif.R

GifTextView：
* 恢复去掉的对 API 16 以下的支持