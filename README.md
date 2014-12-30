# ![Logo](https://github.com/xiaopansky/Spear/raw/master/sample/src/main/res/drawable-mdpi/ic_launcher.png) Spear

Spear是Android上的一个图片加载器，目的是为了帮助开发者从本地或网络读取图片，然后处理并显示在ImageView上

Spear is an image loader for Android, the purpose is to help the developers to read a picture from a local or network, and then processed and displayed on the ImageView.

![sample](https://github.com/xiaopansky/Spear/raw/master/docs/sample.jpg)

###Features
>* ``多种URI支持``. 支持``http://``、``https://``、``assets://``、``content://``、``file://``、``drawable://``等6种URI。
>* ``异步加载``. 采用线程池来处理每一个请求，并且网络加载和本地加载会放在不同的线程池中执行，保证不会因为网络加载而堵塞本地加载。
>* ``支持缓存``. 采用Lru算法在本地和内存中缓存图片，本地缓存可设置``过期``时间。
>* ``支持ViewHolder``. 即使你在ListView中使用了ViewHolder也依然可以使用ImageLoader来加载图片，并且图片显示绝对不会混乱。
>* ``重复下载过滤``. 如果两个请求的图片地址一样的话，第二个就会等待，一直到第一个下载成功后才会继续处理。
>* ``即时取消无用请求``. 在onDetachedFromWindow或重复利用的时候会取消无用的请求。
>* ``支持进度回调``. 通过progressListener()方法即可设置并开启进度回调。
>* ``防止加载过大Bitmap``,默认最大Bitmap限制为当前屏幕宽高的1.5倍，这样可以有效防止加载过大图片到内存中。
>* ``重新处理图片尺寸``. 可自定义加载到内存的图片的尺寸，使用display()方法显示图片的时候还会自动根据ImageView的宽高来重新处理。
>* ``自带RequestOptions管理器``. 你可以通过Spear.putOptions(Enum<?>, RequestOptions)存储RequestOptions。然后在使用的时候指定名称即可。
>* ``提供SpearImageView``. 让加载图片更加简单，只需调用setImageBy***系列方法即可显示各种图片。
>* ``额外提供load()和download()``. 如果你不是要显示图片只是想要加载然后用作其他用途，那么你可以使用load()方法。
>* ``强大的自定义功能``. 可自定义请求分发与执行、缓存、解码、处理、显示、默认图片、失败图片等。
>* ``强制使用单例模式``. 你只能通过Spear.with(Context)方法获取实例，降低使用复杂度

### Sample App
>* [Get it on Google Play](https://play.google.com/store/apps/details?id=me.xiaoapn.android.imageloader)
>* [Download APK](https://github.com/xiaopansky/Spear/raw/master/releases/SpearSample-2.4.1.apk)

###Usage guide

####显示图片
Spear支持以下6种URI：
>* "http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg"; // from Web
>* "https://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg"; // from Web
>* "/mnt/sdcard/image.png"; // from SD card
>* "content://media/external/audio/albumart/13"; // from content provider
>* "assets://image.png"; // from assets
>* "drawable://" + R.drawable.image; // from drawable resource

**Image from http or https**
```java
String uri = "http://www.huabian.com/uploadfile/2013/1222/20131222054754556.jpg";
Spear.with(context).display(uri, imageView).fire();
```

**Image from file**
```java
Spear.with(context).display("/mnt/sfs.png", imageView).fire();
```
or
```java
Spear.with(context).display(new File("/mnt/sfs.png"), imageView).fire();
```
        
**Image from content provider**
```java
Spear.with(context).display("content://media/external/audio/albumart/13", imageView).fire();
```
or
```java
Uri uri = ...;
Spear.with(context).display(uri, imageView).fire();
```

        
**Image from drawable resource**
```java
Spear.with(context).display("drawable://"+R.drawable.ic_launcher, imageView).fire();
```
or
```java
Spear.with(context).display(R.drawable.ic_launcher, imageView).fire();
```

        
**Image from assets**
```java
Spear.with(context).display("assets://test.png", imageView).fire();
```
or
```java
String uri = Scheme.ASSETS.createUri("test.png");
Spear.with(context).display(uri, imageView).fire();
```

``一定要记得最后要调用fire()方法哦``

####配置显示选项：
```java
Spear.with(getBaseContext())
    .display("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg", imageView)
    .loadingDrawable(R.drawable.image_loading)    // 设置正在加载的时候显示的图片
    .loadFailDrawable(R.drawable.image_load_fail)   // 设置当加载失败的时候显示的图片
    .disableDiskCache() // 禁用磁盘缓存
    .disableMemoryCache()   // 禁用内存缓存
    .diskCacheTimeout(60 * 1000) // 设置磁盘缓存有效期为60秒
    .maxsize(1000, 800) // 设置最大尺寸，用来解码Bitmap时计算inSampleSize，防止加载过大的图片到内存中，默认为当前屏幕的1.5倍
    .resize(300, 300)   // 重新定义图片宽高，将原始图片加载到内存中之后会使用ImageProcessor根据原始图片创建一张新的300x300的图片
    .displayer(new OriginalFadeInImageDisplayer())  // 设置图片显示器，在最后一步会使用ImageDisplayer来显示图片
    .processor(new CircleImageProcessor())  // 设置图片处理器
    .scaleType(ImageView.ScaleType.FIT_START)   // 设置图片显示模式，在使用ImageProcessor处理图片的时候会用到此参数，默认为ImageView的ScaleType
    .listener(new DisplayListener() {   // 设置监听器
        @Override
        public void onStarted() {
            
        }

        @Override
        public void onCompleted(String uri, ImageView imageView, BitmapDrawable drawable) {

        }

        @Override
        public void onFailed(FailureCause failureCause) {

        }

        @Override
        public void onCanceled() {

        }
    })
    .progressListener(new ProgressListener() {  // 设置进度监听器
        @Override
        public void onUpdateProgress(int totalLength, int completedLength) {
            
        }
    })
    .fire();
```

####download()、load()、display()
Spear除了有display()方法用来显示图片之外，还有load()用来加载图片和download()方法用来下载图片

>* download()：下载图片，此方法仅仅实现将图片下载到本地；
>* load()：加载图片，此方法在将图片下载到本地之后会将图片加载到内存并实现本地缓存功能；
>* display()：显示图片，此方法在将图片下载并加载到内存之后，会将图片放到内存缓存中，然后显示在ImageView上，并实现内存缓存。

实际上整个显示图片的过程可分为下载、加载和显示三部分，这三个方法正好对应这三部分，因此你可以根据你的需求选择不同的方法来处理图片

``这三个方法的用法都一样``

display()与load()、download()的区别
>* display()的fire()方法必须在主线程执行，否则将会有异常发生
>* **在使用display()方法显示图片的时候，Spear会自动根据ImageView的宽高计算maxsize和resize，条件就是计算maxsize时要求ImageView的宽高至少有一个是固定的，而计算resize的时候要求宽高都是固定的，这样就省却了很多麻烦，也节省了内存**
>* 可使用的属性display()最多，download()最少具体如下表所示：

下面是属性表（'-'代表不支持）

|属性|download|load|display|
|:--|:--|:--|:--|
|diskCache|true|true|true|
|diskCacheTimeout|0（永久有效）|0（永久有效）|0（永久有效）|
|maxsize|-|设备屏幕尺寸的1.5倍|结合ImageView的宽高来计算|
|resize|-|null|结合ImageView的宽高来计算|
|ImageProcessor|-|null|null|
|ScaleType|-|ScaleType.FIT_CENTER|ScaleType.FIT_CENTER|
|memoryCache|-|-|true|
|ImageDisplayer|-|-|DefaultImageDisplayer（无任何特效）|
|loadingDrawable|-|-|null|
|loadFailedDrawable|-|-|null|
|listener|null（在``非主线程``回调）|null（在``非主线程``回调）|null（在``主线程``回调）|
|progressListener|null（在``非主线程``回调）|null（在``非主线程``回调）|null（在``主线程``回调）|

####你可能还感兴趣的功能：
>* [取消请求](https://github.com/xiaopansky/Spear/wiki/CancelRequest)
>* [使用``RequestOptions``定义属性模板来简化属性设置](https://github.com/xiaopansky/Spear/wiki/RequestOptions)
>* [监听加载``开始``、``成功``、``失败``以及``进度``](https://github.com/xiaopansky/Spear/wiki/listener)
>* [使用``SpearImageView``简化显示图片的操作](https://github.com/xiaopansky/Spear/wiki/SpearImageView)
>* [使用``maxsize``防止加载过大的图片以``节省内存``](https://github.com/xiaopansky/Spear/wiki/maxsize)
>* [使用``resize``修改图片的尺寸或者使加载到内存的图片``同ImageView的尺寸一样``，最大限度的``节省内存``](https://github.com/xiaopansky/Spear/wiki/resize)
>* [设置下载``失败重试次数``、``超时时间``（ImageDownloader）](https://github.com/xiaopansky/Spear/wiki/ImageDownloader)
>* [自定义``InSampleSize``计算规则或``自定义图片解码器``（ImageDecoder）](https://github.com/xiaopansky/Spear/wiki/ImageDecoder)
>* [将图片处理成``圆形``的、``椭圆形``的或者加上``倒影效果``（ImageProcessor）](https://github.com/xiaopansky/Spear/wiki/ImageProcessor)
>* [以有趣的动画显示图片，例如以``渐变``或``缩放``的方式显示（ImageDisplayer）](https://github.com/xiaopansky/Spear/wiki/ImageDisplayer)
>* [设置``请求执行顺序``、``任务队列长度``或``线程池大小``（RequestExecutor）](https://github.com/xiaopansky/Spear/wiki/RequestExecutor)
>* [设置``内存缓存容量``（MemoryCache）](https://github.com/xiaopansky/Spear/wiki/MemoryCache)
>* [设置``磁盘缓存目录``或``保留空间大小``（DiskCache）](https://github.com/xiaopansky/Spear/wiki/DiskCache)

###Downloads
>* [spear-1.2.0.jar](https://github.com/xiaopansky/Spear/raw/master/releases/spear-1.2.0.jar)
>* [spear-1.2.0-sources.zip](https://github.com/xiaopansky/Spear/raw/master/releases/spear-1.2.0-sources.zip)

###Change log
###1.3.0
**SpearImageView**
>* ``修复``. 修复了由于在onDetachedFromWindow()方法中执行了setImageDrawable(null)释放图片导致在RecyclerView回滚的时候图片显示空白的BUG
>* ``修复``. 修复了由于在onDetachedFromWindow()方法中主动取消了请求导致在RecyclerView中会出现错误取消而引起了图片显示不出来的BUG
>* ``修复``. 取消了在setImageByUri()方法中的过滤请求功能，因为这里只能根据URI过滤。例如：同一个URI在同一个SpearImageView上调用setImageByUri()方法显示了两次，但是这两次显示的时候SpearImageView的宽高是不一样的，结果就是第一次的显示请求继续执行，第二次的显示请求被拒绝了。现在去掉过滤功能后统一都交给了Spear处理，结果会是第一次的显示请求被取消，第二次的显示请求继续执行。
>* ``新增``. 新增在图片表面显示进度的功能，你只需调用setEnableShowProgress(boolean)方法开启即可
>* ``优化``. debug开关不再由Spear.isDebug()控制，而是在SpearImageView中新增了一个debugMode参数来控制
>* ``新增``. 新增类似MaterialDesign的按下脉波效果。你只需注册点击事件或调用setClickable(true)，然后调用setEnablePressRipple(true)即可

**Other**
>* ``修复``. display时计算maxsize和resize的时候不再考虑real width和real height。这是因为当ImageView的宽高是固定的，在循环重复利用的时候从第二次循环利用开始，最终计算出来的size都将是上一次的size，显然这是个很严重的BUG。当所有的ImageView的宽高都是一样的时候看不出来这个问题，都不一样的时候问题就出来了。
>* ``优化``. 默认任务执行器的队列长度由20调整为200，这是由于如果你一次性要显示大量的图片，队列长度比较小的话，后面的将会出现异常
>* ``修复``. 修复了由于DisplayHelper、LoadHelper、DownloadHelper的options()方法发现参数为null时返回了一个null对象的BUG，这会导致使用SpearImageView时由于没有设置DisplayOptions而引起崩溃
>* ``修复``. 修复在2.3及以下缓存RecyclingBitmapDrawable的时候忘记添加计数导致Bitmap被提前回收而引发崩溃的BUG
>* ``删除``. 删除SoftReferenceMemoryCache.java
>* ``移动``. 移动DiskCache.java、LruDiskCache.java、LruMemoryCache.java、MemoryCache.java到cache目录下
>* ``优化``. 优化HttpClientImageDownloader，读取数据的时候出现异常或取消的时候主动关闭输入流，避免堵塞连接池，造成ConnectionPoolTimeoutException异常
>* ``优化``. 将计算默认maxsize的代码封装成一个方法并放到了ImageSizeCalculator.java中，方便开发者自定义
>* ``优化``. 优化了默认的inSampleSize的计算方法，增加了限制图片像素数超过目标尺寸像素的两倍，这样可以有效防止那些一边特小一边特大的图片，以特大的姿态被加载到内存中
>* ``优化``. 优化了默认的ImageDisplayer和默认的裁剪ImageProcessor的实现方式
>* ``修改``. 修改DisplayHelper中loadFailedDrawable()方法的名称为loadFailDrawable()
>* ``修复``. 修复DisplayHelper、LoadHelper、DownloadHelper中调用options()方法设置参数的时候会直接覆盖Helper中的参数的BUG，修改后的规则是如果Options中的参数倍设置过才会直接覆盖
>* ``修改``. ImageDownloader.setTimeout()改名为setConnectTimeout()
>* ``优化``. 将一些配置移到了Configuration.java中，debug配置直接改成了静态的
>* ``修改``. 默认的图片下载器改成了HttpUrlConnectionImageDownloader，HttpClientImageDownloader退居二线变成了替补
>* ``优化``. 优化了自带的图片处理器，根据ScaleType，以不同的方式处理图片，主要体现在是否裁剪图片上
>* ``优化``. 默认图片和失败图片使用ImageProcessor处理时支持使用DisplayHelper中的resize和scaleType
>* ``优化``. 优化自带的图片处理器，对ScaleType支持更完善，更准确
>* ``新增``. 增加pause功能，你可以在列表滚动时调用pause()方法暂停加载新图片，在列表停止滚动后调用resume()方法恢复并刷新列表，通过这样的手段来提高列表滑动流畅度
>* ``新增``. LruDiskCache增加maxsize功能
>* ``优化``. 调整LruDiskCache的默认保留空间为100M
>* ``优化``. 当uri为null或空时显示loadingDrawable
>* ``修改``. DisplayListener.From.LOCAL改名为DisplayListener.From.DISK
>* ``新增``. 增加对例如“/mtn/sdcard0/sample.png”uri的支持
>* ``优化``. 默认解码器在遇到1x1的图片时按照失败处理
>* ``优化``. 默认线程池的keepAliveTime时间由1秒改为60秒
>* ``修改``. 不再默认根据ImageView的Layout Size设置resize，新增resizeByImageViewLayoutSize()方法开启此功能
>* ``修改``. 当你使用OriginalFadeInImageDisplayer作为displayer的时候会默认开启resizeByImageViewLayoutSize功能，因为不开启resizeByImageViewLayoutSize的话图片最终就会显示变形
>* ``修改``. image uri不再支持file:///mnt/sdcard/image.png，改为直接支持/mnt/sdcard/image.png

###1.2.0
>* ``优化``. display的fire方法去掉了异步线程过滤，由于display基本都是在主线程执行的过滤异步线程没有意义
>* ``优化``. 改善了需要通过Handler在主线程执行回调以及显示的方式，以前是使用Runnable，现在时通过Message，这样就避免了创建Runnable，由于display是非常频繁的操作，因此这将会是有意义的改善
>* ``优化``. 优化了DisplayHelper的使用，以前是为每一次display都创建一个DisplayHelper，现在是只要你是按照display().fire()这样连续的使用，那么所有的display将共用一个DisplayHelper，这将会避免创建大量的DisplayHelper
>* ``优化``. ProgressListener.onUpdateProgress(long, long)改为ProgressListener.onUpdateProgress(int, int)，因为int足够用了

[查看更多...](https://github.com/xiaopansky/Spear/wiki/Change-log/_edit)

###License
```java
/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```
