package me.xiaopan.spear.sample.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.inject.app.InjectFragment;
import me.xiaopan.android.widget.PagerSlidingTabStrip;
import me.xiaopan.spear.sample.R;
import me.xiaopan.spear.sample.adapter.FragmentAdapter;

/**
 * 明星首页
 */
@InjectContentView(R.layout.fragment_star_index)
public class StarIndexFragment extends InjectFragment{
    @InjectView(R.id.pager_star_content) private ViewPager viewPager;
    private GetStarTagStripListener getPagerSlidingTagStripListener;
    private FragmentAdapter fragmentAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof GetStarTagStripListener){
            getPagerSlidingTagStripListener = (GetStarTagStripListener) activity;
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
            fragments[0] = new HotStarFragment();
            fragments[1] = new StarCatalogFragment();
            fragmentAdapter = new FragmentAdapter(getChildFragmentManager(), fragments);
        }
        viewPager.setAdapter(fragmentAdapter);
        getPagerSlidingTagStripListener.onGetStarTabStrip().setViewPager(viewPager);
    }

    public interface GetStarTagStripListener{
        public PagerSlidingTabStrip onGetStarTabStrip();
    }
}
