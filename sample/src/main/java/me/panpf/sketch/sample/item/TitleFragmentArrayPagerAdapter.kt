package me.panpf.sketch.sample.item

import android.util.SparseIntArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter

class TitleFragmentArrayPagerAdapter(fm: FragmentManager, private val fragments: Array<Fragment>, private val titles: Array<String>) : FragmentPagerAdapter(fm) {

    private var notifyNumber = 0
    private var notifyNumberPool: SparseIntArray? = null

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    fun isEnabledPositionNoneOnNotifyDataSetChanged(): Boolean {
        return notifyNumberPool != null
    }

    fun setEnabledPositionNoneOnNotifyDataSetChanged(enabled: Boolean) {
        if (enabled) {
            notifyNumberPool = SparseIntArray()
            notifyNumber = 0
        } else {
            notifyNumberPool = null
        }
    }

    override fun notifyDataSetChanged() {
        if (notifyNumberPool != null) notifyNumber++
        super.notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        if (notifyNumberPool != null && notifyNumberPool!!.get(`object`.hashCode()) != notifyNumber) {
            notifyNumberPool!!.put(`object`.hashCode(), notifyNumber)
            return PagerAdapter.POSITION_NONE
        }
        return super.getItemPosition(`object`)
    }
}