package com.github.panpf.sketch.sample.widget;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.util.Pools;
import androidx.core.view.PointerIconCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.panpf.sketch.sample.R;
import com.google.android.material.tabs.TabLayout;

/**
 * 垂直布局的TabLayout
 * 大部分代码从{@link TabLayout}当中拷贝出来，并结合垂直布局的特性进行改造
 * <p>
 * Created by lemon on 10/01/2018.
 */
public class VerticalTabLayout extends ScrollView {

    public interface OnTabSelectedListener {

        void onTabSelected(Tab tab);

        void onTabUnselected(Tab tab);

        void onTabReselected(Tab tab);
    }

    public interface OnCustomTabViewRenderListener {
        /**
         * 当需要tab的item需要自定义view的时候，通过这个接口方法通知视图渲染
         *
         * @param tab
         */
        void onRender(Tab tab);
    }

    /**
     * 绑定viewpager的时候，构造tabItem的构造器
     */
    public interface ViewPagerTabItemCreator {
        Tab create(int position);
    }


    private static final int DEFAULT_GAP_TEXT_ICON = 8; // dps
    private static final int INDICATOR_GRAVITY_LEFT = 100;
    private static final int INDICATOR_GRAVITY_RIGHT = 101;
    private static final int INDICATOR_GRAVITY_FILL = 102;
    /**
     * configure of styles
     */
    private int mColorIndicator;
    private int mTabMargin;
    private int mIndicatorWidth;
    private int mIndicatorGravity;
    private float mIndicatorCorners;
    private int mTabMode;
    private int mTabHeight;
    private ColorStateList mTabTextColors;
    private int mTabPaddingStart;
    private int mTabPaddingTop;
    private int mTabPaddingEnd;
    private int mTabPaddingBottom;

    private int mIndicatorPaddingStart;
    private int mIndicatorPaddingTop;
    private int mIndicatorPaddingEnd;
    private int mIndicatorPaddingBottom;

    private int mIndicatorAnimDuration;

    public static int TAB_MODE_FIXED = 10;
    public static int TAB_MODE_SCROLLABLE = 11;

    /**
     * configure for {@link #setupWithViewPager(ViewPager)}
     */
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private OnTabPageChangeListener mTabPageChangeListener;
    private ViewPagerOnVerticalTabSelectedListener currentVpSelectedListener;
    private DataSetObserver mPagerAdapterObserver;
    /**
     * tabLayout properties
     */
    private ViewPagerTabItemCreator tabItemCreator;
    private TabStrip mTabStrip;
    private Tab mSelectedTab;
    private List<Tab> tabs = new ArrayList<>();
    private List<OnTabSelectedListener> mTabSelectedListeners = new ArrayList<>();
    private static final Pools.Pool<Tab> sTabPool = new Pools.SynchronizedPool<>(16);
    private final Pools.Pool<TabView> mTabViewPool = new Pools.SimplePool<>(12);


    public VerticalTabLayout(Context context) {
        this(context, null);
    }

