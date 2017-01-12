package me.xiaopan.sketchsample.adapter.itemfactory;

import android.content.Context;
import android.support.v4.app.Fragment;

import me.xiaopan.assemblyadapter.AssemblyFragmentItemFactory;
import me.xiaopan.sketchsample.fragment.ImageFragment;
import me.xiaopan.sketchsample.util.Settings;

public class ImageFragmentItemFactory extends AssemblyFragmentItemFactory<String> {
    private Context context;
    private String loadingImageOptionsId;

    public ImageFragmentItemFactory(Context context, String loadingImageOptionsId) {
        this.context = context;
        this.loadingImageOptionsId = loadingImageOptionsId;
    }

    @Override
    public boolean isTarget(Object o) {
        return o instanceof String;
    }

    @Override
    public Fragment createFragment(int i, String uri) {
        boolean showTools = Settings.getBoolean(context, Settings.PREFERENCE_SHOW_TOOLS_IN_IMAGE_DETAIL);
        return ImageFragment.build(uri, loadingImageOptionsId, showTools);
    }
}
