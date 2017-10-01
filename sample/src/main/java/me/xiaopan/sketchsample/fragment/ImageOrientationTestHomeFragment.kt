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
import me.xiaopan.sketchsample.util.ImageOrientationCorrectTestFileGenerator
import me.xiaopan.ssvt.bindView

@BindContentView(R.layout.fragment_pager_tab)
class ImageOrientationTestHomeFragment : BaseFragment() {
    val tabStrip: PagerSlidingTabStrip by bindView(R.id.tab_pagerTabFragment_tabs)
    val viewPager: ViewPager by bindView(R.id.pager_pagerTabFragment_content)

    private var fragmentAdapter: FragmentArrayPagerAdapter? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (fragmentAdapter == null) {
            val filePaths = ImageOrientationCorrectTestFileGenerator.getInstance(context).filePaths
            val fragments = arrayOfNulls<Fragment>(filePaths.size)
            for (w in filePaths.indices) {
                fragments[w] = ImageOrientationTestFragment.build(filePaths[w])
            }
            fragmentAdapter = FragmentArrayPagerAdapter(childFragmentManager, fragments)
        }
        viewPager.adapter = fragmentAdapter

        tabStrip.setTabViewFactory(MainActivity.TitleTabFactory(arrayOf("ROTATE_90", "ROTATE_180", "ROTATE_270", "FLIP_HORIZONTAL", "TRANSPOSE", "FLIP_VERTICAL", "TRANSVERSE"), activity))
        tabStrip.setViewPager(viewPager)
    }
}
