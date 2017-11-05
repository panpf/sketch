package me.panpf.sketch.sample.adapter.itemfactory

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import me.xiaopan.assemblyadapter.AssemblyRecyclerItem
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory
import me.panpf.sketch.uri.ApkIconUriModel
import me.panpf.sketch.uri.AppIconUriModel
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.AppInfo
import me.panpf.sketch.sample.bindView
import me.panpf.sketch.sample.util.XpkIconUriModel
import me.panpf.sketch.sample.widget.SampleImageView

class AppItemFactory(private val listener: AppItemListener?) : AssemblyRecyclerItemFactory<AppItemFactory.AppItem>() {

    override fun isTarget(o: Any): Boolean {
        return o is AppInfo
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): AppItem {
        return AppItem(R.layout.list_item_app, viewGroup)
    }

    interface AppItemListener {
        fun onClickApp(position: Int, appInfo: AppInfo)
    }

    inner class AppItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyRecyclerItem<AppInfo>(itemLayoutId, parent) {
        val iconImageView: SampleImageView by bindView(R.id.image_installedApp_icon)
        val nameTextView: TextView by bindView(R.id.text_installedApp_name)
        val infoTextView: TextView by bindView(R.id.text_installedApp_info)

        override fun onConfigViews(context: Context) {
            iconImageView.setOptions(ImageOptions.ROUND_RECT)
            iconImageView.page = SampleImageView.Page.APP_LIST

            getItemView().setOnClickListener {
                listener?.onClickApp(position, data)
            }
        }

        override fun onSetData(i: Int, appInfo: AppInfo) {
            if (appInfo.isTempInstalled) {
                iconImageView.displayImage(AppIconUriModel.makeUri(appInfo.id, appInfo.versionCode))
            } else if (appInfo.isTempXPK) {
                iconImageView.displayImage(XpkIconUriModel.makeUri(appInfo.apkFilePath))
            } else {
                iconImageView.displayImage(ApkIconUriModel.makeUri(appInfo.apkFilePath))
            }
            nameTextView.text = appInfo.name
            infoTextView.text = String.format("v%s  |  %s", appInfo.versionName, appInfo.formattedAppSize)
        }
    }
}
