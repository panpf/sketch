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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectParentMember;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.spear.DisplayOptions;
import me.xiaopan.android.spear.DownloadOptions;
import me.xiaopan.android.spear.RequestOptions;
import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.sample.MyActionBarActivity;
import me.xiaopan.android.spear.sample.fragment.AboutFragment;
import me.xiaopan.android.spear.sample.fragment.AppListFragment;
import me.xiaopan.android.spear.sample.fragment.PhotoAlbumFragment;
import me.xiaopan.android.spear.sample.fragment.SearchFragment;
import me.xiaopan.android.spear.sample.fragment.StarIndexFragment;
import me.xiaopan.android.spear.sample.util.AnimationUtils;
import me.xiaopan.android.spear.sample.util.DimenUtils;
import me.xiaopan.android.spear.sample.util.Settings;
import me.xiaopan.android.widget.PagerSlidingTabStrip;

/**
 * 首页
 */
@InjectParentMember
@InjectContentView(R.layout.activity_main)
public class MainActivity extends MyActionBarActivity implements StarIndexFragment.GetStarTagStripListener, AppListFragment.GetAppListTagStripListener, View.OnClickListener{
    @InjectView(R.id.tabStrip_main_star) private PagerSlidingTabStrip starTabStrip;
    @InjectView(R.id.tabStrip_main_appList) private PagerSlidingTabStrip appListTabStrip;
    @InjectView(R.id.drawer_main_content) private DrawerLayout drawerLayout;
    @InjectView(R.id.layout_main_leftMenu) private View leftMenuView;
    @InjectView(R.id.button_main_search) private View searchButton;
    @InjectView(R.id.button_main_star) private View starButton;
    @InjectView(R.id.button_main_photoAlbum) private View photoAlbumButton;
    @InjectView(R.id.button_main_appList) private View appListButton;
    @InjectView(R.id.button_main_about) private View aboutButton;
    @InjectView(R.id.item_main_scrollingPauseLoad) private View scrollingPauseLoadItem;
    @InjectView(R.id.checkBox_main_scrollingPauseLoad) private CheckBox scrollingPauseLoadCheckBox;
    @InjectView(R.id.item_main_mobileNetworkPauseDownload) private View mobileNetworkPauseDownloadItem;
    @InjectView(R.id.checkBox_main_mobileNetworkPauseDownload) private CheckBox mobileNetworkPauseDownloadCheckBox;
    @InjectView(R.id.item_main_showImageDownloadProgress) private View showImageDownloadProgressItem;
    @InjectView(R.id.checkBox_main_showImageDownloadProgress) private CheckBox showImageDownloadProgressCheckBox;
    @InjectView(R.id.item_main_showImageFromFlag) private View showImageFromFlagItem;
    @InjectView(R.id.checkBox_main_showImageFromFlag) private CheckBox showImageFromFlagCheckBox;
    @InjectView(R.id.item_main_clickDisplayOnFailed) private View clickDisplayOnFailedItem;
    @InjectView(R.id.checkBox_main_clickDisplayOnFailed) private CheckBox clickDisplayOnFailedCheckBox;
    @InjectView(R.id.item_main_clickDisplayOnPauseDownload) private View clickDisplayOnPauseDownloadItem;
    @InjectView(R.id.checkBox_main_clickDisplayOnPauseDownload) private CheckBox clickDisplayOnPauseDownloadCheckBox;
    @InjectView(R.id.item_main_showClickRipple) private View showClickRippleItem;
    @InjectView(R.id.checkBox_main_showClickRipple) private CheckBox showClickRippleCheckBox;
    @InjectView(R.id.item_main_cleanMemoryCache) private View cleanMemoryCacheItem;
    @InjectView(R.id.text_main_memoryCacheSize) private TextView memoryCacheSizeTextView;
    @InjectView(R.id.item_main_cleanDiskCache) private View cleanDiskCacheItem;
    @InjectView(R.id.text_main_diskCacheSize) private TextView diskCacheSizeTextView;
    @InjectView(R.id.item_main_enableMemoryCache) private View enableMemoryCacheItem;
    @InjectView(R.id.checkBox_main_enableMemoryCache) private CheckBox enableMemoryCacheCheckBox;
    @InjectView(R.id.item_main_enableDiskCache) private View enableDiskCacheItem;
    @InjectView(R.id.checkBox_main_enableDiskCache) private CheckBox enableDiskCacheCheckBox;

