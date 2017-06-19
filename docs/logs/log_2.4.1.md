本版本主要修复了demo中的bug，然后顺便将sketch中同样的场景都加了预防措施

:green_heart: 可平滑升级 :green_heart:

其它：
* :ambulance: 所有使用磁盘缓存编辑锁的地方都加了try finally，防止异常导致无法释放锁的情况发生

Sample APP
* :bug: 修复release版本混淆后找不到FFmpegMediaMetadataRetriever类的bug
* :bug: 修复由于VideoThumbnailPreprocessor在遇到异常后没有释放缓存编辑锁，导致阻塞了所有的本地解码线程的bug
