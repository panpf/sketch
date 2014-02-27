# ![Logo](https://github.com/xiaopansky/Android-ImageLoader/raw/master/res/drawable-mdpi/ic_launcher.png) Android-ImageLoader

Android-ImageLoader是用在Android上的一个图片加载类库，主要用于从本地或网络加载图片并显示在ImageView上，最低兼容Android2.2

![sample](https://github.com/xiaopansky/Android-ImageLoader/raw/master/docs/sample.jpg)

##Features

>* 异步加载。采用线程池来处理每一个请求，并且网络加载和本地加载会放在不同的线程池中执行，保证不会因为网络加载而堵塞本地加载。

>* 支持缓存。支持在本地或内存中缓存图片，内存缓存采用Android扩展包中的LruCache来缓存，且最大容量为可用内存的八分之一，本地缓存可定义有效期，和最大容量（超过最大容量时或当前存储不够用时会自动根据活跃度清除不活跃的本地缓存文件）。

>* 强大的自定义功能。通过Configuration类你可以自定义缓存器、解码器以及下载器；通过Options类你可以自定义缓存策略、Bitmap处理器、Bitmap显示器、以及不同状态时的图片。

>* 支持ViewHolder。即使你在ListView中使用了ViewHolder也依然可以使用ImageLoader来加载图片，并且图片显示绝对不会混乱。

>* 重复下载过滤。如果两个请求的图片地址一样的话，第二个就会等待，一直到第一个下载成功后才会继续处理。

##Usage

###1.定义加载选项
```java
Options defaultOptions = new Options(getBaseContext())
	.setLoadingDrawableResId(R.drawable.image_loading)
	.setFailureDrawableResId(R.drawable.image_load_failure);
```

###2.显示图片
你可以在任何地方调用以下代码来显示图片
```java
ImageLoader.getInstance(getContext()).display(imageUri, imageView, defaultOptions);
```
不管你是在Adapter的getView()中调用还是在Activity的onCrate()中调用都不会显示混乱。

##Extend
###1.使用Options
```java
Options defaultOptions2 = new Options(getBaseContext())
	.setEmptyDrawableResId(R.drawable.image_load_failure)	//设置当uri为空时显示的图片
	.setLoadingDrawableResId(R.drawable.image_loading)	//设置当正在加载显示的图片
	.setFailureDrawableResId(R.drawable.image_load_failure)	//设置当加载失败时显示的图片
	.setEnableMenoryCache(true)	//开启内存缓存，开启后会采用Lru算法将Bitmap缓存在内存中，以便重复利用
	.setEnableDiskCache(true)	//开启硬盘缓存，开启后会先将图片下载到本地，然后再加载到内存中
	.setDiskCachePeriodOfValidity(1000 * 60 * 60 * 24)	//设置硬盘缓存有效期为24小时，24小时过后将重新下载图片
	.setImageMaxSize(new ImageSize(getBaseContext().getResources().getDisplayMetrics().widthPixels, getBaseContext().getResources().getDisplayMetrics().heightPixels))	//设置加载到内存中的图片的最大尺寸，如果原图的尺寸大于最大尺寸，在读取的时候就会缩小至合适的尺寸再读取
	.setMaxRetryCount(2)	//设置最大重试次数，当连接超时时会再次尝试下载
	.setBitmapProcessor(new ReflectionBitmapProcessor())	//设置Bitmap处理器，当图片从本地读取内存中后会使用BitmapProcessor将图片处理一下，因此你可以通过BitmapProcessor将图片处理成任何你想要的效果
	.setBitmapDisplayer(new FadeInBitmapDisplayer());	//设置图片显示器，在处理完图片之后会调用BitmapDisplayer来显示图片，因此你可以通过BitmapDisplayer自定义任何你想要的方式来显示图片
```
另外Options默认的配置是：
>* 开启内存缓存和硬盘缓存
>* 图片最大尺寸为当前设备屏幕的尺寸
>* 图片显示器为FadeInBitmapDisplayer（渐入效果）
>* 最大重试次数为2

```java
public Options(Context context) {
	setEnableMenoryCache(true)
	.setEnableDiskCache(true)
	.setImageMaxSize(new ImageSize(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels))
	.setBitmapDisplayer(new FadeInBitmapDisplayer())
	.setMaxRetryCount(2);
}
```

###2.利用Configuration().putOptions()来管理多个Options
当你有多个Options的时候你要怎么去管理并方便的使用呢？别担心我已经为你提供了一个绝对可行的解决方案。

首先你需要定义一个枚举类来作为Options的标签，如下：
```java
public enum OptionsType {
	/**
	 * 默认的
	 */
	DEFAULT, 
	
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
	VIEW_PAGER;
}
```
然后定义多个Options，且通过ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(Enum<?> enum, Options options)方法将Options和Enum绑定并放进Configuratin中
```java
Options defaultOptions = new Options(getBaseContext())
	.setLoadingDrawableResId(R.drawable.image_loading)
	.setFailureDrawableResId(R.drawable.image_load_failure)
	.setBitmapProcessor(new ReflectionBitmapProcessor());
ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(OptionsType.DEFAULT, defaultOptions);

Options listOptions = new Options(getBaseContext())
	.setLoadingDrawableResId(R.drawable.image_loading)
	.setFailureDrawableResId(R.drawable.image_load_failure)
	.setBitmapProcessor(new CircleBitmapProcessor());
ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(OptionsType.LIST_VIEW, listOptions);

Options gridOptions = new Options(getBaseContext())
	.setLoadingDrawableResId(R.drawable.image_loading)
	.setFailureDrawableResId(R.drawable.image_load_failure)
	.setEnableMenoryCache(false)
ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(OptionsType.SIMPLE, gridOptions);

Options galleryOptions = new Options(getBaseContext())
	.setLoadingDrawableResId(R.drawable.image_loading)
	.setFailureDrawableResId(R.drawable.image_load_failure)
	.setBitmapProcessor(new RoundedCornerBitmapProcessor());
ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(OptionsType.GALLERY, galleryOptions);

Options viewPagerOptions = new Options(getBaseContext())
	.setFailureDrawableResId(R.drawable.image_load_failure)
	.setBitmapDisplayer(new ZoomOutBitmapDisplayer());
ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(OptionsType.VIEW_PAGER, viewPagerOptions);
```
然后在使用的时候就可以调用ImageLoader.getInstance(context).display(String imageUri, ImageView imageView, Enum<?> optionsName)方法来传入对应的枚举来显示图片了，ImageLoader会根据你传入的枚举从Configuration中取出对应的Options。
```java
ImageLoader.getInstance(context).display(imageUrls[position], viewHolder.image, OptionsType.GALLERY);
```
注意：如果无法从Configuration中获取Options的话ImageLoader就会创建一个默认的Options来继续加载，如下所示：
```java
if(options == null){
	options = new Options(configuration.getContext());
}
```

###3.自定义TaskExecutor（任务执行器）
默认采用的是BaseTaskExecutor，那么先介绍下BaseTaskExecutor的特性吧
>* 首先BaseTaskExecutor将任务分成了两种，一种是耗时较长的需要从网络下载图片的``网络任务``，另一种是从本地加载的``本地任务``。这两种任务会分别放在不同的线程池中执行，``网络任务线程池``核心线程数``5``个，最大线程数``10``个，而``本地任务线程池``则是核心线程数``1``个，最大线程数也是``1``个，这样一来可以保证不会因为网络任务而堵塞了本地任务的加载，并且本地任务可以一个一个加载。
>* 任务等待区采用的是有界队列，长度是20，这样可以保证在能够及时加载最新的任务。

如果你了解了BaseTaskExecutor的特性后依然感觉BaseTaskExecutor无法满足你的需求的话，你可以通过实现TaskExecutor接口来自定义你的TaskExecutor，然后调用ImageLoader.getInstance(getBaseContext()).getConfiguration().setTaskExecutor(TaskExecutor taskExecutor)方法应用即可，不过建议你在动手实现之前先参考一下BaseTaskExecutor。

###4.自定义BitmapCacher（图片缓存器）
BitmapCacher是用来缓存Bitmap的，包括内存缓存和硬盘缓存，ImageLoader提供了以下两种缓存实现共选择：
>* BitmapLruCacher：内存缓存部分采用LRU（近期最少使用）算法来缓存Bimtap，硬盘缓存部分两者都一样；
>* BitmapSoftReferenceCacher：内存缓存部分采用软引用的方式来缓存Bitmap，硬盘缓存部分两者都一样。由于从Android4.0起虚拟机将变得异常活跃，所以此种缓存方法已经失去了其应有的作用，所以不建议使用。

默认采用的是BitmapLruCacher，如果你想自定义的话只需实现BitmapCacher接口，然后调用ImageLoader.getInstance(getBaseContext()).getConfiguration().setBitmapCacher(BitmapCacher bitmapCacher)方法应用即可，同样建议在动手实现之前先参考一下BitmapLruCacher。

###5.自定义BitmapDecoder（图片解码器）
BitmapDecoder是用来解码Bitmap的，默认的实现是BaseBitmapDecoder，如果你想自定义的话只需实现BitmapDecoder接口，然后调用ImageLoader.getInstance(getBaseContext()).getConfiguration().setBitmapLoader(BitmapDecoder bitmapDecoder)方法应用即可，同样建议在动手实现之前先参考一下BaseBitmapDecoder。

###6.自定义ImageDownloader（图片下载器）
ImageDownloader是用来下载图片的，默认的实现是LockImageDownloader，其唯一的特点就是能够避免同一个URI重复下载。实现原理很简单，LockImageDownloader会为每一个URI生成一个锁，执行下载的时候会先去尝试获取锁，如果这时候这个锁被别人用着，那么就会等待别人执行完释放之后才能继续执行，同样在获取到锁之后或先在本地检查一下是否已经下载好了。如果你想自定义的haunted只需实现ImageDownloader接口，然后调用ImageLoader.getInstance(getBaseContext()).getConfiguration().setImageDownloader(ImageDownloader imageDownloader)方法应用即可，同样建议在动手实现之前先参考一下LockImageDownloader。

###7.自定义BitmapProcessor（图片处理器）
BitmapProcessor是用来在BitmapDecoder解码完图片之后在对图片进行处理的，因此你可以利用BitmapProcessor将图片处理成任何你想要的效果。ImageLoader默认提供了三种BitmapProcessor供你使用：
>* CircleBitmapProcessor：圆形图片处理器，可以将图片处理成圆形的，如示例图所示；
>* ReflectionBitmapProcessor：倒影图片处理器，可以将图片处理成倒影效果的，如示例图所示。另外倒影的高度以及倒影的距离都可以通过构造函数来自定义；
>* RoundedCornerBitmapProcessor：圆角图片处理器，可以将图片处理成圆角的，如示例图所示。另外圆角的半径可以通过构造函数来自定义；

如果你想自定义的话只需实现BitmapProcessor接口，然后调用Options.setBitmapProcessor(BitmapProcessor bitmapProcessor)应用即可，另外有几点需要注意：
>* BitmapProcessor接口有一个叫getTag()的方法，此方法的目的是获取一个能够标识当前BitmapProcessor的字符串用来组装图片的缓存ID。如果本地同一张图片使用不同的BitmapProcessor处理的话，最后的效果是不一样的，那么在内存中的缓存ID就不能一样，所以你要保证getTag()方法返回的字符串一定是独一无二的；
>* 通过BitmapProcessor的process()方法传进去的Bitmap在你处理完之后你无需释放它，ImageLoader会去处理的；
>* 在处理的过程中如果你多次创建了新的Bitmap，那么在你用完之后一定要记得释放。

###8.自定义BitmapDisplayer（图片显示器）
BitmapDisplayer是最后用来显示图片的，你可以通过BitmapDisplayer来以不同的动画来显示图片，默认提供以下三种：
>* FadeInBitmapDisplayer： 渐入效果。
>* ZoomInBitmapDisplayer：渐入且由小到大效果。
>* ZoomOutBitmapDisplayer：渐入且由大到小效果。

如果你想自定义的话只需实现BitmapDisplayer接口，然后调用Options.setBitmapDisplayer(BitmapDisplayer bitmapDisplayer)应用即可。

##Downloads
**[android-image-loader-2.2.1.jar](https://github.com/xiaopansky/Android-ImageLoader/raw/master/releases/android-image-loader-2.2.1.jar)**

**[android-image-loader-2.2.1-with-src.jar](https://github.com/xiaopansky/Android-ImageLoader/raw/master/releases/android-image-loader-2.2.1-with-src.jar)**

##Change Log
###2.2.1
>* 更新版权信息
>* 重命名SimpleBitmapDecoder为BaseBitmapDecoder

###2.2.0
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
