package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.assemblyadapter.FragmentArrayPagerAdapter;
import me.xiaopan.psts.PagerSlidingTabStrip;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.MainActivity;

@InjectContentView(R.layout.fragment_pager_tab)
public class ProcessorTestFragment extends MyFragment {
    @InjectView(R.id.tab_pagerTabFragment_tabs)
    private PagerSlidingTabStrip tabStrip;

    @InjectView(R.id.pager_pagerTabFragment_content)
    private ViewPager viewPager;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setAdapter(new FragmentArrayPagerAdapter(getChildFragmentManager(), new Fragment[]{
                new ReflectionTestFragment(),
                new GaussianBlurTestFragment(),
                new RotateTestFragment(),
                new RoundRectTestFragment(),
                new CircleTestFragment(),
                new ResizeTestFragment(),
        }));

        tabStrip.setTabViewFactory(new MainActivity.TitleTabFactory(new String[]{
                "REFLECTION",
                "GAUSSIAN_BLUR",
                "ROTATE",
                "ROUND_RECT",
                "CIRCLE",
                "RESIZE",
        }, getActivity()));
        tabStrip.setViewPager(viewPager);
    }
}
