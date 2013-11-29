package me.xiaopan.easy.imageloader.sample;

import java.util.ArrayList;
import java.util.List;

import me.xiaoapn.easy.imagelader.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity {
	ViewPager viewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewPager = (ViewPager) findViewById(R.id.viewPager_main);
		
		List<TitleFragment> fragments = new ArrayList<TitleFragment>();
		
		Bundle veryLargeListBundle = new Bundle();
		veryLargeListBundle.putString(ImageListFragment.PARAM_REQUIRED_STRING_NAME, "超大图ListView");
		veryLargeListBundle.putStringArray(ImageListFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.urls_veryLarge));
		ImageListFragment veryLargeListFragment = new ImageListFragment();
		veryLargeListFragment.setArguments(veryLargeListBundle);
		fragments.add(veryLargeListFragment);
		
		Bundle smallListBundle = new Bundle();
		smallListBundle.putString(ImageListFragment.PARAM_REQUIRED_STRING_NAME, "小图ListView");
		smallListBundle.putStringArray(ImageListFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.urls_small));
		ImageListFragment smallListFragment = new ImageListFragment();
		smallListFragment.setArguments(smallListBundle);
		fragments.add(smallListFragment);
		
		Bundle veryLargeGridBundle = new Bundle();
		veryLargeGridBundle.putString(ImageGridFragment.PARAM_REQUIRED_STRING_NAME, "超大图ListView");
		veryLargeGridBundle.putStringArray(ImageGridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.urls_veryLarge));
		ImageGridFragment veryLargeGridFragment = new ImageGridFragment();
		veryLargeGridFragment.setArguments(veryLargeGridBundle);
		fragments.add(veryLargeGridFragment);
		
		Bundle smallGridBundle = new Bundle();
		smallGridBundle.putString(ImageGridFragment.PARAM_REQUIRED_STRING_NAME, "小图ListView");
		smallGridBundle.putStringArray(ImageGridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.urls_small));
		ImageGridFragment smallGridFragment = new ImageGridFragment();
		smallGridFragment.setArguments(smallGridBundle);
		fragments.add(smallGridFragment);
		
		Bundle galleryBundle = new Bundle();
		galleryBundle.putString(ImageGalleryFragment.PARAM_REQUIRED_STRING_NAME, "Gallery");
		galleryBundle.putStringArray(ImageGalleryFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.urls_small));
		ImageGalleryFragment galleryFragment = new ImageGalleryFragment();
		galleryFragment.setArguments(galleryBundle);
		fragments.add(galleryFragment);
		
		fragments.add(new ImageFragment());

		viewPager.setAdapter(new TitleFragmentPagerAdapter(getSupportFragmentManager(), fragments));
	}
}
