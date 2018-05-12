package me.panpf.sketch.sample.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import kotlinx.android.synthetic.main.fragment_pager_tab.*
import me.panpf.adapter.pager.FragmentArrayPagerAdapter
import me.panpf.pagerid.PagerIndicator
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.activity.MainActivity

@BindContentView(R.layout.fragment_pager_tab)
class ImageShaperTestFragment : BaseFragment() {
    val tabStrip: PagerIndicator by lazy {tab_pagerTabFragment_tabs}
    val viewPager: ViewPager by lazy {pager_pagerTabFragment_content}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity ?: return
        viewPager.adapter = FragmentArrayPagerAdapter(childFragmentManager, arrayOf<Fragment>(
                RoundRectImageShaperTestFragment(),
                CircleImageShaperTestFragment(),
                ShapeSizeImageShaperTestFragment()))

        tabStrip.setTabViewFactory(MainActivity.TitleTabFactory(arrayOf(
                "ROUND_RECT",
                "CIRCLE",
                "SHAPE_SIZE"), activity))
        tabStrip.setViewPager(viewPager)
    }
}