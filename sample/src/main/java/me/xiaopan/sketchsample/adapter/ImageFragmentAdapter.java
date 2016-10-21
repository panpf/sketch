package me.xiaopan.sketchsample.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import me.xiaopan.sketchsample.fragment.ImageFragment;

public class ImageFragmentAdapter extends FragmentStatePagerAdapter {
    private List<String> uris;
    private String loadingImageOptionsInfo;

    public ImageFragmentAdapter(FragmentManager fm, List<String> uris, String loadingImageOptionsInfo) {
        super(fm);
        this.uris = uris;
        this.loadingImageOptionsInfo = loadingImageOptionsInfo;
    }

    @Override
    public int getCount() {
        return uris.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return ImageFragment.build(uris.get(arg0), loadingImageOptionsInfo);
    }
}