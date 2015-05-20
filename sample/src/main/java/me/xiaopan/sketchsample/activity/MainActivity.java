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

package me.xiaopan.sketchsample.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectParentMember;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.widget.PagerSlidingTabStrip;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketchsample.MyAppCompatActivity;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.fragment.AboutFragment;
import me.xiaopan.sketchsample.fragment.AppListFragment;
import me.xiaopan.sketchsample.fragment.PhotoAlbumFragment;
import me.xiaopan.sketchsample.fragment.SearchFragment;
import me.xiaopan.sketchsample.fragment.StarIndexFragment;
import me.xiaopan.sketchsample.util.AnimationUtils;
import me.xiaopan.sketchsample.util.DeviceUtils;
import me.xiaopan.sketchsample.util.DimenUtils;
import me.xiaopan.sketchsample.util.Settings;
import me.xiaopan.sketchsample.widget.SlidingPaneLayoutCompatDrawerLayout;

/**
 * 首页
 */
@InjectParentMember
@InjectContentView(R.layout.activity_main)
public class MainActivity extends MyAppCompatActivity implements StarIndexFragment.GetStarTagStripListener, AppListFragment.GetAppListTagStripListener, View.OnClickListener, WindowBackgroundManager.OnSetWindowBackgroundListener, AboutFragment.TogglePageListener {
    @InjectView(R.id.layout_main_conteng) private View contentView;
    @InjectView(R.id.tabStrip_main_star) private PagerSlidingTabStrip starTabStrip;
    @InjectView(R.id.tabStrip_main_appList) private PagerSlidingTabStrip appListTabStrip;
    @InjectView(R.id.drawer_main_content) private SlidingPaneLayout slidingPaneLayout;
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
    @InjectView(R.id.item_main_imagesOfLowQuality) private View imagesOfLowQualityItem;
    @InjectView(R.id.checkBox_main_imagesOfLowQuality) private CheckBox imagesOfLowQualityCheckBox;

    private long lastClickBackTime;
    private Type type;
    private Settings settings;

    private WindowBackgroundManager windowBackgroundManager;
    private ActionBarDrawerToggle toggleDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        onInitLayoutTopPadding();

        windowBackgroundManager = new WindowBackgroundManager(this);

        slidingPaneLayout.setShadowResourceLeft(R.drawable.shape_drawer_shadow_down_left);
        slidingPaneLayout.setSliderFadeColor(Color.parseColor("#00ffffff"));
        initSlidingPanLayoutSlideListener();

