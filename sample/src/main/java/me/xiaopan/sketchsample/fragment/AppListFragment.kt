package me.xiaopan.sketchsample.fragment

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import me.xiaopan.assemblyadapter.FragmentArrayPagerAdapter
import me.xiaopan.psts.PagerSlidingTabStrip
import me.xiaopan.sketchsample.BaseFragment
import me.xiaopan.sketchsample.BindContentView
import me.xiaopan.sketchsample.R
import me.xiaopan.ssvt.bindView

/**
 * App列表页面，用来展示已安装APP和本地APK列表
 */
@BindContentView(R.layout.fragment_pager)
class AppListFragment : BaseFragment() {
    val viewPager: ViewPager by bindView(R.id.pager_pagerFragment_content)

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

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (fragmentAdapter == null) {
            val fragments = arrayOfNulls<Fragment>(2)
            fragments[0] = InstalledAppFragment()
            fragments[1] = AppPackageListFragment()
            fragmentAdapter = FragmentArrayPagerAdapter(childFragmentManager, fragments)
        }
        viewPager.adapter = fragmentAdapter
        getPagerSlidingTagStripListener!!.onGetAppListTabStrip().setViewPager(viewPager)
    }

    interface GetAppListTagStripListener {
        fun onGetAppListTabStrip(): PagerSlidingTabStrip
    }
}
