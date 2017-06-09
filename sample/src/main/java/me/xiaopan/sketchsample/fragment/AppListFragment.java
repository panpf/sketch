package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.FragmentArrayPagerAdapter;
import me.xiaopan.psts.PagerSlidingTabStrip;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;

/**
 * App列表页面，用来展示已安装APP和本地APK列表
 */
@BindContentView(R.layout.fragment_pager)
public class AppListFragment extends BaseFragment {
    @BindView(R.id.pager_pagerFragment_content)
    ViewPager viewPager;

    private GetAppListTagStripListener getPagerSlidingTagStripListener;
    private FragmentArrayPagerAdapter fragmentAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof GetAppListTagStripListener) {
            getPagerSlidingTagStripListener = (GetAppListTagStripListener) activity;
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
            Fragment[] fragments = new Fragment[2];
            fragments[0] = new InstalledAppFragment();
            fragments[1] = new AppPackageListFragment();
            fragmentAdapter = new FragmentArrayPagerAdapter(getChildFragmentManager(), fragments);
        }
        viewPager.setAdapter(fragmentAdapter);
        getPagerSlidingTagStripListener.onGetAppListTabStrip().setViewPager(viewPager);
    }

    public interface GetAppListTagStripListener {
        PagerSlidingTabStrip onGetAppListTabStrip();
    }
}
