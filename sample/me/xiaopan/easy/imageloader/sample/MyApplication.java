package me.xiaopan.easy.imageloader.sample;

import me.xiaoapn.easy.imagelader.R;
import me.xiaoapn.easy.imageloader.AlphaScaleShowAnimationListener;
import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.Options;
import me.xiaoapn.easy.imageloader.OptionsFactory;
import android.app.Application;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Options defaultOptions = ImageLoader.getInstance().getConfiguration().getDefaultOptions();
		defaultOptions.setLoadingImageResource(R.drawable.images_loading);
		defaultOptions.setLoadFailureImageResource(R.drawable.images_load_failure);
		defaultOptions.setShowAnimationListener(new AlphaScaleShowAnimationListener());
		
		Options listOptions = OptionsFactory.getListOptions();
		listOptions.setLoadingImageResource(R.drawable.images_loading);
		listOptions.setLoadFailureImageResource(R.drawable.images_load_failure);
		listOptions.setShowAnimationListener(new AlphaScaleShowAnimationListener());
		
		ImageLoader.getInstance().setDebugMode(true);
	}
}