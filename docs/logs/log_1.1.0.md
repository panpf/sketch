>* ``新增``. ImageDownloader新增setProgressCallbackNumber(int)方法可用来控制进度回调次数
>* ``新增``. DownloadListener、LoadLinstener、DisplayListener的onCompleted()方法新增From参数，用来表示数据来自哪里
>* ``新增``.  SpearImageView新增类似Picasso的Debug功能，只需调用Spear.setDebugMode(true)开启调试模式即可开启此功能
>* ``优化``. 优化内置的几种图片处理器的resize处理规则。当原图尺寸小于resize时，之前是担心会创建一张更大的图，浪费内存，于是做法是尺寸不变，现在的做法是依然处理但是resize要根据原图尺寸重新计算，原则就是保证新的resize小于原图尺寸并且宽高比同旧的resize一样。例如原图宽高是300x225，resize宽高是400x400，那么之前的结果就是resize还是400x400，最终图片是300x225，而现在的结果是调整resize为255x255，最终图片是225x225
>* ``新增``. 支持仅根据宽或高限制图片大小，例如：maxsize为500x-1，意思就是宽最大为500，高随之缩放
>* ``优化``. 调整了DefaultRequestExecitor的创建方式，网络下载线程池最大容量由10修改为5
>* ``优化``. 调整了DisplayRequest.Helper的options()方法里应用DisplayOptions.resize的规则