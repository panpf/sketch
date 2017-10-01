package me.xiaopan.sketchsample.fragment

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.Formatter
import android.view.View
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter
import me.xiaopan.sketchsample.BaseFragment
import me.xiaopan.sketchsample.BindContentView
import me.xiaopan.sketchsample.R
import me.xiaopan.sketchsample.adapter.itemfactory.AppItemFactory
import me.xiaopan.sketchsample.adapter.itemfactory.AppListHeaderItemFactory
import me.xiaopan.sketchsample.bean.AppInfo
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager
import me.xiaopan.sketchsample.widget.HintView
import me.xiaopan.ssvt.bindView
import net.sourceforge.pinyin4j.PinyinHelper
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

/**
 * 已安装APP列表
 */
@BindContentView(R.layout.fragment_recycler)
class InstalledAppFragment : BaseFragment(), AppItemFactory.AppItemListener {
    val recyclerView: RecyclerView by bindView(R.id.recycler_recyclerFragment_content)
    val hintView: HintView by bindView(R.id.hint_recyclerFragment)
    val refreshLayout: SwipeRefreshLayout by bindView(R.id.refresh_recyclerFragment)

    private val adapter: AssemblyRecyclerAdapter? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(view!!.context)
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

            val packageManager = fragment.context.packageManager
            val packageInfoList = packageManager.getInstalledPackages(0)
            val appInfoList = ArrayList<AppInfo>(packageInfoList.size)
            for (packageInfo in packageInfoList) {
                val appInfo = AppInfo(true)
                appInfo.name = packageInfo.applicationInfo.loadLabel(packageManager).toString()
                appInfo.packageName = packageInfo.packageName
                appInfo.sortName = toPinYin(appInfo.name)
                appInfo.id = packageInfo.packageName
                appInfo.versionName = packageInfo.versionName
                appInfo.apkFilePath = packageInfo.applicationInfo.publicSourceDir
                appInfo.formattedAppSize = Formatter.formatFileSize(fragment.context, File(appInfo.apkFilePath).length())
                appInfo.versionCode = packageInfo.versionCode
                appInfoList.add(appInfo)
            }

            Collections.sort(appInfoList) { lhs, rhs -> lhs.sortName.compareTo(rhs.sortName, ignoreCase = true) }

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
