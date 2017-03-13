SLog:
>* :bug: 修复DiskLruCache使用SLog.w(String, String, Object...)方法打印日志时，如果第二个参数中包含有特定的字符串（例如“3A”）时就会崩溃的bug