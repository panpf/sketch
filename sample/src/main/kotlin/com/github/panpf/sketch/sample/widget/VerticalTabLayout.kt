package com.github.panpf.sketch.sample.widget

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.content.res.ColorStateList
import android.database.DataSetObserver
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.TooltipCompat
import androidx.core.util.Pools.Pool
import androidx.core.util.Pools.SimplePool
import androidx.core.util.Pools.SynchronizedPool
import androidx.core.view.PointerIconCompat
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.github.panpf.sketch.sample.R.color
import com.github.panpf.sketch.sample.R.styleable
import com.google.android.material.R.layout
import com.google.android.material.tabs.TabLayout
import java.lang.ref.WeakReference

/**
 * 垂直布局的TabLayout
 * 大部分代码从[TabLayout]当中拷贝出来，并结合垂直布局的特性进行改造
 *
 * Created by lemon on 10/01/2018.
 */
class VerticalTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {
    private val mTabViewPool: Pool<TabView>? = SimplePool(12)

    /**
     * configure of styles
     */
    private var mColorIndicator = 0
    private var mTabMargin = 0
    private var mIndicatorWidth = 0
    private var mIndicatorGravity = 0
    private var mIndicatorCorners = 0f
    private var mTabMode = 0
    private var mTabHeight = 0
    private var mTabTextColors: ColorStateList? = null
    private var mTabPaddingStart = 0
    private var mTabPaddingTop = 0
    private var mTabPaddingEnd = 0
    private var mTabPaddingBottom = 0
    private var mIndicatorPaddingStart = 0
    private var mIndicatorPaddingTop = 0
    private var mIndicatorPaddingEnd = 0
    private var mIndicatorPaddingBottom = 0
    private var mIndicatorAnimDuration = 0

    /**
     * configure for [.setupWithViewPager]
     */
    private var mViewPager: ViewPager? = null
    private var mPagerAdapter: PagerAdapter? = null
    private var mTabPageChangeListener: OnTabPageChangeListener? = null
    private var currentVpSelectedListener: ViewPagerOnVerticalTabSelectedListener? = null
    private var mPagerAdapterObserver: DataSetObserver? = null

    /**
     * tabLayout properties
     */
    private var tabItemCreator: ViewPagerTabItemCreator? = null
    private var mTabStrip: TabStrip? = null
    private var mSelectedTab: Tab? = null
    private val tabs: MutableList<Tab> = ArrayList()
    private val mTabSelectedListeners: MutableList<OnTabSelectedListener> = ArrayList()

    init {
        initStyleConfigure(context, attrs)
    }

