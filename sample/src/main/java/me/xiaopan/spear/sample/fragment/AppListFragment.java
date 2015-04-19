package me.xiaopan.spear.sample.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.widget.PagerSlidingTabStrip;
import me.xiaopan.spear.sample.MyFragment;
import me.xiaopan.spear.sample.R;
import me.xiaopan.spear.sample.adapter.FragmentAdapter;

/**
 * App列表页面，用来展示已安装APP和本地APK列表
 */
@InjectContentView(R.layout.fragment_app_list)
public class AppListFragment extends MyFragment {
    @InjectView(R.id.pager_appList_content) private ViewPager viewPager;
    private GetAppListTagStripListener getPagerSlidingTagStripListener;
    private FragmentAdapter fragmentAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof GetAppListTagStripListener){
            getPagerSlidingTagStripListener = (GetAppListTagStripListener) activity;
        }else{
            getPagerSlidingTagStripListener = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(getPagerSlidingTagStripListener != null){
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
        if(fragmentAdapter == null){
            Fragment[] fragments = new Fragment[2];
            fragments[0] = new InstalledAppFragment();
            fragments[1] = new AppPackageFragment();
            fragmentAdapter = new FragmentAdapter(getChildFragmentManager(), fragments);
        }
        viewPager.setAdapter(fragmentAdapter);
        getPagerSlidingTagStripListener.onGetAppListTabStrip().setViewPager(viewPager);
    }

    public interface GetAppListTagStripListener{
        PagerSlidingTabStrip onGetAppListTabStrip();
    }
}
