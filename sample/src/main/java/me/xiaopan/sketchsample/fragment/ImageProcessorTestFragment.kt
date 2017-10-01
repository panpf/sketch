package me.xiaopan.sketchsample.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import me.xiaopan.assemblyadapter.FragmentArrayPagerAdapter
import me.xiaopan.psts.PagerSlidingTabStrip
import me.xiaopan.sketchsample.BaseFragment
import me.xiaopan.sketchsample.BindContentView
import me.xiaopan.sketchsample.R
import me.xiaopan.sketchsample.activity.MainActivity
import me.xiaopan.ssvt.bindView

@BindContentView(R.layout.fragment_pager_tab)
class ImageProcessorTestFragment : BaseFragment() {
    val tabStrip: PagerSlidingTabStrip by bindView(R.id.tab_pagerTabFragment_tabs)
    val viewPager: ViewPager by bindView(R.id.pager_pagerTabFragment_content)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager.adapter = FragmentArrayPagerAdapter(childFragmentManager, arrayOf<Fragment>(
                ReflectionImageProcessorTestFragment(),
                GaussianBlurImageProcessorTestFragment(),
                RotateImageProcessorTestFragment(),
                RoundRectImageProcessorTestFragment(),
                CircleImageProcessorTestFragment(),
                ResizeImageProcessorTestFragment(),
                MaskImageProcessorTestFragment(),
                WrappedImageProcessorTestFragment()))

        tabStrip.setTabViewFactory(MainActivity.TitleTabFactory(arrayOf(
                "REFLECTION",
                "GAUSSIAN_BLUR",
                "ROTATE",
                "ROUND_RECT",
                "CIRCLE",
                "RESIZE",
                "MASK",
                "WRAPPED"), activity))
        tabStrip.setViewPager(viewPager)
    }
}
