package me.panpf.sketch.sample.ui

import android.os.AsyncTask
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.format.Formatter
import android.view.View
import kotlinx.android.synthetic.main.fragment_recycler.*
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.item.AppItemFactory
import me.panpf.sketch.sample.item.AppListHeaderItemFactory
import me.panpf.sketch.sample.bean.AppInfo
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import net.sourceforge.pinyin4j.PinyinHelper
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

/**
 * 已安装APP列表
 */
@BindContentView(R.layout.fragment_recycler)
class InstalledAppFragment : BaseFragment(), AppItemFactory.AppItemListener {

    private val adapter: AssemblyRecyclerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_recyclerFragment_content.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(view.context)
        recycler_recyclerFragment_content.addOnScrollListener(ScrollingPauseLoadManager(view.context))

        refresh_recyclerFragment.isEnabled = false

        if (adapter != null) {
            recycler_recyclerFragment_content.adapter = adapter
            recycler_recyclerFragment_content.scheduleLayoutAnimation()
        } else {
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

    class LoadAppsTask(private val fragmentWeakReference: WeakReference<InstalledAppFragment>) : AsyncTask<Int, Int, List<AppInfo>?>() {

        override fun onPreExecute() {
            super.onPreExecute()

            fragmentWeakReference.get()?.hint_recyclerFragment?.loading(null)
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
                appInfo.formattedAppSize = Formatter.formatFileSize(fragment.context, File(appInfo.apkFilePath).length())
                appInfo.versionCode = packageInfo.versionCode
                appInfoList.add(appInfo)
            }

            appInfoList.sortWith(Comparator { lhs, rhs -> (lhs.sortName ?: "").compareTo((rhs.sortName ?: ""))})

            return appInfoList
        }

        private fun toPinYin(text: String): String {
            val stringBuilder = StringBuilder()
            for (c in text.toCharArray()) {
                val a = PinyinHelper.toHanyuPinyinStringArray(c)
                if (a != null) {
                    stringBuilder.append(a[0])
                } else {
                    stringBuilder.append(c)
                }
            }
            return stringBuilder.toString()
        }

        override fun onPostExecute(appInfoList: List<AppInfo>?) {
            val fragment = fragmentWeakReference.get() ?: return

            fragment.hint_recyclerFragment.hidden()

            val dataList = ArrayList<Any>((appInfoList?.size ?: 0) + 1)
            dataList.add(String.format("您的设备上共安装了%d款应用", appInfoList?.size ?: 0))
            if (appInfoList != null) {
                dataList.addAll(appInfoList)
            }
            val adapter = AssemblyRecyclerAdapter(dataList)
            adapter.addItemFactory(AppItemFactory(fragment))
            adapter.addItemFactory(AppListHeaderItemFactory())
            fragment.recycler_recyclerFragment_content.adapter = adapter
            fragment.recycler_recyclerFragment_content.scheduleLayoutAnimation()
        }
    }
}
