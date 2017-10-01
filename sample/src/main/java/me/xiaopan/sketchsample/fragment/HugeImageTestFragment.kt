package me.xiaopan.sketchsample.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import me.xiaopan.assemblyadapter.FragmentArrayPagerAdapter
import me.xiaopan.psts.PagerSlidingTabStrip
import me.xiaopan.sketchsample.AssetImage
import me.xiaopan.sketchsample.BaseFragment
import me.xiaopan.sketchsample.BindContentView
import me.xiaopan.sketchsample.R
import me.xiaopan.sketchsample.activity.MainActivity
import me.xiaopan.sketchsample.bean.Image
import me.xiaopan.ssvt.bindView

/**
 * 大图页面，用来展示Sketch显示大图的能力
 */
@BindContentView(R.layout.fragment_pager_tab)
class HugeImageTestFragment : BaseFragment() {
    val tabStrip: PagerSlidingTabStrip by bindView(R.id.tab_pagerTabFragment_tabs)
    val viewPager: ViewPager by bindView(R.id.pager_pagerTabFragment_content)

    private var fragmentAdapter: FragmentArrayPagerAdapter? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (fragmentAdapter == null) {
            val hugeAssetImageNames = AssetImage.HUGE_IMAGES
            val fragments = arrayOfNulls<Fragment>(hugeAssetImageNames.size)
            for (w in hugeAssetImageNames.indices) {
                val url = hugeAssetImageNames[w]
                fragments[w] = ImageFragment.build(Image(url, url), null, true)
            }
            fragmentAdapter = FragmentArrayPagerAdapter(childFragmentManager, fragments)
        }
        viewPager.adapter = fragmentAdapter

        tabStrip.setTabViewFactory(MainActivity.TitleTabFactory(arrayOf("WORLD", "QMSHT", "CWB", "CARD"), activity))
        tabStrip.setViewPager(viewPager)
    }
}
