package me.panpf.sketch.sample.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.Formatter
import android.view.View
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.adapter.FixedItemInfo
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.adapter.itemfactory.AppItemFactory
import me.panpf.sketch.sample.adapter.itemfactory.AppScanningItemFactory
import me.panpf.sketch.sample.bean.AppInfo
import me.panpf.sketch.sample.bean.AppScanning
import me.panpf.sketch.sample.bindView
import me.panpf.sketch.sample.util.FileScanner
import me.panpf.sketch.sample.util.FileUtils
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import me.panpf.sketch.sample.util.XpkInfo
import me.panpf.sketch.sample.widget.HintView
import me.panpf.sketch.util.SketchUtils
import java.io.File
import java.lang.ref.WeakReference
import java.util.*
import java.util.zip.ZipFile

/**
 * 本地安装包页面
 */
@BindContentView(R.layout.fragment_recycler)
class AppPackageListFragment : BaseFragment(), AppItemFactory.AppItemListener {
    val refreshLayout: SwipeRefreshLayout by bindView(R.id.refresh_recyclerFragment)
    val recyclerView: RecyclerView by bindView(R.id.recycler_recyclerFragment_content)
    val hintView: HintView by bindView(R.id.hint_recyclerFragment)

    private var adapter: AssemblyRecyclerAdapter? = null
    private var fileScanner: FileScanner? = null
    private var scanningItemInfo: FixedItemInfo? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshLayout.isEnabled = false

        recyclerView.layoutManager = LinearLayoutManager(view!!.context)
        recyclerView.addOnScrollListener(ScrollingPauseLoadManager(view.context))

        if (adapter != null) {
            recyclerView.adapter = adapter
            recyclerView.scheduleLayoutAnimation()
        } else {
            loadAppList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fileScanner?.let {
            if (it.isRunning) {
                it.cancel()
            }
        }
    }

    private fun loadAppList() {
        context?.let {
            LoadAppsTask(WeakReference(this)).execute("")
        }
    }

    override fun onClickApp(position: Int, appInfo: AppInfo) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.setDataAndType(Uri.fromFile(File(appInfo.apkFilePath)), "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private class LoadAppsTask(internal var fragmentWeakReference: WeakReference<AppPackageListFragment>) : AsyncTask<String, Int, Array<String>>() {

        override fun onPreExecute() {
            val fragment = fragmentWeakReference.get() ?: return
            val context = fragment.context?.applicationContext ?: return

            fragment.fileScanner = FileScanner(MyFileChecker(context), MyScanListener(fragmentWeakReference))
            fragment.fileScanner!!.setDirFilter(MyDirFilter())

            val adapter = AssemblyRecyclerAdapter(ArrayList<Any>())
            adapter.addItemFactory(AppItemFactory(fragment))
            fragment.scanningItemInfo = adapter.addHeaderItem(AppScanningItemFactory(), AppScanning())

            fragment.recyclerView.adapter = adapter
            fragment.adapter = adapter
        }

        override fun doInBackground(vararg params: String): Array<String>? {
            val fragment = fragmentWeakReference.get()
            if (fragment != null) {
                return SketchUtils.getAllAvailableSdcardPath(fragment.context)
            }
            return null
        }

        override fun onPostExecute(files: Array<String>?) {
            val fragment = fragmentWeakReference.get()
            if (fragment != null) {
                if (files == null || files.isEmpty()) {
                    MyScanListener(fragmentWeakReference).onCompleted()
                } else {
                    fragment.fileScanner!!.execute(files)
                }
            }
        }
    }

    private class MyScanListener(internal var fragmentWeakReference: WeakReference<AppPackageListFragment>) : FileScanner.ScanListener {
        private val startTime = System.currentTimeMillis()

        override fun onStarted() {
            val fragment = fragmentWeakReference.get()
            if (fragment != null) {
                val appScanning = fragment.scanningItemInfo!!.data as AppScanning
                appScanning.running = true

                fragment.scanningItemInfo!!.data = appScanning
            }
        }

        override fun onScanDir(dir: File) {

        }

