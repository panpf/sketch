package me.xiaopan.easy.imageloader.sample.fragment;

import me.xiaoapn.easy.imageloader.ImageLoader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
			ImageView imageView = new ImageView(getActivity());
			ImageLoader.getInstance().display(uri, imageView);
			return imageView;
		}else{
			return null;
		}
	}
}
