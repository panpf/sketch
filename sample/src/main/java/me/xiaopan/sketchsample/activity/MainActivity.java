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
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectParentMember;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.psts.PagerSlidingTabStrip;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.BuildConfig;
import me.xiaopan.sketchsample.MyBaseActivity;
import me.xiaopan.sketchsample.NotificationService;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.fragment.AboutFragment;
import me.xiaopan.sketchsample.fragment.AppListFragment;
import me.xiaopan.sketchsample.fragment.LargesFragment;
import me.xiaopan.sketchsample.fragment.PhotoAlbumFragment;
import me.xiaopan.sketchsample.fragment.SearchFragment;
import me.xiaopan.sketchsample.fragment.StarIndexFragment;
import me.xiaopan.sketchsample.fragment.TestFragment;
import me.xiaopan.sketchsample.util.AnimationUtils;
import me.xiaopan.sketchsample.util.DeviceUtils;
import me.xiaopan.sketchsample.util.Settings;

/**
 * 首页
 */
@InjectParentMember
@InjectContentView(R.layout.activity_main)
public class MainActivity extends MyBaseActivity implements StarIndexFragment.GetStarTagStripListener, AppListFragment.GetAppListTagStripListener, LargesFragment.GetLargeTagStripListener, View.OnClickListener, WindowBackgroundManager.OnSetListener, AboutFragment.TogglePageListener {
    @InjectView(R.id.layout_main_content)
    private View contentView;
    @InjectView(R.id.tabStrip_main_star)
    private PagerSlidingTabStrip starTabStrip;
    @InjectView(R.id.tabStrip_main_appList)
    private PagerSlidingTabStrip appListTabStrip;
    @InjectView(R.id.tabStrip_main_large)
    private PagerSlidingTabStrip largeTabStrip;
    @InjectView(R.id.drawer_main_content)
    private DrawerLayout drawerLayout;
    @InjectView(R.id.layout_main_leftMenu)
    private View leftMenuView;
    @InjectView(R.id.layout_main_leftMenuContent)
    private View leftMenuContentView;
    @InjectView(R.id.button_main_search)
    private View searchButton;
    @InjectView(R.id.button_main_star)
    private View starButton;
    @InjectView(R.id.button_main_photoAlbum)
    private View photoAlbumButton;
    @InjectView(R.id.button_main_appList)
    private View appListButton;
    @InjectView(R.id.button_main_largeImage)
    private View largeImageButton;
    @InjectView(R.id.button_main_test)
    private View testButton;
    @InjectView(R.id.button_main_about)
    private View aboutButton;
    @InjectView(R.id.item_main_scrollingPauseLoad)
    private View scrollingPauseLoadItem;
    @InjectView(R.id.checkBox_main_scrollingPauseLoad)
    private CheckBox scrollingPauseLoadCheckBox;
    @InjectView(R.id.item_main_mobileNetworkPauseDownload)
    private View mobileNetworkPauseDownloadItem;
    @InjectView(R.id.checkBox_main_mobileNetworkPauseDownload)
    private CheckBox mobileNetworkPauseDownloadCheckBox;
    @InjectView(R.id.item_main_showImageDownloadProgress)
    private View showImageDownloadProgressItem;
    @InjectView(R.id.checkBox_main_showImageDownloadProgress)
    private CheckBox showImageDownloadProgressCheckBox;
    @InjectView(R.id.item_main_showImageFromFlag)
    private View showImageFromFlagItem;
    @InjectView(R.id.checkBox_main_showImageFromFlag)
    private CheckBox showImageFromFlagCheckBox;
    @InjectView(R.id.item_main_clickDisplayOnFailed)
    private View clickDisplayOnFailedItem;
    @InjectView(R.id.checkBox_main_clickDisplayOnFailed)
    private CheckBox clickDisplayOnFailedCheckBox;
    @InjectView(R.id.item_main_clickDisplayOnPauseDownload)
    private View clickDisplayOnPauseDownloadItem;
    @InjectView(R.id.checkBox_main_clickDisplayOnPauseDownload)
    private CheckBox clickDisplayOnPauseDownloadCheckBox;
    @InjectView(R.id.item_main_showPressedStatus)
    private View showPressedStatusItem;
    @InjectView(R.id.checkBox_main_showPressedStatus)
    private CheckBox showPressedStatusCheckBox;
    @InjectView(R.id.item_main_cleanMemoryCache)
    private View cleanMemoryCacheItem;
    @InjectView(R.id.text_main_memoryCacheSize)
    private TextView memoryCacheSizeTextView;
    @InjectView(R.id.item_main_cleanPlaceholderMemoryCache)
    private View cleanPlaceholderMemoryCacheItem;
    @InjectView(R.id.text_main_placeholderMemoryCacheSize)
    private TextView placeholderMemoryCacheSizeTextView;
    @InjectView(R.id.item_main_cleanDiskCache)
    private View cleanDiskCacheItem;
    @InjectView(R.id.text_main_diskCacheSize)
    private TextView diskCacheSizeTextView;
    @InjectView(R.id.item_main_globalDisableCacheInMemory)
    private View cacheMemoryItem;
    @InjectView(R.id.checkBox_main_globalDisableCacheInMemory)
    private CheckBox globalDisableCacheInMemoryCheckBox;
    @InjectView(R.id.item_main_globalDisableCacheInDisk)
    private View cacheInDiskItem;
    @InjectView(R.id.checkBox_main_globalDisableCacheInDisk)
    private CheckBox globalDisableCacheInDiskCheckBox;
    @InjectView(R.id.item_main_globalLowQualityImage)
    private View lowQualityImageItem;
    @InjectView(R.id.checkBox_main_globalLowQualityImage)
    private CheckBox globalLowQualityImageCheckBox;
    @InjectView(R.id.item_main_globalInPreferQualityOverSpeed)
    private View inPreferQualityOverSpeedItem;
    @InjectView(R.id.checkBox_main_globalInPreferQualityOverSpeed)
    private CheckBox globalInPreferQualityOverSpeedCheckBox;

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

        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shadow_down_left, Gravity.LEFT);
