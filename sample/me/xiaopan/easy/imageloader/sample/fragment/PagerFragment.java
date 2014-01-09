package me.xiaopan.easy.imageloader.sample.fragment;

import me.xiaopan.easy.imageloader.sample.adapter.ImageFragmentAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PagerFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewPager viewPager = new ViewPager(getActivity());
		viewPager.setId(444754121);
		viewPager.setBackgroundColor(Color.BLACK);
		viewPager.setAdapter(new ImageFragmentAdapter(getFragmentManager(), getArguments().getStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS)));
		return viewPager;
	}
}
