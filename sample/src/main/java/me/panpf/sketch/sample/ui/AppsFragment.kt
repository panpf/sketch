package me.panpf.sketch.sample.ui

import android.os.AsyncTask
import android.os.Bundle
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.promeg.pinyinhelper.Pinyin
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.bean.AppInfo
import me.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import me.panpf.sketch.sample.item.AppItemFactory
import me.panpf.sketch.sample.item.AppListHeaderItemFactory
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

/**
 * 已安装APP列表
 */
class AppsFragment : BaseBindingFragment<FragmentRecyclerBinding>(),
    AppItemFactory.AppItemListener {

    private val adapter: AssemblyRecyclerAdapter? = null

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRecyclerBinding.inflate(inflater, parent, false)

    override fun onInitData(binding: FragmentRecyclerBinding, savedInstanceState: Bundle?) {
        binding.recyclerRecyclerFragmentContent.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            addOnScrollListener(ScrollingPauseLoadManager(requireContext()))
            if (this@AppsFragment.adapter != null) {
                adapter = this@AppsFragment.adapter
                scheduleLayoutAnimation()
            }
        }

        binding.refreshRecyclerFragment.isEnabled = false

        if (adapter == null) {
            loadAppList()
        }
    }

    private fun loadAppList() {
        LoadAppsTask(WeakReference(this)).execute(0)
    }

    override fun onClickApp(position: Int, appInfo: AppInfo) {
        val context = context ?: return
        val intent = context.packageManager.getLaunchIntentForPackage(appInfo.packageName)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class LoadAppsTask(private val fragmentWeakReference: WeakReference<AppsFragment>) :
        AsyncTask<Int, Int, List<AppInfo>?>() {

        override fun onPreExecute() {
            super.onPreExecute()

            fragmentWeakReference.get()?.binding?.hintRecyclerFragment?.loading(null)
        }

        override fun doInBackground(vararg params: Int?): List<AppInfo>? {
            val fragment = fragmentWeakReference.get() ?: return null
            val context = fragment.context ?: return null

            val packageManager = context.packageManager
            val packageInfoList = packageManager.getInstalledPackages(0)
            val appInfoList = ArrayList<AppInfo>(packageInfoList.size)
            for (packageInfo in packageInfoList) {
                val appInfo = AppInfo(true)
                appInfo.name = packageInfo.applicationInfo.loadLabel(packageManager).toString()
                appInfo.packageName = packageInfo.packageName
                appInfo.sortName = toPinYin(appInfo.name ?: "")
                appInfo.id = packageInfo.packageName
                appInfo.versionName = packageInfo.versionName
                appInfo.apkFilePath = packageInfo.applicationInfo.publicSourceDir
                appInfo.formattedAppSize =
                    Formatter.formatFileSize(fragment.context, File(appInfo.apkFilePath).length())
                appInfo.versionCode = packageInfo.versionCode
                appInfoList.add(appInfo)
            }

            appInfoList.sortWith { lhs, rhs ->
                (lhs.sortName ?: "").compareTo((rhs.sortName ?: ""))
            }

            return appInfoList
        }

        private fun toPinYin(text: String): String {
            return Pinyin.toPinyin(text, "")
        }

        override fun onPostExecute(appInfoList: List<AppInfo>?) {
            val fragment = fragmentWeakReference.get() ?: return

            fragment.binding?.hintRecyclerFragment?.hidden()

            val dataList = ArrayList<Any>((appInfoList?.size ?: 0) + 1)
            dataList.add(String.format("您的设备上共安装了%d款应用", appInfoList?.size ?: 0))
            if (appInfoList != null) {
                dataList.addAll(appInfoList)
            }
            val adapter = AssemblyRecyclerAdapter(dataList)
            adapter.addItemFactory(AppItemFactory(fragment))
            adapter.addItemFactory(AppListHeaderItemFactory())
            fragment.binding?.recyclerRecyclerFragmentContent?.adapter = adapter
            fragment.binding?.recyclerRecyclerFragmentContent?.scheduleLayoutAnimation()
        }
    }
}
