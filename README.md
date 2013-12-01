# ![Logo](https://github.com/ixiaopan/EasyImageLoader/raw/master/res/drawable-mdpi/ic_launcher.png) EasyImageLoader

EasyImageLoader是用在Android上的一个图片加载类库，主要用于从本地或网络加载图片并显示在ImageView上。

##特征

>* 异步加载。采用线程池（默认容量是20）来处理每一个请求，当线程池负荷已满的时候，新的加载请求会被放到一个有界等待队列（默认容量是10）中，这样就可以保证最新的请求会被及时的处理。

>* 支持缓存。支持在本地或内存中（默认采用lru算法来缓存Bitmap，且最大容量为可用内存的八分之一）缓存图片数据，并且可定义缓存有效期。

>* 强大的自定义功能。通过Options类你可以自定义每一个请求的动画、默认图片、加载失败图片、图片加载处理器、超时重试次数、缓存目录等功能。

>* 支持ViewHolder。即使你在ListView中使用了ViewHolder也依然可以使用ImageLoader来加载图片，并且图片显示绝对不会混乱。

>* 重复下载过滤。如果两个请求的图片地址一样的话，是第二个是不会重复下载的，当第一个下载下载完成的时候会先后显示在两个请求上指定的ImageView上。


##示例

###然后在Application中初始化ImagLoader

```java
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		//初始化默认的Options，当调用ImageLoader.getInstance().load()方法却没有指定Options的时候会默认使用此Options
		Options defaultOptions = ImageLoader.getInstance().getConfiguration().getDefaultOptions();
		defaultOptions.setLoadingImageResource(R.drawable.images_loading);	//设置加载中显示的图片
		defaultOptions.setLoadFailureImageResource(R.drawable.images_load_failure); 	//设置加载失败时显示的图片
		defaultOptions.setShowAnimationListener(new AlphaScaleShowAnimationListener());
		
		ImageLoader.getInstance().setDebugMode(true);
	}
}
```

###使用ImageLoader

```java
ImageLoader.getInstance().load(imageUrls[position], viewHolder.image);
```

##注意事项

1. 在使用ImageLoader之前你最好在Application中设置一下默认Options的加载中图片和加载失败图片

2. ImageLoader提供了一个单例，所以没有特殊需求的话，你只须通过ImageLoader.getInstance()方法获取其实例即可。

##下载
**[easy-image-loader-1.0.0.jar](https://github.com/ixiaopan/EasyImageLoader/raw/master/downloads/easy-image-loader-1.0.0.jar)**
