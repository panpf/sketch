package me.xiaopan.easy.imageloader.sample;

import me.xiaoapn.easy.imagelader.R;
import me.xiaoapn.easy.imageloader.AlphaScaleShowAnimationListener;
import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.Options;
import android.app.Application;

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