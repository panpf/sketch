package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import kotlinx.android.synthetic.main.fragment_pager_tab.*
import me.panpf.adapter.pager.FragmentArrayPagerAdapter
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView

@BindContentView(R.layout.fragment_pager_tab)
class ImageShaperTestFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity ?: return
        pager_pagerTabFragment_content.adapter = FragmentArrayPagerAdapter(childFragmentManager, arrayOf<Fragment>(
                RoundRectImageShaperTestFragment(),
                CircleImageShaperTestFragment(),
                ShapeSizeImageShaperTestFragment()))

        tab_pagerTabFragment_tabs.setTabViewFactory(TitleTabFactory(arrayOf(
                "ROUND_RECT",
                "CIRCLE",
                "SHAPE_SIZE"), activity))
        tab_pagerTabFragment_tabs.setViewPager(pager_pagerTabFragment_content)
    }
}