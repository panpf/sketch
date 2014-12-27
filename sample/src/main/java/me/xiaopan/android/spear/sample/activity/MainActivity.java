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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectParentMember;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.spear.sample.MyActionBarActivity;
import me.xiaopan.android.spear.sample.fragment.IndexFragment;
import me.xiaopan.android.spear.sample.fragment.PhotoAlbumFragment;
import me.xiaopan.android.spear.sample.fragment.StartFragment;
import me.xiaopan.android.spear.sample.util.AnimationUtils;
import me.xiaopan.android.spear.sample.util.DimenUtils;
import me.xiaopan.android.widget.PagerSlidingTabStrip;

/**
 * 首页
 */
@InjectParentMember
@InjectContentView(R.layout.activity_main)
public class MainActivity extends MyActionBarActivity implements StartFragment.GetPagerSlidingTagStripListener{
    @InjectView(R.id.tabStrip_main) private PagerSlidingTabStrip titleTabStrip;
    @InjectView(R.id.drawer_main_content) private DrawerLayout drawerLayout;
    @InjectView(R.id.layout_main_leftMenu) private View leftMenuView;
    @InjectView(R.id.button_main_index) private View indexButton;
    @InjectView(R.id.button_main_star) private View starButton;
    @InjectView(R.id.button_main_photoAlbum) private View photoAlbumButton;
    @InjectView(R.id.button_main_about) private View aboutButton;

    private Fragment indexFragment;
    private Fragment starFragment;
    private Fragment photoAlbumFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);

        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shadow_down_left, Gravity.START);

        // 设置左侧菜单的宽度为屏幕的一半
        ViewGroup.LayoutParams params = leftMenuView.getLayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels*0.6);
        leftMenuView.setLayoutParams(params);

        indexButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);

                if (!"首页".equals(getSupportActionBar().getTitle())) {
                    getSupportActionBar().setTitle("首页");
                    AnimationUtils.invisibleViewByAlpha(titleTabStrip);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, indexFragment != null ? indexFragment : (indexFragment = new IndexFragment()))
                            .commit();
                }
            }
        });

        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);

                if(!"明星".equals(getSupportActionBar().getTitle())){
                    getSupportActionBar().setTitle("明星");
                    AnimationUtils.visibleViewByAlpha(titleTabStrip);
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, starFragment != null ? starFragment : (starFragment = new StartFragment()))
                            .commit();
                }
            }
        });

        photoAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);

                if(!"本地相册".equals(getSupportActionBar().getTitle())){
                    getSupportActionBar().setTitle("本地相册");
                    AnimationUtils.invisibleViewByAlpha(titleTabStrip);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, photoAlbumFragment != null ? photoAlbumFragment : (photoAlbumFragment = new PhotoAlbumFragment()))
                            .commit();
                }
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_github))));
            }
        });

        titleTabStrip.setTabViewFactory(new TitleTabFactory(new String[]{"最热", "名录"}, getBaseContext()));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_main_content, new StartFragment())
                .commit();
        getSupportActionBar().setTitle("明星");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_search :
                startActivity(new Intent(getBaseContext(), SearchActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MENU){
            if(drawerLayout.isDrawerOpen(Gravity.START)){
                drawerLayout.closeDrawer(Gravity.START);
            }else{
                drawerLayout.openDrawer(Gravity.START);
            }
            return true;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public PagerSlidingTabStrip onGetPagerSlidingTabStrip() {
        return titleTabStrip;
    }

    private static class TitleTabFactory implements PagerSlidingTabStrip.TabViewFactory{
        private String[] titles;
        private Context context;
        private int paddingLeft;
        private int paddingTop;

        private TitleTabFactory(String[] titles, Context context) {
            this.titles = titles;
            this.context = context;
            this.paddingLeft = DimenUtils.dp2px(context, 16);
            this.paddingTop = DimenUtils.dp2px(context, 16);
        }

        @Override
        public void addTabs(ViewGroup viewGroup, int i) {
            for(String title : titles){
                TextView textView = new TextView(context);
                textView.setText(title);
                textView.setPadding(paddingLeft, paddingTop, paddingLeft, paddingTop);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(context.getResources().getColorStateList(R.color.tab));
                viewGroup.addView(textView);
            }
        }
    }
}
