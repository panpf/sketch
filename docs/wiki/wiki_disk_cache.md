DiskCache用来在本地磁盘上缓存图片。默认实现是LruDiskCache，其根据文件最后修改时间实现了LRU算法，默认最大容量为100M，保留空间为100M。

####相关方法
DiskCache定义了以下方法可供实现自定义配置
>* setCacheDir(File)：设置缓存文件存放目录，默认为当前应用缓存目录下的sketch文件夹
>* setReserveSize(int)：设置保留空间，单位字节，当磁盘剩余空间小于给定值的时候就会自动回收最少使用的图片，默认为100M
>* setMaxSize(int): 设置本地缓存可用的最大容量，默认为100M
>* getCacheFile(String)：获取缓存文件，返回null表示没有
>* getSize()：获取已用缓存容量，此方法可能比较耗时
>* clear()：清除缓存