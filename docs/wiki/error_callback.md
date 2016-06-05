#### 简介

Sketch在运行的过程中会有各种各样的异常，你可以通过ErrorCallback收到这些异常，然后将这些异常记录下来帮助解决问题

### 使用

ErrorCallback默认实现只是将收到的异常打印在了logcat中，而你需要继承ErrorCallback然后去记录异常

ErrorCallback包含以下方法：
>* onInstallDiskCacheFailed(Exception, File)：安装DiskLruCache失败，但Sketch还是可以正常运行的。
    一旦失败了这个方法就会被频繁回调，这是因为每一次需要用到DiskCache的时候都会尝试恢复，再次失败就会再次回调。
    因此你在记录日志的时候不必每次失败都记，可以每隔一个小时记一次
>* onDecodeGifImageFailed(Throwable, LoadRequest, BitmapFactory.Options)：解码gif图片失败。你需要过滤一下Throwable，
    如果是UnsatisfiedLinkError或ExceptionInInitializerError就是找到libpl_droidsonroids_gif.so，
    这个时候你就要记录一下当前设备的abi类型，Sketch中包含的libpl_droidsonroids_gif.so已经覆盖了全平台了，如果还找不到就说明有其他的问题了。
    另外这里的Options是只含有outWidth、outHeight、outMimeType信息的
>* onDecodeNormalImageFailed(Throwable, LoadRequest, BitmapFactory.Options)：解码普通图片失败。这里的Options是只含有outWidth、outHeight、outMimeType、inSampleSize信息的

最后调用Sketch.with(context).getConfiguration().setErrorCallback(ErrorCallback)使用你的ErrorCallback