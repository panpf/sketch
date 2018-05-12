package me.panpf.sketch.sample.fragment

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.Formatter
import android.view.View
import kotlinx.android.synthetic.main.fragment_recycler.*
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.adapter.itemfactory.AppItemFactory
import me.panpf.sketch.sample.adapter.itemfactory.AppListHeaderItemFactory
import me.panpf.sketch.sample.bean.AppInfo
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import me.panpf.sketch.sample.widget.HintView
import net.sourceforge.pinyin4j.PinyinHelper
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

/**
 * 已安装APP列表
 */
@BindContentView(R.layout.fragment_recycler)
class InstalledAppFragment : BaseFragment(), AppItemFactory.AppItemListener {
    val recyclerView: RecyclerView by lazy {recycler_recyclerFragment_content}
    val hintView: HintView by lazy {hint_recyclerFragment}
    val refreshLayout: SwipeRefreshLayout by lazy {refresh_recyclerFragment}

    private val adapter: AssemblyRecyclerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.addOnScrollListener(ScrollingPauseLoadManager(view.context))

        refreshLayout.isEnabled = false

        if (adapter != null) {
            recyclerView.adapter = adapter
            recyclerView.scheduleLayoutAnimation()
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

    class LoadAppsTask(val fragmentWeakReference: WeakReference<InstalledAppFragment>) : AsyncTask<Int, Int, List<AppInfo>?>() {

        override fun onPreExecute() {
            super.onPreExecute()

            fragmentWeakReference.get()?.hintView?.loading(null)
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

            Collections.sort(appInfoList) { lhs, rhs -> (lhs.sortName ?: "").compareTo((rhs.sortName ?: ""))}

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

            fragment.hintView.hidden()

            val dataList = ArrayList<Any>((appInfoList?.size ?: 0) + 1)
            dataList.add(String.format("您的设备上共安装了%d款应用", appInfoList?.size ?: 0))
            if (appInfoList != null) {
                dataList.addAll(appInfoList)
            }
            val adapter = AssemblyRecyclerAdapter(dataList)
            adapter.addItemFactory(AppItemFactory(fragment))
            adapter.addItemFactory(AppListHeaderItemFactory())
            fragment.recyclerView.adapter = adapter
            fragment.recyclerView.scheduleLayoutAnimation()
        }
    }
}
