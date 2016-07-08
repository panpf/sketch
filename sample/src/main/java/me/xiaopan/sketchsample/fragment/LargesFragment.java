package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.psts.PagerSlidingTabStrip;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.FragmentAdapter;

/**
 * 大图页面，用来展示Sketch显示超大图
 */
@InjectContentView(R.layout.fragment_app_list)
public class LargesFragment extends MyFragment {
    @InjectView(R.id.pager_appList_content)
    private ViewPager viewPager;
    private GetLargeTagStripListener getPagerSlidingTagStripListener;
    private FragmentAdapter fragmentAdapter;

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
            Fragment[] fragments = new Fragment[4];
            fragments[0] = LargeImageFragment.build(UriScheme.ASSET.createUri("world_map.jpg"));
            fragments[1] = LargeImageFragment.build(UriScheme.ASSET.createUri("qing_ming_shang_he_tu.jpg"));
            fragments[2] = LargeImageFragment.build(UriScheme.ASSET.createUri("chang_wei_bo.jpg"));
            fragments[3] = LargeImageFragment.build(UriScheme.ASSET.createUri("test_card.png"));
            fragmentAdapter = new FragmentAdapter(getChildFragmentManager(), fragments);
        }
        viewPager.setAdapter(fragmentAdapter);
        getPagerSlidingTagStripListener.onGetLargeTabStrip().setViewPager(viewPager);
    }

    public interface GetLargeTagStripListener {
        PagerSlidingTabStrip onGetLargeTabStrip();
    }
}
