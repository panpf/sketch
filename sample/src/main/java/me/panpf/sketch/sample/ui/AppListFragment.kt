package me.panpf.sketch.sample.ui

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import kotlinx.android.synthetic.main.fragment_pager.*
import me.panpf.adapter.pager.FragmentArrayPagerAdapter
import me.panpf.pagerid.PagerIndicator
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R

/**
 * App列表页面，用来展示已安装APP和本地APK列表
 */
@BindContentView(R.layout.fragment_pager)
class AppListFragment : BaseFragment() {

    var getPagerSlidingTagStripListener: GetAppListTagStripListener? = null
    var fragmentAdapter: FragmentArrayPagerAdapter? = null

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)

        if (activity is GetAppListTagStripListener) {
            getPagerSlidingTagStripListener = activity
        } else {
            getPagerSlidingTagStripListener = null
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (getPagerSlidingTagStripListener != null) {
            getPagerSlidingTagStripListener = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (fragmentAdapter == null) {
            val fragments = arrayOfNulls<Fragment>(2)
            fragments[0] = InstalledAppFragment()
            fragments[1] = AppPackageListFragment()
            fragmentAdapter = FragmentArrayPagerAdapter(childFragmentManager, fragments)
        }
        pager_pagerFragment_content.adapter = fragmentAdapter
        getPagerSlidingTagStripListener!!.onGetAppListTabStrip().setViewPager(pager_pagerFragment_content)
    }

    interface GetAppListTagStripListener {
        fun onGetAppListTabStrip(): PagerIndicator
    }
}
