# ![Logo](https://github.com/xiaopansky/HappyImageLoader/raw/master/res/drawable-mdpi/ic_launcher.png) HappyImageLoader

HappyImageLoader是Android上的一个图片加载类库，主要用于从本地或网络读取图片，然后处理并显示在ImageView上

![sample](https://github.com/xiaopansky/HappyImageLoader/raw/master/docs/sample.jpg)

##Features

>* 异步加载。采用线程池来处理每一个请求，并且网络加载和本地加载会放在不同的线程池中执行，保证不会因为网络加载而堵塞本地加载。

>* 支持缓存。支持在本地或内存中缓存图片，内存缓存采用Android扩展包中的LruCache来缓存，且最大容量为可用内存的八分之一，本地缓存可定义有效期，和最大容量（超过最大容量时或当前存储不够用时会自动根据活跃度清除不活跃的本地缓存文件）。

>* 强大的自定义功能。通过Configuration类你可以自定义请求执行器、缓存器、解码器等；通过DisplayOptions类你可以自定义缓存策略、Bitmap处理器、Bitmap显示器、以及不同状态时的图片。

>* 支持ViewHolder。即使你在ListView中使用了ViewHolder也依然可以使用ImageLoader来加载图片，并且图片显示绝对不会混乱。

>* 重复下载过滤。如果两个请求的图片地址一样的话，第二个就会等待，一直到第一个下载成功后才会继续处理。

## Sample App
>* [Get it on Google Play](https://play.google.com/store/apps/details?id=me.xiaoapn.android.imageloader)
>* [Download APK](https://github.com/xiaopansky/HappyImageLoader/raw/master/releases/HappyImageLoader-2.4.0.apk)

##Usage Guide

### Sample
In activity
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
	ImageView imageView = new ImageView(getBaseContext());
	setContentView(imageView);
	ImageLoader.getInstance(getBaseContext()).display("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg", imageView);
}
```

In Adapter
```java
@Override
public View getView(int position, View convertView, ViewGroup parent) {
	final ViewHolder viewHolder;
	if(convertView == null){
		viewHolder = new ViewHolder();
		convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_image, null);
		viewHolder.image = (ImageView) convertView.findViewById(R.id.image_gridItem);
		convertView.setTag(viewHolder);
	}else{
		viewHolder = (ViewHolder) convertView.getTag();
	}
	
	ImageLoader.getInstance(context).display(imageUris[position], viewHolder.image);
	return convertView;
}
```

###Advanced
ImageLodaer有三个最主要的方法
>* download() 下载图片，此方法仅仅实现将图片下载到本地
>* load() 加载图片，此方法在将图片下载到本地之后会将图片加载到内存
>* display() 显示图片，此方法在将图片下载并加载到内存之后，会将图片放到内存缓存中，然后显示在ImageView上

你可以根据你的需求选择不同的方法来处理图片

以下将重点介绍display()方法的使用，在看完之后关于load()和download()方法的使用你将无师自通。

####1.URI类型
支持以下六种URI类型
>* "http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg"; // from Web
>* "https://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg"; // from Web
>* "file:///mnt/sdcard/image.png"; // from SD card
>* "content://media/external/audio/albumart/13"; // from content provider
>* "assets://image.png"; // from assets
>* "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)

####2.配置ImageLodaer
```java
ImageLoader.getInstance(getBaseContext()).getConfiguration()
	.setBitmapDecoder(new DefaultBitmapDecoder())  // 设置Bitmap解码器
	.setDebugMode(true)  // 开启Debug模式，在控制台输出LOG
	.setDiskCache(new LruDiskCache(getBaseContext()))  // 设置磁盘缓存器 
	.setDownloader(new LockDownloader())  // 设置下载器 
	.setMemoryCache(new LruMemoryCache())  // 设置内存缓存器
	.setRequestExecutor(new DefaultRequestExecutor());  // 设置请求执行器
```

####3.使用DisplayOptions自定义显示选项
```java
DisplayOptions displayOptions = new DisplayOptions(getBaseContext())
.setEmptyUriDrawable(R.drawable.image_failure)    //设置当uri为空时显示的图片
.setLoadingDrawable(R.drawable.image_displaying)	//设置默认图片
.setLoadFailDrawable(R.drawable.image_failure)	//设置当加载失败时显示的图片
.setEnableMemoryCache(true)	//开启内存缓存，开启后会采用Lru算法将Bitmap缓存在内存中，以便重复利用
.setEnableDiskCache(true)	//开启硬盘缓存，开启后会先将图片下载到本地，然后再加载到内存中
.setEnableProgressCallback(true) // 开启进度回调功能
.setDiskCachePeriodOfValidity(1000 * 60 * 60 * 24)	//设置硬盘缓存有效期为24小时，24小时过后将重新下载图片
.setDecodeMaxSize(new ImageSize(720, 1280))	// 设置解码最大尺寸，在解码图片的时候会根据DecodeMaxSize来计算inSampleSize，防止加载到内存中的图片的尺寸过大，默认值为当前设备屏幕的尺寸
.setProcessSize(new ImageSize(400, 400))   // 设置处理尺寸，你可以通过此参数并配合BitmapProcessor指定最终显示到ImageView上的Bitmap的尺寸，一般情况下使用display()方法的时候不需要设置此参数，因为display()方法会根据ImageView的宽高来自动计算出一个合适的ProcessSize，前提条件是你需要在布局中明确指定ImageView的宽高，不能用match_parent或wrap_content
.setMaxRetryCount(2)	//设置最大重试次数，当连接超时时会再次尝试下载
.setProcessor(new ReflectionBitmapProcessor())	//设置Bitmap处理器，当图片从本地读取内存中后会使用BitmapProcessor将图片处理一下，你可以通过BitmapProcessor将图片处理成任何你想要的效果
.setDisplayer(new ZoomInBitmapDisplayer())	//设置图片显示器，在处理完图片之后会调用BitmapDisplayer来显示图片，你可以通过BitmapDisplayer自定义任何你想要的方式来显示图片
.setProcessLoadingDrawable(false)    //设置不使用BitmapProcessor处理加载中图片
.setProcessLoadFailDrawable(false)   //设置不使用BitmapProcessor处理加载失败图片
.setProcessEmptyUriDrawable(false);  //设置不使用BitmapProcessor处理URI为空图片
```

配置好后调用display()方法的时候传进去即可，例如：
```java
ImageLoader.getInstance(getBaseContext()).display("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg", imageView, displayOptions);
```

DisplayOptions默认的配置是：
>* 开启内存缓存和硬盘缓存
>* DecodeMaxSize为当前设备屏幕的尺寸
>* Displayer为DefaultBitmapDisplayer（无任何动画效果效果）
>* 最大重试次数为2

####4.利用Configuration().putOptions()来管理多个DisplayOptions
当你有多个DisplayOptions的时候你要怎么去管理并方便的使用呢？别担心我已经为你提供了一个绝对可行的解决方案。

首先你需要定义一个枚举类来作为Options的标签，如下：
```java
public enum DisplayOptionsType {
	/**
	 * ListView用的
	 */
	LIST_VIEW, 
	
	/**
	 * GridView用的
	 */
	GRID_VIEW, 
	
	/**
	 * Gallery用的
	 */
	GALLERY, 
	
	/**
	 * ViewPager用的
	 */
	VIEW_PAGER,
}
```
然后定义多个DisplayOptions，且通过ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(Enum<?> enum, Options options)方法将DisplayOptions和Enum绑定并放进Configuratin中
```java
ImageLoader.getInstance(getBaseContext()).getConfiguration()
    .putOptions(DisplayOptionsType.GRID_VIEW, new DisplayOptions(getBaseContext())
        .setLoadingDrawable(R.drawable.image_loading)
        .setLoadFailDrawable(R.drawable.image_load_fail)
        .setProcessor(new ReflectionBitmapProcessor()))
    .putOptions(DisplayOptionsType.VIEW_PAGER, new DisplayOptions(getBaseContext())
        .setLoadFailDrawable(R.drawable.image_load_fail)
        .setDisplayer(new ZoomOutBitmapDisplayer()))
    .putOptions(DisplayOptionsType.LIST_VIEW, new DisplayOptions(getBaseContext())
        .setLoadingDrawable(R.drawable.image_loading)
        .setLoadFailDrawable(R.drawable.image_load_fail)
        .setProcessor(new CircleBitmapProcessor()))
    .putOptions(DisplayOptionsType.GALLERY, new DisplayOptions(getBaseContext())
        .setLoadingDrawable(R.drawable.image_loading)
        .setLoadFailDrawable(R.drawable.image_load_fail)
        .setProcessor(new RoundedCornerBitmapProcessor()));
```
然后在使用的时候就可以调用ImageLoader.getInstance(context).display(String imageUri, ImageView imageView, Enum<?> optionsName)方法来传入对应的枚举来显示图片了，ImageLoader会根据你传入的枚举从Configuration中取出对应的DisplayOptions。
```java
ImageLoader.getInstance(context).display(imageUrls[position], viewHolder.image, OptionsType.GALLERY);
```
注意：如果无法从Configuration中获取DisplayOptions的话ImageLoader就会创建一个默认的DisplayOptions。

####5.RequestExecutor（请求执行器）
RequestExecutor是用来执行请求的，默认的实现是DefaultRequestExecutor，其包含三个线程池
>* 网络任务线程池：主要用来执行比较耗时的下载任务，核心线程数``5``个，最大线程数``10``；
>* 本地任务线程池：用来执行本地任务，例如assets、drawable、缓存图片等。核心线程数``1``个，最大线程数也是``1``个，这样一来本地任务可以一个一个加载；
>* 任务调度线程池：用来分发请求，其核心作用在于判断请求该放到网络任务线程池执行还是该放到本地任务线程池执行。核心线程数``1``个，最大线程数也是``1``个。

三种线程池的任务队列都是长度为20的有界队列，这样可以保证能够及时加载最新的任务。
你可以通过DefaultRequestExecutor的构造函数指定新的任务调度执行器、网络任务执行器、本地任务执行器来达到自定义的目的，详情请参考DefaultRequestExecutor的源码

####6.DiskCache（磁盘缓存器）
DiskCache用来将图片缓存在本地磁盘上，方便下次读取。特点是可以设置最大容量并自动清除不活跃的缓存文件，默认提供了LruDiskCache实现。

你可以通过DiskCache的setDir(File)方法来自定义缓存目录

####7.MemoryCache（内存缓存器）
MemoryCache用来在内存中缓存Bitmap，默认提供了以下两种实现供选择（默认采用的是LruMemoryCache）：
>* LruMemoryCache：内存缓存部分采用LRU（近期最少使用）算法来缓存Bimtap；
>* SoftReferenceMemoryCache：内存缓存部分采用软引用的方式来缓存Bitmap，由于从Android4.0起虚拟机将变得异常活跃，所以此种缓存方法已经失去了其应有的作用，所以不建议使用。

你可以通过LruMemoryCache的构造函数实现自定义内存缓存最大容量，默认为可用内存的八分之一

####8.BitmapDecoder（图片解码器）
BitmapDecoder是用来解码Bitmap的，默认的实现是DefaultBitmapDecoder。

####9.BitmapProcessor（图片处理器）
BitmapProcessor是用来在BitmapDecoder解码完图片之后在对图片进行处理的，因此你可以利用BitmapProcessor将图片处理成任何你想要的效果。ImageLoader默认提供了四种BitmapProcessor供你使用：
>* TailorBitmapProcessor：图片裁剪处理器，可以将图片按照ProcessSize的尺寸进行裁剪
>* CircleBitmapProcessor：圆形图片处理器，可以将图片处理成圆形的，如示例图所示；
>* ReflectionBitmapProcessor：倒影图片处理器，可以将图片处理成倒影效果的，如示例图所示。另外倒影的高度以及倒影的距离都可以通过构造函数来自定义；
>* RoundedCornerBitmapProcessor：圆角图片处理器，可以将图片处理成圆角的，如示例图所示。另外圆角的半径可以通过构造函数来自定义

以上几种BitmapProcessor都会使用ProcessSize作为新创建的Bitmap的尺寸

如果你想自定义的话只需实现BitmapProcessor接口，然后调用Options.setBitmapProcessor(BitmapProcessor processor)应用即可，另外有几点需要注意：
>* BitmapProcessor接口有一个叫getTag()的方法，此方法的目的是获取一个能够标识当前BitmapProcessor的字符串用来组装图片的缓存ID。如果本地同一张图片使用不同的BitmapProcessor处理的话，最后的效果是不一样的，那么在内存中的缓存ID就不能一样，所以你要保证getTag()方法返回的字符串一定是独一无二的；
>* 通过BitmapProcessor的process()方法传进去的Bitmap在你处理完之后你无需释放它，ImageLoader会去处理的；
>* 在处理的过程中如果你多次创建了新的Bitmap，那么在你用完之后一定要记得释放。
>* 如果processSize不为null，那么你新创建的Bitmap的尺寸一定要跟processSize一致

####10.BitmapDisplayer（图片显示器）
BitmapDisplayer是最后用来显示图片的，你可以通过BitmapDisplayer来以不同的动画来显示图片，默认提供以下三种（默认采用的是DefaultBitmapDisplayer）：
>* DefaultBitmapDisplayer： 默认的图片显示器，没有任何动画效果。
>* ZoomInBitmapDisplayer：由小到大效果显示器，比例是0.5f到1.0f。
>* ZoomOutBitmapDisplayer：由大到小效果显示器，比例是1.5f到1.0f。
>* OriginalFadeInBitmapDisplayer： 原图过渡效果显示器，如果ImageView当前显示的有图片就用已有的图片和新的图片生成一个过渡图片（TransitionDrawable）。
>* ColorFadeInBitmapDisplayer：颜色过渡显示器，你可以指定一种颜色作为过渡效果的起始色。

注意：请慎用过渡效果显示器，因为当两张图片的尺寸不一致，但其中一张的尺寸非常接近ImageView时，TransitionDrawable会依这张图片作为标准强行将另一张图片拉伸

####11.Downloader（图片下载器）
Downloader是用来下载图片的，默认的实现是LockDownloader。LockDownloader会根据下载地址加锁，防止重复下载

###你还可以参考示例程序来更加直观的了解使用方式

##Downloads
>* [android-happy-image-loader-2.4.0.jar](https://github.com/xiaopansky/HappyImageLoader/raw/master/releases/android-happy-image-loader-2.4.0.jar)
>* [android-happy-image-loader-2.4.0-with-src.jar](https://github.com/xiaopansky/HappyImageLoader/raw/master/releases/android-happy-image-loader-2.4.0-with-src.jar)

dependencies
>* [android-support-v4.jar](https://github.com/xiaopansky/HappyImageLoader/raw/master/libs/android-support-v4.jar)

##Change Log
####2.4.0
>* 默认的BitmapDisplayer不再采用FadeInBitmapDisplayer，而采用了新增的DefaultBitmapDisplayer
>* FadeInBitmapDisplayer改名为OriginalFadeInBitmapDisplayer
>* ZoomInBitmapDisplayer和ZoomOutBitmapDisplayer增加了几个构造函数，方便自定义缩放比例和插值器
>* 新增DefaultBitmapDisplayer和ColorFadeInBitmapDisplayer
>* 新增TailorBitmapProcessor
>* LruMemoryCache增加LruMemoryCache(int maxSize)构造函数，方便自定义内存缓存容量
>* download()、load()、display()都增加了多种重载函数，方便直接加载不同类型的图片

####2.3.6
>* 修复进度更新延迟很严重BUG
>* 默认关闭进度更新功能，你可以通过Options.setEnableProgressCallback(true)方法开启

####2.3.5
>* 增加Downloader来管理图片下载，删除了HttpClientCreator
>* 当请求已经取消时新的Downloader将不再读取数据，这么做是为了更快的处理新的请求
>* 修复当同一个下载地址被多次请求时从第二个开始的请求可能会失败的BUG。原因是在第一个请求正在写入数据的过程中，第二个请求发起了，第二个请求检测到已经有了缓存文件，于是就去读取并解码。但实际上这时候的缓存文件是不完整的，所以就会解码失败。

####2.3.4
>* ImageLoader和DiskCache中增加``public File getCacheFileByUri(String uri)``方法，方便开发者将缓存图片用作它途

####2.3.3
>* ImageLoader类增加清除缓存的方法
>* 当检查ContentType发现不是图片时抛出异常并在控制台打印，方便开发者发现问题
>* HttpClient增加UserAgent，防止个别网站由于没有UserAgent而导致下载失败
>* 优化默认提供的三种处理器的处理效果，特别是倒影处理器，当限制处理尺寸时，会截取原图中的一部分来绘制倒影图片，保证不会显示变形

####2.3.2
>* 当无需取消的时候更新其DisplayListener
>* 优化网络部分，解决会偶尔解码失败的bug
>* DisplayOptions增加设置是否使用BitmapProcessor处理默认图片的方法

####2.3.1
>* 本次更新主要是重命名一些方法和参数，以及补充一下注释，详情请参考示例代码

####2.3.0
>* 重新梳理代码处理逻辑
>* 增加load()和download()方法
>* 优化CircleBitmapProcessor和RoundedCornerBitmapProcessor处理逻辑
>* 整体大升级，提高稳定性以及处理效率

####2.2.1
>* 更新版权信息
>* 重命名SimpleBitmapDecoder为BaseBitmapDecoder

####2.2.0
>* 去掉初始化方法（init(Context)），不再需要初始化
>* getInstance()方法增加Context参数，初始化的工作移到了这里

##License
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
