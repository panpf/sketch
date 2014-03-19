package me.xiaopan.android.imageloader.sample.fragment;

import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.sample.DisplayOptionsType;
import me.xiaopan.android.imageloader.task.DisplayListener;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageFragment extends Fragment {
	public static final String PARAM_REQUIRED_IMAGE_URI = "PARAM_REQUIRED_IMAGE_URI";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		String uri = null;
		Bundle bundle = getArguments();
		if(bundle != null){
			uri = bundle.getString(PARAM_REQUIRED_IMAGE_URI);
		}
		if(uri != null){
			View rootView = inflater.inflate(R.layout.fragment_image, null);
			ImageView imageView = (ImageView) rootView.findViewById(R.id.image_imageFragment_image);
			
			final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress_imageFragment_progress);
			
			ImageLoader.getInstance(getActivity()).display(uri, imageView, DisplayOptionsType.VIEW_PAGER, new DisplayListener() {
				@Override
				public void onStarted(String imageUri, ImageView imageView) {
					progressBar.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onFailed(String imageUri, ImageView imageView) {
					progressBar.setVisibility(View.GONE);
				}
				
				@Override
				public void onComplete(String imageUri, ImageView imageView, BitmapDrawable drawable) {
					progressBar.setVisibility(View.GONE);
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
