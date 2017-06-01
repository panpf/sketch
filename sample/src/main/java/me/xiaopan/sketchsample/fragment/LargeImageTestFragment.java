package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.assemblyadapter.FragmentArrayPagerAdapter;
import me.xiaopan.psts.PagerSlidingTabStrip;
import me.xiaopan.sketchsample.AssetImage;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.MainActivity;
import me.xiaopan.sketchsample.bean.Image;

/**
 * 大图页面，用来展示Sketch显示大图的能力
 */
@InjectContentView(R.layout.fragment_pager_tab)
public class LargeImageTestFragment extends MyFragment {
    @InjectView(R.id.tab_pagerTabFragment_tabs)
    private PagerSlidingTabStrip tabStrip;

    @InjectView(R.id.pager_pagerTabFragment_content)
    private ViewPager viewPager;

    private FragmentArrayPagerAdapter fragmentAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (fragmentAdapter == null) {
            String[] largeAssetImageNames = AssetImage.LARGES;
            Fragment[] fragments = new Fragment[largeAssetImageNames.length];
            for(int w = 0; w < largeAssetImageNames.length; w++){
                String url = largeAssetImageNames[w];
                fragments[w] = ImageFragment.build(new Image(url, url), null, true);
            }
            fragmentAdapter = new FragmentArrayPagerAdapter(getChildFragmentManager(), fragments);
        }
        viewPager.setAdapter(fragmentAdapter);

        tabStrip.setTabViewFactory(new MainActivity.TitleTabFactory(new String[]{
                "WORLD",
                "QMSHT",
                "CWB",
                "CARD"}, getActivity()));
        tabStrip.setViewPager(viewPager);
    }
}