        override fun onFindFile(fileItem: FileScanner.FileItem) {
            val fragment = fragmentWeakReference.get()
            if (fragment != null) {
                if (fileItem is AppInfo) {

                    fragment.adapter!!.dataList?.add(fileItem)

                    val appScanning = fragment.scanningItemInfo!!.data as AppScanning
                    appScanning.count = fragment.adapter!!.dataCount

                    fragment.scanningItemInfo!!.data = appScanning

                    fragment.adapter!!.notifyDataSetChanged()
                }
            }
        }

        override fun onUpdateProgress(totalLength: Int, completedLength: Int) {
            val fragment = fragmentWeakReference.get()
            if (fragment != null) {
                val appScanning = fragment.scanningItemInfo!!.data as AppScanning

                appScanning.totalLength = totalLength
                appScanning.completedLength = completedLength

                fragment.scanningItemInfo!!.data = appScanning
            }
        }

        override fun onCompleted() {
            val fragment = fragmentWeakReference.get()
            if (fragment != null) {
                val appScanning = fragment.scanningItemInfo!!.data as AppScanning
                appScanning.running = false

                appScanning.time = System.currentTimeMillis() - startTime

                fragment.scanningItemInfo!!.data = appScanning
            }
        }

        override fun onCanceled() {

        }
    }

    private class MyDirFilter : FileScanner.DirFilter {

        override fun accept(dir: File): Boolean {
            val fileNameLowerCase = dir.name.toLowerCase()

            var keyword = "."
            if (fileNameLowerCase.startsWith(keyword)) {
                return false
            }

            keyword = "tuniuapp"
            if (keyword.equals(fileNameLowerCase, ignoreCase = true)) {
                return false
            }

            keyword = "cache"
            if (keyword.equals(fileNameLowerCase, ignoreCase = true) || fileNameLowerCase.endsWith(keyword)) {
                return false
            }

            keyword = "log"
            if (keyword.equals(fileNameLowerCase, ignoreCase = true) || fileNameLowerCase.endsWith(keyword)) {
                return false
            }

            keyword = "dump"

            if (keyword.equals(fileNameLowerCase, ignoreCase = true) || fileNameLowerCase.endsWith(keyword)) {
                return false
            }

            return true
        }
    }

    private class MyFileChecker(private val context: Context) : FileScanner.FileChecker {

        override fun accept(pathname: File): FileScanner.FileItem? {
            // 是文件的话根据后缀名判断是APK还是XPK
            val fileNameLowerCase = pathname.name.toLowerCase()
            if (pathname.isFile) {
                val suffix = FileUtils.subSuffix(fileNameLowerCase)
                if (".apk".equals(suffix, ignoreCase = true)) {
                    return parseFromApk(context, pathname)
                } else if (".xpk".equals(suffix, ignoreCase = true)) {
                    return parseFromXpk(pathname)
                } else {
                    return null
                }
            }

            return null
        }

        override fun onFinished() {}

        private fun parseFromApk(context: Context, file: File): AppInfo? {
            val packageInfo = context.packageManager.getPackageArchiveInfo(file.path, PackageManager.GET_ACTIVITIES)
                    ?: return null
            packageInfo.applicationInfo.sourceDir = file.path
            packageInfo.applicationInfo.publicSourceDir = file.path

            val appInfo = AppInfo(false)
            appInfo.name = packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
            appInfo.id = packageInfo.packageName
            appInfo.versionName = packageInfo.versionName
            appInfo.apkFilePath = file.path
            appInfo.appSize = file.length()
            appInfo.formattedAppSize = Formatter.formatFileSize(context, appInfo.appSize)

            return appInfo
        }

        private fun parseFromXpk(file: File): AppInfo? {
            try {
                val appInfo = AppInfo(false)
                val xpkInfo = XpkInfo.getXPKManifestDom(ZipFile(file)) ?: throw Exception()

                appInfo.name = xpkInfo.appName
                appInfo.id = xpkInfo.packageName
                appInfo.versionName = xpkInfo.versionName
                appInfo.apkFilePath = file.path
                appInfo.appSize = file.length()
                appInfo.formattedAppSize = Formatter.formatFileSize(context, appInfo.appSize)
                appInfo.isTempXPK = true

                return appInfo
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }
    }
}
