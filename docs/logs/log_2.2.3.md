ErrorCallback:
>* ``优化``. onInstallDiskCacheFailed异常细分出UnableCreateDirException，标明无法创建缓存目录
>* ``新增``. onInstallDiskCacheFailed异常细分出UnableCreateFileException，标明无法缓存目录中创建文件
>* ``新增``. 新增onProcessImageFailed(OutOfMemoryError, String, ImageProcessor)方法
>* ``新增``. 新增构造函数ErrorCallback(Context) 
>* ``修改``. ErrorCallback重命名为ExceptionMonitor 
>* ``新增``. 新增onDownloadFailed(DownloadRequest, Throwable)，可监控下载失败异常 
   
DiskCache：
>* ``优化``. DiskCache在执行close的时候，顺便把缓存编辑锁清了 
>* ``新增``. DiskCache新增isClosed()方法
>* ``优化``. LruDiskCache内部各个方法增加了closed判断，增强稳定性
>* ``修改``. 现在DiskCache.getEditLock(String)可能返回null，因此要判断一下 
>* ``优化``. LruDiskCache安装磁盘缓存的时候增加目录可用性检查和文件创建测试
>* ``优化``. 现在LruDiskCache创建缓存目录的时候会优先考虑所有SD卡，然后考虑内部存储
>* ``BUG``. 修复cache is closed异常 

MemoryCache：
>* ``优化``. MemoryCache在执行close的时候，顺便把缓存编辑锁清了 
>* ``新增``. MemoryCache新增isClosed()方法
>* ``优化``. LruMemoryCache内部各个方法增加了closed判断，增强稳定性
>* ``修改``. 现在MemoryCache.getEditLock(String)可能返回null，因此要判断一下