    private fun initStyleConfigure(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, styleable.VerticalTabLayout)
        mColorIndicator = typedArray.getColor(
            styleable.VerticalTabLayout_indicator_color, context.resources.getColor(
                color.colorAccent
            )
        )
        mIndicatorWidth = typedArray.getDimension(
            styleable.VerticalTabLayout_indicator_width,
            dpToPx(3).toFloat()
        ).toInt()
        mIndicatorCorners =
            typedArray.getDimension(styleable.VerticalTabLayout_indicator_corners, 0f)
        mIndicatorGravity =
            typedArray.getInteger(styleable.VerticalTabLayout_indicator_gravity, Gravity.LEFT)
        if (mIndicatorGravity == INDICATOR_GRAVITY_LEFT) {
            mIndicatorGravity = Gravity.LEFT
        } else if (mIndicatorGravity == INDICATOR_GRAVITY_RIGHT) {
            mIndicatorGravity = Gravity.RIGHT
        } else if (mIndicatorGravity == INDICATOR_GRAVITY_FILL) {
            mIndicatorGravity = Gravity.FILL
        }
        mIndicatorAnimDuration = typedArray.getInt(styleable.VerticalTabLayout_anim_duration, 100)
        mTabMargin = typedArray.getDimension(styleable.VerticalTabLayout_tab_margin, 0f).toInt()
        mTabMode = typedArray.getInteger(styleable.VerticalTabLayout_tab_mode, TAB_MODE_FIXED)
        val defaultTabHeight = LinearLayout.LayoutParams.WRAP_CONTENT
        mTabHeight = typedArray.getDimension(
            styleable.VerticalTabLayout_tab_height,
            defaultTabHeight.toFloat()
        ).toInt()
        mTabPaddingBottom =
            typedArray.getDimensionPixelSize(styleable.VerticalTabLayout_tab_padding, 0)
        mTabPaddingEnd = mTabPaddingBottom
        mTabPaddingTop = mTabPaddingEnd
        mTabPaddingStart = mTabPaddingTop
        mTabPaddingStart = typedArray.getDimensionPixelSize(
            styleable.VerticalTabLayout_tab_paddingStart,
            mTabPaddingStart
        )
        mTabPaddingTop = typedArray.getDimensionPixelSize(
            styleable.VerticalTabLayout_tab_paddingTop,
            mTabPaddingTop
        )
        mTabPaddingEnd = typedArray.getDimensionPixelSize(
            styleable.VerticalTabLayout_tab_paddingEnd,
            mTabPaddingEnd
        )
        mTabPaddingBottom = typedArray.getDimensionPixelSize(
            styleable.VerticalTabLayout_tab_paddingBottom,
            mTabPaddingBottom
        )
        mIndicatorPaddingBottom =
            typedArray.getDimensionPixelSize(styleable.VerticalTabLayout_indicator_padding, 0)
        mIndicatorPaddingTop = mIndicatorPaddingBottom
        mIndicatorPaddingEnd = mIndicatorPaddingTop
        mIndicatorPaddingStart = mIndicatorPaddingEnd
        mIndicatorPaddingStart = typedArray.getDimensionPixelSize(
            styleable.VerticalTabLayout_indicator_paddingStart,
            mIndicatorPaddingStart
        )
        mIndicatorPaddingEnd = typedArray.getDimensionPixelSize(
            styleable.VerticalTabLayout_indicator_paddingEnd,
            mIndicatorPaddingEnd
        )
        mIndicatorPaddingTop = typedArray.getDimensionPixelSize(
            styleable.VerticalTabLayout_indicator_paddingTop,
            mIndicatorPaddingTop
        )
        mIndicatorPaddingBottom = typedArray.getDimensionPixelSize(
            styleable.VerticalTabLayout_indicator_paddingBottom,
            mIndicatorPaddingBottom
        )
        val textColor = typedArray.getColor(styleable.VerticalTabLayout_tab_textColor, Color.BLACK)
        val selected =
            typedArray.getColor(styleable.VerticalTabLayout_tab_textSelectedColor, Color.BLACK)
        mTabTextColors = createColorStateList(textColor, selected)
        typedArray.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) removeAllViews()
        initTabStrip()
    }

    private fun initTabStrip() {
        mTabStrip = TabStrip(context)
        addView(mTabStrip, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    val tabCount: Int
        get() = tabs.size
    val selectedTabPosition: Int
        get() = if (mSelectedTab != null) mSelectedTab!!.position else -1

    fun setTabMode(mode: Int) {
        check(!(mode != TAB_MODE_FIXED && mode != TAB_MODE_SCROLLABLE)) { "only support TAB_MODE_FIXED or TAB_MODE_SCROLLABLE" }
        if (mode == mTabMode) return
        mTabMode = mode
        for (i in 0 until mTabStrip!!.childCount) {
            val view = mTabStrip!!.getChildAt(i)
            val params = view.layoutParams as LinearLayout.LayoutParams
            initTabWithMode(params)
            if (i == 0) {
                params.setMargins(0, 0, 0, 0)
            }
            view.layoutParams = params
        }
        mTabStrip!!.invalidate()
        mTabStrip!!.post { mTabStrip!!.updateIndicator() }
    }

    /**
     * [.TAB_MODE_SCROLLABLE]的时候有效
     *
     * @param margin margin
     */
    fun setTabMargin(margin: Int) {
        if (margin == mTabMargin) return
        mTabMargin = margin
        if (mTabMode == TAB_MODE_FIXED) return
        for (i in 0 until mTabStrip!!.childCount) {
            val view = mTabStrip!!.getChildAt(i)
            val params = view.layoutParams as LinearLayout.LayoutParams
            params.setMargins(0, if (i == 0) 0 else mTabMargin, 0, 0)
            view.layoutParams = params
        }
        mTabStrip!!.invalidate()
        mTabStrip!!.post { mTabStrip!!.updateIndicator() }
    }

    /**
     * 只在[.TAB_MODE_SCROLLABLE]的时候有效
     *
     * @param height height
     */
    fun setTabHeight(height: Int) {
        if (height == mTabHeight) return
        mTabHeight = height
        if (mTabMode == TAB_MODE_FIXED) return
        for (i in 0 until mTabStrip!!.childCount) {
            val view = mTabStrip!!.getChildAt(i)
            val params = view.layoutParams as LinearLayout.LayoutParams
            params.height = mTabHeight
            view.layoutParams = params
        }
        mTabStrip!!.invalidate()
        mTabStrip!!.post { mTabStrip!!.updateIndicator() }
    }

    fun setIndicatorColor(color: Int) {
        mColorIndicator = color
        mTabStrip!!.invalidate()
    }

    fun setIndicatorWidth(width: Int) {
        mIndicatorWidth = width
        mTabStrip!!.setIndicatorGravity()
    }

    fun setIndicatorCorners(corners: Int) {
        mIndicatorCorners = corners.toFloat()
        mTabStrip!!.invalidate()
    }

    /**
     * @param gravity only support Gravity.LEFT,Gravity.RIGHT,Gravity.FILL
     */
    fun setIndicatorGravity(gravity: Int) {
        if (gravity == Gravity.LEFT || gravity == Gravity.RIGHT || Gravity.FILL == gravity) {
            mIndicatorGravity = gravity
            mTabStrip!!.setIndicatorGravity()
        } else {
            throw IllegalStateException("only support Gravity.LEFT,Gravity.RIGHT,Gravity.FILL")
        }
    }

    fun addOnTabSelectedListener(listener: OnTabSelectedListener?) {
        if (listener != null && !mTabSelectedListeners.contains(listener)) {
            mTabSelectedListeners.add(listener)
        }
    }

    fun removeOnTabSelectedListener(listener: OnTabSelectedListener?) {
        if (listener != null) {
            mTabSelectedListeners.remove(listener)
        }
    }

    fun newTab(): Tab {
        var tab = sTabPool.acquire()
        if (tab == null) {
            tab = Tab()
        }
        tab.mParent = this
        tab.mView = createTabView(tab)
        return tab
    }

    fun addTab(tab: Tab, selected: Boolean) {
        addTab(tab, tabs.size, selected)
    }

    @JvmOverloads
    fun addTab(tab: Tab, position: Int = tabs.size, selected: Boolean = false) {
        configureTab(tab, position)
        addTabView(tab)
        if (selected) {
            tab.select()
        }
    }

    private fun createTabView(tab: Tab): TabView? {
        var tabView = mTabViewPool?.acquire()
        if (tabView == null) {
            tabView = TabView(context)
        }
        tabView!!.tab = tab
        tabView.isFocusable = true
        return tabView
    }

    private fun configureTab(tab: Tab, position: Int) {
        tab.position = position
        tabs.add(position, tab)
        val count = tabs.size
        for (i in position + 1 until count) {
            tabs[i].position = i
        }
    }

    private fun addTabView(tab: Tab) {
        val tabView = tab.mView
        addTabWithMode(tabView)
        if (mTabStrip!!.indexOfChild(tabView) == 0) {
            tabView!!.isSelected = true
            mSelectedTab = tab
            mTabStrip!!.post { mTabStrip!!.moveIndicator(0f) }
        }
    }

    private fun addTabWithMode(tabView: TabView?) {
        val params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        initTabWithMode(params)
        mTabStrip!!.addView(tabView, params)
    }

    private fun initTabWithMode(params: LinearLayout.LayoutParams) {
        if (mTabMode == TAB_MODE_FIXED) {
            params.height = 0
            params.weight = 1.0f
            params.setMargins(0, 0, 0, 0)
            isFillViewport = true
        } else if (mTabMode == TAB_MODE_SCROLLABLE) {
            params.height = mTabHeight
            params.weight = 0f
            params.setMargins(0, mTabMargin, 0, 0)
            isFillViewport = false
        }
    }

    fun removeTabAt(position: Int) {
        val selectedTabPosition = if (mSelectedTab != null) mSelectedTab!!.position else 0
        removeTabViewAt(position)
        val removedTab = tabs.removeAt(position)
        if (removedTab != null) {
            removedTab.reset()
            sTabPool.release(removedTab)
        }
        val newTabCount = tabs.size
        for (i in position until newTabCount) {
            tabs[i].position = i
        }
        if (selectedTabPosition == position) {
            selectTab(if (tabs.isEmpty()) null else tabs[Math.max(0, position - 1)])
        }
    }

    fun removeAllTabs() {
        for (i in mTabStrip!!.childCount - 1 downTo 0) {
            removeTabViewAt(i)
        }
        val i = tabs.iterator()
        while (i.hasNext()) {
            val tab = i.next()
            i.remove()
            tab.reset()
            sTabPool.release(tab)
        }
        mSelectedTab = null
    }

    private fun removeTabViewAt(position: Int) {
        val view = mTabStrip!!.getChildAt(position) as TabView
        mTabStrip!!.removeViewAt(position)
        if (view != null) {
            view.reset()
            mTabViewPool!!.release(view)
        }
        requestLayout()
    }

    fun getTabAt(index: Int): Tab? {
        return if (index < 0 || index >= tabCount) null else tabs[index]
    }

    @JvmOverloads
    fun selectTab(tab: Tab?, updateIndicator: Boolean = true) {
        val currentTab = mSelectedTab
        if (currentTab == tab) {
            if (currentTab != null && tab != null) {
                dispatchTabReselected(tab)
                scrollToTab(tab.position)
            }
        } else {
            val newPosition = tab?.position ?: TabLayout.Tab.INVALID_POSITION
            setSelectedTabView(newPosition)
            if (updateIndicator) {
                mTabStrip!!.moveIndicatorWithAnimator(newPosition)
            }
            currentTab?.let { dispatchTabUnselected(it) }
            mSelectedTab = tab
            if (tab != null) {
                scrollToTab(tab.position)
                dispatchTabSelected(tab)
            }
        }
    }

    private fun setSelectedTabView(position: Int) {
        val tabCount = mTabStrip!!.childCount
        if (position < tabCount) {
            for (i in 0 until tabCount) {
                val child = mTabStrip!!.getChildAt(i)
                child.isSelected = i == position
            }
        }
    }

    private fun dispatchTabSelected(tab: Tab) {
        for (i in mTabSelectedListeners.indices) {
            mTabSelectedListeners[i].onTabSelected(tab)
        }
    }

    private fun dispatchTabUnselected(tab: Tab) {
        for (i in mTabSelectedListeners.indices) {
            mTabSelectedListeners[i].onTabUnselected(tab)
        }
    }

    private fun dispatchTabReselected(tab: Tab) {
        for (i in mTabSelectedListeners.indices) {
            mTabSelectedListeners[i].onTabReselected(tab)
        }
    }

    fun setupWithViewPager(viewPager: ViewPager?) {
        setupWithViewPager(viewPager, null)
    }

    fun setupWithViewPager(viewPager: ViewPager?, creator: ViewPagerTabItemCreator?) {
        tabItemCreator = creator
        if (mViewPager != null && mTabPageChangeListener != null) {
            mViewPager!!.removeOnPageChangeListener(mTabPageChangeListener!!)
        }
        if (viewPager != null) {
            val adapter = viewPager.adapter
                ?: throw IllegalArgumentException("ViewPager does not have a PagerAdapter set")
            mViewPager = viewPager
            if (mTabPageChangeListener == null) {
                mTabPageChangeListener = OnTabPageChangeListener(this)
            }
            viewPager.addOnPageChangeListener(mTabPageChangeListener!!)
            if (currentVpSelectedListener == null) {
                currentVpSelectedListener = ViewPagerOnVerticalTabSelectedListener(viewPager)
            }
            addOnTabSelectedListener(currentVpSelectedListener)
            setPagerAdapter(adapter)
        } else {
            mViewPager = null
            setPagerAdapter(null)
        }
        populateFromPagerAdapter(creator)
    }

    private fun setPagerAdapter(adapter: PagerAdapter?) {
        if (mPagerAdapter != null && mPagerAdapterObserver != null) {
            mPagerAdapter!!.unregisterDataSetObserver(mPagerAdapterObserver!!)
        }
        mPagerAdapter = adapter
        if (adapter != null) {
            if (mPagerAdapterObserver == null) {
                mPagerAdapterObserver = PagerAdapterObserver()
            }
            adapter.registerDataSetObserver(mPagerAdapterObserver!!)
        }
    }

    private fun populateFromPagerAdapter(creator: ViewPagerTabItemCreator?) {
        removeAllTabs()
        if (mPagerAdapter != null) {
            val adapterCount = mPagerAdapter!!.count
            for (i in 0 until adapterCount) {
                val title =
                    if (mPagerAdapter!!.getPageTitle(i) == null) "tab$i" else mPagerAdapter!!.getPageTitle(
                        i
                    ).toString()
                if (creator != null) {
                    addTab(creator.create(i), false)
                } else {
                    addTab(newTab().setText(title), false)
                }
            }
            if (mViewPager != null && adapterCount > 0) {
                val curItem = mViewPager!!.currentItem
                if (curItem != selectedTabPosition && curItem < tabCount) {
                    selectTab(getTabAt(curItem))
                }
            }
        }
    }

    private fun scrollToTab(position: Int) {
        val tabView = getTabAt(position)!!.mView
        val y = scrollY
        val tabTop = tabView!!.top + tabView.height / 2 - y
        val target = height / 2
        if (tabTop > target) {
            smoothScrollBy(0, tabTop - target)
        } else if (tabTop < target) {
            smoothScrollBy(0, tabTop - target)
        }
    }

    fun setScrollPosition(position: Int, positionOffset: Float) {
        mTabStrip!!.moveIndicator(positionOffset + position)
    }

    private fun dpToPx(dps: Int): Int {
        return Math.round(resources.displayMetrics.density * dps)
    }

    interface OnTabSelectedListener {
        fun onTabSelected(tab: Tab)
        fun onTabUnselected(tab: Tab)
        fun onTabReselected(tab: Tab)
    }

    interface OnCustomTabViewRenderListener {
        /**
         * 当需要tab的item需要自定义view的时候，通过这个接口方法通知视图渲染
         *
         * @param tab
         */
        fun onRender(tab: Tab?)
    }

    /**
     * 绑定viewpager的时候，构造tabItem的构造器
     */
    interface ViewPagerTabItemCreator {
        fun create(position: Int): Tab
    }

    /**
     * modify from [TabLayout.Tab]
     *
     *
     * [VerticalTabLayout]的子单元
     * 通过[VerticalTabLayout.addTab]添加item
     * 通过[VerticalTabLayout.newTab]构建实例
     */
    class Tab internal constructor() {
        var mParent: VerticalTabLayout? = null
        var mView: TabView? = null
        var tag: Any? = null
            private set
        var icon: Drawable? = null
            private set
        var text: CharSequence? = null
            private set
        var contentDescription: CharSequence? = null
            private set
        var position = INVALID_POSITION
        var customView: View? = null
            private set
        var renderListener: OnCustomTabViewRenderListener? = null

        fun setTag(tag: Any?): Tab {
            this.tag = tag
            return this
        }

        fun setCustomView(view: View?, listener: OnCustomTabViewRenderListener?): Tab {
            customView = view
            renderListener = listener
            updateView()
            return this
        }

        fun setCustomView(@LayoutRes resId: Int, listener: OnCustomTabViewRenderListener?): Tab {
            val inflater = LayoutInflater.from(mView!!.context)
            return setCustomView(inflater.inflate(resId, mView, false), listener)
        }

        fun setIcon(icon: Drawable?): Tab {
            this.icon = icon
            updateView()
            return this
        }

        fun setIcon(@DrawableRes resId: Int): Tab {
            requireNotNull(mParent) { "Tab not attached to a TabLayout" }
            return setIcon(AppCompatResources.getDrawable(mParent!!.context, resId))
        }

        fun setText(text: CharSequence?): Tab {
            this.text = text
            updateView()
            return this
        }

        fun setText(@StringRes resId: Int): Tab {
            requireNotNull(mParent) { "Tab not attached to a TabLayout" }
            return setText(mParent!!.resources.getText(resId))
        }

        fun select() {
            requireNotNull(mParent) { "Tab not attached to a TabLayout" }
            mParent!!.selectTab(this)
        }

        val isSelected: Boolean
            get() {
                requireNotNull(mParent) { "Tab not attached to a TabLayout" }
                return mParent!!.selectedTabPosition == position
            }

        fun setContentDescription(@StringRes resId: Int): Tab {
            requireNotNull(mParent) { "Tab not attached to a TabLayout" }
            return setContentDescription(mParent!!.resources.getText(resId))
        }

        fun setContentDescription(contentDesc: CharSequence?): Tab {
            contentDescription = contentDesc
            updateView()
            return this
        }

        fun updateView() {
            if (mView != null) {
                mView!!.update()
                // 如果view是自定义的view，通过接口将渲染事件传递出去
                if (renderListener != null) {
                    renderListener!!.onRender(this)
                }
            }
        }

        fun reset() {
            mParent = null
            mView = null
            tag = null
            icon = null
            text = null
            contentDescription = null
            position = INVALID_POSITION
            customView = null
            renderListener = null
        }

        companion object {
            const val INVALID_POSITION = -1
        }
    }

    /**
     * [ViewPager]和[VerticalTabLayout]的联动
     * 监听[ViewPager]的变化，更新[VerticalTabLayout]
     */
    private class OnTabPageChangeListener(tabLayout: VerticalTabLayout) : OnPageChangeListener {
        private val mTabLayoutRef: WeakReference<VerticalTabLayout>
        var mUpdateIndicator = false
        private var mPreviousScrollState = 0
        private var mScrollState = 0

        init {
            mTabLayoutRef = WeakReference(tabLayout)
        }

        override fun onPageScrollStateChanged(state: Int) {
            mPreviousScrollState = mScrollState
            mScrollState = state
            mUpdateIndicator =
                !(mScrollState == ViewPager.SCROLL_STATE_SETTLING && mPreviousScrollState == ViewPager.SCROLL_STATE_IDLE)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            val tabLayout = mTabLayoutRef.get()
            if (mUpdateIndicator && tabLayout != null) {
                tabLayout.setScrollPosition(position, positionOffset)
            }
        }

        override fun onPageSelected(position: Int) {
            val tabLayout = mTabLayoutRef.get()
            if (tabLayout != null && tabLayout.selectedTabPosition != position && position < tabLayout.tabCount) {
                tabLayout.selectTab(tabLayout.getTabAt(position), !mUpdateIndicator)
            }
        }
    }

    /**
     * [ViewPager]和[VerticalTabLayout]的联动
     * 监听[VerticalTabLayout]的变化，更新[ViewPager]
     */
    class ViewPagerOnVerticalTabSelectedListener(viewPager: ViewPager) : OnTabSelectedListener {
        private val viewPagerRef: WeakReference<ViewPager>

        init {
            viewPagerRef = WeakReference(viewPager)
        }

        override fun onTabSelected(tab: Tab) {
            val viewPager = viewPagerRef.get()
            if (viewPager != null && viewPager.adapter!!.count >= tab.position) {
                viewPager.currentItem = tab.position
            }
        }

        override fun onTabUnselected(tab: Tab) {}
        override fun onTabReselected(tab: Tab) {}
    }

    private inner class TabStrip(context: Context?) : LinearLayout(context) {
        private var mIndicatorTopY = 0f
        private var mIndicatorX = 0f
        private var mIndicatorBottomY = 0f
        private var mLastWidth = 0
        private val mIndicatorPaint: Paint
        private val mIndicatorRect: RectF
        private var mIndicatorAnimatorSet: AnimatorSet? = null

        init {
            setWillNotDraw(false)
            orientation = VERTICAL
            mIndicatorPaint = Paint()
            mIndicatorPaint.isAntiAlias = true
            mIndicatorGravity = if (mIndicatorGravity == 0) Gravity.LEFT else mIndicatorGravity
            mIndicatorRect = RectF()
            setIndicatorGravity()
        }

        fun setIndicatorGravity() {
            if (mIndicatorGravity == Gravity.LEFT) {
                mIndicatorX = 0f
                if (mLastWidth != 0) mIndicatorWidth = mLastWidth
                setPadding(mIndicatorWidth, 0, 0, 0)
            } else if (mIndicatorGravity == Gravity.RIGHT) {
                if (mLastWidth != 0) mIndicatorWidth = mLastWidth
                setPadding(0, 0, mIndicatorWidth, 0)
            } else if (mIndicatorGravity == Gravity.FILL) {
                mIndicatorX = 0f
                setPadding(0, 0, 0, 0)
            }
            post {
                if (mIndicatorGravity == Gravity.RIGHT) {
                    mIndicatorX = (width - mIndicatorWidth).toFloat()
                } else if (mIndicatorGravity == Gravity.FILL) {
                    mLastWidth = mIndicatorWidth
                    mIndicatorWidth = width
                }
                invalidate()
            }
        }

        private fun calcIndicatorY(offset: Float) {
            val index = Math.floor(offset.toDouble()).toInt()
            val childView = getChildAt(index)
            if (Math.floor(offset.toDouble()) != (childCount - 1).toDouble() && Math.ceil(offset.toDouble()) != 0.0) {
                val nextView = getChildAt(index + 1)
                mIndicatorTopY = childView.top + (nextView.top - childView.top) * (offset - index)
                mIndicatorBottomY =
                    childView.bottom + (nextView.bottom - childView.bottom) * (offset - index)
            } else {
                mIndicatorTopY = childView.top.toFloat()
                mIndicatorBottomY = childView.bottom.toFloat()
            }
        }

        fun updateIndicator() {
            moveIndicatorWithAnimator(selectedTabPosition)
        }

        fun moveIndicator(offset: Float) {
            calcIndicatorY(offset)
            invalidate()
        }

        /**
         * move indicator to a tab location
         *
         * @param index tab location's index
         */
        fun moveIndicatorWithAnimator(index: Int) {
            val direction: Int = index - selectedTabPosition
            val childView = getChildAt(index)
            val targetTop = childView.top.toFloat()
            val targetBottom = childView.bottom.toFloat()
            if (mIndicatorTopY == targetTop && mIndicatorBottomY == targetBottom) return
            if (mIndicatorAnimatorSet != null && mIndicatorAnimatorSet!!.isRunning) {
                mIndicatorAnimatorSet!!.end()
            }
            post {
                var startAnimate: ValueAnimator? = null
                var endAnimate: ValueAnimator? = null
                if (direction > 0) {
                    startAnimate = ValueAnimator.ofFloat(mIndicatorBottomY, targetBottom)
                        .setDuration(mIndicatorAnimDuration.toLong())
                    startAnimate.addUpdateListener(AnimatorUpdateListener { animation ->
                        mIndicatorBottomY = animation.animatedValue.toString().toFloat()
                        invalidate()
                    })
                    endAnimate = ValueAnimator.ofFloat(mIndicatorTopY, targetTop)
                        .setDuration(mIndicatorAnimDuration.toLong())
                    endAnimate.addUpdateListener(AnimatorUpdateListener { animation ->
                        mIndicatorTopY = animation.animatedValue.toString().toFloat()
                        invalidate()
                    })
                } else if (direction < 0) {
                    startAnimate = ValueAnimator.ofFloat(mIndicatorTopY, targetTop)
                        .setDuration(mIndicatorAnimDuration.toLong())
                    startAnimate.addUpdateListener(AnimatorUpdateListener { animation ->
                        mIndicatorTopY = animation.animatedValue.toString().toFloat()
                        invalidate()
                    })
                    endAnimate = ValueAnimator.ofFloat(mIndicatorBottomY, targetBottom)
                        .setDuration(mIndicatorAnimDuration.toLong())
                    endAnimate.addUpdateListener(AnimatorUpdateListener { animation ->
                        mIndicatorBottomY = animation.animatedValue.toString().toFloat()
                        invalidate()
                    })
                }
                if (startAnimate != null) {
                    mIndicatorAnimatorSet = AnimatorSet()
                    mIndicatorAnimatorSet!!.play(endAnimate).after(startAnimate)
                    mIndicatorAnimatorSet!!.start()
                }
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            mIndicatorPaint.color = mColorIndicator
            mIndicatorRect.left = mIndicatorX + mIndicatorPaddingStart
            mIndicatorRect.top = mIndicatorTopY + mIndicatorPaddingTop
            mIndicatorRect.right = mIndicatorX + mIndicatorWidth - mIndicatorPaddingEnd
            mIndicatorRect.bottom = mIndicatorBottomY - mIndicatorPaddingBottom
            if (mIndicatorCorners != 0f) {
                canvas.drawRoundRect(
                    mIndicatorRect,
                    mIndicatorCorners,
                    mIndicatorCorners,
                    mIndicatorPaint
                )
            } else {
                canvas.drawRect(mIndicatorRect, mIndicatorPaint)
            }
        }
    }

    /**
     * modify from [TabLayout.TabView]
     *
     *
     * tab的视图，由一个简单的[ImageView]+ [TextView] 组成
     * 如果需要复杂的视图效果可以通过[Tab.setCustomView]设置自定义的view
     */
    inner class TabView(context: Context?) : LinearLayout(context) {
        private var mTab: Tab? = null
        private var mTextView: TextView? = null
        private var mIconView: ImageView? = null
        private var mCustomView: View? = null

        init {
            ViewCompat.setPaddingRelative(
                this,
                mTabPaddingStart,
                mTabPaddingTop,
                mTabPaddingEnd,
                mTabPaddingBottom
            )
            gravity = Gravity.CENTER
            orientation = VERTICAL
            isClickable = true
            ViewCompat.setPointerIcon(
                this,
                PointerIconCompat.getSystemIcon(getContext(), PointerIconCompat.TYPE_HAND)
            )
        }

        override fun performClick(): Boolean {
            val handled = super.performClick()
            return if (mTab != null) {
                if (!handled) {
                    playSoundEffect(SoundEffectConstants.CLICK)
                }
                mTab!!.select()
                true
            } else {
                handled
            }
        }

        override fun setSelected(selected: Boolean) {
            val changed = isSelected != selected
            super.setSelected(selected)
            if (changed && selected && VERSION.SDK_INT < 16) {
                // Pre-JB we need to manually send the TYPE_VIEW_SELECTED event
                sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
            }
            if (mTextView != null) {
                mTextView!!.isSelected = selected
            }
            if (mIconView != null) {
                mIconView!!.isSelected = selected
            }
            if (mCustomView != null) {
                mCustomView!!.isSelected = selected
            }
        }

        fun reset() {
            tab = null
            isSelected = false
        }

        fun update() {
            val tab = mTab
            val custom = tab?.customView
            if (custom != null) {
                val customParent = custom.parent
                if (customParent !== this) {
                    if (customParent != null) {
                        (customParent as ViewGroup).removeView(custom)
                    }
                    addView(custom)
                }
                mCustomView = custom
                if (mTextView != null) {
                    mTextView!!.visibility = GONE
                }
                if (mIconView != null) {
                    mIconView!!.visibility = GONE
                    mIconView!!.setImageDrawable(null)
                }
            } else {
                // We do not have a custom view. Remove one if it already exists
                if (mCustomView != null) {
                    removeView(mCustomView)
                    mCustomView = null
                }
            }
            if (mCustomView == null) {
                // If there isn't a custom view, we'll us our own in-built layouts
                if (mIconView == null) {
                    val iconView = LayoutInflater.from(context)
                        .inflate(layout.design_layout_tab_icon, this, false) as ImageView
                    addView(iconView, 0)
                    mIconView = iconView
                }
                if (mTextView == null) {
                    val textView = LayoutInflater.from(context)
                        .inflate(layout.design_layout_tab_text, this, false) as TextView
                    addView(textView)
                    mTextView = textView
                }
                if (mTabTextColors != null) {
                    mTextView!!.setTextColor(mTabTextColors)
                }
                updateTextAndIcon(mTextView, mIconView)
            } else {
                if (tab!!.renderListener != null) {
                    tab.renderListener!!.onRender(tab)
                }
            }
            isSelected = tab != null && tab.isSelected
        }

        private fun updateTextAndIcon(textView: TextView?, iconView: ImageView?) {
            val icon = if (mTab != null) mTab!!.icon else null
            val text = if (mTab != null) mTab!!.text else null
            val contentDesc = if (mTab != null) mTab!!.contentDescription else null
            if (iconView != null) {
                if (icon != null) {
                    iconView.setImageDrawable(icon)
                    iconView.visibility = VISIBLE
                    visibility = VISIBLE
                } else {
                    iconView.visibility = GONE
                    iconView.setImageDrawable(null)
                }
                iconView.contentDescription = contentDesc
            }
            val hasText = !TextUtils.isEmpty(text)
            if (textView != null) {
                if (hasText) {
                    textView.text = text
                    textView.visibility = VISIBLE
                    visibility = VISIBLE
                } else {
                    textView.visibility = GONE
                    textView.text = null
                }
                textView.contentDescription = contentDesc
            }
            if (iconView != null) {
                val lp = iconView.layoutParams as MarginLayoutParams
                var bottomMargin = 0
                if (hasText && iconView.visibility == VISIBLE) {
                    // If we're showing both text and icon, add some margin bottom to the icon
                    bottomMargin = dpToPx(DEFAULT_GAP_TEXT_ICON)
                }
                if (bottomMargin != lp.bottomMargin) {
                    lp.bottomMargin = bottomMargin
                    iconView.requestLayout()
                }
            }
            TooltipCompat.setTooltipText(this, if (hasText) null else contentDesc)
        }

        var tab: Tab?
            get() = mTab
            set(tab) {
                if (mTab != tab) {
                    mTab = tab
                    update()
                }
            }
    }

    /**
     * 监听[ViewPager]的数据源[PagerAdapter]的变化
     */
    private inner class PagerAdapterObserver : DataSetObserver() {
        override fun onChanged() {
            populateFromPagerAdapter(tabItemCreator)
        }

        override fun onInvalidated() {
            populateFromPagerAdapter(tabItemCreator)
        }
    }

    companion object {
        private const val DEFAULT_GAP_TEXT_ICON = 8 // dps
        private const val INDICATOR_GRAVITY_LEFT = 100
        private const val INDICATOR_GRAVITY_RIGHT = 101
        private const val INDICATOR_GRAVITY_FILL = 102
        private val sTabPool: Pool<Tab> = SynchronizedPool(16)
        var TAB_MODE_FIXED = 10
        var TAB_MODE_SCROLLABLE = 11
        private fun createColorStateList(defaultColor: Int, selectedColor: Int): ColorStateList {
            val states = arrayOfNulls<IntArray>(2)
            val colors = IntArray(2)
            var i = 0
            states[i] = SELECTED_STATE_SET
            colors[i] = selectedColor
            i++

            // Default enabled state
            states[i] = EMPTY_STATE_SET
            colors[i] = defaultColor
            i++
            return ColorStateList(states, colors)
        }
    }
}