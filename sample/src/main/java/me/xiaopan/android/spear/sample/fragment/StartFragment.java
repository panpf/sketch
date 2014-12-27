package me.xiaopan.android.spear.sample.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.inject.app.InjectFragment;
import me.xiaopan.android.widget.PagerSlidingTabStrip;

@InjectContentView(R.layout.fragment_start)
public class StartFragment extends InjectFragment{
    @InjectView(R.id.pager_start_content) private ViewPager viewPager;
    private GetPagerSlidingTagStripListener getPagerSlidingTagStripListener;
    private ContentAdapter contentAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof GetPagerSlidingTagStripListener){
            getPagerSlidingTagStripListener = (GetPagerSlidingTagStripListener) activity;
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
        if(contentAdapter == null){
            Fragment[] fragments = new Fragment[2];
            fragments[0] = new HotStarFragment();
            fragments[1] = new StarCatalogFragment();
            contentAdapter = new ContentAdapter(getChildFragmentManager(), fragments);
        }
        viewPager.setAdapter(contentAdapter);
        getPagerSlidingTagStripListener.onGetPagerSlidingTabStrip().setViewPager(viewPager);
    }

    private static class ContentAdapter extends FragmentPagerAdapter {
        private Fragment[] fragments;

        public ContentAdapter(FragmentManager fm, Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

    public interface GetPagerSlidingTagStripListener{
        public PagerSlidingTabStrip onGetPagerSlidingTabStrip();
    }
}
