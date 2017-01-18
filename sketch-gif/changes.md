集成android-gif-drawable 1.2.4 后对其源码的修改

GifDrawable：
>* Bitmap mBuffer字段的访问范围由包级别改为protected
>* GifDrawable(GifInfoHandle, final GifDrawable, ScheduledThreadPoolExecutor, boolean)构造函数中创建Bitmap的部分抽离出一个单独的方法，可供子类继承修改
>* recycle()方法中回收mBuffer部分抽离出一个单独的方法，可供子类继承修改

包名为me.xiaopan.sketch.gif
>* GifTextView中对R.java的引用
>* GifViewUtils中对R.java的引用
>* ReLinker.java中对BuildConfig引用