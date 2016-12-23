package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.assemblyadapter.FragmentArrayPagerAdapter;
import me.xiaopan.psts.PagerSlidingTabStrip;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketchsample.AssetImage;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;

/**
 * 大图页面，用来展示Sketch显示大图的能力
 */
@InjectContentView(R.layout.fragment_pager)
public class LargeImageTestFragment extends MyFragment {
    @InjectView(R.id.pager_pagerFragment_content)
    private ViewPager viewPager;

    private GetLargeTagStripListener getPagerSlidingTagStripListener;
    private FragmentArrayPagerAdapter fragmentAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof GetLargeTagStripListener) {
            getPagerSlidingTagStripListener = (GetLargeTagStripListener) activity;
        } else {
            getPagerSlidingTagStripListener = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getPagerSlidingTagStripListener != null) {
            getPagerSlidingTagStripListener = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (fragmentAdapter == null) {
            String[] largeAssetImageNames = AssetImage.LARGES;
            Fragment[] fragments = new Fragment[largeAssetImageNames.length];
            for(int w = 0; w < largeAssetImageNames.length; w++){
                fragments[w] = ImageFragment.build(UriScheme.ASSET.createUri(largeAssetImageNames[w]), null);
            }
            fragmentAdapter = new FragmentArrayPagerAdapter(getChildFragmentManager(), fragments);
        }
        viewPager.setAdapter(fragmentAdapter);
        getPagerSlidingTagStripListener.onGetLargeTabStrip().setViewPager(viewPager);
    }

    public interface GetLargeTagStripListener {
        PagerSlidingTabStrip onGetLargeTabStrip();
    }
}