    public VerticalTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyleConfigure(context, attrs);
    }

    private void initStyleConfigure(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalTabLayout);
        mColorIndicator = typedArray.getColor(R.styleable.VerticalTabLayout_indicator_color, context.getResources().getColor(R.color.colorAccent));
        mIndicatorWidth = (int) typedArray.getDimension(R.styleable.VerticalTabLayout_indicator_width, dpToPx(3));
        mIndicatorCorners = typedArray.getDimension(R.styleable.VerticalTabLayout_indicator_corners, 0);
        mIndicatorGravity = typedArray.getInteger(R.styleable.VerticalTabLayout_indicator_gravity, Gravity.LEFT);
        if (mIndicatorGravity == INDICATOR_GRAVITY_LEFT) {
            mIndicatorGravity = Gravity.LEFT;
        } else if (mIndicatorGravity == INDICATOR_GRAVITY_RIGHT) {
            mIndicatorGravity = Gravity.RIGHT;
        } else if (mIndicatorGravity == INDICATOR_GRAVITY_FILL) {
            mIndicatorGravity = Gravity.FILL;
        }
        mIndicatorAnimDuration = typedArray.getInt(R.styleable.VerticalTabLayout_anim_duration, 100);
        mTabMargin = (int) typedArray.getDimension(R.styleable.VerticalTabLayout_tab_margin, 0);
        mTabMode = typedArray.getInteger(R.styleable.VerticalTabLayout_tab_mode, TAB_MODE_FIXED);
        int defaultTabHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        mTabHeight = (int) typedArray.getDimension(R.styleable.VerticalTabLayout_tab_height, defaultTabHeight);

        mTabPaddingStart = mTabPaddingTop = mTabPaddingEnd = mTabPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.VerticalTabLayout_tab_padding, 0);
        mTabPaddingStart = typedArray.getDimensionPixelSize(R.styleable.VerticalTabLayout_tab_paddingStart, mTabPaddingStart);
        mTabPaddingTop = typedArray.getDimensionPixelSize(R.styleable.VerticalTabLayout_tab_paddingTop, mTabPaddingTop);
        mTabPaddingEnd = typedArray.getDimensionPixelSize(R.styleable.VerticalTabLayout_tab_paddingEnd, mTabPaddingEnd);
        mTabPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.VerticalTabLayout_tab_paddingBottom, mTabPaddingBottom);

        mIndicatorPaddingStart = mIndicatorPaddingEnd = mIndicatorPaddingTop = mIndicatorPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.VerticalTabLayout_indicator_padding, 0);
        mIndicatorPaddingStart = typedArray.getDimensionPixelSize(R.styleable.VerticalTabLayout_indicator_paddingStart, mIndicatorPaddingStart);
        mIndicatorPaddingEnd = typedArray.getDimensionPixelSize(R.styleable.VerticalTabLayout_indicator_paddingEnd, mIndicatorPaddingEnd);
        mIndicatorPaddingTop = typedArray.getDimensionPixelSize(R.styleable.VerticalTabLayout_indicator_paddingTop, mIndicatorPaddingTop);
        mIndicatorPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.VerticalTabLayout_indicator_paddingBottom, mIndicatorPaddingBottom);

        int textColor = typedArray.getColor(R.styleable.VerticalTabLayout_tab_textColor, Color.BLACK);
        int selected = typedArray.getColor(R.styleable.VerticalTabLayout_tab_textSelectedColor, Color.BLACK);
        mTabTextColors = createColorStateList(textColor, selected);

        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) removeAllViews();
        initTabStrip();
    }

    private static ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];
        int i = 0;

        states[i] = SELECTED_STATE_SET;
        colors[i] = selectedColor;
        i++;

        // Default enabled state
        states[i] = EMPTY_STATE_SET;
        colors[i] = defaultColor;
        i++;

        return new ColorStateList(states, colors);
    }

    private void initTabStrip() {
        mTabStrip = new TabStrip(getContext());
        addView(mTabStrip, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public int getTabCount() {
        return tabs.size();
    }

    public int getSelectedTabPosition() {
        return mSelectedTab != null ? mSelectedTab.getPosition() : -1;
    }

    public void setTabMode(int mode) {
        if (mode != TAB_MODE_FIXED && mode != TAB_MODE_SCROLLABLE) {
            throw new IllegalStateException("only support TAB_MODE_FIXED or TAB_MODE_SCROLLABLE");
        }
        if (mode == mTabMode) return;
        mTabMode = mode;
        for (int i = 0; i < mTabStrip.getChildCount(); i++) {
            View view = mTabStrip.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            initTabWithMode(params);
            if (i == 0) {
                params.setMargins(0, 0, 0, 0);
            }
            view.setLayoutParams(params);
        }
        mTabStrip.invalidate();
        mTabStrip.post(new Runnable() {
            @Override
            public void run() {
                mTabStrip.updateIndicator();
            }
        });
    }

    /**
     * {@link #TAB_MODE_SCROLLABLE}的时候有效
     *
     * @param margin margin
     */
    public void setTabMargin(int margin) {
        if (margin == mTabMargin) return;
        mTabMargin = margin;
        if (mTabMode == TAB_MODE_FIXED) return;
        for (int i = 0; i < mTabStrip.getChildCount(); i++) {
            View view = mTabStrip.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.setMargins(0, i == 0 ? 0 : mTabMargin, 0, 0);
            view.setLayoutParams(params);
        }
        mTabStrip.invalidate();
        mTabStrip.post(new Runnable() {
            @Override
            public void run() {
                mTabStrip.updateIndicator();
            }
        });
    }

    /**
     * 只在{@link #TAB_MODE_SCROLLABLE}的时候有效
     *
     * @param height height
     */
    public void setTabHeight(int height) {
        if (height == mTabHeight) return;
        mTabHeight = height;
        if (mTabMode == TAB_MODE_FIXED) return;
        for (int i = 0; i < mTabStrip.getChildCount(); i++) {
            View view = mTabStrip.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.height = mTabHeight;
            view.setLayoutParams(params);
        }
        mTabStrip.invalidate();
        mTabStrip.post(new Runnable() {
            @Override
            public void run() {
                mTabStrip.updateIndicator();
            }
        });
    }

    public void setIndicatorColor(int color) {
        mColorIndicator = color;
        mTabStrip.invalidate();
    }

    public void setIndicatorWidth(int width) {
        mIndicatorWidth = width;
        mTabStrip.setIndicatorGravity();
    }

    public void setIndicatorCorners(int corners) {
        mIndicatorCorners = corners;
        mTabStrip.invalidate();
    }

    /**
     * @param gravity only support Gravity.LEFT,Gravity.RIGHT,Gravity.FILL
     */
    public void setIndicatorGravity(int gravity) {
        if (gravity == Gravity.LEFT || gravity == Gravity.RIGHT || Gravity.FILL == gravity) {
            mIndicatorGravity = gravity;
            mTabStrip.setIndicatorGravity();
        } else {
            throw new IllegalStateException("only support Gravity.LEFT,Gravity.RIGHT,Gravity.FILL");
        }
    }

    public void addOnTabSelectedListener(OnTabSelectedListener listener) {
        if (listener != null && !mTabSelectedListeners.contains(listener)) {
            mTabSelectedListeners.add(listener);
        }
    }

    public void removeOnTabSelectedListener(OnTabSelectedListener listener) {
        if (listener != null) {
            mTabSelectedListeners.remove(listener);
        }
    }

    public Tab newTab() {
        Tab tab = sTabPool.acquire();
        if (tab == null) {
            tab = new Tab();
        }
        tab.mParent = this;
        tab.mView = createTabView(tab);
        return tab;
    }

    public void addTab(Tab tab) {
        addTab(tab, tabs.size());
    }

    public void addTab(Tab tab, int position) {
        addTab(tab, position, false);
    }

    public void addTab(Tab tab, boolean selected) {
        addTab(tab, tabs.size(), selected);
    }

    public void addTab(Tab tab, int position, boolean selected) {
        configureTab(tab, position);
        addTabView(tab);
        if (selected) {
            tab.select();
        }
    }

    private TabView createTabView(@NonNull final Tab tab) {
        TabView tabView = mTabViewPool != null ? mTabViewPool.acquire() : null;
        if (tabView == null) {
            tabView = new TabView(getContext());
        }
        tabView.setTab(tab);
        tabView.setFocusable(true);
        return tabView;
    }

    private void configureTab(Tab tab, int position) {
        tab.setPosition(position);
        tabs.add(position, tab);
        final int count = tabs.size();
        for (int i = position + 1; i < count; i++) {
            tabs.get(i).setPosition(i);
        }
    }

    private void addTabView(Tab tab) {
        final TabView tabView = tab.mView;
        addTabWithMode(tabView);
        if (mTabStrip.indexOfChild(tabView) == 0) {
            tabView.setSelected(true);
            mSelectedTab = tab;
            mTabStrip.post(new Runnable() {
                @Override
                public void run() {
                    mTabStrip.moveIndicator(0);
                }
            });
        }
    }

    private void addTabWithMode(TabView tabView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        initTabWithMode(params);
        mTabStrip.addView(tabView, params);
    }

    private void initTabWithMode(LinearLayout.LayoutParams params) {
        if (mTabMode == TAB_MODE_FIXED) {
            params.height = 0;
            params.weight = 1.0f;
            params.setMargins(0, 0, 0, 0);
            setFillViewport(true);
        } else if (mTabMode == TAB_MODE_SCROLLABLE) {
            params.height = mTabHeight;
            params.weight = 0f;
            params.setMargins(0, mTabMargin, 0, 0);
            setFillViewport(false);
        }
    }

    public void removeTabAt(int position) {
        final int selectedTabPosition = mSelectedTab != null ? mSelectedTab.getPosition() : 0;
        removeTabViewAt(position);
        final Tab removedTab = tabs.remove(position);
        if (removedTab != null) {
            removedTab.reset();
            sTabPool.release(removedTab);
        }
        final int newTabCount = tabs.size();
        for (int i = position; i < newTabCount; i++) {
            tabs.get(i).setPosition(i);
        }
        if (selectedTabPosition == position) {
            selectTab(tabs.isEmpty() ? null : tabs.get(Math.max(0, position - 1)));
        }
    }

    public void removeAllTabs() {
        for (int i = mTabStrip.getChildCount() - 1; i >= 0; i--) {
            removeTabViewAt(i);
        }
        for (final Iterator<Tab> i = tabs.iterator(); i.hasNext(); ) {
            final Tab tab = i.next();
            i.remove();
            tab.reset();
            sTabPool.release(tab);
        }
        mSelectedTab = null;
    }

    private void removeTabViewAt(int position) {
        final TabView view = (TabView) mTabStrip.getChildAt(position);
        mTabStrip.removeViewAt(position);
        if (view != null) {
            view.reset();
            mTabViewPool.release(view);
        }
        requestLayout();
    }

    public Tab getTabAt(int index) {
        return (index < 0 || index >= getTabCount()) ? null : tabs.get(index);
    }

    void selectTab(final Tab tab) {
        selectTab(tab, true);
    }

    void selectTab(final Tab tab, boolean updateIndicator) {
        final Tab currentTab = mSelectedTab;
        if (currentTab == tab) {
            if (currentTab != null) {
                dispatchTabReselected(tab);
                scrollToTab(tab.getPosition());
            }
        } else {
            final int newPosition = tab != null ? tab.getPosition() : TabLayout.Tab.INVALID_POSITION;
            setSelectedTabView(newPosition);
            if (updateIndicator) {
                mTabStrip.moveIndicatorWithAnimator(newPosition);
            }
            if (currentTab != null) {
                dispatchTabUnselected(currentTab);
            }
            mSelectedTab = tab;
            if (tab != null) {
                scrollToTab(tab.getPosition());
                dispatchTabSelected(tab);
            }
        }
    }

    private void setSelectedTabView(int position) {
        final int tabCount = mTabStrip.getChildCount();
        if (position < tabCount) {
            for (int i = 0; i < tabCount; i++) {
                final View child = mTabStrip.getChildAt(i);
                child.setSelected(i == position);
            }
        }
    }

    private void dispatchTabSelected(Tab tab) {
        for (int i = 0; i < mTabSelectedListeners.size(); i++) {
            mTabSelectedListeners.get(i).onTabSelected(tab);
        }
    }

    private void dispatchTabUnselected(Tab tab) {
        for (int i = 0; i < mTabSelectedListeners.size(); i++) {
            mTabSelectedListeners.get(i).onTabUnselected(tab);
        }
    }

    private void dispatchTabReselected(Tab tab) {
        for (int i = 0; i < mTabSelectedListeners.size(); i++) {
            mTabSelectedListeners.get(i).onTabReselected(tab);
        }
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        setupWithViewPager(viewPager, null);
    }

    public void setupWithViewPager(ViewPager viewPager, ViewPagerTabItemCreator creator) {
        this.tabItemCreator = creator;
        if (mViewPager != null && mTabPageChangeListener != null) {
            mViewPager.removeOnPageChangeListener(mTabPageChangeListener);
        }

        if (viewPager != null) {
            final PagerAdapter adapter = viewPager.getAdapter();
            if (adapter == null) {
                throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
            }
            mViewPager = viewPager;
            if (mTabPageChangeListener == null) {
                mTabPageChangeListener = new OnTabPageChangeListener(this);
            }
            viewPager.addOnPageChangeListener(mTabPageChangeListener);
            if (currentVpSelectedListener == null) {
                currentVpSelectedListener = new ViewPagerOnVerticalTabSelectedListener(viewPager);
            }
            addOnTabSelectedListener(currentVpSelectedListener);
            setPagerAdapter(adapter);
        } else {
            mViewPager = null;
            setPagerAdapter(null);
        }
        populateFromPagerAdapter(creator);
    }

    private void setPagerAdapter(@Nullable final PagerAdapter adapter) {
        if (mPagerAdapter != null && mPagerAdapterObserver != null) {
            mPagerAdapter.unregisterDataSetObserver(mPagerAdapterObserver);
        }
        mPagerAdapter = adapter;
        if (adapter != null) {
            if (mPagerAdapterObserver == null) {
                mPagerAdapterObserver = new PagerAdapterObserver();
            }
            adapter.registerDataSetObserver(mPagerAdapterObserver);
        }
    }

    private void populateFromPagerAdapter(ViewPagerTabItemCreator creator) {
        removeAllTabs();
        if (mPagerAdapter != null) {
            final int adapterCount = mPagerAdapter.getCount();
            for (int i = 0; i < adapterCount; i++) {
                String title = mPagerAdapter.getPageTitle(i) == null ? "tab" + i : mPagerAdapter.getPageTitle(i).toString();
                if (creator != null) {
                    addTab(creator.create(i), false);
                } else {
                    addTab(newTab().setText(title), false);
                }
            }
            if (mViewPager != null && adapterCount > 0) {
                final int curItem = mViewPager.getCurrentItem();
                if (curItem != getSelectedTabPosition() && curItem < getTabCount()) {
                    selectTab(getTabAt(curItem));
                }
            }
        }
    }

    private void scrollToTab(int position) {
        final TabView tabView = getTabAt(position).mView;
        int y = getScrollY();
        int tabTop = tabView.getTop() + tabView.getHeight() / 2 - y;
        int target = getHeight() / 2;
        if (tabTop > target) {
            smoothScrollBy(0, tabTop - target);
        } else if (tabTop < target) {
            smoothScrollBy(0, tabTop - target);
        }
    }

    public void setScrollPosition(int position, float positionOffset) {
        mTabStrip.moveIndicator(positionOffset + position);
    }

    private int dpToPx(int dps) {
        return Math.round(getResources().getDisplayMetrics().density * dps);
    }

    private class TabStrip extends LinearLayout {
        private float mIndicatorTopY;
        private float mIndicatorX;
        private float mIndicatorBottomY;
        private int mLastWidth;
        private Paint mIndicatorPaint;
        private RectF mIndicatorRect;
        private AnimatorSet mIndicatorAnimatorSet;

        public TabStrip(Context context) {
            super(context);
            setWillNotDraw(false);
            setOrientation(LinearLayout.VERTICAL);
            mIndicatorPaint = new Paint();
            mIndicatorPaint.setAntiAlias(true);
            mIndicatorGravity = mIndicatorGravity == 0 ? Gravity.LEFT : mIndicatorGravity;
            mIndicatorRect = new RectF();
            setIndicatorGravity();
        }

        protected void setIndicatorGravity() {
            if (mIndicatorGravity == Gravity.LEFT) {
                mIndicatorX = 0;
                if (mLastWidth != 0) mIndicatorWidth = mLastWidth;
                setPadding(mIndicatorWidth, 0, 0, 0);
            } else if (mIndicatorGravity == Gravity.RIGHT) {
                if (mLastWidth != 0) mIndicatorWidth = mLastWidth;
                setPadding(0, 0, mIndicatorWidth, 0);
            } else if (mIndicatorGravity == Gravity.FILL) {
                mIndicatorX = 0;
                setPadding(0, 0, 0, 0);
            }
            post(new Runnable() {
                @Override
                public void run() {
                    if (mIndicatorGravity == Gravity.RIGHT) {
                        mIndicatorX = getWidth() - mIndicatorWidth;
                    } else if (mIndicatorGravity == Gravity.FILL) {
                        mLastWidth = mIndicatorWidth;
                        mIndicatorWidth = getWidth();
                    }
                    invalidate();
                }
            });
        }

        private void calcIndicatorY(float offset) {
            int index = (int) Math.floor(offset);
            View childView = getChildAt(index);
            if (Math.floor(offset) != getChildCount() - 1 && Math.ceil(offset) != 0) {
                View nextView = getChildAt(index + 1);
                mIndicatorTopY = childView.getTop() + (nextView.getTop() - childView.getTop()) * (offset - index);
                mIndicatorBottomY = childView.getBottom() + (nextView.getBottom() - childView.getBottom()) * (offset - index);
            } else {
                mIndicatorTopY = childView.getTop();
                mIndicatorBottomY = childView.getBottom();
            }
        }

        protected void updateIndicator() {
            moveIndicatorWithAnimator(getSelectedTabPosition());
        }

        protected void moveIndicator(float offset) {
            calcIndicatorY(offset);
            invalidate();
        }

        /**
         * move indicator to a tab location
         *
         * @param index tab location's index
         */
        protected void moveIndicatorWithAnimator(int index) {
            final int direction = index - getSelectedTabPosition();
            View childView = getChildAt(index);
            final float targetTop = childView.getTop();
            final float targetBottom = childView.getBottom();

            if (mIndicatorTopY == targetTop && mIndicatorBottomY == targetBottom) return;
            if (mIndicatorAnimatorSet != null && mIndicatorAnimatorSet.isRunning()) {
                mIndicatorAnimatorSet.end();
            }
            post(new Runnable() {
                @Override
                public void run() {
                    ValueAnimator startAnimate = null;
                    ValueAnimator endAnimate = null;
                    if (direction > 0) {
                        startAnimate = ValueAnimator.ofFloat(mIndicatorBottomY, targetBottom).setDuration(mIndicatorAnimDuration);
                        startAnimate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                mIndicatorBottomY = Float.parseFloat(animation.getAnimatedValue().toString());
                                invalidate();
                            }
                        });
                        endAnimate = ValueAnimator.ofFloat(mIndicatorTopY, targetTop).setDuration(mIndicatorAnimDuration);
                        endAnimate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                mIndicatorTopY = Float.parseFloat(animation.getAnimatedValue().toString());
                                invalidate();
                            }
                        });
                    } else if (direction < 0) {
                        startAnimate = ValueAnimator.ofFloat(mIndicatorTopY, targetTop).setDuration(mIndicatorAnimDuration);
                        startAnimate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                mIndicatorTopY = Float.parseFloat(animation.getAnimatedValue().toString());
                                invalidate();
                            }
                        });
                        endAnimate = ValueAnimator.ofFloat(mIndicatorBottomY, targetBottom).setDuration(mIndicatorAnimDuration);
                        endAnimate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                mIndicatorBottomY = Float.parseFloat(animation.getAnimatedValue().toString());
                                invalidate();
                            }
                        });
                    }
                    if (startAnimate != null) {
                        mIndicatorAnimatorSet = new AnimatorSet();
                        mIndicatorAnimatorSet.play(endAnimate).after(startAnimate);
                        mIndicatorAnimatorSet.start();
                    }
                }
            });

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            mIndicatorPaint.setColor(mColorIndicator);
            mIndicatorRect.left = mIndicatorX + mIndicatorPaddingStart;
            mIndicatorRect.top = mIndicatorTopY + mIndicatorPaddingTop;
            mIndicatorRect.right = mIndicatorX + mIndicatorWidth - mIndicatorPaddingEnd;
            mIndicatorRect.bottom = mIndicatorBottomY - mIndicatorPaddingBottom;
            if (mIndicatorCorners != 0) {
                canvas.drawRoundRect(mIndicatorRect, mIndicatorCorners, mIndicatorCorners, mIndicatorPaint);
            } else {
                canvas.drawRect(mIndicatorRect, mIndicatorPaint);
            }
        }

    }

    /**
     * modify from {@link TabLayout.Tab}
     * <p>
     * {@link VerticalTabLayout}的子单元
     * 通过{@link VerticalTabLayout#addTab(Tab)}添加item
     * 通过{@link VerticalTabLayout#newTab()}构建实例
     */
    public static final class Tab {

        public static final int INVALID_POSITION = -1;

        private Object mTag;
        private Drawable mIcon;
        private CharSequence mText;
        private CharSequence mContentDesc;
        private int mPosition = INVALID_POSITION;
        private View mCustomView;
        private OnCustomTabViewRenderListener renderListener;

        VerticalTabLayout mParent;
        TabView mView;

        Tab() {
        }

        @Nullable
        public Object getTag() {
            return mTag;
        }

        @NonNull
        public Tab setTag(@Nullable Object tag) {
            mTag = tag;
            return this;
        }

        @Nullable
        public View getCustomView() {
            return mCustomView;
        }

        @NonNull
        public Tab setCustomView(@Nullable View view, OnCustomTabViewRenderListener listener) {
            mCustomView = view;
            renderListener = listener;
            updateView();
            return this;
        }

        @NonNull
        public Tab setCustomView(@LayoutRes int resId, OnCustomTabViewRenderListener listener) {
            final LayoutInflater inflater = LayoutInflater.from(mView.getContext());
            return setCustomView(inflater.inflate(resId, mView, false), listener);
        }

        @Nullable
        public Drawable getIcon() {
            return mIcon;
        }

        public int getPosition() {
            return mPosition;
        }

        void setPosition(int position) {
            mPosition = position;
        }

        @Nullable
        public CharSequence getText() {
            return mText;
        }

        @NonNull
        public Tab setIcon(@Nullable Drawable icon) {
            mIcon = icon;
            updateView();
            return this;
        }

        @NonNull
        public Tab setIcon(@DrawableRes int resId) {
            if (mParent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            return setIcon(AppCompatResources.getDrawable(mParent.getContext(), resId));
        }

        @NonNull
        public Tab setText(@Nullable CharSequence text) {
            mText = text;
            updateView();
            return this;
        }

        @NonNull
        public Tab setText(@StringRes int resId) {
            if (mParent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            return setText(mParent.getResources().getText(resId));
        }


        public void select() {
            if (mParent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            mParent.selectTab(this);
        }

        public boolean isSelected() {
            if (mParent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            return mParent.getSelectedTabPosition() == mPosition;
        }

        @NonNull
        public Tab setContentDescription(@StringRes int resId) {
            if (mParent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            return setContentDescription(mParent.getResources().getText(resId));
        }

        @NonNull
        public Tab setContentDescription(@Nullable CharSequence contentDesc) {
            mContentDesc = contentDesc;
            updateView();
            return this;
        }

        @Nullable
        public CharSequence getContentDescription() {
            return mContentDesc;
        }

        void updateView() {
            if (mView != null) {
                mView.update();
                // 如果view是自定义的view，通过接口将渲染事件传递出去
                if (renderListener != null) {
                    renderListener.onRender(this);
                }
            }
        }

        void reset() {
            mParent = null;
            mView = null;
            mTag = null;
            mIcon = null;
            mText = null;
            mContentDesc = null;
            mPosition = INVALID_POSITION;
            mCustomView = null;
            renderListener = null;
        }
    }

    /**
     * modify from {@link TabLayout.TabView}
     * <p>
     * tab的视图，由一个简单的{@link ImageView }+ {@link TextView} 组成
     * 如果需要复杂的视图效果可以通过{@link Tab#setCustomView(View, OnCustomTabViewRenderListener)}设置自定义的view
     */
    class TabView extends LinearLayout {
        private Tab mTab;
        private TextView mTextView;
        private ImageView mIconView;
        private View mCustomView;

        public TabView(Context context) {
            super(context);

            ViewCompat.setPaddingRelative(this, mTabPaddingStart, mTabPaddingTop, mTabPaddingEnd, mTabPaddingBottom);
            setGravity(Gravity.CENTER);
            setOrientation(VERTICAL);
            setClickable(true);
            ViewCompat.setPointerIcon(this, PointerIconCompat.getSystemIcon(getContext(), PointerIconCompat.TYPE_HAND));
        }

        @Override
        public boolean performClick() {
            final boolean handled = super.performClick();
            if (mTab != null) {
                if (!handled) {
                    playSoundEffect(SoundEffectConstants.CLICK);
                }
                mTab.select();
                return true;
            } else {
                return handled;
            }
        }

        @Override
        public void setSelected(final boolean selected) {
            final boolean changed = isSelected() != selected;

            super.setSelected(selected);

            if (changed && selected && Build.VERSION.SDK_INT < 16) {
                // Pre-JB we need to manually send the TYPE_VIEW_SELECTED event
                sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
            }

            if (mTextView != null) {
                mTextView.setSelected(selected);
            }
            if (mIconView != null) {
                mIconView.setSelected(selected);
            }
            if (mCustomView != null) {
                mCustomView.setSelected(selected);
            }
        }

        void setTab(@Nullable final Tab tab) {
            if (mTab != tab) {
                mTab = tab;
                update();
            }
        }

        void reset() {
            setTab(null);
            setSelected(false);
        }

        final void update() {
            final Tab tab = mTab;
            final View custom = tab != null ? tab.getCustomView() : null;
            if (custom != null) {
                final ViewParent customParent = custom.getParent();
                if (customParent != this) {
                    if (customParent != null) {
                        ((ViewGroup) customParent).removeView(custom);
                    }
                    addView(custom);
                }
                mCustomView = custom;
                if (mTextView != null) {
                    mTextView.setVisibility(GONE);
                }
                if (mIconView != null) {
                    mIconView.setVisibility(GONE);
                    mIconView.setImageDrawable(null);
                }
            } else {
                // We do not have a custom view. Remove one if it already exists
                if (mCustomView != null) {
                    removeView(mCustomView);
                    mCustomView = null;
                }
            }

            if (mCustomView == null) {
                // If there isn't a custom view, we'll us our own in-built layouts
                if (mIconView == null) {
                    ImageView iconView = (ImageView) LayoutInflater.from(getContext()).inflate(com.google.android.material.R.layout.design_layout_tab_icon, this, false);
                    addView(iconView, 0);
                    mIconView = iconView;
                }
                if (mTextView == null) {
                    TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(com.google.android.material.R.layout.design_layout_tab_text, this, false);
                    addView(textView);
                    mTextView = textView;
                }
                if (mTabTextColors != null) {
                    mTextView.setTextColor(mTabTextColors);
                }
                updateTextAndIcon(mTextView, mIconView);
            } else {
                if (tab.renderListener != null) {
                    tab.renderListener.onRender(tab);
                }
            }
            setSelected(tab != null && tab.isSelected());
        }

        private void updateTextAndIcon(@Nullable final TextView textView, @Nullable final ImageView iconView) {
            final Drawable icon = mTab != null ? mTab.getIcon() : null;
            final CharSequence text = mTab != null ? mTab.getText() : null;
            final CharSequence contentDesc = mTab != null ? mTab.getContentDescription() : null;

            if (iconView != null) {
                if (icon != null) {
                    iconView.setImageDrawable(icon);
                    iconView.setVisibility(VISIBLE);
                    setVisibility(VISIBLE);
                } else {
                    iconView.setVisibility(GONE);
                    iconView.setImageDrawable(null);
                }
                iconView.setContentDescription(contentDesc);
            }

            final boolean hasText = !TextUtils.isEmpty(text);
            if (textView != null) {
                if (hasText) {
                    textView.setText(text);
                    textView.setVisibility(VISIBLE);
                    setVisibility(VISIBLE);
                } else {
                    textView.setVisibility(GONE);
                    textView.setText(null);
                }
                textView.setContentDescription(contentDesc);
            }

            if (iconView != null) {
                MarginLayoutParams lp = ((MarginLayoutParams) iconView.getLayoutParams());
                int bottomMargin = 0;
                if (hasText && iconView.getVisibility() == VISIBLE) {
                    // If we're showing both text and icon, add some margin bottom to the icon
                    bottomMargin = dpToPx(DEFAULT_GAP_TEXT_ICON);
                }
                if (bottomMargin != lp.bottomMargin) {
                    lp.bottomMargin = bottomMargin;
                    iconView.requestLayout();
                }
            }
            TooltipCompat.setTooltipText(this, hasText ? null : contentDesc);
        }

        public Tab getTab() {
            return mTab;
        }
    }

    /**
     * {@link ViewPager}和{@link VerticalTabLayout}的联动
     * 监听{@link ViewPager}的变化，更新{@link VerticalTabLayout}
     */
    private static class OnTabPageChangeListener implements ViewPager.OnPageChangeListener {
        private int mPreviousScrollState;
        private final WeakReference<VerticalTabLayout> mTabLayoutRef;
        private int mScrollState;
        boolean mUpdateIndicator;

        public OnTabPageChangeListener(VerticalTabLayout tabLayout) {
            mTabLayoutRef = new WeakReference<>(tabLayout);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mPreviousScrollState = mScrollState;
            mScrollState = state;
            mUpdateIndicator = !(mScrollState == ViewPager.SCROLL_STATE_SETTLING && mPreviousScrollState == ViewPager.SCROLL_STATE_IDLE);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            final VerticalTabLayout tabLayout = mTabLayoutRef.get();
            if (mUpdateIndicator && tabLayout != null) {
                tabLayout.setScrollPosition(position, positionOffset);
            }
        }

        @Override
        public void onPageSelected(int position) {
            final VerticalTabLayout tabLayout = mTabLayoutRef.get();
            if (tabLayout != null && tabLayout.getSelectedTabPosition() != position && position < tabLayout.getTabCount()) {
                tabLayout.selectTab(tabLayout.getTabAt(position), !mUpdateIndicator);
            }
        }
    }

    /**
     * 监听{@link ViewPager}的数据源{@link PagerAdapter}的变化
     */
    private class PagerAdapterObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            populateFromPagerAdapter(tabItemCreator);
        }

        @Override
        public void onInvalidated() {
            populateFromPagerAdapter(tabItemCreator);
        }
    }

    /**
     * {@link ViewPager}和{@link VerticalTabLayout}的联动
     * 监听{@link VerticalTabLayout}的变化，更新{@link ViewPager}
     */
    public static class ViewPagerOnVerticalTabSelectedListener implements OnTabSelectedListener {

        private final WeakReference<ViewPager> viewPagerRef;

        public ViewPagerOnVerticalTabSelectedListener(ViewPager viewPager) {
            this.viewPagerRef = new WeakReference<>(viewPager);
        }

        @Override
        public void onTabSelected(Tab tab) {
            ViewPager viewPager = viewPagerRef.get();
            if (viewPager != null && viewPager.getAdapter().getCount() >= tab.getPosition()) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        }

        @Override
        public void onTabUnselected(Tab tab) {

        }

        @Override
        public void onTabReselected(Tab tab) {

        }
    }

}