        // 设置左侧菜单的宽度为屏幕的一半
        ViewGroup.LayoutParams params = leftMenuView.getLayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels*0.7);
        leftMenuView.setLayoutParams(params);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toggleDrawable = new ActionBarDrawerToggle(this, new SlidingPaneLayoutCompatDrawerLayout(getBaseContext(), slidingPaneLayout), toolbar, R.string.drawer_open, R.string.drawer_close);

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
        imagesOfLowQualityCheckBox.setChecked(settings.isImagesOfLowQuality());

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
        imagesOfLowQualityItem.setOnClickListener(this);

        starTabStrip.setTabViewFactory(new TitleTabFactory(new String[]{"最热", "名录"}, getBaseContext()));
        appListTabStrip.setTabViewFactory(new TitleTabFactory(new String[]{"已安装", "安装包"}, getBaseContext()));

        searchButton.performClick();
    }

    private void onInitLayoutTopPadding(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            int statusBarHeight = DeviceUtils.getStatusBarHeight(getResources());
            if(statusBarHeight > 0){
                contentView.setPadding(contentView.getPaddingLeft(), statusBarHeight, contentView.getPaddingRight(), contentView.getPaddingBottom());
                leftMenuView.setPadding(contentView.getPaddingLeft(), statusBarHeight, contentView.getPaddingRight(), contentView.getPaddingBottom());
            }else{
                slidingPaneLayout.setFitsSystemWindows(true);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // 同步状态，这一步很重要，要不然初始
        toggleDrawable.syncState();
        toggleDrawable.onDrawerSlide(null, 1.0f);
        toggleDrawable.onDrawerSlide(null, 0.5f);
        toggleDrawable.onDrawerSlide(null, 0.0f);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggleDrawable.onConfigurationChanged(newConfig);
    }

    private void initSlidingPanLayoutSlideListener(){
        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                toggleDrawable.onDrawerSlide(panel, slideOffset);
            }

            @Override
            public void onPanelOpened(View panel) {
                toggleDrawable.onDrawerOpened(panel);
                refreshCacheSizeInfo(false, Sketch.with(getBaseContext()).getConfiguration().getMemoryCache().getSize(), Sketch.with(getBaseContext()).getConfiguration().getMemoryCache().getMaxSize());
                new AsyncTask<Integer, Integer, Long>() {
                    @Override
                    protected Long doInBackground(Integer... params) {
                        return Sketch.with(getBaseContext()).getConfiguration().getDiskCache().getSize();
                    }

                    @Override
                    protected void onPostExecute(Long diskUsedSize) {
                        refreshCacheSizeInfo(true, diskUsedSize, Sketch.with(getBaseContext()).getConfiguration().getDiskCache().getMaxSize());
                    }
                }.execute(0);
            }

            @Override
            public void onPanelClosed(View panel) {
                toggleDrawable.onDrawerClosed(panel);
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
            if(slidingPaneLayout.isOpen()){
                slidingPaneLayout.closePane();
            }else{
                slidingPaneLayout.openPane();
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
                slidingPaneLayout.closePane();
                if (type != Type.ABOUT) {
                    AnimationUtils.invisibleViewByAlpha(starTabStrip);
                    AnimationUtils.invisibleViewByAlpha(appListTabStrip);
                    getSupportActionBar().setTitle("关于Sketch");
                    type = Type.ABOUT;
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new AboutFragment())
                            .commit();
                }
                break;
            case R.id.button_main_appList :
                slidingPaneLayout.closePane();
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
                slidingPaneLayout.closePane();
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
                slidingPaneLayout.closePane();
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
                slidingPaneLayout.closePane();
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
                Sketch.with(getBaseContext()).getConfiguration().setMobileNetworkPauseDownload(newMobileNetStopDownloadValue);
                slidingPaneLayout.closePane();
                break;
            case R.id.item_main_scrollingPauseLoad :
                boolean newPauseLoadValue = !settings.isScrollingPauseLoad();
                settings.setScrollingPauseLoad(newPauseLoadValue);
                scrollingPauseLoadCheckBox.setChecked(newPauseLoadValue);
                slidingPaneLayout.closePane();
                break;
            case R.id.item_main_showImageDownloadProgress :
                boolean newShowProgressValue = !settings.isShowImageDownloadProgress();
                settings.setShowImageDownloadProgress(newShowProgressValue);
                showImageDownloadProgressCheckBox.setChecked(newShowProgressValue);
                slidingPaneLayout.closePane();
                break;
            case R.id.item_main_showImageFromFlag :
                boolean newShowImageFromFlag = !settings.isShowImageFromFlag();
                settings.setShowImageFromFlag(newShowImageFromFlag);
                showImageFromFlagCheckBox.setChecked(newShowImageFromFlag);
                slidingPaneLayout.closePane();
                break;
            case R.id.item_main_clickDisplayOnFailed :
                boolean newClickDisplayOnFailed = !settings.isClickDisplayOnFailed();
                settings.setClickDisplayOnFailed(newClickDisplayOnFailed);
                clickDisplayOnFailedCheckBox.setChecked(newClickDisplayOnFailed);
                slidingPaneLayout.closePane();
                break;
            case R.id.item_main_clickDisplayOnPauseDownload :
                boolean newClickDisplayOnPauseDownload = !settings.isClickDisplayOnPauseDownload();
                settings.setClickDisplayOnPauseDownload(newClickDisplayOnPauseDownload);
                clickDisplayOnPauseDownloadCheckBox.setChecked(newClickDisplayOnPauseDownload);
                slidingPaneLayout.closePane();
                break;
            case R.id.item_main_cleanMemoryCache :
                Sketch.with(getBaseContext()).getConfiguration().getMemoryCache().clear();
                refreshCacheSizeInfo(false, 0, Sketch.with(getBaseContext()).getConfiguration().getMemoryCache().getMaxSize());
                break;
            case R.id.item_main_cleanDiskCache :
                refreshCacheSizeInfo(true, 0, Sketch.with(getBaseContext()).getConfiguration().getMemoryCache().getMaxSize());
                new AsyncTask<Integer, Integer, Integer>(){
                    @Override
                    protected Integer doInBackground(Integer... params) {
                        Sketch.with(getBaseContext()).getConfiguration().getDiskCache().clear();
                        return null;
                    }
                }.execute(0);
                break;
            case R.id.item_main_showClickRipple :
                boolean newShowClickRippleValue = !settings.isShowClickRipple();
                showClickRippleCheckBox.setChecked(newShowClickRippleValue);
                settings.setShowClickRipple(newShowClickRippleValue);
                slidingPaneLayout.closePane();
                break;
            case R.id.item_main_enableDiskCache :
                boolean newEnableDiskCacheValue = !settings.isEnableDiskCache();
                settings.setEnableDiskCache(newEnableDiskCacheValue);
                enableDiskCacheCheckBox.setChecked(newEnableDiskCacheValue);
                Sketch.with(getBaseContext()).getConfiguration().setEnableDiskCache(newEnableDiskCacheValue);
                slidingPaneLayout.closePane();
                break;
            case R.id.item_main_enableMemoryCache :
                boolean newEnableMemoryCacheValue = !settings.isEnableMemoryCache();
                enableMemoryCacheCheckBox.setChecked(newEnableMemoryCacheValue);
                settings.setEnableMemoryCache(newEnableMemoryCacheValue);
                Sketch.with(getBaseContext()).getConfiguration().setEnableMemoryCache(newEnableMemoryCacheValue);
                slidingPaneLayout.closePane();
                break;
            case R.id.item_main_imagesOfLowQuality :
                boolean newImagesOfLowQualityValue = !settings.isImagesOfLowQuality();
                imagesOfLowQualityCheckBox.setChecked(newImagesOfLowQualityValue);
                settings.setImagesOfLowQuality(newImagesOfLowQualityValue);
                Sketch.with(getBaseContext()).getConfiguration().setImagesOfLowQuality(newImagesOfLowQualityValue);
                slidingPaneLayout.closePane();
                break;
        }
    }

    @Override
    public void onSetWindowBackground(String currentBackgroundUri, Drawable newDrawable) {
        windowBackgroundManager.setBackground(currentBackgroundUri, newDrawable);
    }

    @Override
    public String getCurrentBackgroundUri() {
        return windowBackgroundManager.getCurrentBackgroundUri();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        windowBackgroundManager.destroy();
    }

    @Override
    public void onToggleToGifSample() {
        searchButton.performClick();
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
