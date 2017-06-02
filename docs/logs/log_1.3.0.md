**SpearImageView**
>* ``修复``. 兼容RecyclerView，因为在RecyclerView中View的生命周期略有变化，导致图片显示异常，现已修复
>* ``修复``. 取消了在setImageByUri()方法中的过滤请求功能，因为这里只能根据URI过滤。例如：同一个URI在同一个SpearImageView上调用setImageByUri()方法显示了两次，但是这两次显示的时候SpearImageView的宽高是不一样的，结果就是第一次的显示请求继续执行，第二次的显示请求被拒绝了。现在去掉过滤功能后统一都交给了Spear处理，结果会是第一次的显示请求被取消，第二次的显示请求继续执行。
>* ``新增``. 新增在图片表面显示进度的功能，你只需调用setEnableShowProgress(boolean)方法开启即可
>* ``优化``. debug开关不再由Spear.isDebug()控制，而是在SpearImageView中新增了一个debugMode参数来控制
>* ``新增``. 新增类似MaterialDesign的点击涟漪效果。你只需注册点击事件或调用setClickable(true)，然后调用setEnableClickRipple(true)即可
>* ``修复``. 修复了使用SpearImageView时设置了DisplayOptions、DisplayListener等参数，但最终没有通过setImageBy***()方法显示图片而是通过Spear.with(context).display(imageUrl, spearImageView)显示图片最终导致DisplayOptions、DisplayListener等参数不起作用的BUG
>* ``修改``. setImageBy***()系列方法，改名为setImageFrom***()

**Download**
>* ``优化``. 优化HttpClientImageDownloader，读取数据的时候出现异常或取消的时候主动关闭输入流，避免堵塞连接池，造成ConnectionPoolTimeoutException异常
>* ``修改``. 默认下载器改为HttpUrlConnectionImageDownloader.java，而HttpClientImageDownloader则作为备选
>* ``修改``. ImageDownloader.setTimeout()改名为setConnectTimeout()
>* ``优化``. 优化下载的实现，使其更稳定

**Cache**
>* ``删除``. 删除SoftReferenceMemoryCache.java
>* ``移动``. 移动DiskCache.java、LruDiskCache.java、LruMemoryCache.java、MemoryCache.java到cache目录下
>* ``优化``. 调整LruDiskCache的默认保留空间为100M
>* ``新增``. LruDiskCache增加maxsize功能
>* ``修复``. 修复在2.3及以下缓存RecyclingBitmapDrawable的时候忘记添加计数导致Bitmap被提前回收而引发崩溃的BUG

**Decode**
>* ``优化``. 优化了默认的inSampleSize的计算方法，增加了限制图片像素数超过目标尺寸像素的两倍，这样可以有效防止那些一边特小一边特大的图片，以特大的姿态被加载到内存中
>* ``优化``. 将计算默认maxsize的代码封装成一个方法并放到了ImageSizeCalculator.java中
>* ``修复``. 计算maxsize的时候不再考虑ImageView的getWidth()和getHeight()，这是因为当ImageView的宽高是固定的，在循环重复利用的时候从第二次循环利用开始，最终计算出来的size都将是上一次的size，显然这是个很严重的BUG。当所有的ImageView的宽高都是一样的时候看不出来这个问题，都不一样的时候问题就出来了。
>* ``优化``. 默认解码器在遇到1x1的图片时按照失败处理

**Display**
>* ``优化``. 优化了默认ImageDisplayer的实现方式
>* ``修改``. 修改ColorFadeInImageDisplayer的名字为ColorTransitionImageDisplayer；OriginalFadeInImageDisplayer的名字为TransitionImageDisplayer
>* ``修改``. 当你使用TransitionImageDisplayer作为displayer的时候会默认开启resizeByImageViewLayoutSize功能，因为不开启resizeByImageViewLayoutSize的话图片最终就会显示变形

**Execute**
>* ``优化``. 默认任务执行器的任务队列的长度由20调整为200，这是由于如果你一次性要显示大量的图片，队列长度比较小的话，后面的将会出现异常
>* ``优化``. 默认任务执行器的线程池的keepAliveTime时间由1秒改为60秒

**Process**
>* ``修复``. 计算resize的时候不再考虑ImageView的getWidth()和getHeight()，这是因为当ImageView的宽高是固定的，在循环重复利用的时候从第二次循环利用开始，最终计算出来的size都将是上一次的size，显然这是个很严重的BUG。当所有的ImageView的宽高都是一样的时候看不出来这个问题，都不一样的时候问题就出来了。
>* ``优化``. 优化了默认ImageProcessor的实现方式
>* ``优化``. 优化自带的几种图片处理器，对ScaleType支持更完善，更准确

**Request**
>* ``修改``. DisplayListener.From.LOCAL改名为DisplayListener.From.DISK

**Spear**
>* ``优化``. 将一些配置移到了Configuration.java中，debugMode的设置直接改成了静态的
>* ``新增``. 增加pause功能，你可以在列表滚动时调用pause()方法暂停加载新图片，在列表停止滚动后调用resume()方法恢复并刷新列表，通过这样的手段来提高列表滑动流畅度
>* ``修改``. image uri不再支持“file:///mnt/sdcard/image.png”，直接支持“/mnt/sdcard/image.png”
>* ``修复``. 修复了由于DisplayHelper、LoadHelper、DownloadHelper的options()方法参数为null时返回了一个null对象的BUG，这会导致使用SpearImageView时由于没有设置DisplayOptions而引起崩溃
>* ``修改``. 修改DisplayHelper中loadFailedDrawable()方法的名称为loadFailDrawable()
>* ``修复``. 修复DisplayHelper、LoadHelper、DownloadHelper中调用options()方法设置参数的时候会直接覆盖Helper中的参数的BUG，修改后的规则是如果helper中为null，且Options中的参数被设置过才会覆盖
>* ``优化``. 默认图片和失败图片使用ImageProcessor处理时支持使用DisplayHelper中的resize和scaleType
>* ``优化``. 调用display()方法显示图片时，当uri为null或空时显示loadingDrawable
>* ``优化``. display的fire方法去掉了异步线程过滤，由于display基本都是在主线程执行的过滤异步线程没有意义
>* ``修改``. 不再默认根据ImageView的Layout Size设置resize，新增resizeByImageViewLayoutSize()方法开启此功能。另外当你使用TransitionImageDisplayer作为displayer的时候会默认开启resizeByImageViewLayoutSize功能，因为不开启resizeByImageViewLayoutSize的话图片最终就会显示变形