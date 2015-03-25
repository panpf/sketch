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
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.ptr.folding.BaseFoldingLayout;
import com.ptr.folding.FoldingDrawerLayout;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectParentMember;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.spear.sample.MyActionBarActivity;
import me.xiaopan.android.spear.sample.fragment.AboutFragment;
import me.xiaopan.android.spear.sample.fragment.AppListFragment;
import me.xiaopan.android.spear.sample.fragment.PhotoAlbumFragment;
import me.xiaopan.android.spear.sample.fragment.SearchFragment;
import me.xiaopan.android.spear.sample.fragment.StarFragment;
import me.xiaopan.android.spear.sample.util.AnimationUtils;
import me.xiaopan.android.spear.sample.util.DimenUtils;
import me.xiaopan.android.spear.sample.util.MobileNetworkPauseDownloadNewImageManager;
import me.xiaopan.android.spear.sample.util.Settings;
import me.xiaopan.android.widget.PagerSlidingTabStrip;

/**
 * 首页
 */
@InjectParentMember
@InjectContentView(R.layout.activity_main)
public class MainActivity extends MyActionBarActivity implements StarFragment.GetStarTagStripListener, AppListFragment.GetAppListTagStripListener {
    @InjectView(R.id.tabStrip_main_star) private PagerSlidingTabStrip starTabStrip;
    @InjectView(R.id.tabStrip_main_appList) private PagerSlidingTabStrip appListTabStrip;
    @InjectView(R.id.drawer_main_content) private DrawerLayout drawerLayout;
    @InjectView(R.id.layout_main_leftMenu) private View leftMenuView;
    @InjectView(R.id.button_main_search) private View searchButton;
    @InjectView(R.id.button_main_star) private View starButton;
    @InjectView(R.id.button_main_photoAlbum) private View photoAlbumButton;
    @InjectView(R.id.button_main_appList) private View appListButton;
    @InjectView(R.id.button_main_about) private View aboutButton;
    @InjectView(R.id.item_main_scrollingPauseLoadNewImage) private View scrollingPauseLoadNewImageItem;
    @InjectView(R.id.item_main_mobileNetworkPauseDownloadNewImage) private View mobileNetworkPauseDownloadImageItem;
    @InjectView(R.id.item_main_showImageDownloadProgress) private View showImageDownloadProgressItem;
    @InjectView(R.id.checkBox_main_scrollingPauseLoadNewImage) private CheckBox scrollingPauseLoadNewImageCheckBox;
    @InjectView(R.id.checkBox_main_mobileNetworkPauseDownloadNewImage) private CheckBox mobileNetworkPauseDownloadImageCheckBox;
    @InjectView(R.id.checkBox_main_showImageDownloadProgress) private CheckBox showImageDownloadProgressCheckBox;

    private long lastClickBackTime;
    private Type type;
    private Settings settings;

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
        params.width = (int) (getResources().getDisplayMetrics().widthPixels*0.7);
        leftMenuView.setLayoutParams(params);

