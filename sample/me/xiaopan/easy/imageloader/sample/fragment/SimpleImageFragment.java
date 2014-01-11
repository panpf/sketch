package me.xiaopan.easy.imageloader.sample.fragment;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.R;
import me.xiaoapn.easy.imageloader.task.ImageLoadListener;
import me.xiaopan.easy.imageloader.sample.OptionsType;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SimpleImageFragment extends Fragment {
	public static final String PARAM_REQUIRED_IMAGE_URI = "PARAM_REQUIRED_IMAGE_URI";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		String uri = null;
		Bundle bundle = getArguments();
		if(bundle != null){
			uri = bundle.getString(PARAM_REQUIRED_IMAGE_URI);
		}
		if(uri != null){
			View rootView = inflater.inflate(R.layout.fragment_simple_image, null);
			final ProgressBar progressBar1 = (ProgressBar) rootView.findViewById(R.id.progress_simpleImage_1);
			ImageLoader.getInstance().display(uri, (ImageView) rootView.findViewById(R.id.image_simpleImage_1), OptionsType.SIMPLE, new ImageLoadListener() {
				@Override
				public void onStarted(String imageUri, ImageView imageView) {
					progressBar1.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onFailed(String imageUri, ImageView imageView) {
					progressBar1.setVisibility(View.GONE);
				}
				
				@Override
				public void onComplete(String imageUri, ImageView imageView, BitmapDrawable drawable) {
					progressBar1.setVisibility(View.GONE);
				}
				
				@Override
				public void onCancelled(String imageUri, ImageView imageView) {
				}
			});
			
			final ProgressBar progressBar2 = (ProgressBar) rootView.findViewById(R.id.progress_simpleImage_2);
			ImageLoader.getInstance().display(uri, (ImageView) rootView.findViewById(R.id.image_simpleImage_2), OptionsType.SIMPLE, new ImageLoadListener() {
				@Override
				public void onStarted(String imageUri, ImageView imageView) {
					progressBar2.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onFailed(String imageUri, ImageView imageView) {
					progressBar2.setVisibility(View.GONE);
				}
				
				@Override
				public void onComplete(String imageUri, ImageView imageView, BitmapDrawable drawable) {
					progressBar2.setVisibility(View.GONE);
				}
				
				@Override
				public void onCancelled(String imageUri, ImageView imageView) {
				}
			});
			return rootView;
		}else{
			return null;
		}
	}
}
