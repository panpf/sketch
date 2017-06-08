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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;
import me.xiaopan.psts.PagerSlidingTabStrip;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.cache.MemoryCache;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.BaseActivity;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.BuildConfig;
import me.xiaopan.sketchsample.ImageOptions;
import me.xiaopan.sketchsample.NotificationService;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.itemfactory.CheckMenuItemFactory;
import me.xiaopan.sketchsample.adapter.itemfactory.InfoMenuItemFactory;
import me.xiaopan.sketchsample.adapter.itemfactory.MenuTitleItemFactory;
import me.xiaopan.sketchsample.adapter.itemfactory.PageMenuItemFactory;
import me.xiaopan.sketchsample.bean.CheckMenu;
import me.xiaopan.sketchsample.bean.InfoMenu;
import me.xiaopan.sketchsample.event.CacheCleanEvent;
import me.xiaopan.sketchsample.fragment.AboutFragment;
import me.xiaopan.sketchsample.fragment.AppListFragment;
import me.xiaopan.sketchsample.fragment.Base64ImageTestFragment;
import me.xiaopan.sketchsample.fragment.ImageOrientationTestHomeFragment;
import me.xiaopan.sketchsample.fragment.ImageProcessorTestFragment;
import me.xiaopan.sketchsample.fragment.ImageShaperTestFragment;
import me.xiaopan.sketchsample.fragment.InBitmapTestFragment;
import me.xiaopan.sketchsample.fragment.LargeImageTestFragment;
import me.xiaopan.sketchsample.fragment.MyPhotosFragment;
import me.xiaopan.sketchsample.fragment.MyVideosFragment;
import me.xiaopan.sketchsample.fragment.OtherTestFragment;
import me.xiaopan.sketchsample.fragment.RepeatLoadOrDownloadTestFragment;
import me.xiaopan.sketchsample.fragment.SearchFragment;
import me.xiaopan.sketchsample.fragment.UnsplashPhotosFragment;
import me.xiaopan.sketchsample.util.AnimationUtils;
import me.xiaopan.sketchsample.util.AppConfig;
import me.xiaopan.sketchsample.util.DeviceUtils;
import me.xiaopan.sketchsample.util.ImageOrientationCorrectTestFileGenerator;
import me.xiaopan.sketchsample.widget.SampleImageView;

/**
 * 首页
 */
@BindContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements AppListFragment.GetAppListTagStripListener, ApplyBackgroundCallback {

    @BindView(R.id.layout_main_content)
    View contentView;
    @BindView(R.id.tabStrip_main_appList)
    PagerSlidingTabStrip appListTabStrip;
    @BindView(R.id.drawer_main_content)
    DrawerLayout drawerLayout;
    @BindView(R.id.recycler_main_menu)
    RecyclerView menuRecyclerView;
    @BindView(R.id.layout_main_leftMenu)
    ViewGroup leftMenuView;
    @BindView(R.id.image_main_background)
    SampleImageView backgroundImageView;
    @BindView(R.id.image_main_menuBackground)
    SampleImageView menuBackgroundImageView;

    private long lastClickBackTime;
    private Page page;

    private ActionBarDrawerToggle toggleDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        initData();
        startService(new Intent(getBaseContext(), NotificationService.class));
        switchPage(Page.UNSPLASH);
    }

    private void initViews() {
        ViewGroup.LayoutParams layoutParams = backgroundImageView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getResources().getDisplayMetrics().heightPixels;
        backgroundImageView.setLayoutParams(layoutParams);

        backgroundImageView.setOptions(ImageOptions.WINDOW_BACKGROUND);

        layoutParams = menuBackgroundImageView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getResources().getDisplayMetrics().heightPixels;
        menuBackgroundImageView.setLayoutParams(layoutParams);

        menuBackgroundImageView.setOptions(ImageOptions.WINDOW_BACKGROUND);
        menuBackgroundImageView.getOptions().setImageDisplayer(null);

        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shadow_down_left, Gravity.LEFT);

        // 设置左侧菜单的宽度为屏幕的一半
        ViewGroup.LayoutParams params = leftMenuView.getLayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
        leftMenuView.setLayoutParams(params);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toggleDrawable = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        appListTabStrip.setTabViewFactory(new TitleTabFactory(new String[]{"APP", "PACKAGE"}, getBaseContext()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = DeviceUtils.getStatusBarHeight(getResources());
            if (statusBarHeight > 0) {
                contentView.setPadding(contentView.getPaddingLeft(), statusBarHeight, contentView.getPaddingRight(), contentView.getPaddingBottom());
                menuRecyclerView.setPadding(contentView.getPaddingLeft(), statusBarHeight, contentView.getPaddingRight(), contentView.getPaddingBottom());
            } else {
                drawerLayout.setFitsSystemWindows(true);
            }
        }

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                toggleDrawable.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                toggleDrawable.onDrawerOpened(drawerView);
                menuRecyclerView.getAdapter().notifyDataSetChanged();
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

    private void initData() {
        AssemblyRecyclerAdapter adapter = new AssemblyRecyclerAdapter(makeMenuList());
        adapter.addItemFactory(new MenuTitleItemFactory());
        adapter.addItemFactory(new PageMenuItemFactory(new PageMenuItemFactory.OnClickItemListener() {
            @Override
            public void onClickItem(Page page) {
                switchPage(page);
            }
        }));
        adapter.addItemFactory(new CheckMenuItemFactory());
        adapter.addItemFactory(new InfoMenuItemFactory());
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        menuRecyclerView.setAdapter(adapter);

        ImageOrientationCorrectTestFileGenerator.getInstance(getBaseContext()).onAppStart();
    }

    private List<Object> makeMenuList() {
        List<Object> menuList = new ArrayList<Object>();

        final View.OnClickListener menuClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        };

        menuList.add("Integrated Page");
        for (Page page : Page.getNormalPage()) {
            if (!page.isDisable()) {
                menuList.add(page);
            }
        }

        menuList.add("Test Page");
        for (Page page : Page.getTestPage()) {
            if (!page.isDisable()) {
                menuList.add(page);
            }
        }

        menuList.add("Cache");
        menuList.add(new InfoMenu("Memory Cache (Click Clean)") {
            @Override
            public String getInfo() {
                MemoryCache memoryCache = Sketch.with(getBaseContext()).getConfiguration().getMemoryCache();
                String usedSizeFormat = Formatter.formatFileSize(getBaseContext(), memoryCache.getSize());
                String maxSizeFormat = Formatter.formatFileSize(getBaseContext(), memoryCache.getMaxSize());
                return usedSizeFormat + "/" + maxSizeFormat;
            }

            @Override
            public void onClick(AssemblyRecyclerAdapter adapter) {
                Sketch.with(getBaseContext()).getConfiguration().getMemoryCache().clear();
                menuClickListener.onClick(null);
                adapter.notifyDataSetChanged();

                EventBus.getDefault().post(new CacheCleanEvent());
            }
        });
        menuList.add(new InfoMenu("Bitmap Pool (Click Clean)") {
            @Override
            public String getInfo() {
                BitmapPool bitmapPool = Sketch.with(getBaseContext()).getConfiguration().getBitmapPool();
                String usedSizeFormat = Formatter.formatFileSize(getBaseContext(), bitmapPool.getSize());
                String maxSizeFormat = Formatter.formatFileSize(getBaseContext(), bitmapPool.getMaxSize());
                return usedSizeFormat + "/" + maxSizeFormat;
            }

            @Override
            public void onClick(AssemblyRecyclerAdapter adapter) {
                Sketch.with(getBaseContext()).getConfiguration().getBitmapPool().clear();
                menuClickListener.onClick(null);
                adapter.notifyDataSetChanged();

                EventBus.getDefault().post(new CacheCleanEvent());
            }
        });
        menuList.add(new InfoMenu("Disk Cache (Click Clean)") {
            @Override
            public String getInfo() {
                DiskCache diskCache = Sketch.with(getBaseContext()).getConfiguration().getDiskCache();
                String usedSizeFormat = Formatter.formatFileSize(getBaseContext(), diskCache.getSize());
                String maxSizeFormat = Formatter.formatFileSize(getBaseContext(), diskCache.getMaxSize());
                return usedSizeFormat + "/" + maxSizeFormat;
            }

            @Override
            public void onClick(final AssemblyRecyclerAdapter adapter) {
                new AsyncTask<Integer, Integer, Integer>() {
                    @Override
                    protected Integer doInBackground(Integer... params) {
                        Sketch.with(getBaseContext()).getConfiguration().getDiskCache().clear();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        menuClickListener.onClick(null);
                        adapter.notifyDataSetChanged();

                        EventBus.getDefault().post(new CacheCleanEvent());
                    }
                }.execute(0);
            }
        });
        menuList.add(new CheckMenu(this, "Disable Memory Cache", AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Disable Bitmap Pool", AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Disable Disk Cache", AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK, null, menuClickListener));

        menuList.add("Gesture Zoom");
        menuList.add(new CheckMenu(this, "Enabled Gesture Zoom In Detail Page", AppConfig.Key.SUPPORT_ZOOM, new CheckMenu.OnCheckedChangedListener() {
            @Override
            public void onCheckedChangedBefore(boolean checked) {
                if (!checked && AppConfig.getBoolean(getBaseContext(), AppConfig.Key.SUPPORT_LARGE_IMAGE)) {
                    AppConfig.putBoolean(getBaseContext(), AppConfig.Key.SUPPORT_LARGE_IMAGE, false);
                }
            }

            @Override
            public void onCheckedChanged(boolean checked) {

            }
        }, menuClickListener));
        menuList.add(new CheckMenu(this, "Enabled Read Mode In Detail Page", AppConfig.Key.READ_MODE, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Enabled Location Animation In Detail Page", AppConfig.Key.LOCATION_ANIMATE, null, menuClickListener));

        menuList.add("Block Display Large Image");
        menuList.add(new CheckMenu(this, "Enabled Block Display Large Image In Detail Page", AppConfig.Key.SUPPORT_LARGE_IMAGE, new CheckMenu.OnCheckedChangedListener() {
            @Override
            public void onCheckedChangedBefore(boolean checked) {
                if (checked && !AppConfig.getBoolean(getBaseContext(), AppConfig.Key.SUPPORT_ZOOM)) {
                    AppConfig.putBoolean(getBaseContext(), AppConfig.Key.SUPPORT_ZOOM, true);
                }
            }

            @Override
            public void onCheckedChanged(boolean checked) {

            }
        }, menuClickListener));
        menuList.add(new CheckMenu(this, "Visible To User Decode Large Image In Detail Page", AppConfig.Key.PAGE_VISIBLE_TO_USER_DECODE_LARGE_IMAGE, null, menuClickListener));

        menuList.add("GIF");
        menuList.add(new CheckMenu(this, "Auto Play GIF In List", AppConfig.Key.PLAY_GIF_ON_LIST, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Click Play GIF In List", AppConfig.Key.CLICK_PLAY_GIF, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Show GIF Flag In List", AppConfig.Key.SHOW_GIF_FLAG, null, menuClickListener));

        menuList.add("Decode");
        menuList.add(new CheckMenu(this, "In Prefer Quality Over Speed", AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Low Quality Bitmap", AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Enabled Thumbnail Mode In List", AppConfig.Key.THUMBNAIL_MODE, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Cache Processed Image In Disk", AppConfig.Key.CACHE_PROCESSED_IMAGE, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Disabled Correct Image Orientation", AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION, null, menuClickListener));

        menuList.add("Other");
        menuList.add(new CheckMenu(this, "Show Unsplash Large Image In Detail Page", AppConfig.Key.SHOW_UNSPLASH_LARGE_IMAGE, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Show Mapping Thumbnail In Detail Page", AppConfig.Key.SHOW_TOOLS_IN_IMAGE_DETAIL, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Show Press Status In List", AppConfig.Key.CLICK_SHOW_PRESSED_STATUS, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Show Image From Corner Mark", AppConfig.Key.SHOW_IMAGE_FROM_FLAG, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Show Download Progress In List", AppConfig.Key.SHOW_IMAGE_DOWNLOAD_PROGRESS, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Click Show Image On Pause Download In List", AppConfig.Key.CLICK_RETRY_ON_PAUSE_DOWNLOAD, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Click Retry On Error In List", AppConfig.Key.CLICK_RETRY_ON_FAILED, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Scrolling Pause Load Image In List", AppConfig.Key.SCROLLING_PAUSE_LOAD, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Mobile Network Pause Download Image", AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD, null, menuClickListener));

        menuList.add("Log");
        menuList.add(new CheckMenu(this, "Output Request Course Log", AppConfig.Key.LOG_REQUEST, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Output Cache Log", AppConfig.Key.LOG_CACHE, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Output Gesture Zoom Log", AppConfig.Key.LOG_ZOOM, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Output Block Display Large Image Log", AppConfig.Key.LOG_LARGE, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Output Used Time Log", AppConfig.Key.LOG_TIME, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Output Other Log", AppConfig.Key.LOG_BASE, null, menuClickListener));
        menuList.add(new CheckMenu(this, "Sync Output Log To Disk (cache/sketch_log)", AppConfig.Key.OUT_LOG_2_SDCARD, null, menuClickListener));

        return menuList;
    }

    @Override
    protected boolean isDisableSetFitsSystemWindows() {
        return true;
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

    private void switchPage(Page newPage) {
        if (this.page == newPage) {
            return;
        }
        this.page = newPage;

        if (page == Page.APP_LIST) {
            AnimationUtils.visibleViewByAlpha(appListTabStrip);
        } else {
            AnimationUtils.invisibleViewByAlpha(appListTabStrip);
        }

        getSupportActionBar().setTitle(page.getName());
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                .replace(R.id.frame_main_content, page.getFragment())
                .commit();

        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
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
    public void onApplyBackground(String imageUri) {
        backgroundImageView.displayImage(imageUri);
        menuBackgroundImageView.displayImage(imageUri);
    }

    public enum Page {
        UNSPLASH("Unsplash", UnsplashPhotosFragment.class, false, false),
        SEARCH("GIF Search", SearchFragment.class, false, false),
        MY_PHOTOS("My Photos", MyPhotosFragment.class, false, false),
        MY_VIDEOS("My Videos", MyVideosFragment.class, false, false),
        APP_LIST("My Apps", AppListFragment.class, false, false),
        ABOUT("About Sketch", AboutFragment.class, false, false),

        LARGE_IMAGE("Block Display Large Image", LargeImageTestFragment.class, true, false),
        IMAGE_PROCESSOR_TEST("Image Processor Test", ImageProcessorTestFragment.class, true, false),
        IMAGE_SHAPER_TESt("Image Shaper Test", ImageShaperTestFragment.class, true, false),
        REPEAT_LOAD_OR_DOWNLOAD_TEST("Repeat Load Or Download Test", RepeatLoadOrDownloadTestFragment.class, true, false),
        IN_BITMAP_TESt("inBitmap Test", InBitmapTestFragment.class, true, false),
        IMAGE_ORIENTATION_TEST("Image Orientation Test", ImageOrientationTestHomeFragment.class, true, false),
        BASE64_IMAGE_TESt("Base64 Image Test", Base64ImageTestFragment.class, true, false),
        OTHER_TEST("Other Test", OtherTestFragment.class, true, !BuildConfig.DEBUG),;

        private String name;
        private Class<? extends Fragment> fragmentClass;
        private boolean disable;
        private boolean test;

        Page(String name, Class<? extends Fragment> fragmentClass, boolean test, boolean disable) {
            this.name = name;
            this.fragmentClass = fragmentClass;
            this.disable = disable;
            this.test = test;
        }

        public static Page[] getNormalPage() {
            List<Page> normalPageList = new LinkedList<Page>();
            for (Page page : values()) {
                if (!page.test) {
                    normalPageList.add(page);
                }
            }
            return normalPageList.toArray(new Page[normalPageList.size()]);
        }

        public static Page[] getTestPage() {
            List<Page> testPageList = new LinkedList<Page>();
            for (Page page : values()) {
                if (page.test) {
                    testPageList.add(page);
                }
            }
            return testPageList.toArray(new Page[testPageList.size()]);
        }

        public String getName() {
            return name;
        }

        public Fragment getFragment() {
            try {
                return fragmentClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
                return null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }

        public boolean isDisable() {
            return disable;
        }
    }

    public static class TitleTabFactory implements PagerSlidingTabStrip.TabViewFactory {
        private String[] titles;
        private Context context;

        public TitleTabFactory(String[] titles, Context context) {
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
