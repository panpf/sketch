ImagePreprocessor用来辅助显示特殊文件中包含的图片，例如读取APK和已安装APP图片的功能就是用ImagePreprocessor来实现的

实现原理很简单，在解码之前，先执行ImagePreprocessor读取特殊文件中的图片，然后放到磁盘缓存中，解码的时候会优先读取磁盘缓存

默认的ImagePreprocessor只实现了读取APK图标和已安装APP图标的功能，

#### 扩展ImagePreprocessor的功能
你可以扩展ImagePreprocessor实现读取其他类型文件的图片（例如.MP4）

首先要继承ImagePreprocessor，重写以下方法：
>* boolean isSpecific(LoadRequest)：判断这个请求是否可以用ImagePreprocessor预处理
>* PreProcessResult getDiskCacheEntry(LoadRequest)：isSpecific返回true之后才会调用这个方法进行处理并返回缓存文件

需要注意的是在getDiskCacheEntry方法中编辑磁盘缓存的时候要开同步锁，详情可参考[ImagePreprocessor](../../sketch/src/main/java/me/xiaopan/sketch/feature/ImagePreprocessor.java)源码

然后调用Sketch.with(context).getConfiguration().setImagePreprocessor()设置即可