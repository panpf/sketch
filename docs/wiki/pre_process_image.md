ImagePreprocessor用来辅助显示特殊文件中包含的图片，例如读取APK和已安装APP图片的功能就是用ImagePreprocessor来实现的

实现原理很简单，在解码之前，先通过ImagePreprocessor将特殊文件中包含的图片提取出来放到放到磁盘缓存中，解码的时候会磁盘缓存中读取图片

默认的ImagePreprocessor只实现了读取APK图标、已安装APP图标以及解析Base64图片的功能，

#### 扩展ImagePreprocessor的功能
你可以扩展ImagePreprocessor实现读取其他类型文件的图片（例如.MP4）

首先你要实现Preprocessor接口写一个预处理类，实现以下方法：
>* boolean match(Context, UriInfo)：根据uri判断是否需要预处理
>* PreProcessResult process(Context, UriInfo)：match方法返回true之后才会调用这个方法进行处理并返回处理结果

需要注意的是在process()方法中编辑磁盘缓存的时候要上锁，详情可参考[ApkIconPreprocessor](../../sketch/src/main/java/me/xiaopan/sketch/preprocess/ApkIconPreprocessor.java)

然后调用Sketch.with(context).getConfiguration().getImagePreprocessor().addPreprocessor(Preprocessor)方法加进去即可
