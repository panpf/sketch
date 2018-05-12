package me.panpf.sketch.sample.adapter.itemfactory

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import me.panpf.adapter.AssemblyItem
import me.panpf.adapter.AssemblyItemFactory
import me.panpf.adapter.ktx.bindView
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.AppInfo
import me.panpf.sketch.sample.util.XpkIconUriModel
import me.panpf.sketch.sample.widget.SampleImageView
import me.panpf.sketch.uri.ApkIconUriModel
import me.panpf.sketch.uri.AppIconUriModel

class AppItemFactory(private val listener: AppItemListener?) : AssemblyItemFactory<AppInfo>() {

    override fun match(o: Any?): Boolean {
        return o is AppInfo
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): AppItem {
        return AppItem(R.layout.list_item_app, viewGroup)
    }

    interface AppItemListener {
        fun onClickApp(position: Int, appInfo: AppInfo)
    }

    inner class AppItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyItem<AppInfo>(itemLayoutId, parent) {
        private val iconImageView: SampleImageView by bindView(R.id.image_installedApp_icon)
        private val nameTextView: TextView by bindView(R.id.text_installedApp_name)
        private val infoTextView: TextView by bindView(R.id.text_installedApp_info)

        override fun onConfigViews(context: Context) {
            iconImageView.setOptions(ImageOptions.ROUND_RECT)
            iconImageView.page = SampleImageView.Page.APP_LIST

            itemView.setOnClickListener { data?.let { it1 -> listener?.onClickApp(position, it1) } }
        }

        override fun onSetData(i: Int, appInfo: AppInfo?) {
            when {
                appInfo?.isTempInstalled == true -> iconImageView.displayImage(
                        AppIconUriModel.makeUri(appInfo.id ?: "", appInfo.versionCode))
                appInfo?.isTempXPK == true -> iconImageView.displayImage(
                        XpkIconUriModel.makeUri(appInfo.apkFilePath ?: ""))
                else -> iconImageView.displayImage(
                        ApkIconUriModel.makeUri(appInfo?.apkFilePath ?: ""))
            }
            nameTextView.text = appInfo?.name
            infoTextView.text = String.format("v%s  |  %s", appInfo?.versionName, appInfo?.formattedAppSize)
        }
    }
}