//        drawerLayout.setSliderFadeColor(Color.parseColor("#00ffffff"));
        initSlidingPanLayoutSlideListener();

        // 设置左侧菜单的宽度为屏幕的一半
        ViewGroup.LayoutParams params = leftMenuView.getLayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
        leftMenuView.setLayoutParams(params);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
//        toggleDrawable = new ActionBarDrawerToggle(this, new SlidingPaneLayoutCompatDrawerLayout(getBaseContext(), drawerLayout), toolbar, R.string.drawer_open, R.string.drawer_close);
        toggleDrawable = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        settings = Settings.with(getBaseContext());
        scrollingPauseLoadCheckBox.setChecked(settings.isScrollingPauseLoad());
        showImageDownloadProgressCheckBox.setChecked(settings.isShowImageDownloadProgress());
        mobileNetworkPauseDownloadCheckBox.setChecked(settings.isMobileNetworkPauseDownload());
        showImageFromFlagCheckBox.setChecked(settings.isShowImageFromFlag());
        clickDisplayOnFailedCheckBox.setChecked(settings.isClickDisplayOnFailed());
        clickDisplayOnPauseDownloadCheckBox.setChecked(settings.isClickDisplayOnPauseDownload());
        showPressedStatusCheckBox.setChecked(settings.isShowPressedStatus());
        globalDisableCacheInMemoryCheckBox.setChecked(settings.isGlobalDisableCacheInMemory());
        globalDisableCacheInDiskCheckBox.setChecked(settings.isGlobalDisableCacheInDisk());
        globalLowQualityImageCheckBox.setChecked(settings.isGlobalLowQualityImage());
        globalInPreferQualityOverSpeedCheckBox.setChecked(settings.isGlobalInPreferQualityOverSpeed());

        starButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        photoAlbumButton.setOnClickListener(this);
        appListButton.setOnClickListener(this);
        largeImageButton.setOnClickListener(this);
        testButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
        showImageDownloadProgressItem.setOnClickListener(this);
        scrollingPauseLoadItem.setOnClickListener(this);
        mobileNetworkPauseDownloadItem.setOnClickListener(this);
        showImageFromFlagItem.setOnClickListener(this);
        clickDisplayOnFailedItem.setOnClickListener(this);
        clickDisplayOnPauseDownloadItem.setOnClickListener(this);
        cleanDiskCacheItem.setOnClickListener(this);
        cleanMemoryCacheItem.setOnClickListener(this);
        cleanPlaceholderMemoryCacheItem.setOnClickListener(this);
        showPressedStatusItem.setOnClickListener(this);
        cacheInDiskItem.setOnClickListener(this);
        cacheMemoryItem.setOnClickListener(this);
        lowQualityImageItem.setOnClickListener(this);
        inPreferQualityOverSpeedItem.setOnClickListener(this);

        starTabStrip.setTabViewFactory(new TitleTabFactory(new String[]{"最热", "名录"}, getBaseContext()));
        appListTabStrip.setTabViewFactory(new TitleTabFactory(new String[]{"已安装", "安装包"}, getBaseContext()));
        largeTabStrip.setTabViewFactory(new TitleTabFactory(new String[]{"WORLD", "QMSHT", "CWB", "CARD"}, getBaseContext()));

        if (!BuildConfig.DEBUG) {
            testButton.setVisibility(View.GONE);
        }

        largeImageButton.performClick();

        startService(new Intent(getBaseContext(), NotificationService.class));
    }

    @Override
    protected boolean isDisableSetFitsSystemWindows() {
        return true;
    }

    private void onInitLayoutTopPadding() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = DeviceUtils.getStatusBarHeight(getResources());
            if (statusBarHeight > 0) {
                contentView.setPadding(contentView.getPaddingLeft(), statusBarHeight, contentView.getPaddingRight(), contentView.getPaddingBottom());
                leftMenuContentView.setPadding(contentView.getPaddingLeft(), statusBarHeight, contentView.getPaddingRight(), contentView.getPaddingBottom());
            } else {
                drawerLayout.setFitsSystemWindows(true);
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

    private void initSlidingPanLayoutSlideListener() {
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                toggleDrawable.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                toggleDrawable.onDrawerOpened(drawerView);
                refreshMemoryCacheSizeInfo();
                refreshPlaceholderMemoryCacheSizeInfo();
                refreshDiskCacheSizeInfo();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                toggleDrawable.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void refreshMemoryCacheSizeInfo() {
        String usedSizeFormat = Formatter.formatFileSize(getBaseContext(), Sketch.with(getBaseContext()).getConfiguration().getMemoryCache().getSize());
        String maxSizeFormat = Formatter.formatFileSize(getBaseContext(), Sketch.with(getBaseContext()).getConfiguration().getMemoryCache().getMaxSize());
        String cacheInfo = usedSizeFormat + "/" + maxSizeFormat;
        memoryCacheSizeTextView.setText(cacheInfo);
    }

    private void refreshPlaceholderMemoryCacheSizeInfo() {
        String usedSizeFormat = Formatter.formatFileSize(getBaseContext(), Sketch.with(getBaseContext()).getConfiguration().getPlaceholderImageMemoryCache().getSize());
        String maxSizeFormat = Formatter.formatFileSize(getBaseContext(), Sketch.with(getBaseContext()).getConfiguration().getPlaceholderImageMemoryCache().getMaxSize());
        String cacheInfo = usedSizeFormat + "/" + maxSizeFormat;
        placeholderMemoryCacheSizeTextView.setText(cacheInfo);
    }

    private void refreshDiskCacheSizeInfo() {
        String usedSizeFormat = Formatter.formatFileSize(getBaseContext(), Sketch.with(getBaseContext()).getConfiguration().getDiskCache().getSize());
        String maxSizeFormat = Formatter.formatFileSize(getBaseContext(), Sketch.with(getBaseContext()).getConfiguration().getDiskCache().getMaxSize());
        String cacheInfo = usedSizeFormat + "/" + maxSizeFormat;
        diskCacheSizeTextView.setText(cacheInfo);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
            return true;
        } else {
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
        if ((currentTime - lastClickBackTime) > 2000) {
            lastClickBackTime = currentTime;
            Toast.makeText(getBaseContext(), "再按一下返回键退出" + getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_main_about:
                drawerLayout.closeDrawer(Gravity.LEFT);
                if (type != Type.ABOUT) {
                    AnimationUtils.invisibleViewByAlpha(starTabStrip);
                    AnimationUtils.invisibleViewByAlpha(appListTabStrip);
                    AnimationUtils.invisibleViewByAlpha(largeTabStrip);
                    getSupportActionBar().setTitle("关于Sketch");
                    type = Type.ABOUT;
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new AboutFragment())
                            .commit();
                }
                break;
            case R.id.button_main_appList:
                drawerLayout.closeDrawer(Gravity.LEFT);
                if (type != Type.APP_LIST) {
                    getSupportActionBar().setTitle("本地APP");
                    AnimationUtils.invisibleViewByAlpha(starTabStrip);
                    AnimationUtils.visibleViewByAlpha(appListTabStrip);
                    AnimationUtils.invisibleViewByAlpha(largeTabStrip);
                    type = Type.APP_LIST;
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new AppListFragment())
                            .commit();
                }
                break;
            case R.id.button_main_photoAlbum:
                drawerLayout.closeDrawer(Gravity.LEFT);
                if (type != Type.LOCAL_PHOTO_ALBUM) {
                    AnimationUtils.invisibleViewByAlpha(starTabStrip);
                    AnimationUtils.invisibleViewByAlpha(appListTabStrip);
                    AnimationUtils.invisibleViewByAlpha(largeTabStrip);
                    getSupportActionBar().setTitle("本地相册");
                    type = Type.LOCAL_PHOTO_ALBUM;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new PhotoAlbumFragment())
                            .commit();
                }
                break;
            case R.id.button_main_search:
                drawerLayout.closeDrawer(Gravity.LEFT);
                if (type != Type.SEARCH) {
                    AnimationUtils.invisibleViewByAlpha(starTabStrip);
                    AnimationUtils.invisibleViewByAlpha(appListTabStrip);
                    AnimationUtils.invisibleViewByAlpha(largeTabStrip);
                    getSupportActionBar().setTitle("图片搜索");
                    type = Type.SEARCH;
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new SearchFragment())
                            .commit();
                }
                break;
            case R.id.button_main_star:
                drawerLayout.closeDrawer(Gravity.LEFT);
                if (type != Type.STAR) {
                    getSupportActionBar().setTitle("明星图片");
                    AnimationUtils.visibleViewByAlpha(starTabStrip);
                    AnimationUtils.invisibleViewByAlpha(appListTabStrip);
                    AnimationUtils.invisibleViewByAlpha(largeTabStrip);
                    type = Type.STAR;
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new StarIndexFragment())
                            .commit();
                }
                break;
            case R.id.button_main_largeImage:
                drawerLayout.closeDrawer(Gravity.LEFT);
                if (type != Type.LARGE_IMAGE) {
                    getSupportActionBar().setTitle("超大图片");
                    AnimationUtils.invisibleViewByAlpha(starTabStrip);
                    AnimationUtils.invisibleViewByAlpha(appListTabStrip);
                    AnimationUtils.visibleViewByAlpha(largeTabStrip);
                    type = Type.LARGE_IMAGE;
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new LargesFragment())
                            .commit();
                }
                break;
            case R.id.button_main_test:
                drawerLayout.closeDrawer(Gravity.LEFT);
                if (type != Type.TEST) {
                    getSupportActionBar().setTitle("测试");
                    AnimationUtils.invisibleViewByAlpha(starTabStrip);
                    AnimationUtils.invisibleViewByAlpha(appListTabStrip);
                    AnimationUtils.invisibleViewByAlpha(largeTabStrip);
                    type = Type.TEST;
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                            .replace(R.id.frame_main_content, new TestFragment())
                            .commit();
                }
                break;
            case R.id.item_main_mobileNetworkPauseDownload:
                boolean newMobileNetStopDownloadValue = !settings.isMobileNetworkPauseDownload();
                settings.setMobileNetworkPauseDownload(newMobileNetStopDownloadValue);
                mobileNetworkPauseDownloadCheckBox.setChecked(newMobileNetStopDownloadValue);
                Sketch.with(getBaseContext()).getConfiguration().setMobileNetworkGlobalPauseDownload(newMobileNetStopDownloadValue);
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.item_main_scrollingPauseLoad:
                boolean newPauseLoadValue = !settings.isScrollingPauseLoad();
                settings.setScrollingPauseLoad(newPauseLoadValue);
                scrollingPauseLoadCheckBox.setChecked(newPauseLoadValue);
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.item_main_showImageDownloadProgress:
                boolean newShowProgressValue = !settings.isShowImageDownloadProgress();
                settings.setShowImageDownloadProgress(newShowProgressValue);
                showImageDownloadProgressCheckBox.setChecked(newShowProgressValue);
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.item_main_showImageFromFlag:
                boolean newShowImageFromFlag = !settings.isShowImageFromFlag();
                settings.setShowImageFromFlag(newShowImageFromFlag);
                showImageFromFlagCheckBox.setChecked(newShowImageFromFlag);
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.item_main_clickDisplayOnFailed:
                boolean newClickDisplayOnFailed = !settings.isClickDisplayOnFailed();
                settings.setClickDisplayOnFailed(newClickDisplayOnFailed);
                clickDisplayOnFailedCheckBox.setChecked(newClickDisplayOnFailed);
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.item_main_clickDisplayOnPauseDownload:
                boolean newClickDisplayOnPauseDownload = !settings.isClickDisplayOnPauseDownload();
                settings.setClickDisplayOnPauseDownload(newClickDisplayOnPauseDownload);
                clickDisplayOnPauseDownloadCheckBox.setChecked(newClickDisplayOnPauseDownload);
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.item_main_cleanMemoryCache:
                Sketch.with(getBaseContext()).getConfiguration().getMemoryCache().clear();
                refreshMemoryCacheSizeInfo();
                break;
            case R.id.item_main_cleanPlaceholderMemoryCache:
                Sketch.with(getBaseContext()).getConfiguration().getPlaceholderImageMemoryCache().clear();
                refreshPlaceholderMemoryCacheSizeInfo();
                break;
            case R.id.item_main_cleanDiskCache:
                new AsyncTask<Integer, Integer, Integer>() {
                    @Override
                    protected Integer doInBackground(Integer... params) {
                        Sketch.with(getBaseContext()).getConfiguration().getDiskCache().clear();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        refreshDiskCacheSizeInfo();
                    }
                }.execute(0);
                break;
            case R.id.item_main_showPressedStatus:
                boolean newShowPressedStatusValue = !settings.isShowPressedStatus();
                showPressedStatusCheckBox.setChecked(newShowPressedStatusValue);
                settings.setShowPressedStatus(newShowPressedStatusValue);
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.item_main_globalDisableCacheInDisk:
                boolean newGlobalDisableCacheInDiskValue = !settings.isGlobalDisableCacheInDisk();
                settings.setGlobalDisableCacheInDisk(newGlobalDisableCacheInDiskValue);
                globalDisableCacheInDiskCheckBox.setChecked(newGlobalDisableCacheInDiskValue);
                Sketch.with(getBaseContext()).getConfiguration().setGlobalDisableCacheInDisk(newGlobalDisableCacheInDiskValue);
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.item_main_globalDisableCacheInMemory:
                boolean newGlobalDisableCacheInMemoryValue = !settings.isGlobalDisableCacheInMemory();
                globalDisableCacheInMemoryCheckBox.setChecked(newGlobalDisableCacheInMemoryValue);
                settings.setGlobalDisableCacheInMemory(newGlobalDisableCacheInMemoryValue);
                Sketch.with(getBaseContext()).getConfiguration().setGlobalDisableCacheInMemory(newGlobalDisableCacheInMemoryValue);
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.item_main_globalLowQualityImage:
                boolean newGlobalLowQualityImageValue = !settings.isGlobalLowQualityImage();
                globalLowQualityImageCheckBox.setChecked(newGlobalLowQualityImageValue);
                settings.setGlobalLowQualityImage(newGlobalLowQualityImageValue);
                Sketch.with(getBaseContext()).getConfiguration().setGlobalLowQualityImage(newGlobalLowQualityImageValue);
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.item_main_globalInPreferQualityOverSpeed:
                boolean globalInPreferQualityOverSpeed = !settings.isGlobalInPreferQualityOverSpeed();
                globalInPreferQualityOverSpeedCheckBox.setChecked(globalInPreferQualityOverSpeed);
                settings.setGlobalInPreferQualityOverSpeed(globalInPreferQualityOverSpeed);
                Sketch.with(getBaseContext()).getConfiguration().setGlobalInPreferQualityOverSpeed(globalInPreferQualityOverSpeed);
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
        }
    }

    @Override
    public void onSetWindowBackground(String currentBackgroundUri, Bitmap bitmap) {
        windowBackgroundManager.setBackground(currentBackgroundUri, bitmap);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = true;
        try {
            result = super.dispatchTouchEvent(ev);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public PagerSlidingTabStrip onGetLargeTabStrip() {
        return largeTabStrip;
    }

    private enum Type {
        STAR,
        SEARCH,
        LOCAL_PHOTO_ALBUM,
        ABOUT,
        APP_LIST,
        LARGE_IMAGE,
        TEST,
    }

    private static class TitleTabFactory implements PagerSlidingTabStrip.TabViewFactory {
        private String[] titles;
        private Context context;

        private TitleTabFactory(String[] titles, Context context) {
            this.titles = titles;
            this.context = context;
        }

        @Override
        public void addTabs(ViewGroup viewGroup, int i) {
            int number = 0;
            for (String title : titles) {
                TextView textView = new TextView(context);
                textView.setText(title);
                int padding = SketchUtils.dp2px(context, 12);
                if (number == 0) {
                    textView.setPadding(
                            padding,
                            padding,
                            padding / 2,
                            padding);
                } else if (number == titles.length - 1) {
                    textView.setPadding(
                            padding / 2,
                            padding,
                            padding,
                            padding);
                } else {
                    textView.setPadding(
                            padding / 2,
                            padding,
                            padding / 2,
                            padding);
                }
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(context.getResources().getColorStateList(R.color.tab));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                viewGroup.addView(textView);
                number++;
            }
        }
    }
}
