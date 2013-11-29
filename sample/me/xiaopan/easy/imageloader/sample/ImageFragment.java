package me.xiaopan.easy.imageloader.sample;

import me.xiaoapn.easy.imagelader.R;
import me.xiaoapn.easy.imageloader.ImageLoader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends TitleFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_image, null);
		ImageView imageView = (ImageView) rootView.findViewById(R.id.image1);
		ImageLoader.getInstance().load("http://s1.dwstatic.com/group1/M00/AF/39/cfc4623b24057a642f6e812269175ada.jpg", imageView);
		ImageView imageView2 = (ImageView) rootView.findViewById(R.id.image2);
		ImageLoader.getInstance().load("http://s1.dwstatic.com/group1/M00/98/47/db8bbf7cf28ac4d4ce101ed5d2683ab0.jpg", imageView2);
		return rootView;
	}
	
	@Override
	public String getTitle() {
		return "使用默认选项加载图片";
	}
}