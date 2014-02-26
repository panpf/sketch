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
###使用Options
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

###利用Configuration().putOptions()来管理多个Options

###自定义任务执行器（TaskExecutor）

###自定义图片缓存器（BitmapCacher）

###自定义图片解码器（BitmapDecoder）

###自定义图片下载器（ImageDownloader）

###自定义图片处理器（BitmapProcessor）

###自定义图片显示器（BitmapDisplayer）

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
