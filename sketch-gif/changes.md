集成 android-gif-drawable 后对其源码的修改

版本：1.2.10

GifDrawable：
* Bitmap mBuffer字段的访问范围由包级别改为protected
* GifDrawable(GifInfoHandle, final GifDrawable, ScheduledThreadPoolExecutor, boolean)构造函数中创建Bitmap的部分抽离出一个单独的方法，可供子类继承修改
* recycle()方法中回收mBuffer部分抽离出一个单独的方法，可供子类继承修改

GifTextView：
* R的包名改为me.panpf.sketch.gif.R

GifViewUtils
* R的包名改为me.panpf.sketch.gif.R

ReLinker.java
* R的包名改为me.panpf.sketch.gif.R