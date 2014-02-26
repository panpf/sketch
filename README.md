# ![Logo](https://github.com/xiaopansky/Android-ImageLoader/raw/master/res/drawable-mdpi/ic_launcher.png) Android-ImageLoader

Android-ImageLoader是用在Android上的一个图片加载类库，主要用于从本地或网络加载图片并显示在ImageView上。

![sample1](https://github.com/xiaopansky/Android-ImageLoader/raw/master/docs/sample1.png)
![sample2](https://github.com/xiaopansky/Android-ImageLoader/raw/master/docs/sample2.png)

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

##Downloads
**[android-image-loader-2.2.0.jar](https://github.com/xiaopansky/Android-ImageLoader/raw/master/releases/android-image-loader-2.2.0.jar)**

**[android-image-loader-2.2.0-with-src.jar](https://github.com/xiaopansky/Android-ImageLoader/raw/master/releases/android-image-loader-2.2.0-with-src.jar)**

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
>* 默认开启内存缓存和硬盘缓存
>* 默认图片最大尺寸为当前设备屏幕的尺寸
>* 默认图片处理器为FadeInBitmapDisplayer（渐入效果）
>* 默认最大重试次数为2

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
	.setBitmapProcessor(null);
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
然后在使用的时候就可以调用``ImageLoader.getInstance(context).display(String imageUri, ImageView imageView, Enum<?> optionsName)``方法来传入对应的枚举来显示图片了，ImageLoader会根据你传入的枚举从Configuration中取出对应的Options。
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
>* 首先BaseTaskExecutor将任务分成了两种，一种是耗时较长的需要从网络下载图片的``网络任务``，另一种是从本地加载的``本地任务``。这两种任务会分别放在不同的线程池中执行，``网络任务线程池``核心线程数5个，最大线程数``10``个，而``本地任务线程池``则是核心线程数1个，最大线程数也是``1``个，这样一来可以保证不会因为网络任务而堵塞了本地任务的加载，并且本地任务可以一个一个加载。
>* 任务等待区采用的是有界队列，长度是20，这样可以保证在能够及时记载最新的任务。

如果你了解了BaseTaskExecutor的特性后依然感觉BaseTaskExecutor无法满足你的需求的话，你可以通过实现TaskExecutor接口来自定义你的TaskExecutor，不过建议你在动手实现之前先参考一下BaseTaskExecutor。

自定义好你的TaskExecutor后你只需调用``ImageLoader.getInstance(getBaseContext()).getConfiguration().setTaskExecutor(TaskExecutor taskExecutor)``方法应用即可。

###4.自定义BitmapCacher（图片缓存器）


###5.自定义BitmapDecoder（图片解码器）


###6.自定义ImageDownloader（图片下载器）


###7.自定义BitmapProcessor（图片处理器）


###8.自定义BitmapDisplayer（图片显示器）


具体使用方式可以查看源码中的示例程序

##Change Log

##License
```java
/*
 * Copyright 2013 Peng fei Pan
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