        settings = Settings.with(getBaseContext());
        scrollingPauseLoadNewImageCheckBox.setChecked(settings.isScrollingPauseLoadNewImage());
        showImageDownloadProgressCheckBox.setChecked(settings.isShowImageDownloadProgress());
        mobileNetworkPauseDownloadImageCheckBox.setChecked(settings.isMobileNetworkPauseDownloadNewImage());

        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);

                if (type != Type.STAR) {
                    getSupportActionBar().setTitle("明星图片");
                    AnimationUtils.visibleViewByAlpha(starTabStrip);
                    AnimationUtils.invisibleViewByAlpha(appListTabStrip);
                    type = Type.STAR;
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new StarFragment())
                            .commit();
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);

                if(type != Type.SEARCH){
                    AnimationUtils.invisibleViewByAlpha(starTabStrip);
                    AnimationUtils.invisibleViewByAlpha(appListTabStrip);
                    type = Type.SEARCH;
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new SearchFragment())
                            .commit();
                }
            }
        });

        photoAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);

                if(type != Type.LOCAL_PHOTO_ALBUM){
                    AnimationUtils.invisibleViewByAlpha(starTabStrip);
                    AnimationUtils.invisibleViewByAlpha(appListTabStrip);
                    getSupportActionBar().setTitle("本地相册");
                    type = Type.LOCAL_PHOTO_ALBUM;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new PhotoAlbumFragment())
                            .commit();
                }
            }
        });

        appListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);

                if(type != Type.APP_LIST){
                    getSupportActionBar().setTitle("本地APP");
                    AnimationUtils.invisibleViewByAlpha(starTabStrip);
                    AnimationUtils.visibleViewByAlpha(appListTabStrip);
                    type = Type.APP_LIST;
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new AppListFragment())
                            .commit();
                }
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);

                if (type != Type.ABOUT) {
                    AnimationUtils.invisibleViewByAlpha(starTabStrip);
                    AnimationUtils.invisibleViewByAlpha(appListTabStrip);
                    getSupportActionBar().setTitle("关于Spear");
                    type = Type.ABOUT;
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new AboutFragment())
                            .commit();
                }
            }
        });

        showImageDownloadProgressItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newShowProgressValue = !settings.isShowImageDownloadProgress();
                settings.setShowImageDownloadProgress(newShowProgressValue);
                showImageDownloadProgressCheckBox.setChecked(newShowProgressValue);
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        scrollingPauseLoadNewImageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newPauseLoadNewImageValue = !settings.isScrollingPauseLoadNewImage();
                settings.setScrollingPauseLoadNewImage(newPauseLoadNewImageValue);
                scrollingPauseLoadNewImageCheckBox.setChecked(newPauseLoadNewImageValue);
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        mobileNetworkPauseDownloadImageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newMobileNetStopDownloadImageValue = !settings.isMobileNetworkPauseDownloadNewImage();
                settings.setMobileNetworkPauseDownloadNewImage(newMobileNetStopDownloadImageValue);
                mobileNetworkPauseDownloadImageCheckBox.setChecked(newMobileNetStopDownloadImageValue);
                MobileNetworkPauseDownloadNewImageManager.with(getBaseContext()).setPauseDownloadImage(newMobileNetStopDownloadImageValue);
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        starTabStrip.setTabViewFactory(new TitleTabFactory(new String[]{"最热", "名录"}, getBaseContext()));
        appListTabStrip.setTabViewFactory(new TitleTabFactory(new String[]{"已安装", "安装包"}, getBaseContext()));

        starButton.performClick();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BaseFoldingLayout foldingLayout = ((FoldingDrawerLayout) drawerLayout).getFoldingLayout(leftMenuView);
                if (foldingLayout != null) {
                    foldingLayout.setNumberOfFolds(4);
                } else {
                    handler.postDelayed(this, 100);
                }
            }
        }, 100);
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
    public PagerSlidingTabStrip onGetStarTabStrip() {
        return starTabStrip;
    }

    @Override
    public PagerSlidingTabStrip onGetAppListTabStrip() {
        return appListTabStrip;
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if((currentTime - lastClickBackTime) > 2000){
            lastClickBackTime = currentTime;
            Toast.makeText(getBaseContext(), "再按一下返回键退出"+getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();
            return;
        }

        super.onBackPressed();
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
            int number = 0;
            for(String title : titles){
                TextView textView = new TextView(context);
                textView.setText(title);
                if(number == 0){
                    textView.setPadding(
                            DimenUtils.dp2px(context, 16),
                            DimenUtils.dp2px(context, 16),
                            DimenUtils.dp2px(context, 8),
                            DimenUtils.dp2px(context, 16));
                }else if(number == titles.length-1){
                    textView.setPadding(
                            DimenUtils.dp2px(context, 8),
                            DimenUtils.dp2px(context, 16),
                            DimenUtils.dp2px(context, 16),
                            DimenUtils.dp2px(context, 16));
                }else{
                    textView.setPadding(
                            DimenUtils.dp2px(context, 8),
                            DimenUtils.dp2px(context, 16),
                            DimenUtils.dp2px(context, 8),
                            DimenUtils.dp2px(context, 16));
                }
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(context.getResources().getColorStateList(R.color.tab));
                viewGroup.addView(textView);
                number++;
            }
        }
    }

    private enum Type{
        STAR,
        SEARCH,
        LOCAL_PHOTO_ALBUM,
        ABOUT,
        APP_LIST,
    }
}