    private long lastClickBackTime;
    private Type type;
    private Settings settings;
    private ActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);

        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shadow_down_left, Gravity.START);
        setRefreshCacheSize();

        // 设置左侧菜单的宽度为屏幕的一半
        ViewGroup.LayoutParams params = leftMenuView.getLayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels*0.7);
        leftMenuView.setLayoutParams(params);

        settings = Settings.with(getBaseContext());
        scrollingPauseLoadCheckBox.setChecked(settings.isScrollingPauseLoad());
        showImageDownloadProgressCheckBox.setChecked(settings.isShowImageDownloadProgress());
        mobileNetworkPauseDownloadCheckBox.setChecked(settings.isMobileNetworkPauseDownload());
        showImageFromFlagCheckBox.setChecked(settings.isShowImageFromFlag());
        clickDisplayOnFailedCheckBox.setChecked(settings.isClickDisplayOnFailed());
        clickDisplayOnPauseDownloadCheckBox.setChecked(settings.isClickDisplayOnPauseDownload());
        showClickRippleCheckBox.setChecked(settings.isShowClickRipple());
        enableMemoryCacheCheckBox.setChecked(settings.isEnableMemoryCache());
        enableDiskCacheCheckBox.setChecked(settings.isEnableDiskCache());

        starButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        photoAlbumButton.setOnClickListener(this);
        appListButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
        showImageDownloadProgressItem.setOnClickListener(this);
        scrollingPauseLoadItem.setOnClickListener(this);
        mobileNetworkPauseDownloadItem.setOnClickListener(this);
        showImageFromFlagItem.setOnClickListener(this);
        clickDisplayOnFailedItem.setOnClickListener(this);
        clickDisplayOnPauseDownloadItem.setOnClickListener(this);
        cleanDiskCacheItem.setOnClickListener(this);
        cleanMemoryCacheItem.setOnClickListener(this);
        showClickRippleItem.setOnClickListener(this);
        enableDiskCacheItem.setOnClickListener(this);
        enableMemoryCacheItem.setOnClickListener(this);

        starTabStrip.setTabViewFactory(new TitleTabFactory(new String[]{"最热", "名录"}, getBaseContext()));
        appListTabStrip.setTabViewFactory(new TitleTabFactory(new String[]{"已安装", "安装包"}, getBaseContext()));

        starButton.performClick();
    }

    private void setRefreshCacheSize(){
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                drawerToggle.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerToggle.onDrawerOpened(drawerView);
                if(leftMenuView == drawerView){
                    refreshCacheSizeInfo(false, Spear.with(getBaseContext()).getConfiguration().getMemoryCache().getSize(), Spear.with(getBaseContext()).getConfiguration().getMemoryCache().getMaxSize());
                    new AsyncTask<Integer, Integer, Long>() {
                        @Override
                        protected Long doInBackground(Integer... params) {
                            return Spear.with(getBaseContext()).getConfiguration().getDiskCache().getSize();
                        }

                        @Override
                        protected void onPostExecute(Long diskUsedSize) {
                            refreshCacheSizeInfo(true, diskUsedSize, Spear.with(getBaseContext()).getConfiguration().getDiskCache().getMaxSize());
                        }
                    }.execute(0);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerToggle.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                drawerToggle.onDrawerStateChanged(newState);
            }
        });
    }

    private void refreshCacheSizeInfo(boolean disk, long useSize, long maxSize){
        String usedSizeFormat = Formatter.formatFileSize(getBaseContext(), useSize);
        String maxSizeFormat = Formatter.formatFileSize(getBaseContext(), maxSize);
        String cacheInfo = usedSizeFormat+"/"+maxSizeFormat;
        if(disk){
            diskCacheSizeTextView.setText(cacheInfo);
        }else{
            memoryCacheSizeTextView.setText(cacheInfo);
        }
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_main_about :
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
                break;
            case R.id.button_main_appList :
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
                break;
            case R.id.button_main_photoAlbum :
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
                break;
            case R.id.button_main_search :
                drawerLayout.closeDrawer(Gravity.START);
                if (type != Type.SEARCH) {
                    AnimationUtils.invisibleViewByAlpha(starTabStrip);
                    AnimationUtils.invisibleViewByAlpha(appListTabStrip);
                    type = Type.SEARCH;
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new SearchFragment())
                            .commit();
                }
                break;
            case R.id.button_main_star :
                drawerLayout.closeDrawer(Gravity.START);
                if (type != Type.STAR) {
                    getSupportActionBar().setTitle("明星图片");
                    AnimationUtils.visibleViewByAlpha(starTabStrip);
                    AnimationUtils.invisibleViewByAlpha(appListTabStrip);
                    type = Type.STAR;
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new StarIndexFragment())
                            .commit();
                }
                break;
            case R.id.item_main_mobileNetworkPauseDownload :
                boolean newMobileNetStopDownloadValue = !settings.isMobileNetworkPauseDownload();
                settings.setMobileNetworkPauseDownload(newMobileNetStopDownloadValue);
                mobileNetworkPauseDownloadCheckBox.setChecked(newMobileNetStopDownloadValue);
                Spear.with(getBaseContext()).getConfiguration().setMobileNetworkPauseDownload(newMobileNetStopDownloadValue);
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.item_main_scrollingPauseLoad :
                boolean newPauseLoadValue = !settings.isScrollingPauseLoad();
                settings.setScrollingPauseLoad(newPauseLoadValue);
                scrollingPauseLoadCheckBox.setChecked(newPauseLoadValue);
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.item_main_showImageDownloadProgress :
                boolean newShowProgressValue = !settings.isShowImageDownloadProgress();
                settings.setShowImageDownloadProgress(newShowProgressValue);
                showImageDownloadProgressCheckBox.setChecked(newShowProgressValue);
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.item_main_showImageFromFlag :
                boolean newShowImageFromFlag = !settings.isShowImageFromFlag();
                settings.setShowImageFromFlag(newShowImageFromFlag);
                showImageFromFlagCheckBox.setChecked(newShowImageFromFlag);
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.item_main_clickDisplayOnFailed :
                boolean newClickDisplayOnFailed = !settings.isClickDisplayOnFailed();
                settings.setClickDisplayOnFailed(newClickDisplayOnFailed);
                clickDisplayOnFailedCheckBox.setChecked(newClickDisplayOnFailed);
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.item_main_clickDisplayOnPauseDownload :
                boolean newClickDisplayOnPauseDownload = !settings.isClickDisplayOnPauseDownload();
                settings.setClickDisplayOnPauseDownload(newClickDisplayOnPauseDownload);
                clickDisplayOnPauseDownloadCheckBox.setChecked(newClickDisplayOnPauseDownload);
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.item_main_cleanMemoryCache :
                Spear.with(getBaseContext()).getConfiguration().getMemoryCache().clear();
                refreshCacheSizeInfo(false, 0, Spear.with(getBaseContext()).getConfiguration().getMemoryCache().getMaxSize());
                break;
            case R.id.item_main_cleanDiskCache :
                refreshCacheSizeInfo(true, 0, Spear.with(getBaseContext()).getConfiguration().getMemoryCache().getMaxSize());
                new AsyncTask<Integer, Integer, Integer>(){
                    @Override
                    protected Integer doInBackground(Integer... params) {
                        Spear.with(getBaseContext()).getConfiguration().getDiskCache().clear();
                        return null;
                    }
                }.execute(0);
                break;
            case R.id.item_main_showClickRipple :
                boolean newShowClickRippleValue = !settings.isShowClickRipple();
                settings.setShowClickRipple(newShowClickRippleValue);
                showClickRippleCheckBox.setChecked(newShowClickRippleValue);
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.item_main_enableDiskCache :
                boolean newEnableDiskCacheValue = !settings.isEnableDiskCache();
                settings.setEnableDiskCache(newEnableDiskCacheValue);
                setCacheStatus(false, newEnableDiskCacheValue);
                enableDiskCacheCheckBox.setChecked(newEnableDiskCacheValue);
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.item_main_enableMemoryCache :
                boolean newEnableMemoryCacheValue = !settings.isEnableMemoryCache();
                settings.setEnableMemoryCache(newEnableMemoryCacheValue);
                setCacheStatus(true, newEnableMemoryCacheValue);
                enableMemoryCacheCheckBox.setChecked(newEnableMemoryCacheValue);
                drawerLayout.closeDrawer(Gravity.START);
                break;
        }
    }

    private void setCacheStatus(boolean memory, boolean newValue){
        Map<Enum<?>, RequestOptions> optionsMap = Spear.getOptionsMap();
        if(optionsMap != null && optionsMap.size() > 0){
            for(RequestOptions options : optionsMap.values()){
                if(options instanceof DisplayOptions){
                    DisplayOptions displayOptions = (DisplayOptions) options;
                    if(memory){
                        if(newValue){
                            displayOptions.enableMemoryCache();
                        }else{
                            displayOptions.disableMemoryCache();
                        }
                    }else{
                        if(newValue){
                            displayOptions.enableDiskCache();
                        }else{
                            displayOptions.disableDiskCache();
                        }
                    }
                }else if(options instanceof DownloadOptions){
                    DownloadOptions displayOptions = (DownloadOptions) options;
                    if(newValue){
                        displayOptions.enableDiskCache();
                    }else{
                        displayOptions.disableDiskCache();
                    }
                }
            }
        }
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
