/*
 * Copyright 2013 Peng fei Pan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.android.spear.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.spear.sample.MyActionBarActivity;
import me.xiaopan.android.spear.sample.fragment.HotStarFragment;
import me.xiaopan.android.spear.sample.fragment.StarCatalogFragment;
import me.xiaopan.android.spear.sample.widget.HintView;
import me.xiaopan.android.widget.PagerSlidingTabStrip;

/**
 * 明星首页
 */
@InjectContentView(R.layout.activity_star)
public class StarActivity extends MyActionBarActivity {
    @InjectView(R.id.tabStrip_star) private PagerSlidingTabStrip titleTabStrip;
    @InjectView(R.id.pager_star) private ViewPager contentPager;
    @InjectView(R.id.hint_star) private HintView hintView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("明星");

        titleTabStrip.setAllowWidthFull(true);
        titleTabStrip.setTabViewFactory(new TitleTabFactory(new String[]{"最热女明星", "最热男明星", "女明星", "男明星"}, getBaseContext()));

        Fragment[] fragments = new Fragment[4];
        fragments[0] = new HotStarFragment();
        fragments[1] = new HotStarFragment();
        fragments[2] = new StarCatalogFragment();
        fragments[3] = new StarCatalogFragment();

        Bundle bundle1 = new Bundle();
        bundle1.putBoolean(HotStarFragment.PARAM_REQUIRED_BOOLEAN_MAN_STAR, true);
        fragments[1].setArguments(bundle1);

        Bundle bundle = new Bundle();
        bundle.putBoolean(StarCatalogFragment.PARAM_REQUIRED_BOOLEAN_MAN_STAR, true);
        fragments[3].setArguments(bundle);

        contentPager.setAdapter(new ContentAdapter(getSupportFragmentManager(), fragments));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        getMenuInflater().inflate(R.menu.menu_photo_album, menu);
        getMenuInflater().inflate(R.menu.menu_github, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_search :
                startActivity(new Intent(getBaseContext(), SearchActivity.class));
                break;
            case R.id.menu_github :
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_github))));
                break;
            case R.id.menu_photoAlbum :
                startActivity(new Intent(getBaseContext(), PhotoAlbumActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class TitleTabFactory implements PagerSlidingTabStrip.TabViewFactory{
        private String[] titles;
        private Context context;

        private TitleTabFactory(String[] titles, Context context) {
            this.titles = titles;
            this.context = context;
        }

        @Override
        public void addTabs(ViewGroup viewGroup, int i) {
            for(String title : titles){
                TextView textView = new TextView(context);
                textView.setText(title);
                textView.setPadding(16, 16, 16, 16);
                textView.setGravity(Gravity.CENTER);
                viewGroup.addView(textView);
            }
        }
    }

    private static class ContentAdapter extends FragmentPagerAdapter{
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
}
