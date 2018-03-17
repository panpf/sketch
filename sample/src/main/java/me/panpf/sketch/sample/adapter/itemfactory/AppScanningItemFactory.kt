package me.panpf.sketch.sample.adapter.itemfactory

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import me.panpf.adapter.AssemblyRecyclerItem
import me.panpf.adapter.AssemblyRecyclerItemFactory
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.AppScanning
import me.panpf.sketch.sample.bindView

class AppScanningItemFactory : AssemblyRecyclerItemFactory<AppScanningItemFactory.AppListHeaderItem>() {
    override fun isTarget(o: Any): Boolean {
        return o is AppScanning
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): AppListHeaderItem {
        return AppListHeaderItem(R.layout.list_item_app_scanning, viewGroup)
    }

    inner class AppListHeaderItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyRecyclerItem<AppScanning>(itemLayoutId, parent) {
        val textView: TextView by bindView(R.id.text_appScanningItem)
        val progressBar: ProgressBar by bindView(R.id.progress_appScanningItem)

        override fun onConfigViews(context: Context) {

        }

        override fun onSetData(i: Int, scanning: AppScanning) {
            if (scanning.running) {
                val progress = if (scanning.totalLength > 0) (scanning.completedLength.toFloat() / scanning.totalLength * 100).toInt() else 0
                textView.text = String.format("已发现%d个安装包, %d%%", scanning.count, progress)
                progressBar.visibility = View.VISIBLE
            } else {
                textView.text = String.format("共发现%d个安装包，用时%d秒", scanning.count, scanning.time / 1000)
                progressBar.visibility = View.GONE
            }
        }
    }
}
