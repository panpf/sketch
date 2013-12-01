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
		
		Bundle largeListBundle = new Bundle();
		largeListBundle.putString(ImageListFragment.PARAM_REQUIRED_STRING_NAME, "ListView（超大图）");
		largeListBundle.putStringArray(ImageListFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.urls_veryLarge));
		ImageListFragment largeListFragment = new ImageListFragment();
		largeListFragment.setArguments(largeListBundle);
		fragments.add(largeListFragment);
		
		Bundle largeGridBundle = new Bundle();
		largeGridBundle.putString(ImageGridFragment.PARAM_REQUIRED_STRING_NAME, "GridView（超大图）");
		largeGridBundle.putStringArray(ImageGridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.urls_veryLarge));
		ImageGridFragment veryLargeGridFragment = new ImageGridFragment();
		veryLargeGridFragment.setArguments(largeGridBundle);
		fragments.add(veryLargeGridFragment);
		
		Bundle largeGalleryBundle = new Bundle();
		largeGalleryBundle.putString(ImageGalleryFragment.PARAM_REQUIRED_STRING_NAME, "Gallery（超大图）");
		largeGalleryBundle.putStringArray(ImageGalleryFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.urls_veryLarge));
		ImageGalleryFragment largeGalleryFragment = new ImageGalleryFragment();
		largeGalleryFragment.setArguments(largeGalleryBundle);
		fragments.add(largeGalleryFragment);
		
		Bundle smallListBundle = new Bundle();
		smallListBundle.putString(ImageListFragment.PARAM_REQUIRED_STRING_NAME, "ListView（小图）");
		smallListBundle.putStringArray(ImageListFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.urls_small));
		ImageListFragment smallListFragment = new ImageListFragment();
		smallListFragment.setArguments(smallListBundle);
		fragments.add(smallListFragment);
		
		Bundle smallGridBundle = new Bundle();
		smallGridBundle.putString(ImageGridFragment.PARAM_REQUIRED_STRING_NAME, "GridView（小图）");
		smallGridBundle.putStringArray(ImageGridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.urls_small));
		ImageGridFragment smallGridFragment = new ImageGridFragment();
		smallGridFragment.setArguments(smallGridBundle);
		fragments.add(smallGridFragment);
		
		Bundle galleryBundle = new Bundle();
		galleryBundle.putString(ImageGalleryFragment.PARAM_REQUIRED_STRING_NAME, "Gallery（小图）");
		galleryBundle.putStringArray(ImageGalleryFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.urls_small));
		ImageGalleryFragment galleryFragment = new ImageGalleryFragment();
		galleryFragment.setArguments(galleryBundle);
		fragments.add(galleryFragment);
		
		fragments.add(new ImageFragment());

		viewPager.setAdapter(new TitleFragmentPagerAdapter(getSupportFragmentManager(), fragments));
	}
}
