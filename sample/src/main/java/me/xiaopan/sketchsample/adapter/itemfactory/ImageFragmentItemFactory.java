package me.xiaopan.sketchsample.adapter.itemfactory;

import android.support.v4.app.Fragment;

import me.xiaopan.assemblyadapter.AssemblyFragmentItemFactory;
import me.xiaopan.sketchsample.fragment.ImageFragment;

public class ImageFragmentItemFactory extends AssemblyFragmentItemFactory<String> {
    private String loadingImageOptionsId;

    public ImageFragmentItemFactory(String loadingImageOptionsId) {
        this.loadingImageOptionsId = loadingImageOptionsId;
    }

    @Override
    public boolean isTarget(Object o) {
        return o instanceof String;
    }

    @Override
    public Fragment createFragment(int i, String uri) {
        return ImageFragment.build(uri, loadingImageOptionsId);
    }
}
