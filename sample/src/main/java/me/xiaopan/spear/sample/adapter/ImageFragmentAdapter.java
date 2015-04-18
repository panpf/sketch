package me.xiaopan.spear.sample.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import me.xiaopan.spear.sample.fragment.ImageFragment;

public class ImageFragmentAdapter extends FragmentStatePagerAdapter {
    private List<String> uris;

    public ImageFragmentAdapter(FragmentManager fm, List<String> uris) {
        super(fm);
        this.uris = uris;
    }

    @Override
    public int getCount() {
        return uris.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ImageFragment.PARAM_REQUIRED_IMAGE_URI, uris.get(arg0));
        imageFragment.setArguments(bundle);
        return imageFragment;
    }
}